package ru.tailsxcraft.civcraft.command.town;

import java.text.SimpleDateFormat;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import ru.tailsxcraft.civcraft.command.CommandBase;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.randomevents.RandomEvent;
import ru.tailsxcraft.civcraft.util.CivColor;

public class TownEventCommand extends CommandBase {

	@Override
	public void init() {
		command = "/town event";
		displayName = CivSettings.localize.localizedString("cmd_town_event_name");
		
		commands.put("show", CivSettings.localize.localizedString("cmd_town_event_showDesc"));
		commands.put("activate", CivSettings.localize.localizedString("cmd_town_event_activateDesc"));
	}

	public void activate_cmd() throws CivException {
		Town town = getSelectedTown();
		RandomEvent event = town.getActiveEvent();

		if (event == null) {
			CivMessage.sendError(sender, CivSettings.localize.localizedString("cmd_town_event_activateNone"));
		} else {
			event.activate();
			CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("cmd_town_event_activateSuccess"));
		}
	}
	
	public void show_cmd() throws CivException {
		Town town = getSelectedTown();
		RandomEvent event = town.getActiveEvent();

		if (event == null) {
			CivMessage.sendError(sender, CivSettings.localize.localizedString("cmd_town_event_activateNone"));
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("M/dd h:mm:ss a z");

			CivMessage.sendHeading(sender, CivSettings.localize.localizedString("cmd_town_event_showCurrent")+" "+event.configRandomEvent.name);
			CivMessage.send(sender, CivColor.Green+CivSettings.localize.localizedString("cmd_town_event_showStarted")+" "+CivColor.LightGreen+sdf.format(event.getStartDate()));
			CivMessage.send(sender, CivColor.Green+CivSettings.localize.localizedString("cmd_town_event_showEnd")+" "+CivColor.LightGreen+sdf.format(event.getEndDate()));
			if (event.isActive()) {
				CivMessage.send(sender, CivColor.LightGray+CivSettings.localize.localizedString("cmd_town_event_showActive"));
			} else {
				CivMessage.send(sender, CivColor.Yellow+CivSettings.localize.localizedString("cmd_town_event_showInactive"));
			}
			CivMessage.send(sender, CivColor.Green+"-- "+CivSettings.localize.localizedString("cmd_town_event_showMessageHeading")+" ---");
			CivMessage.send(sender, CivColor.LightGray+event.getMessages());
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
