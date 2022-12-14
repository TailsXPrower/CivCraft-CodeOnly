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
package ru.tailsxcraft.civcraft.event;

import java.util.Calendar;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.items.BonusGoodie;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.threading.TaskMaster;

public class GoodieRepoEvent implements EventInterface {

	public static void repoProcess() {
		class SyncTask implements Runnable {
			@Override
			public void run() {
				
				for (Town town : CivGlobal.getTowns()) {
					for (BonusGoodie goodie : town.getBonusGoodies()) {
						town.removeGoodie(goodie);
					}
				}
				
				for (BonusGoodie goodie : CivGlobal.getBonusGoodies()) {
					try {
						goodie.replenish();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		
		TaskMaster.syncTask(new SyncTask());
	}
	
	@Override
	public void process() {
		CivLog.info("TimerEvent: GoodieRepo -------------------------------------");
		repoProcess();
		CivMessage.globalTitle(CivSettings.localize.localizedString("goodieRepoBroadcastTitle"),"");
		CivMessage.global(CivSettings.localize.localizedString("goodieRepoBroadcast"));
	}

	@Override
	public Calendar getNextDate() throws InvalidConfiguration {
		Calendar cal = EventTimer.getCalendarInServerTimeZone();
		int repo_day = CivSettings.getInteger(CivSettings.goodsConfig, "trade_goodie_repo_day");
		int repo_hour = CivSettings.getInteger(CivSettings.goodsConfig, "trade_goodie_repo_hour");
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, repo_hour);
		cal.add(Calendar.DATE, repo_day);
		return cal;
	}

}
