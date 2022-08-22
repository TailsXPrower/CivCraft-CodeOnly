package ru.tailsxcraft.global.perks.components;


import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.interactive.InteractiveRenameCivOrTown;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;

public class RenameCivOrTown extends PerkComponent {

	@Override
	public void onActivate(Resident resident) {
		
		if (!resident.hasTown()) {
			CivMessage.sendError(resident, CivSettings.localize.localizedString("RenameCivOrTown_NotResident"));
			return;
		}
		
		resident.setInteractiveMode(new InteractiveRenameCivOrTown(resident, this));
	}
	
}
