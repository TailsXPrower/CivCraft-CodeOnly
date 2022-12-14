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

import java.util.ArrayList;

import ru.tailsxcraft.civcraft.components.Component;
import ru.tailsxcraft.civcraft.components.ProjectileComponent;
import ru.tailsxcraft.civcraft.main.CivGlobal;

public class ProjectileComponentTimer implements Runnable {
	
	@Override
	public void run() {
		
		try {
			if (!CivGlobal.towersEnabled) {
				return;
			}
			
			Component.componentsLock.lock();
			try {
				ArrayList<Component> projectileComponents = Component.componentsByType.get(ProjectileComponent.class.getName());
				
				if (projectileComponents == null) {
					return;
				}
				
				for (Component c : projectileComponents) {
					ProjectileComponent projectileComponent = (ProjectileComponent)c;
					projectileComponent.process();
				}
			} finally {
				Component.componentsLock.unlock();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
