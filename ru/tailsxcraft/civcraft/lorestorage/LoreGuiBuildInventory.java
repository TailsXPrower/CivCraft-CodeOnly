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
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class LoreGuiBuildInventory {
			
	public final int MAX_INV_SIZE = 54;
	public final int INV_ROW_COUNT = 9;
	
	public Inventory inv;
	public String invName;

	public HashMap<String, String> inventoryAttrs = new HashMap<String, String>();
	public HashMap<String, Inventory> inventoryPages = new HashMap<String, Inventory>();
	
	public LoreGuiBuildInventory(Player player, String name, int inventorySize) {
		invName = name;
		if ( LoreGuiInventory.getBuildInventory(player) != null ) {
			createInventory(player, inventorySize, name);
			LoreGuiInventory.updateBuildInventory(player, this);
		} else {
			createInventory(player, inventorySize, name);
			LoreGuiInventory.setBuildInventory(player, this);
		}
	}
	
	public LoreGuiBuildInventory(Player player, String name, int inventorySize, int pagesSize, String...value) {
		invName = name;
		for(int i = 0; i < value.length; i++) {
			String str = value[i];
			addPage(player, pagesSize, str);
		}
		if ( LoreGuiInventory.getBuildInventory(player) != null ) {
			createInventory(player, inventorySize, name);
			LoreGuiInventory.updateBuildInventory(player, this);
		} else {
			createInventory(player, inventorySize, name);
			LoreGuiInventory.setBuildInventory(player, this);
		}
	}
	
	public LoreGuiBuildInventory(Player player, String name, int inventorySize, String[] value, int...sizes) {
		invName = name;
		for(int i = 0; i < value.length; i++) {
			String str = value[i];
			addPage(player, sizes[i], str);
		}
		if ( LoreGuiInventory.getBuildInventory(player) != null ) {
			createInventory(player, inventorySize, name);
			LoreGuiInventory.updateBuildInventory(player, this);
		} else {
			createInventory(player, inventorySize, name);
			LoreGuiInventory.setBuildInventory(player, this);
		}
	}
	
	public LoreGuiBuildInventory(Player player, String name, int inventorySize, int pagesSize, String[] value, String[] index) {
		invName = name;
		for(int i = 0; i < value.length; i++) {
			String str = value[i];
			String ind = index[i];
			addPage(player, pagesSize, str, ind);
		}
		if ( LoreGuiInventory.getBuildInventory(player) != null ) {
			createInventory(player, inventorySize, name);
			LoreGuiInventory.updateBuildInventory(player, this);
		} else {
			createInventory(player, inventorySize, name);
			LoreGuiInventory.setBuildInventory(player, this);
		}
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	public Inventory getPage(String name) {
		return inventoryPages.get(name);
	}

	public Set<Entry<String, Inventory>> getPages() {
		return inventoryPages.entrySet();
	}
	
	public void setPage(Inventory inv, String name) {
		inventoryPages.put(name, inv);
	}
	
	public void addPage(InventoryHolder holder, int pagesSize, String name) {
		Inventory inventory = createInventory(holder, pagesSize, name);
		inventoryPages.put(name, inventory);
	}
	
	public void addPage(InventoryHolder holder, int pagesSize, String name, String index) {
		Inventory inventory = createInventory(holder, pagesSize, name);
		inventoryPages.put(index, inventory);
	}
	
	public Inventory createInventory(InventoryHolder holder, int size, String name) {
	    inv = Bukkit.createInventory(holder, size, name);
		return inv;
	}
	
	public void updateInventory(Inventory inventory) {
		inv = inventory;
	}
	
	public void setInventoryData(String key, String value) {
		inventoryAttrs.put(key, value);
	}
	
	public String getInventoryData(String name, String key ) {
		return inventoryAttrs.get(key);
	}
	
	public void setInventory(String name, int size, InventoryHolder holder) {
		Inventory inv = Bukkit.createInventory(holder, size, name);
		this.inv = inv;
	}
}
