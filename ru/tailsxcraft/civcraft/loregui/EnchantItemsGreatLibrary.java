package ru.tailsxcraft.civcraft.loregui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import gpl.AttributeUtil;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigEnchant;
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

public class EnchantItemsGreatLibrary implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
	    if ( event.getView().getTitle().equalsIgnoreCase("Александрийская Библиотека")) {
	    	Inventory inv = event.getInventory();
	    	Resident resident = CivGlobal.getResident(player);
	    	HashMap<ItemStack, Integer> itemPositions = new HashMap<ItemStack, Integer>();
	    	List<ItemStack> items = new ArrayList<ItemStack>();
	    	List<ItemStack> customItems = new ArrayList<ItemStack>();
	    	ConfigEnchant configEnchant = CivSettings.enchants.get(LoreGuiItem.getActionData(stack, "enchantId"));
	    	Enchantment enchant = null;
	    	switch (configEnchant.enchant_id) {
			case "FIRE_ASPECT":
				enchant = Enchantment.getByKey(NamespacedKey.minecraft("fire_aspect"));
				break;
			case "PROTECTION_FIRE":
				enchant = Enchantment.getByKey(NamespacedKey.minecraft("fire_protection"));
				break;
			case "ARROW_FIRE":
				enchant = Enchantment.getByKey(NamespacedKey.minecraft("flame"));
				break;
	    	}
	    	for ( int i = 9; i < 18; i++) {
	    		ItemStack item = inv.getItem(i);
	    		if ( item == null || item.getType() == Material.AIR ) continue;
	    		if ( enchant == null ) {
	    			switch (item.getType()) {
					case WOODEN_PICKAXE:
					case STONE_PICKAXE:
					case IRON_PICKAXE:
					case DIAMOND_PICKAXE:
					case GOLDEN_PICKAXE:
						if (!LoreMaterial.isCustom(item)) {					
							return;
						}
						
						if (LoreMaterial.hasEnhancement(item, configEnchant.enchant_id)) {
							return;
						}
						
		    			AttributeUtil atrs = new AttributeUtil(item);
		    			atrs.setCivCraftProperty("itemCode", item.getItemMeta().getDisplayName()+i);
		    			ItemStack newItem = atrs.getStack();
		    			itemPositions.put(newItem, i);
		    			customItems.add(newItem);
						break;
					default:
						continue;
		    	    }
	    			continue;
	    		}
	    		
	    		if (!enchant.canEnchantItem(item)) {
    				continue;
    			}
	    		
	    		CivLog.info("Adding item");
	    		
	    		items.add(item);
	        } 
	    	
	    	
	    	if ( enchant != null ) {
	    		if ( items.size() == 0 ) return;
	    		
	    		if (!resident.getTreasury().hasEnough(items.size()*configEnchant.cost)) {
			    	return;
			    }
	    		
	    		resident.getTreasury().withdraw(items.size()*configEnchant.cost);
	    		
	    		for ( ItemStack item : items ) {
    				if ( LoreGuiItem.getActionData(stack, "enchantId").equalsIgnoreCase("ench_fire_aspect")) {
        				item.addEnchantment(enchant, 2);
        			} else if ( LoreGuiItem.getActionData(stack, "enchantId").equalsIgnoreCase("ench_fire_protection")) {
        				item.addEnchantment(enchant, 3);
        			} else if ( LoreGuiItem.getActionData(stack, "enchantId").equalsIgnoreCase("ench_flame")) {
        				item.addEnchantment(enchant, 1);
        			}	
    			}		
	    	} else {
	    		if ( customItems.size() == 0 ) return;
	    		
	    		if (!resident.getTreasury().hasEnough(customItems.size()*configEnchant.cost)) {
					return;
				}
	    		
	    		resident.getTreasury().withdraw(configEnchant.cost);
	    		
	    		for ( ItemStack item : customItems ) {    			
					int itemPosition = itemPositions.get(item);
					ItemStack newItem = LoreMaterial.addEnhancement(item, LoreEnhancement.enhancements.get(configEnchant.enchant_id));
					AttributeUtil atr = new AttributeUtil(newItem);
					atr.removeCivCraftProperty("itemCode");
	    			inv.setItem(itemPosition, atr.getStack());
    			}		
    	    }
	    }
    }
}
