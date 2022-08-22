package ru.tailsxcraft.civcraft.interactive;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.global.perks.Perk;
import ru.tailsxcraft.global.perks.components.CustomTemplate;

public class InteractiveCustomTemplateConfirm implements InteractiveResponse {

	String playerName;
	CustomTemplate customTemplate;
	
	public InteractiveCustomTemplateConfirm(String playerName, CustomTemplate customTemplate) {
		this.playerName = playerName;
		this.customTemplate = customTemplate;
		displayQuestion();
	}
	
	public void displayQuestion() {
		Player player;
		try {
			player = CivGlobal.getPlayer(playerName);
		} catch (CivException e) {
			return;
		}
		
		Resident resident = CivGlobal.getResident(player);
		Town town = resident.getTown();
		Perk perk = customTemplate.getParent();
		
		CivMessage.sendHeading(player, CivSettings.localize.localizedString("interactive_template_heading"));
		CivMessage.send(player, CivColor.LightGreen+CivColor.BOLD+CivSettings.localize.localizedString("var_interactive_template_bind1",perk.getDisplayName(),town.getName()));
		CivMessage.send(player, CivColor.LightGreen+CivColor.BOLD+CivSettings.localize.localizedString("interactive_template_bind2"));
		CivMessage.send(player, CivColor.LightGreen+CivColor.BOLD+CivSettings.localize.localizedString("interactive_template_bind3"));
		CivMessage.send(player, CivColor.LightGreen+CivColor.BOLD+CivSettings.localize.localizedString("interactive_template_bind4"));
		CivMessage.send(player, CivColor.LightGreen+ChatColor.BOLD+CivSettings.localize.localizedString("interactive_template_bind5"));
	}
	
	@Override
	public void respond(String message, Resident resident) {
		Player player;
		try {
			player = CivGlobal.getPlayer(resident);
		} catch (CivException e) {
			return;
		}
		resident.clearInteractiveMode();

		if (!message.equalsIgnoreCase("yes")) {
			CivMessage.sendError(player, CivSettings.localize.localizedString("interactive_template_cancel"));
			return;
		}
		
		customTemplate.bindTemplateToTown(resident.getTown(), resident);
		customTemplate.markAsUsed(resident);
		CivMessage.sendSuccess(player, CivSettings.localize.localizedString("var_interactive_template_success",customTemplate.getParent().getDisplayName(),resident.getTown().getName()));
	}
}
