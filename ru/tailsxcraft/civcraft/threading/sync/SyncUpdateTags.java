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
package ru.tailsxcraft.civcraft.threading.sync;

import java.util.Collection;

import net.md_5.itag.iTag;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.object.Resident;

import org.bukkit.entity.Player;

public class SyncUpdateTags implements Runnable {

	Collection<Resident> residentsToSendUpdate;
	String playerToUpdate;
	
	public SyncUpdateTags(String playerToUpdate, Collection<Resident> residentsToSendUpdate) {
		this.residentsToSendUpdate = residentsToSendUpdate;
		this.playerToUpdate = playerToUpdate;
	}

	@Override
	public void run() {
		if (CivSettings.hasITag) {
			try {
				Player player = CivGlobal.getPlayer(playerToUpdate);		
				for (Resident resident : residentsToSendUpdate) {
					try {
						Player resPlayer = CivGlobal.getPlayer(resident);
						if (player == resPlayer) {
							continue;
						}
						iTag.getInstance().refreshPlayer(player, resPlayer);
						iTag.getInstance().refreshPlayer(resPlayer, player);
					} catch (CivException e) {
						// one of these players is not online.
					}
				}
				
				
			} catch (CivException e1) {
				return;
			}		
		}
	}
	
}
