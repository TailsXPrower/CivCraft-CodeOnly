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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigBuildableInfo;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiBuildInventory;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.util.CivColor;

public class LoreStructuresInventory {
	
	public static void openInventory(Player player, Town town) {
		/* Main Inventory */
		LoreGuiBuildInventory guiInv = new LoreGuiBuildInventory(player, "Категории построек", 27, 36, new String[]{ChatColor.DARK_RED+"Военные постройки", ChatColor.DARK_AQUA+"Технологические постройки", CivColor.Green+"Экономические постройки", ChatColor.LIGHT_PURPLE+"Культурные постройки", CivColor.Gold+"Чудеса света"});
		Inventory inv = guiInv.inv;
		/* First category */
		ItemStack categoryOne = LoreGuiItem.build(ChatColor.DARK_RED+"Военные постройки", Material.RED_CONCRETE);
		categoryOne = LoreGuiItem.setAction(categoryOne, "OpenInventory");
		categoryOne = LoreGuiItem.setActionData(categoryOne, "invType", "showBuildInvPage");
		categoryOne = LoreGuiItem.setActionData(categoryOne, "pageName", ChatColor.DARK_RED+"Военные постройки");
		inv.setItem(9, categoryOne);
		/* Second category */
		ItemStack categoryTwo = LoreGuiItem.build(ChatColor.DARK_AQUA+"Технологические постройки", Material.CYAN_CONCRETE);
		categoryTwo = LoreGuiItem.setAction(categoryTwo, "OpenInventory");
		categoryTwo = LoreGuiItem.setActionData(categoryTwo, "invType", "showBuildInvPage");
		categoryTwo = LoreGuiItem.setActionData(categoryTwo, "pageName", ChatColor.DARK_AQUA+"Технологические постройки");
		inv.setItem(11, categoryTwo);
		/* Third category */
		ItemStack categoryThree = LoreGuiItem.build(CivColor.Green+"Экономические постройки", Material.LIME_CONCRETE);
		categoryThree = LoreGuiItem.setAction(categoryThree, "OpenInventory");
		categoryThree = LoreGuiItem.setActionData(categoryThree, "invType", "showBuildInvPage");
		categoryThree = LoreGuiItem.setActionData(categoryThree, "pageName", CivColor.Green+"Экономические постройки");
		inv.setItem(13, categoryThree);
		/* Fourth category */
		ItemStack categoryFour = LoreGuiItem.build(ChatColor.LIGHT_PURPLE+"Культурные постройки", Material.MAGENTA_CONCRETE);
		categoryFour = LoreGuiItem.setAction(categoryFour, "OpenInventory");
		categoryFour = LoreGuiItem.setActionData(categoryFour, "invType", "showBuildInvPage");
		categoryFour = LoreGuiItem.setActionData(categoryFour, "pageName", ChatColor.LIGHT_PURPLE+"Культурные постройки");
		inv.setItem(15, categoryFour);
		/* Fifth category */
		ItemStack categoryFive = LoreGuiItem.build(CivColor.Gold+"Чудеса света", Material.YELLOW_CONCRETE);
		categoryFive = LoreGuiItem.setAction(categoryFive, "OpenInventory");
		categoryFive = LoreGuiItem.setActionData(categoryFive, "invType", "showBuildInvPage");
		categoryFive = LoreGuiItem.setActionData(categoryFive, "pageName", CivColor.Gold+"Чудеса света");
		inv.setItem(17, categoryFive);
		/* Pages */
		ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
		pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
		pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
		
		ItemStack pageItem = LoreGuiItem.build(ChatColor.RED+"Нет доступных построек", Material.RED_WOOL);
		/* Page 1 */
		Inventory page1 = guiInv.getPage(ChatColor.DARK_RED+"Военные постройки");
		
		int count = 0;
		for (ConfigBuildableInfo sinfo : CivSettings.structures.values()) {
			if ( sinfo.structureGroup.equalsIgnoreCase("military") ) {
				if ( count == 27 ) {
					count = 28;
    			}
				
				String require_structure = "";
				String require_tech = "";
				
				if ( sinfo.require_structure == null ) {
					require_structure = CivColor.Red+"Отсутствует";
				} else if (town.hasStructure(sinfo.require_structure)) {
					require_structure = CivColor.Green+CivSettings.structures.get(sinfo.require_structure).displayName;
				} else if (!town.hasStructure(sinfo.require_structure)) {
					require_structure = CivColor.Red+CivSettings.structures.get(sinfo.require_structure).displayName;
				}
				
				if ( sinfo.require_tech == null ) {
					require_tech = CivColor.Red+"Отсутствует";
				} else if (town.getCiv().hasTechnology(sinfo.require_tech)) {
					require_tech = CivColor.Green+CivSettings.techs.get(sinfo.require_tech).name;
				} else if (!town.getCiv().hasTechnology(sinfo.require_tech)) {
					require_tech = CivColor.Red+CivSettings.techs.get(sinfo.require_tech).name;
				}
				
    			ItemStack item = null;
    			if ( sinfo.structureItem == null || sinfo.structureItem == "" || sinfo.structureItem == "null") {
    				item = LoreGuiItem.build(ChatColor.DARK_RED+sinfo.displayName, Material.RED_CONCRETE, new String[]{CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(sinfo.cost), CivColor.Gold+"Молоточки: "+CivColor.White+Math.round(sinfo.hammer_cost), CivColor.LightGray+"_______________________________", CivColor.LightGray+"* "+CivColor.Gold+"Требуемая технология: "+require_tech, CivColor.LightGray+"* "+CivColor.Gold+"Требуемая структура: "+require_structure});
    			} else {
    				item = LoreGuiItem.build(ChatColor.DARK_RED+sinfo.displayName, Material.matchMaterial(sinfo.structureItem), new String[]{CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(sinfo.cost), CivColor.Gold+"Молоточки: "+CivColor.White+Math.round(sinfo.hammer_cost), CivColor.LightGray+"_______________________________", CivColor.LightGray+"* "+CivColor.Gold+"Требуемая технология: "+require_tech, CivColor.LightGray+"* "+CivColor.Gold+"Требуемая структура: "+require_structure});
    			}
    			item = LoreGuiItem.setAction(item, "Build");
    			item = LoreGuiItem.setActionData(item, "buildName", sinfo.displayName);
    			page1.setItem(count, item);
    			count++;
			}
		}

		page1.setItem(27, pageBack);
		/* Page 2 */
		Inventory page2 = guiInv.getPage(ChatColor.DARK_AQUA+"Технологические постройки");

		int twoCount = 0;
		for (ConfigBuildableInfo sinfo : CivSettings.structures.values()) {
			if ( sinfo.structureGroup.equalsIgnoreCase("industrial") ) {
				if ( twoCount == 27 ) {
					twoCount = 28;
    			}
				
				String require_structure = "";
				String require_tech = "";
				
				if ( sinfo.require_structure == null ) {
					require_structure = CivColor.Red+"Отсутствует";
				} else if (town.hasStructure(sinfo.require_structure)) {
					require_structure = CivColor.Green+CivSettings.structures.get(sinfo.require_structure).displayName;
				} else if (!town.hasStructure(sinfo.require_structure)) {
					require_structure = CivColor.Red+CivSettings.structures.get(sinfo.require_structure).displayName;
				}
				
				if ( sinfo.require_tech == null ) {
					require_tech = CivColor.Red+"Отсутствует";
				} else if (town.getCiv().hasTechnology(sinfo.require_tech)) {
					require_tech = CivColor.Green+CivSettings.techs.get(sinfo.require_tech).name;
				} else if (!town.getCiv().hasTechnology(sinfo.require_tech)) {
					require_tech = CivColor.Red+CivSettings.techs.get(sinfo.require_tech).name;
				}
				
    			ItemStack item = null;
    			if ( sinfo.structureItem == null || sinfo.structureItem == "" || sinfo.structureItem == "null") {
    				item = LoreGuiItem.build(ChatColor.DARK_AQUA+sinfo.displayName, Material.CYAN_CONCRETE, new String[]{CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(sinfo.cost), CivColor.Gold+"Молоточки: "+CivColor.White+Math.round(sinfo.hammer_cost), CivColor.LightGray+"_______________________________", CivColor.LightGray+"* "+CivColor.Gold+"Требуемая технология: "+require_tech, CivColor.LightGray+"* "+CivColor.Gold+"Требуемая структура: "+require_structure});
    			} else {
    				item = LoreGuiItem.build(ChatColor.DARK_AQUA+sinfo.displayName, Material.matchMaterial(sinfo.structureItem), new String[]{CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(sinfo.cost), CivColor.Gold+"Молоточки: "+CivColor.White+Math.round(sinfo.hammer_cost), CivColor.LightGray+"_______________________________", CivColor.LightGray+"* "+CivColor.Gold+"Требуемая технология: "+require_tech, CivColor.LightGray+"* "+CivColor.Gold+"Требуемая структура: "+require_structure});
    			}
    			item = LoreGuiItem.setAction(item, "Build");
    			item = LoreGuiItem.setActionData(item, "buildName", sinfo.displayName);
    			page2.setItem(twoCount, item);
    			twoCount++;
			}
		}
		
		page2.setItem(27, pageBack);
		/* Page 3 */
		Inventory page3 = guiInv.getPage(CivColor.Green+"Экономические постройки");

		int threeCount = 0;
		for (ConfigBuildableInfo sinfo : CivSettings.structures.values()) {
			if ( sinfo.structureGroup.equalsIgnoreCase("economy") ) {
				if ( threeCount == 27 ) {
					threeCount = 28;
    			}
				
				String require_structure = "";
				String require_tech = "";
				
				if ( sinfo.require_structure == null ) {
					require_structure = CivColor.Red+"Отсутствует";
				} else if (town.hasStructure(sinfo.require_structure)) {
					require_structure = CivColor.Green+CivSettings.structures.get(sinfo.require_structure).displayName;
				} else if (!town.hasStructure(sinfo.require_structure)) {
					require_structure = CivColor.Red+CivSettings.structures.get(sinfo.require_structure).displayName;
				}
				
				if ( sinfo.require_tech == null ) {
					require_tech = CivColor.Red+"Отсутствует";
				} else if (town.getCiv().hasTechnology(sinfo.require_tech)) {
					require_tech = CivColor.Green+CivSettings.techs.get(sinfo.require_tech).name;
				} else if (!town.getCiv().hasTechnology(sinfo.require_tech)) {
					require_tech = CivColor.Red+CivSettings.techs.get(sinfo.require_tech).name;
				}
				
    			ItemStack item = null;
    			if ( sinfo.structureItem == null || sinfo.structureItem == "" || sinfo.structureItem == "null") {
    				item = LoreGuiItem.build(CivColor.Green+sinfo.displayName, Material.LIME_CONCRETE, new String[]{CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(sinfo.cost), CivColor.Gold+"Молоточки: "+CivColor.White+Math.round(sinfo.hammer_cost), CivColor.LightGray+"_______________________________", CivColor.LightGray+"* "+CivColor.Gold+"Требуемая технология: "+require_tech, CivColor.LightGray+"* "+CivColor.Gold+"Требуемая структура: "+require_structure});
    			} else {
    				item = LoreGuiItem.build(CivColor.Green+sinfo.displayName, Material.matchMaterial(sinfo.structureItem), new String[]{CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(sinfo.cost), CivColor.Gold+"Молоточки: "+CivColor.White+Math.round(sinfo.hammer_cost), CivColor.LightGray+"_______________________________", CivColor.LightGray+"* "+CivColor.Gold+"Требуемая технология: "+require_tech, CivColor.LightGray+"* "+CivColor.Gold+"Требуемая структура: "+require_structure});
    			}
    			item = LoreGuiItem.setAction(item, "Build");
    			item = LoreGuiItem.setActionData(item, "buildName", sinfo.displayName);
    			page3.setItem(threeCount, item);
    			threeCount++;
			}
		}

		page3.setItem(27, pageBack);
		/* Page 4 */
		Inventory page4 = guiInv.getPage(ChatColor.LIGHT_PURPLE+"Культурные постройки");

		int fourCount = 0;
		for (ConfigBuildableInfo sinfo : CivSettings.structures.values()) {
			if ( sinfo.structureGroup.equalsIgnoreCase("culture") ) {
				if ( fourCount == 27 ) {
    				fourCount = 28;
    			}
				
				String require_structure = "";
				String require_tech = "";
				
				if ( sinfo.require_structure == null ) {
					require_structure = CivColor.Red+"Отсутствует";
				} else if (town.hasStructure(sinfo.require_structure)) {
					require_structure = CivColor.Green+CivSettings.structures.get(sinfo.require_structure).displayName;
				} else if (!town.hasStructure(sinfo.require_structure)) {
					require_structure = CivColor.Red+CivSettings.structures.get(sinfo.require_structure).displayName;
				}
				
				if ( sinfo.require_tech == null ) {
					require_tech = CivColor.Red+"Отсутствует";
				} else if (town.getCiv().hasTechnology(sinfo.require_tech)) {
					require_tech = CivColor.Green+CivSettings.techs.get(sinfo.require_tech).name;
				} else if (!town.getCiv().hasTechnology(sinfo.require_tech)) {
					require_tech = CivColor.Red+CivSettings.techs.get(sinfo.require_tech).name;
				}
				
    			ItemStack item = null;
    			if ( sinfo.structureItem == null || sinfo.structureItem == "" || sinfo.structureItem == "null") {
    				item = LoreGuiItem.build(ChatColor.LIGHT_PURPLE+sinfo.displayName, Material.MAGENTA_CONCRETE, new String[]{CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(sinfo.cost), CivColor.Gold+"Молоточки: "+CivColor.White+Math.round(sinfo.hammer_cost), CivColor.LightGray+"_______________________________", CivColor.LightGray+"* "+CivColor.Gold+"Требуемая технология: "+require_tech, CivColor.LightGray+"* "+CivColor.Gold+"Требуемая структура: "+require_structure});
    			} else {
    				item = LoreGuiItem.build(ChatColor.LIGHT_PURPLE+sinfo.displayName, Material.matchMaterial(sinfo.structureItem), new String[]{CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(sinfo.cost), CivColor.Gold+"Молоточки: "+CivColor.White+Math.round(sinfo.hammer_cost), CivColor.LightGray+"_______________________________", CivColor.LightGray+"* "+CivColor.Gold+"Требуемая технология: "+require_tech, CivColor.LightGray+"* "+CivColor.Gold+"Требуемая структура: "+require_structure});
    			}
    			item = LoreGuiItem.setAction(item, "Build");
    			item = LoreGuiItem.setActionData(item, "buildName", sinfo.displayName);
    			page4.setItem(fourCount, item);
    			fourCount++;
			}
		}

		page4.setItem(27, pageBack);
		/* Page 5 */
		Inventory page5 = guiInv.getPage(CivColor.Gold+"Чудеса света");
		
		int fiveCount = 0;
		for (ConfigBuildableInfo sinfo : CivSettings.wonders.values()) {
			if ( fiveCount == 27 ) {
				fiveCount = 28;
			}
			
			String require_structure = "";
			String require_tech = "";
			
			if ( sinfo.require_structure == null) {
				require_structure = CivColor.Red+"Отсутствует";
			} else if (town.hasStructure(sinfo.require_structure)) {
				require_structure = CivColor.Green+CivSettings.structures.get(sinfo.require_structure).displayName;
			} else if (!town.hasStructure(sinfo.require_structure)) {
				require_structure = CivColor.Red+CivSettings.structures.get(sinfo.require_structure).displayName;
			}
			
			if ( sinfo.require_tech == null || CivSettings.techs.get(sinfo.require_tech) == null) {
				require_tech = CivColor.Red+"Отсутствует";
			} else if (town.getCiv().hasTechnology(sinfo.require_tech)) {
				require_tech = CivColor.Green+CivSettings.techs.get(sinfo.require_tech).name;
			} else if (!town.getCiv().hasTechnology(sinfo.require_tech)) {
				require_tech = CivColor.Red+CivSettings.techs.get(sinfo.require_tech).name;
			}

			ItemStack item = null;
			if ( sinfo.structureItem == null || sinfo.structureItem == "" || sinfo.structureItem == "null") {
				item = LoreGuiItem.build(CivColor.Gold+sinfo.displayName, Material.YELLOW_CONCRETE, new String[]{CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(sinfo.cost), CivColor.Gold+"Молоточки: "+CivColor.White+Math.round(sinfo.hammer_cost), CivColor.LightGray+"_______________________________", CivColor.LightGray+"* "+CivColor.Gold+"Требуемая технология: "+require_tech, CivColor.LightGray+"* "+CivColor.Gold+"Требуемая структура: "+require_structure});
			} else {
				item = LoreGuiItem.build(CivColor.Gold+sinfo.displayName, Material.matchMaterial(sinfo.structureItem), new String[]{CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(sinfo.cost), CivColor.Gold+"Молоточки: "+CivColor.White+Math.round(sinfo.hammer_cost), CivColor.LightGray+"_______________________________", CivColor.LightGray+"* "+CivColor.Gold+"Требуемая технология: "+require_tech, CivColor.LightGray+"* "+CivColor.Gold+"Требуемая структура: "+require_structure});
			}
			item = LoreGuiItem.setAction(item, "Build");
			item = LoreGuiItem.setActionData(item, "buildName", sinfo.displayName);
			page5.setItem(fiveCount, item);
			fiveCount++;
		}

		page5.setItem(27, pageBack);
		/* Open inventory */
		player.openInventory(inv);
	}
	
}
