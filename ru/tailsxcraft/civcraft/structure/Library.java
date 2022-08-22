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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import ru.tailsxcraft.civcraft.camp.WarCamp;
import ru.tailsxcraft.civcraft.components.AttributeBiome;
import ru.tailsxcraft.civcraft.components.NonMemberFeeComponent;
import ru.tailsxcraft.civcraft.components.ProjectileArrowComponent;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiInventory;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItemListener;
import ru.tailsxcraft.civcraft.lorestorage.LoreMaterial;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.LibraryEnchantment;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.StructureSign;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.civcraft.util.ItemManager;
import ru.tailsxcraft.civcraft.util.SimpleBlock;

public class Library extends Structure {

	private int level = 1;
	public AttributeBiome cultureBeakers;
	
	ArrayList<LibraryEnchantment> enchantments = new ArrayList<LibraryEnchantment>();

	private NonMemberFeeComponent nonMemberFeeComponent;
	
	public static Enchantment getEnchantFromString(String name) {
		
		// Armor Enchantments
		if (name.equalsIgnoreCase("protection")) {
			return Enchantment.PROTECTION_ENVIRONMENTAL;
		}
		if (name.equalsIgnoreCase("fire_protection")) {
			return Enchantment.PROTECTION_FIRE;
		}
		if (name.equalsIgnoreCase("feather_falling")) {
			return Enchantment.PROTECTION_FALL;
		}
		if (name.equalsIgnoreCase("blast_protection")) {
			return Enchantment.PROTECTION_EXPLOSIONS;
		}
		if (name.equalsIgnoreCase("projectile_protection")) {
			return Enchantment.PROTECTION_PROJECTILE;
		}
		if (name.equalsIgnoreCase("respiration")) {
			return Enchantment.OXYGEN;
		}
		if (name.equalsIgnoreCase("aqua_affinity")) {
			return Enchantment.WATER_WORKER;
		}
		
		// Sword Enchantments
		if (name.equalsIgnoreCase("sharpness")) {
			return Enchantment.DAMAGE_ALL;
		}
		if (name.equalsIgnoreCase("smite")) {
			return Enchantment.DAMAGE_UNDEAD;
		}
		if (name.equalsIgnoreCase("bane_of_arthropods")) {
			return Enchantment.DAMAGE_ARTHROPODS;
		}
		if (name.equalsIgnoreCase("knockback")) {
			return Enchantment.KNOCKBACK;
		}
		if (name.equalsIgnoreCase("fire_aspect")) {
			return Enchantment.FIRE_ASPECT;
		}
		if (name.equalsIgnoreCase("looting")) {
			return Enchantment.LOOT_BONUS_MOBS;
		}
		
		// Tool Enchantments
		if (name.equalsIgnoreCase("efficiency")) {
			return Enchantment.DIG_SPEED;
		}
		if (name.equalsIgnoreCase("silk_touch")) {
			return Enchantment.SILK_TOUCH;
		}
		if (name.equalsIgnoreCase("unbreaking")) {
			return Enchantment.DURABILITY;
		}
		if (name.equalsIgnoreCase("fortune")) {
			return Enchantment.LOOT_BONUS_BLOCKS;
		}
		
		// Bow Enchantments
		if (name.equalsIgnoreCase("power")) {
			return Enchantment.ARROW_DAMAGE;
		}
		if (name.equalsIgnoreCase("punch")) {
			return Enchantment.ARROW_KNOCKBACK;
		}
		if (name.equalsIgnoreCase("flame")) {
			return Enchantment.ARROW_FIRE;
		}
		if (name.equalsIgnoreCase("infinity")) {
			return Enchantment.ARROW_INFINITE;
		}
		
		return null;
		
	}

	public double getNonResidentFee() {
		return this.nonMemberFeeComponent.getFeeRate();
	}

	public void setNonResidentFee(double nonResidentFee) {
		this.nonMemberFeeComponent.setFeeRate(nonResidentFee);
	}
	
	private String getNonResidentFeeString() {
		return CivColor.Gold+"Пошлина: "+CivColor.White+((int)(getNonResidentFee()*100) + "%").toString();		
	}
	
	public String getNonResidentFeePublic() {
		return getNonResidentFeeString();		
	}
	
	protected Library(Location center, String id, Town town)
			throws CivException {
		super(center, id, town);
		nonMemberFeeComponent = new NonMemberFeeComponent(this);
		nonMemberFeeComponent.onSave();
		setLevel(town.saved_library_level);
	}

	public Library(ResultSet rs) throws SQLException, CivException {
		super(rs);
		nonMemberFeeComponent = new NonMemberFeeComponent(this);
		nonMemberFeeComponent.onLoad();
	}
	
	@Override
	public void loadSettings() {
		super.loadSettings();	
	}

	public int getLevel() {
		return level;
	}


	public void setLevel(int level) {
		this.level = level;
	}
	
	/*
	private StructureSign getSignFromSpecialId(int special_id) {
		for (StructureSign sign : getSigns()) {
			int id = Integer.valueOf(sign.getAction());
			if (id == special_id) {
				return sign;
			}
		}
		return null;
	}
	
	@Override
	public void updateSignText() {

		int count = 0;
		
		for (LibraryEnchantment enchant : this.enchantments) {
			StructureSign sign = getSignFromSpecialId(count);
			if (sign == null) {
				CivLog.error("sign from special id was null, id:"+count);
				return;
			}
			sign.setText(enchant.displayName+"\n"+
					"Level "+enchant.level+"\n"+
					getNonResidentFeeString()+"\n"+
					"For "+enchant.price);
			sign.update();
			count++;
		}
	
		for (; count < getSigns().size(); count++) {
			StructureSign sign = getSignFromSpecialId(count);
			sign.setText("Library Slot\nEmpty");
			sign.update();
		}
	}*/
	
	public void validateEnchantment(ItemStack item, LibraryEnchantment ench) throws CivException {
		if (ench.enchant != null) {
			
			if(!ench.enchant.canEnchantItem(item)) {
				throw new CivException(CivSettings.localize.localizedString("library_enchant_cannotEnchant"));
			}
			
			if (item.containsEnchantment(ench.enchant) && item.getEnchantmentLevel(ench.enchant) >= ench.level) {
				throw new CivException(CivSettings.localize.localizedString("library_enchant_hasEnchant"));
			}
			
			
		} else {
			if (!ench.enhancement.canEnchantItem(item)) {
				throw new CivException(CivSettings.localize.localizedString("library_enchant_cannotEnchant"));
			}
			
			if (ench.enhancement.hasEnchantment(item)) {
				throw new CivException(CivSettings.localize.localizedString("library_enchant_hasEnchantment"));
			}
		}
	}
	
	public ItemStack addEnchantment(ItemStack item, LibraryEnchantment ench) {
		if (ench.enchant != null) {
			item.addUnsafeEnchantment(ench.enchant, ench.level);
		} else {
			item = LoreMaterial.addEnhancement(item, ench.enhancement);
		}
		return item;
	}
	
	public void add_enchantment_to_tool(Player player, StructureSign sign, PlayerInteractEvent event) throws CivException {
		int special_id = Integer.valueOf(sign.getAction());

		if (!event.hasItem()) {
			CivMessage.send(player, CivColor.Rose+CivSettings.localize.localizedString("library_enchant_itemNotInHand"));
			return;
		}
		ItemStack item = event.getItem();
		
		if (special_id >= this.enchantments.size()) {
			throw new CivException(CivSettings.localize.localizedString("library_enchant_notReady"));
		}
		
		
		LibraryEnchantment ench = this.enchantments.get(special_id);
		this.validateEnchantment(item, ench);
		
		int payToTown = (int) Math.round(ench.price*getNonResidentFee());
		Resident resident;
				
		resident = CivGlobal.getResident(player.getName());
		Town t = resident.getTown();	
		if (t == this.getTown()) {
				// Pay no taxes! You're a member.
				payToTown = 0;
		}					
				
		// Determine if resident can pay.
		if (!resident.getTreasury().hasEnough(ench.price+payToTown)) {
			CivMessage.send(player, CivColor.Rose+CivSettings.localize.localizedString("var_library_enchant_cannotAfford",ench.price+payToTown,CivSettings.CURRENCY_NAME));
			return;
		}
				
		// Take money, give to server, TEH SERVER HUNGERS ohmnom nom
		resident.getTreasury().withdraw(ench.price);
		
		// Send money to town for non-resident fee
		if (payToTown != 0) {
			getTown().depositDirect(payToTown);
			CivMessage.send(player,CivColor.Yellow+" "+CivSettings.localize.localizedString("var_taxes_paid",payToTown,CivSettings.CURRENCY_NAME));
		}
				
		// Successful payment, process enchantment.
		ItemStack newStack = this.addEnchantment(item, ench);
		player.getInventory().setItemInMainHand(newStack);
		CivMessage.send(player, CivColor.LightGreen+CivSettings.localize.localizedString("var_library_enchantment_added",ench.displayName));
	}

	@Override
	public void processSignAction(Player player, StructureSign sign, PlayerInteractEvent event) {
        Resident resident = CivGlobal.getResident(player);
		
		if (resident == null) {
			return;
		}
		
		switch (sign.getAction()) {
		case "enchants":
			
			Inventory libraryInv = LoreGuiInventory.createGuiInventory(player, 18, "Библиотека");
			LoreGuiInventory.setInventoryData("Библиотека", "activeLevel", "1");
			
			ItemStack libraryInf = LoreGuiItem.build(CivColor.LightGreen+"Информация о Библиотеке", Material.PAPER, new String[]{CivColor.Gold+"Уровень: "+CivColor.White+LoreGuiInventory.getInventoryData("Библиотека", "activeLevel"),getNonResidentFeeString(), "", CivColor.LightGreen+"Нажмите, чтобы переключить уровень"});
			libraryInf = LoreGuiItem.setAction(libraryInf, "ChangeLibraryLevel");
			libraryInf = LoreGuiItem.setActionData(libraryInf, "townId", ""+this.getTown().getId());
			libraryInv.setItem(8, libraryInf);
			
			Integer count = 0;
			
			if ( this.enchantments != null || !this.enchantments.isEmpty() ) {
				for (LibraryEnchantment mat : this.enchantments) {
					ItemStack item = null;
					if (mat.name.equalsIgnoreCase("looting") && mat.level == 1 || mat.name.equalsIgnoreCase("efficiency") && mat.level == 1 ) {
						if ( mat.level != 0 ) {
							item = LoreGuiItem.build(CivColor.Yellow+mat.displayName+" "+mat.level, Material.ENCHANTED_BOOK, CivColor.Gold+"Стоимость: "+CivColor.White+mat.price);
						} else {
							item = LoreGuiItem.build(CivColor.Yellow+mat.displayName, Material.ENCHANTED_BOOK, CivColor.Gold+"Стоимость: "+CivColor.White+mat.price);
						}
						item = LoreGuiItem.setAction(item, "EnchantItems");
						item = LoreGuiItem.setActionData(item, "townId", ""+this.getTown().getId());
						if (mat.enchant != null) {
							item = LoreGuiItem.setActionData(item, "enchantName", ""+mat.name);
							item = LoreGuiItem.setActionData(item, "enchantLevel", ""+mat.level);
						} else {
							item = LoreGuiItem.setActionData(item, "customEnchantName", ""+mat.enhancement.getName());
						}
						item = LoreGuiItem.setActionData(item, "enchantPrice", ""+mat.price);
						libraryInv.setItem(count, item);
						count++;
					}
				}
				for ( int i = 0; i < 8; i++ ) {
					if ( libraryInv.getItem(i) != null ) continue;
					
					ItemStack item = LoreGuiItem.build(CivColor.Red+"Пусто", Material.RED_STAINED_GLASS_PANE);
					
					libraryInv.setItem(i, item);
				}
			} else {
				for ( int i = 0; i < 8; i++ ) {
					ItemStack item = LoreGuiItem.build(CivColor.Red+"Пусто", Material.RED_STAINED_GLASS_PANE);
					
					libraryInv.setItem(i, item);
				}
			}
			LoreGuiInventory.updateGuiInventory("Библиотека", libraryInv);
			player.openInventory(libraryInv);
			break;
		}
	}
	
	@Override
	public String getDynmapDescription() {
		String out = "<u><b>"+this.getDisplayName()+"</u></b><br/>";
		
		if (this.enchantments.size() == 0) {
			out += CivSettings.localize.localizedString("library_dynmap_nothingStocked");
		} 
		else {
			for (LibraryEnchantment mat : this.enchantments) {
				if ( mat.name.equalsIgnoreCase("Silk_Touch") || mat.name.equalsIgnoreCase("LoreEnhancementSoulBound") || mat.name.equalsIgnoreCase("Feather_Falling") ) {
					out += CivSettings.localize.localizedString("var_library_dynmap_item",mat.displayName,mat.price)+"<br/>";
				} else {
					out += CivSettings.localize.localizedString("var_library_dynmap_item",mat.displayName+" "+mat.level,mat.price)+"<br/>";
				}
			}
		}
		return out;
	}
	
	
	public ArrayList<LibraryEnchantment> getEnchants() {
		return enchantments;
	}


	public void addEnchant(LibraryEnchantment enchant) throws CivException {
		//if (enchantments.size() >= 8) {
		//	throw new CivException(CivSettings.localize.localizedString("library_full"));
		//}
		enchantments.add(enchant);
	}
	
	@Override
	public String getMarkerIconName() {
		return "bookshelf";
	}

	public void reset() {
		this.enchantments.clear();
		this.updateSignText();
	}
	
	@Override
	public void onPostBuild(BlockCoord absCoord, SimpleBlock commandBlock) {
		StructureSign structSign;
		if (commandBlock.command.equals("/library")) {
			ItemManager.setType(absCoord.getBlock(), commandBlock.getType());
			ItemManager.setData(absCoord.getBlock(), commandBlock.getData());
			structSign = new StructureSign(absCoord, this);
			structSign.setText(CivColor.Green+"Библиотека"+"\n"+CivColor.Black+"Нажмите, чтобы"+"\n"+"зачаровать");
			if ( commandBlock.getType() == Material.OAK_SIGN ) {
				structSign.setRotation((Rotatable)commandBlock.getData());
			} else {
				structSign.setDirection((Directional)commandBlock.getData());
			}
			structSign.setAction("enchants");
			structSign.update();
			this.addStructureSign(structSign);
			CivGlobal.addStructureSign(structSign);
		} 
	}
}
