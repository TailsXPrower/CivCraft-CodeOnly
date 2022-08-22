package ru.tailsxcraft.civcraft.war;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ru.tailsxcraft.anticheat.ACManager;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.threading.tasks.PlayerKickBan;
import ru.tailsxcraft.civcraft.util.CivColor;

public class WarAntiCheat {

	
	public static void kickUnvalidatedPlayers() {
		if (CivGlobal.isCasualMode()) {
			return;
		}
		
		if (!ACManager.isEnabled()) {
			return;
		}
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.isOp()) {
				continue;
			}
			
			if (player.hasPermission("civ.ac_exempt")) {
				continue;
			}
			
			Resident resident = CivGlobal.getResident(player);
			onWarTimePlayerCheck(resident);
		}
		
		CivMessage.global(CivColor.LightGray+CivSettings.localize.localizedString("war_kick_atWarNoAnticheat"));
	}
	
	public static void onWarTimePlayerCheck(Resident resident) {
		if (!resident.hasTown()) {
			return;
		}
		
		if (!resident.getCiv().getDiplomacyManager().isAtWar()) {
			return;
		}
		
		try {
			if (!resident.isUsesAntiCheat()) {
				TaskMaster.syncTask(new PlayerKickBan(resident.getName(), true, false, 
						CivSettings.localize.localizedString("war_kick_needAnticheat1")+
						CivSettings.localize.localizedString("war_kick_needAntiCheat2")));
			}
		} catch (CivException e) {
		}
	}
	
}
