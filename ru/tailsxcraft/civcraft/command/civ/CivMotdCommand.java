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
package ru.tailsxcraft.civcraft.command.civ;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import ru.tailsxcraft.civcraft.command.CommandBase;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Civilization;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.util.CivColor;

public class CivMotdCommand extends CommandBase {

	@Override
	public void init() {
		command = "/civ motd";
		displayName = CivSettings.localize.localizedString("cmd_civ_motd_name");
		
		commands.put("set", CivSettings.localize.localizedString("cmd_civ_motd_setDesc"));
		commands.put("remove", CivSettings.localize.localizedString("cmd_civ_motd_removeDesc"));
	}
	
	public void set_cmd() throws CivException {
		Resident resident = getResident();
		Civilization civ = getSenderCiv();
		
		if (!civ.getLeaderGroup().hasMember(resident) && !civ.getAdviserGroup().hasMember(resident)) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_motd_notallowed"));
		}
		
		if (args.length < 2) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_motd_setPrompt"));
		}
		
		String motd = combineArgs(this.stripArgs(args, 1));
		civ.setMotd(motd);
		civ.save();
		
		CivMessage.sendCiv(civ, "MOTD:"+" "+motd);
	}
	
	public void remove_cmd() throws CivException {
		Resident resident = getResident();
		Civilization civ = getSenderCiv();
		
		if (!civ.getLeaderGroup().hasMember(resident) && !civ.getAdviserGroup().hasMember(resident)) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_motd_notallowed"));
		}
		civ.setMotd(null);
		civ.save();
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("cmd_civ_motd_removeSuccess"));
	}
	
	@Override
	public void doDefaultAction() throws CivException {
		showHelp();
		Resident resident = getResident();
		Civilization civ = getSenderCiv();
		if (civ.MOTD() != null)
		{
			CivMessage.send(resident, CivColor.LightPurple+"[Civ MOTD] "+CivColor.White+resident.getCiv().MOTD());
		}
		else {
			CivMessage.send(resident, CivColor.LightPurple+"[Civ MOTD] "+CivColor.White+CivSettings.localize.localizedString("cmd_civ_motd_noneSet"));
		}

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
