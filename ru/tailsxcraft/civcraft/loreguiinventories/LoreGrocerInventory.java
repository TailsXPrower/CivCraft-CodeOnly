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
package ru.tailsxcraft.civcraft.loreguiinventories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigBuildableInfo;
import ru.tailsxcraft.civcraft.config.ConfigGrocerLevel;
import ru.tailsxcraft.civcraft.config.ConfigMaterial;
import ru.tailsxcraft.civcraft.config.ConfigTech;
import ru.tailsxcraft.civcraft.config.ConfigTechPotion;
import ru.tailsxcraft.civcraft.config.ConfigTownUpgrade;
import ru.tailsxcraft.civcraft.config.ConfigUnit;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiBuildInventory;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItemListener;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.object.Civilization;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.structure.Grocer;
import ru.tailsxcraft.civcraft.util.CivColor;

public class LoreGrocerInventory {
	
	public static void openInventory(Player player, Grocer grocer) {
		LoreGuiBuildInventory guiInv = new LoreGuiBuildInventory(player, "Бакалея", 9, 9, new String[]{"Бакалея", "Бакалея", "Бакалея", "Бакалея"}, new String[]{"fishPage", "chickenPage", "porkPage", "carrotPage"});
		Inventory grocerInv = guiInv.inv;
	    int count = 0;
	    for ( ConfigGrocerLevel level : CivSettings.grocerLevels.values()) {
	    	String name = "";
	    	String lore = "";
	    	if ( grocer.getLevel() < level.level ) {
	    		name = CivColor.LightGreen+level.itemName;
	    		lore = ChatColor.RED+"Недоступно";
	    	} else if ( grocer.getLevel() >= level.level ) {
	    		name = CivColor.LightGreen+level.itemName;
	    		lore = CivColor.Gold+"Нажмите, чтобы купить";
	    	}
	    	
	    	ItemStack item = LoreGuiItem.build(name, level.itemId, lore);
	    	if ( grocer.getLevel() >= level.level ) {
	    		item = LoreGuiItem.setAction(item, "OpenInventory");
	    		item = LoreGuiItem.setActionData(item, "invType", "showBuildInvPage");
	    		if (level.level == 1) {
	    			item = LoreGuiItem.setActionData(item, "pageName", "fishPage");
	    		} else if (level.level == 2) {
	    			item = LoreGuiItem.setActionData(item, "pageName", "chickenPage");
	    		} else if (level.level == 3) {
	    			item = LoreGuiItem.setActionData(item, "pageName", "porkPage");
	    		} else if (level.level == 4) {
	    			item = LoreGuiItem.setActionData(item, "pageName", "carrotPage");
	    		}
	    	}
	    	
	    	grocerInv.setItem(count, item);
	    	count++;
	    }
	    
	    Inventory fishPage = guiInv.getPage("fishPage");
	    Inventory chickenPage = guiInv.getPage("chickenPage");
	    Inventory porkPage = guiInv.getPage("porkPage");
	    Inventory carrotPage = guiInv.getPage("carrotPage");
	    
	    class Grocer {
	    	public void InitializeFish(){
	    		ConfigGrocerLevel level = CivSettings.grocerLevels.get(1);
	    		String name = CivColor.LightGreen+level.itemName;
	    		for ( int i = 0; i < 5; i++ ) {
	    			int amount = 1;
	    			if ( i == 0 ) {
	    				amount = 1;
	    			} else if ( i == 1 ) {
	    				amount = 8;
	    			} else if ( i == 2 ) {
	    				amount = 16;
	    			} else if ( i == 3 ) {
	    				amount = 32;
	    			} else if ( i == 4 ) {
	    				amount = 64;
	    			} else {
	    				amount = 1;
	    			}
	    			String lore = CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(level.price*amount);
	    			ItemStack item = LoreGuiItem.build(name, level.itemId, amount, lore);
	    			item = LoreGuiItem.setAction(item, "BuyItems");
			    	item = LoreGuiItem.setActionData(item, "townId", ""+grocer.getTown().getId());
			    	item = LoreGuiItem.setActionData(item, "itemName", level.itemName);
			    	item = LoreGuiItem.setActionData(item, "itemId", ""+level.itemId);
			    	item = LoreGuiItem.setActionData(item, "itemPrice", ""+level.price);
			    	item = LoreGuiItem.setActionData(item, "itemAmount", ""+amount);
	    			fishPage.setItem(i, item);
	    		}
	    	}
	    	
            public void InitializeChicken(){
            	ConfigGrocerLevel level = CivSettings.grocerLevels.get(2);
            	String name = CivColor.LightGreen+level.itemName;
	    		for ( int i = 0; i < 5; i++ ) {
	    			int amount = 1;
	    			if ( i == 0 ) {
	    				amount = 1;
	    			} else if ( i == 1 ) {
	    				amount = 8;
	    			} else if ( i == 2 ) {
	    				amount = 16;
	    			} else if ( i == 3 ) {
	    				amount = 32;
	    			} else if ( i == 4 ) {
	    				amount = 64;
	    			} else {
	    				amount = 1;
	    			}
	    			String lore = CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(level.price*amount);
	    			ItemStack item = LoreGuiItem.build(name, level.itemId, amount, lore);
	    			item = LoreGuiItem.setAction(item, "BuyItems");
			    	item = LoreGuiItem.setActionData(item, "townId", ""+grocer.getTown().getId());
			    	item = LoreGuiItem.setActionData(item, "itemName", level.itemName);
			    	item = LoreGuiItem.setActionData(item, "itemId", ""+level.itemId);
			    	item = LoreGuiItem.setActionData(item, "itemPrice", ""+level.price);
			    	item = LoreGuiItem.setActionData(item, "itemAmount", ""+amount);
			    	chickenPage.setItem(i, item);
	    		}
	    	}
            
            public void InitializePork(){
            	ConfigGrocerLevel level = CivSettings.grocerLevels.get(3);
            	String name = CivColor.LightGreen+level.itemName;
	    		for ( int i = 0; i < 5; i++ ) {
	    			int amount = 1;
	    			if ( i == 0 ) {
	    				amount = 1;
	    			} else if ( i == 1 ) {
	    				amount = 8;
	    			} else if ( i == 2 ) {
	    				amount = 16;
	    			} else if ( i == 3 ) {
	    				amount = 32;
	    			} else if ( i == 4 ) {
	    				amount = 64;
	    			} else {
	    				amount = 1;
	    			}
	    			String lore = CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(level.price*amount);
	    			ItemStack item = LoreGuiItem.build(name, level.itemId, amount, lore);
	    			item = LoreGuiItem.setAction(item, "BuyItems");
			    	item = LoreGuiItem.setActionData(item, "townId", ""+grocer.getTown().getId());
			    	item = LoreGuiItem.setActionData(item, "itemName", level.itemName);
			    	item = LoreGuiItem.setActionData(item, "itemId", ""+level.itemId);
			    	item = LoreGuiItem.setActionData(item, "itemPrice", ""+level.price);
			    	item = LoreGuiItem.setActionData(item, "itemAmount", ""+amount);
			    	porkPage.setItem(i, item);
	    		}
	    	}

            public void InitializeCarrot(){
            	ConfigGrocerLevel level = CivSettings.grocerLevels.get(4);
            	String name = CivColor.LightGreen+level.itemName;
	    		for ( int i = 0; i < 5; i++ ) {
	    			int amount = 1;
	    			if ( i == 0 ) {
	    				amount = 1;
	    			} else if ( i == 1 ) {
	    				amount = 8;
	    			} else if ( i == 2 ) {
	    				amount = 16;
	    			} else if ( i == 3 ) {
	    				amount = 32;
	    			} else if ( i == 4 ) {
	    				amount = 64;
	    			} else {
	    				amount = 1;
	    			}
	    			String lore = CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(level.price*amount);
	    			ItemStack item = LoreGuiItem.build(name, level.itemId, amount, lore);
	    			item = LoreGuiItem.setAction(item, "BuyItems");
			    	item = LoreGuiItem.setActionData(item, "townId", ""+grocer.getTown().getId());
			    	item = LoreGuiItem.setActionData(item, "itemName", level.itemName);
			    	item = LoreGuiItem.setActionData(item, "itemId", ""+level.itemId);
			    	item = LoreGuiItem.setActionData(item, "itemPrice", ""+level.price);
			    	item = LoreGuiItem.setActionData(item, "itemAmount", ""+amount);
			    	carrotPage.setItem(i, item);
	    		}
	    	}
	    }
	    
	    Grocer grocery = new Grocer();
	    grocery.InitializeFish();
	    grocery.InitializeChicken();
	    grocery.InitializePork();
	    grocery.InitializeCarrot();
	    
	    ItemStack itemInfo = LoreGuiItem.build(CivColor.LightGreen+"Бакалея", Material.PAPER, CivColor.Gold+"Уровень: "+CivColor.White+grocer.getLevel(),grocer.getNonResidentFeeString());
	    grocerInv.setItem(8, itemInfo);
	    player.openInventory(grocerInv);
	    LoreGuiItemListener.guiInventories.put("Бакалея", grocerInv);
	}
	
}
