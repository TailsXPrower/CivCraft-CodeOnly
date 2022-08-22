package ru.tailsxcraft.civcraft.tutorial;

import gpl.AttributeUtil;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigMaterial;
import ru.tailsxcraft.civcraft.config.ConfigMaterialCategory;
import ru.tailsxcraft.civcraft.lorestorage.LoreCraftableMaterial;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItemListener;
import ru.tailsxcraft.civcraft.lorestorage.LoreMaterial;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.civcraft.util.ItemManager;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CivTutorial {

	public static Inventory tutorialInventory = null;
	public static Inventory craftingHelpInventory = null;
	public static Inventory guiInventory = null;
	public static final int MAX_CHEST_SIZE = 6;
	
	public static void showTutorialInventory(Player player) {	
		if (tutorialInventory == null) {
			tutorialInventory = Bukkit.getServer().createInventory(player, 9*3, CivSettings.localize.localizedString("tutorial_gui_heading"));
		
	
			tutorialInventory.addItem(LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_workbench_heading"), ItemManager.getType(Material.CRAFTING_TABLE), 
				ChatColor.RESET+CivSettings.localize.localizedString("tutorial_workbench_Line1"),
				ChatColor.RESET+CivSettings.localize.localizedString("tutorial_workbench_Line2"),
				ChatColor.RESET+CivSettings.localize.localizedString("tutorial_workbench_Line3"),
				ChatColor.RESET+CivSettings.localize.localizedString("tutorial_workbench_Line4"),
				ChatColor.RESET+CivSettings.localize.localizedString("tutorial_workbench_Line5"),
				ChatColor.RESET+CivColor.LightGreen+CivSettings.localize.localizedString("tutorial_workbench_Line6")
				));
		
			tutorialInventory.addItem(LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_compass_heading"), ItemManager.getType(Material.COMPASS), 
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_compass_Line1"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_compass_Line2"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_compass_Line3"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_compass_Line4"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_compass_Line5")
					));
			
			tutorialInventory.addItem(LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_diamondOre_heading"), ItemManager.getType(Material.DIAMOND_ORE), 
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_diamondOre_Line1"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_diamondOre_Line2"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_diamondOre_Line3"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_diamondOre_Line4"),
					ChatColor.RESET+CivSettings.localize.localizedString("var_tutorial_diamondOre_Line5",CivSettings.CURRENCY_NAME),
					ChatColor.RESET+CivSettings.localize.localizedString("var_tutorial_diamondOre_Line6",CivSettings.CURRENCY_NAME)
					));
			
			tutorialInventory.addItem(LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_Fence_heading"), ItemManager.getType(Material.OAK_FENCE), 
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_Fence_Line1"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_Fence_Line2"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_Fence_Line3"),
					ChatColor.RESET+CivSettings.localize.localizedString("var_tutorial_Fence_Line4",CivSettings.CURRENCY_NAME),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_Fence_Line5"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_Fence_Line6"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_Fence_Line7")
					));
			
			tutorialInventory.addItem(LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_goldHelmet_heading"), ItemManager.getType(Material.GOLDEN_HELMET), 
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_goldHelmet_Line1"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_goldHelmet_Line2"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_goldHelmet_Line3"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_goldHelmet_Line4"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_goldHelmet_Line5"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_goldHelmet_Line6"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_goldHelmet_Line7")
					));
			
			if (CivGlobal.isCasualMode()) {
				tutorialInventory.addItem(LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_firework_heading"), ItemManager.getType(Material.FIREWORK_ROCKET), 
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_firework_Line1"),
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_firework_Line2"),
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_firework_Line3"),
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_firework_Line4"),
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_firework_Line5")
						));
			} else {
				tutorialInventory.addItem(LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_ironSword_heading"), ItemManager.getType(Material.IRON_SWORD), 
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_ironSword_Line1"),
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_ironSword_Line2"),
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_ironSword_Line3"),
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_ironSword_Line4"),
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_ironSword_Line5"),
						ChatColor.RESET+CivSettings.localize.localizedString("tutorial_ironSword_Line6")
						));
			}
			
			tutorialInventory.setItem(8, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_bookAndQuill_heading"), ItemManager.getType(Material.WRITABLE_BOOK), 
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_bookAndQuill_Line1"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_bookAndQuill_Line2"),
					ChatColor.RESET+CivColor.LightGreen+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_bookAndQuill_Line3"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_bookAndQuill_Line4")
					));
			
			tutorialInventory.setItem(9, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_campQuest_heading"), ItemManager.getType(Material.WRITABLE_BOOK), 
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_campQuest_Line1"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_campQuest_Line2"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_campQuest_Line3"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_campQuest_Line4")
					));
						
			tutorialInventory.setItem(10, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_civQuest_heading"), ItemManager.getType(Material.WRITABLE_BOOK), 
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_civQuest_Line1"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_civQuest_Line2"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_civQuest_Line3"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_civQuest_Line4"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_civQuest_Line5")
					));
			
			
			tutorialInventory.setItem(11, LoreGuiItem.build(CivColor.LightBlue+ChatColor.BOLD+CivSettings.localize.localizedString("tutorial_needRecipe_heading"), ItemManager.getType(Material.WRITABLE_BOOK), 
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_needRecipe_Line1"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_needRecipe_Line2"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_needRecipe_Line3"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_needRecipe_Line4"),
					ChatColor.RESET+CivSettings.localize.localizedString("tutorial_needRecipe_Line5")
					));
			
			for (ConfigMaterialCategory cat : ConfigMaterialCategory.getCategories()) {
			for (ConfigMaterial mat : cat.materials.values()) {
				if (mat.id.equals("mat_found_civ"))
				{
				ItemStack stack = getInfoBookForItem(mat.id);
				if (stack != null) {
					stack = LoreGuiItem.setAction(stack, "TutorialRecipe");
					tutorialInventory.setItem(19,LoreGuiItem.asGuiItem(stack));
				}
				} else if (mat.id.equals("mat_found_camp")) {
					ItemStack stack = getInfoBookForItem(mat.id);
					if (stack != null) {
						stack = LoreGuiItem.setAction(stack, "TutorialRecipe");
						tutorialInventory.setItem(18,LoreGuiItem.asGuiItem(stack));
					}
				}
			}
			}
			
			/* Add back buttons. */
			ItemStack backButton = LoreGuiItem.build("Back", ItemManager.getType(Material.MAP), CivSettings.localize.localizedString("tutorial_lore_backToCategories"));
			backButton = LoreGuiItem.setAction(backButton, "OpenInventory");
			backButton = LoreGuiItem.setActionData(backButton, "invType", "showGuiInv");
			backButton = LoreGuiItem.setActionData(backButton, "invName", CivSettings.localize.localizedString("tutorial_lore_CivcraftInfo"));
			tutorialInventory.setItem(26, backButton);
		
			LoreGuiItemListener.guiInventories.put(CivSettings.localize.localizedString("tutorial_gui_heading"), tutorialInventory);
		}
		
		if (player != null && player.isOnline() && player.isValid()) {
			player.openInventory(tutorialInventory);	
		}
	}
	
	public static ItemStack getInfoBookForItem(String matID) {
		LoreCraftableMaterial loreMat = LoreCraftableMaterial.getCraftMaterialFromId(matID);
		ItemStack stack = LoreMaterial.spawn(loreMat);
							
		if (!loreMat.isCraftable()) {
			return null;
		}
		
		AttributeUtil attrs = new AttributeUtil(stack);
		attrs.removeAll(); /* Remove all attribute modifiers to prevent them from displaying */
		LinkedList<String> lore = new LinkedList<String>();
		
		lore.add(""+ChatColor.RESET+ChatColor.BOLD+ChatColor.GOLD+CivSettings.localize.localizedString("tutorial_clickForRecipe"));
		
		attrs.setLore(lore);				
		stack = attrs.getStack();
		return stack;
	}
	
	public static void showCraftingHelp(Player player) {
		if (craftingHelpInventory == null) {
			craftingHelpInventory = Bukkit.getServer().createInventory(player, MAX_CHEST_SIZE*9, CivSettings.localize.localizedString("tutorial_customRecipesHeading"));

			/* Build the Category Inventory. */
			for (ConfigMaterialCategory cat : ConfigMaterialCategory.getCategories()) {
				if (cat.craftableCount == 0) {
					continue;
				}

				Material identifier;
				if (cat.name.contains("Рыб")) {
					identifier = Material.COD;
				}
				else if (cat.name.contains("Катализаторы")) {
					identifier = Material.BOOK;
				}
				else if (cat.name.contains("Амуниция")) {
					identifier = Material.IRON_SWORD;
				}
				else if (cat.name.contains("Материалы")) {
					identifier = Material.OAK_SLAB;
				}
				else if (cat.name.contains("Инструменты")) {
					identifier = Material.IRON_SHOVEL;
				}
				else if (cat.name.contains("Eggs")) {
					identifier = Material.VILLAGER_SPAWN_EGG;
				}
				else {
					identifier = Material.WRITTEN_BOOK;
				}
				ItemStack infoRec = LoreGuiItem.build(cat.name, 
						identifier,  
						CivColor.LightBlue+cat.materials.size()+" "+CivSettings.localize.localizedString("tutorial_lore_items"),
						CivColor.Gold+CivSettings.localize.localizedString("tutorial_lore_clickToOpen"));
						infoRec = LoreGuiItem.setAction(infoRec, "OpenInventory");
						infoRec = LoreGuiItem.setActionData(infoRec, "invType", "showGuiInv");
						infoRec = LoreGuiItem.setActionData(infoRec, "invName", cat.name+" "+CivSettings.localize.localizedString("tutorial_lore_recipes"));
						
						craftingHelpInventory.addItem(infoRec);
						
						
				Inventory inv = Bukkit.createInventory(player, LoreGuiItem.MAX_INV_SIZE, cat.name+" "+CivSettings.localize.localizedString("tutorial_lore_recipes"));
				for (ConfigMaterial mat : cat.materials.values()) {
					if ( mat.id.contains("_alt")) continue;
					ItemStack stack = getInfoBookForItem(mat.id);
					if (stack != null) {
						stack = LoreGuiItem.setAction(stack, "ShowRecipe");
						inv.addItem(LoreGuiItem.asGuiItem(stack));
					}
				}
				
				/* Add back buttons. */
				ItemStack backButton = LoreGuiItem.build("Back", ItemManager.getType(Material.MAP), CivSettings.localize.localizedString("tutorial_lore_backToCategories"));
				backButton = LoreGuiItem.setAction(backButton, "OpenInventory");
				backButton = LoreGuiItem.setActionData(backButton, "invType", "showCraftingHelp");
				inv.setItem(LoreGuiItem.MAX_INV_SIZE-1, backButton);
				
				LoreGuiItemListener.guiInventories.put(cat.name+" "+CivSettings.localize.localizedString("tutorial_lore_recipes"), inv);
			}
			
			/* Add back buttons. */
			ItemStack backButton = LoreGuiItem.build("Back", ItemManager.getType(Material.MAP), CivSettings.localize.localizedString("tutorial_lore_backToCategories"));
			backButton = LoreGuiItem.setAction(backButton, "OpenInventory");
			backButton = LoreGuiItem.setActionData(backButton, "invType", "showGuiInv");
			backButton = LoreGuiItem.setActionData(backButton, "invName", "Информация о CivCraft");
			craftingHelpInventory.setItem(LoreGuiItem.MAX_INV_SIZE-1, backButton);
			
			LoreGuiItemListener.guiInventories.put(CivSettings.localize.localizedString("tutorial_customRecipesHeading"), craftingHelpInventory);
		}
		
		player.openInventory(craftingHelpInventory);
	}
	
	public static void spawnGuiBook(Player player) {
		if (guiInventory == null) {
			guiInventory = Bukkit.getServer().createInventory(player, 5*9, "Информация о CivCraft");

			ItemStack infoRec = LoreGuiItem.build(CivColor.Gold+"Что такое CivCraft?", Material.WRITABLE_BOOK, new String[]{CivColor.White+
					"CivCraft - это мир, в котором победит лишь сильнейших из всех!",
					CivColor.White+"Вам предстоит сражаться за право быть великим правителем мира!",
					CivColor.White+"Разбейте Лагерь, возведите Цивилизацию и приведите её к мировому господству!",
					CivColor.White+"Сражайтесь за право быть выше всех на пьедестале почёта!",
					CivColor.White+"Сможете ли вы доказать, что ваша Цивилизация имеет право быть самой великой или же нет?"});
			guiInventory.setItem(4, infoRec);
			
			ItemStack infoRec2 = LoreGuiItem.build(CivColor.Red+"Война", Material.IRON_SWORD, new String[]{CivColor.White+
					"Война - время, когда у вас есть возможность показать своё превосходство над другой Цивилизацией.",
					CivColor.White+"Во время войны вы можете захватывать Города и даже целые Цивилизации!",
					CivColor.White+"Война проходит каждую субботу с 18:00 до 20:00 по МСК."});
			guiInventory.setItem(15, infoRec2);
			
			ItemStack infoRec3 = LoreGuiItem.build(ChatColor.DARK_PURPLE+"Цивилизация", Material.WHITE_BANNER, new String[]{CivColor.White+ 
					"Цивилизация - это место, которое станет вашим вторым испытанием, как для предводителя!",
					CivColor.White+"Вам предстоит открывать новые технологии, строить различные здания, а также великолепные Чудеса Света.",
					CivColor.White+"Развивайте свою Цивилизацию, основывая новые Города.",
					CivColor.White+"Считайте каждую копейку в казне Цивилизации, станьте научным стратегом или поведите за собой целую армию!"});
			guiInventory.setItem(11, infoRec3);
			
			ItemStack infoRec4 = LoreGuiItem.build(CivColor.LightBlue+"Ресурсы", Material.DIAMOND, new String[]{CivColor.White+
					"В мире CivCraft, также как и в обычном мире, можно встретить различные руды.",
					CivColor.White+"Но из некоторых таких руд могут выпасть особые ресурсы или же их можно добыть при помощи рыбалки.",
					CivColor.White+"Все эти ресурсы используются для крафта особых предметов из мира CivCraft.",
					CivColor.White+"Каждый такой предмет имеет какие-то свойства или обладает определёнными характеристиками."});
			guiInventory.setItem(40, infoRec4);
			
			ItemStack infoRec5 = LoreGuiItem.build(CivColor.Green+"Мобы", Material.ZOMBIE_SPAWN_EGG, new String[]{CivColor.White+
					"В обычном мире можно встретить различных существ, включая и мирных животных.",
					CivColor.White+"Чтобы облегчить рутину крафтов CivCraft-предметов, мир наполнен различными \"Спавнерами\".",
					CivColor.White+"\"Спавнеры\" создают особых враждебных монстров, которые обладают определёнными характеристиками.",
					CivColor.White+"Также с них вы сможете добыть либо уже готовые части для Брони или Оружия, или же их составляющие."});
			guiInventory.setItem(33, infoRec5);
			
			ItemStack infoRec6 = LoreGuiItem.build(CivColor.Yellow+"Лагерь", Material.CAMPFIRE, new String[]{CivColor.White+
					"Лагерь - это самое первое, о чём вы должны задуматься при первом заходе на сервер.",
					CivColor.White+"Это место, где вы сможете собрать тех, кто будет с вами до самого конца.",
					CivColor.White+"Получится ли у вас доказать, что вы тот, кто сможет привести своё племя к небывалым высотам?"});
			guiInventory.setItem(29, infoRec6);
			
			ItemStack craftRec = LoreGuiItem.build(CivColor.White+"Рецепты крафтов", 
					ItemManager.getType(Material.CRAFTING_TABLE), 
				    CivColor.Gold+"Нажмите, чтобы просмотреть");
			craftRec = LoreGuiItem.setAction(craftRec, "OpenInventory");
			craftRec = LoreGuiItem.setActionData(craftRec, "invType", "showCraftingHelp");
			guiInventory.setItem(22, craftRec);
			
			LoreGuiItemListener.guiInventories.put("Информация о CivCraft", guiInventory);
		}
		
		player.openInventory(guiInventory);

	}
	
	
}
