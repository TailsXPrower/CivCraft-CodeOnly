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
import ru.tailsxcraft.civcraft.config.ConfigMaterial;
import ru.tailsxcraft.civcraft.config.ConfigTech;
import ru.tailsxcraft.civcraft.config.ConfigTechPotion;
import ru.tailsxcraft.civcraft.config.ConfigTownUpgrade;
import ru.tailsxcraft.civcraft.config.ConfigUnit;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiBuildInventory;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.object.Civilization;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.util.CivColor;

public class LoreTechsInventory {
	
	public static void openInventory(Player player, Civilization civ) {
		/* Main Inventory */
		LoreGuiBuildInventory guiInv = new LoreGuiBuildInventory(player, "Категории изучений", 27, 45, new String[]{"Древность | Античность", "Средневековье", "Эпоха Возрождения", "Современность", "Древность | Античность", "Средневековье", "Эпоха Возрождения", "Индустриальная Эра", "Современность", "Древность | Античность", "Средневековье", "Древность | Античность", "Средневековье", "Эпоха Возрождения | Индустриальная Эра", "Чудеса Света", "Чудеса Света"}, new String[]{"firstEraMilitary", "secondEraMilitary", "thirdEraMilitary", "fourthEraMilitary", "firstEraIndustrial", "secondEraIndustrial", "thirdEraIndustrial", "fourthEraIndustrial", "fifthEraIndustrial", "firstEraEconomy", "secondEraEconomy", "firstEraCulture", "secondEraCulture", "thirdEraCulture", "wonders", "wonders2"});
		Inventory inv = guiInv.inv;
		/* First category */
		ItemStack categoryOne = LoreGuiItem.build(ChatColor.DARK_RED+"Вооружение", Material.RED_CONCRETE);
		categoryOne = LoreGuiItem.setAction(categoryOne, "OpenInventory");
		categoryOne = LoreGuiItem.setActionData(categoryOne, "invType", "showBuildInvPage");
		categoryOne = LoreGuiItem.setActionData(categoryOne, "pageName", "firstEraMilitary");
		inv.setItem(9, categoryOne);
		/* Second category */
		ItemStack categoryTwo = LoreGuiItem.build(ChatColor.DARK_AQUA+"Технологический прогресс", Material.CYAN_CONCRETE);
		categoryTwo = LoreGuiItem.setAction(categoryTwo, "OpenInventory");
		categoryTwo = LoreGuiItem.setActionData(categoryTwo, "invType", "showBuildInvPage");
		categoryTwo = LoreGuiItem.setActionData(categoryTwo, "pageName", "firstEraIndustrial");
		inv.setItem(11, categoryTwo);
		/* Third category */
		ItemStack categoryThree = LoreGuiItem.build(CivColor.LightGreen+"Экономика", Material.LIME_CONCRETE);
		categoryThree = LoreGuiItem.setAction(categoryThree, "OpenInventory");
		categoryThree = LoreGuiItem.setActionData(categoryThree, "invType", "showBuildInvPage");
		categoryThree = LoreGuiItem.setActionData(categoryThree, "pageName", "firstEraEconomy");
		inv.setItem(13, categoryThree);
		/* Fourth category */
		ItemStack categoryFour = LoreGuiItem.build(ChatColor.LIGHT_PURPLE+"Культура", Material.MAGENTA_CONCRETE);
		categoryFour = LoreGuiItem.setAction(categoryFour, "OpenInventory");
		categoryFour = LoreGuiItem.setActionData(categoryFour, "invType", "showBuildInvPage");
		categoryFour = LoreGuiItem.setActionData(categoryFour, "pageName", "firstEraCulture");
		inv.setItem(15, categoryFour);
		/* Fifth category */
		ItemStack categoryFive = LoreGuiItem.build(CivColor.Gold+"Чудеса света", Material.YELLOW_CONCRETE);
		categoryFive = LoreGuiItem.setAction(categoryFive, "OpenInventory");
		categoryFive = LoreGuiItem.setActionData(categoryFive, "invType", "showBuildInvPage");
		categoryFive = LoreGuiItem.setActionData(categoryFive, "pageName", "wonders");
		inv.setItem(17, categoryFive);
		/* Pages */
		ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
		pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
		pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
		
		ItemStack linealItem = LoreGuiItem.build(ChatColor.GRAY+"Линия", Material.BLACK_STAINED_GLASS_PANE);
		
		HashMap<String, ItemStack> techItems = new HashMap<String, ItemStack>();
		for ( ConfigTech tech : CivSettings.techs.values()) {
			Material status = null;
			String name = "";
			String techs = "";
			String[] requireTechs = null;
			String[] lore = null;
			if ( tech.require_techs == null ) {
				techs = CivColor.Red+"Отсутствуют";
			} else {
				requireTechs = tech.require_techs.split(":");
				for ( int i = 0; i < requireTechs.length; i++ ) {
					ConfigTech techRequire = CivSettings.techs.get(requireTechs[i]);
					if ( requireTechs[i] == requireTechs[requireTechs.length-1] ) {
						if ( civ.hasTechnology(techRequire.id) ) {
							techs += CivColor.Green+techRequire.name+CivColor.White;
						} else {
							techs += CivColor.Red+techRequire.name+CivColor.White;
						}
					} else {
						if ( civ.hasTechnology(techRequire.id) ) {
							techs += CivColor.Green+techRequire.name+CivColor.White+", ";
						} else {
							techs += CivColor.Red+techRequire.name+CivColor.White+", ";
						}
					}
				}
			}
			List<String> opens = new ArrayList<String>();
            
            List<String> loreSet = new ArrayList<String>();
            
			if ( tech.isAvailable(civ) && !civ.hasTechnology(tech.id) && civ.getResearchTech() != tech ) {
				status = Material.LIGHT_BLUE_CONCRETE;
				name = ChatColor.AQUA+tech.name;
				loreSet.add(CivColor.Gold+"Стоимость в Монетах: "+CivColor.White+Math.round(tech.cost));
				loreSet.add(CivColor.Gold+"Стоимость в Пробирках: "+CivColor.White+Math.round(tech.beaker_cost));
				loreSet.add(CivColor.Gold+"Необходимые технологии: "+techs);
				if ( !tech.id.equalsIgnoreCase("tech_great_pyramid") || !tech.id.equalsIgnoreCase("tech_hanging_gardens") || !tech.id.contentEquals("tech_colossus") 
						|| !tech.id.equalsIgnoreCase("tech_chichen_itza") || !tech.id.equalsIgnoreCase("tech_great_library") || !tech.id.equalsIgnoreCase("tech_notre_dame") 
						|| !tech.id.equalsIgnoreCase("tech_council_of_eight") || !tech.id.equalsIgnoreCase("tech_colosseum") || !tech.id.equalsIgnoreCase("tech_globe_theatre")
						|| !tech.id.equalsIgnoreCase("tech_great_lighthouse") || !tech.id.equalsIgnoreCase("tech_mother_tree") || !tech.id.equalsIgnoreCase("tech_grand_ship_ingermanland")) {
					loreSet.add(CivColor.White+"______________________________________");
					loreSet.add(CivColor.Gold+"Открывает: ");
				}
			} else if ( civ.hasTechnology(tech.id) ){
				status = Material.LIME_CONCRETE;
				name = CivColor.Green+tech.name;
				loreSet.add(CivColor.Gold+"Стоимость в Монетах: "+CivColor.White+Math.round(tech.cost));
				loreSet.add(CivColor.Gold+"Стоимость в Пробирках: "+CivColor.White+Math.round(tech.beaker_cost));
				loreSet.add(CivColor.Gold+"Необходимые технологии: "+techs);
				if ( !tech.id.equalsIgnoreCase("tech_great_pyramid") || !tech.id.equalsIgnoreCase("tech_hanging_gardens") || !tech.id.contentEquals("tech_colossus") 
						|| !tech.id.equalsIgnoreCase("tech_chichen_itza") || !tech.id.equalsIgnoreCase("tech_great_library") || !tech.id.equalsIgnoreCase("tech_notre_dame") 
						|| !tech.id.equalsIgnoreCase("tech_council_of_eight") || !tech.id.equalsIgnoreCase("tech_colosseum") || !tech.id.equalsIgnoreCase("tech_globe_theatre")
						|| !tech.id.equalsIgnoreCase("tech_great_lighthouse") || !tech.id.equalsIgnoreCase("tech_mother_tree") || !tech.id.equalsIgnoreCase("tech_grand_ship_ingermanland")) {
					loreSet.add(CivColor.White+"______________________________________");
					loreSet.add(CivColor.Gold+"Открывает: ");
				}
			} else if ( !tech.isAvailable(civ) && !civ.hasTechnology(tech.id) && civ.getResearchTech() != tech ){
				status = Material.LIGHT_GRAY_CONCRETE;
				name = CivColor.LightGray+tech.name;
				loreSet.add(CivColor.Gold+"Стоимость в Монетах: "+CivColor.White+Math.round(tech.cost));
				loreSet.add(CivColor.Gold+"Стоимость в Пробирках: "+CivColor.White+Math.round(tech.beaker_cost));
				loreSet.add(CivColor.Gold+"Необходимые технологии: "+techs);
				if ( !tech.id.equalsIgnoreCase("tech_great_pyramid") || !tech.id.equalsIgnoreCase("tech_hanging_gardens") || !tech.id.contentEquals("tech_colossus") 
						|| !tech.id.equalsIgnoreCase("tech_chichen_itza") || !tech.id.equalsIgnoreCase("tech_great_library") || !tech.id.equalsIgnoreCase("tech_notre_dame") 
						|| !tech.id.equalsIgnoreCase("tech_council_of_eight") || !tech.id.equalsIgnoreCase("tech_colosseum") || !tech.id.equalsIgnoreCase("tech_globe_theatre")
						|| !tech.id.equalsIgnoreCase("tech_great_lighthouse") || !tech.id.equalsIgnoreCase("tech_mother_tree") || !tech.id.equalsIgnoreCase("tech_grand_ship_ingermanland")) {
					loreSet.add(CivColor.White+"______________________________________");
					loreSet.add(CivColor.Gold+"Открывает: ");
				}
			} 
			
			for (ConfigBuildableInfo build : CivSettings.structures.values()) {
				if ( build.require_tech == null ) continue;
				
				if ( build.require_tech.equalsIgnoreCase(tech.id) ) {
					opens.add(CivColor.LightGray+"* "+CivColor.LightGreen+build.displayName);
				}
			}
			
            for (ConfigTownUpgrade upg : CivSettings.townUpgrades.values()) {
            	if ( upg.require_tech == null ) continue;
            	
            	if ( upg.require_tech.equalsIgnoreCase(tech.id)) {
            		opens.add(CivColor.LightGray+"* "+CivColor.LightGreen+upg.name);
				}
			}
            
            for (ConfigUnit unit : CivSettings.units.values()) {
            	if ( unit.require_tech == null ) continue;
            	
            	if ( unit.require_tech.equalsIgnoreCase(tech.id) ) {
            		opens.add(CivColor.LightGray+"* "+CivColor.LightGreen+unit.name);
				}
			}
            
            for (ConfigTechPotion potion : CivSettings.techPotions.values()) {
            	if ( potion.require_tech == null ) continue;
            	
            	if ( potion.require_tech.equalsIgnoreCase(tech.id) ) {
            		opens.add(CivColor.LightGray+"* "+CivColor.LightGreen+potion.name);
				}
			}
            
            for (ConfigMaterial mat : CivSettings.materials.values()) {
            	if ( mat.required_tech == null ) continue;
            	
            	if ( mat.required_tech.equalsIgnoreCase(tech.id) ) {
            		if ( mat.category.contains("1")) {
            			opens.add(CivColor.LightGray+"*"+CivColor.LightGreen+" Т1 Вооружение");
            			break;
            		} else if ( mat.category.contains("2")) {
            			opens.add(CivColor.LightGray+"*"+CivColor.LightGreen+" Т2 Вооружение");
            			break;
            		} else if ( mat.category.contains("3")) {
            			opens.add(CivColor.LightGray+"*"+CivColor.LightGreen+" Т3 Вооружение");
            			break;
            		} else if ( mat.category.contains("4")) {
            			opens.add(CivColor.LightGray+"*"+CivColor.LightGreen+" Т4 Вооружение");
            			break;
            		} else { 
            			opens.add(CivColor.LightGray+"* "+CivColor.LightGreen+mat.name);
            		}
				}
			}
            
            if ( !tech.id.equalsIgnoreCase("tech_great_pyramid") || !tech.id.equalsIgnoreCase("tech_hanging_gardens") || !tech.id.equalsIgnoreCase("tech_colossus") 
					|| !tech.id.equalsIgnoreCase("tech_chichen_itza") || !tech.id.equalsIgnoreCase("tech_great_library") || !tech.id.equalsIgnoreCase("tech_notre_dame") 
					|| !tech.id.equalsIgnoreCase("tech_council_of_eight") || !tech.id.equalsIgnoreCase("tech_colosseum") || !tech.id.equalsIgnoreCase("tech_globe_theatre")
					|| !tech.id.equalsIgnoreCase("tech_great_lighthouse") || !tech.id.equalsIgnoreCase("tech_mother_tree") || !tech.id.equalsIgnoreCase("tech_grand_ship_ingermanland")) {
            	if ( opens.size() == 0 || opens == null ) {
                	loreSet.add(CivColor.LightGray+"*"+CivColor.LightGreen+" Ничего");
                } else {
                	loreSet.addAll(opens);
                }
			}
            
            if ( civ.getResearchTech() == tech ){
				status = Material.YELLOW_CONCRETE;
				name = CivColor.Yellow+tech.name;
				int percentageComplete = (int)((civ.getResearchProgress() / civ.getResearchTech().getAdjustedBeakerCost(civ))*100);
				loreSet.clear();
				loreSet.add(CivColor.LightGreen+"Изучается");
				loreSet.add(CivColor.Gold+"Прогресс изучения: "+CivColor.White+percentageComplete+"%");
			}

			ItemStack techItem = LoreGuiItem.build(name, status, loreSet.toArray(new String[loreSet.size()]));
		    techItem = LoreGuiItem.setAction(techItem, "StartTech");
		    techItem = LoreGuiItem.setActionData(techItem, "techName", tech.name);
			techItems.put(tech.id, techItem);
		}
		
		Inventory military1 = guiInv.getPage("firstEraMilitary");
		Inventory military2 = guiInv.getPage("secondEraMilitary");
		Inventory military3 = guiInv.getPage("thirdEraMilitary");
		Inventory military4 = guiInv.getPage("fourthEraMilitary");
		Inventory industrial1 = guiInv.getPage("firstEraIndustrial");
		Inventory industrial2 = guiInv.getPage("secondEraIndustrial");
		Inventory industrial3 = guiInv.getPage("thirdEraIndustrial");
		Inventory industrial4 = guiInv.getPage("fourthEraIndustrial");
		Inventory industrial5 = guiInv.getPage("fifthEraIndustrial");
		Inventory economy1 = guiInv.getPage("firstEraEconomy");
		Inventory economy2 = guiInv.getPage("secondEraEconomy");
		Inventory culture1 = guiInv.getPage("firstEraCulture");
		Inventory culture2 = guiInv.getPage("secondEraCulture");
		Inventory culture3 = guiInv.getPage("thirdEraCulture");
		Inventory wonders = guiInv.getPage("wonders");
		Inventory wonders2 = guiInv.getPage("wonders2");
		
		class techs {
			public void createMilitaryEraFirst() {
				ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
				pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
				pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
				
				ItemStack pageNextEraMilitary = LoreGuiItem.build(ChatColor.WHITE+"Следующая эра", Material.MAP);
				pageNextEraMilitary = LoreGuiItem.setAction(pageNextEraMilitary, "OpenInventory");
				pageNextEraMilitary = LoreGuiItem.setActionData(pageNextEraMilitary, "invType", "showBuildInvPage");
				pageNextEraMilitary = LoreGuiItem.setActionData(pageNextEraMilitary, "pageName", "secondEraMilitary");
				
				ItemStack infoMilitaryPanel = LoreGuiItem.build(ChatColor.RED+"Вооружение", Material.RED_STAINED_GLASS_PANE);
				
				military1.setItem(40, techItems.get("tech_armory"));
				military1.setItem(29, linealItem);
				military1.setItem(30, linealItem);
				military1.setItem(31, linealItem);
				military1.setItem(32, linealItem);
				military1.setItem(33, linealItem);
				military1.setItem(11, linealItem);
				military1.setItem(12, linealItem);
				military1.setItem(13, linealItem);
				military1.setItem(14, linealItem);
				military1.setItem(15, linealItem);
				military1.setItem(4, linealItem);
				military1.setItem(20, techItems.get("tech_archery"));
				military1.setItem(22, techItems.get("tech_blacksmithing"));
				military1.setItem(24, techItems.get("tech_metal_casting"));
				military1.setItem(0, infoMilitaryPanel);
				military1.setItem(9, infoMilitaryPanel);
				military1.setItem(27, infoMilitaryPanel);
				military1.setItem(36, infoMilitaryPanel);
				military1.setItem(8, infoMilitaryPanel);
				military1.setItem(17, infoMilitaryPanel);
				military1.setItem(35, infoMilitaryPanel);
				military1.setItem(44, infoMilitaryPanel);
				military1.setItem(18, pageBack);
				military1.setItem(26, pageNextEraMilitary);
			}
			
			public void createMilitaryEraSecond() {	
				ItemStack pageBackEraMilitary = LoreGuiItem.build(ChatColor.WHITE+"Предыдущая эра", Material.MAP);
				pageBackEraMilitary = LoreGuiItem.setAction(pageBackEraMilitary, "OpenInventory");
				pageBackEraMilitary = LoreGuiItem.setActionData(pageBackEraMilitary, "invType", "showBuildInvPage");
				pageBackEraMilitary = LoreGuiItem.setActionData(pageBackEraMilitary, "pageName", "firstEraMilitary");
				ItemStack pageNextEraMilitary = LoreGuiItem.build(ChatColor.WHITE+"Следующая эра", Material.MAP);
				pageNextEraMilitary = LoreGuiItem.setAction(pageNextEraMilitary, "OpenInventory");
				pageNextEraMilitary = LoreGuiItem.setActionData(pageNextEraMilitary, "invType", "showBuildInvPage");
				pageNextEraMilitary = LoreGuiItem.setActionData(pageNextEraMilitary, "pageName", "thirdEraMilitary");
				ItemStack infoMilitaryPanel = LoreGuiItem.build(ChatColor.RED+"Вооружение", Material.RED_STAINED_GLASS_PANE);
				military2.setItem(40, linealItem);
				military2.setItem(28, linealItem);
				military2.setItem(29, linealItem);
				military2.setItem(30, linealItem);
				military2.setItem(31, linealItem);
				military2.setItem(32, linealItem);
				military2.setItem(33, linealItem);
				military2.setItem(34, linealItem);
				military2.setItem(10, linealItem);
				military2.setItem(11, linealItem);
				military2.setItem(12, linealItem);
				military2.setItem(13, linealItem);
				military2.setItem(14, linealItem);
				military2.setItem(15, linealItem);
				military2.setItem(16, linealItem);
				military2.setItem(4, linealItem);
				military2.setItem(19, techItems.get("tech_fletching"));
				military2.setItem(21, techItems.get("tech_leather_refinement"));
				military2.setItem(23, techItems.get("tech_alloys"));
				military2.setItem(25, techItems.get("tech_sword_smithing"));
				military2.setItem(0, infoMilitaryPanel);
				military2.setItem(9, infoMilitaryPanel);
				military2.setItem(27, infoMilitaryPanel);
				military2.setItem(36, infoMilitaryPanel);
				military2.setItem(8, infoMilitaryPanel);
				military2.setItem(17, infoMilitaryPanel);
				military2.setItem(35, infoMilitaryPanel);
				military2.setItem(44, infoMilitaryPanel);
				military2.setItem(18, pageBackEraMilitary);
				military2.setItem(26, pageNextEraMilitary);
			}
			
			public void createMilitaryEraThird() {	
				ItemStack pageBackEraMilitary = LoreGuiItem.build(ChatColor.WHITE+"Предыдущая эра", Material.MAP);
				pageBackEraMilitary = LoreGuiItem.setAction(pageBackEraMilitary, "OpenInventory");
				pageBackEraMilitary = LoreGuiItem.setActionData(pageBackEraMilitary, "invType", "showBuildInvPage");
				pageBackEraMilitary = LoreGuiItem.setActionData(pageBackEraMilitary, "pageName", "secondEraMilitary");
				ItemStack pageNextEraMilitary = LoreGuiItem.build(ChatColor.WHITE+"Следующая эра", Material.MAP);
				pageNextEraMilitary = LoreGuiItem.setAction(pageNextEraMilitary, "OpenInventory");
				pageNextEraMilitary = LoreGuiItem.setActionData(pageNextEraMilitary, "invType", "showBuildInvPage");
				pageNextEraMilitary = LoreGuiItem.setActionData(pageNextEraMilitary, "pageName", "fourthEraMilitary");
				ItemStack infoMilitaryPanel = LoreGuiItem.build(ChatColor.RED+"Вооружение", Material.RED_STAINED_GLASS_PANE);
				military3.setItem(40, linealItem);
				military3.setItem(28, linealItem);
				military3.setItem(29, linealItem);
				military3.setItem(30, linealItem);
				military3.setItem(31, linealItem);
				military3.setItem(32, linealItem);
				military3.setItem(33, linealItem);
				military3.setItem(34, linealItem);
				military3.setItem(10, linealItem);
				military3.setItem(11, linealItem);
				military3.setItem(12, linealItem);
				military3.setItem(13, linealItem);
				military3.setItem(14, linealItem);
				military3.setItem(15, linealItem);
				military3.setItem(16, linealItem);
				military3.setItem(4, linealItem);
				military3.setItem(19, techItems.get("tech_tillering"));
				military3.setItem(21, techItems.get("tech_tanning"));
				military3.setItem(23, techItems.get("tech_chemical_bonding"));
				military3.setItem(25, techItems.get("tech_folded_steel"));
				military3.setItem(0, infoMilitaryPanel);
				military3.setItem(9, infoMilitaryPanel);
				military3.setItem(27, infoMilitaryPanel);
				military3.setItem(36, infoMilitaryPanel);
				military3.setItem(8, infoMilitaryPanel);
				military3.setItem(17, infoMilitaryPanel);
				military3.setItem(35, infoMilitaryPanel);
				military3.setItem(44, infoMilitaryPanel);
				military3.setItem(18, pageBackEraMilitary);
				military3.setItem(26, pageNextEraMilitary);
			}
			
			public void createMilitaryEraFourth() {	
				ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
				pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
				pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
				ItemStack pageBackEraMilitary = LoreGuiItem.build(ChatColor.WHITE+"Предыдущая эра", Material.MAP);
				pageBackEraMilitary = LoreGuiItem.setAction(pageBackEraMilitary, "OpenInventory");
				pageBackEraMilitary = LoreGuiItem.setActionData(pageBackEraMilitary, "invType", "showBuildInvPage");
				pageBackEraMilitary = LoreGuiItem.setActionData(pageBackEraMilitary, "pageName", "secondEraMilitary");
				ItemStack infoMilitaryPanel = LoreGuiItem.build(ChatColor.RED+"Вооружение", Material.RED_STAINED_GLASS_PANE);
				military4.setItem(37, linealItem);
				military4.setItem(38, linealItem);
				military4.setItem(39, linealItem);
				military4.setItem(40, linealItem);
				military4.setItem(41, linealItem);
				military4.setItem(42, linealItem);
				military4.setItem(43, linealItem);
				military4.setItem(19, linealItem);
				military4.setItem(20, linealItem);
				military4.setItem(21, linealItem);
				military4.setItem(22, linealItem);
				military4.setItem(23, linealItem);
				military4.setItem(24, linealItem);
				military4.setItem(25, linealItem);
				military4.setItem(4, linealItem);
				military4.setItem(28, techItems.get("tech_laminates"));
				military4.setItem(30, techItems.get("tech_composites"));
				military4.setItem(32, techItems.get("tech_forging"));
				military4.setItem(34, techItems.get("tech_tempering"));
				military4.setItem(13, techItems.get("tech_military_science"));
				military4.setItem(0, infoMilitaryPanel);
				military4.setItem(9, infoMilitaryPanel);
				military4.setItem(27, infoMilitaryPanel);
				military4.setItem(36, infoMilitaryPanel);
				military4.setItem(8, infoMilitaryPanel);
				military4.setItem(17, infoMilitaryPanel);
				military4.setItem(35, infoMilitaryPanel);
				military4.setItem(44, infoMilitaryPanel);
				military4.setItem(18, pageBackEraMilitary);
				military4.setItem(26, pageBack);
			}
			
            public void createIndustrialEraFirst() {			
				ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
				pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
				pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
				ItemStack pageNextEraIndustrial = LoreGuiItem.build(ChatColor.WHITE+"Следующая эра", Material.MAP);
				pageNextEraIndustrial = LoreGuiItem.setAction(pageNextEraIndustrial, "OpenInventory");
				pageNextEraIndustrial = LoreGuiItem.setActionData(pageNextEraIndustrial, "invType", "showBuildInvPage");
				pageNextEraIndustrial = LoreGuiItem.setActionData(pageNextEraIndustrial, "pageName", "secondEraIndustrial");
				
				ItemStack infoPanel = LoreGuiItem.build(ChatColor.AQUA+"Технологический прогресс", Material.LIGHT_BLUE_STAINED_GLASS_PANE);
				
				industrial1.setItem(30, linealItem);
				industrial1.setItem(31, linealItem);
				industrial1.setItem(32, linealItem);
				industrial1.setItem(12, linealItem);
				industrial1.setItem(14, linealItem);
				industrial1.setItem(40, techItems.get("tech_mining"));
				industrial1.setItem(21, techItems.get("tech_productivity"));
				industrial1.setItem(23, techItems.get("tech_masonry"));
				industrial1.setItem(5, techItems.get("tech_advanced_masonry"));
				industrial1.setItem(3, techItems.get("tech_sailing"));
				industrial1.setItem(0, infoPanel);
				industrial1.setItem(9, infoPanel);
				industrial1.setItem(27, infoPanel);
				industrial1.setItem(36, infoPanel);
				industrial1.setItem(8, infoPanel);
				industrial1.setItem(17, infoPanel);
				industrial1.setItem(35, infoPanel);
				industrial1.setItem(44, infoPanel);
				industrial1.setItem(18, pageBack);
				industrial1.setItem(26, pageNextEraIndustrial);
			}
            
            public void createIndustrialEraSecond() {	
				ItemStack pageBackEra = LoreGuiItem.build(ChatColor.WHITE+"Предыдущая эра", Material.MAP);
				pageBackEra = LoreGuiItem.setAction(pageBackEra, "OpenInventory");
				pageBackEra = LoreGuiItem.setActionData(pageBackEra, "invType", "showBuildInvPage");
				pageBackEra = LoreGuiItem.setActionData(pageBackEra, "pageName", "firstEraIndustrial");
				ItemStack pageNextEra = LoreGuiItem.build(ChatColor.WHITE+"Следующая эра", Material.MAP);
				pageNextEra = LoreGuiItem.setAction(pageNextEra, "OpenInventory");
				pageNextEra = LoreGuiItem.setActionData(pageNextEra, "invType", "showBuildInvPage");
				pageNextEra = LoreGuiItem.setActionData(pageNextEra, "pageName", "thirdEraIndustrial");
				ItemStack infoPanel = LoreGuiItem.build(ChatColor.AQUA+"Технологический прогресс", Material.LIGHT_BLUE_STAINED_GLASS_PANE);
				industrial2.setItem(38, linealItem);
				industrial2.setItem(40, linealItem);
				industrial2.setItem(42, linealItem);
				industrial2.setItem(22, linealItem);
				industrial2.setItem(24, linealItem);
				industrial2.setItem(4, linealItem);
				industrial2.setItem(6, linealItem);
				industrial2.setItem(29, techItems.get("tech_exploration"));
				industrial2.setItem(31, techItems.get("tech_taxation"));
				industrial2.setItem(33, techItems.get("tech_artillery"));
				industrial2.setItem(13, techItems.get("tech_efficiency"));
				industrial2.setItem(15, techItems.get("tech_advanced_artillery"));
				industrial2.setItem(0, infoPanel);
				industrial2.setItem(9, infoPanel);
				industrial2.setItem(27, infoPanel);
				industrial2.setItem(36, infoPanel);
				industrial2.setItem(8, infoPanel);
				industrial2.setItem(17, infoPanel);
				industrial2.setItem(35, infoPanel);
				industrial2.setItem(44, infoPanel);
				industrial2.setItem(18, infoPanel);
				industrial2.setItem(26, infoPanel);
				industrial2.setItem(18, pageBackEra);
				industrial2.setItem(26, pageNextEra);
			}
            
            public void createIndustrialEraThird() {	
				ItemStack pageBackEra = LoreGuiItem.build(ChatColor.WHITE+"Предыдущая эра", Material.MAP);
				pageBackEra = LoreGuiItem.setAction(pageBackEra, "OpenInventory");
				pageBackEra = LoreGuiItem.setActionData(pageBackEra, "invType", "showBuildInvPage");
				pageBackEra = LoreGuiItem.setActionData(pageBackEra, "pageName", "secondEraIndustrial");
				ItemStack pageNextEra = LoreGuiItem.build(ChatColor.WHITE+"Следующая эра", Material.MAP);
				pageNextEra = LoreGuiItem.setAction(pageNextEra, "OpenInventory");
				pageNextEra = LoreGuiItem.setActionData(pageNextEra, "invType", "showBuildInvPage");
				pageNextEra = LoreGuiItem.setActionData(pageNextEra, "pageName", "fourthEraIndustrial");
				ItemStack infoPanel = LoreGuiItem.build(ChatColor.AQUA+"Технологический прогресс", Material.LIGHT_BLUE_STAINED_GLASS_PANE);
				industrial3.setItem(23, linealItem);
				industrial3.setItem(24, linealItem);
				industrial3.setItem(6, linealItem);
				industrial3.setItem(40, linealItem);
				industrial3.setItem(22, linealItem);
				industrial3.setItem(21, linealItem);
				industrial3.setItem(20, linealItem);
				industrial3.setItem(4, linealItem);
				industrial3.setItem(2, linealItem);
				industrial3.setItem(15, techItems.get("tech_construction"));
				industrial3.setItem(31, techItems.get("tech_machinery"));
				industrial3.setItem(11, techItems.get("tech_advanced_efficiency"));
				industrial3.setItem(13, techItems.get("tech_scientific_method"));
				industrial3.setItem(0, infoPanel);
				industrial3.setItem(9, infoPanel);
				industrial3.setItem(27, infoPanel);
				industrial3.setItem(36, infoPanel);
				industrial3.setItem(8, infoPanel);
				industrial3.setItem(17, infoPanel);
				industrial3.setItem(35, infoPanel);
				industrial3.setItem(44, infoPanel);
				industrial3.setItem(18, infoPanel);
				industrial3.setItem(26, infoPanel);
				industrial3.setItem(18, pageBackEra);
				industrial3.setItem(26, pageNextEra);
			}
            
            public void createIndustrialEraFourth() {	
				ItemStack pageBackEra = LoreGuiItem.build(ChatColor.WHITE+"Предыдущая эра", Material.MAP);
				pageBackEra = LoreGuiItem.setAction(pageBackEra, "OpenInventory");
				pageBackEra = LoreGuiItem.setActionData(pageBackEra, "invType", "showBuildInvPage");
				pageBackEra = LoreGuiItem.setActionData(pageBackEra, "pageName", "thirdEraIndustrial");
				ItemStack pageNextEra = LoreGuiItem.build(ChatColor.WHITE+"Следующая эра", Material.MAP);
				pageNextEra = LoreGuiItem.setAction(pageNextEra, "OpenInventory");
				pageNextEra = LoreGuiItem.setActionData(pageNextEra, "invType", "showBuildInvPage");
				pageNextEra = LoreGuiItem.setActionData(pageNextEra, "pageName", "fifthEraIndustrial");
				ItemStack infoPanel = LoreGuiItem.build(ChatColor.AQUA+"Технологический прогресс", Material.LIGHT_BLUE_STAINED_GLASS_PANE);
				industrial4.setItem(39, linealItem);
				industrial4.setItem(41, linealItem);
				industrial4.setItem(21, linealItem);
				industrial4.setItem(23, linealItem);
				industrial4.setItem(3, linealItem);
				industrial4.setItem(14, linealItem);
				industrial4.setItem(5, linealItem);
				industrial4.setItem(32, techItems.get("tech_fertilizer"));
				industrial4.setItem(30, techItems.get("tech_electricity"));
				industrial4.setItem(12, techItems.get("tech_innovation"));
				industrial4.setItem(0, infoPanel);
				industrial4.setItem(9, infoPanel);
				industrial4.setItem(27, infoPanel);
				industrial4.setItem(36, infoPanel);
				industrial4.setItem(8, infoPanel);
				industrial4.setItem(17, infoPanel);
				industrial4.setItem(35, infoPanel);
				industrial4.setItem(44, infoPanel);
				industrial4.setItem(18, infoPanel);
				industrial4.setItem(26, infoPanel);
				industrial4.setItem(18, pageBackEra);
				industrial4.setItem(26, pageNextEra);
			}
            
            public void createIndustrialEraFifth() {	
            	ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
				pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
				pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
				ItemStack pageBackEra = LoreGuiItem.build(ChatColor.WHITE+"Предыдущая эра", Material.MAP);
				pageBackEra = LoreGuiItem.setAction(pageBackEra, "OpenInventory");
				pageBackEra = LoreGuiItem.setActionData(pageBackEra, "invType", "showBuildInvPage");
				pageBackEra = LoreGuiItem.setActionData(pageBackEra, "pageName", "fourthEraIndustrial");
				ItemStack infoPanel = LoreGuiItem.build(ChatColor.AQUA+"Технологический прогресс", Material.LIGHT_BLUE_STAINED_GLASS_PANE);
				industrial5.setItem(39, linealItem);
				industrial5.setItem(41, linealItem);
				industrial5.setItem(21, linealItem);
				industrial5.setItem(23, linealItem);
				industrial5.setItem(3, linealItem);
				industrial5.setItem(5, linealItem);
				industrial5.setItem(32, techItems.get("tech_biology"));
				industrial5.setItem(14, techItems.get("tech_refrigeration"));
				industrial5.setItem(30, techItems.get("tech_radio"));
				industrial5.setItem(12, techItems.get("tech_plastics"));
				industrial5.setItem(0, infoPanel);
				industrial5.setItem(9, infoPanel);
				industrial5.setItem(27, infoPanel);
				industrial5.setItem(36, infoPanel);
				industrial5.setItem(8, infoPanel);
				industrial5.setItem(17, infoPanel);
				industrial5.setItem(35, infoPanel);
				industrial5.setItem(44, infoPanel);
				industrial5.setItem(18, infoPanel);
				industrial5.setItem(26, infoPanel);
				industrial5.setItem(18, pageBackEra);
				industrial5.setItem(26, pageBack);
			}
            
            public void createEconomyEraFirst() {			
				ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
				pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
				pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
				ItemStack pageNextEra = LoreGuiItem.build(ChatColor.WHITE+"Следующая эра", Material.MAP);
				pageNextEra = LoreGuiItem.setAction(pageNextEra, "OpenInventory");
				pageNextEra = LoreGuiItem.setActionData(pageNextEra, "invType", "showBuildInvPage");
				pageNextEra = LoreGuiItem.setActionData(pageNextEra, "pageName", "secondEraEconomy");
				
				ItemStack infoPanel = LoreGuiItem.build(CivColor.LightGreen+"Экономика", Material.LIME_STAINED_GLASS_PANE);
				
				economy1.setItem(30, linealItem);
				economy1.setItem(31, linealItem);
				economy1.setItem(32, linealItem);
				economy1.setItem(11, linealItem);
				economy1.setItem(12, linealItem);
				economy1.setItem(13, linealItem);
				economy1.setItem(14, linealItem);
				economy1.setItem(15, linealItem);
				economy1.setItem(40, techItems.get("tech_agriculture"));
				economy1.setItem(21, techItems.get("tech_pottery"));
				economy1.setItem(23, techItems.get("tech_hunting"));
				economy1.setItem(2, techItems.get("tech_automation"));
				economy1.setItem(4, techItems.get("tech_trade"));
				economy1.setItem(6, techItems.get("tech_advanced_hunting"));
				economy1.setItem(0, infoPanel);
				economy1.setItem(9, infoPanel);
				economy1.setItem(27, infoPanel);
				economy1.setItem(36, infoPanel);
				economy1.setItem(8, infoPanel);
				economy1.setItem(17, infoPanel);
				economy1.setItem(35, infoPanel);
				economy1.setItem(44, infoPanel);
				economy1.setItem(18, pageBack);
				economy1.setItem(26, pageNextEra);
			}
            
            public void createEconomyEraSecond() {	
            	ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
				pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
				pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
            	ItemStack pageBackEra = LoreGuiItem.build(ChatColor.WHITE+"Предыдущая эра", Material.MAP);
				pageBackEra = LoreGuiItem.setAction(pageBackEra, "OpenInventory");
				pageBackEra = LoreGuiItem.setActionData(pageBackEra, "invType", "showBuildInvPage");
				pageBackEra = LoreGuiItem.setActionData(pageBackEra, "pageName", "firstEraEconomy");
				
				ItemStack infoPanel = LoreGuiItem.build(CivColor.LightGreen+"Экономика", Material.LIME_STAINED_GLASS_PANE);
				
				economy2.setItem(39, linealItem);
				economy2.setItem(41, linealItem);
				economy2.setItem(21, linealItem);
				economy2.setItem(23, linealItem);
				economy2.setItem(14, linealItem);
				economy2.setItem(3, linealItem);
				economy2.setItem(5, linealItem);
				economy2.setItem(30, techItems.get("tech_commerce"));
				economy2.setItem(32, techItems.get("tech_education"));
				economy2.setItem(12, techItems.get("tech_global_trade"));
				economy2.setItem(0, infoPanel);
				economy2.setItem(9, infoPanel);
				economy2.setItem(27, infoPanel);
				economy2.setItem(36, infoPanel);
				economy2.setItem(8, infoPanel);
				economy2.setItem(17, infoPanel);
				economy2.setItem(35, infoPanel);
				economy2.setItem(44, infoPanel);
				economy2.setItem(18, pageBackEra);
				economy2.setItem(26, pageBack);
			}
            
            public void createCultureEraFirst() {			
				ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
				pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
				pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
				ItemStack pageNextEra = LoreGuiItem.build(ChatColor.WHITE+"Следующая эра", Material.MAP);
				pageNextEra = LoreGuiItem.setAction(pageNextEra, "OpenInventory");
				pageNextEra = LoreGuiItem.setActionData(pageNextEra, "invType", "showBuildInvPage");
				pageNextEra = LoreGuiItem.setActionData(pageNextEra, "pageName", "secondEraCulture");
				
				ItemStack infoPanel = LoreGuiItem.build(ChatColor.LIGHT_PURPLE+"Культура", Material.MAGENTA_STAINED_GLASS_PANE);
				
				culture1.setItem(30, linealItem);
				culture1.setItem(31, linealItem);
				culture1.setItem(32, linealItem);
				culture1.setItem(12, linealItem);
				culture1.setItem(14, linealItem);
				culture1.setItem(40, techItems.get("tech_religion"));
				culture1.setItem(21, techItems.get("tech_code_of_laws"));
				culture1.setItem(23, techItems.get("tech_brewing"));
				culture1.setItem(3, techItems.get("tech_writing"));
				culture1.setItem(5, techItems.get("tech_priesthood"));
				culture1.setItem(0, infoPanel);
				culture1.setItem(9, infoPanel);
				culture1.setItem(27, infoPanel);
				culture1.setItem(36, infoPanel);
				culture1.setItem(8, infoPanel);
				culture1.setItem(17, infoPanel);
				culture1.setItem(35, infoPanel);
				culture1.setItem(44, infoPanel);
				culture1.setItem(18, pageBack);
				culture1.setItem(26, pageNextEra);
			}
            
            public void createCultureEraSecond() {			
            	ItemStack pageBackEra = LoreGuiItem.build(ChatColor.WHITE+"Предыдущая эра", Material.MAP);
				pageBackEra = LoreGuiItem.setAction(pageBackEra, "OpenInventory");
				pageBackEra = LoreGuiItem.setActionData(pageBackEra, "invType", "showBuildInvPage");
				pageBackEra = LoreGuiItem.setActionData(pageBackEra, "pageName", "firstEraCulture");
				ItemStack pageNextEra = LoreGuiItem.build(ChatColor.WHITE+"Следующая эра", Material.MAP);
				pageNextEra = LoreGuiItem.setAction(pageNextEra, "OpenInventory");
				pageNextEra = LoreGuiItem.setActionData(pageNextEra, "invType", "showBuildInvPage");
				pageNextEra = LoreGuiItem.setActionData(pageNextEra, "pageName", "thirdEraCulture");
				
				ItemStack infoPanel = LoreGuiItem.build(ChatColor.LIGHT_PURPLE+"Культура", Material.MAGENTA_STAINED_GLASS_PANE);
				
				culture2.setItem(39, linealItem);
				culture2.setItem(41, linealItem);
				culture2.setItem(21, linealItem);
				culture2.setItem(23, linealItem);
				culture2.setItem(3, linealItem);
				culture2.setItem(5, linealItem);
				culture2.setItem(30, techItems.get("tech_monarchy"));
				culture2.setItem(32, techItems.get("tech_fermentation"));
				culture2.setItem(12, techItems.get("tech_currency"));
				culture2.setItem(14, techItems.get("tech_malting"));
				culture2.setItem(0, infoPanel);
				culture2.setItem(9, infoPanel);
				culture2.setItem(27, infoPanel);
				culture2.setItem(36, infoPanel);
				culture2.setItem(8, infoPanel);
				culture2.setItem(17, infoPanel);
				culture2.setItem(35, infoPanel);
				culture2.setItem(44, infoPanel);
				culture2.setItem(18, pageBackEra);
				culture2.setItem(26, pageNextEra);
			}
            
            public void createCultureEraThird() {
            	ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
				pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
				pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
            	ItemStack pageBackEra = LoreGuiItem.build(ChatColor.WHITE+"Предыдущая эра", Material.MAP);
				pageBackEra = LoreGuiItem.setAction(pageBackEra, "OpenInventory");
				pageBackEra = LoreGuiItem.setActionData(pageBackEra, "invType", "showBuildInvPage");
				pageBackEra = LoreGuiItem.setActionData(pageBackEra, "pageName", "secondEraCulture");
				
				ItemStack infoPanel = LoreGuiItem.build(ChatColor.LIGHT_PURPLE+"Культура", Material.MAGENTA_STAINED_GLASS_PANE);
				
				culture3.setItem(30, linealItem);
				culture3.setItem(12, linealItem);
				culture3.setItem(39, techItems.get("tech_economics"));
				culture3.setItem(41, techItems.get("tech_alchemy"));
				culture3.setItem(21, techItems.get("tech_nationalism"));
				culture3.setItem(3, techItems.get("tech_archaeology"));
				culture3.setItem(0, infoPanel);
				culture3.setItem(9, infoPanel);
				culture3.setItem(27, infoPanel);
				culture3.setItem(36, infoPanel);
				culture3.setItem(8, infoPanel);
				culture3.setItem(17, infoPanel);
				culture3.setItem(35, infoPanel);
				culture3.setItem(44, infoPanel);
				culture3.setItem(18, pageBackEra);
				culture3.setItem(26, pageBack);
			}
            
            public void createWonders() {			
            	ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
				pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
				pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInv");
				ItemStack pageNext = LoreGuiItem.build(ChatColor.WHITE+"Следующая страница", Material.MAP);
				pageNext = LoreGuiItem.setAction(pageNext, "OpenInventory");
				pageNext = LoreGuiItem.setActionData(pageNext, "invType", "showBuildInvPage");
				pageNext = LoreGuiItem.setActionData(pageNext, "pageName", "wonders2");
				
				ItemStack infoPanel = LoreGuiItem.build(ChatColor.GOLD+"Чудеса света", Material.ORANGE_STAINED_GLASS_PANE);
				
				wonders.setItem(11, techItems.get("tech_colosseum"));
				wonders.setItem(29, techItems.get("tech_great_pyramid"));
				wonders.setItem(13, techItems.get("tech_hanging_gardens"));
				wonders.setItem(31, techItems.get("tech_mother_tree"));
				wonders.setItem(15, techItems.get("tech_colossus"));
				wonders.setItem(33, techItems.get("tech_notre_dame"));
				wonders.setItem(0, infoPanel);
				wonders.setItem(9, infoPanel);
				wonders.setItem(27, infoPanel);
				wonders.setItem(36, infoPanel);
				wonders.setItem(8, infoPanel);
				wonders.setItem(17, infoPanel);
				wonders.setItem(35, infoPanel);
				wonders.setItem(44, infoPanel);
				wonders.setItem(18, pageBack);
				wonders.setItem(26, pageNext);
			}
            
            public void createWondersTwo() {	
            	ItemStack pageBackСat = LoreGuiItem.build(ChatColor.WHITE+"Вернуться к категориям", Material.MAP);
            	pageBackСat = LoreGuiItem.setAction(pageBackСat, "OpenInventory");
				pageBackСat = LoreGuiItem.setActionData(pageBackСat, "invType", "showBuildInv");
            	ItemStack pageBack = LoreGuiItem.build(ChatColor.WHITE+"Предыдущая страница", Material.MAP);
				pageBack = LoreGuiItem.setAction(pageBack, "OpenInventory");
				pageBack = LoreGuiItem.setActionData(pageBack, "invType", "showBuildInvPage");
				pageBack = LoreGuiItem.setActionData(pageBack, "pageName", "wonders");
				
				ItemStack infoPanel = LoreGuiItem.build(ChatColor.GOLD+"Чудеса света", Material.ORANGE_STAINED_GLASS_PANE);
				
				wonders2.setItem(11, techItems.get("tech_grand_ship_ingermanland"));
				wonders2.setItem(29, techItems.get("tech_great_lighthouse"));
				wonders2.setItem(13, techItems.get("tech_chichen_itza"));
				wonders2.setItem(31, techItems.get("tech_globe_theatre"));
				wonders2.setItem(15, techItems.get("tech_great_library"));
				wonders2.setItem(33, techItems.get("tech_council_of_eight"));
				wonders2.setItem(0, infoPanel);
				wonders2.setItem(9, infoPanel);
				wonders2.setItem(27, infoPanel);
				wonders2.setItem(36, infoPanel);
				wonders2.setItem(8, infoPanel);
				wonders2.setItem(17, infoPanel);
				wonders2.setItem(35, infoPanel);
				wonders2.setItem(44, infoPanel);
				wonders2.setItem(18, pageBack);
				wonders2.setItem(26, pageBackСat);
			}
		}
		
		techs techs = new techs();
		techs.createMilitaryEraFirst();
		techs.createMilitaryEraSecond();
		techs.createMilitaryEraThird();
		techs.createMilitaryEraFourth();
		techs.createIndustrialEraFirst();
		techs.createIndustrialEraSecond();
		techs.createIndustrialEraThird();
		techs.createIndustrialEraFourth();
		techs.createIndustrialEraFifth();
		techs.createEconomyEraFirst();
		techs.createEconomyEraSecond();
		techs.createCultureEraFirst();
		techs.createCultureEraSecond();
		techs.createCultureEraThird();
		techs.createWonders();
		techs.createWondersTwo();
		
		if ( CivSettings.techs.get("tech_enlightenment").isAvailable(civ) ) {
			inv.clear();
			inv.setItem(13, techItems.get("tech_enlightenment"));
		}
		
		if ( civ.hasTechnology("tech_enlightenment") ) {
			inv.clear();
			inv.setItem(13, techItems.get("tech_enlightenment"));
		}
		
		/* Open inventory */
		player.openInventory(inv);
	}
	
}
