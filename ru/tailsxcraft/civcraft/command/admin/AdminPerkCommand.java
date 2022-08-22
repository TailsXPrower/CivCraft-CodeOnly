package ru.tailsxcraft.civcraft.command.admin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import ru.tailsxcraft.civcraft.command.CommandBase;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigPerk;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.util.CivColor;

public class AdminPerkCommand extends CommandBase {

	@Override
	public void init() {
		command = "/ad perk";
		displayName = CivSettings.localize.localizedString("adcmd_perk_name");
		
		commands.put("list", CivSettings.localize.localizedString("adcmd_perk_listDesc"));
		commands.put("reload", CivSettings.localize.localizedString("adcmd_perk_reloadDesc"));
	}

	public void list_cmd() {
		CivMessage.sendHeading(sender, CivSettings.localize.localizedString("adcmd_perk_listHeading"));
		for (ConfigPerk perk : CivSettings.perks.values()) {
			CivMessage.send(sender, CivColor.Green+perk.display_name+CivColor.LightGreen+" id:"+CivColor.Rose+perk.id);
		}
		CivMessage.send(sender, CivColor.LightGray+CivSettings.localize.localizedString("adcmd_perk_listingSuccess"));
	}
	
	public void reload_cmd() throws FileNotFoundException, IOException, InvalidConfigurationException, InvalidConfiguration
	{
		CivSettings.reloadPerks();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

}
