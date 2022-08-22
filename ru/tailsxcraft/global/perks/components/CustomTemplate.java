package ru.tailsxcraft.global.perks.components;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.interactive.InteractiveCustomTemplateConfirm;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.sessiondb.SessionEntry;
import ru.tailsxcraft.civcraft.structure.Buildable;
import ru.tailsxcraft.civcraft.template.Template;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.global.perks.Perk;

public class CustomTemplate extends PerkComponent {
	
	@Override
	public void onActivate(Resident resident) {
		Player player;
		try {
			player = CivGlobal.getPlayer(resident);
		} catch (CivException e) {
			return;
		}
		
		Town town = resident.getTown();
		if (town == null) {
			CivMessage.sendError(player, CivSettings.localize.localizedString("customTemplate_noTown"));
			return;
		}
		
		if (hasTownTemplate(town)) {
			CivMessage.sendError(player, CivColor.Rose+CivSettings.localize.localizedString("customTemplatE_alreadyBound"));
			return;
		}
		
		/*
		 * Send resident into interactive mode to confirm that they want
		 * to bind the template to this town. 
		 */
		resident.setInteractiveMode(new InteractiveCustomTemplateConfirm(resident.getName(), this));
		
	}
	
	private String getTemplateSessionKey(Town town) {
		return "customtemplate:"+town.getName()+":"+this.getString("template");
	}
	private static String getTemplateSessionKey(Town town, String buildableBaseName) {
		return "customtemplate:"+town.getName()+":"+buildableBaseName;
	}
	
	private static String getTemplateSessionValue(Perk perk, Resident resident) {
		return perk.getIdent()+":"+resident.getName();
	}
	
	public void bindTemplateToTown(Town town, Resident resident) {
		CivGlobal.getSessionDB().add(getTemplateSessionKey(town), getTemplateSessionValue(this.getParent(), resident), 
				town.getCiv().getId(), town.getId(), 0);		
	}
	
	public boolean hasTownTemplate(Town town) {
		ArrayList<SessionEntry> entries = CivGlobal.getSessionDB().lookup(getTemplateSessionKey(town));
		
		for (SessionEntry entry : entries) {
			String[] split = entry.value.split(":");
			
			if (this.getParent().getIdent().equals(split[0])) {
				return true;
			}
		}
		
		return false;
	}
	
	public static ArrayList<Perk> getTemplatePerksForBuildable(Town town, String buildableBaseName) {
		ArrayList<SessionEntry> entries = CivGlobal.getSessionDB().lookup(getTemplateSessionKey(town, buildableBaseName));
		ArrayList<Perk> perks = new ArrayList<Perk>();
		
		for (SessionEntry entry : entries) {
			String[] split = entry.value.split(":");
			
			Perk perk = Perk.staticPerks.get(split[0]);
			if (perk != null) {
				Perk tmpPerk = new Perk(perk.configPerk);
				tmpPerk.provider = split[1];
				perks.add(tmpPerk);
			} else {
				CivLog.warning("Unknown perk in session db:"+split[0]);
				continue;
			}
		}
		
		return perks;
	}
	

	public Template getTemplate(Player player, Buildable buildable) {
		Template tpl = new Template();
		try {
			tpl.initTemplate(player.getLocation(), buildable, this.getString("theme"));
		} catch (CivException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tpl;
	}
	
}
