package ru.tailsxcraft.civcraft.randomevents.components;


import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.randomevents.RandomEventComponent;

public class PayPlayer extends RandomEventComponent {

	@Override
	public void process() {
		String playerName = this.getParent().componentVars.get(getString("playername_var"));
		if (playerName == null) {
			CivLog.warning("No playername var for pay player.");
			return;
		}

		Resident resident = CivGlobal.getResident(playerName);
		double coins = this.getDouble("amount");
		resident.getTreasury().deposit(coins);
		CivMessage.send(resident, CivSettings.localize.localizedString("resident_paid")+" "+coins+" "+CivSettings.CURRENCY_NAME);	
	}

}
