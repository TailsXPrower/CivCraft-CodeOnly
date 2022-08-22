package ru.tailsxcraft.civcraft.questions;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.util.CivColor;

public class CapitulateRequest implements QuestionResponseInterface {

	public Town capitulator;
	public String from;
	public String to;
	public String playerName;
	
	@Override
	public void processResponse(String param) {
		if (param.equalsIgnoreCase("accept")) {
			capitulator.capitulate();
			CivMessage.global(CivSettings.localize.localizedString("var_capitulateAccept",from,to));
		} else {
			CivMessage.send(playerName, CivColor.LightGray+CivSettings.localize.localizedString("var_RequestDecline",to));
		}
	}

	@Override
	public void processResponse(String response, Resident responder) {
		processResponse(response);		
	}
}
