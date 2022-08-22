package ru.tailsxcraft.civcraft.loregui;

import gpl.AttributeUtil;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigIngredient;
import ru.tailsxcraft.civcraft.config.ConfigMaterialCategory;
import ru.tailsxcraft.civcraft.config.ConfigTech;
import ru.tailsxcraft.civcraft.lorestorage.LoreCraftableMaterial;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItemListener;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.util.ItemManager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShowRecipe implements GuiAction {

	public static final int START_OFFSET = LoreGuiItem.INV_ROW_COUNT + 3;
	
	public ItemStack getIngredItem(ConfigIngredient ingred, Inventory recInv) {
		String name;
		String message;
		ItemStack entryStack;
		if (ingred.custom_id == null) {
			name = ItemManager.getMaterialData(ingred.type_id, ingred.data).toString();
			message = "Vanilla Item";
			entryStack = LoreGuiItem.build(name, ingred.type_id, message);
		} else {
			LoreCraftableMaterial cmat = LoreCraftableMaterial.getCraftMaterialFromId(ingred.custom_id);
			name = cmat.getName();
			if (cmat.getConfigMaterial().ingredients == null) {
				message = CivSettings.localize.localizedString("loreGui_recipes_notCraftable");	
			} else {	
				message = CivSettings.localize.localizedString("loreGui_recipes_clickForRecipe");
			}
			entryStack = LoreCraftableMaterial.spawn(cmat);
			entryStack = LoreGuiItem.asGuiItem(entryStack);
			entryStack = LoreGuiItem.setAction(entryStack, "ShowRecipe");
			entryStack = LoreGuiItem.setActionData(entryStack, "backInventory", recInv.toString());
			AttributeUtil attrs = new AttributeUtil(entryStack);
			attrs.addLore(message);
			entryStack = attrs.getStack();
		}
		return entryStack;
	}
	
	public void buildCraftTableBorder(Inventory recInv) {
		int offset = 2;
		ItemStack stack;
	
		stack = LoreGuiItem.build("Craft Table Border", ItemManager.getType(Material.CRAFTING_TABLE), "");
		
		for (int y = 0; y <= 4; y++) {
			for (int x = 0; x <= 4; x++) {
				if (x == 0 || x == 4 || y == 0 || y == 4) {
					recInv.setItem(offset+(y*LoreGuiItem.INV_ROW_COUNT)+x, stack);
				}
			}
		}		
	}
	
	public void buildInfoBar(LoreCraftableMaterial craftMat, Inventory recInv, Player player) {
		int offset = 0;
		ItemStack stack = null;
		
		if (craftMat.getConfigMaterial().required_tech != null) {
			Resident resident = CivGlobal.getResident(player);
			ConfigTech tech = CivSettings.techs.get(craftMat.getConfigMaterial().required_tech);
			if (tech != null) {
			
				if (resident.hasTown() && resident.getCiv().hasTechnology(craftMat.getConfigMaterial().required_tech)) {
					stack = LoreGuiItem.build(CivSettings.localize.localizedString("loreGui_recipes_requiredTech"), Material.EMERALD_BLOCK, tech.name);
				} else {
					stack = LoreGuiItem.build(CivSettings.localize.localizedString("loreGui_recipes_requiredTech"), Material.REDSTONE_BLOCK, tech.name);
				}
			}
			
			if (stack != null) {
				recInv.setItem(offset+0, stack);
			}
		}
		
		if (craftMat.isShaped()) {
			stack = LoreGuiItem.build(CivSettings.localize.localizedString("loreGui_recipes_shaped"), Material.HOPPER, "");
		} else {
			stack = LoreGuiItem.build(CivSettings.localize.localizedString("loreGui_recipes_unshaped"), Material.COAL, "");
		}
		offset += LoreGuiItem.INV_ROW_COUNT;
		recInv.setItem(offset+0, stack);
		

	}
	
	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
		
		LoreCraftableMaterial craftMat = LoreCraftableMaterial.getCraftMaterial(stack);
		if (craftMat == null || craftMat.getConfigMaterial().ingredients == null) {
			/* Do nothing for now. */
			return;
		}
		
		String title = craftMat.getName()+" "+CivSettings.localize.localizedString("loreGui_recipes_guiHeading");
		if (title.length() > 32) {
			title = title.substring(0, 32);
		}
		
		Inventory recInv = Bukkit.getServer().createInventory(player, LoreGuiItem.MAX_INV_SIZE, title);
		if (craftMat.isShaped()) {		
			int offset = START_OFFSET;
			for (String line : craftMat.getConfigMaterial().shape) {
				for (int i = 0; i < line.toCharArray().length; i++) {
					ConfigIngredient ingred = null;
					for (ConfigIngredient in : craftMat.getConfigMaterial().ingredients.values()) {
						if (in.letter.equalsIgnoreCase(String.valueOf(line.toCharArray()[i]))) {
							ingred = in;
							break;
						}
					}
					
					if (ingred != null) {
						recInv.setItem(i+offset, getIngredItem(ingred, recInv));
					}
				}
				offset += LoreGuiItem.INV_ROW_COUNT;
			}
		} else {
			int x = 0;
			int offset = START_OFFSET;
			for (ConfigIngredient ingred : craftMat.getConfigMaterial().ingredients.values()) {
				if (ingred != null) {				
					for (int i = 0; i < ingred.count; i++) {						
						recInv.setItem(x+offset, getIngredItem(ingred, recInv));
						
						x++;
						if (x >= 3) {
							x = 0;
							offset += LoreGuiItem.INV_ROW_COUNT;
						}
					}
				}
			}
		}
		
		String backInventory = LoreGuiItem.getActionData(stack, "backInventory");
		if (backInventory != null) {
			Inventory inv = LoreGuiItemListener.guiInventories.get(backInventory);
			ItemStack backButton = LoreGuiItem.build(CivSettings.localize.localizedString("loreGui_recipes_back"), ItemManager.getType(Material.MAP), CivSettings.localize.localizedString("loreGui_recipes_back"));
			backButton = LoreGuiItem.setAction(backButton, "OpenInventory");
			backButton = LoreGuiItem.setActionData(backButton, "invType", "showGuiInv");
			backButton = LoreGuiItem.setActionData(backButton, "invName", backInventory);
			recInv.setItem(LoreGuiItem.MAX_INV_SIZE-1, backButton);
		} else {
			ConfigMaterialCategory cat = ConfigMaterialCategory.getCategory(craftMat.getConfigMaterial().categoryCivColortripped); 
			if (cat != null) {					
				ItemStack backButton = LoreGuiItem.build(CivSettings.localize.localizedString("loreGui_recipes_back"), ItemManager.getType(Material.MAP), CivSettings.localize.localizedString("loreGui_recipes_backMsg")+" "+cat.name);
				backButton = LoreGuiItem.setAction(backButton, "OpenInventory");
				backButton = LoreGuiItem.setActionData(backButton, "invType", "showGuiInv");
				backButton = LoreGuiItem.setActionData(backButton, "invName", cat.name+" Recipes");
				recInv.setItem(LoreGuiItem.MAX_INV_SIZE-1, backButton);
			}
		}
		
		LoreGuiItemListener.guiInventories.put(title, recInv);
		buildCraftTableBorder(recInv);
		buildInfoBar(craftMat, recInv, player);
		player.openInventory(recInv);
	}

}
