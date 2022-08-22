package ru.tailsxcraft.civcraft.loregui;

import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import ru.tailsxcraft.civcraft.config.ConfigBuildableInfo;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.structure.Buildable;
import ru.tailsxcraft.civcraft.structurevalidation.StructureValidator;
import ru.tailsxcraft.civcraft.template.Template;
import ru.tailsxcraft.civcraft.template.Template.TemplateType;
import ru.tailsxcraft.civcraft.threading.TaskMaster;

public class BuildWithDefaultPersonalTemplate implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
		Resident resident = CivGlobal.getResident(player);
		ConfigBuildableInfo info = resident.pendingBuildableInfo;
		
		try {
			String path = Template.getTemplateFilePath(info.template_base_name, Template.getDirection(player.getLocation()), TemplateType.STRUCTURE, "default");
			Template tpl;
			try {
				//tpl.load_template(path);
				tpl = Template.getTemplate(path, player.getLocation());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			Location centerLoc = Buildable.repositionCenterStatic(player.getLocation(), info, Template.getDirection(player.getLocation()), (double)tpl.size_x, (double)tpl.size_z);	
			//Buildable.validate(player, null, tpl, centerLoc, resident.pendingCallback);
			TaskMaster.asyncTask(new StructureValidator(player, tpl.getFilepath(), centerLoc, resident.pendingCallback), 0);
			player.closeInventory();

		} catch (CivException e) {
			CivMessage.sendError(player, e.getMessage());
		}		
	}

}
