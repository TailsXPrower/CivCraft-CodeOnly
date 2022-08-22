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

import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.structure.Structure;
import ru.tailsxcraft.civcraft.template.Template;

public class StartStructureBuild implements Runnable {

	public String playerName;
	public Structure struct;
	public Template tpl;
	public Location centerLoc;
	
	@Override
	public void run() {
		Player player;
		try {
			player = CivGlobal.getPlayer(playerName);
		} catch (CivException e1) {
			e1.printStackTrace();
			return;
		}

		try {
			struct.doBuild(player, centerLoc, tpl);
			struct.save();
		} catch (CivException e) {
			CivMessage.sendError(player, CivSettings.localize.localizedString("internalCommandException")+" "+e.getMessage());
		} catch (IOException e) {
			CivMessage.sendError(player, CivSettings.localize.localizedString("internalIOException"));
			e.printStackTrace();
		} catch (SQLException e) {
			CivMessage.sendError(player, CivSettings.localize.localizedString("internalDatabaseException"));
			e.printStackTrace();
		}
	}

}
