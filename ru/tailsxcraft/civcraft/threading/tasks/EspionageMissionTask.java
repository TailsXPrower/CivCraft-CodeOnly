/*************************************************************************
 * 
 * AVRGAMING LLC
 * __________________
 * 
 *  [2013] AVRGAMING LLC
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of AVRGAMING LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to AVRGAMING LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from AVRGAMING LLC.
 */
package ru.tailsxcraft.civcraft.threading.tasks;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.cache.PlayerLocationCache;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigMission;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.items.units.Unit;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.CultureChunk;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.structure.ScoutShip;
import ru.tailsxcraft.civcraft.structure.ScoutTower;
import ru.tailsxcraft.civcraft.structure.Structure;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.util.ChunkCoord;
import ru.tailsxcraft.civcraft.util.CivColor;

public class EspionageMissionTask implements Runnable {

	ConfigMission mission;
	String playerName;
	Town target;
	int secondsLeft;
	Location startLocation;
	
	
	public EspionageMissionTask (ConfigMission mission, String playerName, Location startLocation, Town target, int seconds) {
		this.mission = mission;
		this.playerName = playerName;
		this.target = target;
		this.startLocation = startLocation;
		this.secondsLeft = seconds;
	}
	
	@Override
	public void run() {
		int exposePerSecond;
		int exposePerPlayer;
		int exposePerScout;
		try {
			exposePerSecond = CivSettings.getInteger(CivSettings.espionageConfig, "espionage.exposure_per_second");
			exposePerPlayer = CivSettings.getInteger(CivSettings.espionageConfig, "espionage.exposure_per_player");
			exposePerScout = CivSettings.getInteger(CivSettings.espionageConfig, "espionage.exposure_per_scout");
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			return;
		}	
		
		Player player;
		try {
			player = CivGlobal.getPlayer(playerName);
		} catch (CivException e) {
			return;
		}
		Resident resident = CivGlobal.getResident(player);	
		CivMessage.send(player, CivColor.LightGreen+CivColor.BOLD+CivSettings.localize.localizedString("espionage_missionStarted"));
			
		while (secondsLeft > 0) {
	
			if (secondsLeft > 0) {
				secondsLeft--;
				
				/* Add base exposure. */
				resident.setPerformingMission(true);
				resident.setSpyExposure(resident.getSpyExposure() + exposePerSecond);
				
				/* Add players nearby exposure */
				//PlayerLocationCache.lock.lock();
				try {
					int playerCount = PlayerLocationCache.getNearbyPlayers(new BlockCoord(player.getLocation()), 600).size();
					playerCount--;
					resident.setSpyExposure(resident.getSpyExposure() + (playerCount*exposePerPlayer));
				} finally {
				//	PlayerLocationCache.lock.unlock();
				}
				
				/* Add scout tower exposure */
				int amount = 0;
				double range;
				try {
					range = CivSettings.getDouble(CivSettings.warConfig, "scout_tower.range");
				} catch (InvalidConfiguration e) {
					e.printStackTrace();
					resident.setPerformingMission(false);
					return;
				}
				
				BlockCoord bcoord = new BlockCoord(player.getLocation());
								
				for (Structure struct : target.getStructures()) {
					if (!struct.isActive()) {
						continue;
					}
					
					if (struct instanceof ScoutTower || struct instanceof ScoutShip) {
						if (bcoord.distance(struct.getCenterLocation()) < range) {
							amount += exposePerScout;							
						}
					}
				}
				resident.setSpyExposure(resident.getSpyExposure() + amount);
				
				/* Process exposure penalities */
				if (target.processSpyExposure(resident)) {
					CivMessage.global(CivColor.Yellow+CivSettings.localize.localizedString("var_espionage_missionFailedAlert",(CivColor.White+player.getName()),mission.name,target.getName()));
					CivMessage.send(player, CivColor.Rose+CivSettings.localize.localizedString("espionage_missionFailed"));
					Unit.removeUnit(player);
					resident.setPerformingMission(false);
					return;
				}
				
				if ((secondsLeft % 15) == 0) {
					CivMessage.send(player, CivColor.Yellow+CivColor.BOLD+CivSettings.localize.localizedString("var_espionage_secondsRemain",secondsLeft));
				} else if (secondsLeft < 15) {
					CivMessage.send(player, CivColor.Yellow+CivColor.BOLD+CivSettings.localize.localizedString("var_espionage_secondsRemain",secondsLeft));
				}
				
			}
			
			ChunkCoord coord = new ChunkCoord(player.getLocation());
			CultureChunk cc = CivGlobal.getCultureChunk(coord);
			
			if (cc == null || cc.getCiv() != target.getCiv()) {
				CivMessage.sendError(player, CivSettings.localize.localizedString("espionage_missionAborted"));
				return;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return;
			}
		}
		
		resident.setPerformingMission(false);
		TaskMaster.syncTask(new PerformMissionTask(mission, playerName));
	}

}
