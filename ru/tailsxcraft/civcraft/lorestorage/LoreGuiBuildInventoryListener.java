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
package ru.tailsxcraft.civcraft.lorestorage;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class LoreGuiBuildInventoryListener implements Listener {


	/*
	 * The second phase cancels the event if a non-gui item has been
	 * dropped into a gui inventory.
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void OnInventoryClickSecondPhase(InventoryClickEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
			LoreGuiBuildInventory guiInv = LoreGuiInventory.buildInventories.get(event.getWhoClicked());
			if ( guiInv == null || guiInv.invName == null ) {
				return;
			}
			if (guiInv.invName.equalsIgnoreCase(event.getView().getTitle()) || guiInv.inventoryPages.containsValue(event.getInventory())) {
				event.setCancelled(true);
				return;
			}
		} else if (event.isShiftClick()) {
			LoreGuiBuildInventory guiInv = LoreGuiInventory.buildInventories.get(event.getWhoClicked());
			if ( guiInv == null || guiInv.invName == null ) {
				return;
			}
			if (guiInv.invName.equalsIgnoreCase(event.getView().getTitle()) || guiInv.inventoryPages.containsValue(event.getInventory())) {
				event.setCancelled(true);
				return;
			}			
		}
		
	}

	@EventHandler(priority = EventPriority.LOW) 
	public void OnInventoryDragEvent(InventoryDragEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		for (int slot : event.getRawSlots()) {
			if (slot < event.getView().getTopInventory().getSize()) {
				LoreGuiBuildInventory guiInv = LoreGuiInventory.buildInventories.get(event.getWhoClicked());
				if ( guiInv == null || guiInv.invName == null ) {
					return;
				}
				if (guiInv.invName.equalsIgnoreCase(event.getView().getTitle()) || guiInv.inventoryPages.containsValue(event.getInventory())) {
					event.setCancelled(true);
					return;
				}
			}
		}
		
	}
	
}
