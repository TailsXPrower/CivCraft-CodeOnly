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
package ru.tailsxcraft.civcraft.listener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import ru.tailsxcraft.civcraft.camp.Camp;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigTechPotion;
import ru.tailsxcraft.civcraft.items.units.UnitItemMaterial;
import ru.tailsxcraft.civcraft.items.units.UnitMaterial;
import ru.tailsxcraft.civcraft.lorestorage.LoreMaterial;
import ru.tailsxcraft.civcraft.main.CivData;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.CultureChunk;
import ru.tailsxcraft.civcraft.object.Relation;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.threading.tasks.PlayerChunkNotifyAsyncTask;
import ru.tailsxcraft.civcraft.threading.tasks.PlayerLoginAsyncTask;
import ru.tailsxcraft.civcraft.threading.timers.PlayerLocationCacheUpdate;
import ru.tailsxcraft.civcraft.util.ChunkCoord;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.civcraft.util.ItemManager;
import ru.tailsxcraft.civcraft.war.War;
import ru.tailsxcraft.civcraft.war.WarStats;

public class PlayerListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPickup(PlayerPickupItemEvent event) {
		
		String name;
		boolean rare = false;
		ItemStack item = event.getItem().getItemStack();
		if (item.getItemMeta().hasDisplayName()) {
			name = item.getItemMeta().getDisplayName();
			rare = true;
		} else {
			name = item.getType().name().replace("_", " ").toLowerCase();
		}
		
		Resident resident = CivGlobal.getResident(event.getPlayer());
		if (resident.getItemMode().equals("all")) {
			CivMessage.send(event.getPlayer(), CivColor.LightGreen+CivSettings.localize.localizedString("var_customItem_Pickup",CivColor.LightPurple+event.getItem().getItemStack().getAmount(),name),item);
		} else if (resident.getItemMode().equals("rare") && rare) {
			CivMessage.send(event.getPlayer(), CivColor.LightGreen+CivSettings.localize.localizedString("var_customItem_Pickup",CivColor.LightPurple+event.getItem().getItemStack().getAmount(),name),item);
		}
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerJoinEvent event) {
		CivLog.info("Scheduling on player login task for player:"+event.getPlayer().getName()+":"+event.getPlayer().getUniqueId());
		TaskMaster.asyncTask("onPlayerLogin-"+event.getPlayer().getName(), new PlayerLoginAsyncTask(event.getPlayer().getUniqueId()), 0);

		CivGlobal.playerFirstLoginMap.put(event.getPlayer().getName(), new Date());
		PlayerLocationCacheUpdate.playerQueue.add(event.getPlayer().getName());
//		MobSpawnerTimer.playerQueue.add((event.getPlayer().getName()));
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
		//Handle Teleportation Things here!
		if (event.getCause().equals(TeleportCause.COMMAND) ||
				event.getCause().equals(TeleportCause.PLUGIN)) {	
			CivLog.info("[TELEPORT]"+" "+event.getPlayer().getName()+" "+"to:"+event.getTo().getBlockX()+","+event.getTo().getBlockY()+","+event.getTo().getBlockZ()+
					" "+"from:"+event.getFrom().getBlockX()+","+event.getFrom().getBlockY()+","+event.getFrom().getBlockZ());
			Player player = event.getPlayer();
			if (!player.isOp() && !( player.hasPermission("civ.admin") || player.hasPermission(CivSettings.TPALL) ) ) {
				CultureChunk cc = CivGlobal.getCultureChunk(new ChunkCoord(event.getTo()));
				Camp toCamp = CivGlobal.getCampFromChunk(new ChunkCoord(event.getTo()));
				Resident resident = CivGlobal.getResident(player);
				if (resident != null && cc != null && cc.getCiv() != resident.getCiv() && !cc.getCiv().isAdminCiv()) {
					Relation.Status status = cc.getCiv().getDiplomacyManager().getRelationStatus(player);
					if (!(status.equals(Relation.Status.ALLY) && player.hasPermission(CivSettings.TPALLY) )
							&& !(status.equals(Relation.Status.NEUTRAL) && player.hasPermission(CivSettings.TPNEUTRAL)) 
							&& !(status.equals(Relation.Status.HOSTILE) && player.hasPermission(CivSettings.TPHOSTILE))
							&& !(status.equals(Relation.Status.PEACE) && player.hasPermission(CivSettings.TPWAR))
							&& !(status.equals(Relation.Status.WAR) && player.hasPermission(CivSettings.TPWAR))
							&& !player.hasPermission(CivSettings.TPALL)
							) {
						/* 
						 * Deny telportation into Civ if not allied.
						 */
						event.setTo(event.getFrom());
						if (!event.isCancelled())
						{
							CivLog.debug("Cancelled Event "+event.getEventName()+" with cause: "+event.getCause());
						event.setCancelled(true);
							CivMessage.send(resident, CivColor.Red+CivSettings.localize.localizedString("teleportDeniedPrefix")+" "+CivColor.White+CivSettings.localize.localizedString("var_teleportDeniedCiv",CivColor.Green+cc.getCiv().getName()+CivColor.White));
							return;
						}
					}
				}
				
				if (toCamp != null && toCamp != resident.getCamp() && !player.hasPermission(CivSettings.TPCAMP)) {
						/* 
						 * Deny telportation into Civ if not allied.
						 */
					event.setTo(event.getFrom());
						if (!event.isCancelled())
						{
							CivLog.debug("Cancelled Event "+event.getEventName()+" with cause: "+event.getCause());
						event.setCancelled(true);
							CivMessage.send(resident, CivColor.Red+CivSettings.localize.localizedString("teleportDeniedPrefix")+" "+CivColor.White+CivSettings.localize.localizedString("var_teleportDeniedCamp",CivColor.Green+toCamp.getName()+CivColor.White));
							return;
						}
					
				}
				
//				if (War.isWarTime()) {
//					
//					if (toCamp != null && toCamp == resident.getCamp()) {
//						return;
//					}
//					if (cc != null && (cc.getCiv() == resident.getCiv() || cc.getCiv().isAdminCiv())) {
//						return;
//					}
//					
//					event.setTo(event.getFrom());
//					if (!event.isCancelled())
//					{
//					event.setCancelled(true);
//						CivMessage.send(resident, CivColor.Red+"[Denied] "+CivColor.White+"You're not allowed to Teleport during War unless you are teleporting to your own Civ or Camp");
//					}
//				}
			}
		}
	}
		
	private void setModifiedMovementSpeed(Player player) {
		/* Change move speed based on armor. */
		double speed;
		Resident resident = CivGlobal.getResident(player);
		if (resident != null)
		{
			speed = resident.getWalkingModifier();
		} else {
			speed =CivSettings.normal_speed;
		}
		
		player.setWalkSpeed((float) Math.min(1.0f, speed));
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerMove(PlayerMoveEvent event) {
		/*
		 * Abort if we havn't really moved
		 */
		if (event.getFrom().getBlockX() == event.getTo().getBlockX() && 
			event.getFrom().getBlockZ() == event.getTo().getBlockZ() && 
			event.getFrom().getBlockY() == event.getTo().getBlockY()) {
			return;
		}
		if (!CivGlobal.speedChunks)
		{
			/* Get the Modified Speed for the player. */
			setModifiedMovementSpeed(event.getPlayer());
		}
				
		ChunkCoord fromChunk = new ChunkCoord(event.getFrom());
		ChunkCoord toChunk = new ChunkCoord(event.getTo());
		
		// Haven't moved chunks.
		if (fromChunk.equals(toChunk)) {
			return;
		}
		if (CivGlobal.speedChunks)
		{
			/* Get the Modified Speed for the player. */
			setModifiedMovementSpeed(event.getPlayer());
		}
		
		TaskMaster.syncTask( 
				new PlayerChunkNotifyAsyncTask(event.getFrom(), event.getTo(), event.getPlayer().getName()));

	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		Resident resident = CivGlobal.getResident(player);
		if (resident == null || !resident.hasTown()) {
			return;
		}
		
		if (War.isWarTime() && resident.getCiv().getDiplomacyManager().isAtWar() && !resident.getCiv().isConquered()) {
			if ( resident.getCiv().getCapitolStructure() != null && !resident.getCiv().getCapitolStructure().isDestroyed() && resident.getCiv().getCapitolStructure().isActive() ) {
				event.setRespawnLocation(resident.getCiv().getCapitolStructure().getRandomRespawnPoint().getLocation());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Resident resident = CivGlobal.getResident(event.getPlayer());
		if (resident != null) {
			if (resident.previewUndo != null) {
				resident.previewUndo.clear();
			}
			resident.clearInteractiveMode();
		}		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			//Unit.removeUnit(((Player)event.getEntity()));
			Boolean keepInventory = Boolean.valueOf(Bukkit.getWorld("world").getGameRuleValue("keepInventory"));
				if (!keepInventory) {
				ArrayList<ItemStack> stacksToRemove = new ArrayList<ItemStack>();
				for (ItemStack stack : event.getDrops()) {
					if (stack != null) {
						//CustomItemStack is = new CustomItemStack(stack);
						LoreMaterial material = LoreMaterial.getMaterial(stack);
						if (material != null) {
							material.onPlayerDeath(event, stack);
							if (material instanceof UnitMaterial) {
								stacksToRemove.add(stack);
								continue;
							}
							
							if (material instanceof UnitItemMaterial) {
								stacksToRemove.add(stack);
								continue;
							}
						}
					}
				}
				
				for (ItemStack stack : stacksToRemove) {
					event.getDrops().remove(stack);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (War.isWarTime()) {
			if (event.getEntity().getKiller() != null) {
				WarStats.incrementPlayerKills(event.getEntity().getKiller().getName());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST) 
	public void onPortalCreate(PortalCreateEvent event) {
		event.setCancelled(true);
	}
	
//	@EventHandler(priority = EventPriority.NORMAL)
//	public void OnCraftItemEvent(CraftItemEvent event) {
//		if (event.getInventory() == null) {
//			return;
//		}
//		
//		ItemStack resultStack = event.getInventory().getResult();
//		if (resultStack == null) {
//			return;
//		}
//		
//		if (CivSettings.techItems == null) {
//			CivLog.error("tech items null???");
//			return;
//		}
//
//		//XXX Replaced via materials system.
////		ConfigTechItem item = CivSettings.techItems.get(resultStack.getTypeId());
////		if (item != null) {
////			Resident resident = CivGlobal.getResident(event.getWhoClicked().getName());
////			if (resident != null && resident.hasTown()) {
////				if (resident.getCiv().hasTechnology(item.require_tech)) {
////					return;
////				}
////			}	
////			event.setCancelled(true);
////			CivMessage.sendError((Player)event.getWhoClicked(), "You do not have the required technology to craft a "+item.name);
////		}
//	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPortalEvent(PlayerPortalEvent event) {
		if(event.getCause().equals(TeleportCause.END_PORTAL)) {
			event.setCancelled(true);
			CivMessage.sendErrorNoRepeat(event.getPlayer(), CivSettings.localize.localizedString("playerListen_endDisabled"));
			return;
		}
		
		if (event.getCause().equals(TeleportCause.NETHER_PORTAL)) {
			event.setCancelled(true);
			CivMessage.sendErrorNoRepeat(event.getPlayer(), CivSettings.localize.localizedString("playerListen_netherDisabled"));
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		// THIS EVENT IS NOT RUN IN OFFLINE MODE
	}

	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void OnPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
		Resident resident = CivGlobal.getResident(event.getPlayer());
	
		if (resident == null) {
			event.setCancelled(true);
			return;
		}

		ChunkCoord coord = new ChunkCoord(event.getBlockClicked().getLocation());
		CultureChunk cc = CivGlobal.getCultureChunk(coord);
		if (cc != null) {
			if (event.getBucket().equals(Material.LAVA_BUCKET) || 
					event.getBucket().equals(Material.LAVA)) {
				
				if (!resident.hasTown() || (resident.getTown().getCiv() != cc.getCiv())) {
					CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("playerListen_placeLavaDenied"));
					event.setCancelled(true);
					return;
				}
				
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void OnBrewEvent(BrewEvent event) {
		/* Hardcoded disables based on ingredients used. */
		if (event.getContents().contains(Material.SPIDER_EYE) ||
			event.getContents().contains(Material.GOLDEN_CARROT) ||
			event.getContents().contains(Material.GHAST_TEAR) ||
			event.getContents().contains(Material.FERMENTED_SPIDER_EYE) ||
			event.getContents().contains(Material.BLAZE_POWDER) ||
			event.getContents().contains(Material.GUNPOWDER)) {
			event.setCancelled(true);
		}
		
		if (event.getContents().contains(Material.POTION)) {
			ItemStack potion = event.getContents().getItem(event.getContents().first(Material.POTION));
			
			if (potion.getDurability() == CivData.MUNDANE_POTION_DATA || 
				potion.getDurability() == CivData.MUNDANE_POTION_EXT_DATA ||
				potion.getDurability() == CivData.THICK_POTION_DATA) {
				event.setCancelled(true);
			}
		}
	}
	
	private boolean isPotionDisabled(PotionEffect type) {
		if (type.getType().equals(PotionEffectType.SPEED) ||
			type.getType().equals(PotionEffectType.FIRE_RESISTANCE) ||
			type.getType().equals(PotionEffectType.HEAL)) {
			return false;
		}
		
		return true;
	}
	
	@EventHandler(priority = EventPriority.LOW) 
	public void onPotionSplash(PotionSplashEvent event) {
		ThrownPotion potion = event.getPotion();

		if (!(potion.getShooter() instanceof Player)) {
			return;
		} 
		for (PotionEffect effect : event.getPotion().getEffects()) {
			if (isPotionDisabled(effect)) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW) 
	public void onConsume(PlayerItemConsumeEvent event) {
		if (ItemManager.getType(event.getItem()) == CivData.GOLDEN_APPLE) {
			CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("itemUse_errorGoldenApple"));
			event.setCancelled(true);
			return;
		}
		
		if (event.getItem().getType().equals(Material.POTION)) {
			PotionMeta meta = (PotionMeta) event.getItem().getItemMeta();
			for (PotionEffect effect : meta.getCustomEffects()) {
				String name = effect.getType().getName();
				Integer amp = effect.getAmplifier();
				ConfigTechPotion pot = CivSettings.techPotions.get(""+name+amp);
				if (pot != null) {
					if (!pot.hasTechnology(event.getPlayer())) {
						CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("var_playerListen_potionNoTech",pot.name));
						event.setCancelled(true);
						return;
					}
					if (pot.hasTechnology(event.getPlayer())) {
						event.setCancelled(false);
						return;
					}
				} else {
					CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("playerListen_denyUse"));
					event.setCancelled(true);
					return;
				}
			}
			
			
			
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryOpenEvent(InventoryOpenEvent event) {
		if (event.getInventory() instanceof DoubleChestInventory) {
			DoubleChestInventory doubleInv = (DoubleChestInventory)event.getInventory();
						
			Chest leftChest = (Chest)doubleInv.getHolder().getLeftSide();			
			/*Generate a new player 'switch' event for the left and right chests. */
			PlayerInteractEvent interactLeft = new PlayerInteractEvent((Player)event.getPlayer(), Action.RIGHT_CLICK_BLOCK, null, leftChest.getBlock(), null);
			BlockListener.OnPlayerSwitchEvent(interactLeft);
			
			if (interactLeft.isCancelled()) {
				event.setCancelled(true);
				return;
			}
			
			Chest rightChest = (Chest)doubleInv.getHolder().getRightSide();
			PlayerInteractEvent interactRight = new PlayerInteractEvent((Player)event.getPlayer(), Action.RIGHT_CLICK_BLOCK, null, rightChest.getBlock(), null);
			BlockListener.OnPlayerSwitchEvent(interactRight);
			
			if (interactRight.isCancelled()) {
				event.setCancelled(true);
				return;
			}			
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Player attacker;
		Player defender;
		String damage;
		
		if (event.getEntity() instanceof Player) {
			defender = (Player)event.getEntity();
		} else {
			defender = null;
		}
		
		if (event.getDamager() instanceof Player) {
			attacker = (Player)event.getDamager();
		} else if (event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow)event.getDamager();
			if (arrow.getShooter() instanceof Player) {
				attacker = (Player)arrow.getShooter();
			} else {
				attacker = null;
			}
		} else {
			attacker = null;
		}
		
		if (attacker == null && defender == null) {
			return;
		}
		
		damage = new DecimalFormat("#.#").format(event.getDamage());
		
		if (defender != null) {
			Resident defenderResident = CivGlobal.getResident(defender);
			if (defenderResident.isCombatInfo()) {	
				if (attacker != null) {
					CivMessage.send(defender, CivColor.LightGray+"   "+CivSettings.localize.localizedString("playerListen_combatHeading")+" "+CivSettings.localize.localizedString("var_playerListen_combatDefend",CivColor.Rose+attacker.getName()+CivColor.LightGray,CivColor.Rose+damage+CivColor.LightGray));				
				} else {
					String entityName = null;
					
					if (event.getDamager() instanceof LivingEntity) {
						entityName = ((LivingEntity)event.getDamager()).getCustomName();
					}
					
					if (entityName == null) {
						entityName = event.getDamager().getType().toString();
					}
					
					CivMessage.send(defender, CivColor.LightGray+"   "+CivSettings.localize.localizedString("playerListen_combatHeading")+" "+CivSettings.localize.localizedString("var_playerListen_combatDefend",CivColor.LightPurple+entityName+CivColor.LightGray,CivColor.Rose+damage+CivColor.LightGray));
				}
			}
		}
		
		if (attacker != null) {
			Resident attackerResident = CivGlobal.getResident(attacker);
			if (attackerResident.isCombatInfo()) {
				if (defender != null) {
					CivMessage.send(attacker, CivColor.LightGray+"   "+CivSettings.localize.localizedString("playerListen_combatHeading")+" "+CivSettings.localize.localizedString("var_playerListen_attack",CivColor.Rose+defender.getName()+CivColor.LightGray,CivColor.LightGreen+damage+CivColor.LightGray));
				} else {
					String entityName = null;
					
					if (event.getEntity() instanceof LivingEntity) {
						entityName = ((LivingEntity)event.getEntity()).getCustomName();
					}
					
					if (entityName == null) {
						entityName = event.getEntity().getType().toString();
					}
					
					CivMessage.send(attacker, CivColor.LightGray+"   "+CivSettings.localize.localizedString("playerListen_combatHeading")+" "+CivSettings.localize.localizedString("var_playerListen_attack",CivColor.LightPurple+entityName+CivColor.LightGray,CivColor.LightGreen+damage+CivColor.LightGray));
				}
			}
		}
		
		
		
		
	}
}
