package ru.tailsxcraft.civcraft.loregui;


import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.object.Resident;

public class ShowPerkPage implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		
		Resident resident = CivGlobal.getResident((Player)event.getWhoClicked());
		resident.showPerkPage(Integer.valueOf(LoreGuiItem.getActionData(stack, "page")));				
	}

}
