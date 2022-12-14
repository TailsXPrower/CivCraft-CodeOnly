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
package ru.tailsxcraft.civcraft.threading.timers;

import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.cache.PlayerLocationCache;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivGlobal;

public class PlayerLocationCacheUpdate implements Runnable {

	public static int UPDATE_LIMIT = 20;
	public static Queue<String> playerQueue = new LinkedList<String>();
	
	@Override
	public void run() {

	//	if (PlayerLocationCache.lock.tryLock()) {		
			try {
				for (int i = 0; i < UPDATE_LIMIT; i++) {
					String playerName = playerQueue.poll();
					if (playerName == null) {
						return;
					}
					
					try {
						Player player = CivGlobal.getPlayer(playerName);
						if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
							//Don't leave creative or Spectator players in the cache.
							PlayerLocationCache.remove(playerName);
							continue;
						}
						PlayerLocationCache.updateLocation(player);
						playerQueue.add(playerName);
						
					} catch (CivException e) {
						// player not online. remove from queue by not re-adding.
						PlayerLocationCache.remove(playerName);
						continue;
					}
				}
			} finally {
			//	PlayerLocationCache.lock.unlock();
			}
		//}
	}

}
