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
package ru.tailsxcraft.civcraft.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import ru.tailsxcraft.civcraft.camp.WarCamp;
import ru.tailsxcraft.civcraft.components.ProjectileArrowComponent;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItemListener;
import ru.tailsxcraft.civcraft.main.CivData;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.ControlPoint;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.StructureBlock;
import ru.tailsxcraft.civcraft.object.StructureSign;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.civcraft.util.ItemManager;
import ru.tailsxcraft.civcraft.util.SimpleBlock;
import ru.tailsxcraft.civcraft.war.War;

public class Capitol extends TownHall {
	
	private HashMap<Integer, ProjectileArrowComponent> arrowTowers = new HashMap<Integer, ProjectileArrowComponent>();
	private StructureSign respawnSign;
	private int index = 0;
	
	public Location locin;
	public Location locout;

	public Capitol(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}
	

	protected Capitol(Location center, String id, Town town)
			throws CivException {
		super(center, id, town);
	}
	
	private RespawnLocationHolder getSelectedHolder() {
		ArrayList<RespawnLocationHolder> respawnables =  this.getTown().getCiv().getAvailableRespawnables();	
		return respawnables.get(index);
	}
	
	@SuppressWarnings("unused")
	@Override
	public void processSignAction(Player player, StructureSign sign, PlayerInteractEvent event) {
		//int special_id = Integer.valueOf(sign.getAction());
		Resident resident = CivGlobal.getResident(player);
		
		if (resident == null) {
			return;
		}
		
		if (resident.getCiv() == this.getCiv()) 
		{	
			if (!War.isWarTime()) {
				switch (sign.getAction()) 
				{
				case "wartpin":		
					player.teleport(locout);		
					break;
				case "wartpout":
					player.teleport(locin);			
					break;
				}
			} else {
				switch (sign.getAction()) 
				{
				case "wartpin":		
					CivMessage.send(resident, CivColor.Red+"Идёт война, использование телепорта запрещено!");
					break;
				case "wartpout":
					CivMessage.send(resident, CivColor.Red+"Идёт война, использование телепорта запрещено!");		
					break;
				}
			}
		} else {
			CivMessage.send(resident, CivColor.Red+"Вы не являетесь членом этой цивилизации!");
		}
		
		
		if (!War.isWarTime()) {
			return;
		}
		Boolean hasPermission = false;
		if((resident.getTown().isMayor(resident)) || (resident.getTown().getAssistantGroup().hasMember(resident)) || (resident.getCiv().getLeaderGroup().hasMember(resident)) || (resident.getCiv().getAdviserGroup().hasMember(resident))){
			hasPermission = true;
		}
		
		switch (sign.getAction()) {
		case "respawn":
			
			RespawnLocationHolder holder = getSelectedHolder();
			int respawnTimeSeconds = this.getRespawnTime();
			Date now = new Date();
			
			if (resident.getLastKilledTime() != null) {
				long secondsLeft = (resident.getLastKilledTime().getTime() + (respawnTimeSeconds*1000)) - now.getTime();
				if (secondsLeft > 0) {
					secondsLeft /= 1000; 
					CivMessage.sendError(resident, CivColor.Rose+CivSettings.localize.localizedString("var_capitol_secondsLeftTillRespawn",secondsLeft));
					return;
				}
			}
			
			Inventory respawnInv = Bukkit.createInventory(null, 27, "Города Цивилизации");
			Inventory respawnInv2 = Bukkit.createInventory(null, 27, "Города Цивилизации 2");
			Collection<Town> towns = resident.getCiv().getTowns();
			Iterator<Town> townIter = towns.iterator();

			Integer count = 1;
			
			while(townIter.hasNext()) {
				  Town town = townIter.next();
				  if (town.getTownHall() == null) continue;
				  if (!town.getTownHall().isActive()) continue;
				  
				  ItemStack item = null;
				  if (town.defeated) {
					  String[] lore = {CivColor.Yellow+"Город", CivColor.LightBlue+"Нажмите, чтобы телепортироваться"};
					  item = LoreGuiItem.build(CivColor.Red+town.getName(), Material.RED_TERRACOTTA, lore);
				  } else if ( town.claimed ){
					  String[] lore = {CivColor.Yellow+"Город", CivColor.LightBlue+"Нажмите, чтобы телепортироваться"};
					  item = LoreGuiItem.build(CivColor.Yellow+town.getName(), Material.YELLOW_TERRACOTTA, lore);
				  } else if ( town.isCapitol() ){
					  String[] lore = {CivColor.Yellow+"Капитолий", CivColor.LightBlue+"Нажмите, чтобы телепортироваться"};
					  item = LoreGuiItem.build(CivColor.Green+town.getName(), Material.GREEN_TERRACOTTA, lore);
				  } else {
					  String[] lore = {CivColor.Yellow+"Родной город", CivColor.LightBlue+"Нажмите, чтобы телепортироваться"};
					  item = LoreGuiItem.build(CivColor.LightGreen+town.getName(), Material.LIME_TERRACOTTA, lore);
				  } 
				 
				  item = LoreGuiItem.setAction(item, "TeleportToCity");
				  item = LoreGuiItem.setActionData(item, "cityName", town.getName());
				  if ( town.isCapitol() ) {
					  respawnInv.setItem(0, item);
				  } else {
					  if ( count < 26 ) {
						  respawnInv.setItem(count, item);
						  count++;
					  } else {
						  count = 0;
						  if ( count == 18 ) {
							  count = 19;
						  }
						  respawnInv2.setItem(count, item);
						  count++;
					  }
				  }
			}
			
			for ( int i = 0; i < resident.getCiv().getWarCamps().size(); i++ ) {
				WarCamp camp = resident.getCiv().getWarCamps().get(i);
				ItemStack item = null;
				String[] lore = {CivColor.Yellow+"Военный лагерь", CivColor.LightBlue+"Нажмите, чтобы телепортироваться"};
				item = LoreGuiItem.build(ChatColor.AQUA+camp.getName(), Material.CYAN_TERRACOTTA, lore);
				item = LoreGuiItem.setAction(item, "TeleportToCity");
				item = LoreGuiItem.setActionData(item, "campId", ""+i);
				
				if ( count < 26 ) {
					  respawnInv.setItem(count, item);
					  count++;
				  } else {
					  count = 0;
					  if ( count == 18 ) {
						  count = 19;
					  }
					  respawnInv2.setItem(count, item);
					  count++;
				  }
			}
			
			ItemStack item = LoreGuiItem.build(CivColor.LightGreen+"Далее", Material.MAP, CivColor.Gray+"Нажмите, чтобы посмотреть дальше");
			if (towns.size() >= 25) {
				item = LoreGuiItem.setAction(item, "OpenInventory");
				item = LoreGuiItem.setActionData(item, "invType", "showGuiInv");
				item = LoreGuiItem.setActionData(item, "invName", "Respawn2");
				
				ItemStack itemBack = LoreGuiItem.build(CivColor.LightGreen+"Назад", Material.MAP, CivColor.Gray+"Нажмите, чтобы вернуться назад");
				itemBack = LoreGuiItem.setAction(itemBack, "OpenInventory");
				itemBack = LoreGuiItem.setActionData(itemBack, "invType", "showGuiInv");
				itemBack = LoreGuiItem.setActionData(itemBack, "invName", "Respawn");
				respawnInv2.setItem(18, itemBack);
			}
			respawnInv.setItem(26, item);
			
			
			LoreGuiItemListener.guiInventories.put("Respawn", respawnInv);
			LoreGuiItemListener.guiInventories.put("Respawn2", respawnInv2);
			
			player.openInventory(respawnInv);
			/*
			ArrayList<RespawnLocationHolder> respawnables =  this.getTown().getCiv().getAvailableRespawnables();
			if (index >= respawnables.size()) {
				index = 0;
				changeIndex(index);
				CivMessage.sendError(resident, CivSettings.localize.localizedString("capitol_cannotRespawn"));
				return;
			}
			
			RespawnLocationHolder holder = getSelectedHolder();
			int respawnTimeSeconds = this.getRespawnTime();
			Date now = new Date();
			
			if (resident.getLastKilledTime() != null) {
				long secondsLeft = (resident.getLastKilledTime().getTime() + (respawnTimeSeconds*1000)) - now.getTime();
				if (secondsLeft > 0) {
					secondsLeft /= 1000; 
					CivMessage.sendError(resident, CivColor.Rose+CivSettings.localize.localizedString("var_capitol_secondsLeftTillRespawn",secondsLeft));
					return;
				}
			}
			
			BlockCoord revive = holder.getRandomRevivePoint();
			Location loc;
			if (revive == null) {
				loc = player.getBedSpawnLocation();
			} else {
				loc = revive.getLocation();
			}
			
			CivMessage.send(player, CivColor.LightGreen+CivSettings.localize.localizedString("capitol_respawningAlert"));
			player.teleport(loc);		
			*/
			break;
		}
	}
	
	
	@Override
	public void onPostBuild(BlockCoord absCoord, SimpleBlock commandBlock) {
		StructureSign structSign;
		
		if (commandBlock.command.equals("/towerfire")) {
			String id = commandBlock.keyvalues.get("id");
			Integer towerID = Integer.valueOf(id);
			
			if (!arrowTowers.containsKey(towerID)) {
				
				ProjectileArrowComponent arrowTower = new ProjectileArrowComponent(this, absCoord.getLocation());
				arrowTower.createComponent(this);
				arrowTower.setTurretLocation(absCoord);
				
				arrowTowers.put(towerID, arrowTower);
			}
		} else if (commandBlock.command.equals("/next")) {
			ItemManager.setType(absCoord.getBlock(), Material.AIR);
			
		} else if (commandBlock.command.equals("/prev")) {
			ItemManager.setType(absCoord.getBlock(), Material.AIR);
			
		} else if (commandBlock.command.equals("/respawndata")) {
			ItemManager.setType(absCoord.getBlock(), commandBlock.getType());
			ItemManager.setData(absCoord.getBlock(), commandBlock.getData());
			structSign = new StructureSign(absCoord, this);
			structSign.setText(CivColor.Green+"Нажмите"+CivColor.Black+", чтобы"+"\n"+"открыть меню"+"\n"+"возрождения.");
			if ( commandBlock.getType() == Material.OAK_SIGN ) {
				structSign.setRotation((Rotatable)commandBlock.getData());
			} else {
				structSign.setDirection((Directional)commandBlock.getData());
			}
			structSign.setAction("respawn");
			structSign.update();
			this.addStructureSign(structSign);
			CivGlobal.addStructureSign(structSign);
			
			this.respawnSign = structSign;
		} else if (commandBlock.command.equals("/wartpin")) {
			ItemManager.setType(absCoord.getBlock(), commandBlock.getType());
			ItemManager.setData(absCoord.getBlock(), commandBlock.getData());
			structSign = new StructureSign(absCoord, this);
			structSign.setText("Телепорт в"+"\n"+"военную комнату");
			structSign.setRotation((Rotatable)commandBlock.getData());
			structSign.setAction("wartpin");
			structSign.update();
			this.locin = structSign.getCoord().getLocation();
			this.addStructureSign(structSign);
			CivGlobal.addStructureSign(structSign);
		} else if (commandBlock.command.equals("/wartpout")) {
			ItemManager.setType(absCoord.getBlock(), commandBlock.getType());
			ItemManager.setData(absCoord.getBlock(), commandBlock.getData());
			structSign = new StructureSign(absCoord, this);
			structSign.setText("Телепорт"+"\n"+"обратно");
			structSign.setRotation((Rotatable)commandBlock.getData());
			structSign.setAction("wartpout");
			structSign.update();
			this.locout = structSign.getCoord().getLocation();
			this.addStructureSign(structSign);
			CivGlobal.addStructureSign(structSign);
		}		
	}
	
	@Override
	public void createControlPoint(BlockCoord absCoord) {
		
		Location centerLoc = absCoord.getLocation();
		
		/* Build the bedrock tower. */
		//for (int i = 0; i < 1; i++) {
		Block b = centerLoc.getBlock();
		ItemManager.setType(b, ItemManager.getType(Material.SANDSTONE)); ItemManager.setData(b, b.getBlockData());
		
		StructureBlock sb = new StructureBlock(new BlockCoord(b), this);
		this.addStructureBlock(sb.getCoord(), true);
		//}
		
		/* Build the control block. */
		b = centerLoc.getBlock().getRelative(0, 1, 0);
		ItemManager.setType(b, CivData.OBSIDIAN);
		sb = new StructureBlock(new BlockCoord(b), this);
		this.addStructureBlock(sb.getCoord(), true);
		
		int capitolControlHitpoints;
		try {
			capitolControlHitpoints = CivSettings.getInteger(CivSettings.warConfig, "war.control_block_hitpoints_capitol");
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			capitolControlHitpoints = 100;
		}
		
		BlockCoord coord = new BlockCoord(b);
		this.controlPoints.put(coord, new ControlPoint(coord, this, capitolControlHitpoints));
	}
	
	@Override
	public void onInvalidPunish() {
		int invalid_respawn_penalty;
		try {
			invalid_respawn_penalty = CivSettings.getInteger(CivSettings.warConfig, "war.invalid_respawn_penalty");
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			return;
		}
		
		CivMessage.sendTown(this.getTown(), CivColor.Rose+CivColor.BOLD+CivSettings.localize.localizedString("capitol_cannotSupport1")+
				" "+CivSettings.localize.localizedString("var_capitol_cannotSupport2",invalid_respawn_penalty));
	}
	
	@Override
	public boolean isValid() {
		if (this.getCiv().isAdminCiv()) {
			return true;
		}
		
		/* 
		 * Validate that all of the towns in our civ have town halls. If not, then 
		 * we need to punish by increasing respawn times.
		 */
		for (Town town : this.getCiv().getTowns()) {
			TownHall townhall = town.getTownHall();
			if (townhall == null) {
				return false;
			}
		}
		
		return super.isValid();
	}
	
	@Override
	public String getRespawnName() {
		return "Capitol\n"+this.getTown().getName();
	}
}
