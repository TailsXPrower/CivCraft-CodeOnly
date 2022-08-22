package ru.tailsxcraft.civcraft.interactive;

import ru.tailsxcraft.civcraft.camp.WarCamp;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigBuildableInfo;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;

public class InteractiveWarCampFound implements InteractiveResponse {

	ConfigBuildableInfo info;
	
	public InteractiveWarCampFound(ConfigBuildableInfo info) {
		this.info = info;
	}
	
	@Override
	public void respond(String message, Resident resident) {
		resident.clearInteractiveMode();

		if (!message.equalsIgnoreCase("yes")) {
			CivMessage.send(resident, CivSettings.localize.localizedString("interactive_warcamp_Cancel"));
			return;
		}
		
		WarCamp.newCamp(resident, info);
	}

}
