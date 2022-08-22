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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.command.CommandBase;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.structure.Bank;
import ru.tailsxcraft.civcraft.structure.Blacksmith;
import ru.tailsxcraft.civcraft.structure.Grocer;
import ru.tailsxcraft.civcraft.structure.Library;
import ru.tailsxcraft.civcraft.structure.ScoutShip;
import ru.tailsxcraft.civcraft.structure.ScoutTower;
import ru.tailsxcraft.civcraft.structure.Stable;
import ru.tailsxcraft.civcraft.structure.Structure;

public class TownSetCommand extends CommandBase {

	@Override
	public void init() {
		command = "/town set";
		displayName = CivSettings.localize.localizedString("cmd_town_set_name");
		
		commands.put("taxrate", CivSettings.localize.localizedString("cmd_town_set_taxrateDesc"));
		commands.put("flattax", CivSettings.localize.localizedString("cmd_town_set_flattaxDesc"));
		commands.put("bankfee", CivSettings.localize.localizedString("cmd_town_set_bankfeeDesc"));
		commands.put("storefee", CivSettings.localize.localizedString("cmd_town_set_storefeeDesc"));
		commands.put("grocerfee", CivSettings.localize.localizedString("cmd_town_set_grocerfeeDesc"));
		commands.put("libraryfee", CivSettings.localize.localizedString("cmd_town_set_libraryfeeDesc"));
		commands.put("blacksmithfee", CivSettings.localize.localizedString("cmd_town_set_blacksmithfeeDesc"));
		commands.put("stablefee", CivSettings.localize.localizedString("cmd_town_set_stablefeeDesc"));
		
		commands.put("scoutrate", CivSettings.localize.localizedString("cmd_town_set_scoutrateDesc"));
		
	}
	
	public void stablefee_cmd() throws CivException {
		Town town = getSelectedTown();
		Integer feeInt = getNamedInteger(1);
		
		Structure struct = town.findStructureByConfigId("s_stable");
		if (struct == null) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_set_stablefeeNone"));
		}
		
		Stable stable = (Stable)struct;
		
		if (feeInt < Stable.FEE_MIN || feeInt > Stable.FEE_MAX) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_set_stablefeeRates"));
		}
	
		stable.setNonResidentFee(((double)feeInt/100));
		stable.updateSignText();
		
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_cmd_town_set_feeSuccess",feeInt));
	}
	
	public void scoutrate_cmd() throws CivException {
		Town town = getSelectedTown();
		Integer rate = getNamedInteger(1);
		
		if (rate != 10 && rate != 30 && rate != 60) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_set_scoutrateRates"));
		}
		
		for (Structure struct : town.getStructures()) {
			if (struct instanceof ScoutTower) {
				((ScoutTower)struct).setReportSeconds(rate);
			} else if (struct instanceof ScoutShip) {
				((ScoutShip)struct).setReportSeconds(rate);
			}
		}
		
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_cmd_town_set_scoutrateSuccess",rate));
	}
	
	public void blacksmithfee_cmd() throws CivException {
		Town town = getSelectedTown();
		Integer feeInt = getNamedInteger(1);
		
		if (feeInt < 5 || feeInt > 15) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_set_rate5to15"));
		}
		
		Structure struct = town.findStructureByConfigId("s_blacksmith");
		if (struct == null) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_set_blacksmithfeeNone"));
		}

		((Blacksmith)struct).setNonResidentFee(((double)feeInt/100));
		((Blacksmith)struct).updateSignText();
		
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_cmd_town_set_feeSuccess",feeInt));
	}
	
	
	public void libraryfee_cmd() throws CivException {
		Town town = getSelectedTown();
		Integer feeInt = getNamedInteger(1);
		
		if (feeInt < 5 || feeInt > 15) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_set_rate5to15"));
		}
		
		Structure struct = town.findStructureByConfigId("s_library");
		if (struct == null) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_set_libraryfeeNone"));
		}

		((Library)struct).setNonResidentFee(((double)feeInt/100));
		((Library)struct).updateSignText();
		
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_cmd_town_set_feeSuccess",feeInt));
	}
	
	public void grocerfee_cmd() throws CivException {
		Town town = getSelectedTown();
		Integer feeInt = getNamedInteger(1);
		
		if (feeInt < 5 || feeInt > 15) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_set_rate5to15"));
		}
		
		Structure struct = town.findStructureByConfigId("s_grocer");
		if (struct == null) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_set_grocerfeeNone"));
		}

		((Grocer)struct).setNonResidentFee(((double)feeInt/100));
		((Grocer)struct).updateSignText();
		
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_cmd_town_set_feeSuccess",feeInt));
		
	}
	
	public void bankfee_cmd() throws CivException {
		Town town = getSelectedTown();
		Integer feeInt = getNamedInteger(1);
		
		if (feeInt < 5 || feeInt > 15) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_set_rate5to15"));
		}
		
		Structure struct = town.findStructureByConfigId("s_bank");
		if (struct == null) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_set_bankfeeNone"));
		}
		
		((Bank)struct).setNonResidentFee(((double)feeInt/100));
		((Bank)struct).updateSignText();
		
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_cmd_town_set_feeSuccess",feeInt));
		
	}
	
	public void taxrate_cmd() throws CivException {
		Town town = getSelectedTown();
		
		if (args.length < 2) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_set_taxratePrompt"));
		}
		
		try { 
			town.setTaxRate(Double.valueOf(args[1])/100);
		} catch (NumberFormatException e) {
			throw new CivException(args[1]+" "+CivSettings.localize.localizedString("cmd_enterNumerError"));
		}
		
		town.quicksave();
		CivMessage.sendTown(town, CivSettings.localize.localizedString("var_cmd_town_set_taxrateSuccess",args[1]));
	}

	public void flattax_cmd() throws CivException {
		Town town = getSelectedTown();	
		if (args.length < 2) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_set_taxratePrompt"));
		}
				
		try { 
			town.setFlatTax(Integer.valueOf(args[1]));
		} catch (NumberFormatException e) {
			throw new CivException(args[1]+" "+CivSettings.localize.localizedString("cmd_enterNumerError"));
		}
		
		town.quicksave();
		CivMessage.send(town, CivSettings.localize.localizedString("var_cmd_town_set_flattaxSuccess",args[1]));
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
		Town town = getSelectedTown();
		Player player = getPlayer();
		
		if (!town.playerIsInGroupName("mayors", player) && !town.playerIsInGroupName("assistants", player)) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_claimNoPerm"));
		}		
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

}
