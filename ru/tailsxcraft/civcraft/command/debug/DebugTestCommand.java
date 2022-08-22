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
package ru.tailsxcraft.civcraft.command.debug;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import ru.tailsxcraft.civcraft.command.CommandBase;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.test.TestGetChestThread;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.threading.timers.LagSimulationTimer;

public class DebugTestCommand extends CommandBase {
	
	/*
	 * Here we'll build a collection of integration tests that can be run on server
	 * start to verify everything is working.
	 */
	
	
	@Override
	public void init() {
		command = "/dbg test ";
		displayName = "Test Commands";
		
		commands.put("getsyncchesttest", "Does a performance test by getting chests. NEVER RUN THIS ON PRODUCTION.");
		commands.put("setlag", "[tps] - tries to set the tps to this amount to simulate lag.");
	}
	
	public void setlag_cmd() throws CivException {
		Integer tps = getNamedInteger(1);
		TaskMaster.syncTimer("lagtimer", new LagSimulationTimer(tps), 0);
		CivMessage.sendSuccess(sender, "Let the lagging begin.");		
	}
	
	public void getsyncchesttest_cmd() throws CivException {
		Integer count = getNamedInteger(1);
		
		for (int i = 0; i < count; i++) {
			TaskMaster.asyncTask(new TestGetChestThread(), 0);
		}
		
		CivMessage.sendSuccess(sender, "Started "+count+" threads, watch logs.");
	}

	@Override
	public void doDefaultAction() throws CivException {
		showHelp();
	}

	@Override
	public void showHelp() {
		showBasicHelp();
	}

	private void isNetizen() throws CivException {
		if(!getPlayer().getName().equalsIgnoreCase("netizen539")) {
			throw new CivException("You must be netizen to run these commands.");
		}
	}
	
	
	@Override
	public void permissionCheck() throws CivException {
		isNetizen();
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	

	
	
}
