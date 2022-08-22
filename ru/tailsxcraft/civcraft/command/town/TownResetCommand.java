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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import ru.tailsxcraft.civcraft.command.CommandBase;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigTownUpgrade;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.structure.Library;

public class TownResetCommand extends CommandBase {

	@Override
	public void init() {
		command = "/town reset";
		displayName = CivSettings.localize.localizedString("cmd_town_reset_name");
		
		commands.put("library", CivSettings.localize.localizedString("cmd_town_reset_libraryDesc"));
		commands.put("store", CivSettings.localize.localizedString("cmd_town_reset_storeDesc"));
	}

	public void library_cmd() throws CivException {
		Town town = getSelectedTown();
		
		Library library = (Library) town.findStructureByConfigId("s_library");
		if (library == null) {
			throw new CivException(CivSettings.localize.localizedString("cmd_town_reset_libraryNone"));
		}
		
		ArrayList<ConfigTownUpgrade> removeUs = new ArrayList<ConfigTownUpgrade>();
		for(ConfigTownUpgrade upgrade : town.getUpgrades().values()) {
			if (upgrade.action.contains("enable_library_enchantment")) {
				removeUs.add(upgrade);
			}
		}
		
		for (ConfigTownUpgrade upgrade : removeUs) {
			town.removeUpgrade(upgrade);
		}
		
		library.reset();
		
		town.save();
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("cmd_town_reset_librarySuccess"));
	}
	
	@Override
	public void doDefaultAction() throws CivException {
		showHelp();
	}

	@Override
	public void showHelp() {
		this.showBasicHelp();
	}

	@Override
	public void permissionCheck() throws CivException {
		this.validMayorAssistantLeader();
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

}
