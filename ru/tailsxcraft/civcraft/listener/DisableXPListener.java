package ru.tailsxcraft.civcraft.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.main.CivData;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.civcraft.util.ItemManager;

public class DisableXPListener implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onExpBottleEvent(ExpBottleEvent event) {
		event.setExperience(0);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onEnchantItemEvent(EnchantItemEvent event) {
		CivMessage.sendError(event.getEnchanter(), CivSettings.localize.localizedString("customItem_NoEnchanting"));
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onItemSpawnEvent(ItemSpawnEvent event) {
//		if (event.getEntity().getType().equals(EntityType.EXPERIENCE_ORB)) {
//			event.setCancelled(true);
//		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		
		if (event.getClickedBlock() == null || ItemManager.getType(event.getClickedBlock()) == CivData.AIR) {
			return;
		}
		
		Block block = event.getClickedBlock();
		
		if (block.getType().equals(Material.ENCHANTING_TABLE)) {
			CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("customItem_enchantTableDisabled"));
			event.setCancelled(true);
		}
		
		if (block.getType().equals(Material.ANVIL)) {
			
			// Started to get annoyed not being able to rename items as OP. This makes it easier.
			if (!(event.getPlayer().isOp()))
			{
				CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("customItem_anvilDisabled"));
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerExpChange(PlayerExpChangeEvent event) {
		Resident resident = CivGlobal.getResident(event.getPlayer());
		CivMessage.send(resident, CivColor.LightGreen+CivSettings.localize.localizedString("var_customItem_Pickup",CivColor.Yellow+event.getAmount()+CivColor.LightGreen,CivSettings.CURRENCY_NAME));
		resident.getTreasury().deposit(event.getAmount());
		
		
		event.setAmount(0);
	}
	
}
