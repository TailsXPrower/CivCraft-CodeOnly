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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class LoreGuiInventory {
			
	public static final int MAX_INV_SIZE = 54;
	public static final int INV_ROW_COUNT = 9;
	
	public static HashMap<String, Inventory> inventories = new HashMap<String, Inventory>();
	public static HashMap<String, HashMap<String, String>> inventoryAttrs = new HashMap<String, HashMap<String, String>>();
	public static HashMap<Player, LoreGuiBuildInventory> buildInventories = new HashMap<Player, LoreGuiBuildInventory>();
	
	
	public static Inventory getGuiInventory(String name) {
		return inventories.get(name);
	}
	
	public static Inventory createGuiInventory(InventoryHolder holder, int size, String name) {
		Inventory inv = Bukkit.createInventory(holder, size, name);
		inventories.put(name, inv);
		return inv;
	}
	
	public static Inventory updateGuiInventory(String name, Inventory inv) {
		inventories.replace(name, inv);
		return inv;
	}
	
	public static void setInventoryData(String name, String key, String value ) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put(key, value);
		inventoryAttrs.put(name, attributes);
	}
	
	public static String getInventoryData(String name, String key ) {
		HashMap<String, String> attrs = inventoryAttrs.get(name);
		return attrs.get(key);
	}
	
	public static void setGuiInventory(String name, int size, InventoryHolder holder) {
		Inventory inv = Bukkit.createInventory(holder, size, name);
		inventories.put(name, inv);
	}
	
	public static LoreGuiBuildInventory getBuildInventory(Player player) {
		return buildInventories.get(player);
	}
	
	public static LoreGuiBuildInventory updateBuildInventory(Player player, LoreGuiBuildInventory inv) {
		buildInventories.replace(player, inv);
		return inv;
	}
	
	public static void setBuildInventory(Player player, LoreGuiBuildInventory inv) {
		buildInventories.put(player, inv);
	}
}
