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

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.util.CivColor;

public class TownAddOutlawTask implements Runnable {

	String name;
	Town town;
	
	
	public TownAddOutlawTask(String name, Town town) {
		this.name = name;
		this.town = town;
	}

	@Override
	public void run() {
		
		try {
			Player player = CivGlobal.getPlayer(name);
			CivMessage.send(player, CivColor.Yellow+ChatColor.BOLD+CivSettings.localize.localizedString("var_TownAddOutlawTask_Notify",town.getName()));
		} catch (CivException e) {
		}
		
		town.addOutlaw(name);
		town.save();
		CivMessage.sendTown(town, CivColor.Yellow+CivSettings.localize.localizedString("var_TownAddOutlawTask_Message",name));
		
	}
	
}
