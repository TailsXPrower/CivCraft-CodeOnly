package ru.tailsxcraft.civcraft.loregui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiBuildInventory;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiInventory;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItemListener;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.tutorial.CivTutorial;

public class OpenInventory implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
		player.closeInventory();
		
		class SyncTaskDelayed implements Runnable {
			String playerName;
			ItemStack stack;
			
			public SyncTaskDelayed(String playerName, ItemStack stack) {
				this.playerName = playerName;
				this.stack = stack;
			}
			
			@Override
			public void run() {
				Player player;
				try {
					player = CivGlobal.getPlayer(playerName);
				} catch (CivException e) {
					e.printStackTrace();
					return;
				}
				
				switch (LoreGuiItem.getActionData(stack, "invType")) {
				case "showTutorialInventory":
					CivTutorial.showTutorialInventory(player);
					break;
				case "showCraftingHelp":
					CivTutorial.showCraftingHelp(player);
					break;
				case "showGuiInv":
					String invName = LoreGuiItem.getActionData(stack, "invName");
					Inventory inv = LoreGuiItemListener.guiInventories.get(invName);
					if (inv != null) {
						player.openInventory(inv);
					} else {
						CivLog.error("Couldn't find GUI inventory:"+invName);
					}
					break;
				case "showBuildInvPage":
					String pageName = LoreGuiItem.getActionData(stack, "pageName");
					LoreGuiBuildInventory guiInv = LoreGuiInventory.buildInventories.get(player);
					Inventory page = guiInv.getPage(pageName);
					if (page != null) {
						player.openInventory(page);
					} else {
						CivLog.error("Couldn't find GUI inventory:"+page);
					}
					break;
				case "showBuildInv":
					LoreGuiBuildInventory invGui = LoreGuiInventory.buildInventories.get(player);
					Inventory inventory = invGui.getInventory();
					if (inventory != null) {
						player.openInventory(inventory);
					} else {
						CivLog.error("Couldn't find GUI inventory:"+inventory);
					}
					break;
				default:
					break;
				}
			}
		}
		
		TaskMaster.syncTask(new SyncTaskDelayed(player.getName(), stack));		
	}

}
