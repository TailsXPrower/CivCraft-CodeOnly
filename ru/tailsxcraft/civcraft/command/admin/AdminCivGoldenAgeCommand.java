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
package ru.tailsxcraft.civcraft.command.admin;


import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.civilization.GoldenAge;
import ru.tailsxcraft.civcraft.civilization.GoldenAgeSweeper;
import ru.tailsxcraft.civcraft.command.CommandBase;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Civilization;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.threading.tasks.PlayerKickBan;
import ru.tailsxcraft.civcraft.war.War;

public class AdminCivGoldenAgeCommand extends CommandBase {

	@Override
	public void init() {
		command = "/ad civ goldenage";
		displayName = "Золотой век";
		
		commands.put("start", "[Цивилизация] [Время] - Команда, чтобы начать золотой век в Цивилизации");
		commands.put("stop", "[Цивилизация] - Команда, чтобы остановить золотой век в Цивилизации");
	}
	
	public void start_cmd() throws CivException{
		Civilization motherCiv = getNamedCiv(1);
		Integer length = getNamedInteger(2);
		
		GoldenAge goldenAge = new GoldenAge();
		goldenAge.setLength(length);
		goldenAge.start(motherCiv);
        
		CivMessage.sendSuccess(sender, "Вы начали Золотой век в цивилизации "+motherCiv.getName()+"!");
	}
	
	public void stop_cmd() throws CivException {
		Civilization motherCiv = getNamedCiv(1);
		
		if( motherCiv.getActiveGoldenAge() == null) {
			CivMessage.sendError(sender, "В указанной цивилизации не идёт Золотой век.");
			return;
		}
		
		GoldenAge event = motherCiv.getActiveGoldenAge();
		
		CivMessage.sendCiv(motherCiv, "Золотой век для нашей цивилизации закончился, всё вернулось в прежнее состояние.");
		event.cleanup();
		GoldenAgeSweeper.remove(event);
		
		CivMessage.sendSuccess(sender, "Вы закончили Золотой век в цивилизации "+motherCiv.getName()+"!");
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
		if (sender.isOp() == false) {
			throw new CivException(CivSettings.localize.localizedString("adcmd_NotAdmin"));			
		}	
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

}
