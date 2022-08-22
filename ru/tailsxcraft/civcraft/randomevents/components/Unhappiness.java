package ru.tailsxcraft.civcraft.randomevents.components;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.randomevents.RandomEventComponent;

public class Unhappiness extends RandomEventComponent {

	public static String getKey(Town town) {
		return "randomevent:unhappiness:"+town.getId();
	}
	
	
	@Override
	public void process() {
		
		int unhappiness = Integer.valueOf(this.getString("value"));
		int duration = Integer.valueOf(this.getString("duration"));
		
		CivGlobal.getSessionDB().add(getKey(this.getParentTown()), unhappiness+":"+duration, this.getParentTown().getCiv().getId(), this.getParentTown().getId(), 0);	
		sendMessage(CivSettings.localize.localizedString("var_re_unhappiness1",unhappiness,duration));
		
	}

}
