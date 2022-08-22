package ru.tailsxcraft.civcraft.items.components;

import gpl.AttributeUtil;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.tutorial.CivTutorial;
import ru.tailsxcraft.civcraft.util.CivColor;

import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class TutorialBook extends ItemComponent {

	@Override
	public void onPrepareCreate(AttributeUtil attrs) {
		attrs.addLore(CivColor.Gold+CivSettings.localize.localizedString("tutorialBook_lore1"));
		attrs.addLore(CivColor.Rose+CivSettings.localize.localizedString("tutorialBook_lore2"));
	}

	
	public void onInteract(PlayerInteractEvent event) {
		
		event.setCancelled(true);
		if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) &&
				!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		
		//CivTutorial.showCraftingHelp(event.getPlayer());
		CivTutorial.spawnGuiBook(event.getPlayer());

	}
	
	public void onItemSpawn(ItemSpawnEvent event) {
		event.setCancelled(true);
	}

	
}
