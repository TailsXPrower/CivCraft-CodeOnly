package ru.tailsxcraft.civcraft.randomevents.components;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.randomevents.RandomEventComponent;

public class Happiness extends RandomEventComponent {

	@Override
	public void process() {
		int happiness = Integer.valueOf(this.getString("value"));
		int duration = Integer.valueOf(this.getString("duration"));
		
		CivGlobal.getSessionDB().add(getKey(this.getParentTown()), happiness+":"+duration, this.getParentTown().getCiv().getId(), this.getParentTown().getId(), 0);	
		sendMessage(CivSettings.localize.localizedString("var_re_happiness1",happiness,duration));		
	}

	public static String getKey(Town town) {
		return "randomevent:happiness:"+town.getId();
	}

}
