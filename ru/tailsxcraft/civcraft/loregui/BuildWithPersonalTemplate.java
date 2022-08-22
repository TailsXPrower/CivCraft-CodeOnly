package ru.tailsxcraft.civcraft.loregui;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import ru.tailsxcraft.civcraft.config.ConfigBuildableInfo;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.structure.Buildable;
import ru.tailsxcraft.civcraft.structurevalidation.StructureValidator;
import ru.tailsxcraft.civcraft.template.Template;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.global.perks.Perk;
import ru.tailsxcraft.global.perks.components.CustomPersonalTemplate;

public class BuildWithPersonalTemplate implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
		Resident resident = CivGlobal.getResident(player);
		
		ConfigBuildableInfo info = resident.pendingBuildableInfo;
		try {
			/* get the template name from the perk's CustomTemplate component. */
			String perk_id = LoreGuiItem.getActionData(stack, "perk");
			Perk perk = Perk.staticPerks.get(perk_id);
			CustomPersonalTemplate customTemplate = (CustomPersonalTemplate)perk.getComponent("CustomPersonalTemplate");
			Template tpl = customTemplate.getTemplate(player, resident.pendingBuildableInfo);
			Location centerLoc = Buildable.repositionCenterStatic(player.getLocation(), info, Template.getDirection(player.getLocation()), (double)tpl.size_x, (double)tpl.size_z);	
			TaskMaster.asyncTask(new StructureValidator(player, tpl.getFilepath(), centerLoc, resident.pendingCallback), 0);
			resident.desiredTemplate = tpl;
			player.closeInventory();
		} catch (CivException e) {
			CivMessage.sendError(player, e.getMessage());
		}
	}

}
