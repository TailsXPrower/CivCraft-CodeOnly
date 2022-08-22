package ru.tailsxcraft.global.perks.components;

import java.io.IOException;

import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigBuildableInfo;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.template.Template;
import ru.tailsxcraft.civcraft.util.CivColor;

public class CustomPersonalTemplate extends PerkComponent {
	
	@Override
	public void onActivate(Resident resident) {
		CivMessage.send(resident, CivColor.LightGreen+CivSettings.localize.localizedString("customTemplate_personal"));
	}
	

	public Template getTemplate(Player player, ConfigBuildableInfo info) {
		Template tpl = new Template();
		try {
			tpl.initTemplate(player.getLocation(), info, this.getString("theme"));
		} catch (CivException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tpl;
	}
}
