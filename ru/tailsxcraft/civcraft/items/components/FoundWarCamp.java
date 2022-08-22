package ru.tailsxcraft.civcraft.items.components;

import gpl.AttributeUtil;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigBuildableInfo;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.interactive.InteractiveWarCampFound;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.structure.Buildable;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.util.CallbackInterface;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.civcraft.war.War;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class FoundWarCamp extends ItemComponent implements CallbackInterface {
	
	public static ConfigBuildableInfo info = new ConfigBuildableInfo();
	static {
		info.id = "warcamp";
		info.displayName = "War Camp";
		info.ignore_floating = false;
		info.template_base_name = "warcamp";
		info.tile_improvement = false;
		info.templateYShift = -1;
		info.max_hitpoints = 100;
	}
	
	@Override
	public void onPrepareCreate(AttributeUtil attrUtil) {
		attrUtil.addLore(ChatColor.RESET+CivColor.Gold+CivSettings.localize.localizedString("buildWarCamp_lore1"));
		attrUtil.addLore(ChatColor.RESET+CivColor.Rose+CivSettings.localize.localizedString("itemLore_RightClickToUse"));		
	}
	
	public void foundCamp(Player player) throws CivException {
		Resident resident = CivGlobal.getResident(player);
		
		if (!resident.hasTown()) {
			throw new CivException(CivSettings.localize.localizedString("buildWarCamp_errorNotInCiv"));
		}
		
		if (!resident.getCiv().getLeaderGroup().hasMember(resident) &&
			!resident.getCiv().getAdviserGroup().hasMember(resident)) {
			throw new CivException(CivSettings.localize.localizedString("buildWarCamp_errorNotPerms"));
		}
		
		if (!War.isWarTime()) {
			throw new CivException(CivSettings.localize.localizedString("buildWarCamp_errorNotWarTime"));
		}
		
		/*
		 * Build a preview for the Capitol structure.
		 */
		CivMessage.send(player, CivColor.LightGreen+CivColor.BOLD+CivSettings.localize.localizedString("build_checking_position"));

		
		Buildable.buildVerifyStatic(player, info, player.getLocation(), this);
	}
	
	public void onInteract(PlayerInteractEvent event) {
		
		event.setCancelled(true);
		if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) &&
				!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		
		try {
			foundCamp(event.getPlayer());
		} catch (CivException e) {
			CivMessage.sendError(event.getPlayer(), e.getMessage());
		}
		
		class SyncTask implements Runnable {
			String name;
				
			public SyncTask(String name) {
				this.name = name;
			}
			
			@Override
			public void run() {
				Player player;
				try {
					player = CivGlobal.getPlayer(name);
				} catch (CivException e) {
					return;
				}
				player.updateInventory();
			}
		}
		TaskMaster.syncTask(new SyncTask(event.getPlayer().getName()));
		
		return;
		
	}

	@Override
	public void execute(String playerName) {
		Player player;
		try {
			player = CivGlobal.getPlayer(playerName);
		} catch (CivException e) {
			return;
		}
		Resident resident = CivGlobal.getResident(playerName);
		int warTimeout;
		try {
			warTimeout = CivSettings.getInteger(CivSettings.warConfig, "warcamp.rebuild_timeout");
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			return;
		}
		
		CivMessage.sendHeading(player, CivSettings.localize.localizedString("buildWarCamp_heading"));
		CivMessage.send(player, CivColor.LightGreen+CivSettings.localize.localizedString("buildWarCamp_prompt1"));
		CivMessage.send(player, CivColor.LightGreen+"   -"+CivSettings.localize.localizedString("buildWarCamp_prompt2"));
		CivMessage.send(player, CivColor.LightGreen+"   -"+CivSettings.localize.localizedString("var_buildWarCamp_prompt3",warTimeout));
		CivMessage.send(player, " ");
		CivMessage.send(player, CivColor.LightGreen+ChatColor.BOLD+CivSettings.localize.localizedString("buildWarCamp_prompt5"));
		CivMessage.send(player, CivColor.LightGray+CivSettings.localize.localizedString("buildWarCamp_prompt6"));
		
		resident.setInteractiveMode(new InteractiveWarCampFound(info));
	}
}
