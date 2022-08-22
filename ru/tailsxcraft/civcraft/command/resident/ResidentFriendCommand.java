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
package ru.tailsxcraft.civcraft.command.resident;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import ru.tailsxcraft.civcraft.command.CommandBase;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;

public class ResidentFriendCommand extends CommandBase {

	@Override
	public void init() {
		command = "/resident friend";
		displayName = CivSettings.localize.localizedString("cmd_res_friend_name");
		
		commands.put("add", CivSettings.localize.localizedString("cmd_res_friend_addDesc"));
		commands.put("remove", CivSettings.localize.localizedString("cmd_res_friend_removeDesc"));
		commands.put("list", CivSettings.localize.localizedString("cmd_res_friend_listDesc"));
	}
	
	public void add_cmd() throws CivException {
		Resident resident = getResident();
		
		if (args.length < 2) {
			throw new CivException(CivSettings.localize.localizedString("cmd_res_friend_addPrompt"));
		}
		
		Resident friendToAdd = getNamedResident(1);
		
		resident.addFriend(friendToAdd);
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_cmd_res_friend_addSuccess",args[1]));	
		resident.save();
	}
	
	public void remove_cmd() throws CivException {
	Resident resident = getResident();
		
		if (args.length < 2) {
			throw new CivException(CivSettings.localize.localizedString("cmd_res_friend_removePrompt"));
		}
		
		Resident friendToRemove = getNamedResident(1);
		
		resident.removeFriend(friendToRemove);
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_cmd_res_friend_removeSuccess",args[1]));	
		resident.save();
	}
	
	public void list_cmd() throws CivException {
		Resident resident = getResident();
		CivMessage.sendHeading(sender, CivSettings.localize.localizedString("var_cmd_res_friend_listHeading",resident.getName()));
		
		String out = "";
		for (String res : resident.getFriends()) {
			out += res+ ", ";
		}
		CivMessage.send(sender, out);
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
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

}
