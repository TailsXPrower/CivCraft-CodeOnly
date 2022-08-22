package pvptimer;

import java.util.Date;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.civcraft.util.DateUtil;

public class PvPTimer implements Runnable {

	@Override
	public void run() {
		
		for (Resident resident : CivGlobal.getResidents()) {
			if (!resident.isProtected()) {
				continue;
			}
			
			int mins;
			try {
				mins = CivSettings.getInteger(CivSettings.civConfig, "global.pvp_timer");
				if (DateUtil.isAfterMins(new Date(resident.getRegistered()), mins)) {
				//if (DateUtil.isAfterSeconds(new Date(resident.getRegistered()), mins)) {
					resident.setisProtected(false);
					CivMessage.send(resident, CivColor.LightGray+CivSettings.localize.localizedString("pvpTimerEnded"));
				}
			} catch (InvalidConfiguration e) {
				e.printStackTrace();
				return;
			}
		}
	}
}
