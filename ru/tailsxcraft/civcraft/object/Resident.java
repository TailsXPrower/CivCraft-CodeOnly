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
package ru.tailsxcraft.civcraft.object;

import gpl.InventorySerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import ru.tailsxcraft.civcraft.camp.Camp;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigBuildableInfo;
import ru.tailsxcraft.civcraft.config.ConfigPerk;
import ru.tailsxcraft.civcraft.database.SQL;
import ru.tailsxcraft.civcraft.database.SQLUpdate;
import ru.tailsxcraft.civcraft.event.EventTimer;
import ru.tailsxcraft.civcraft.exception.AlreadyRegisteredException;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.exception.InvalidNameException;
import ru.tailsxcraft.civcraft.interactive.InteractiveResponse;
import ru.tailsxcraft.civcraft.items.units.Unit;
import ru.tailsxcraft.civcraft.lorestorage.LoreCraftableMaterial;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.permission.PermissionGroup;
import ru.tailsxcraft.civcraft.sessiondb.SessionEntry;
import ru.tailsxcraft.civcraft.structure.Buildable;
import ru.tailsxcraft.civcraft.structure.TownHall;
import ru.tailsxcraft.civcraft.template.Template;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.threading.tasks.BuildPreviewAsyncTask;
import ru.tailsxcraft.civcraft.tutorial.CivTutorial;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.util.CallbackInterface;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.civcraft.util.ItemManager;
import ru.tailsxcraft.civcraft.util.PlayerBlockChangeUtil;
import ru.tailsxcraft.civcraft.util.SimpleBlock;
import ru.tailsxcraft.global.perks.NotVerifiedException;
import ru.tailsxcraft.global.perks.Perk;
import ru.tailsxcraft.global.perks.components.CustomPersonalTemplate;
import ru.tailsxcraft.global.perks.components.CustomTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
//import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
//import org.bukkit.potion.PotionEffect;
//import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Resident extends SQLObject {

	private Town town = null;
	private Camp camp = null;
	private boolean townChat = false;
	private boolean civChat = false;
	private boolean adminChat = false;
	private boolean combatInfo = false;
	private boolean titleAPI = true;
	private String showBuild = "style";
	
	private boolean usesAntiCheat = false;
	
	public static HashSet<String> allchatters = new HashSet<String>();
	
	/* Town or civ to chat in besides your own. */
	private Town townChatOverride = null;
	private Civilization civChatOverride = null;
	private boolean permOverride = false;
	private boolean sbperm = false;
	private boolean controlBlockInstantBreak = false;
	private int townID = 0;
	private int campID = 0;
	private boolean dontSaveTown = false;
	private String timezone;
	
	private boolean banned = false;
	
	private long registered;
	private long lastOnline;
	private int daysTilEvict;
	private boolean givenKit;
	private ConcurrentHashMap<String, Integer> friends = new ConcurrentHashMap<String, Integer>();
	private EconObject treasury;
	private boolean muted;
	private Date muteExpires = null;
	
	private boolean interactiveMode = false;
	private InteractiveResponse interactiveResponse = null;
	private BuildPreviewAsyncTask previewTask = null;
	
	private double spyExposure = 0.0;
	public static int MAX_SPY_EXPOSURE = 1000;
	private boolean performingMission = false;
	
	private Town selectedTown = null;
	
	private Scoreboard scoreboard = null;
	public String desiredCivName;
	public String desiredCapitolName;
	public String desiredTownName;
	public Location desiredTownLocation = null;
	public Template desiredTemplate = null;
	
	public boolean allchat = false; 
	
	/* XXX 
	 * This buildable is used as place to store which buildable we're working on when interacting 
	 * with GUI items. We want to be able to pass the buildable object to the GUI's action function,
	 * but there isn't a good way to do this ATM. If we had a way to send arbitary objects it would
	 * be better. Could we store it here on the resident object?
	 */
	public Buildable pendingBuildable;
	public ConfigBuildableInfo pendingBuildableInfo;
	public CallbackInterface pendingCallback;
	
	private boolean showScout = true;
	private boolean showTown = true;
	private boolean showCiv = true;
	private boolean showMap = false;
	private boolean showInfo = false;
	private String itemMode = "all";
	private String savedInventory = null;
	private boolean isProtected = false;
	
	public ConcurrentHashMap<BlockCoord, SimpleBlock> previewUndo = null;
	public LinkedHashMap<String, Perk> perks = new LinkedHashMap<String, Perk>();
	private Date lastKilledTime = null;
	private String lastIP = "";
	private UUID uid;
	private double walkingModifier = CivSettings.normal_speed;
	public String debugTown;
	
	public Resident(UUID uid, String name) throws InvalidNameException {
		this.setName(name);
		this.uid = uid;		
		this.treasury = CivGlobal.createEconObject(this);
		setTimezoneToServerDefault();
		loadSettings();
	}
	
	public Resident(ResultSet rs) throws SQLException, InvalidNameException {
		this.load(rs);
		loadSettings();
	}
	
	public void loadSettings() {
		this.spyExposure = 0.0;
	}
	
	public static final String TABLE_NAME = "RESIDENTS";
	public static void init() throws SQLException {
		if (!SQL.hasTable(TABLE_NAME)) {
			String table_create = "CREATE TABLE " + SQL.tb_prefix + TABLE_NAME+" (" + 
					"`id` int(11) unsigned NOT NULL auto_increment," +
					"`name` VARCHAR(64) NOT NULL," +
					"`uuid` VARCHAR(256) NOT NULL DEFAULT 'UNKNOWN',"+
					"`currentName` VARCHAR(64) DEFAULT NULL,"+
					"`town_id` int(11)," + 
					"`lastOnline` BIGINT NOT NULL," +
					"`registered` BIGINT NOT NULL," + 
					"`friends` mediumtext," + 
					"`debt` double DEFAULT 0," +
					"`coins` double DEFAULT 0," +
					"`daysTilEvict` mediumint DEFAULT NULL," +
					"`givenKit` bool NOT NULL DEFAULT '0'," +
					"`camp_id` int(11)," +
					"`timezone` mediumtext,"+
					"`banned` bool NOT NULL DEFAULT '0'," +
					"`bannedMessage` mediumtext DEFAULT NULL,"+
					"`savedInventory` mediumtext DEFAULT NULL,"+
					"`isProtected` bool NOT NULL DEFAULT '0',"+
					"`flags` mediumtext DEFAULT NULL,"+
					"`last_ip` mediumtext DEFAULT NULL,"+
					"`debug_town` mediumtext DEFAULT NULL,"+
					"`debug_civ` mediumtext DEFAULT NuLL,"+
					"UNIQUE KEY (`name`), " +
					"PRIMARY KEY (`id`)" + ")";
			
			SQL.makeTable(table_create);
			CivLog.info("Created "+TABLE_NAME+" table");
		} else {
			CivLog.info(TABLE_NAME+" table OK!");
			
			if (!SQL.hasColumn(TABLE_NAME, "uuid")) {
				CivLog.info("\tCouldn't find `uuid` for resident.");
				SQL.addColumn(TABLE_NAME, "`uuid` VARCHAR(256) NOT NULL DEFAULT 'UNKNOWN'");
			}	
			
			if (!SQL.hasColumn(TABLE_NAME, "currentName")) {
				CivLog.info("\tCouldn't find `currentName` for resident.");
				SQL.addColumn(TABLE_NAME, "`currentName` VARCHAR(64) DEFAULT NULL");
			}	
			
			if (!SQL.hasColumn(TABLE_NAME, "banned")) {
				CivLog.info("\tCouldn't find `banned` for resident.");
				SQL.addColumn(TABLE_NAME, "`banned` bool default 0");
			}			
			
			if (!SQL.hasColumn(TABLE_NAME, "bannedMessage")) {
				CivLog.info("\tCouldn't find `bannedMessage` for resident.");
				SQL.addColumn(TABLE_NAME, "`bannedMessage` mediumtext default null");
			}
			
			if (!SQL.hasColumn(TABLE_NAME, "last_ip")) {
				CivLog.info("\tCouldn't find `last_ip` for resident.");
				SQL.addColumn(TABLE_NAME, "`last_ip` mediumtext default null");
			}
			
			if (!SQL.hasColumn(TABLE_NAME, "camp_id")) {
				CivLog.info("\tCouldn't find `camp_id` for resident.");
				SQL.addColumn(TABLE_NAME, "`camp_id` int(11) default 0");
			}
			
			if (!SQL.hasColumn(TABLE_NAME, "timezone")) {
				CivLog.info("\tCouldn't find `timezone` for resident.");
				SQL.addColumn(TABLE_NAME, "`timezone` mediumtext default null");			
			}
			
			if (!SQL.hasColumn(TABLE_NAME, "debug_civ")) {
				CivLog.info("\tCouldn't find `debug_civ` for resident.");
				SQL.addColumn(TABLE_NAME, "`debug_civ` mediumtext default null");
			}
		
			if (!SQL.hasColumn(TABLE_NAME, "debug_town")) {
				CivLog.info("\tCouldn't find `debug_town` for resident.");
				SQL.addColumn(TABLE_NAME, "`debug_town` mediumtext default null");
			}
			
			SQL.makeCol("flags", "mediumtext", TABLE_NAME);
			SQL.makeCol("savedInventory", "mediumtext", TABLE_NAME);
			SQL.makeCol("isProtected", "bool NOT NULL DEFAULT '0'", TABLE_NAME);
		}		
	}

	@Override
	public void load(ResultSet rs) throws SQLException, InvalidNameException {
		this.setId(rs.getInt("id"));
		this.setName(rs.getString("name"));
		this.townID = rs.getInt("town_id");
		this.campID = rs.getInt("camp_id");
		this.lastIP = rs.getString("last_ip");
		this.debugTown = rs.getString("debug_town");

		if (rs.getString("uuid").equalsIgnoreCase("UNKNOWN")) {
			this.uid = null;
		} else {
			this.uid = UUID.fromString(rs.getString("uuid"));
		}
		
		this.treasury = CivGlobal.createEconObject(this);
		this.getTreasury().setBalance(rs.getDouble("coins"), false);
		this.setGivenKit(rs.getBoolean("givenKit"));
		this.setTimezone(rs.getString("timezone"));
		this.loadFlagSaveString(rs.getString("flags"));
		this.savedInventory = rs.getString("savedInventory");
		this.isProtected = rs.getBoolean("isProtected");
		
		if (this.getTimezone() == null) {
			this.setTimezoneToServerDefault();
		}
		
		if (this.townID != 0) {
			this.setTown(CivGlobal.getTownFromId(this.townID));
			if (this.town == null) {
				CivLog.error("COULD NOT FIND TOWN("+this.townID+") FOR RESIDENT("+this.getId()+") Name:"+this.getName());
				/*
				 * When a town fails to load, we wont be able to find it above.
				 * However this can cause a cascade effect where because we couldn't find
				 * the town above, we save this resident's town as NULL which wipes
				 * their town information from the database when the resident gets saved.
				 * Just to make sure this doesn't happen the boolean below guards resident saves.
				 * There ought to be a better way...
				 */	
				if (CivGlobal.testFileFlag("cleanupDatabase")) {
					this.saveNow();
				} else {
					this.dontSaveTown = true;
				}
				return;
			}
		}
		
		if (this.campID != 0) {
			this.setCamp(CivGlobal.getCampFromId(this.campID));
			if (this.camp == null) {
				CivLog.error("COULD NOT FIND CAMP("+this.campID+") FOR RESIDENT("+this.getId()+") Name:"+this.getName());
			} else {
				camp.addMember(this);
			}
		}
		
		if (this.getTown() != null) {
			try {
				this.getTown().addResident(this);
			} catch (AlreadyRegisteredException e) {
				e.printStackTrace();
			}
		}
		
		this.setLastOnline(rs.getLong("lastOnline"));
		this.setRegistered(rs.getLong("registered"));
		this.setDaysTilEvict(rs.getInt("daysTilEvict"));
		this.getTreasury().setDebt(rs.getDouble("debt"));
		this.loadFriendsFromSaveString(rs.getString("friends"));
		
	}

	private void setTimezoneToServerDefault() {
		this.timezone = EventTimer.getCalendarInServerTimeZone().getTimeZone().getID();
	}

	public String getFlagSaveString() {
		String flagString = "";
		
		if (this.isShowMap()) {
			flagString += "map,";
		}
		
		if (this.isShowTown()) {
			flagString += "showtown,";
		}
		
		if (this.isShowCiv()) {
			flagString += "showciv,";
		}
		
		if (this.isShowScout()) {
			flagString += "showscout,";
		}
		
		if (this.isShowInfo()) {
			flagString += "info,";
		}
		
		if (this.combatInfo) {
			flagString += "combatinfo,";
		}
		if (this.isTitleAPI()) {
			flagString += "titleapi,";
		}
		
		if (this.getShowBuild() == "style") {
			flagString += "style,";
		} else if (this.getShowBuild() == "melon") {
			flagString += "melon,";
		} else if (this.getShowBuild() == "none") {
			flagString += "none,";
		}
		
		if (this.itemMode.equals("rare")) {
			flagString += "itemModeRare,";
		} else if (this.itemMode.equals("none")) {
			flagString += "itemModeNone,";
		}
		
		return flagString;
	}
	
	public void loadFlagSaveString(String str) {
		if (str == null) {
			return;
		}
		
		String[] split = str.split(",");
		
		for (String s : split) {
			switch (s.toLowerCase()) {
			case "map":
				this.setShowMap(true);
				break;
			case "showtown":
				this.setShowTown(true);
				break;
			case "showciv":
				this.setShowCiv(true);
				break;
			case "showscout":
				this.setShowScout(true);
				break;
			case "info":
				this.setShowInfo(true);
				break;
			case "combatinfo":
				this.setCombatInfo(true);
				break;
			case "style":
				this.setShowBuild("style");
				break;
			case "melon":
				this.setShowBuild("melon");
				break;
			case "none":
				this.setShowBuild("none");
				break;
			case "titleapi":
				if (CivSettings.hasTitleAPI)
				{
				this.setTitleAPI(true);
				} else {
					this.setTitleAPI(false);
				}
				break;
			case "itemmoderare":
				this.itemMode = "rare";
				break;
			case "itemmodenone":
				this.itemMode = "none";
				break;
			}
		}
	}
	
	@Override
	public void save() {
		SQLUpdate.add(this);
	}
	
	@Override
	public void saveNow() throws SQLException {
		
		HashMap<String, Object> hashmap = new HashMap<String, Object>();
		
		hashmap.put("name", this.getName());
		hashmap.put("uuid", this.getUUIDString());
		if (this.getTown() != null) {
			hashmap.put("town_id", this.getTown().getId());
		} else {
			if (!dontSaveTown) {
				hashmap.put("town_id", null);
			}
		}
		
		if (this.getCamp() != null) {
			hashmap.put("camp_id", this.getCamp().getId());
		} else {
			hashmap.put("camp_id", null);
		}
		
		hashmap.put("lastOnline", this.getLastOnline());
		hashmap.put("registered", this.getRegistered());
		hashmap.put("debt", this.getTreasury().getDebt());
		hashmap.put("daysTilEvict", this.getDaysTilEvict());
		hashmap.put("friends", this.getFriendsSaveString());
		hashmap.put("givenKit", this.isGivenKit());
		hashmap.put("coins", this.getTreasury().getBalance());
		hashmap.put("timezone", this.getTimezone());
		hashmap.put("flags", this.getFlagSaveString());
		hashmap.put("last_ip", this.getLastIP());
		hashmap.put("savedInventory", this.savedInventory);
		hashmap.put("isProtected", this.isProtected);
		
		if (this.getTown() != null) {
			hashmap.put("debug_town", this.getTown().getName());

			if (this.getTown().getCiv() != null) {
				hashmap.put("debug_civ", this.getCiv().getName());
			}
		}
		
		SQL.updateNamedObject(this, hashmap, TABLE_NAME);
	}
	
	public String getTownString() {
		if (town == null) {
			return "none";
		}
		return this.getTown().getName();
	}
	
	public Town getTown() {
		return town;
	}

	public void setTown(Town town) {
		this.town = town;
	}

	public boolean hasTown() {
		return town != null;
	}

	public long getRegistered() {
		return registered;
	}

	public void setRegistered(long registered) {
		this.registered = registered;
	}

	public long getLastOnline() {
		return lastOnline;
	}

	public void setLastOnline(long lastOnline) {
		this.lastOnline = lastOnline;
	}

	@Override
	public void delete() throws SQLException {	
		SQL.deleteByName(this.getName(), TABLE_NAME);
	}

	public EconObject getTreasury() {
		return treasury;
	}

	public void setTreasury(EconObject treasury) {
		this.treasury = treasury;
	}

	public void onEnterDebt() {
		this.daysTilEvict = CivSettings.GRACE_DAYS;
	}

	public void warnDebt() {
		Player player;
		try {
			player = CivGlobal.getPlayer(this);
			CivMessage.send(player, CivColor.Yellow+CivSettings.localize.localizedString("var_resident_debtmsg",this.getTreasury().getDebt(),CivSettings.CURRENCY_NAME));
			CivMessage.send(player, CivColor.LightGray+CivSettings.localize.localizedString("var_resident_debtEvictAlert1",this.daysTilEvict));
		} catch (CivException e) {
			//Player is not online.
		}
	}
	
	public int getDaysTilEvict() {
		return daysTilEvict;
	}

	public void setDaysTilEvict(int daysTilEvict) {
		this.daysTilEvict = daysTilEvict;
	}

	public void decrementGraceCounters() {
		this.daysTilEvict--;
		if (this.daysTilEvict == 0) {
			this.getTown().removeResident(this);

			try {
				CivMessage.send(CivGlobal.getPlayer(this), CivColor.Yellow+CivSettings.localize.localizedString("resident_evictedAlert"));
			} catch (CivException e) {
				// Resident not online.
			}
			return;
		}
		
		if (this.getTreasury().inDebt()) {
			warnDebt();
		} else {
			warnEvict();
		}
		
		this.save();
	}
	
	public double getPropertyTaxOwed() {
		double total = 0;
		
		if (this.getTown() == null) {
			return total;
		}
		
		for (TownChunk tc : this.getTown().getTownChunks()) {
			if (tc.perms.getOwner() == this) {
				double tax = tc.getValue()*this.getTown().getTaxRate();
				total += tax;
			}
		}
		return total;
	}
	
	public boolean isLandOwner() {
		if (this.getTown() == null) 
			return false;
		
		for (TownChunk tc : this.getTown().getTownChunks()) {
			if (tc.perms.getOwner() == this) {
				return true;
			}
		}
		
		return false;
	}
	
	
	public double getFlatTaxOwed() {
		if (this.getTown() == null)
			return 0;
		
		return this.getTown().getFlatTax();
	}

	public boolean isTaxExempt() {
		return this.getTown().isInGroup("mayors", this) || this.getTown().isInGroup("assistants", this);
	}

	public void payOffDebt() {
		this.getTreasury().payTo(this.getTown().getTreasury(), this.getTreasury().getDebt());
		this.getTreasury().setDebt(0);
		this.daysTilEvict = -1;
		this.save();
	}
	
	public void addFriend(Resident resident) {
		friends.put(resident.getName(), 1);
	}
	
	public boolean isFriend(Resident resident) {		
		return  friends.containsKey(resident.getName());
	}
	
	public Collection<String> getFriends() {
		return friends.keySet();
	}
	
	private String getFriendsSaveString() {
		String out = "";
		for (String name : friends.keySet()) {
			out += name+",";
		}
		return out;
	}
	
	private void loadFriendsFromSaveString(String string) {
		String[] split = string.split(",");
		
		for (String str : split) {
			friends.put(str, 1);
		}
	}

	public void removeFriend(Resident friendToAdd) {
		friends.remove(friendToAdd.getName());
	}

	public String getGroupsString() {
		String out = "";
		
		for (PermissionGroup grp : CivGlobal.getGroups()) {
			if (grp.hasMember(this)) {
				if (grp.getTown() != null) {
					if (grp.isProtectedGroup()) {
						out += CivColor.LightPurple;
					} else {
						out += CivColor.White;
					}
					out += grp.getName()+"("+grp.getTown().getName()+")";
					
				} else if (grp.getCiv() != null) {
					out += CivColor.Gold+grp.getName()+"("+grp.getCiv().getName()+")";
				}
				
				out += ", ";
			} 
		}
		
		return out;
	}

	public void warnEvict() {
		try {
			CivMessage.send(CivGlobal.getPlayer(this), CivColor.Yellow+CivSettings.localize.localizedString("var_resident_evictionNotice1",this.getDaysTilEvict()));
		} catch (CivException e) {
			//player offline.
		}
	}

	public boolean isTownChat() {
		return townChat;
	}

	public void setTownChat(boolean townChat) {
		this.townChat = townChat;
	}

	public boolean isCivChat() {
		return civChat;
	}

	public void setCivChat(boolean civChat) {
		this.civChat = civChat;
	}

	public boolean isAdminChat() {
		return adminChat;
	}

	public void setAdminChat(boolean adminChat) {
		this.adminChat = adminChat;
	}

	public Town getTownChatOverride() {
		return townChatOverride;
	}

	public void setTownChatOverride(Town townChatOverride) {
		this.townChatOverride = townChatOverride;
	}

	public Civilization getCivChatOverride() {
		return civChatOverride;
	}

	public void setCivChatOverride(Civilization civChatOverride) {
		this.civChatOverride = civChatOverride;
	}

	public boolean isPermOverride() {
		return permOverride;
	}

	public void setPermOverride(boolean permOverride) {
		this.permOverride = permOverride;
	}
	
	@SuppressWarnings("deprecation")
	public int takeItemsInHand(Material itemId, Material itemData) throws CivException {
		Player player = CivGlobal.getPlayer(this);
		Inventory inv = player.getInventory();
		if (!inv.contains(itemId)) {
			return 0;
		}

		if ((player.getInventory().getItemInMainHand().getType() != itemId) &&
				(player.getInventory().getItemInMainHand().getType() != itemData)) {
			return 0;
		}
		
		ItemStack stack = player.getInventory().getItemInMainHand();
		int count = stack.getAmount();
		inv.removeItem(stack);
		
		player.updateInventory();
		return count;
	}
	
	
	@SuppressWarnings("deprecation")
	public boolean takeItemInHand(Material itemId, Material itemData, int amount) throws CivException {
		Player player = CivGlobal.getPlayer(this);
		Inventory inv = player.getInventory();
	
		if (!inv.contains(itemId)) {
			return false;
		}

		if ((player.getInventory().getItemInMainHand().getType() != itemId) &&
				(player.getInventory().getItemInMainHand().getType() != itemData)) {
			return false;
		}
		
		ItemStack stack = player.getInventory().getItemInMainHand();
		
		if (stack.getAmount() < amount) {
			return false;
		} else if (stack.getAmount() == amount) {
			inv.removeItem(stack);
		} else {
			stack.setAmount(stack.getAmount() - amount);
		}
		
		player.updateInventory();
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public boolean takeItem(Material itemId, int amount) throws CivException {
		Player player = CivGlobal.getPlayer(this);
		Inventory inv = player.getInventory();
	
		if (!inv.contains(itemId)) {
			return false;
		}
		
		HashMap<Integer, ? extends ItemStack> stacks;
		stacks = inv.all(itemId);
		
		for (ItemStack stack : stacks.values()) {
			
			if (stack.getAmount() <= 0)
				continue;
			
			if (stack.getAmount() < amount) {
				amount -= stack.getAmount();
				stack.setAmount(0);
				inv.removeItem(stack);
				continue;
			}
			else {			
				stack.setAmount(stack.getAmount()-amount);
				break;
			}
		}
		
		player.updateInventory();
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public int giveItem(Material itemId, int amount) throws CivException {
		Player player = CivGlobal.getPlayer(this);
		Inventory inv = player.getInventory();
		ItemStack stack = new ItemStack(itemId, amount);
		HashMap <Integer, ItemStack> leftovers = null;
		leftovers = inv.addItem(stack);
		
		int leftoverAmount = 0;
		for (ItemStack i : leftovers.values()) {
			leftoverAmount += i.getAmount();
		}
		player.updateInventory();
		return amount - leftoverAmount;
	}
	
	public boolean buyItem(String itemName, Material id, double price, int amount) throws CivException {
		
		if (!this.getTreasury().hasEnough(price)) {
			throw new CivException(CivSettings.localize.localizedString("resident_notEnoughMoney")+" "+CivSettings.CURRENCY_NAME);
		}
		
		boolean completed = true;
		int bought = 0;
		bought = giveItem(id, amount);
		if (bought != amount) {
			this.getTreasury().withdraw(price);
			takeItem(id, bought);
			completed = false;
		} else {
			this.getTreasury().withdraw(price);
		}
		
		if (completed) {
			return true;
		}
		else {
			throw new CivException(CivSettings.localize.localizedString("resident_buyInvenFull"));
		}
	}

	public Civilization getCiv() {
		if (this.getTown() == null) {
			return null;
		}
		return this.getTown().getCiv();
	}
	
	@SuppressWarnings("deprecation")
	public void setScoreboardName(String name, String key) {
		if (this.scoreboard == null) {
			this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
			Team team = this.scoreboard.registerNewTeam("team");
			team.addPlayer(CivGlobal.getFakeOfflinePlayer(key));
			team.setDisplayName(name);
		} else {
			Team team = this.scoreboard.getTeam("team");
			team.setDisplayName(name);
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public void setScoreboardValue(String name, String key, int value) {
		if (this.scoreboard == null) {
			return;
		}
		
		Objective obj = scoreboard.getObjective("obj:"+key);
		if (obj == null) {
			obj = scoreboard.registerNewObjective(name, "dummy");
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
			Score score = obj.getScore(CivGlobal.getFakeOfflinePlayer(key));
			score.setScore(value);
		} else {
			Score score = obj.getScore(CivGlobal.getFakeOfflinePlayer(key));
			score.setScore(value);
		}
	}
	
	public void showScoreboard() {
		if (this.scoreboard != null) {
			Player player;
			try {
				player = CivGlobal.getPlayer(this);
				player.setScoreboard(this.scoreboard);
			} catch (CivException e) {
				e.printStackTrace();
			}
		} 
	}
	
	public void hideScoreboard() {
		Player player;
		try {
			player = CivGlobal.getPlayer(this);
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		} catch (CivException e) {
			e.printStackTrace();
		}
	}

	public boolean isGivenKit() {
		return givenKit;
	}

	public void setGivenKit(boolean givenKit) {
		this.givenKit = givenKit;
	}

	public boolean isSBPermOverride() {
		return sbperm;
	}

	public void setSBPermOverride(boolean b) {
		sbperm = b;
	}

	public void setInteractiveMode(InteractiveResponse interactive) {
		this.interactiveMode = true;
		this.interactiveResponse = interactive;
	}
	
	public void clearInteractiveMode() {
		this.interactiveMode = false;
		this.interactiveResponse = null;
	}
	
	public InteractiveResponse getInteractiveResponse() {
		return this.interactiveResponse;
	}

	public boolean isInteractiveMode() {
		return interactiveMode;
	}

	public Town getSelectedTown() {
		return selectedTown;
	}

	public void setSelectedTown(Town selectedTown) {
		this.selectedTown = selectedTown;
	}

	public Camp getCamp() {
		return camp;
	}

	public void setCamp(Camp camp) {
		this.camp = camp;
	}

	public boolean hasCamp() {
		return (this.camp != null);
	}

	public String getCampString() {
		if (this.camp == null) {
			return "none";
		}
		return this.camp.getName();
	}

	public void showWarnings(Player player) {
		/* Notify Resident of any invalid structures. */
		if (this.getTown() != null) {
			for (Buildable struct : this.getTown().invalidStructures) {
				CivMessage.send(player, CivColor.Yellow+ChatColor.BOLD+
						CivSettings.localize.localizedString("var_resident_structInvalidAlert1",struct.getDisplayName(),struct.getCenterLocation())+
						" "+CivSettings.localize.localizedString("resident_structInvalidAlert2")+" "+struct.getInvalidReason());
			}
			
			/* Show any event messages. */
			if (this.getTown().getActiveEvent() != null) {
				CivMessage.send(player, CivColor.Yellow+CivSettings.localize.localizedString("var_resident_eventNotice1",this.getTown().getActiveEvent().configRandomEvent.name));
			}
		}
		
		
	}

	public boolean isShowScout() {
		return showScout;
	}

	public void setShowScout(boolean showScout) {
		this.showScout = showScout;
	}

	public boolean isShowTown() {
		return showTown;
	}

	public void setShowTown(boolean showTown) {
		this.showTown = showTown;
	}

	public boolean isShowCiv() {
		return showCiv;
	}

	public void setShowCiv(boolean showCiv) {
		this.showCiv = showCiv;
	}

	public boolean isShowMap() {
		return showMap;
	}

	public void setShowMap(boolean showMap) {
		this.showMap = showMap;
	}
	
	public void startPreviewTask(Template tpl, Block block, UUID uuid) {
		for (int y = 0; y < tpl.size_y; y++) {
			for (int x = 0; x < tpl.size_x; x++) {
				for (int z = 0; z < tpl.size_z; z++) {
					Block b = block.getRelative(x, y, z);
					
					if (tpl.blocks[x][y][z].isAir()) {
						continue;
					}
					BlockData data = null;
					if ( getShowBuild() == "style" ) {
						data = tpl.blocks[x][y][z].getData();
					} else if ( getShowBuild() == "melon" ) {
						data = Bukkit.createBlockData(Material.MELON);
					} 
					ItemManager.sendBlockChange(Bukkit.getPlayer(uuid), b.getLocation(), data);
					previewUndo.put(new BlockCoord(b.getLocation()),
							new SimpleBlock(ItemManager.getType(b), ItemManager.getData(b)));			
				}
			}
		}
		//this.previewTask = new BuildPreviewAsyncTask(tpl, block, uuid);
		//TaskMaster.asyncTask(previewTask, 0);
	}
	
	public void undoPreview() {
		if (this.previewUndo == null) {
			this.previewUndo = new ConcurrentHashMap<BlockCoord, SimpleBlock>();
			return;
		}
		
		
		/*synchronized(this)
	    {
			if (this.previewTask != null) {
				try {
					previewTask.lock.lockInterruptibly();
					try {
						previewTask.aborted = true;
					} finally {
						previewTask.lock.unlock();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					return;
				}
			}
	    }*/
		
		try {
			Player player = CivGlobal.getPlayer(this);
			PlayerBlockChangeUtil util = new PlayerBlockChangeUtil();
			for (BlockCoord coord : this.previewUndo.keySet()) {
				SimpleBlock sb = this.previewUndo.get(coord);
				util.addUpdateBlock(player.getName(), coord, sb.getType(), sb.getData());
 			}
			
			util.sendUpdate(player.getName());
		} catch (CivException e) {
			//Fall down and return.
		}
		
		this.previewUndo.clear();
		this.previewUndo = new ConcurrentHashMap<BlockCoord, SimpleBlock>();
	}

	public boolean isShowInfo() {
		return showInfo;
	}

	public void setShowInfo(boolean showInfo) {
		this.showInfo = showInfo;
	}

	public boolean isBanned() {
		return banned;
	}

	public void setBanned(boolean banned) {
		this.banned = banned;
	}

	public double getSpyExposure() {
		return spyExposure;
	}

	public void setSpyExposure(double spyExposure) {
		this.spyExposure = spyExposure;
		
		try {
			Player player = CivGlobal.getPlayer(this);
			double percentage = spyExposure / MAX_SPY_EXPOSURE;
			player.setExp((float)percentage);
		} catch (CivException e) {
		}
		
	}

	public boolean isPerformingMission() {
		return performingMission;
	}

	public void setPerformingMission(boolean performingMission) {
		this.performingMission = performingMission;
	}
	
	public void giveTemplate(String name) {
		int perkCount;
		try {
			perkCount = CivSettings.getInteger(CivSettings.perkConfig, "system.free_perk_count");
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			return;
		}
		for (ConfigPerk p : CivSettings.perks.values()) {
			Perk perk = new Perk(p);
			
			if (perk.getIdent().startsWith(("tpl_"+name).toLowerCase()) || perk.getIdent().startsWith(("template_"+name).toLowerCase()))
			{
				perk.count = perkCount;
				this.perks.put(perk.getIdent(), perk);
			}
		}
		
	}

	public void giveAllFreePerks() {
		int perkCount;
		try {
			perkCount = CivSettings.getInteger(CivSettings.perkConfig, "system.free_perk_count");
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			return;
		}
		
		for (ConfigPerk p : CivSettings.perks.values()) {
			Perk perk = new Perk(p);
			
			if (perk.getIdent().startsWith("perk_"))
			{
				perk.count = perkCount;
				this.perks.put(perk.getIdent(), perk);
			}
		}
		
	}
	
	public void loadPerks(final Player player) {
//		if (!PlatinumManager.isEnabled()) {
//			return;
//		}
		
		class AsyncTask implements Runnable {
			Resident resident;
			
			public AsyncTask(Resident resident) {
				this.resident = resident;
			}
			
			@Override
			public void run() {
				try {
                    resident.perks.clear();
					Player player = CivGlobal.getPlayer(resident);				
					try {
						CivGlobal.perkManager.loadPerksForResident(resident);
					} catch (SQLException e) {
						CivMessage.sendError(player, CivSettings.localize.localizedString("resident_couldnotloadperks"));
						e.printStackTrace();
						return;
					} catch (NotVerifiedException e) {
						return;
					}	
				} catch (CivException e1) {
					return;
				}
				try {

					String perkMessage = "";
					if (CivSettings.getString(CivSettings.perkConfig, "system.free_perks").equalsIgnoreCase("true")) {
						resident.giveAllFreePerks();
						perkMessage = CivSettings.localize.localizedString("PlayerLoginAsync_perksMsg1")+" ";
					} else if (CivSettings.getString(CivSettings.perkConfig, "system.free_admin_perks").equalsIgnoreCase("true")) {
						if (player.hasPermission(CivSettings.MINI_ADMIN) || player.hasPermission(CivSettings.FREE_PERKS)) {
							resident.giveAllFreePerks();
							perkMessage = CivSettings.localize.localizedString("PlayerLoginAsync_perksMsg1")+": ";
							perkMessage += "Weather"+", ";
						}
					}
					
					for (ConfigPerk p : CivSettings.templates.values()) {
						if (player.hasPermission("civ.perk."+p.simple_name)) {
							resident.giveTemplate(p.simple_name);
							perkMessage += p.display_name+", ";
						}
					}

					perkMessage += CivSettings.localize.localizedString("PlayerLoginAsync_perksMsg2");
					
					CivMessage.send(resident, CivColor.LightGreen+perkMessage);
				} catch (InvalidConfiguration e) {
					e.printStackTrace();
				}
				
				/* User was verified, lets see if it was the first time. */
//				PlatinumManager.givePlatinumOnce(resident,
//				CivSettings.platinumRewards.get("loginFirstVerified").name, 
//				CivSettings.platinumRewards.get("loginFirstVerified").amount, 
//				"Achievement! First time you've logged in while verified! %d");
			}
		}
		
		TaskMaster.asyncTask(new AsyncTask(this), 0);
	}
	
	public void setBadLeader() {	
		cleanupBadLeader();
		
		String value = ""+this.getName();
		String key = getBadLeaderKey();
		CivGlobal.getSessionDB().add(key, value, 0, 0, 0);
		
		TextComponent textComponent2 = Component.text()
				  .content("На вас наложили эффект ").color(NamedTextColor.RED)
				  .append(Component.text().content("\"Плохой Вождь\"").clickEvent(ClickEvent.runCommand("/accept")).hoverEvent(HoverEvent.showText(Component.text("Эффект \"Плохой Вождь\" накладывается на 2 часа после того, как вы покидаете лагерь, лагерь был разрушен, вы расформировали лагерь").color(NamedTextColor.GREEN))).color(NamedTextColor.GOLD).build())
				  .append(Component.text("!").color(NamedTextColor.RED))
				  .build();
		try {
			CivGlobal.getPlayer(this).sendMessage(textComponent2);
		} catch (CivException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getBadLeaderKey() {
		return "badleader:"+this.getName();
	}
	
	public void cleanupBadLeader() {
		CivGlobal.getSessionDB().delete_all(getBadLeaderKey());
	}

	public void setRejoinCooldown(Town town) {	
		String value = ""+town.getCiv().getId();
		String key = getCooldownKey();
		CivGlobal.getSessionDB().add(key, value, 0, 0, 0);
	}
	
	public String getCooldownKey() {
		return "cooldown:"+this.getName();
	}
	
	public void cleanupCooldown() {
		CivGlobal.getSessionDB().delete_all(getCooldownKey());
	}
	
	public void validateJoinTown(Town town) throws CivException {
		if (this.hasTown() && this.getCiv() == town.getCiv()) {
			/* allow players to join the same civ, no probs */
			return;
		}
		
		long cooldownTime;
		int cooldownHours;
		try {
			cooldownHours = CivSettings.getInteger(CivSettings.civConfig, "global.join_civ_cooldown");
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			return;
		}
		
		cooldownTime = cooldownHours * 60*60*1000; /*convert hours to milliseconds. */

		ArrayList<SessionEntry> entries = CivGlobal.getSessionDB().lookup(getCooldownKey());
		if (entries.size() > 0) {
			Civilization oldCiv = CivGlobal.getCivFromId(Integer.valueOf(entries.get(0).value));
			if (oldCiv == null) {
				/* Hmm old civ is gone. */
				cleanupCooldown();
				return;
			}
			
			if (oldCiv == town.getCiv()) {
				/* We're rejoining the old civ, allow it. */
				cleanupCooldown();
				return;
			}
			
			/* Check if cooldown is expired. */
			Date now = new Date();
			if (now.getTime() > (entries.get(0).time + cooldownTime)) {
				/* Entry is expired, allow cooldown and cleanup. */
				cleanupCooldown();
				return;
			}
			
			throw new CivException(CivSettings.localize.localizedString("var_resident_cannotJoinCivJustLeft1",cooldownHours));
		}	
	}
	
	public LinkedList<Perk> getPersonalTemplatePerks(ConfigBuildableInfo info) {
		LinkedList<Perk> templates = new LinkedList<Perk>();
		
		for (Perk perk : this.perks.values()) {
			CustomPersonalTemplate customTemplate = (CustomPersonalTemplate) perk.getComponent("CustomPersonalTemplate");
			if (customTemplate == null) {
				continue;
			}
			
			if (customTemplate.getString("id").equals(info.id)) {
				templates.add(perk);
			}
		}
		return templates;
	}

	public ArrayList<Perk> getUnboundTemplatePerks(ArrayList<Perk> alreadyBoundPerkList, ConfigBuildableInfo info) {
		ArrayList<Perk> unboundPerks = new ArrayList<Perk>();
		for (Perk ourPerk : perks.values()) {

			if (!ourPerk.getIdent().contains("template"))
			{
			CustomTemplate customTemplate = (CustomTemplate) ourPerk.getComponent("CustomTemplate");
			if (customTemplate == null) {
				continue;
			}
			
			if (!customTemplate.getString("template").equals(info.template_base_name)) {
				/* Not the correct template. */
				continue;
			}
			
			for (Perk perk : alreadyBoundPerkList) {
				if (perk.getIdent().equals(ourPerk.getIdent())) {
					/* Perk is already bound in this town, do not display for binding. */
					break;
				}
			}
			
			unboundPerks.add(ourPerk);
			}
		}
		
		return unboundPerks;
	}

	public boolean isControlBlockInstantBreak() {
		return controlBlockInstantBreak;
	}

	public void setControlBlockInstantBreak(boolean controlBlockInstantBreak) {
		this.controlBlockInstantBreak = controlBlockInstantBreak;
	}

	
	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	public boolean isCombatInfo() {
		return combatInfo;
	}

	public void setCombatInfo(boolean combatInfo) {
		this.combatInfo = combatInfo;
	}

	public boolean isInactiveForDays(int days) {
		Calendar now = Calendar.getInstance();
		Calendar expire = Calendar.getInstance();
		expire.setTimeInMillis(this.getLastOnline());
		
		expire.add(Calendar.DATE, days);
		
		if (now.after(expire)) {
			return true;
		}
		
		return false;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	
	public boolean hasTechForItem(ItemStack stack) {
		
		LoreCraftableMaterial craftMat = LoreCraftableMaterial.getCraftMaterial(stack);
		if (craftMat == null) {
			return true;
		}
		
		if (craftMat.getConfigMaterial().required_tech == null) {
			return true;
		}
		
		if (!this.hasTown()) {
			return false;
		}
		
		/* Parse technoloies */
		String[] split = craftMat.getConfigMaterial().required_tech.split(",");
		for (String tech : split) {
			tech = tech.replace(" ", "");
			if (!this.getCiv().hasTechnology(tech)) {
				return false;
			}
		}
		
		return true;	
	}

	public Date getLastKilledTime() {
		return lastKilledTime;
	}

	public void setLastKilledTime(Date lastKilledTime) {
		this.lastKilledTime = lastKilledTime;
	}

	public Date getMuteExpires() {
		return muteExpires;
	}

	public void setMuteExpires(Date muteExpires) {
		this.muteExpires = muteExpires;
	}

	public String getItemMode() {
		return itemMode;
	}

	public void setItemMode(String itemMode) {
		this.itemMode = itemMode;
	}

	public void toggleItemMode() {
		if (this.itemMode.equals("all")) {
			this.itemMode = "rare";
			CivMessage.send(this, CivColor.LightGreen+CivSettings.localize.localizedString("resident_toggleItemRare"));
		} else if (this.itemMode.equals("rare")) {
			this.itemMode = "none";
			CivMessage.send(this, CivColor.LightGreen+CivSettings.localize.localizedString("resident_toggleItemNone"));
		} else {
			this.itemMode = "all";
			CivMessage.send(this, CivColor.LightGreen+CivSettings.localize.localizedString("resident_toggleItemAll"));
		}
		this.save();
	}
	
	public void setLastIP(String hostAddress) {
		this.lastIP = hostAddress;
	}
	
	public String getLastIP() {
		return this.lastIP;
	}

	public void teleportHome() {
		Player player;
		try {
			player = CivGlobal.getPlayer(this);
			teleportHome(player);
		} catch (CivException e) {
			return;
		}
	}
	
	public void teleportHome(Player player) {		
		if (this.hasTown()) {
			TownHall townhall = this.getTown().getTownHall();
			if (townhall != null) {
				BlockCoord coord = townhall.getRandomRevivePoint();
				player.teleport(coord.getLocation());
			}
		} else {
			World world = Bukkit.getWorld("world");
			player.teleport(world.getSpawnLocation());
		}
	}
	
	public boolean canDamageControlBlock() {
		if (this.hasTown()) {
			if (!this.getCiv().getCapitolStructure().isValid()) {
				return false;
			}
		}
		
		return true;
	}

	public boolean isUsesAntiCheat() throws CivException {
		CivGlobal.getPlayer(this);
		return usesAntiCheat;
	}

	public void setUsesAntiCheat(boolean usesAntiCheat) {
		this.usesAntiCheat = usesAntiCheat;
	}
	
	public void saveInventory() {
		try {
			Player player = CivGlobal.getPlayer(this);			
			String serial =  InventorySerializer.InventoryToString(player.getInventory());
			this.setSavedInventory(serial);
			this.save();
		} catch (CivException e) {
		}
	}
	
	public void clearInventory() {
		try {
			Player player = CivGlobal.getPlayer(this);
			player.getInventory().clear();
			player.getInventory().setArmorContents(new ItemStack[4]);
		} catch (CivException e) {
		}
	}

	public void restoreInventory() {
		if (this.savedInventory == null) {
			return;
		}
		
		try {
			Player player = CivGlobal.getPlayer(this);
			clearInventory();
			InventorySerializer.StringToInventory(player.getInventory(), this.savedInventory);
			this.setSavedInventory(null);
			this.save();
		} catch (CivException e) {
			// Player offline??
			e.printStackTrace();
			this.setSavedInventory(null);
			this.save();
		}
	}
	
	public String getSavedInventory() {
		return savedInventory;
	}

	public void setSavedInventory(String savedInventory) {
		this.savedInventory = savedInventory;
	}
	
	public boolean isProtected() {
		return isProtected;
	}
	
	public void setisProtected(boolean prot) {
		isProtected = prot;
	}
	
	public void showPerkPage(int pageNumber) {
		Player player;
		try {
			player = CivGlobal.getPlayer(this);
		} catch (CivException e) {
			return;
		}
		
		Inventory inv = Bukkit.getServer().createInventory(player, CivTutorial.MAX_CHEST_SIZE*9, CivSettings.localize.localizedString("resident_perksGuiHeading"));
		
		for (Object obj : perks.values()) {
			Perk p = (Perk)obj;

			if (p.getIdent().startsWith("temp"))
			{
				ItemStack stack = LoreGuiItem.build(p.configPerk.display_name, 
						p.configPerk.type_id, 
					    CivColor.LightBlue+CivSettings.localize.localizedString("resident_perksGuiClickToView"),
						CivColor.LightBlue+CivSettings.localize.localizedString("resident_perksGuiTheseTemplates"));
				stack = LoreGuiItem.setAction(stack, "ShowTemplateType");
				stack = LoreGuiItem.setActionData(stack, "perk", p.configPerk.id);

				inv.addItem(stack);
			}
			else if (p.getIdent().startsWith("perk"))
			{
				ItemStack stack = LoreGuiItem.build(p.getDisplayName(), 
						p.configPerk.type_id, 
						CivColor.Gold+CivSettings.localize.localizedString("resident_perksGui_clickToActivate"),
						"Unlimted Uses");
				stack = LoreGuiItem.setAction(stack, "ActivatePerk");
				stack = LoreGuiItem.setActionData(stack, "perk", p.configPerk.id);

				inv.addItem(stack);
				
			}
			
		}
		
		player.openInventory(inv);
	}
	
	public void showTemplatePerks(String name) {
		Player player;
		try {
			player = CivGlobal.getPlayer(this);
		} catch (CivException e) {
			return;
		}
		
		Inventory inv = Bukkit.getServer().createInventory(player, CivTutorial.MAX_CHEST_SIZE*9, CivSettings.localize.localizedString("resident_perksGui_templatesHeading")+" "+name);
		
		for (Object obj : perks.values()) {
			Perk p = (Perk)obj;
			if (p.getIdent().contains("tpl_" +name))
			{
			ItemStack stack = LoreGuiItem.build(p.configPerk.display_name, 
					p.configPerk.type_id, 
					CivColor.Gold+CivSettings.localize.localizedString("resident_perksGui_clickToActivate"),
					CivColor.LightBlue+"Count: "+p.count);
			stack = LoreGuiItem.setAction(stack, "ActivatePerk");
			stack = LoreGuiItem.setActionData(stack, "perk", p.configPerk.id);

			inv.addItem(stack);
			}
		}
		
		player.openInventory(inv);
	}

	public UUID getUUID() {
		return uid;
	}
	
	public String getUUIDString() {
		return uid.toString();
	}
	
	public void setUUID(UUID uid) {
		this.uid = uid;
	}

	public double getWalkingModifier() {
		return walkingModifier;
	}

	public void setWalkingModifier(double walkingModifier) {
		this.walkingModifier = walkingModifier;
	}
	
	public void calculateWalkingModifier(Player player) {
		double speed = CivSettings.normal_speed;
		
		/* Set speed from armor. */
		if (Unit.isWearingFullComposite(player)) {
			speed *= CivSettings.T4_leather_speed;
		} else if (Unit.isWearingFullHardened(player)) {
			speed *= CivSettings.T3_leather_speed;
		} else if (Unit.isWearingFullRefined(player)) {
			speed *= CivSettings.T2_leather_speed;
		} else if (Unit.isWearingFullBasicLeather(player)) {
			speed *= CivSettings.T1_leather_speed;
		} else {
			if (Unit.isWearingAnyDiamond(player)) {
				speed *= CivSettings.T4_metal_speed;
			} else if (Unit.isWearingAnyGold(player)) {
				speed *= CivSettings.T3_metal_speed;
			} else if (Unit.isWearingAnyChain(player)) {
				speed *= CivSettings.T2_metal_speed;
			} else if (Unit.isWearingAnyIron(player)) {
				speed *= CivSettings.T1_metal_speed;
			}
		}
		this.walkingModifier = speed;
	}

	public boolean isTitleAPI() {
		return titleAPI;
	}

	public void setTitleAPI(boolean titleAPI) {
		this.titleAPI = titleAPI;
	}
	
	public String getShowBuild() {
		return showBuild;
	}
	
	public void setShowBuild(String showBuild) {
		this.showBuild = showBuild;
	}
}
