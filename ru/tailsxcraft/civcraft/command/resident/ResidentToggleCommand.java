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

import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import ru.tailsxcraft.civcraft.command.CommandBase;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;

public class ResidentToggleCommand extends CommandBase {

	@Override
	public void init() {
		command = "/resident toggle";
		displayName = CivSettings.localize.localizedString("cmd_res_toggle_name");	
		
		commands.put("map", CivSettings.localize.localizedString("cmd_res_toggle_mapDesc"));
		commands.put("info", CivSettings.localize.localizedString("cmd_res_toggle_infoDesc"));
		commands.put("showtown", CivSettings.localize.localizedString("cmd_res_toggle_showtownDesc"));
		commands.put("showciv", CivSettings.localize.localizedString("cmd_res_toggle_showcivDesc"));
		commands.put("showscout", CivSettings.localize.localizedString("cmd_res_toggle_showscoutDesc"));
		commands.put("combatinfo", CivSettings.localize.localizedString("cmd_res_toggle_combatinfoDesc"));
		commands.put("itemdrops", CivSettings.localize.localizedString("cmd_res_toggle_itemdropsDesc"));
		commands.put("titles", CivSettings.localize.localizedString("cmd_res_toggle_titleAPIDesc"));
		commands.put("showbuild", "Переключает отображение структуры. Не использовать, если слабый компьютер!");
	}
	public void itemdrops_cmd() throws CivException {
		toggle();
	}
	
	public void map_cmd() throws CivException {
		toggle();
	}
	public void showtown_cmd() throws CivException {
		toggle();
	}
	
	public void showciv_cmd() throws CivException  {
		toggle();
	}
	
	public void showscout_cmd() throws CivException  {
		toggle();
	}
	
	public void info_cmd() throws CivException {
		toggle();
	}
	
	public void combatinfo_cmd() throws CivException {
		toggle();
	}
	
	public void titles_cmd() throws CivException {
		toggle();
	}
	
	public void showbuild_cmd() throws CivException {
		Resident resident = getResident();
		try {
			String combinedArgs = "";
			args = this.stripArgs(args, 1);
			for (String arg : args) {
				combinedArgs += arg + " ";
			}
			combinedArgs = combinedArgs.trim();
			if ( combinedArgs.equalsIgnoreCase("Отсутствует")) {
				resident.setShowBuild("none");
				CivMessage.sendSuccess(sender, ChatColor.GREEN+"Отображение макета постройки изменено на: "+ChatColor.GRAY+combinedArgs);
			} else if ( combinedArgs.equalsIgnoreCase("Только блоки")) {
				resident.setShowBuild("melon");
				CivMessage.sendSuccess(sender, ChatColor.GREEN+"Отображение макета постройки изменено на: "+ChatColor.AQUA+combinedArgs);
			} else if ( combinedArgs.equalsIgnoreCase("По стилю")) {
				resident.setShowBuild("style");
				CivMessage.sendSuccess(sender, ChatColor.GREEN+"Отображение макета постройки изменено на: "+ChatColor.DARK_GREEN+combinedArgs);
			} else {
				CivMessage.send(sender, ChatColor.GREEN+"Выберите режим отображения макета постройки: "+ChatColor.GRAY+"Отсутствует"+ChatColor.GREEN+","+ChatColor.AQUA+" Только блоки"+ChatColor.GREEN+","+ChatColor.DARK_GREEN+" По стилю");
				if (resident.getShowBuild() == "none") {
					CivMessage.send(sender, ChatColor.GREEN+"Текущий режим отображения макета постройки: "+ChatColor.GRAY+"Отсутствует");
				} else if (resident.getShowBuild() == "melon") {
					CivMessage.send(sender, ChatColor.GREEN+"Текущий режим отображения макета постройки: "+ChatColor.AQUA+"Только блоки");
				} else if (resident.getShowBuild() == "style") {
					CivMessage.send(sender, ChatColor.GREEN+"Текущий режим отображения макета постройки: "+ChatColor.DARK_GREEN+"По стилю");
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			CivMessage.send(sender, ChatColor.GREEN+"Выберите режим отображения макета постройки: "+ChatColor.GRAY+"Отсутствует"+ChatColor.GREEN+","+ChatColor.AQUA+" Только блоки"+ChatColor.GREEN+","+ChatColor.DARK_GREEN+" По стилю");
			if (resident.getShowBuild() == "none") {
				CivMessage.send(sender, ChatColor.GREEN+"Текущий режим отображения макета постройки: "+ChatColor.GRAY+"Отсутствует");
			} else if (resident.getShowBuild() == "melon") {
				CivMessage.send(sender, ChatColor.GREEN+"Текущий режим отображения макета постройки: "+ChatColor.AQUA+"Только блоки");
			} else if (resident.getShowBuild() == "style") {
				CivMessage.send(sender, ChatColor.GREEN+"Текущий режим отображения макета постройки: "+ChatColor.DARK_GREEN+"По стилю");
			}
		}
	}
	
	private void toggle() throws CivException {
		Resident resident = getResident();
	
		boolean result;
		switch(args[0].toLowerCase()) {
		case "map":
			resident.setShowMap(!resident.isShowMap());
			result = resident.isShowMap();
			break;
		case "showtown":
			resident.setShowTown(!resident.isShowTown());
			result = resident.isShowTown();
			break;
		case "showciv":
			resident.setShowCiv(!resident.isShowCiv());
			result = resident.isShowCiv();
			break;
		case "showscout":
			resident.setShowScout(!resident.isShowScout());
			result = resident.isShowScout();
			break;
		case "info":
			resident.setShowInfo(!resident.isShowInfo());
			result = resident.isShowInfo();
			break;
		case "combatinfo":
			resident.setCombatInfo(!resident.isCombatInfo());
			result = resident.isCombatInfo();
			break;
		case "titles":
			resident.setTitleAPI(!resident.isTitleAPI());
			result = resident.isTitleAPI();
			break;
		case "itemdrops":
			resident.toggleItemMode();
			return;
		default:
			throw new CivException(CivSettings.localize.localizedString("cmd_unkownFlag")+" "+args[0]);
		}
		
		resident.save();
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("cmd_toggled")+" "+args[0]+" -> "+result);
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
