/*************************************************************************
 * 
 * AVRGAMING LLC
 * __________________
 * 
 *  [2013] AVRGAMING LLC
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of AVRGAMING LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to AVRGAMING LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from AVRGAMING LLC.
 */
package ru.tailsxcraft.civcraft.camp;

import gpl.AttributeUtil;
import ru.tailsxcraft.civcraft.components.ConsumeLevelComponent;
import ru.tailsxcraft.civcraft.components.SifterComponent;
import ru.tailsxcraft.civcraft.components.ConsumeLevelComponent.Result;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigCampLonghouseLevel;
import ru.tailsxcraft.civcraft.config.ConfigCampUpgrade;
import ru.tailsxcraft.civcraft.database.SQL;
import ru.tailsxcraft.civcraft.database.SQLUpdate;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.exception.InvalidNameException;
import ru.tailsxcraft.civcraft.exception.InvalidObjectException;
import ru.tailsxcraft.civcraft.items.components.Tagged;
import ru.tailsxcraft.civcraft.lorestorage.LoreCraftableMaterial;
import ru.tailsxcraft.civcraft.main.CivData;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.BuildableDamageBlock;
import ru.tailsxcraft.civcraft.object.Civilization;
import ru.tailsxcraft.civcraft.object.ControlPoint;
import ru.tailsxcraft.civcraft.object.CultureChunk;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.StructureBlock;
import ru.tailsxcraft.civcraft.object.TownChunk;
import ru.tailsxcraft.civcraft.permission.PlotPermissions;
import ru.tailsxcraft.civcraft.sessiondb.SessionEntry;
import ru.tailsxcraft.civcraft.structure.Buildable;
import ru.tailsxcraft.civcraft.template.Template;
import ru.tailsxcraft.civcraft.template.Template.TemplateType;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.threading.tasks.PostBuildSyncTask;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.util.ChunkCoord;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.civcraft.util.FireworkEffectPlayer;
import ru.tailsxcraft.civcraft.util.ItemManager;
import ru.tailsxcraft.civcraft.util.MultiInventory;
import ru.tailsxcraft.civcraft.util.SimpleBlock;
import ru.tailsxcraft.civcraft.util.SimpleBlock.Type;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Camp extends Buildable {

	private String ownerName;
	private int hitpoints;
	private int firepoints;
	private BlockCoord corner;	
	
	private HashMap<String, Resident> members = new HashMap<String, Resident>();
	public static final double SHIFT_OUT = 2;
	public static final String SUBDIR = "camp";
	private boolean undoable = false;

	/* Locations that exhibit vanilla growth */
	public HashSet<BlockCoord> growthLocations = new HashSet<BlockCoord>();
	private boolean gardenEnabled = false;
	
	/* Camp blocks on this structure. */
	public HashMap<BlockCoord, CampBlock> campBlocks = new HashMap<BlockCoord, CampBlock>();
	
	/* Fire locations for the firepit. */
	public HashMap<Integer, BlockCoord> firepitBlocks = new HashMap<Integer, BlockCoord>();
	public HashSet<BlockCoord> fireFurnaceBlocks = new HashSet<BlockCoord>();
	private Integer coal_per_firepoint;
	private Integer maxFirePoints;
	
	/* Sifter Component */
	public SifterComponent sifter = new SifterComponent();
	public ReentrantLock sifterLock = new ReentrantLock(); 
	private boolean sifterEnabled = false;
	
	/* Longhouse Stuff. */
	public HashSet<BlockCoord> foodDepositPoints = new HashSet<BlockCoord>();
	public ConsumeLevelComponent consumeComponent;
	private boolean longhouseEnabled = false;
	
	/* Doors we protect. */
	public HashSet<BlockCoord> doors = new HashSet<BlockCoord>();
	
	
	/* Control blocks */
	public HashMap<BlockCoord, ControlPoint> controlBlocks = new HashMap<BlockCoord, ControlPoint>();
	
	private Date nextRaidDate;
	private int raidLength;
	
	private HashMap<String, ConfigCampUpgrade> upgrades = new HashMap<String, ConfigCampUpgrade>();
	
	public static void newCamp(Resident resident, Player player, String name) {
		
		class SyncTask implements Runnable {
			
			Resident resident;
			String name;
			Player player;
			
			public SyncTask(Resident resident, String name, Player player) {
				this.resident = resident;
				this.name = name;
				this.player = player;
			}
			
			@Override
			public void run() {
				try {					
					Camp existCamp = CivGlobal.getCamp(name);
					if (existCamp != null) {
						throw new CivException("("+name+") "+CivSettings.localize.localizedString("camp_nameTaken"));
					}
					
					ItemStack stack = player.getInventory().getItemInMainHand();
					LoreCraftableMaterial craftMat = LoreCraftableMaterial.getCraftMaterial(stack);
					if (craftMat == null || !craftMat.hasComponent("FoundCamp")) {
						throw new CivException(CivSettings.localize.localizedString("camp_missingItem"));
					}
					
					Camp camp = new Camp(resident, name, player.getLocation());
					camp.buildCamp(player, player.getLocation());
					camp.setUndoable(true);
					CivGlobal.addCamp(camp);
					camp.save();
				
					CivMessage.sendSuccess(player, CivSettings.localize.localizedString("camp_createSuccess"));
					ItemStack newStack = new ItemStack(Material.AIR);
					player.getInventory().setItemInMainHand(newStack);
					resident.clearInteractiveMode();
				} catch (CivException e) {
					CivMessage.sendError(player, e.getMessage());
				}
			}
		}
		
		TaskMaster.syncTask(new SyncTask(resident, name, player));
	}
	
	public Camp(Resident owner, String name, Location corner) throws CivException {
		this.ownerName = owner.getUUID().toString();
		this.corner = new BlockCoord(corner);
		try {
			this.setName(name);
		} catch (InvalidNameException e1) {
			//e1.printStackTrace();
			throw new CivException("Invalid name, please choose another.");
		}
		nextRaidDate = new Date();
		nextRaidDate.setTime(nextRaidDate.getTime() + 24*60*60*1000);

		try {
			this.firepoints = CivSettings.getInteger(CivSettings.campConfig, "camp.firepoints");
			this.hitpoints = CivSettings.getInteger(CivSettings.campConfig, "camp.hitpoints");
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
		}
		loadSettings();
	}
	
	public Camp(ResultSet rs) throws SQLException, InvalidNameException, InvalidObjectException, CivException {
		this.load(rs);
		loadSettings();
	}
	
	@Override
	public void loadSettings() {
		try {
			coal_per_firepoint = CivSettings.getInteger(CivSettings.campConfig, "camp.coal_per_firepoint");
			maxFirePoints = CivSettings.getInteger(CivSettings.campConfig, "camp.firepoints");
			
			// Setup sifter
			double gold_nugget_chance = CivSettings.getDouble(CivSettings.campConfig, "camp.sifter_gold_nugget_chance");
			double iron_ignot_chance = CivSettings.getDouble(CivSettings.campConfig, "camp.sifter_iron_ingot_chance");
			
			raidLength = CivSettings.getInteger(CivSettings.campConfig, "camp.raid_length");
			
			sifter.addSiftItem(ItemManager.getType(Material.COBBLESTONE), (short)0, gold_nugget_chance, ItemManager.getType(Material.GOLD_NUGGET), (short)0, 1);
			sifter.addSiftItem(ItemManager.getType(Material.COBBLESTONE), (short)0, iron_ignot_chance, ItemManager.getType(Material.IRON_INGOT), (short)0, 1);
			sifter.addSiftItem(ItemManager.getType(Material.COBBLESTONE), (short)0, 1.0, ItemManager.getType(Material.GRAVEL), (short)0, 1);
			
			consumeComponent = new ConsumeLevelComponent();
			consumeComponent.setBuildable(this);
			for (ConfigCampLonghouseLevel lvl : CivSettings.longhouseLevels.values()) {
				consumeComponent.addLevel(lvl.level, lvl.count);
				consumeComponent.setConsumes(lvl.level, lvl.consumes);
			}
			this.consumeComponent.onLoad();

		} catch (InvalidConfiguration e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static final String TABLE_NAME = "CAMPS";
	public static void init() throws SQLException {
		if (!SQL.hasTable(TABLE_NAME)) {
			String table_create = "CREATE TABLE " + SQL.tb_prefix + TABLE_NAME+" (" + 
					"`id` int(11) unsigned NOT NULL auto_increment," +
					"`name` VARCHAR(64) NOT NULL," +
					"`owner_name` mediumtext NOT NULL," +
					"`firepoints` int(11) DEFAULT 0," +
					"`next_raid_date` long,"+
					"`corner` mediumtext,"+
					"`upgrades` mediumtext,"+
					"`template_name` mediumtext,"+
				"PRIMARY KEY (`id`)" + ")";
			
			SQL.makeTable(table_create);
			CivLog.info("Created "+TABLE_NAME+" table");
		} else {
			CivLog.info(TABLE_NAME+" table OK!");
			SQL.makeCol("name", "VARCHAR(64) NOT NULL", TABLE_NAME);
			SQL.makeCol("upgrades", "mediumtext", TABLE_NAME);
			SQL.makeCol("template_name", "mediumtext", TABLE_NAME);
			SQL.makeCol("next_raid_date", "long", TABLE_NAME);
		}
	}
	
	
	@Override
	public void load(ResultSet rs) throws SQLException, InvalidNameException,
			InvalidObjectException, CivException {
		this.setId(rs.getInt("id"));
		this.setName(rs.getString("name"));
			this.ownerName = rs.getString("owner_name");		
		
		this.corner = new BlockCoord(rs.getString("corner"));
		this.nextRaidDate = new Date(rs.getLong("next_raid_date"));
		this.setTemplateName(rs.getString("template_name"));
		
		try {
			this.hitpoints = CivSettings.getInteger(CivSettings.campConfig, "camp.hitpoints");
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
		}
		
		this.firepoints = rs.getInt("firepoints");
		
		if (this.ownerName == null) {
			CivLog.error("COULD NOT FIND OWNER FOR CAMP ID:"+this.getId());
			return;
		}
		
		this.loadUpgradeString(rs.getString("upgrades"));
		this.bindCampBlocks();
	}

	@Override
	public void save() {
		SQLUpdate.add(this);

	}

	@Override
	public void saveNow() throws SQLException {
		HashMap<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("name", this.getName());
			hashmap.put("owner_name", this.getOwner().getUUIDString());		
		
		hashmap.put("firepoints", this.firepoints);
		hashmap.put("corner", this.corner.toString());
		hashmap.put("next_raid_date", this.nextRaidDate.getTime());
		hashmap.put("upgrades", this.getUpgradeSaveString());
		hashmap.put("template_name", this.getSavedTemplatePath());

		SQL.updateNamedObject(this, hashmap, TABLE_NAME);			
	}	
	
	@Override
	public void delete() throws SQLException {
		
		for (Resident resident : this.members.values()) {
			resident.setCamp(null);
			resident.save();
		}
		
		this.unbindCampBlocks();
		SQL.deleteNamedObject(this, TABLE_NAME);
		CivGlobal.removeCamp(this.getName());
	}
	
	public void loadUpgradeString(String upgrades) {
		String[] split = upgrades.split(",");
		for (String id : split) {
			
			if (id == null || id.equalsIgnoreCase("")) {
				continue;
			}
			id = id.trim();
			ConfigCampUpgrade upgrade = CivSettings.campUpgrades.get(id);
			if (upgrade == null) {
				CivLog.warning("Unknown upgrade id "+id+" during load.");
				continue;
			}
			
			this.upgrades.put(id, upgrade);
			upgrade.processAction(this);
		}
	}
	
	public String getUpgradeSaveString() {
		String out = "";
		for (ConfigCampUpgrade upgrade : this.upgrades.values()) {
			out += upgrade.id+",";
		}
		
		return out;
	}
	
	public void destroy() {
		this.fancyCampBlockDestory();
		try {
			this.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void disband() {
		this.undoFromTemplate();
		
		try {
			this.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void undo() {
		this.undoFromTemplate();

		try {
			this.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void undoFromTemplate() {
		Template undo_tpl = new Template();
		try {
			undo_tpl.initUndoTemplate(this.getCorner().toString(), SUBDIR);
			undo_tpl.buildUndoTemplate(undo_tpl, this.getCorner().getBlock());
			undo_tpl.deleteUndoTemplate(this.getCorner().toString(), SUBDIR);
			
		} catch (IOException | CivException e1) {
			e1.printStackTrace();
		}
	}
	
	public void buildCamp(Player player, Location center) throws CivException {
		
		String templateFile;
		try {
			templateFile = CivSettings.getString(CivSettings.campConfig, "camp.template");
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			return;
		}
		Resident resident = CivGlobal.getResident(player);

		/* Load in the template. */
		Template tpl;
		if (resident.desiredTemplate == null) {
			try {
				//tpl.setDirection(center);
				String templatePath = Template.getTemplateFilePath(templateFile, Template.getDirection(center), TemplateType.STRUCTURE, "default");
				this.setTemplateName(templatePath);
				//tpl.load_template(templatePath);
				tpl = Template.getTemplate(templatePath, center);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			} catch (CivException e) {
				e.printStackTrace();
				return;
			}
		} else {
			tpl = resident.desiredTemplate;
			resident.desiredTemplate = null;
			this.setTemplateName(tpl.getFilepath());
		}
				
		corner.setFromLocation(this.repositionCenter(center, tpl.dir(), tpl.size_x, tpl.size_z));
		checkBlockPermissionsAndRestrictions(player, corner.getBlock(), tpl.size_x, tpl.size_y, tpl.size_z);
		try {
			tpl.saveUndoTemplate(this.getCorner().toString(), SUBDIR, getCorner().getLocation());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		buildCampFromTemplate(tpl, corner);

		TaskMaster.syncTask(new PostBuildSyncTask(tpl, this));
		processCommandSigns(tpl, corner);
		try {
			this.saveNow();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new CivException("Internal SQL Error.");
		}
		
		this.addMember(resident);
		resident.save();
		
	}

	public void reprocessCommandSigns() {		
		/* Load in the template. */
		//Template tpl = new Template();
		Template tpl;
		try {
			//tpl.load_template(this.getSavedTemplatePath());
			tpl = Template.getTemplate(this.getSavedTemplatePath(), null);
		} catch (IOException | CivException e) {
			e.printStackTrace();
			return;
		}
		
		processCommandSigns(tpl, corner);
	}
	
	private void processCommandSigns(Template tpl, BlockCoord corner) {
		for (BlockCoord relativeCoord : tpl.commandBlockRelativeLocations) {
			SimpleBlock sb = tpl.blocks[relativeCoord.getX()][relativeCoord.getY()][relativeCoord.getZ()];
			BlockCoord absCoord = new BlockCoord(corner.getBlock().getRelative(relativeCoord.getX(), relativeCoord.getY(), relativeCoord.getZ()));

			switch (sb.command) {
			case "/gardensign":
				if (!this.gardenEnabled) {
					ItemManager.setType(absCoord.getBlock(), ItemManager.getType(Material.OAK_SIGN));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
					
					Sign sign = (Sign)absCoord.getBlock().getState();
					sign.setLine(0, "Garden Disabled");
					sign.setLine(1, "Upgrade using");
					sign.setLine(2, "/camp upgrade");
					sign.setLine(3, "command");
					sign.update();
					this.addCampBlock(absCoord);
				} else {
					ItemManager.setType(absCoord.getBlock(), ItemManager.getType(Material.AIR));
					this.removeCampBlock(absCoord);
				}
				break;
			case "/growth":
				if (this.gardenEnabled) {
					this.growthLocations.add(absCoord);
					CivGlobal.vanillaGrowthLocations.add(absCoord);
					
					Block b = absCoord.getBlock();
					if (ItemManager.getType(b) != CivData.FARMLAND) {
						ItemManager.setType(b, CivData.FARMLAND);
					}
					
					this.addCampBlock(absCoord, true);
					this.addCampBlock(new BlockCoord(absCoord.getBlock().getRelative(0, 1, 0)), true);
				} else {
					this.addCampBlock(absCoord);
					this.addCampBlock(new BlockCoord(absCoord.getBlock().getRelative(0, 1, 0)));
				}
				break;
			case "/firepit":
				this.firepitBlocks.put(Integer.valueOf(sb.keyvalues.get("id")), absCoord);
				this.addCampBlock(absCoord);
				break;
			case "/fire":
				ItemManager.setType(absCoord.getBlock(), ItemManager.getType(Material.FIRE));
				break;
			case "/firefurnace":
				this.fireFurnaceBlocks.add(absCoord);
				BlockData data = CivData.convertSignDataToChestData(sb, Material.FURNACE);
				ItemManager.setType(absCoord.getBlock(), ItemManager.getType(Material.FURNACE));
				ItemManager.setData(absCoord.getBlock(), data);
				this.addCampBlock(absCoord);

				break;
			case "/sifter":
				Integer id = Integer.valueOf(sb.keyvalues.get("id"));
				switch (id) {
				case 0:
					sifter.setSourceCoord(absCoord);
					break;
				case 1:
					sifter.setDestCoord(absCoord);
					break;
				default:
					CivLog.warning("Unknown ID for sifter in camp:"+id);
					break;
				}
				
				if (this.sifterEnabled) {
					ItemManager.setType(absCoord.getBlock(), ItemManager.getType(Material.CHEST));
					BlockData data2 = CivData.convertSignDataToChestData(sb);
					ItemManager.setData(absCoord.getBlock(), data2);
				} else {
					try {
					ItemManager.setType(absCoord.getBlock(), ItemManager.getType(Material.OAK_SIGN));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
					
					Sign sign = (Sign)absCoord.getBlock().getState();
					sign.setLine(0, CivSettings.localize.localizedString("camp_sifterUpgradeSign1"));
					sign.setLine(1, CivSettings.localize.localizedString("upgradeUsing_SignText"));
					sign.setLine(2, "/camp upgrade");
					sign.setLine(3, "");
					sign.update();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				this.addCampBlock(absCoord);
				break;
			case "/foodinput":
				if (this.longhouseEnabled) {
					this.foodDepositPoints.add(absCoord);
					ItemManager.setType(absCoord.getBlock(), ItemManager.getType(Material.CHEST));
					BlockData data3 = CivData.convertSignDataToChestData(sb);
					ItemManager.setData(absCoord.getBlock(), data3);
				} else {
					ItemManager.setType(absCoord.getBlock(), ItemManager.getType(Material.OAK_SIGN));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
					
					Sign sign = (Sign)absCoord.getBlock().getState();
					sign.setLine(0, CivSettings.localize.localizedString("camp_longhouseSign1"));
					sign.setLine(1, CivSettings.localize.localizedString("camp_longhouseSign2"));
					sign.setLine(2, CivSettings.localize.localizedString("upgradeUsing_SignText"));
					sign.setLine(3, "/camp upgrade");
					sign.update();
				}
				this.addCampBlock(absCoord);
				break;
			case "/door":
				this.doors.add(absCoord);
				Block doorBlock = absCoord.getBlock();
				Block doorBlock2 = absCoord.getBlock().getRelative(0, 1, 0);
				

				BlockData doorDirection = CivData.convertSignDataToDoorDirectionData(sb);
				BlockData doorDirectionUpper = CivData.convertSignDataToDoorDirectionDataUpper(sb);
				
				
				ItemManager.setTypeIdAndData(doorBlock, Material.OAK_DOOR, doorDirection, false);
				ItemManager.setTypeIdAndData(doorBlock2, Material.OAK_DOOR, doorDirectionUpper, false);

				this.addCampBlock(new BlockCoord(doorBlock));
				this.addCampBlock(new BlockCoord(doorBlock2));
				break;
			case "/control":
				this.createControlPoint(absCoord);
				break;
			case "/literal":
				/* Unrecognized command... treat as a literal sign. */
				ItemManager.setType(absCoord.getBlock(), Material.OAK_SIGN);
				ItemManager.setData(absCoord.getBlock(), sb.getData());
				
				Sign sign = (Sign)absCoord.getBlock().getState();
				sign.setLine(0, sb.message[0]);
				sign.setLine(1, sb.message[1]);
				sign.setLine(2, sb.message[2]);
				sign.setLine(3, sb.message[3]);
				sign.update();
				break;
			}
		}
		
		updateFirepit();
	}
	
	private void removeCampBlock(BlockCoord absCoord) {
		this.campBlocks.remove(absCoord);
		CivGlobal.removeCampBlock(absCoord);
	}

	private void updateFirepit() {
		try {
			int maxFirePoints = CivSettings.getInteger(CivSettings.campConfig, "camp.firepoints");
			int totalFireBlocks = this.firepitBlocks.size();

			double percentLeft = (double)this.firepoints / (double) maxFirePoints;
			
			//  x/totalFireBlocks = percentLeft / 100
			int litFires = (int)(percentLeft*totalFireBlocks);
			
			for (int i = 0; i < totalFireBlocks; i++) {
				BlockCoord next = this.firepitBlocks.get(i);
				if (next == null) {
					CivLog.warning("Couldn't find firepit id:"+i);
					continue;
				}
				if (i < litFires) {
					Campfire campfire = (Campfire)Bukkit.createBlockData(Material.CAMPFIRE);
				    campfire.setLit(true);
				    ItemManager.setTypeIdAndData(next.getBlock(), Material.CAMPFIRE, campfire, false);
				} else {
					Campfire campfire = (Campfire)Bukkit.createBlockData(Material.CAMPFIRE);
				    campfire.setLit(false);
					ItemManager.setTypeIdAndData(next.getBlock(), Material.CAMPFIRE, campfire, false);
				}
			}
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
		}
	}
	
	public void processFirepoints() {
		
		MultiInventory mInv = new MultiInventory();
		for (BlockCoord bcoord : this.fireFurnaceBlocks) {
			Furnace furnace = (Furnace)bcoord.getBlock().getState();
			mInv.addInventory(furnace.getInventory());
		}

		if (mInv.contains(null, CivData.COAL, (short)0, coal_per_firepoint)) {
			try {
				mInv.removeItem(CivData.COAL, coal_per_firepoint, true);
			} catch (CivException e) {
				e.printStackTrace();
			}
			
			this.firepoints++;
			if (firepoints > maxFirePoints) {
				firepoints = maxFirePoints;
			}
		} else if (mInv.contains(null, Material.DRIED_KELP_BLOCK, (short)0, 1)) {
			try {
				mInv.removeItem(Material.DRIED_KELP_BLOCK, 1, true);
			} catch (CivException e) {
				e.printStackTrace();
			}
			
			this.firepoints++;
			if (firepoints > maxFirePoints) {
				firepoints = maxFirePoints;
			}
		} else {
			this.firepoints--;
			CivMessage.sendCamp(this, CivColor.Yellow+CivSettings.localize.localizedString("var_camp_campfireDown",this.firepoints));
			
			double percentLeft = (double)this.firepoints / (double)this.maxFirePoints;
			if (percentLeft < 0.3) {
				CivMessage.sendCamp(this, CivColor.Yellow+ChatColor.BOLD+CivSettings.localize.localizedString("camp_campfire30percent"));
			}
			
			if (this.firepoints < 0) {
					this.destroy();
			}
		}
		
		this.save();
		this.updateFirepit();
	}
	
	public void processLonghouse() {
		MultiInventory mInv = new MultiInventory();
		
		for (BlockCoord bcoord : this.foodDepositPoints) {
			Block b = bcoord.getBlock();
			if (b.getState() instanceof Chest) {
				try {
				mInv.addInventory(((Chest)b.getState()).getInventory());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		if (mInv.getInventoryCount() == 0) {
			CivMessage.sendCamp(this, CivColor.Rose+CivSettings.localize.localizedString("camp_longhouseNoChest"));
			return;
		}
		
		long badLeaderTime;
		int badLeaderHours;
		try {
			badLeaderHours = CivSettings.getInteger(CivSettings.civConfig, "global.bad_leader_timer");
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			return;
		}
		
		badLeaderTime = badLeaderHours * 60*60*1000; /*convert hours to milliseconds. */

		ArrayList<SessionEntry> entries = CivGlobal.getSessionDB().lookup(this.getOwner().getBadLeaderKey());
		if (entries.size() > 0) {
			/* Check if cooldown is expired. */
			Date now = new Date();
			if (now.getTime() < (entries.get(0).time + badLeaderTime)) {
				/* Entry is expired, allow cooldown and cleanup. */
				CivLog.info("Now time is: "+now.getTime());
				CivLog.info("Entries time is: "+entries.get(0).time);
				CivLog.info("Badleader time is: "+badLeaderTime);
				if ( now.getTime() > (entries.get(0).time + (badLeaderTime / 2))) {
					CivMessage.sendCamp(this, CivColor.Rose+"Наш Лагерь не смог создать жетон, т.к на нашего Вождя действует эффект \"Плохой Вождь\", который продлится 1 час");
				} else {
					CivMessage.sendCamp(this, CivColor.Rose+"Наш Лагерь не смог создать жетон, т.к на нашего Вождя действует эффект \"Плохой Вождь\", который продлится "+CivColor.Green+badLeaderHours+" часа");
				}
				return;
			}
			
			this.getOwner().cleanupBadLeader();
		}	
		
		this.consumeComponent.setSource(mInv);
		Result result = this.consumeComponent.processConsumption(true);
		this.consumeComponent.onSave();
		
		switch (result) {
		case STARVE:
			CivMessage.sendCamp(this, CivColor.LightGreen+CivSettings.localize.localizedString("var_camp_yourLonghouseDown",(CivColor.Rose+CivSettings.localize.localizedString("var_camp_longhouseStarved",consumeComponent.getCountString())+CivColor.LightGreen),CivSettings.CURRENCY_NAME));
			return;
		case LEVELDOWN:
			CivMessage.sendCamp(this, CivColor.LightGreen+CivSettings.localize.localizedString("var_camp_yourLonghouseDown",(CivColor.Rose+CivSettings.localize.localizedString("camp_longhouseStavedAndLeveledDown")+CivColor.LightGreen),CivSettings.CURRENCY_NAME));
			return;
		case STAGNATE:
			CivMessage.sendCamp(this, CivColor.LightGreen+CivSettings.localize.localizedString("var_camp_yourLonghouseDown",(CivColor.Yellow+CivSettings.localize.localizedString("camp_longhouseStagnated")+CivColor.LightGreen),CivSettings.CURRENCY_NAME));
			return;
		case UNKNOWN:
			CivMessage.sendCamp(this, CivColor.LightGreen+CivSettings.localize.localizedString("var_camp_yourLonghouseDown",(CivColor.Purple+CivSettings.localize.localizedString("camp_longhouseSomethingUnknown")+CivColor.LightGreen),CivSettings.CURRENCY_NAME));
			return;
		default:
			break;
		}
		
		ConfigCampLonghouseLevel lvl = null;
		if (result == Result.LEVELUP) {
			lvl = CivSettings.longhouseLevels.get(consumeComponent.getLevel()-1);
		} else {
			lvl = CivSettings.longhouseLevels.get(consumeComponent.getLevel());
		}
		
		double total_coins = lvl.coins;
		this.getOwner().getTreasury().deposit(total_coins);
		
		LoreCraftableMaterial craftMat =  LoreCraftableMaterial.getCraftMaterialFromId("mat_token_of_leadership");
		if (craftMat != null) {
			ItemStack token = LoreCraftableMaterial.spawn(craftMat);
			
			Tagged tag = (Tagged) craftMat.getComponent("Tagged");
			Resident res = CivGlobal.getResident(this.getOwnerName());
			
			token = tag.addTag(token, res.getUUIDString());
	
			AttributeUtil attrs = new AttributeUtil(token);
			attrs.addLore(CivColor.LightGray+res.getName());
			token = attrs.getStack();
			
			mInv.addItems(token, true);
		}
		
		String stateMessage = "";
		switch (result) {
		case GROW:
			stateMessage = CivColor.Green+CivSettings.localize.localizedString("var_camp_longhouseGrew",consumeComponent.getCountString()+CivColor.LightGreen);
			break;
		case LEVELUP:
			stateMessage = CivColor.Green+CivSettings.localize.localizedString("camp_longhouselvlUp")+CivColor.LightGreen;
			break;
		case MAXED:
			stateMessage = CivColor.Green+CivSettings.localize.localizedString("var_camp_longhouseIsMaxed",consumeComponent.getCountString()+CivColor.LightGreen);
			break;
		default:
			break;
		}
		
		CivMessage.sendCamp(this, CivColor.LightGreen+CivSettings.localize.localizedString("var_camp_yourLonghouse",stateMessage,total_coins,CivSettings.CURRENCY_NAME));
	}
	
	private void buildCampFromTemplate(Template tpl, BlockCoord corner) {
		
		Block cornerBlock = corner.getBlock();
		for (int x = 0; x < tpl.size_x; x++) {
			for (int y = 0; y < tpl.size_y; y++) {
				for (int z = 0; z < tpl.size_z; z++) {
					Block nextBlock = cornerBlock.getRelative(x, y, z);
					
					if (tpl.blocks[x][y][z].specialType == Type.COMMAND) {
						continue;
					}
					
					if (tpl.blocks[x][y][z].specialType == Type.LITERAL) {
						// Adding a command block for literal sign placement
						tpl.blocks[x][y][z].command = "/literal";
						tpl.commandBlockRelativeLocations.add(new BlockCoord(cornerBlock.getWorld().getName(), x, y,z));
						continue;
					}

					try {
						if (ItemManager.getType(nextBlock) != tpl.blocks[x][y][z].getType()) {
								ItemManager.setType(nextBlock, tpl.blocks[x][y][z].getType());
								ItemManager.setData(nextBlock, tpl.blocks[x][y][z].getData());
								
						}
						
						if (ItemManager.getType(nextBlock) != CivData.AIR) {
							this.addCampBlock(new BlockCoord(nextBlock.getLocation()));
						}
					} catch (Exception e) {
						CivLog.error(e.getMessage());
					}
				}
			}
		}		
	}
	
	private void bindCampBlocks() {
		// Called mostly on a reload, determines which blocks should be protected based on the corner
		// location and the template's size. We need to verify that each block is a part of the template.
	
		
		/* Load in the template. */
		Template tpl;
		try {
			//tpl.load_template(this.getSavedTemplatePath());
			tpl = Template.getTemplate(this.getSavedTemplatePath(), null);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (CivException e) {
			e.printStackTrace();
			return;
		}
		
		for (int y = 0; y < tpl.size_y; y++) {
			for (int z = 0; z < tpl.size_z; z++) {
				for (int x = 0; x < tpl.size_x; x++) {
					int relx = getCorner().getX() + x;
					int rely = getCorner().getY() + y;
					int relz = getCorner().getZ() + z;
					
					BlockCoord coord = new BlockCoord(this.getCorner().getWorldname(), (relx), (rely), (relz));
					
					if (tpl.blocks[x][y][z].getType() == CivData.AIR) {
						continue;
					}
					
					if (tpl.blocks[x][y][z].specialType == SimpleBlock.Type.COMMAND) {
						continue;
					}
						
					this.addCampBlock(coord);
				}
			}
		}
		
		this.processCommandSigns(tpl, corner);
		
	}
	
	protected Location repositionCenter(Location center, String dir, double x_size, double z_size) throws CivException {
		Location loc = new Location(center.getWorld(), 
				center.getX(), center.getY(), center.getZ(), 
				center.getYaw(), center.getPitch());
		
		// Reposition tile improvements
		if (dir.equalsIgnoreCase("east")) {
			loc.setZ(loc.getZ() - (z_size / 2));
			loc.setX(loc.getX() + SHIFT_OUT);
		}
		else if (dir.equalsIgnoreCase("west")) {
			loc.setZ(loc.getZ() - (z_size / 2));
			loc.setX(loc.getX() - (SHIFT_OUT+x_size));

		}
		else if (dir.equalsIgnoreCase("north")) {
			loc.setX(loc.getX() - (x_size / 2));
			loc.setZ(loc.getZ() - (SHIFT_OUT+z_size));
		}
		else if (dir.equalsIgnoreCase("south")) {
			loc.setX(loc.getX() - (x_size / 2));
			loc.setZ(loc.getZ() + SHIFT_OUT);

		}
		
		return loc;
	}
	
	protected void checkBlockPermissionsAndRestrictions(Player player, Block centerBlock, int regionX, int regionY, int regionZ) throws CivException {
		
		ChunkCoord ccoord = new ChunkCoord(centerBlock.getLocation());
		CultureChunk cc = CivGlobal.getCultureChunk(ccoord);
		if (cc != null) {
			throw new CivException(CivSettings.localize.localizedString("camp_checkInCivError"));
		}
		
		if (player.getLocation().getY() >= 200) {
			throw new CivException(CivSettings.localize.localizedString("camp_checkTooHigh"));
		}
		
		if ((regionY + centerBlock.getLocation().getBlockY()) >= 255) {
			throw new CivException(CivSettings.localize.localizedString("camp_checkWayTooHigh"));
		}
		
		if (player.getLocation().getY() < CivGlobal.minBuildHeight) {
			throw new CivException(CivSettings.localize.localizedString("cannotBuild_toofarUnderground"));
		}
		
		if (!player.isOp()) {
			Buildable.validateDistanceFromSpawn(centerBlock.getLocation());
		}
		
		int yTotal = 0;
		int yCount = 0;
		
		for (int x = 0; x < regionX; x++) {
			for (int y = 0; y < regionY; y++) {
				for (int z = 0; z < regionZ; z++) {
					Block b = centerBlock.getRelative(x, y, z);
					
					if (ItemManager.getType(b) == CivData.CHEST) {
						throw new CivException(CivSettings.localize.localizedString("cannotBuild_chestInWay"));
					}
		
					BlockCoord coord = new BlockCoord(b);
					ChunkCoord chunkCoord = new ChunkCoord(coord.getLocation());
					
					TownChunk tc = CivGlobal.getTownChunk(chunkCoord);
					if (tc != null && !tc.perms.hasPermission(PlotPermissions.Type.DESTROY, CivGlobal.getResident(player))) {
						// Make sure we have permission to destroy any block in this area.
						throw new CivException(CivSettings.localize.localizedString("cannotBuild_needPermissions")+" "+b.getX()+","+b.getY()+","+b.getZ());
					}
					
					if (CivGlobal.getProtectedBlock(coord) != null) {
						throw new CivException(CivSettings.localize.localizedString("cannotBuild_protectedInWay"));
					}
					
					if (CivGlobal.getStructureBlock(coord) != null) {
						throw new CivException(CivSettings.localize.localizedString("cannotBuild_structureInWay"));
					}
				
					if (CivGlobal.getFarmChunk(chunkCoord) != null) {
						throw new CivException(CivSettings.localize.localizedString("cannotBuild_farmInWay"));
					}
		
					if (CivGlobal.getWallChunk(chunkCoord) != null) {
						throw new CivException(CivSettings.localize.localizedString("cannotBuild_wallInWay"));
					}
					
					if (CivGlobal.getCampBlock(coord) != null) {
						throw new CivException(CivSettings.localize.localizedString("cannotBuild_campinWay"));
					}
					
					yTotal += b.getWorld().getHighestBlockYAt(centerBlock.getX()+x, centerBlock.getZ()+z);
					yCount++;
				}
			}
		}

		
		double highestAverageBlock = (double)yTotal / (double)yCount;
		
		if (((centerBlock.getY() > (highestAverageBlock+10)) || 
				(centerBlock.getY() < (highestAverageBlock-10)))) {
			throw new CivException(CivSettings.localize.localizedString("cannotBuild_toofarUnderground"));
		}
		
	}
	
	public void unbindCampBlocks() {
		for (BlockCoord bcoord : this.campBlocks.keySet()) {
			CivGlobal.removeCampBlock(bcoord);
			ChunkCoord coord = new ChunkCoord(bcoord);
			CivGlobal.removeCampChunk(coord);
		}
	}
	
	private void addCampBlock(BlockCoord coord) {
		addCampBlock(coord, false);
	}
	
	private void addCampBlock(BlockCoord coord, boolean friendlyBreakable) {
		CampBlock cb = new CampBlock(coord, this, friendlyBreakable);
		
		this.campBlocks.put(coord, cb);
		CivGlobal.addCampBlock(cb);
	}

	public void addMember(Resident resident) {
		this.members.put(resident.getName(), resident);
		resident.setCamp(this);
		resident.save();
	}
	
	public void removeMember(Resident resident) {
		this.members.remove(resident.getName());
		resident.setCamp(null);
		resident.save();
	}
	
	public Resident getMember(String name) {
		return this.members.get(name);
	}
	
	public boolean hasMember(String name) {
		return this.members.containsKey(name);
	}

	public Resident getOwner() {
		return CivGlobal.getResidentViaUUID(UUID.fromString(ownerName));
	}


	public void setOwner(Resident owner) {
		this.ownerName = owner.getUUID().toString();
	}


	public int getHitpoints() {
		return hitpoints;
	}


	public void setHitpoints(int hitpoints) {
		this.hitpoints = hitpoints;
	}


	public int getFirepoints() {
		return firepoints;
	}


	public void setFirepoints(int firepoints) {
		this.firepoints = firepoints;
	}


	public BlockCoord getCorner() {
		return corner;
	}


	public void setCorner(BlockCoord corner) {
		this.corner = corner;
	}

	public void fancyCampBlockDestory() {
		for (BlockCoord coord : this.campBlocks.keySet()) {
			
			if (CivGlobal.getStructureChest(coord) != null) {
				continue;
			}
			
			if (CivGlobal.getStructureSign(coord) != null) {
				continue;
			}
			
			if (ItemManager.getType(coord.getBlock()) == CivData.CHEST) {
				continue;
			}
			
			if (ItemManager.getType(coord.getBlock()) == CivData.SIGN) {
				continue;
			}
			
			if (ItemManager.getType(coord.getBlock()) == CivData.WALL_SIGN) {
				continue;
			}
			
			if (CivSettings.alwaysCrumble.contains(ItemManager.getType(coord.getBlock()))) {
				ItemManager.setType(coord.getBlock(), CivData.GRAVEL);
				continue;
			}
						
			Random rand = new Random();
			
			// Each block has a 10% chance to turn into gravel
			if (rand.nextInt(100) <= 10) {
				ItemManager.setType(coord.getBlock(), CivData.GRAVEL);
				continue;
			}
			
			// Each block has a 50% chance of starting a fire
			if (rand.nextInt(100) <= 50) {
				ItemManager.setType(coord.getBlock(), CivData.FIRE);
				continue;
			}
			
			// Each block has a 1% chance of launching an explosion effect
			if (rand.nextInt(100) <= 1) {
				FireworkEffect effect = FireworkEffect.builder().with(org.bukkit.FireworkEffect.Type.BURST).withColor(Color.ORANGE).withColor(Color.RED).withTrail().withFlicker().build();
				FireworkEffectPlayer fePlayer = new FireworkEffectPlayer();
				for (int i = 0; i < 3; i++) {
					try {
						fePlayer.playFirework(coord.getBlock().getWorld(), coord.getLocation(), effect);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}			
			}
		}
	}

	public void createControlPoint(BlockCoord absCoord) {
		
		Location centerLoc = absCoord.getLocation();
		
		/* Build the bedrock tower. */
		Block b = centerLoc.getBlock();
		ItemManager.setType(b, CivData.FENCE); 
		ItemManager.setData(b, Material.OAK_FENCE.createBlockData());
		
		StructureBlock sb = new StructureBlock(new BlockCoord(b), this);
		this.addCampBlock(sb.getCoord());
		
		/* Build the control block. */
		b = centerLoc.getBlock().getRelative(0, 1, 0);
		ItemManager.setType(b, CivData.OBSIDIAN);
		sb = new StructureBlock(new BlockCoord(b), this);
		this.addCampBlock(sb.getCoord());
	
		int campControlHitpoints;
		try {
			campControlHitpoints = CivSettings.getInteger(CivSettings.warConfig, "war.control_block_hitpoints_camp");
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			campControlHitpoints = 100;
		}
		
		BlockCoord coord = new BlockCoord(b);		
		this.controlBlocks.put(coord, new ControlPoint(coord, this, campControlHitpoints));
	}
	
	
	public boolean isUndoable() {
		return undoable;
	}

	public void setUndoable(boolean undoable) {
		this.undoable = undoable;
	}

	@Override
	public String getDisplayName() {
		return CivSettings.localize.localizedString("Camp");
	}
	
	@Override
	public void sessionAdd(String key, String value) {
		CivGlobal.getSessionDB().add(key, value, 0, 0, 0);
	}
	
	//XXX TODO make sure these all work...
	@Override
	public void processUndo() throws CivException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateBuildProgess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void build(Player player, Location centerLoc, Template tpl) throws Exception {		
	}

	@Override
	protected void runOnBuild(Location centerLoc, Template tpl) throws CivException {
		return;
	}

	@Override
	public String getDynmapDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMarkerIconName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnload() {
		// TODO Auto-generated method stub
		
	}

	public Collection<Resident> getMembers() {
		return this.members.values();
	}

	public String getOwnerName() {
		Resident res = CivGlobal.getResidentViaUUID(UUID.fromString(ownerName));
		return res.getName();
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public int getLonghouseLevel() {
		return this.consumeComponent.getLevel();
	}
	
	public String getLonghouseCountString() {
		return this.consumeComponent.getCountString();
	}
	
	public String getMembersString() {
		String out = "";
		for (Resident resident : members.values()) {
			out += resident.getName()+" ";
		}
		return out;
	}

	public void onControlBlockHit(ControlPoint cp, World world, Player player) {
		world.playSound(cp.getCoord().getLocation(), Sound.BLOCK_ANVIL_USE, 0.2f, 1);
		world.playEffect(cp.getCoord().getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
		
		CivMessage.send(player, CivColor.LightGray+CivSettings.localize.localizedString("camp_hitControlBlock")+"("+cp.getHitpoints()+" / "+cp.getMaxHitpoints()+")");
		CivMessage.sendCamp(this, CivColor.Yellow+CivSettings.localize.localizedString("camp_controlBlockUnderAttack"));
	}
	
	
	public void onControlBlockDestroy(ControlPoint cp, World world, Player player) {		
		ItemManager.setType(cp.getCoord().getLocation().getBlock(), CivData.AIR);
		world.playSound(cp.getCoord().getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, -1.0f);
		world.playSound(cp.getCoord().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
		
		FireworkEffect effect = FireworkEffect.builder().with(org.bukkit.FireworkEffect.Type.BURST).withColor(Color.YELLOW).withColor(Color.RED).withTrail().withFlicker().build();
		FireworkEffectPlayer fePlayer = new FireworkEffectPlayer();
		for (int i = 0; i < 3; i++) {
			try {
				fePlayer.playFirework(world, cp.getCoord().getLocation(), effect);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		boolean allDestroyed = true;
		for (ControlPoint c : this.controlBlocks.values()) {
			if (c.isDestroyed() == false) {
				allDestroyed = false;
				break;
			}
		}

		if (allDestroyed) {
			this.getOwner().setBadLeader();
			CivMessage.sendCamp(this, CivColor.Rose+CivSettings.localize.localizedString("camp_destroyed"));
			this.destroy();
		} else {
			CivMessage.sendCamp(this, CivColor.Rose+CivSettings.localize.localizedString("camp_controlBlockDestroyed"));
		}
		
	}
	
	@Override
	public void onDamage(int amount, World world, Player player, BlockCoord hit, BuildableDamageBlock hit2) {
	
		ControlPoint cp = this.controlBlocks.get(hit);
		if (cp != null) {
			Date now = new Date();
			Resident resident = CivGlobal.getResident(player);
			
			if (resident.isProtected()) {
				CivMessage.sendError(player, CivSettings.localize.localizedString("camp_protected"));
				return;
			}
			
			if (now.after(getNextRaidDate())) {
				if (!cp.isDestroyed()) {
					cp.damage(amount);
					if (cp.isDestroyed()) {
						onControlBlockDestroy(cp, world, player);
					} else {
						onControlBlockHit(cp, world, player);
					}
				} else {
					CivMessage.send(player, CivColor.Rose+CivSettings.localize.localizedString("camp_controlBlockAlreadyDestroyed"));
				}
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("M/dd h:mm:ss a z");
				CivMessage.send(player, CivColor.Rose+CivSettings.localize.localizedString("camp_protectedUntil")+" "+sdf.format(getNextRaidDate()));
			}
			
		}
	}

	public void setNextRaidDate(Date next) {
		this.nextRaidDate = next;
		this.save();
	}

	public Date getNextRaidDate() {
		Date raidEnd = new Date(this.nextRaidDate.getTime());
		raidEnd.setTime(this.nextRaidDate.getTime() + 60*60*1000*this.raidLength);
		
		Date now = new Date();
		if (now.getTime() > raidEnd.getTime()) {
			this.nextRaidDate.setTime(nextRaidDate.getTime() + 60*60*1000*24);
		}
		
		return this.nextRaidDate;
	}

	public boolean isSifterEnabled() {
		return sifterEnabled;
	}

	public void setSifterEnabled(boolean sifterEnabled) {
		this.sifterEnabled = sifterEnabled;
	}

	public Collection<ConfigCampUpgrade> getUpgrades() {
		return this.upgrades.values();
	}

	public boolean hasUpgrade(String require_upgrade) {
		return this.upgrades.containsKey(require_upgrade);
	}

	public void purchaseUpgrade(ConfigCampUpgrade upgrade) throws CivException {
		Resident owner = this.getOwner();
		
		if (!owner.getTreasury().hasEnough(upgrade.cost)) {
			throw new CivException(CivSettings.localize.localizedString("var_camp_ownerMissingCost",upgrade.cost,CivSettings.CURRENCY_NAME));
		}
		
		this.upgrades.put(upgrade.id, upgrade);
		upgrade.processAction(this);
		
		
		this.reprocessCommandSigns();
		owner.getTreasury().withdraw(upgrade.cost);
		this.save();
		return;
	}

	public boolean isLonghouseEnabled() {
		return longhouseEnabled;
	}

	public void setLonghouseEnabled(boolean longhouseEnabled) {
		this.longhouseEnabled = longhouseEnabled;
	}

	public boolean isGardenEnabled() {
		return gardenEnabled;
	}

	public void setGardenEnabled(boolean gardenEnabled) {
		this.gardenEnabled = gardenEnabled;
	}
}
