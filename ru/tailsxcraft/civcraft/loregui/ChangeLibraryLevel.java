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
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiInventory;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.lorestorage.LoreMaterial;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.LibraryEnchantment;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.structure.Library;
import ru.tailsxcraft.civcraft.structure.Structure;
import ru.tailsxcraft.civcraft.util.CivColor;

public class ChangeLibraryLevel implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
	    if ( event.getView().getTitle().equalsIgnoreCase("Библиотека")) {
	    	Town town = CivGlobal.getTownFromId(Integer.parseInt(LoreGuiItem.getActionData(stack, "townId")));
	    	Library library = null;
	    	Collection<Structure> structs = town.getStructures();
	    	for ( Structure struct : structs ) {
	    		if ( struct instanceof Library ) {
	    			library = (Library)struct;
	    		}
	    	}
	    	Inventory libraryInv = event.getInventory();
	    	
	    	Integer count = 0;
	    	
	    	for ( int i = 0; i < 9; i++ ) {
				libraryInv.clear(i);
			}
	    	
	    	LoreGuiInventory.setInventoryData("Библиотека", "activeLevel", ""+(Integer.parseInt(LoreGuiInventory.getInventoryData("Библиотека", "activeLevel"))+1));
	    	
	    	ItemStack libraryInf = LoreGuiItem.build(CivColor.LightGreen+"Информация о Библиотеке", Material.PAPER, new String[]{CivColor.Gold+"Уровень: "+CivColor.White+LoreGuiInventory.getInventoryData("Библиотека", "activeLevel"),library.getNonResidentFeePublic(), "", CivColor.LightGreen+"Нажмите, чтобы переключить уровень"});
			libraryInf = LoreGuiItem.setAction(libraryInf, "ChangeLibraryLevel");
			libraryInf = LoreGuiItem.setActionData(libraryInf, "townId", ""+town.getId());
			libraryInv.setItem(8, libraryInf);
	    	
	    	if ( library.getEnchants() != null || !library.getEnchants().isEmpty() ) {
	    		if(Integer.parseInt(LoreGuiInventory.getInventoryData("Библиотека", "activeLevel")) > library.getLevel()) {
    				LoreGuiInventory.setInventoryData("Библиотека", "activeLevel", "1");
    			}
	    		
	    		switch(Integer.parseInt(LoreGuiInventory.getInventoryData("Библиотека", "activeLevel"))) {
	    		case 1:
	    			count = 0;
	    			for (LibraryEnchantment mat : library.getEnchants()) {
						ItemStack item = null;
						if (mat.name.equalsIgnoreCase("looting") && mat.level == 1 || mat.name.equalsIgnoreCase("efficiency") && mat.level == 1 ) {
							if ( mat.level != 0 ) {
								item = LoreGuiItem.build(CivColor.Yellow+mat.displayName+" "+mat.level, Material.ENCHANTED_BOOK, CivColor.Gold+"Стоимость: "+CivColor.White+mat.price);
							} else {
								item = LoreGuiItem.build(CivColor.Yellow+mat.displayName, Material.ENCHANTED_BOOK, CivColor.Gold+"Стоимость: "+CivColor.White+mat.price);
							}
							item = LoreGuiItem.setAction(item, "EnchantItems");
							item = LoreGuiItem.setActionData(item, "townId", ""+town.getId());
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
	    			break;
	    		case 2:
	    			count = 0;
	    			for (LibraryEnchantment mat : library.getEnchants()) {
						ItemStack item = null;
						if (mat.name.equalsIgnoreCase("looting") && mat.level == 2 || mat.name.equalsIgnoreCase("efficiency") && mat.level == 2 || mat.name.equalsIgnoreCase("fortune") && mat.level == 1 || mat.name.equalsIgnoreCase("Silk_Touch") || mat.name.equalsIgnoreCase("LoreEnhancementSoulBound") ) {
							if ( mat.name.equalsIgnoreCase("Silk_Touch") || mat.name.equalsIgnoreCase("LoreEnhancementSoulBound") ) {
								item = LoreGuiItem.build(CivColor.Yellow+mat.displayName, Material.ENCHANTED_BOOK, CivColor.Gold+"Стоимость: "+CivColor.White+mat.price);
							} else {
								item = LoreGuiItem.build(CivColor.Yellow+mat.displayName+" "+mat.level, Material.ENCHANTED_BOOK, CivColor.Gold+"Стоимость: "+CivColor.White+mat.price);
							}
							item = LoreGuiItem.setAction(item, "EnchantItems");
							item = LoreGuiItem.setActionData(item, "townId", ""+town.getId());
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
	    			break;
	    		case 3:
	    			count = 0;
	    			for (LibraryEnchantment mat : library.getEnchants()) {
						ItemStack item = null;
						if (mat.name.equalsIgnoreCase("efficiency") && mat.level == 3 || mat.name.equalsIgnoreCase("efficiency") && mat.level == 4 ) {
							if ( mat.level != 0 ) {
								item = LoreGuiItem.build(CivColor.Yellow+mat.displayName+" "+mat.level, Material.ENCHANTED_BOOK, CivColor.Gold+"Стоимость: "+CivColor.White+mat.price);
							} else {
								item = LoreGuiItem.build(CivColor.Yellow+mat.displayName, Material.ENCHANTED_BOOK, CivColor.Gold+"Стоимость: "+CivColor.White+mat.price);
							}
							item = LoreGuiItem.setAction(item, "EnchantItems");
							item = LoreGuiItem.setActionData(item, "townId", ""+town.getId());
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
	    			break;
	    		case 4:
	    			count = 0;
	    			for (LibraryEnchantment mat : library.getEnchants()) {
						ItemStack item = null;
						if ( mat.name.equalsIgnoreCase("efficiency") && mat.level == 5 || mat.name.equalsIgnoreCase("Feather_Falling") ) {
						    if ( mat.name.equalsIgnoreCase("Feather_Falling") ) {
								item = LoreGuiItem.build(CivColor.Yellow+mat.displayName, Material.ENCHANTED_BOOK, CivColor.Gold+"Стоимость: "+CivColor.White+mat.price);
							} else {
								item = LoreGuiItem.build(CivColor.Yellow+mat.displayName+" "+mat.level, Material.ENCHANTED_BOOK, CivColor.Gold+"Стоимость: "+CivColor.White+mat.price);
							}
							item = LoreGuiItem.setAction(item, "EnchantItems");
							item = LoreGuiItem.setActionData(item, "townId", ""+town.getId());
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
	    			LoreGuiInventory.setInventoryData("Библиотека", "activeLevel", "0");
	    			break;
	    		}
				/*
				for (LibraryEnchantment mat : this.enchantments) {
					ItemStack item = null;
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
				}*/
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
