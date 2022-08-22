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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import ru.tailsxcraft.civcraft.command.CommandBase;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigGovernment;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Civilization;
import ru.tailsxcraft.civcraft.util.CivColor;

public class CivGovCommand extends CommandBase {

	@Override
	public void init() {
		command = "/civ gov";
		displayName = CivSettings.localize.localizedString("cmd_civ_gov_name");
		
		commands.put("info", CivSettings.localize.localizedString("cmd_civ_gov_infoDesc"));
		commands.put("change", CivSettings.localize.localizedString("cmd_civ_gov_changeDesc"));
		commands.put("list", CivSettings.localize.localizedString("cmd_civ_gov_listDesc"));
	}
	
	public void change_cmd() throws CivException {
		Civilization civ = getSenderCiv();
		
		if (args.length < 2) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_gov_changePrompt"));
		}
		
		ConfigGovernment gov = ConfigGovernment.getGovernmentFromName(args[1]);
		if (gov == null) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_gov_changeInvalid")+" "+args[1]);
		}
		
		if (!gov.isAvailable(civ)) {
			throw new CivException(CivSettings.localize.localizedString("var_cmd_civ_gov_changeNotHere",gov.displayName));
		}
		
		civ.changeGovernment(civ, gov, false);
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("cmd_civ_gov_changeSuccess"));
	}
	
	public void list_cmd() throws CivException {
		Civilization civ = getSenderCiv();
		
		CivMessage.sendHeading(sender, CivSettings.localize.localizedString("cmd_civ_gov_listHeading"));
		ArrayList<ConfigGovernment> govs = ConfigGovernment.getAvailableGovernments(civ);
		
		for (ConfigGovernment gov : govs) {
			if (gov == civ.getGovernment()) {
				CivMessage.send(sender, CivColor.Gold+gov.displayName+" "+"("+CivSettings.localize.localizedString("currentGovernment")+")");
			} else {
				CivMessage.send(sender, CivColor.Green+gov.displayName);
			}
		}
		
	}
	
	public void info_cmd() throws CivException {
		Civilization civ = getSenderCiv();
		
		CivMessage.sendHeading(sender, CivSettings.localize.localizedString("cmd_civ_gov_infoHading")+" "+civ.getGovernment().displayName);
		CivMessage.send(sender, CivColor.Green+CivSettings.localize.localizedString("cmd_civ_gov_infoTrade")+" "+CivColor.LightGreen+civ.getGovernment().trade_rate+
				CivColor.Green+" "+CivSettings.localize.localizedString("cmd_civ_gov_infoCottage")+" "+CivColor.LightGreen+civ.getGovernment().cottage_rate);
		CivMessage.send(sender, CivColor.Green+CivSettings.localize.localizedString("cmd_civ_gov_infoUpkeep")+" "+CivColor.LightGreen+civ.getGovernment().upkeep_rate+
				CivColor.Green+" "+CivSettings.localize.localizedString("cmd_civ_gov_infoGrowth")+" "+CivColor.LightGreen+civ.getGovernment().growth_rate);
		CivMessage.send(sender, CivColor.Green+CivSettings.localize.localizedString("cmd_civ_gov_infoHammer")+" "+CivColor.LightGreen+civ.getGovernment().hammer_rate+
				CivColor.Green+" "+CivSettings.localize.localizedString("cmd_civ_gov_infoBeaker")+" "+CivColor.LightGreen+civ.getGovernment().beaker_rate);
		CivMessage.send(sender, CivColor.Green+CivSettings.localize.localizedString("cmd_civ_gov_infoCulture")+" "+CivColor.LightGreen+civ.getGovernment().culture_rate+
				CivColor.Green+" "+CivSettings.localize.localizedString("cmd_civ_gov_infoMaxTax")+" "+CivColor.LightGreen+civ.getGovernment().maximum_tax_rate);
				
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
		validLeaderAdvisor();		
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

}
