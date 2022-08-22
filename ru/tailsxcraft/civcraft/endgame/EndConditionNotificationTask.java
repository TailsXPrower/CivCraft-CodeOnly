package ru.tailsxcraft.civcraft.endgame;

import java.util.ArrayList;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Civilization;
import ru.tailsxcraft.civcraft.sessiondb.SessionEntry;
import ru.tailsxcraft.civcraft.util.CivColor;

public class EndConditionNotificationTask implements Runnable {

	@Override
	public void run() {
		
		for (EndGameCondition endCond : EndGameCondition.endConditions) {
			ArrayList<SessionEntry> entries = CivGlobal.getSessionDB().lookup(endCond.getSessionKey());
			if (entries.size() == 0) {
				continue;
			}
			
			for (SessionEntry entry : entries) {
				Civilization civ = EndGameCondition.getCivFromSessionData(entry.value);
				if (civ != null)
				{
					Integer daysLeft = endCond.getDaysToHold() - endCond.getDaysHeldFromSessionData(entry.value);
					if (daysLeft == 0) {
						CivMessage.global(CivSettings.localize.localizedString("var_cmd_civ_info_victory",
								CivColor.LightBlue+CivColor.BOLD+civ.getName()+CivColor.White, CivColor.LightPurple+CivColor.BOLD+endCond.getVictoryName()+CivColor.White));
						break;
					} else {
						CivMessage.global(CivSettings.localize.localizedString("var_cmd_civ_info_daysTillVictoryNew",
								CivColor.LightBlue+CivColor.BOLD+civ.getName()+CivColor.White, CivColor.Yellow+CivColor.BOLD+daysLeft+CivColor.White,CivColor.LightPurple+CivColor.BOLD+endCond.getVictoryName()+CivColor.White));
					}
				}
			}
		}
		
	}

}
