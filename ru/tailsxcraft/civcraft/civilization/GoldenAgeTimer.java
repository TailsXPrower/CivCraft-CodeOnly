package ru.tailsxcraft.civcraft.civilization;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.event.EventInterface;
import ru.tailsxcraft.civcraft.event.EventTimer;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Civilization;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.util.CivColor;

public class GoldenAgeTimer implements EventInterface {

	@Override
	public void process() {
		//CivMessage.global("Random Event Timer!");
		
		for (Civilization civ : CivGlobal.getCivs()) {		
			/* Event is already active in this civ. Lets skip checking it. */
			
            if (GoldenAge.checkGoldenAge(civ, civ.getHappiness())) {
            	if (civ.getActiveGoldenAge() != null) {
    				civ.getActiveGoldenAge().setLength(civ.getActiveGoldenAge().getLength()+8);
    				CivMessage.sendCiv(civ, CivColor.Gold+"Золотой век нашей цивилизации был продлён, так как мы набрали достаточное количество счастья во всех городах!");
    				continue;
    			}
            	
            	GoldenAge goldenAge = new GoldenAge();
    			goldenAge.start(civ);
			}
		}
	}

	@Override
	public Calendar getNextDate() throws InvalidConfiguration {
		SimpleDateFormat sdf = new SimpleDateFormat("M/dd h:mm:ss a z");
		Calendar cal = EventTimer.getCalendarInServerTimeZone();

		int hourly_peroid = CivSettings.getInteger(CivSettings.civConfig, "global.hourly_tick");
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.add(Calendar.SECOND, hourly_peroid);
		sdf.setTimeZone(cal.getTimeZone());
		return cal;
	}
}
