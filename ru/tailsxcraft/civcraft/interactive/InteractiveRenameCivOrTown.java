package ru.tailsxcraft.civcraft.interactive;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.exception.InvalidNameException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Civilization;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.global.perks.components.RenameCivOrTown;

public class InteractiveRenameCivOrTown implements InteractiveResponse {

	public String selection = null;
	public String oldName = null;
	public String newName = null;
	public Civilization selectedCiv = null;
	public Town selectedTown = null;
	RenameCivOrTown perk;

	
	public InteractiveRenameCivOrTown(Resident resident, RenameCivOrTown perk) {
		displayQuestion(resident);
		this.perk = perk;
	}
	
	public void displayQuestion(Resident resident) {		
		CivMessage.send(resident, CivColor.Green+CivSettings.localize.localizedString("interactive_rename_question1"));
		CivMessage.send(resident, CivColor.Gray+CivSettings.localize.localizedString("interactive_rename_question2"));
		return;
	}
	
	@Override
	public void respond(String message, Resident resident) {
		
		
		CivMessage.sendHeading(resident, "Rename Civilization or Town");
		
		try {
			if (selection == null) {
				if (message.equalsIgnoreCase("town")) {
					CivMessage.send(resident, CivColor.Green+CivSettings.localize.localizedString("interactive_rename_townPrompt"));
					selection = "town";
				} else if (message.equalsIgnoreCase("civ")) {
					CivMessage.send(resident, CivColor.Green+CivSettings.localize.localizedString("interactive_rename_civPrompt"));
					selection = "civ";
				} else {
					throw new CivException(CivSettings.localize.localizedString("interactive_rename_cancel"));
				}
			} else if (oldName == null) {
				oldName = message;
				if (selection.equals("town")) {
					Town town = CivGlobal.getTown(oldName);
					if (town == null) {
						throw new CivException(CivSettings.localize.localizedString("var_interactive_rename_townNoTown",oldName));
					}
					
					if (!town.getMayorGroup().hasMember(resident) && !town.getCiv().getLeaderGroup().hasMember(resident)) {
						throw new CivException(CivSettings.localize.localizedString("interactive_rename_noPerms"));
					}
					
					selectedTown = town;
					CivMessage.send(resident, CivColor.Green+CivSettings.localize.localizedString("interactive_rename_newtownPrompt"));
				} else if (selection.equals("civ")) {
					Civilization civ = CivGlobal.getCiv(oldName);
					if (civ == null) {
						civ = CivGlobal.getConqueredCiv(oldName);
						if (civ == null) {
							throw new CivException(CivSettings.localize.localizedString("var_interactive_rename_civNone",oldName));
						}
					}
					
					if (!civ.getLeaderGroup().hasMember(resident)) {
						throw new CivException(CivSettings.localize.localizedString("interactive_rename_civnoPerms"));
					}
					
					selectedCiv = civ;
					CivMessage.send(resident, CivColor.Green+CivSettings.localize.localizedString("interactive_rename_newcivPrompt"));
				}
			} else if (newName == null) {
				newName = message.replace(" ", "_");
				if (selectedCiv != null) {
					try {
						CivMessage.global(CivSettings.localize.localizedString("var_interactive_rename_successCiv",resident.getName(),selectedCiv.getName(),newName));
						selectedCiv.rename(newName);
						perk.markAsUsed(resident);
					} catch (InvalidNameException e) {
						throw new CivException(CivSettings.localize.localizedString("interactive_rename_invalidName"));
					}
				} else if (selectedTown != null) {
					try {
						CivMessage.global(CivSettings.localize.localizedString("var_interactive_rename_successTown",resident.getName(),selectedTown.getName(),newName));
						selectedTown.rename(newName);
						perk.markAsUsed(resident);
					} catch (InvalidNameException e) {
						throw new CivException(CivSettings.localize.localizedString("interactive_rename_invalidName"));
					}
				}
			} else {
				throw new CivException(CivSettings.localize.localizedString("interactive_rename_missingInfo"));
			}
		} catch (CivException e) {
			CivMessage.sendError(resident, e.getMessage());
			resident.clearInteractiveMode();
			return;
		}

		
	}

}
