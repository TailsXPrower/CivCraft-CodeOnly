package ru.tailsxcraft.civcraft.command.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.command.CommandBase;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.structure.Buildable;

public class AdminRoadCommand extends CommandBase {

	@Override
	public void init() {
		command = "/ad road";
		displayName = CivSettings.localize.localizedString("adcmd_road_name");	
		
	//	commands.put("destroy", "Destroys nearest road.");
		commands.put("setraidtime", CivSettings.localize.localizedString("adcmd_road_setRaidTimeDesc"));		
	}

	public void setraidtime_cmd() throws CivException {
		Town town = getNamedTown(1);
		Player player = getPlayer();
		
		if (args.length < 3) {
			throw new CivException(CivSettings.localize.localizedString("adcmd_road_setRaidTimePrompt"));
		}
		
		Buildable buildable = town.getNearestBuildable(player.getLocation());
				
		String dateStr = args[2];
		SimpleDateFormat parser = new SimpleDateFormat("d:M:y:H:m");
		
		Date next;
		try {
			next = parser.parse(dateStr);
			CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("adcmd_road_setRaidTimeEnterTime"));
		} catch (ParseException e) {
			throw new CivException(CivSettings.localize.localizedString("var_adcmd_road_setRaidTimeError",args[2]));
		}
		
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
