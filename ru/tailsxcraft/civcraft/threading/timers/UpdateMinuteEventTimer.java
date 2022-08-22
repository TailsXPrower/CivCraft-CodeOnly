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

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import ru.tailsxcraft.civcraft.camp.Camp;
import ru.tailsxcraft.civcraft.camp.CampUpdateTick;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.structure.Structure;
import ru.tailsxcraft.civcraft.structure.wonders.Wonder;
import ru.tailsxcraft.civcraft.threading.CivAsyncTask;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.threading.tasks.FisheryAsyncTask;
import ru.tailsxcraft.civcraft.util.BlockCoord;

public class UpdateMinuteEventTimer extends CivAsyncTask {
		
	public static ReentrantLock lock = new ReentrantLock();
	
	public UpdateMinuteEventTimer() {
	}
	
	@Override
	public void run() {		

		if (!lock.tryLock()) {
			return;
		}
		
		try {
			// Loop through each structure, if it has an update function call it in another async process
			Iterator<Entry<BlockCoord, Structure>> iter = CivGlobal.getStructureIterator();
	
			while(iter.hasNext()) {
				Structure struct = iter.next().getValue();
				
				if (!struct.isActive())
					continue;
				
				try {
					if (struct.getUpdateEvent() != null && !struct.getUpdateEvent().equals("")) {

						if (struct.getUpdateEvent().equals("fish_hatchery_process")) {
							if (!CivGlobal.fisheryEnabled) {
								continue;
							}
							
							TaskMaster.asyncTask("fishHatchery-"+struct.getCorner().toString(), new FisheryAsyncTask(struct), 0);
						}
					}
					
					struct.onUpdate();
				} catch (Exception e) {
					e.printStackTrace();
					//We need to catch any exception so that an error in one town/structure/good does not
					//break things for everybody.
					//TODO log exception into a file or something...
	//				if (struct.getTown() == null) {
	//					RJ.logException("TownUnknown struct:"+struct.config.displayName, e);
	//				} else {
	//					RJ.logException(struct.town.getName()+":"+struct.config.displayName, e);
	//				}
				}
			}
			
			for (Wonder wonder : CivGlobal.getWonders()) {
				wonder.onUpdate();
			}
			
			
			for (Camp camp : CivGlobal.getCamps()) {
				if (!camp.sifterLock.isLocked()) {
					TaskMaster.asyncTask(new CampUpdateTick(camp), 0);
				}
			}
		
		} finally {
			lock.unlock();
		}

	}

}
