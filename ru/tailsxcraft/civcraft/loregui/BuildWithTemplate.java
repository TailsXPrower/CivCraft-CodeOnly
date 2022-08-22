package ru.tailsxcraft.civcraft.loregui;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.template.Template;
import ru.tailsxcraft.global.perks.Perk;
import ru.tailsxcraft.global.perks.components.CustomPersonalTemplate;
import ru.tailsxcraft.global.perks.components.CustomTemplate;

public class BuildWithTemplate implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
		Resident resident = CivGlobal.getResident(player);
			
		String perk_id = LoreGuiItem.getActionData(stack, "perk");
		boolean useDefaultTemplate;
		if (perk_id == null) {
			useDefaultTemplate = true;
		} else {
			useDefaultTemplate = false;
		}
		
		try {
			Template tpl;
			if (!useDefaultTemplate) {
				/* Use a template defined by a perk. */
				Perk perk = Perk.staticPerks.get(perk_id);
				if (perk != null) {
	
					/* get the template name from the perk's CustomTemplate component. */
					CustomTemplate customTemplate = (CustomTemplate)perk.getComponent("CustomTemplate");
					if (customTemplate != null) {
						tpl = customTemplate.getTemplate(player, resident.pendingBuildable);
					} else {
						CustomPersonalTemplate customPersonalTemplate =  (CustomPersonalTemplate)perk.getComponent("CustomPersonalTemplate");
						tpl = customPersonalTemplate.getTemplate(player, resident.pendingBuildable.info);
					}
					
					resident.pendingBuildable.buildPlayerPreview(player, player.getLocation(), tpl);					

				} else {
					CivLog.error(perk_id+" "+CivSettings.localize.localizedString("loreGui_perkActivationFailed"));
				}
			} else {
				/* Use the default template. */
				tpl = new Template();
				try {
					tpl.initTemplate(player.getLocation(), resident.pendingBuildable);
				} catch (CivException e) {
					e.printStackTrace();
					throw e;
				} catch (IOException e) {
					e.printStackTrace();
					throw e;
				}
				
				resident.pendingBuildable.buildPlayerPreview(player, player.getLocation(), tpl);
			}
		} catch (CivException e) {
			CivMessage.sendError(player, e.getMessage());
		} catch (IOException e) {
			CivMessage.sendError(player, CivSettings.localize.localizedString("internalIOException"));
			e.printStackTrace();
		}
		player.closeInventory();
	}

}
