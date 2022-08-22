package ru.tailsxcraft.civcraft.loregui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import gpl.AttributeUtil;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.loreenhancements.LoreEnhancement;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.lorestorage.LoreMaterial;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.structure.Library;
import ru.tailsxcraft.civcraft.structure.Structure;
import ru.tailsxcraft.civcraft.util.CivColor;

public class EnchantItems implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
	    if ( event.getView().getTitle().equalsIgnoreCase("Библиотека")) {
	    	String enchant = LoreGuiItem.getActionData(stack, "enchantName");
	    	String customEnchant = LoreGuiItem.getActionData(stack, "customEnchantName");
	    	LoreEnhancement enchanLore = null;
	    	Enchantment enchans = null;
	    	if ( enchant != null ) {
	    		enchans = Library.getEnchantFromString(enchant);
	    	} else {
	    		enchanLore = LoreEnhancement.enhancements.get(customEnchant);
	    	}
	    	Integer level = null;
	    	if ( LoreGuiItem.getActionData(stack, "enchantLevel") != null ) {
	    		level = Integer.parseInt(LoreGuiItem.getActionData(stack, "enchantLevel"));
	    	}
	    	Town town = CivGlobal.getTownFromId(Integer.parseInt(LoreGuiItem.getActionData(stack, "townId")));
	    	Library library = null;
	    	Collection<Structure> structs = town.getStructures();
	    	for ( Structure struct : structs ) {
	    		if ( struct instanceof Library ) {
	    			library = (Library)struct;
	    		}
	    	}
	    	HashMap<ItemStack, Integer> itemPositions = new HashMap<ItemStack, Integer>();
	    	Inventory inv = event.getInventory();
	    	List<ItemStack> itemsOld = new ArrayList<ItemStack>();
	    	for ( int i = 9; i < 17; i++ ) {
	    		ItemStack item = inv.getItem(i);
	    		if ( item == null || item.getType() == Material.AIR) continue;
	    		if ( enchanLore != null ) {
	    			AttributeUtil atrs = new AttributeUtil(item);
	    			atrs.setCivCraftProperty("itemCode", item.getItemMeta().getDisplayName()+i);
	    			ItemStack newItem = atrs.getStack();
	    			itemPositions.put(newItem, i);
	    			itemsOld.add(newItem);
	    			continue;
	    		}
	    		itemsOld.add(item);
	    	}
	    	if (itemsOld.size() == 0) {
				CivMessage.send(player, CivColor.Rose+"В инвентаре библиотеки пусто, пожалуйста положите туда предметы.");
				return;
			}
	    	double enchanPrice = Double.parseDouble(LoreGuiItem.getActionData(stack, "enchantPrice"));
	    	List<ItemStack> itemsNew = new ArrayList<ItemStack>();
	    	for ( ItemStack item : itemsOld ) {
	    		if (enchans != null) {
	    			
	    			if(!enchans.canEnchantItem(item)) {
	    				continue;
	    			}
	    			
	    			if (item.containsEnchantment(enchans) && item.getEnchantmentLevel(enchans) >= level) {
	    				continue;
	    			}
	    			
	    			itemsNew.add(item);
	    		} else if ( enchanLore != null ){
	    			if (!enchanLore.canEnchantItem(item)) {
	    				continue;
	    			}
	    			
	    			if (enchanLore.hasEnchantment(item)) {
	    				continue;
	    			}
	    			
	    			itemsNew.add(item);
	    		}
	    	}
	    	int payToTown = (int) Math.round(enchanPrice*library.getNonResidentFee());
			Resident resident;
					
			resident = CivGlobal.getResident(player.getName());
			Town t = resident.getTown();	
			if (t == town) {
					// Pay no taxes! You're a member.
					payToTown = 0;
			}					
					
			// Determine if resident can pay.
			if (!resident.getTreasury().hasEnough(itemsNew.size()*(enchanPrice+payToTown))) {
				CivMessage.send(player, CivColor.Rose+CivSettings.localize.localizedString("var_library_enchant_cannotAfford",itemsNew.size()*(enchanPrice+payToTown),CivSettings.CURRENCY_NAME));
				return;
			}
					
			// Take money, give to server, TEH SERVER HUNGERS ohmnom nom
			resident.getTreasury().withdraw(itemsNew.size()*enchanPrice);
			
			// Send money to town for non-resident fee
			if (payToTown != 0) {
				town.depositDirect(itemsNew.size()*payToTown);
				CivMessage.send(player,CivColor.Yellow+" "+CivSettings.localize.localizedString("var_taxes_paid",itemsNew.size()*payToTown,CivSettings.CURRENCY_NAME));
			}
	    	for ( ItemStack item : itemsNew ) {    		
	    		if ( item == null || item.getType() == Material.AIR) continue;
	    		
	    		if (enchans == null) {
	    			Integer itemPosition = itemPositions.get(item);
	    			item = LoreMaterial.addEnhancement(item, enchanLore);
	    			AttributeUtil atrs = new AttributeUtil(item);
	    			atrs.removeCivCraftProperty("itemCode");
	    			inv.setItem(itemPosition, atrs.getStack());
	    		} else {
	    			item.addUnsafeEnchantment(enchans, Integer.parseInt(LoreGuiItem.getActionData(stack, "enchantLevel")));
	    		} 
	    	}
	    }
		/*
		String townName = LoreGuiItem.getActionData(stack, "cityName");
		if ( townName != null ) {
			Town town = CivGlobal.getTown(townName);
			
			BlockCoord revive = town.getTownHall().getRandomRevivePoint();
			Location loc;
			if (revive == null) {
				loc = player.getBedSpawnLocation();
			} else {
				loc = revive.getLocation();
			}
			
			CivMessage.send(player, CivColor.LightGreen+CivSettings.localize.localizedString("capitol_respawningAlert"));
			player.teleport(loc);	
		} else {
			String campId = LoreGuiItem.getActionData(stack, "campId");
			WarCamp camp = CivGlobal.getResident(player).getCiv().getWarCamps().get(Integer.parseInt(campId));
			
			BlockCoord revive = camp.getRandomRevivePoint();
			Location loc;
			if (revive == null) {
				loc = player.getBedSpawnLocation();
			} else {
				loc = revive.getLocation();
			}
			
			CivMessage.send(player, CivColor.LightGreen+CivSettings.localize.localizedString("capitol_respawningAlert"));
			player.teleport(loc);
		}*/
	}



}
