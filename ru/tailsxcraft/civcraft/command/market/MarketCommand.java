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
package ru.tailsxcraft.civcraft.command.market;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import ru.tailsxcraft.civcraft.command.CommandBase;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;

public class MarketCommand extends CommandBase {

	@Override
	public void init() {
		command = "/market";
		displayName = CivSettings.localize.localizedString("cmd_market_Name");	
				
		commands.put("buy", CivSettings.localize.localizedString("cmd_market_buyDesc"));

	}

	public void buy_cmd() {
		MarketBuyCommand cmd = new MarketBuyCommand();	
		cmd.onCommand(sender, null, "buy", this.stripArgs(args, 1));
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
