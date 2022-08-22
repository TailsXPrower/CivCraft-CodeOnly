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

import org.bukkit.Location;
import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.structure.Buildable;
import ru.tailsxcraft.civcraft.structure.wonders.Wonder;
import ru.tailsxcraft.civcraft.template.Template;
import ru.tailsxcraft.civcraft.threading.TaskMaster;

public class InteractiveBuildCommand implements InteractiveResponse {

	Town town;
	Buildable buildable;
	Location center;
	Template tpl;
	
	public InteractiveBuildCommand(Town town, Buildable buildable, Location center, Template tpl) {
		this.town = town;
		this.buildable = buildable;
		this.center = center.clone();
		this.tpl = tpl;
	}
	
	@Override
	public void respond(String message, Resident resident) {
		Player player;
		try {
			player = CivGlobal.getPlayer(resident);
		} catch (CivException e) {
			return;
		}
		
		if (!message.equalsIgnoreCase("yes")) {
			CivMessage.sendError(player, CivSettings.localize.localizedString("interactive_build_cancel"));
			resident.clearInteractiveMode();
			resident.undoPreview();
			return;
		}
		
		
		if (!buildable.validated) {
			CivMessage.sendError(player, CivSettings.localize.localizedString("interactive_build_invalid"));
			return;
		}
		
		if (!buildable.isValid() && !player.isOp()) {
			CivMessage.sendError(player, CivSettings.localize.localizedString("interactive_build_invalidNotOP"));
			return;
		}
		
		class SyncTask implements Runnable {
			Resident resident;
			
			public SyncTask(Resident resident) {
				this.resident = resident;
			}
			
			@Override
			public void run() {
				Player player;
				try {
					player = CivGlobal.getPlayer(resident);
				} catch (CivException e) {
					return;
				}
				
				try {
					if (buildable instanceof Wonder) {
						town.buildWonder(player, buildable.getConfigId(), center, tpl);
					} else {
						town.buildStructure(player, buildable.getConfigId(), center, tpl);
					}
					resident.clearInteractiveMode();
				} catch (CivException e) {
					CivMessage.sendError(player, e.getMessage());
				}
			}
		}
		
		TaskMaster.syncTask(new SyncTask(resident));

	}

}
