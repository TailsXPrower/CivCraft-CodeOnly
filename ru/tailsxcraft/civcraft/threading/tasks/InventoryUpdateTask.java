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
package ru.tailsxcraft.civcraft.threading.tasks;


import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.items.components.RepairCost;
import ru.tailsxcraft.civcraft.lorestorage.LoreCraftableMaterial;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.threading.CivAsyncTask;
import ru.tailsxcraft.civcraft.util.CivColor;

public class InventoryUpdateTask extends CivAsyncTask {

	public static ReentrantLock lock = new ReentrantLock();
	public Player player;
	
	public InventoryUpdateTask() {
	}
	
	public InventoryUpdateTask(Player player) {
		this.player = player;
	}
	
	@Override
	public void run() {	
		
		if (!lock.tryLock()) {
			return;
		}
		
		try {
			if ( player.getOpenInventory() != null ) {
				if ( player.getOpenInventory().getTitle().equalsIgnoreCase("Починка")) {
					Inventory inv = player.getOpenInventory().getTopInventory();
					double totalCost = 0;
					for ( ItemStack item : inv.getContents()) {
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
							continue;
							
						} catch (InvalidConfiguration e) {
							e.printStackTrace();
						}
					}
					
					ItemStack repair = LoreGuiItem.build(CivColor.LightGreen+"Починить", Material.LIME_CONCRETE, CivColor.Gold+"Общая стоимость: "+CivColor.White+((int)totalCost)+" "+CivSettings.CURRENCY_NAME);
					repair = LoreGuiItem.setAction(repair, "RepairItems");
					inv.setItem(8, repair);
				} else {
					return;
				}
			} else {
				return;
			}
		} finally {
			lock.unlock();
		}
	}
}
