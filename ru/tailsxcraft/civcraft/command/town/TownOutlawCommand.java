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
package ru.tailsxcraft.civcraft.command.town;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.command.CommandBase;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Civilization;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.threading.tasks.TownAddOutlawTask;
import ru.tailsxcraft.civcraft.util.CivColor;

public class TownOutlawCommand extends CommandBase {

	@Override
	public void init() {
		command = "/town outlaw";
		displayName = CivSettings.localize.localizedString("cmd_town_outlaw_name");
		
		commands.put("add", CivSettings.localize.localizedString("cmd_town_outlaw_addDesc"));
		commands.put("remove", CivSettings.localize.localizedString("cmd_town_outlaw_removeDesc"));
		commands.put("list", CivSettings.localize.localizedString("cmd_town_outlaw_listDesc"));
		commands.put("addall",CivSettings.localize.localizedString("cmd_town_outlaw_addallDesc") );
		commands.put("removeall", CivSettings.localize.localizedString("cmd_town_outlaw_removeallDesc"));
		commands.put("addallciv", CivSettings.localize.localizedString("cmd_town_outlaw_addallcivDesc"));
		commands.put("removeallciv", CivSettings.localize.localizedString("cmd_town_outlaw_removeallcivDesc"));
	}
	
	public void addall_cmd() throws CivException {
		Town town = getSelectedTown();
		Town targetTown = getNamedTown(1);
	
		for (Resident resident : targetTown.getResidents()) {
			
			try {
				Player player = CivGlobal.getPlayer(args[1]);
				CivMessage.send(player, CivColor.Yellow+ChatColor.BOLD+CivSettings.localize.localizedString("var_cmd_town_outlaw_addAllAlert1",town.getName()));
			} catch (CivException e) {
			}
			TaskMaster.asyncTask(new TownAddOutlawTask(resident.getName(), town), 1000);
		}
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_cmd_town_outlaw_addallalert3",args[1]));
	}
	
	public void removeall_cmd() throws CivException {
		Town town = getSelectedTown();
		Town targetTown = getNamedTown(1);
		
		for (Resident resident : targetTown.getResidents()) {
			town.removeOutlaw(resident.getName());
		}
		town.save();
	}
	
	public void addallciv_cmd() throws CivException {
		Town town = getSelectedTown();
		Civilization targetCiv = getNamedCiv(1);
		
		for (Town targetTown : targetCiv.getTowns()) {	
		for (Resident resident : targetTown.getResidents()) {
			
			try {
				Player player = CivGlobal.getPlayer(args[1]);
				CivMessage.send(player, CivColor.Yellow+ChatColor.BOLD+CivSettings.localize.localizedString("var_cmd_town_outlaw_addAllAlert1",town.getName()));
			} catch (CivException e) {
			}
			TaskMaster.asyncTask(new TownAddOutlawTask(resident.getName(), town), 1000);
		}
		}
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_cmd_town_outlaw_addallalert3",args[1]));
	}
	
	public void removeallciv_cmd() throws CivException {
		Town town = getSelectedTown();
		Civilization targetCiv = getNamedCiv(1);

		for (Town targetTown : targetCiv.getTowns()) {	
		for (Resident resident : targetTown.getResidents()) {
			town.removeOutlaw(resident.getName());
		}
		}
		town.save();
	}
	
	public void add_cmd() throws CivException {
		Town town = getSelectedTown();
		
		if (args.length < 2) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_outlaw_addPrompt"));
		}
		
		Resident resident = getNamedResident(1);
		if (resident.getTown()== town) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_outlaw_addError"));
		}
		
		try {
			Player player = CivGlobal.getPlayer(args[1]);
			CivMessage.send(player, CivColor.Yellow+ChatColor.BOLD+CivSettings.localize.localizedString("var_cmd_town_outlaw_addAllAlert1",town.getName()));			
		} catch (CivException e) {
		}	
		
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_cmd_town_outlaw_addallalert3",args[1]));
		TaskMaster.asyncTask(new TownAddOutlawTask(args[1], town), 1000);	
	}
	
	public void remove_cmd() throws CivException {
		Town town = getSelectedTown();
		
		if (args.length < 2) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_outlaw_removePrompt"));
		}
		
		town.removeOutlaw(args[1]);
		town.save();
		
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_cmd_town_outlaw_removeSuccess",args[1]));
	}
	
	public void list_cmd() throws CivException {
		Town town = getSelectedTown();
		
		CivMessage.sendHeading(sender, CivSettings.localize.localizedString("cmd_town_outlaw_listHeading"));
		
		String out = "";
		for (String outlaw : town.outlaws) {
			if (outlaw.length() >= 2){
			Resident res = CivGlobal.getResidentViaUUID(UUID.fromString(outlaw));
			out += res.getName() + ",";
			}
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
		validMayorAssistantLeader();
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

}
