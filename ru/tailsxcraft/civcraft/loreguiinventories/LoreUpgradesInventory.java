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
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigBuildableInfo;
import ru.tailsxcraft.civcraft.config.ConfigTech;
import ru.tailsxcraft.civcraft.config.ConfigTownUpgrade;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiBuildInventory;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.util.CivColor;

public class LoreUpgradesInventory {
	
	public static void openInventory(Player player, Town town) {
		/* Main Inventory */
		LoreGuiBuildInventory guiInv = new LoreGuiBuildInventory(player, "Категории улучшений", 45, new String[]{"Город", "Банк", "Библиотека", "Карьер", "Дробилка", "Торговый Корабль", "Рыболованая Ферма", "Бакалея"}, 54, 54, 54, 36, 36, 36, 36, 36);
		Inventory inv = guiInv.inv;
		/* First category */
		createCategory(inv, Material.IRON_DOOR, CivColor.Gold, "Город", 10);
		/* Second category */
		createCategory(inv, Material.DIAMOND, CivColor.Gold, "Банк", 12);
		/* Third category */
		createCategory(inv, Material.BOOKSHELF, CivColor.Gold, "Библиотека", 14);
		/* Fourth category */
		createCategory(inv, Material.DIAMOND_PICKAXE, CivColor.Gold, "Карьер", 16);
		/* Fifth category */
		createCategory(inv, Material.STONE, CivColor.Gold, "Дробилка", 28);
		/* Sixth category */
		createCategory(inv, Material.HEART_OF_THE_SEA, CivColor.Gold, "Торговый Корабль", 30);
		/* Seventh category */
		createCategory(inv, Material.FISHING_ROD, CivColor.Gold, "Рыбная Ферма", 32);
		/* Eighth category */
		createCategory(inv, Material.COOKED_COD, CivColor.Gold, "Бакалея", 34);
		/* Pages */

		/* Page 1 */
		Inventory upgrade_town = guiInv.getPage("Город");
		Inventory bank = guiInv.getPage("Банк");
		Inventory library = guiInv.getPage("Библиотека");
		Inventory quarry = guiInv.getPage("Карьер");
		Inventory trommel = guiInv.getPage("Дробилка");
		Inventory tradeship = guiInv.getPage("Торговый Корабль");
		Inventory fishfarm = guiInv.getPage("Рыболованая Ферма");
		Inventory grocer = guiInv.getPage("Бакалея");
		
		class Upgrades {
			public void createUpgradesTown() {
				List<ConfigTownUpgrade> townUpgrades = new ArrayList<ConfigTownUpgrade>();
				int count = 0;
				for ( ConfigTownUpgrade townUpgrade : CivSettings.townUpgrades.values()) {
					if ( !townUpgrade.id.contains("upgrade_town") ) continue;
					townUpgrades.add(townUpgrade);
				}
				for ( int i = 9; i < 36; i++ ) {
					if ( i != 10 && i != 12 && i != 14 && i != 16 && i != 28 && i != 30 && i != 32 && i != 34 ) {
						continue;
					};
					int position = i;
					
					ConfigTownUpgrade townUpgrade = null;
					try { 
						townUpgrade = townUpgrades.get(count);
					} catch ( IndexOutOfBoundsException e ) {
						continue;
					}
					if ( townUpgrade == null ) continue;
					String name = "";
					String required_tech = "";
					String required_upgrade = "";
					String required_structure = "";
					if ( townUpgrade.isAvailable(town)) {
						name = CivColor.LightGreen+townUpgrade.name;
					} else if ( town.hasUpgrade(townUpgrade.id) ) {
						name = CivColor.LightGreen+townUpgrade.name;
					} else {
						name = ChatColor.RED+townUpgrade.name;
					}
					
					if ( townUpgrade.require_tech == null || townUpgrade.require_tech == "" || townUpgrade.require_tech == "null" ) {
						required_tech = ChatColor.RED+"Отсутствует";
					} else {
						ConfigTech tech = CivSettings.techs.get(townUpgrade.require_tech);
						if ( town.getCiv().hasTechnology(townUpgrade.require_tech)) {
							required_tech = CivColor.LightGreen+tech.name;
						} else {
							required_tech = ChatColor.RED+tech.name;
						}
					}
					
					if ( townUpgrade.require_upgrade == null || townUpgrade.require_upgrade == "" || townUpgrade.require_upgrade == "null" ) {
						required_upgrade = ChatColor.RED+"Отсутствует";
					} else {
						ConfigTownUpgrade upgrade = CivSettings.townUpgrades.get(townUpgrade.require_upgrade);
						if ( town.hasUpgrade(townUpgrade.require_upgrade)) {
							required_upgrade = CivColor.LightGreen+upgrade.name;
						} else {
							required_upgrade = ChatColor.RED+upgrade.name;
						}
					}
					
					if ( townUpgrade.require_structure == null || townUpgrade.require_structure == "" || townUpgrade.require_structure == "null" ) {
						required_structure = ChatColor.RED+"Отсутствует";
					} else {
						ConfigBuildableInfo building = CivSettings.structures.get(townUpgrade.require_structure);
						if ( town.hasStructure(townUpgrade.require_structure)) {
							required_structure = CivColor.LightGreen+building.displayName;
						} else {
							required_structure = ChatColor.RED+building.displayName;
						}
					}
					
					String[] lore;
					
					if ( town.hasUpgrade(townUpgrade.id) ) {
						lore = new String[] {CivColor.Gold+"Куплено"};
					} else {
			            lore = new String[] {CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(townUpgrade.cost), CivColor.Gold+"Требуемая технология: "+required_tech, CivColor.Gold+"Требуемое улучшение: "+required_upgrade, CivColor.Gold+"Требуемая постройка: "+required_structure};
					}
					
					ItemStack townItem = LoreGuiItem.build(name, Material.IRON_DOOR, lore);
					townItem = LoreGuiItem.setAction(townItem, "BuyUpgrade");
					townItem = LoreGuiItem.setActionData(townItem, "upgradeName", townUpgrade.name);
					upgrade_town.setItem(position, townItem);
					count++;
				}
				
				for ( int i = 45; i < 54; i++ ) {
					ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
					pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
					pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
					
					if ( i == 49 )  {
					    upgrade_town.setItem(49, pageBack);
					    continue;
					}
					
					ItemStack glassPane = LoreGuiItem.build(ChatColor.DARK_GRAY+"Пустота", Material.GRAY_STAINED_GLASS_PANE);
					
					upgrade_town.setItem(i, glassPane);
				}
			}
	
			public void createUpgradesBank() {
				int count = 0;
				for ( int i = 0; i < 45; i++ ) {
					if ( i != 0 && i != 2 && i != 4 && i != 6 && i != 8 && i != 19 && i != 21 && i != 23 && i != 25 && i != 37 && i != 39 && i != 41 && i != 43 ) {
						continue;
					};
					int position = i;
					
					ConfigTownUpgrade townUpgrade = null;
					
					if ( i == 0 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_bank_level_2"); if ( i == 23 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_bank_level_9");
					if ( i == 2 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_bank_level_3"); if ( i == 25 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_bank_level_10");
					if ( i == 4 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_bank_level_4"); if ( i == 37 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_bank_interest_level_1");
					if ( i == 6 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_bank_level_5"); if ( i == 39 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_bank_interest_level_2");
					if ( i == 8 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_bank_level_6"); if ( i == 41 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_bank_interest_level_3");
					if ( i == 19 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_bank_level_7"); if ( i == 43 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_bank_interest_level_4");
					if ( i == 21 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_bank_level_8"); 
					
					if ( townUpgrade == null ) { 
						continue;
					}
					String name = "";
					String required_tech = "";
					String required_upgrade = "";
					String required_structure = "";
					if ( townUpgrade.isAvailable(town)) {
						name = CivColor.LightGreen+townUpgrade.name;
					} else if ( town.hasUpgrade(townUpgrade.id) ) {
						name = CivColor.LightGreen+townUpgrade.name;
					} else {
						name = ChatColor.RED+townUpgrade.name;
					}
					
					if ( townUpgrade.require_tech == null || townUpgrade.require_tech == "" || townUpgrade.require_tech == "null" ) {
						required_tech = ChatColor.RED+"Отсутствует";
					} else {
						ConfigTech tech = CivSettings.techs.get(townUpgrade.require_tech);
						if ( town.getCiv().hasTechnology(townUpgrade.require_tech)) {
							required_tech = CivColor.LightGreen+tech.name;
						} else {
							required_tech = ChatColor.RED+tech.name;
						}
					}
					
					if ( townUpgrade.require_upgrade == null || townUpgrade.require_upgrade == "" || townUpgrade.require_upgrade == "null" ) {
						required_upgrade = ChatColor.RED+"Отсутствует";
					} else {
						ConfigTownUpgrade upgrade = CivSettings.townUpgrades.get(townUpgrade.require_upgrade);
						if ( town.hasUpgrade(townUpgrade.require_upgrade)) {
							required_upgrade = CivColor.LightGreen+upgrade.name;
						} else {
							required_upgrade = ChatColor.RED+upgrade.name;
						}
					}
					
					if ( townUpgrade.require_structure == null || townUpgrade.require_structure == "" || townUpgrade.require_structure == "null" ) {
						required_structure = ChatColor.RED+"Отсутствует";
					} else {
						ConfigBuildableInfo building = CivSettings.structures.get(townUpgrade.require_structure);
						if ( town.hasStructure(townUpgrade.require_structure)) {
							required_structure = CivColor.LightGreen+building.displayName;
						} else {
							required_structure = ChatColor.RED+building.displayName;
						}
					}
					
					String[] lore;
					
					if ( town.hasUpgrade(townUpgrade.id) ) {
						lore = new String[] {CivColor.Gold+"Куплено"};
					} else {
			            lore = new String[] {CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(townUpgrade.cost), CivColor.Gold+"Требуемая технология: "+required_tech, CivColor.Gold+"Требуемое улучшение: "+required_upgrade, CivColor.Gold+"Требуемая постройка: "+required_structure};
					}
					
					ItemStack townItem = null;
					
					if ( townUpgrade.id.contains("upgrade_bank_level") ) {
					    townItem = LoreGuiItem.build(name, Material.DIAMOND, lore);
					    townItem = LoreGuiItem.setAction(townItem, "BuyUpgrade");
						townItem = LoreGuiItem.setActionData(townItem, "upgradeName", townUpgrade.name);
					} else if ( townUpgrade.id.contains("upgrade_bank_interest") ) {
					    townItem = LoreGuiItem.build(name, Material.WRITABLE_BOOK, lore);
					    townItem = LoreGuiItem.setAction(townItem, "BuyUpgrade");
						townItem = LoreGuiItem.setActionData(townItem, "upgradeName", townUpgrade.name);
					} 
					
					bank.setItem(position, townItem);
					count++;
				}
				
				for ( int i = 45; i < 54; i++ ) {
					ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
					pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
					pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
					
					if ( i == 49 )  {
						bank.setItem(49, pageBack);
					    continue;
					}
					
					ItemStack glassPane = LoreGuiItem.build(ChatColor.DARK_GRAY+"Пустота", Material.GRAY_STAINED_GLASS_PANE);
					
					bank.setItem(i, glassPane);
				}
			}
			
			public void createUpgradesLibrary() {
				int count = 0;
				for ( int i = 0; i < 45; i++ ) {
					if ( i != 1 && i != 4 && i != 7 && i != 18 && i != 20 && i != 22 && i != 24 && i != 26 && i != 28 && i != 30 && i != 32 && i != 34 && i != 38 && i != 42 ) {
						continue;
					};
					int position = i;
					
					ConfigTownUpgrade townUpgrade = null;
					
					if ( i == 1 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_library_level_2"); if ( i == 26 ) townUpgrade = CivSettings.townUpgrades.get("research_efficiency_5");
					if ( i == 4 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_library_level_3"); if ( i == 28 ) townUpgrade = CivSettings.townUpgrades.get("research_looting_1");
					if ( i == 7 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_library_level_4"); if ( i == 30 ) townUpgrade = CivSettings.townUpgrades.get("research_looting_2");
					if ( i == 18 ) townUpgrade = CivSettings.townUpgrades.get("research_efficiency_1"); if ( i == 32 ) townUpgrade = CivSettings.townUpgrades.get("research_fortune_1");
					if ( i == 20 ) townUpgrade = CivSettings.townUpgrades.get("research_efficiency_2"); if ( i == 34 ) townUpgrade = CivSettings.townUpgrades.get("research_silk_touch_1");
					if ( i == 22 ) townUpgrade = CivSettings.townUpgrades.get("research_efficiency_3"); if ( i == 38 ) townUpgrade = CivSettings.townUpgrades.get("research_feather_falling_1");
					if ( i == 24 ) townUpgrade = CivSettings.townUpgrades.get("research_efficiency_4"); if ( i == 42 ) townUpgrade = CivSettings.townUpgrades.get("research_soulbound"); 
					
					if ( townUpgrade == null ) { 
						continue;
					}
					String name = "";
					String required_tech = "";
					String required_upgrade = "";
					String required_structure = "";
					if ( townUpgrade.isAvailable(town)) {
						name = CivColor.LightGreen+townUpgrade.name;
					} else if ( town.hasUpgrade(townUpgrade.id) ) {
						name = CivColor.LightGreen+townUpgrade.name;
					} else {
						name = ChatColor.RED+townUpgrade.name;
					}
					
					if ( townUpgrade.require_tech == null || townUpgrade.require_tech == "" || townUpgrade.require_tech == "null" ) {
						required_tech = ChatColor.RED+"Отсутствует";
					} else {
						ConfigTech tech = CivSettings.techs.get(townUpgrade.require_tech);
						if ( town.getCiv().hasTechnology(townUpgrade.require_tech)) {
							required_tech = CivColor.LightGreen+tech.name;
						} else {
							required_tech = ChatColor.RED+tech.name;
						}
					}
					
					if ( townUpgrade.require_upgrade == null || townUpgrade.require_upgrade == "" || townUpgrade.require_upgrade == "null" ) {
						required_upgrade = ChatColor.RED+"Отсутствует";
					} else {
						ConfigTownUpgrade upgrade = CivSettings.townUpgrades.get(townUpgrade.require_upgrade);
						if ( town.hasUpgrade(townUpgrade.require_upgrade)) {
							required_upgrade = CivColor.LightGreen+upgrade.name;
						} else {
							required_upgrade = ChatColor.RED+upgrade.name;
						}
					}
					
					if ( townUpgrade.require_structure == null || townUpgrade.require_structure == "" || townUpgrade.require_structure == "null" ) {
						required_structure = ChatColor.RED+"Отсутствует";
					} else {
						ConfigBuildableInfo building = CivSettings.structures.get(townUpgrade.require_structure);
						if ( town.hasStructure(townUpgrade.require_structure)) {
							required_structure = CivColor.LightGreen+building.displayName;
						} else {
							required_structure = ChatColor.RED+building.displayName;
						}
					}
					
					String[] lore;
					
					if ( town.hasUpgrade(townUpgrade.id) ) {
						lore = new String[] {CivColor.Gold+"Куплено"};
					} else {
			            lore = new String[] {CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(townUpgrade.cost), CivColor.Gold+"Требуемая технология: "+required_tech, CivColor.Gold+"Требуемое улучшение: "+required_upgrade, CivColor.Gold+"Требуемая постройка: "+required_structure};
					}
					
					ItemStack townItem = null;
					
					if ( townUpgrade.id.contains("upgrade_library") ) {
					    townItem = LoreGuiItem.build(name, Material.BOOKSHELF, lore);
					    townItem = LoreGuiItem.setAction(townItem, "BuyUpgrade");
						townItem = LoreGuiItem.setActionData(townItem, "upgradeName", townUpgrade.name);
					} else if ( townUpgrade.id.contains("research") ) {
					    townItem = LoreGuiItem.build(name, Material.ENCHANTED_BOOK, lore);
					    townItem = LoreGuiItem.setAction(townItem, "BuyUpgrade");
						townItem = LoreGuiItem.setActionData(townItem, "upgradeName", townUpgrade.name);
					} 
					
					library.setItem(position, townItem);
					count++;
				}
				
				for ( int i = 45; i < 54; i++ ) {
					ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
					pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
					pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
					
					if ( i == 49 )  {
						library.setItem(49, pageBack);
					    continue;
					}
					
					ItemStack glassPane = LoreGuiItem.build(ChatColor.DARK_GRAY+"Пустота", Material.GRAY_STAINED_GLASS_PANE);
					
					library.setItem(i, glassPane);
				}
			}
			
			public void createUpgradesQuarry() {
				int count = 0;
				for ( int i = 9; i < 36; i++ ) {
					if ( i != 10 && i != 13 && i != 16 ) {
						continue;
					};
					int position = i;
					
                    ConfigTownUpgrade townUpgrade = null;
					
					if ( i == 10 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_quarry_material_granite"); 
					if ( i == 13 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_quarry_material_diorite");
					if ( i == 16 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_quarry_level_andesite");
					
					if ( townUpgrade == null ) continue;
					String name = "";
					String required_tech = "";
					String required_upgrade = "";
					String required_structure = "";
					if ( townUpgrade.isAvailable(town)) {
						name = CivColor.LightGreen+townUpgrade.name;
					} else if ( town.hasUpgrade(townUpgrade.id) ) {
						name = CivColor.LightGreen+townUpgrade.name;
					} else {
						name = ChatColor.RED+townUpgrade.name;
					}
					
					if ( townUpgrade.require_tech == null || townUpgrade.require_tech == "" || townUpgrade.require_tech == "null" ) {
						required_tech = ChatColor.RED+"Отсутствует";
					} else {
						ConfigTech tech = CivSettings.techs.get(townUpgrade.require_tech);
						if ( town.getCiv().hasTechnology(townUpgrade.require_tech)) {
							required_tech = CivColor.LightGreen+tech.name;
						} else {
							required_tech = ChatColor.RED+tech.name;
						}
					}
					
					if ( townUpgrade.require_upgrade == null || townUpgrade.require_upgrade == "" || townUpgrade.require_upgrade == "null" ) {
						required_upgrade = ChatColor.RED+"Отсутствует";
					} else {
						ConfigTownUpgrade upgrade = CivSettings.townUpgrades.get(townUpgrade.require_upgrade);
						if ( town.hasUpgrade(townUpgrade.require_upgrade)) {
							required_upgrade = CivColor.LightGreen+upgrade.name;
						} else {
							required_upgrade = ChatColor.RED+upgrade.name;
						}
					}
					
					if ( townUpgrade.require_structure == null || townUpgrade.require_structure == "" || townUpgrade.require_structure == "null" ) {
						required_structure = ChatColor.RED+"Отсутствует";
					} else {
						ConfigBuildableInfo building = CivSettings.structures.get(townUpgrade.require_structure);
						if ( town.hasStructure(townUpgrade.require_structure)) {
							required_structure = CivColor.LightGreen+building.displayName;
						} else {
							required_structure = ChatColor.RED+building.displayName;
						}
					}
					
					String[] lore;
					
					if ( town.hasUpgrade(townUpgrade.id) ) {
						lore = new String[] {CivColor.Gold+"Куплено"};
					} else {
			            lore = new String[] {CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(townUpgrade.cost), CivColor.Gold+"Требуемая технология: "+required_tech, CivColor.Gold+"Требуемое улучшение: "+required_upgrade, CivColor.Gold+"Требуемая постройка: "+required_structure};
					}
					
					ItemStack townItem = null;
					if ( townUpgrade.id.contains("upgrade_quarry_material_granite") ) {
					    townItem  = LoreGuiItem.build(name, Material.STONE_PICKAXE, lore);
					    townItem = LoreGuiItem.setAction(townItem, "BuyUpgrade");
						townItem = LoreGuiItem.setActionData(townItem, "upgradeName", townUpgrade.name);
					} else if ( townUpgrade.id.contains("upgrade_quarry_material_diorite") ) {
					    townItem = LoreGuiItem.build(name, Material.IRON_PICKAXE, lore);
					    townItem = LoreGuiItem.setAction(townItem, "BuyUpgrade");
						townItem = LoreGuiItem.setActionData(townItem, "upgradeName", townUpgrade.name);
					} else if ( townUpgrade.id.contains("upgrade_quarry_level_andesite") ) {
					    townItem = LoreGuiItem.build(name, Material.DIAMOND_PICKAXE, lore);
					    townItem = LoreGuiItem.setAction(townItem, "BuyUpgrade");
						townItem = LoreGuiItem.setActionData(townItem, "upgradeName", townUpgrade.name);
					} 
					quarry.setItem(position, townItem);
					count++;
				}
				
				for ( int i = 27; i < 36; i++ ) {
					ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
					pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
					pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
					
					if ( i == 31 )  {
						quarry.setItem(31, pageBack);
					    continue;
					}
					
					ItemStack glassPane = LoreGuiItem.build(ChatColor.DARK_GRAY+"Пустота", Material.GRAY_STAINED_GLASS_PANE);
					
					quarry.setItem(i, glassPane);
				}
			}
			
			public void createUpgradesTrommel() {
				int count = 0;
				for ( int i = 9; i < 36; i++ ) {
					if ( i != 10 && i != 13 && i != 16 ) {
						continue;
					};
					int position = i;
					
					ConfigTownUpgrade townUpgrade = null;
					
					if ( i == 10 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_trommel_material_granite"); 
					if ( i == 13 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_trommel_material_diorite");
					if ( i == 16 ) townUpgrade = CivSettings.townUpgrades.get("upgrade_trommel_level_andesite");
					
					if ( townUpgrade == null ) continue;
					String name = "";
					String required_tech = "";
					String required_upgrade = "";
					String required_structure = "";
					if ( townUpgrade.isAvailable(town)) {
						name = CivColor.LightGreen+townUpgrade.name;
					} else if ( town.hasUpgrade(townUpgrade.id) ) {
						name = CivColor.LightGreen+townUpgrade.name;
					} else {
						name = ChatColor.RED+townUpgrade.name;
					}
					
					if ( townUpgrade.require_tech == null || townUpgrade.require_tech == "" || townUpgrade.require_tech == "null" ) {
						required_tech = ChatColor.RED+"Отсутствует";
					} else {
						ConfigTech tech = CivSettings.techs.get(townUpgrade.require_tech);
						if ( town.getCiv().hasTechnology(townUpgrade.require_tech)) {
							required_tech = CivColor.LightGreen+tech.name;
						} else {
							required_tech = ChatColor.RED+tech.name;
						}
					}
					
					if ( townUpgrade.require_upgrade == null || townUpgrade.require_upgrade == "" || townUpgrade.require_upgrade == "null" ) {
						required_upgrade = ChatColor.RED+"Отсутствует";
					} else {
						ConfigTownUpgrade upgrade = CivSettings.townUpgrades.get(townUpgrade.require_upgrade);
						if ( town.hasUpgrade(townUpgrade.require_upgrade)) {
							required_upgrade = CivColor.LightGreen+upgrade.name;
						} else {
							required_upgrade = ChatColor.RED+upgrade.name;
						}
					}
					
					if ( townUpgrade.require_structure == null || townUpgrade.require_structure == "" || townUpgrade.require_structure == "null" ) {
						required_structure = ChatColor.RED+"Отсутствует";
					} else {
						ConfigBuildableInfo building = CivSettings.structures.get(townUpgrade.require_structure);
						if ( town.hasStructure(townUpgrade.require_structure)) {
							required_structure = CivColor.LightGreen+building.displayName;
						} else {
							required_structure = ChatColor.RED+building.displayName;
						}
					}
					
					String[] lore;
					
					if ( town.hasUpgrade(townUpgrade.id) ) {
						lore = new String[] {CivColor.Gold+"Куплено"};
					} else {
			            lore = new String[] {CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(townUpgrade.cost), CivColor.Gold+"Требуемая технология: "+required_tech, CivColor.Gold+"Требуемое улучшение: "+required_upgrade, CivColor.Gold+"Требуемая постройка: "+required_structure};
					}
					
					ItemStack townItem = null;
					if ( townUpgrade.id.contains("upgrade_trommel_material_granite") ) {
					    townItem  = LoreGuiItem.build(name, Material.GRANITE, lore);
					    townItem = LoreGuiItem.setAction(townItem, "BuyUpgrade");
						townItem = LoreGuiItem.setActionData(townItem, "upgradeName", townUpgrade.name);
					} else if ( townUpgrade.id.contains("upgrade_trommel_material_diorite") ) {
					    townItem = LoreGuiItem.build(name, Material.DIORITE, lore);
					    townItem = LoreGuiItem.setAction(townItem, "BuyUpgrade");
						townItem = LoreGuiItem.setActionData(townItem, "upgradeName", townUpgrade.name);
					} else if ( townUpgrade.id.contains("upgrade_trommel_level_andesite") ) {
					    townItem = LoreGuiItem.build(name, Material.ANDESITE, lore);
					    townItem = LoreGuiItem.setAction(townItem, "BuyUpgrade");
						townItem = LoreGuiItem.setActionData(townItem, "upgradeName", townUpgrade.name);
					} 
					trommel.setItem(position, townItem);
					count++;
				}
				
				for ( int i = 27; i < 36; i++ ) {
					ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
					pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
					pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
					
					if ( i == 31 )  {
						trommel.setItem(31, pageBack);
					    continue;
					}
					
					ItemStack glassPane = LoreGuiItem.build(ChatColor.DARK_GRAY+"Пустота", Material.GRAY_STAINED_GLASS_PANE);
					
					trommel.setItem(i, glassPane);
				}
			}
			
			public void createUpgradesTradeship() {
				List<ConfigTownUpgrade> townUpgrades = new ArrayList<ConfigTownUpgrade>();
				int count = 0;
				for ( ConfigTownUpgrade townUpgrade : CivSettings.townUpgrades.values()) {
					if ( !townUpgrade.id.contains("upgrade_tradeship") ) continue;
					townUpgrades.add(townUpgrade);
				}
				for ( int i = 9; i < 36; i++ ) {
					if ( i != 9 && i != 11 && i != 13 && i != 15 && i != 17 ) {
						continue;
					};
					int position = i;
					
					ConfigTownUpgrade townUpgrade = null;
					try { 
						townUpgrade = townUpgrades.get(count);
					} catch ( IndexOutOfBoundsException e ) {
						continue;
					}
					if ( townUpgrade == null ) continue;
					String name = "";
					String required_tech = "";
					String required_upgrade = "";
					String required_structure = "";
					if ( townUpgrade.isAvailable(town)) {
						name = CivColor.LightGreen+townUpgrade.name;
					} else if ( town.hasUpgrade(townUpgrade.id) ) {
						name = CivColor.LightGreen+townUpgrade.name;
					} else {
						name = ChatColor.RED+townUpgrade.name;
					}
					
					if ( townUpgrade.require_tech == null || townUpgrade.require_tech == "" || townUpgrade.require_tech == "null" ) {
						required_tech = ChatColor.RED+"Отсутствует";
					} else {
						ConfigTech tech = CivSettings.techs.get(townUpgrade.require_tech);
						if ( town.getCiv().hasTechnology(townUpgrade.require_tech)) {
							required_tech = CivColor.LightGreen+tech.name;
						} else {
							required_tech = ChatColor.RED+tech.name;
						}
					}
					
					if ( townUpgrade.require_upgrade == null || townUpgrade.require_upgrade == "" || townUpgrade.require_upgrade == "null" ) {
						required_upgrade = ChatColor.RED+"Отсутствует";
					} else {
						ConfigTownUpgrade upgrade = CivSettings.townUpgrades.get(townUpgrade.require_upgrade);
						if ( town.hasUpgrade(townUpgrade.require_upgrade)) {
							required_upgrade = CivColor.LightGreen+upgrade.name;
						} else {
							required_upgrade = ChatColor.RED+upgrade.name;
						}
					}
					
					if ( townUpgrade.require_structure == null || townUpgrade.require_structure == "" || townUpgrade.require_structure == "null" ) {
						required_structure = ChatColor.RED+"Отсутствует";
					} else {
						ConfigBuildableInfo building = CivSettings.structures.get(townUpgrade.require_structure);
						if ( town.hasStructure(townUpgrade.require_structure)) {
							required_structure = CivColor.LightGreen+building.displayName;
						} else {
							required_structure = ChatColor.RED+building.displayName;
						}
					}
					
					String[] lore;
					
					if ( town.hasUpgrade(townUpgrade.id) ) {
						lore = new String[] {CivColor.Gold+"Куплено"};
					} else {
			            lore = new String[] {CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(townUpgrade.cost), CivColor.Gold+"Требуемая технология: "+required_tech, CivColor.Gold+"Требуемое улучшение: "+required_upgrade, CivColor.Gold+"Требуемая постройка: "+required_structure};
					}
					
					ItemStack townItem = LoreGuiItem.build(name, Material.HEART_OF_THE_SEA, lore);
					townItem = LoreGuiItem.setAction(townItem, "BuyUpgrade");
					townItem = LoreGuiItem.setActionData(townItem, "upgradeName", townUpgrade.name);
					
					tradeship.setItem(position, townItem);
					count++;
				}
				
				for ( int i = 27; i < 36; i++ ) {
					ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
					pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
					pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
					
					if ( i == 31 )  {
						tradeship.setItem(31, pageBack);
					    continue;
					}
					
					ItemStack glassPane = LoreGuiItem.build(ChatColor.DARK_GRAY+"Пустота", Material.GRAY_STAINED_GLASS_PANE);
					
					tradeship.setItem(i, glassPane);
				}
			}
			
			public void createUpgradesFishHatchery() {
				List<ConfigTownUpgrade> townUpgrades = new ArrayList<ConfigTownUpgrade>();
				int count = 0;
				for ( ConfigTownUpgrade townUpgrade : CivSettings.townUpgrades.values()) {
					if ( !townUpgrade.id.contains("upgrade_fish_hatchery") ) continue;
					townUpgrades.add(townUpgrade);
				}
				for ( int i = 9; i < 36; i++ ) {
					if ( i != 10 && i != 13 && i != 16 ) {
						continue;
					};
					int position = i;
					
					ConfigTownUpgrade townUpgrade = null;
					try { 
						townUpgrade = townUpgrades.get(count);
					} catch ( IndexOutOfBoundsException e ) {
						continue;
					}
					if ( townUpgrade == null ) continue;
					String name = "";
					String required_tech = "";
					String required_upgrade = "";
					String required_structure = "";
					if ( townUpgrade.isAvailable(town)) {
						name = CivColor.LightGreen+townUpgrade.name;
					} else if ( town.hasUpgrade(townUpgrade.id) ) {
						name = CivColor.LightGreen+townUpgrade.name;
					} else {
						name = ChatColor.RED+townUpgrade.name;
					}
					
					if ( townUpgrade.require_tech == null || townUpgrade.require_tech == "" || townUpgrade.require_tech == "null" ) {
						required_tech = ChatColor.RED+"Отсутствует";
					} else {
						ConfigTech tech = CivSettings.techs.get(townUpgrade.require_tech);
						if ( town.getCiv().hasTechnology(townUpgrade.require_tech)) {
							required_tech = CivColor.LightGreen+tech.name;
						} else {
							required_tech = ChatColor.RED+tech.name;
						}
					}
					
					if ( townUpgrade.require_upgrade == null || townUpgrade.require_upgrade == "" || townUpgrade.require_upgrade == "null" ) {
						required_upgrade = ChatColor.RED+"Отсутствует";
					} else {
						ConfigTownUpgrade upgrade = CivSettings.townUpgrades.get(townUpgrade.require_upgrade);
						if ( town.hasUpgrade(townUpgrade.require_upgrade)) {
							required_upgrade = CivColor.LightGreen+upgrade.name;
						} else {
							required_upgrade = ChatColor.RED+upgrade.name;
						}
					}
					
					if ( townUpgrade.require_structure == null || townUpgrade.require_structure == "" || townUpgrade.require_structure == "null" ) {
						required_structure = ChatColor.RED+"Отсутствует";
					} else {
						ConfigBuildableInfo building = CivSettings.structures.get(townUpgrade.require_structure);
						if ( town.hasStructure(townUpgrade.require_structure)) {
							required_structure = CivColor.LightGreen+building.displayName;
						} else {
							required_structure = ChatColor.RED+building.displayName;
						}
					}
					
					String[] lore;
					
					if ( town.hasUpgrade(townUpgrade.id) ) {
						lore = new String[] {CivColor.Gold+"Куплено"};
					} else {
			            lore = new String[] {CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(townUpgrade.cost), CivColor.Gold+"Требуемая технология: "+required_tech, CivColor.Gold+"Требуемое улучшение: "+required_upgrade, CivColor.Gold+"Требуемая постройка: "+required_structure};
					}
					
					ItemStack townItem = LoreGuiItem.build(name, Material.FISHING_ROD, lore);
					townItem = LoreGuiItem.setAction(townItem, "BuyUpgrade");
					townItem = LoreGuiItem.setActionData(townItem, "upgradeName", townUpgrade.name);
					
					fishfarm.setItem(position, townItem);
					count++;
				}
				
				for ( int i = 27; i < 36; i++ ) {
					ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
					pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
					pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
					
					if ( i == 31 )  {
						fishfarm.setItem(31, pageBack);
					    continue;
					}
					
					ItemStack glassPane = LoreGuiItem.build(ChatColor.DARK_GRAY+"Пустота", Material.GRAY_STAINED_GLASS_PANE);
					
					fishfarm.setItem(i, glassPane);
				}
			}
			
			public void createUpgradesGrocer() {
				List<ConfigTownUpgrade> townUpgrades = new ArrayList<ConfigTownUpgrade>();
				int count = 0;
				for ( ConfigTownUpgrade townUpgrade : CivSettings.townUpgrades.values()) {
					if ( !townUpgrade.id.contains("upgrade_grocer") ) continue;
					townUpgrades.add(townUpgrade);
				}
				for ( int i = 9; i < 36; i++ ) {
					if ( i != 10 && i != 13 && i != 16 ) {
						continue;
					};
					int position = i;
					
					ConfigTownUpgrade townUpgrade = null;
					try { 
						townUpgrade = townUpgrades.get(count);
					} catch ( IndexOutOfBoundsException e ) {
						continue;
					}
					if ( townUpgrade == null ) continue;
					String name = "";
					String required_tech = "";
					String required_upgrade = "";
					String required_structure = "";
					if ( townUpgrade.isAvailable(town)) {
						name = CivColor.LightGreen+townUpgrade.name;
					} else if ( town.hasUpgrade(townUpgrade.id) ) {
						name = CivColor.LightGreen+townUpgrade.name;
					} else {
						name = ChatColor.RED+townUpgrade.name;
					}
					
					if ( townUpgrade.require_tech == null || townUpgrade.require_tech == "" || townUpgrade.require_tech == "null" ) {
						required_tech = ChatColor.RED+"Отсутствует";
					} else {
						ConfigTech tech = CivSettings.techs.get(townUpgrade.require_tech);
						if ( town.getCiv().hasTechnology(townUpgrade.require_tech)) {
							required_tech = CivColor.LightGreen+tech.name;
						} else {
							required_tech = ChatColor.RED+tech.name;
						}
					}
					
					if ( townUpgrade.require_upgrade == null || townUpgrade.require_upgrade == "" || townUpgrade.require_upgrade == "null" ) {
						required_upgrade = ChatColor.RED+"Отсутствует";
					} else {
						ConfigTownUpgrade upgrade = CivSettings.townUpgrades.get(townUpgrade.require_upgrade);
						if ( town.hasUpgrade(townUpgrade.require_upgrade)) {
							required_upgrade = CivColor.LightGreen+upgrade.name;
						} else {
							required_upgrade = ChatColor.RED+upgrade.name;
						}
					}
					
					if ( townUpgrade.require_structure == null || townUpgrade.require_structure == "" || townUpgrade.require_structure == "null" ) {
						required_structure = ChatColor.RED+"Отсутствует";
					} else {
						ConfigBuildableInfo building = CivSettings.structures.get(townUpgrade.require_structure);
						if ( town.hasStructure(townUpgrade.require_structure)) {
							required_structure = CivColor.LightGreen+building.displayName;
						} else {
							required_structure = ChatColor.RED+building.displayName;
						}
					}
					
					String[] lore;
					
					if ( town.hasUpgrade(townUpgrade.id) ) {
						lore = new String[] {CivColor.Gold+"Куплено"};
					} else {
			            lore = new String[] {CivColor.Gold+"Стоимость: "+CivColor.White+Math.round(townUpgrade.cost), CivColor.Gold+"Требуемая технология: "+required_tech, CivColor.Gold+"Требуемое улучшение: "+required_upgrade, CivColor.Gold+"Требуемая постройка: "+required_structure};
					}
					
					ItemStack townItem = LoreGuiItem.build(name, Material.COOKED_COD, lore);
					
					grocer.setItem(position, townItem);
					count++;
				}
				
				for ( int i = 27; i < 36; i++ ) {
					ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
					pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
					pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
					
					if ( i == 31 )  {
						grocer.setItem(31, pageBack);
					    continue;
					}
					
					ItemStack glassPane = LoreGuiItem.build(ChatColor.DARK_GRAY+"Пустота", Material.GRAY_STAINED_GLASS_PANE);
					
					grocer.setItem(i, glassPane);
				}
			}
		}
		
		Upgrades upgrades = new Upgrades();
		upgrades.createUpgradesTown();
		upgrades.createUpgradesBank();
		upgrades.createUpgradesLibrary();
		upgrades.createUpgradesQuarry();
		upgrades.createUpgradesTrommel();
		upgrades.createUpgradesTradeship();
		upgrades.createUpgradesFishHatchery();
		upgrades.createUpgradesGrocer();
		
		/* Open inventory */
		player.openInventory(inv);
	}
	
	private static void createCategory(Inventory inv, Material mat, String color, String name, int position) {
		ItemStack category = LoreGuiItem.build(color+name, mat);
		category = LoreGuiItem.setAction(category, "OpenInventory");
		category = LoreGuiItem.setActionData(category, "invType", "showBuildInvPage");
		category = LoreGuiItem.setActionData(category, "pageName", name);
		inv.setItem(position, category);
	}
	
}
