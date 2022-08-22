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
package ru.tailsxcraft.civcraft.command.plot;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.command.CommandBase;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.TownChunk;
import ru.tailsxcraft.civcraft.permission.PermissionNode;
import ru.tailsxcraft.civcraft.util.CivColor;

public class PlotPermCommand extends CommandBase {

	@Override
	public void init() {
		command = "/plot perm";
		displayName = CivSettings.localize.localizedString("cmd_plot_perm_name");
		
		commands.put("set", CivSettings.localize.localizedString("cmd_plot_perm_setDesc"));
	}
	
	public void set_cmd() throws CivException {
		Player player = (Player)sender;
		
		TownChunk tc = CivGlobal.getTownChunk(player.getLocation());
		if (tc == null) {
			throw new CivException(CivSettings.localize.localizedString("cmd_plot_perm_setnotInTown"));
		}
		
		if (args.length < 4) {
			showPermCmdHelp();
			throw new CivException(CivSettings.localize.localizedString("cmd_plot_perm_setBadArg"));	
		}
		
		PermissionNode node = null;
		switch(args[1].toLowerCase()) {
		case "build":
			node = tc.perms.build;
			break;
		case "destroy":
			node = tc.perms.destroy;
			break;
		case "interact":
			node = tc.perms.interact;
			break;
		case "itemuse":
			node = tc.perms.itemUse;
			break;
		case "reset":
			//TODO implement permissions reset.
			break;
		default:
			showPermCmdHelp();
			throw new CivException(CivSettings.localize.localizedString("cmd_plot_perm_setBadArg"));
		}
		
		if (node == null) {
			throw new CivException(CivSettings.localize.localizedString("cmd_plot_perm_setInternalError"));
		}
		
		boolean on;
		if (args[3].equalsIgnoreCase("on") || args[3].equalsIgnoreCase("yes") || args[3].equalsIgnoreCase("1")) {
			on = true;
		} else if (args[3].equalsIgnoreCase("off") || args[3].equalsIgnoreCase("no") || args[3].equalsIgnoreCase("0")) {
			on = false;
		} else {
			showPermCmdHelp();
			throw new CivException(CivSettings.localize.localizedString("cmd_plot_perm_setBadArg"));
		}
		
		switch(args[2].toLowerCase()) {
		case "owner":
			node.setPermitOwner(on);
			break;
		case "group":
			node.setPermitGroup(on);
			break;
		case "others":
			node.setPermitOthers(on);
		}
		
		tc.save();
		
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_cmd_plot_perm_setSuccess",node.getType(),on,args[2]));
	}
	
	private void showPermCmdHelp() {
		CivMessage.send(sender, CivColor.LightGray+CivSettings.localize.localizedString("cmd_plot_perm_help1"));
		CivMessage.send(sender, CivColor.LightGray+"    "+CivSettings.localize.localizedString("cmd_plot_perm_help2"));
		CivMessage.send(sender, CivColor.LightGray+"    "+CivSettings.localize.localizedString("cmd_plot_perm_help3"));
	}

	@Override
	public void doDefaultAction() throws CivException {
		showHelp();
	}

	@Override
	public void showHelp() {
		showBasicHelp();
	}

	@Override
	public void permissionCheck() throws CivException {
		if (args.length != 0) {
			validPlotOwner();
		}
			
		return;
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

}
