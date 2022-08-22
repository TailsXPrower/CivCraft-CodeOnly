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
package ru.tailsxcraft.civcraft.structure;

import gpl.HorseModifier;
import gpl.HorseModifier.HorseType;
import gpl.HorseModifier.HorseVariant;
import ru.tailsxcraft.civcraft.components.NonMemberFeeComponent;
import ru.tailsxcraft.civcraft.components.SignSelectionActionInterface;
import ru.tailsxcraft.civcraft.components.SignSelectionComponent;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigStableHorse;
import ru.tailsxcraft.civcraft.config.ConfigStableItem;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.items.BonusGoodie;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.StructureSign;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.util.ChunkCoord;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.civcraft.util.HashTreeSet;
import ru.tailsxcraft.civcraft.util.ItemManager;
import ru.tailsxcraft.civcraft.util.SimpleBlock;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Stable extends Structure {

	public static Integer FEE_MIN = 5;
	public static Integer FEE_MAX = 100;
	private HashMap<Integer, SignSelectionComponent> signSelectors = new HashMap<Integer, SignSelectionComponent>();
	private BlockCoord horseSpawnCoord;
	private BlockCoord muleSpawnCoord;
	private NonMemberFeeComponent nonMemberFeeComponent;
	
	public HashTreeSet<ChunkCoord> chunks = new HashTreeSet<ChunkCoord>();
	public static Map<ChunkCoord, Stable> stableChunks = new ConcurrentHashMap<ChunkCoord, Stable>();
	
	public Stable(ResultSet rs) throws SQLException, CivException {
		super(rs);
		nonMemberFeeComponent = new NonMemberFeeComponent(this);
		nonMemberFeeComponent.onLoad();
	}

	protected Stable(Location center, String id, Town town) throws CivException {
		super(center, id, town);
		nonMemberFeeComponent = new NonMemberFeeComponent(this);
		nonMemberFeeComponent.onSave();
	}
	
	public void bindStableChunks() {
		for (BlockCoord bcoord : this.structureBlocks.keySet()) {
			ChunkCoord coord = new ChunkCoord(bcoord);
			this.chunks.add(coord);
			stableChunks.put(coord, this);
		}
	}
	
	public void unbindStableChunks() {
		for (ChunkCoord coord : this.chunks) {
			stableChunks.remove(coord);
		}
		this.chunks.clear();
	}
	
	@Override
	public void onComplete() {
		bindStableChunks();
	}
	
	@Override
	public void onLoad() throws CivException {
		bindStableChunks();
	}
	
	@Override
	public void delete() throws SQLException {
		super.delete();
		unbindStableChunks();
	}
	
	public void loadSettings() {
		super.loadSettings();

		SignSelectionComponent horseVender = new SignSelectionComponent();
		SignSelectionComponent muleVender = new SignSelectionComponent();
		SignSelectionComponent itemVender = new SignSelectionComponent();
		
		signSelectors.put(0, horseVender);
		signSelectors.put(1, muleVender);
		signSelectors.put(2, itemVender);

		class buyHorseAction implements SignSelectionActionInterface {
			int horse_id;
			double cost;
			
			public buyHorseAction(int horse_id, double cost) {
				this.horse_id = horse_id;
				this.cost = cost;
			}
			
			@Override
			public void process(Player player) {
				ConfigStableHorse horse = CivSettings.horses.get(horse_id);
				if (horse == null) {
					CivMessage.sendError(player, CivSettings.localize.localizedString("stable_unknownHorse"));
					return;
				}
				if (horse_id >= 5 && !getCiv().hasTechnology("tech_military_science"))
				{
					CivMessage.sendError(player, CivSettings.localize.localizedString("stable_missingTech_MilitaryScience"));
					return;
				}
				
				Resident resident = CivGlobal.getResident(player);
				if (!horse.mule) {
					boolean allow = false;
					for (BonusGoodie goodie : getTown().getBonusGoodies()) {
						if (goodie.getConfigTradeGood().id.equals("good_horses")) {
							allow = true;
							break;
						}
					}
					
					if (!allow) {
						CivMessage.sendError(player, CivSettings.localize.localizedString("stable_townNoHorses"));
						return;
					}
				}
			
				double paid;
				if (resident.getTown() != getTown()) {
					if (!resident.getTreasury().hasEnough(getItemCost(cost))) {
						CivMessage.sendError(player, CivSettings.localize.localizedString("var_config_marketItem_notEnoughCurrency",(getItemCost(cost)+" "+CivSettings.CURRENCY_NAME)));

						return;
					}
					
					
					resident.getTreasury().withdraw(getItemCost(cost));
					getTown().depositTaxed(getFeeToTown(cost));
					CivMessage.send(player, CivColor.Yellow+CivSettings.localize.localizedString("var_taxes_paid",getFeeToTown(cost),CivSettings.CURRENCY_NAME));
					paid = getItemCost(cost);
				} else {
					if (!resident.getTreasury().hasEnough(cost)) {
						CivMessage.sendError(player, CivSettings.localize.localizedString("var_config_marketItem_notEnoughCurrency",(cost+" "+CivSettings.CURRENCY_NAME)));
						return;
					}

					resident.getTreasury().withdraw(cost);
					paid = cost;
				}	

				HorseModifier mod;	
				if (!horse.mule) {			
					mod = HorseModifier.spawn(horseSpawnCoord.getLocation(), EntityType.HORSE);
					mod.setTamed(true);
					mod.setSaddled(true);
				} else {
					mod = HorseModifier.spawn(muleSpawnCoord.getLocation(), EntityType.MULE);
				}
				
				mod.setVariant(HorseVariant.valueOf(horse.variant));
				HorseModifier.setHorseSpeed(mod.getHorse(), horse.speed);
				((Horse)mod.getHorse()).setJumpStrength(horse.jump);
				((Horse)mod.getHorse()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(horse.health);
				((Horse)mod.getHorse()).setHealth(horse.health);
				((Horse)mod.getHorse()).setOwner(player);
				((Horse)mod.getHorse()).setCustomName(horse.name);
				((Horse)mod.getHorse()).setCustomNameVisible(true);
				
				CivMessage.send(player, CivColor.LightGreen+CivSettings.localize.localizedString("var_stable_buySuccess",paid,CivSettings.CURRENCY_NAME));
			}
		}
		
		class buyItemAction implements SignSelectionActionInterface {

			Material item_id;
			double cost;
			
			public buyItemAction(Material item_id, double cost) {
				this.item_id = item_id;
				this.cost = cost;
			}
			
			@Override
			public void process(Player player) {

				Resident resident = CivGlobal.getResident(player);
				if ((item_id == Material.IRON_HORSE_ARMOR && item_id == Material.GOLDEN_HORSE_ARMOR && item_id == Material.DIAMOND_HORSE_ARMOR) && !getCiv().hasTechnology("tech_military_science"))
				{
					CivMessage.sendError(player, CivSettings.localize.localizedString("stable_missingTech_MilitaryScience"));
					return;
				}
				
				double paid;
				if (resident.getTown() != getTown()) {
					if (!resident.getTreasury().hasEnough(getItemCost(cost))) {
						CivMessage.sendError(player, CivSettings.localize.localizedString("var_config_marketItem_notEnoughCurrency",(getItemCost(cost)+" "+CivSettings.CURRENCY_NAME)));
						return;
					}
					
					resident.getTreasury().withdraw(getItemCost(cost));
					CivMessage.send(player, CivColor.Yellow+CivSettings.localize.localizedString("var_taxes_paid",getFeeToTown(cost),CivSettings.CURRENCY_NAME));
					paid = getItemCost(cost);
				} else {
					if (!resident.getTreasury().hasEnough(cost)) {
						CivMessage.sendError(player, CivSettings.localize.localizedString("var_config_marketItem_notEnoughCurrency",(cost+" "+CivSettings.CURRENCY_NAME)));
						return;
					}
					
					resident.getTreasury().withdraw(cost);
					paid = cost;
				}

				HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(ItemManager.createItemStack(item_id, 1));
				if (leftovers.size() > 0) {
					for (ItemStack stack : leftovers.values()) {
						player.getWorld().dropItem(player.getLocation(), stack);
					}
				}
				
				CivMessage.send(player, CivColor.LightGreen+CivSettings.localize.localizedString("var_stable_buySuccess",paid,CivSettings.CURRENCY_NAME));
			}
			
		}
		
		for (ConfigStableItem item : CivSettings.stableItems) {
			SignSelectionComponent comp = signSelectors.get(item.store_id);
			if (comp == null) {
				continue;
			}
			if (item.item_id == Material.AIR) {
				comp.addItem(new String[] {CivColor.LightGreen+item.name, CivSettings.localize.localizedString("stable_sign_buyFor"), ""+item.cost, CivSettings.localize.localizedString("Fee:")+this.nonMemberFeeComponent.getFeeString()}, new buyHorseAction(item.horse_id, item.cost));
			} else {
				comp.addItem(new String[] {CivColor.LightGreen+item.name, CivSettings.localize.localizedString("stable_sign_buyFor"), ""+item.cost, CivSettings.localize.localizedString("Fee:")+this.nonMemberFeeComponent.getFeeString()}, new buyItemAction(item.item_id, item.cost));			
			}
		}
	}
	
	private double getItemCost(double cost) {
		return cost + getFeeToTown(cost);
	}
	
	private double getFeeToTown(double cost) {
		return cost*this.nonMemberFeeComponent.getFeeRate();
	}
	
	@Override
	public void processSignAction(Player player, StructureSign sign, PlayerInteractEvent event) {
		SignSelectionComponent signSelection = signSelectors.get(Integer.valueOf(sign.getAction()));
		if (signSelection == null) {
			CivLog.warning("No sign seletor component for with id:"+sign.getAction());
			return;
		}

		switch (sign.getType()) {
		case "prev":
			signSelection.processPrev();
			break;
		case "next":
			signSelection.processNext();
			break;
		case "item":
			signSelection.processAction(player);
			break;
		}
	}
	
	@Override
	public void updateSignText() {
		for (SignSelectionComponent comp : signSelectors.values()) {
			comp.setMessageAllItems(3, CivSettings.localize.localizedString("Fee:")+" "+this.nonMemberFeeComponent.getFeeString());
		}
	}
	
	@Override
	public void onPostBuild(BlockCoord absCoord, SimpleBlock sb) {
		StructureSign structSign;
		int selectorIndex;
		SignSelectionComponent signComp;
		
		switch (sb.command) {
		case "/prev":
			ItemManager.setType(absCoord.getBlock(), sb.getType());
			ItemManager.setData(absCoord.getBlock(), sb.getData());
			structSign = new StructureSign(absCoord, this);
			structSign.setText("\n"+ChatColor.BOLD+ChatColor.UNDERLINE+CivSettings.localize.localizedString("stable_sign_previousUnit"));
			structSign.setDirection((Directional)sb.getData());
			structSign.setAction(sb.keyvalues.get("id"));
			structSign.setType("prev");
			structSign.update();
			this.addStructureSign(structSign);
			CivGlobal.addStructureSign(structSign);			
			break;
		case "/item":
			ItemManager.setType(absCoord.getBlock(), sb.getType());
			ItemManager.setData(absCoord.getBlock(), sb.getData());

			structSign = new StructureSign(absCoord, this);
			structSign.setText("");
			structSign.setDirection((Directional)sb.getData());
			structSign.setAction(sb.keyvalues.get("id"));
			structSign.setType("item");
			structSign.update();
						
			this.addStructureSign(structSign);
			CivGlobal.addStructureSign(structSign);
						
			selectorIndex = Integer.valueOf(sb.keyvalues.get("id"));
			signComp = signSelectors.get(selectorIndex);
			if (signComp != null) {
				signComp.setActionSignCoord(absCoord);
				signComp.updateActionSign();
			} else {
				CivLog.warning("No sign selector found for id:"+selectorIndex);
			}
			
			break;
		case "/next":
			ItemManager.setType(absCoord.getBlock(), sb.getType());
			ItemManager.setData(absCoord.getBlock(), sb.getData());

			structSign = new StructureSign(absCoord, this);
			structSign.setText("\n"+ChatColor.BOLD+ChatColor.UNDERLINE+CivSettings.localize.localizedString("stable_sign_nextUnit"));
			structSign.setDirection((Directional)sb.getData());
			structSign.setType("next");
			structSign.setAction(sb.keyvalues.get("id"));
			structSign.update();
			this.addStructureSign(structSign);
			CivGlobal.addStructureSign(structSign);
			
			break;
		case "/horsespawn":
			this.horseSpawnCoord = absCoord;
			break;
		case "/mulespawn":
			this.muleSpawnCoord = absCoord;
			break;
		}
	}

	public void setNonResidentFee(double d) {
		this.nonMemberFeeComponent.setFeeRate(d);
	}	

}
