package ru.tailsxcraft.civcraft.questions;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Civilization;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.threading.tasks.FoundTownSync;
import ru.tailsxcraft.civcraft.util.CivColor;

public class TownNewRequest implements QuestionResponseInterface {

	public Resident resident;
	public Resident leader;
	public Civilization civ;
	public String name;
	
	@Override
	public void processResponse(String param) {
		if (param.equalsIgnoreCase("accept")) {
			CivMessage.send(civ, CivColor.LightGreen+CivSettings.localize.localizedString("newTown_accepted1",leader.getName(),name));
			TaskMaster.syncTask(new FoundTownSync(resident));
		} else {
			CivMessage.send(resident, CivColor.LightGray+CivSettings.localize.localizedString("var_newTown_declined",leader.getName()));
		}		
	}

	@Override
	public void processResponse(String response, Resident responder) {
		this.leader = responder;
		processResponse(response);		
	}
}
