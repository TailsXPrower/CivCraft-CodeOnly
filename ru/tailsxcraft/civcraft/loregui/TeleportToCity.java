package ru.tailsxcraft.civcraft.loregui;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import ru.tailsxcraft.civcraft.camp.WarCamp;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.util.CivColor;

public class TeleportToCity implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
	    
		String townName = LoreGuiItem.getActionData(stack, "cityName");
		if ( townName != null ) {
			Town town = CivGlobal.getTown(townName);
			
			BlockCoord revive = town.getTownHall().getRandomRevivePoint();
			Location loc;
			if (revive == null) {
				loc = player.getBedSpawnLocation();
			} else {
				loc = revive.getLocation();
			}
			
			CivMessage.send(player, CivColor.LightGreen+CivSettings.localize.localizedString("capitol_respawningAlert"));
			player.teleport(loc);	
		} else {
			String campId = LoreGuiItem.getActionData(stack, "campId");
			WarCamp camp = CivGlobal.getResident(player).getCiv().getWarCamps().get(Integer.parseInt(campId));
			
			BlockCoord revive = camp.getRandomRevivePoint();
			Location loc;
			if (revive == null) {
				loc = player.getBedSpawnLocation();
			} else {
				loc = revive.getLocation();
			}
			
			CivMessage.send(player, CivColor.LightGreen+CivSettings.localize.localizedString("capitol_respawningAlert"));
			player.teleport(loc);
		}
//		for (ConfigBuildableInfo info : CivSettings.structures.values()) {
//			int type = ItemManager.getId(Material.ANVIL);
//			if (info.itemTypeId != 0) {
//				type = info.itemTypeId;
//			}
//			
//			ItemStack is;
//			if (!resident.hasTown()) {
//				is = LoreGuiItem.build(info.displayName, ItemManager.getId(Material.BEDROCK), 0, CivColor.Rose+"Must belong to a town to build structures.");
//			} else {
//				if (!resident.getCiv().hasTechnology(info.require_tech)) {
//					ConfigTech tech = CivSettings.techs.get(info.require_tech);
//					is = LoreGuiItem.build(info.displayName, ItemManager.getId(Material.BEDROCK), 0, CivColor.Rose+"Requires: "+tech.name);
//				} else {
//					is = LoreGuiItem.build(info.displayName, type, info.itemData, CivColor.Gold+"<Click To Build>");
//					is = LoreGuiItem.setAction(is, "BuildChooseTemplate");
//					is = LoreGuiItem.setActionData(is, "info", info.id);
//				}
//			}
//			
//			guiInventory.addItem(is);
//		}
//		
//		
//		LoreGuiItemListener.guiInventories.put(guiInventory.getName(), guiInventory);		
//		TaskMaster.syncTask(new OpenInventoryTask(player, guiInventory));
	}



}
