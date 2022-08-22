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
package ru.tailsxcraft.civcraft.interactive;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivCraft;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.util.CivColor;

public class InteractiveTeleportPlayer implements InteractiveResponse {
    
	@Override
	public void respond(String message, final Resident resident) {
		
		final Player player;
		try {
			player = CivGlobal.getPlayer(resident);
		} catch (CivException e) {
			return;
		}

		resident.clearInteractiveMode();

		if (!message.equalsIgnoreCase("yes")) {
			CivMessage.send(player, CivColor.LightGray+"Вы отклонили телепортацию, удачной Вам игры");
			return;
		}
		
		CivMessage.send(player, CivColor.LightGray+"Телепортация принята! Телепортируемся...");
		
		new BukkitRunnable() {
		    @Override
		    public void run() {
		    	player.teleport(resident.getTown().getTownHall().getRandomRevivePoint().getLocation());
		    }
		}.runTask(CivCraft.getPlugin());
	}

}
