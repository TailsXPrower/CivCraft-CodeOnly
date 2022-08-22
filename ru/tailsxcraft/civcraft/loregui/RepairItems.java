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
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.interactive.InteractiveRepairItem;
import ru.tailsxcraft.civcraft.items.components.RepairCost;
import ru.tailsxcraft.civcraft.loreenhancements.LoreEnhancement;
import ru.tailsxcraft.civcraft.lorestorage.LoreCraftableMaterial;
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

public class RepairItems implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
	    if ( event.getView().getTitle().equalsIgnoreCase("Починка")) {
	    	Inventory inv = event.getInventory();
	    	double totalCost = 0;
	    	ItemStack[] items = inv.getContents();
	    	List<ItemStack> repairedItems = new ArrayList<ItemStack>();
	    	for ( int i = 0; i < 8; i++ ) {
	    		ItemStack item = items[i];
	    		if ( item == null || item.getType() == Material.AIR ) continue;
				
				if (item.getType().getMaxDurability() == 0) {
					continue;
				}
				
				if (item.getDurability() == 0) {
					continue;
				}
				
				LoreCraftableMaterial craftMat = LoreCraftableMaterial.getCraftMaterial(item);
				if (craftMat == null) {
					continue;
				}
				
				try {
					if (craftMat.hasComponent("RepairCost")) {
						RepairCost repairCost = (RepairCost)craftMat.getComponent("RepairCost");
						totalCost += repairCost.getDouble("value");
					} else {
						double baseTierRepair = CivSettings.getDouble(CivSettings.structureConfig, "barracks.base_tier_repair");
						double tierDamp = CivSettings.getDouble(CivSettings.structureConfig, "barracks.tier_damp");
						double tierCost = Math.pow((craftMat.getConfigMaterial().tier), tierDamp);				
						double fromTier = Math.pow(baseTierRepair, tierCost);				
						totalCost += Math.round(fromTier+0);
					}
					repairedItems.add(item);
					continue;
					
				} catch (InvalidConfiguration e) {
					e.printStackTrace();
				}
	    	}
	    	
	    	Resident resident = CivGlobal.getResident(player);
			
			if (!resident.getTreasury().hasEnough(totalCost)) {
				player.closeInventory();
				CivMessage.send(player, CivColor.Red+"У вас недостаточно денег!");
				return;
			}
			
			resident.getTreasury().withdraw(totalCost);
			for ( ItemStack item : repairedItems ) {
				item.setDurability((short)0);
			}
	    	
	    	CivMessage.sendSuccess(player, "Вещи были починены за "+totalCost+" "+CivSettings.CURRENCY_NAME);
	    }
	}



}
