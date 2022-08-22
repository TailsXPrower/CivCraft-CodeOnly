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
package ru.tailsxcraft.civcraft.items.units;

import gpl.AttributeUtil;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigBuildableInfo;
import ru.tailsxcraft.civcraft.config.ConfigUnit;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.interactive.InteractiveTownName;
import ru.tailsxcraft.civcraft.lorestorage.LoreMaterial;
import ru.tailsxcraft.civcraft.main.CivCraft;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.structure.Buildable;
import ru.tailsxcraft.civcraft.structure.TownHall;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.util.CallbackInterface;
import ru.tailsxcraft.civcraft.util.CivColor;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Settler extends UnitMaterial implements CallbackInterface {

	public Settler(String id, ConfigUnit configUnit) {
		super(id,configUnit);
	}
	
	public static void spawn(Inventory inv, Town town) throws CivException {

		ItemStack is = LoreMaterial.spawn(Unit.SETTLER_UNIT);
		
		UnitMaterial.setOwningTown(town, is);
		
		AttributeUtil attrs = new AttributeUtil(is);
		attrs.addLore(CivColor.Rose+CivSettings.localize.localizedString("settler_Lore1")+" "+CivColor.LightBlue+town.getCiv().getName());
		attrs.addLore(CivColor.Gold+CivSettings.localize.localizedString("settler_Lore2"));
		//attrs.addEnhancement(new NamespacedKey(CivCraft.getPlugin(), "LoreEnhancementSoulBound"), null, null);
		attrs.addEnhancement("LoreEnhancementSoulBound", null, null);
		attrs.addLore(CivColor.Gold+CivSettings.localize.localizedString("Soulbound"));
		
		attrs.setCivCraftProperty("owner_civ_id", ""+town.getCiv().getId());
		is = attrs.getStack();
		
		
		if (!Unit.addItemNoStack(inv, is)) {
			throw new CivException(CivSettings.localize.localizedString("var_settler_errorBarracksFull",Unit.SETTLER_UNIT.getUnit().name));
		}

	}
	
	@Override
	public void onInteract(PlayerInteractEvent event) {
		event.setCancelled(true);
		Player player = event.getPlayer();
		player.updateInventory();
		Resident resident = CivGlobal.getResident(player);
		
		if (resident == null || !resident.hasTown()) {
			CivMessage.sendError(player, CivSettings.localize.localizedString("settler_errorNotRes"));
			return;
		}
		
		AttributeUtil attrs = new AttributeUtil(event.getItem());
		String ownerIdString = attrs.getCivCraftProperty("owner_civ_id");
		if (ownerIdString == null) {
			CivMessage.sendError(player, CivSettings.localize.localizedString("settler_errorInvalidOwner"));
			return;
		}
		
		int civ_id = Integer.valueOf(ownerIdString);
		if (civ_id != resident.getCiv().getId()) {
			CivMessage.sendError(player, CivSettings.localize.localizedString("settler_errorNotOwner"));
			return;
		}
		
		double minDistance;
		try {
			minDistance = CivSettings.getDouble(CivSettings.townConfig, "town.min_town_distance");
		} catch (InvalidConfiguration e) {
			CivMessage.sendError(player, CivSettings.localize.localizedString("internalException"));
			e.printStackTrace();
			return;
		}
		
		for (Town town : CivGlobal.getTowns()) {
			TownHall townhall = town.getTownHall();
			if (townhall == null) {
				continue;
			}
			
			double dist = townhall.getCenterLocation().distance(new BlockCoord(event.getPlayer().getLocation()));
			if (dist < minDistance) {
				DecimalFormat df = new DecimalFormat();
				CivMessage.sendError(player, CivSettings.localize.localizedString("var_settler_errorTooClose",town.getName(),df.format(dist),minDistance));
				return;
			}
		}
		
		
		/*
		 * Build a preview for the Capitol structure.
		 */
		CivMessage.send(player, CivColor.LightGreen+CivColor.BOLD+CivSettings.localize.localizedString("build_checking_position"));
		ConfigBuildableInfo info = CivSettings.structures.get("s_townhall");
		try {
			Buildable.buildVerifyStatic(player, info, player.getLocation(), this);
		} catch (CivException e) {
			CivMessage.sendError(player, e.getMessage());
		}	
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
		resident.desiredTownLocation = player.getLocation();
		
		CivMessage.sendHeading(player, CivSettings.localize.localizedString("settler_heading"));
		CivMessage.send(player, CivColor.LightGreen+CivSettings.localize.localizedString("settler_prompt1"));
		CivMessage.send(player, " ");
		CivMessage.send(player, CivColor.LightGreen+ChatColor.BOLD+CivSettings.localize.localizedString("settler_prompt2"));
		CivMessage.send(player, CivColor.LightGray+CivSettings.localize.localizedString("build_cancel_prompt"));
		
		resident.setInteractiveMode(new InteractiveTownName());
	}
	
}
