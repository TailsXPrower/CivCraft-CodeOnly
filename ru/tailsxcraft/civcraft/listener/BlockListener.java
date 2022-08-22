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

import gpl.HorseModifier;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_16_R3.AttributeModifiable;
import net.minecraft.server.v1_16_R3.AxisAlignedBB;
import net.minecraft.server.v1_16_R3.DamageSource;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.GenericAttributes;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import ru.tailsxcraft.civcraft.cache.ArrowFiredCache;
import ru.tailsxcraft.civcraft.cache.CannonFiredCache;
import ru.tailsxcraft.civcraft.cache.CivCache;
import ru.tailsxcraft.civcraft.camp.Camp;
import ru.tailsxcraft.civcraft.camp.CampBlock;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.main.CivData;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.ControlPoint;
import ru.tailsxcraft.civcraft.object.ProtectedBlock;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.StructureBlock;
import ru.tailsxcraft.civcraft.object.StructureChest;
import ru.tailsxcraft.civcraft.object.StructureSign;
import ru.tailsxcraft.civcraft.object.TownChunk;
import ru.tailsxcraft.civcraft.permission.PlotPermissions;
import ru.tailsxcraft.civcraft.structure.Buildable;
import ru.tailsxcraft.civcraft.structure.BuildableLayer;
import ru.tailsxcraft.civcraft.structure.CannonShip;
import ru.tailsxcraft.civcraft.structure.CannonTower;
import ru.tailsxcraft.civcraft.structure.Farm;
import ru.tailsxcraft.civcraft.structure.Pasture;
import ru.tailsxcraft.civcraft.structure.Stable;
import ru.tailsxcraft.civcraft.structure.Wall;
import ru.tailsxcraft.civcraft.structure.farm.FarmChunk;
import ru.tailsxcraft.civcraft.structure.wonders.Battledome;
import ru.tailsxcraft.civcraft.structure.wonders.GrandShipIngermanland;
import ru.tailsxcraft.civcraft.threading.CivAsyncTask;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.threading.tasks.FireWorkTask;
import ru.tailsxcraft.civcraft.threading.tasks.StructureBlockHitEvent;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.util.ChunkCoord;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.civcraft.util.ItemFrameStorage;
import ru.tailsxcraft.civcraft.util.ItemManager;
import ru.tailsxcraft.civcraft.util.TimeTools;
import ru.tailsxcraft.civcraft.war.War;
import ru.tailsxcraft.civcraft.war.WarRegen;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class BlockListener implements Listener {

	/* Experimental, reuse the same object because it is single threaded. */
	public static ChunkCoord coord = new ChunkCoord("", 0, 0);
	public static BlockCoord bcoord = new BlockCoord("", 0,0,0);

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTameEvent(EntityTameEvent event) {
		if (event.getEntity() instanceof Wolf) {
			Wolf wolf = (Wolf) event.getEntity();
			if (wolf.getName().contains("Direwolf")) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSlimeSplitEvent(SlimeSplitEvent event) {
		if (event.getEntity() instanceof Slime) {
			Slime slime = (Slime) event.getEntity();
			if (slime.getName().contains("Brutal") ||
					slime.getName().contains("Elite") ||
					slime.getName().contains("Greater") ||
					slime.getName().contains("Lesser")) {
				slime.setSize(0);
				event.setCancelled(true);
			}
		}
	}	

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockIgniteEvent(BlockIgniteEvent event) {
	//	CivLog.debug("block ignite event");

		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					Block b = event.getBlock().getRelative(x, y, z);		
					bcoord.setFromLocation(b.getLocation());
					StructureBlock sb = CivGlobal.getStructureBlock(bcoord);
					if (sb != null) {
						if (b.getType().isBurnable()) {
							event.setCancelled(true);
						}
						return;
					}

					CampBlock cb = CivGlobal.getCampBlock(bcoord);
					if (cb != null) {
						event.setCancelled(true);
						return;
					}

					StructureSign structSign = CivGlobal.getStructureSign(bcoord);
					if (structSign != null) {
						event.setCancelled(true);
						return;
					}

					StructureChest structChest = CivGlobal.getStructureChest(bcoord);
					if (structChest != null) {
						event.setCancelled(true);
						return;
					}
				}
			}
	    }


		coord.setFromLocation(event.getBlock().getLocation());
		TownChunk tc = CivGlobal.getTownChunk(coord);

		if (tc == null) {
			return;
		}

		if (tc.perms.isFire() == false) {
			CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("fireDisabledInChunk"));
			event.setCancelled(true);
		}		
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityBlockChange(EntityChangeBlockEvent event) {
		bcoord.setFromLocation(event.getBlock().getLocation());

		StructureBlock sb = CivGlobal.getStructureBlock(bcoord);
		if (sb != null) {
			event.setCancelled(true);
			return;
		}
		
		CampBlock cb = CivGlobal.getCampBlock(bcoord);
		if (cb != null) {
			event.setCancelled(true);
			return;
		}

		return;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBurnEvent(BlockBurnEvent event) {
		bcoord.setFromLocation(event.getBlock().getLocation());

		StructureBlock sb = CivGlobal.getStructureBlock(bcoord);
		if (sb != null) {
			event.setCancelled(true);
			return;
		}

		CampBlock cb = CivGlobal.getCampBlock(bcoord);
		if (cb != null) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onProjectileHitEvent(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow) {
			ArrowFiredCache afc = CivCache.arrowsFired.get(event.getEntity().getUniqueId());
			if (afc != null) {
				afc.setHit(true);
			}
		}
		
		if (event.getEntity() instanceof Fireball) {
			CannonFiredCache cfc = CivCache.cannonBallsFired.get(event.getEntity().getUniqueId());
			if (cfc != null) {

				cfc.setHit(true);

				FireworkEffect fe = FireworkEffect.builder().withColor(Color.RED).withColor(Color.BLACK).flicker(true).with(Type.BURST).build();

				Random rand = new Random();
				int spread = 30;
				for (int i = 0; i < 15; i++) {
					int x = rand.nextInt(spread) - spread/2;
					int y = rand.nextInt(spread) - spread/2;
					int z = rand.nextInt(spread) - spread/2;


					Location loc = event.getEntity().getLocation();
					Location location = new Location(loc.getWorld(), loc.getX(),loc.getY(), loc.getZ());
					location.add(x, y, z);

					TaskMaster.syncTask(new FireWorkTask(fe, loc.getWorld(), loc, 5), rand.nextInt(30));
				}

			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		/* Protect the Protected Item Frames! */
		if (event.getEntity() instanceof ItemFrame) {
			ItemFrameStorage iFrameStorage = CivGlobal.getProtectedItemFrame(event.getEntity().getUniqueId());
			if (iFrameStorage != null) {
				event.setCancelled(true);
				return;
			}
		}

		if (event.getDamager() instanceof LightningStrike) {
//			CivLog.debug("onEntityDamageByEntityEvent LightningStrike: "+event.getDamager().getUniqueId());
			try {
				event.setDamage(CivSettings.getInteger(CivSettings.warConfig, "tesla_tower.damage"));
			} catch (InvalidConfiguration e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (event.getDamager() instanceof Arrow) {

		}

		if (event.getDamager() instanceof Fireball) {
			CannonFiredCache cfc = CivCache.cannonBallsFired.get(event.getDamager().getUniqueId());
			if (cfc != null) {

				cfc.setHit(true);
				cfc.destroy(event.getDamager());
				Buildable whoFired = cfc.getWhoFired();
				if (whoFired.getConfigId().equals("s_cannontower")) {
					event.setDamage((double)((CannonTower)whoFired).getDamage());
				} else if (whoFired.getConfigId().equals("s_cannonship")) {
					event.setDamage((double)((CannonShip)whoFired).getDamage());
				} else if (whoFired.getConfigId().equals("w_grand_ship_ingermanland")) {
					event.setDamage((double)((GrandShipIngermanland)whoFired).getCannonDamage());
				}
			}
		}
		
		if (event.getEntity() instanceof Player) {

			/* Only protect against players and entities that players can throw. */
			if (!CivSettings.playerEntityWeapons.contains(event.getDamager().getType())) {
				return;
			}

			Player defender = (Player)event.getEntity();

		coord.setFromLocation(event.getEntity().getLocation());
		TownChunk tc = CivGlobal.getTownChunk(coord);
		boolean allowPVP = false;
		String denyMessage = "";

		if (tc == null) {
			/* In the wilderness, anything goes. */
			allowPVP = true;
		} else {	
			Player attacker = null;
			if (event.getDamager() instanceof Player) {
				attacker = (Player)event.getDamager();
			} else if (event.getDamager() instanceof Projectile) {
				LivingEntity shooter = (LivingEntity) ((Projectile)event.getDamager()).getShooter();
				if (shooter instanceof Player) {
					attacker = (Player) shooter;
				}
			} 

			if (attacker == null) {
				/* Attacker wasnt a player or known projectile, allow it. */
				allowPVP = true;
			} else {
				switch(playersCanPVPHere(attacker, defender, tc)) {
				case ALLOWED:
					allowPVP = true;
					break;
				case NOT_AT_WAR:
					allowPVP = false;
					denyMessage = CivSettings.localize.localizedString("var_pvpError1",defender.getName());
					break;
				case NEUTRAL_IN_WARZONE:
					allowPVP = false;
					denyMessage = CivSettings.localize.localizedString("var_pvpError2",defender.getName());
					break;
				case NON_PVP_ZONE:
					allowPVP = false;
					denyMessage = CivSettings.localize.localizedString("var_pvpError3",defender.getName());
					break;
				}
			}

			if (!allowPVP) {
				CivMessage.sendError(attacker, denyMessage);
				event.setCancelled(true);
			} else {

			}
		}
		}

		return;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void OnCreateSpawnEvent(CreatureSpawnEvent event) {

		if (event.getSpawnReason().equals(SpawnReason.BREEDING)) {
			ChunkCoord coord = new ChunkCoord(event.getEntity().getLocation());
			Pasture pasture = Pasture.pastureChunks.get(coord);

			if (pasture != null) {
				pasture.onBreed(event.getEntity());
			}
		}

		class SyncTask implements Runnable {
			LivingEntity entity;

			public SyncTask(LivingEntity entity) {
				this.entity = entity;
			}

			@Override
			public void run() {
				if (entity != null) {
					if (!HorseModifier.isCivCraftHorse(entity)) {
						CivLog.warning("Removing a normally spawned horse.");
						entity.remove();
					}
				}
			}
		}

		if (event.getEntityType() == EntityType.HORSE) {
			ChunkCoord coord = new ChunkCoord(event.getEntity().getLocation());
			Stable stable = Stable.stableChunks.get(coord);
			if (stable != null) {
				return;
			}
			
			if (event.getSpawnReason().equals(SpawnReason.DEFAULT)) {
				TaskMaster.syncTask(new SyncTask(event.getEntity()));
				return;
			}
			
			if (event.getSpawnReason().equals(SpawnReason.CUSTOM)) {
				return;
			}
			
			CivLog.warning("Canceling horse spawn reason:"+event.getSpawnReason().name());
			event.setCancelled(true);
		}

		coord.setFromLocation(event.getLocation());
		TownChunk tc = CivGlobal.getTownChunk(coord);
		if (tc == null) {
			return;
		}

		if (tc.perms.isMobs() == false) {
			if (event.getSpawnReason().equals(SpawnReason.CUSTOM)) {
				return;
			}
			ChunkCoord coord = new ChunkCoord(event.getEntity().getLocation());
			Battledome battledome = Battledome.battledomeChunks.get(coord);

			if (battledome != null) {
				return;
			}

			if (CivSettings.restrictedSpawns.containsKey(event.getEntityType())) {
				event.setCancelled(true);
				return;
			}
		}		
	}


	@EventHandler(priority = EventPriority.NORMAL)
	public void OnEntityExplodeEvent(EntityExplodeEvent event) {

		if (event.getEntity() == null) {
			return;
		}
		/* prevent ender dragons from breaking blocks. */
		if (event.getEntityType().equals(EntityType.ENDER_DRAGON)) {
			event.setCancelled(true);
		}

		for (Block block : event.blockList()) {
			bcoord.setFromLocation(block.getLocation());
			StructureBlock sb = CivGlobal.getStructureBlock(bcoord);
			if (sb != null) {
				event.setCancelled(true);
				return;
			}

			CampBlock cb = CivGlobal.getCampBlock(bcoord);
			if (cb != null) {
				event.setCancelled(true);
				return;
			}

			StructureSign structSign = CivGlobal.getStructureSign(bcoord);
			if (structSign != null) {
				event.setCancelled(true);
				return;
			}

			StructureChest structChest = CivGlobal.getStructureChest(bcoord);
			if (structChest != null) {
				event.setCancelled(true);
				return;
			}

			coord.setFromLocation(block.getLocation());

			HashSet<Wall> walls = CivGlobal.getWallChunk(coord);
			if (walls != null) {
				for (Wall wall : walls) {
					if (wall.isProtectedLocation(block.getLocation())) {
						event.setCancelled(true);
						return;
					}
				}
			}

			TownChunk tc = CivGlobal.getTownChunk(coord);
			if (tc == null) {
				continue;
			}
			event.setCancelled(true);
			return;
		}

	}

     private final BlockFace[] faces = new BlockFace[] {
			        BlockFace.DOWN,
		            BlockFace.NORTH,
		            BlockFace.EAST,
		            BlockFace.SOUTH,
		            BlockFace.WEST,		            
		            BlockFace.SELF,
		            BlockFace.UP
	  };

    public BlockCoord generatesCobble(Material id, Block b)
    {
        Material mirrorID1 = (id == CivData.WATER_RUNNING || id == CivData.WATER ? CivData.LAVA_RUNNING : CivData.WATER_RUNNING);
        Material mirrorID2 = (id == CivData.WATER_RUNNING || id == CivData.WATER ? CivData.LAVA : CivData.WATER);
        for(BlockFace face : faces)
        {
            Block r = b.getRelative(face, 1);
            if(ItemManager.getType(r) == mirrorID1 || ItemManager.getType(r) == mirrorID2)
            {
            	
            	return new BlockCoord(r);
            }
        }
        
        return null;
    }

//    private static void destroyLiquidRecursive(Block source) {
//    	//source.setTypeIdAndData(CivData.AIR, (byte)0, false);
//    	NMSHandler nms = new NMSHandler();
//    	nms.setBlockFast(source.getWorld(), source.getX(), source.getY(), source.getZ(), 0, (byte)0);
//    	
//    	for (BlockFace face : BlockFace.values()) {
//    		Block relative = source.getRelative(face);
//    		if (relative == null) {
//    			continue;
//    		}
//    		
//    		if (!isLiquid(relative.getTypeId())) {
//    			continue;
//    		}
//    		
//    		destroyLiquidRecursive(relative);
//    	}
//    }
    
//    private static boolean isLiquid(int id) {
//    	return (id >= CivData.WATER && id <= CivData.LAVA);
//    }
    
    private static HashSet<BlockCoord> stopCobbleTasks = new HashSet<BlockCoord>();
	@EventHandler(priority = EventPriority.NORMAL)
	public void OnBlockFromToEvent(BlockFromToEvent event) {
		/* Disable cobblestone generators. */
		Material id = ItemManager.getType(event.getBlock());
	    if(id == CivData.WATER && id == CivData.LAVA)
	    {
	        Block b = event.getToBlock();
	        bcoord.setFromLocation(b.getLocation());

	        Material toid = ItemManager.getType(b);
	        if(toid == Material.AIR)
	        {
	            BlockCoord other = generatesCobble(id, b);
	        	if(other != null)
	            {
	            	//BlockCoord d = new BlockCoord(event.getToBlock());
//	            	BlockCoord fromCoord = new BlockCoord(event.getBlock());
	            	event.setCancelled(true);

	            	class SyncTask implements Runnable {
	            		BlockCoord block;

	            		public SyncTask(BlockCoord block) {
	            			this.block = block;
	            		}

						@Override
						public void run() {
							ItemManager.setTypeIdAndData(block.getBlock(), CivData.NETHERRACK, Bukkit.createBlockData(CivData.NETHERRACK), true);
							stopCobbleTasks.remove(block);
						}
	            	}

	            	if (!stopCobbleTasks.contains(other)) {
	            		stopCobbleTasks.add(other);
	            		TaskMaster.syncTask(new SyncTask(other), 2);
	            	}

//	            	if (!stopCobbleTasks.contains(fromCoord)) {
//	            		stopCobbleTasks.add(fromCoord);
//	            		TaskMaster.syncTask(new SyncTask(fromCoord));
//	            	}
	            }
	        }
	    }
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void OnBlockFormEvent (BlockFormEvent event) {

		/* Disable cobblestone generators. */
		if (ItemManager.getType(event.getNewState()) == CivData.COBBLESTONE) {
			ItemManager.setType(event.getNewState(), CivData.GRAVEL);
			return;
		}

		Chunk spreadChunk = event.getNewState().getChunk();
		coord.setX(spreadChunk.getX());
		coord.setZ(spreadChunk.getZ());
		coord.setWorldname(spreadChunk.getWorld().getName());

		TownChunk tc = CivGlobal.getTownChunk(coord);
		if (tc == null) {
			return;
		}

		if (tc.perms.isFire() == false) {
			if(event.getNewState().getType() == Material.FIRE) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void OnBlockPlaceEvent(BlockPlaceEvent event) {
		Resident resident = CivGlobal.getResident(event.getPlayer());

		if (resident == null) {
			event.setCancelled(true);
			return;
		}

		if(resident.isSBPermOverride()) {
			return;
		}

		bcoord.setFromLocation(event.getBlockAgainst().getLocation());
		StructureSign sign = CivGlobal.getStructureSign(bcoord);
		if (sign != null) {
			event.setCancelled(true);
			return;
		}

		bcoord.setFromLocation(event.getBlock().getLocation());
		StructureBlock sb = CivGlobal.getStructureBlock(bcoord);
		if (sb != null) {
			event.setCancelled(true);
			CivMessage.sendError(event.getPlayer(), 
					CivSettings.localize.localizedString("blockBreak_errorStructure")+" "+sb.getOwner().getDisplayName()+" "+CivSettings.localize.localizedString("blockBreak_errorOwnedBy")+" "+sb.getTown().getName());
			return;
		}

		CampBlock cb = CivGlobal.getCampBlock(bcoord);
		if (cb != null && !cb.canBreak(event.getPlayer().getName())) {
			event.setCancelled(true);
			CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("blockBreak_errorCamp1")+" "+cb.getCamp().getName()+" "+CivSettings.localize.localizedString("blockBreak_errorOwnedBy")+" "+cb.getCamp().getOwner().getName());
			return;
		}  		

		coord.setFromLocation(event.getBlock().getLocation());
		TownChunk tc = CivGlobal.getTownChunk(coord);
		if (CivSettings.blockPlaceExceptions.get(event.getBlock().getType()) != null) {
			return;
		}

		if (tc != null) {	
			if(!tc.perms.hasPermission(PlotPermissions.Type.BUILD, resident)) {
				if (War.isWarTime() && resident.hasTown() && 
						resident.getTown().getCiv().getDiplomacyManager().atWarWith(tc.getTown().getCiv())) {
					if (WarRegen.canPlaceThisBlock(event.getBlock())) {
						WarRegen.saveBlock(event.getBlock(), tc.getTown().getName(), true);
						return;
					} else {
						CivMessage.sendErrorNoRepeat(event.getPlayer(), CivSettings.localize.localizedString("blockPlace_errorWar"));
						event.setCancelled(true);
						return;
					}
				} else {
					event.setCancelled(true);
					CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("blockPlace_errorPermission")+" "+tc.getTown().getName());
				}
			} 
		}

		/* Check if we're going to break too many structure blocks beneath a structure. */
		//LinkedList<StructureBlock> sbList = CivGlobal.getStructureBlocksAt(bcoord.getWorldname(), bcoord.getX(), bcoord.getZ());
		HashSet<Buildable> buildables = CivGlobal.getBuildablesAt(bcoord);
		if (buildables != null) {
			for (Buildable buildable : buildables) {		
				if (!buildable.validated) {
					try {
						buildable.validate(event.getPlayer());
					} catch (CivException e) {
						e.printStackTrace();
					}
					continue;
				}

				/* Building is validated, grab the layer and determine if this would set it over the limit. */
				BuildableLayer layer = buildable.layerValidPercentages.get(bcoord.getY());
				if (layer == null) {
					continue;
				}

				/* Update the layer. */
				layer.current += Buildable.getReinforcementValue(ItemManager.getType(event.getBlockPlaced()));
				if (layer.current < 0) {
					layer.current = 0;
				}
				buildable.layerValidPercentages.put(bcoord.getY(), layer);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void OnBlockBreakEvent(BlockBreakEvent event) {
		Resident resident = CivGlobal.getResident(event.getPlayer());

		if (resident == null) {
			event.setCancelled(true);
			return;
		}

		if (resident.isSBPermOverride()) {
			return;
		}

		bcoord.setFromLocation(event.getBlock().getLocation());
		StructureBlock sb = CivGlobal.getStructureBlock(bcoord);

		if (sb != null) {
			event.setCancelled(true);
			TaskMaster.syncTask(new StructureBlockHitEvent(event.getPlayer().getName(), bcoord, sb, event.getBlock().getWorld()), 0);
			return;
		}

		ProtectedBlock pb = CivGlobal.getProtectedBlock(bcoord);
		if (pb != null) {
			event.setCancelled(true);
			CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("blockBreak_errorProtected"));
			return;
		}

		CampBlock cb = CivGlobal.getCampBlock(bcoord);
		if (cb != null && !cb.canBreak(event.getPlayer().getName())) {
			ControlPoint cBlock = cb.getCamp().controlBlocks.get(bcoord);
			if (cBlock != null || !cb.getCamp().hasMember(event.getPlayer().getName())) {
				cb.getCamp().onDamage(1, event.getBlock().getWorld(), event.getPlayer(), bcoord, null);
				event.setCancelled(true);
				return;
			} else {	
				event.setCancelled(true);
				CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("blockBreak_errorCamp1")+" "+cb.getCamp().getName()+" "+CivSettings.localize.localizedString("blockBreak_errorOwnedBy")+" "+cb.getCamp().getOwner().getName());
				return;
			}
		}

		StructureSign structSign = CivGlobal.getStructureSign(bcoord);
		if (structSign != null && !resident.isSBPermOverride()) {
			event.setCancelled(true);
			CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("blockBreak_errorStructureSign"));
			return;
		}

		StructureChest structChest = CivGlobal.getStructureChest(bcoord);
		if (structChest != null && !resident.isSBPermOverride()) {
			event.setCancelled(true);
			CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("blockBreak_errorStructureChests"));
			return;
		}

		coord.setFromLocation(event.getBlock().getLocation());
		HashSet<Wall> walls = CivGlobal.getWallChunk(coord);

		if (walls != null) {
			for (Wall wall : walls) {
				if (wall.isProtectedLocation(event.getBlock().getLocation())) {
					if (resident == null || !resident.hasTown() || resident.getTown().getCiv() != wall.getTown().getCiv() && !resident.isSBPermOverride()) {
						
						StructureBlock tmpStructureBlock = new StructureBlock(bcoord, wall);
						tmpStructureBlock.setAlwaysDamage(true);
						TaskMaster.syncTask(new StructureBlockHitEvent(event.getPlayer().getName(), bcoord, tmpStructureBlock, event.getBlock().getWorld()), 0);
						//CivMessage.sendError(event.getPlayer(), "Cannot destroy this block, protected by a wall, destroy it first.");
						event.setCancelled(true);
						return;
					} else {
						CivMessage.send(event.getPlayer(), CivColor.LightGray+CivSettings.localize.localizedString("blockBreak_wallAlert")+" "+
								resident.getTown().getCiv().getName());
						break;
					}
				}
			}
		}

		TownChunk tc = CivGlobal.getTownChunk(coord);
		if (tc != null) {
			if(!tc.perms.hasPermission(PlotPermissions.Type.DESTROY, resident)) {
				event.setCancelled(true);

				if (War.isWarTime() && resident.hasTown() && 
						resident.getTown().getCiv().getDiplomacyManager().atWarWith(tc.getTown().getCiv())) {
					if ( event.getBlock().getLocation().getBlockY() < 50) {
						event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) TimeTools.toTicks(30), 2));
						event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (int) TimeTools.toTicks(30), 1));
					} else {
						event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) TimeTools.toTicks(30), 0));
						event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (int) TimeTools.toTicks(30), 0));
					}
					WarRegen.destroyThisBlock(event.getBlock(), tc.getTown());
				} else {
					CivMessage.sendErrorNoRepeat(event.getPlayer(), CivSettings.localize.localizedString("blockBreak_errorPermission")+" "+tc.getTown().getName());
				}
			}
		}

		/* Check if we're going to break too many structure blocks beneath a structure. */
		//LinkedList<StructureBlock> sbList = CivGlobal.getStructureBlocksAt(bcoord.getWorldname(), bcoord.getX(), bcoord.getZ());
		HashSet<Buildable> buildables = CivGlobal.getBuildablesAt(bcoord);
		if (buildables != null) {
			for (Buildable buildable : buildables) {
				if (!buildable.validated) {
					try {
						buildable.validate(event.getPlayer());
					} catch (CivException e) {
						e.printStackTrace();
					}
					continue;
				}

				/* Building is validated, grab the layer and determine if this would set it over the limit. */
				BuildableLayer layer = buildable.layerValidPercentages.get(bcoord.getY());
				if (layer == null) {
					continue;
				}

				double current = layer.current - Buildable.getReinforcementValue(ItemManager.getType(event.getBlock()));
				if (current < 0) {
					current = 0;
				}
				Double percentValid = (double)(current) / (double)layer.max;

				if (percentValid < Buildable.validPercentRequirement) {
					CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("blockBreak_errorSupport")+" "+buildable.getDisplayName());
					event.setCancelled(true);
					return;
				}

				/* Update the layer. */
				layer.current = (int)current;
				buildable.layerValidPercentages.put(bcoord.getY(), layer);
			}
		}

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void OnEntityInteractEvent(EntityInteractEvent event) {
		if (event.getBlock() != null) {			
			if (CivSettings.switchItems.contains(event.getBlock().getType())) {
				coord.setFromLocation(event.getBlock().getLocation());
				TownChunk tc = CivGlobal.getTownChunk(coord);

				if (tc == null) {
					return;
				}

				/* A non-player entity is trying to trigger something, if interact permission is
				 * off for others then disallow it.
				 */
				if (tc.perms.interact.isPermitOthers()) {
					return;
				}

				if (event.getEntity() instanceof Player) {
					CivMessage.sendErrorNoRepeat((Player)event.getEntity(), CivSettings.localize.localizedString("blockUse_errorPermission"));
				}

				event.setCancelled(true);
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void OnPlayerConsumeEvent(PlayerItemConsumeEvent event) {
		ItemStack stack = event.getItem();

		/* Disable notch apples */
		if (ItemManager.getType(event.getItem()) == ItemManager.getType(Material.GOLDEN_APPLE)) {
			if (event.getItem().getDurability() == (short)0x1) {
				CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("itemUse_errorGoldenApple"));
				event.setCancelled(true);
				return;
			}
		}	

		if (stack.getType().equals(Material.POTION)) {
			int effect = event.getItem().getDurability() & 0x000F;			
			if (effect == 0xE) {
				event.setCancelled(true);
				CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("itemUse_errorInvisPotion"));
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL) 
	public void onBlockDispenseEvent(BlockDispenseEvent event) {
		ItemStack stack = event.getItem();
		if (stack != null) {
			if (event.getItem().getType().equals(Material.POTION)) {
				int effect = event.getItem().getDurability() & 0x000F;			
				if (effect == 0xE) { 
					event.setCancelled(true);
					return;
				}
			}

			if (event.getItem().getType().equals(Material.INK_SAC)) {
				//if (event.getItem().getDurability() == 15) { 
					event.setCancelled(true);
					return;
				//}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void OnPlayerInteractEvent(PlayerInteractEvent event) {
		Resident resident = CivGlobal.getResident(event.getPlayer());

		if (resident == null) {
			event.setCancelled(true);
			return;
		}

		if (event.isCancelled()) {
			// Fix for bucket bug.
			if (event.getAction() == Action.RIGHT_CLICK_AIR) {
				Material item = ItemManager.getType(event.getPlayer().getInventory().getItemInMainHand());
				// block cheats for placing water/lava/fire/lighter use.
				if (item == Material.WATER_BUCKET || item == Material.LAVA_BUCKET || item == Material.FLINT_AND_STEEL ) { 
					event.setCancelled(true);
				}
			}
			return;
		}		

		if (event.hasItem()) {

			if (event.getItem().getType().equals(Material.POTION)) {
				int effect = event.getItem().getDurability() & 0x000F;			
				if (effect == 0xE) {
					event.setCancelled(true);
					CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("itemUse_errorInvisPotion"));
					return;
				}
			}

			if (event.getItem().getType().equals(Material.INK_SAC) && event.getItem().getDurability() == 15) {
				Block clickedBlock = event.getClickedBlock();
				if (ItemManager.getType(clickedBlock) == CivData.WHEAT || 
					ItemManager.getType(clickedBlock) == CivData.CARROTS || 
					ItemManager.getType(clickedBlock) == CivData.POTATOES) {
					event.setCancelled(true);
					CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("itemUse_errorBoneMeal"));
					return;
				}
			}
		}

		Block soilBlock = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);

		// prevent players trampling crops
		if ((event.getAction() == Action.PHYSICAL)) {
			if ((soilBlock.getType() == Material.LEGACY_SOIL) || (soilBlock.getType() == Material.LEGACY_CROPS)) {
				//CivLog.debug("no crop cancel.");
				event.setCancelled(true);
				return;	
			}
		}
		/* 
		 * Right clicking causes some dupe bugs for some reason with items that have "actions" such as swords.
		 * It also causes block place events on top of signs. So we'll just only allow signs to work with left click.
		 */
		boolean leftClick = event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK);

		if (event.getClickedBlock() != null) {		

			if (MarkerPlacementManager.isPlayerInPlacementMode(event.getPlayer())) {
				Block block;
				if (event.getBlockFace().equals(BlockFace.UP)) {
					block = event.getClickedBlock().getRelative(event.getBlockFace());
				} else {
					block = event.getClickedBlock();
				}

				try {
					MarkerPlacementManager.setMarker(event.getPlayer(), block.getLocation());
					CivMessage.send(event.getPlayer(), CivColor.LightGreen+CivSettings.localize.localizedString("itemUse_marked"));
				} catch (CivException e) {
					CivMessage.send(event.getPlayer(), CivColor.Rose+e.getMessage());
				}

				event.setCancelled(true);
				return;
			}

			// Check for clicked structure signs.
			bcoord.setFromLocation(event.getClickedBlock().getLocation());
			StructureSign sign = CivGlobal.getStructureSign(bcoord);
			if (sign != null) {

				if (leftClick || sign.isAllowRightClick()) {
					if (sign.getOwner() != null && sign.getOwner().isActive()) {
						try {
							sign.getOwner().processSignAction(event.getPlayer(), sign, event);
							event.setCancelled(true);
						} catch (CivException e) {
							CivMessage.send(event.getPlayer(), CivColor.Rose+e.getMessage());
							event.setCancelled(true);
							return;
						}
					}
				}
				return;
			}
			if (CivSettings.switchItems.contains(event.getClickedBlock().getType())) {
				OnPlayerSwitchEvent(event);
				if (event.isCancelled()) {
					return;
				}
			}
		}

		if (event.hasItem()) {

			if (event.getItem() == null) {
			} else {
				if (CivSettings.restrictedItems.containsKey(event.getItem().getType())) {
					OnPlayerUseItem(event);
					if (event.isCancelled()) {
						return;
					}
				}
			}
		}

	}
	
	public void OnPlayerBedEnterEvent(PlayerBedEnterEvent event) {
		
		Resident resident = CivGlobal.getResident(event.getPlayer().getName());

		if (resident == null) {
			event.setCancelled(true);
			return;
		}
				
		coord.setFromLocation(event.getPlayer().getLocation());
		Camp camp = CivGlobal.getCampFromChunk(coord);
		if (camp != null) {
			if (!camp.hasMember(event.getPlayer().getName())) {
				CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("bedUse_errorNotInCamp"));
				event.setCancelled(true);
				return;
			}
		}		
	}

	public static void OnPlayerSwitchEvent(PlayerInteractEvent event) {

		if (event.getClickedBlock() == null) {
			return;
		}

		Resident resident = CivGlobal.getResident(event.getPlayer().getName());

		if (resident == null) {
			event.setCancelled(true);
			return;
		}

		bcoord.setFromLocation(event.getClickedBlock().getLocation());
		CampBlock cb = CivGlobal.getCampBlock(bcoord);
		if (cb != null && !resident.isPermOverride()) {
			if (!cb.getCamp().hasMember(resident.getName())) {
				CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("blockUse_errorNotInCamp"));
				event.setCancelled(true);
				return;
			}
		}

		coord.setFromLocation(event.getClickedBlock().getLocation());
		TownChunk tc = CivGlobal.getTownChunk(coord);

		if (tc == null) {
			return;
		}

		if (resident.hasTown()) {
			if (War.isWarTime()) {
				if(tc.getTown().getCiv().getDiplomacyManager().atWarWith(resident.getTown().getCiv())) {

					switch (event.getClickedBlock().getType()) {
					case OAK_DOOR:
					case IRON_DOOR:
					case SPRUCE_DOOR:
					case BIRCH_DOOR:
					case JUNGLE_DOOR:
					case ACACIA_DOOR:
					case DARK_OAK_DOOR:
                    case ACACIA_FENCE_GATE:
                    case BIRCH_FENCE_GATE:
                    case DARK_OAK_FENCE_GATE: 
                    case OAK_FENCE_GATE:
                    case SPRUCE_FENCE_GATE:
                    case JUNGLE_FENCE_GATE: 
						return;
					default:
						break;
					}
				}
			}
		}

		event.getClickedBlock().getType();

		if(!tc.perms.hasPermission(PlotPermissions.Type.INTERACT, resident)) {
			event.setCancelled(true);

			if (War.isWarTime() && resident.hasTown() && 
					resident.getTown().getCiv().getDiplomacyManager().atWarWith(tc.getTown().getCiv())) {
				WarRegen.destroyThisBlock(event.getClickedBlock(), tc.getTown());
			} else {
				CivMessage.sendErrorNoRepeat(event.getPlayer(), CivSettings.localize.localizedString("blockUse_errorGeneric")+" "+event.getClickedBlock().getType().toString());
			}
		}

		return;
	}

	private void OnPlayerUseItem(PlayerInteractEvent event) {
		Location loc = (event.getClickedBlock() == null) ? 
				event.getPlayer().getLocation() : 
				event.getClickedBlock().getLocation();

		ItemStack stack = event.getItem();

		coord.setFromLocation(event.getPlayer().getLocation());
		Camp camp = CivGlobal.getCampFromChunk(coord);
		if (camp != null) {
			if (!camp.hasMember(event.getPlayer().getName())) {
				CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("itemUse_errorCamp")+" "+stack.getType().toString());
				event.setCancelled(true);
				return;
			}
		}

		TownChunk tc = CivGlobal.getTownChunk(loc);
		if (tc == null) {
			return;
		}

		Resident resident = CivGlobal.getResident(event.getPlayer().getName());

		if (resident == null) {
			event.setCancelled(true);
		}

		if(!tc.perms.hasPermission(PlotPermissions.Type.ITEMUSE, resident)) {
			event.setCancelled(true);
			CivMessage.sendErrorNoRepeat(event.getPlayer(), CivSettings.localize.localizedString("itemUse_errorGeneric")+" "+stack.getType().toString()+" ");
		}

		return;
	}

	/*
	 * Handles rotating of itemframes
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void OnPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {

		if (event.getRightClicked().getType().equals(EntityType.HORSE)) {
			if (!HorseModifier.isCivCraftHorse((LivingEntity)event.getRightClicked())) {
				CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("horseUse_invalidHorse"));
				event.setCancelled(true);
				event.getRightClicked().remove();
				return;
			}
		}

		ItemStack inHand = event.getPlayer().getInventory().getItemInMainHand();
			if (inHand != null) {

				boolean denyBreeding = false;
				switch (event.getRightClicked().getType()) {
				case COW:
				case SHEEP:
				case MUSHROOM_COW:
					if (inHand.getType().equals(Material.WHEAT)) {
						denyBreeding = true;
					}
					break;
				case PIG:
					if (inHand.getType().equals(Material.CARROT)) {
						denyBreeding = true;
					}
					break;
				case HORSE:
					if (inHand.getType().equals(Material.GOLDEN_APPLE) ||
							inHand.getType().equals(Material.GOLDEN_CARROT)) {
						CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("itemUse_errorNoHorseBreeding"));
						event.setCancelled(true);
						return;
					}
					break;
				case CHICKEN:
					if (inHand.getType().equals(Material.WHEAT_SEEDS) ||
						inHand.getType().equals(Material.MELON_SEEDS) ||
						inHand.getType().equals(Material.PUMPKIN_SEEDS)) {
						denyBreeding = true;
					}
					break;
				case RABBIT:
					if (inHand.getType().equals(Material.CARROT) ||
						inHand.getType().equals(Material.GOLDEN_CARROT) ||
						inHand.getType().equals(Material.DANDELION)) {
						denyBreeding = true;
					}
					break;
				default:
					break;
				}

				if (denyBreeding) {
					ChunkCoord coord = new ChunkCoord(event.getPlayer().getLocation());
					Pasture pasture = Pasture.pastureChunks.get(coord);

					if (pasture == null) {
						CivMessage.sendError(event.getPlayer(), CivSettings.localize.localizedString("itemUse_errorNoWildBreeding"));
						event.setCancelled(true);
					} else {
							int loveTicks;
							NBTTagCompound tag = new NBTTagCompound();
							((CraftEntity)event.getRightClicked()).getHandle().a_(tag);
							loveTicks = tag.getInt("InLove");

							if (loveTicks == 0) {	
								if(!pasture.processMobBreed(event.getPlayer(), event.getRightClicked().getType())) {
									event.setCancelled(true);
								}
							} else {
								event.setCancelled(true);
							}
					}

					return;			
				}
			}
		if (!(event.getRightClicked() instanceof ItemFrame) && !(event.getRightClicked() instanceof Painting)) {
			return;
		}

		coord.setFromLocation(event.getPlayer().getLocation());
		TownChunk tc = CivGlobal.getTownChunk(coord);
		if (tc == null) {
			return;
		}

		Resident resident = CivGlobal.getResident(event.getPlayer().getName());
		if (resident == null) {
			return;
		}

		if(!tc.perms.hasPermission(PlotPermissions.Type.INTERACT, resident)) {
			event.setCancelled(true);
			CivMessage.sendErrorNoRepeat(event.getPlayer(), CivSettings.localize.localizedString("itemUse_errorPaintingOrFrame"));
		}

	}


	/*
	 * Handles breaking of paintings and itemframes.
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void OnHangingBreakByEntityEvent(HangingBreakByEntityEvent event) {	
	//	CivLog.debug("hanging painting break event");

		ItemFrameStorage frameStore = CivGlobal.getProtectedItemFrame(event.getEntity().getUniqueId());
		if (frameStore != null) {		
//			if (!(event.getRemover() instanceof Player)) {
//				event.setCancelled(true);
//				return;
//			}
//			
//			if (frameStore.getTown() != null) {
//				Resident resident = CivGlobal.getResident((Player)event.getRemover());
//				if (resident == null) {
//					event.setCancelled(true);
//					return;
//				}
//				
//				if (resident.hasTown() == false || resident.getTown() != frameStore.getTown()) {
//					event.setCancelled(true);
//					CivMessage.sendError((Player)event.getRemover(), "Cannot remove item from protected item frame. Belongs to another town.");
//					return;
//				}
//			}
//			
//			CivGlobal.checkForEmptyDuplicateFrames(frameStore);
//			
//			ItemStack stack = ((ItemFrame)event.getEntity()).getItem();
//			if (stack != null && !stack.getType().equals(Material.AIR)) {
//				BonusGoodie goodie = CivGlobal.getBonusGoodie(stack);
//				if (goodie != null) {
//					frameStore.getTown().onGoodieRemoveFromFrame(frameStore, goodie);
//				}
//				frameStore.clearItem();
//				TaskMaster.syncTask(new DelayItemDrop(stack, event.getEntity().getLocation()));
//			}
			if (event.getRemover() instanceof Player) {
				CivMessage.sendError((Player)event.getRemover(), CivSettings.localize.localizedString("blockBreak_errorItemFrame"));
			}
			event.setCancelled(true);	
			return;
		}

		if (event.getRemover() instanceof Player) {
			Player player = (Player)event.getRemover();

			coord.setFromLocation(player.getLocation());
			TownChunk tc = CivGlobal.getTownChunk(coord);

			if (tc == null) {
				return;
			}

			Resident resident = CivGlobal.getResident(player.getName());
			if (resident == null) {
				event.setCancelled(true);
			}

			if (!tc.perms.hasPermission(PlotPermissions.Type.DESTROY, resident)) {
				event.setCancelled(true);
				CivMessage.sendErrorNoRepeat(player, CivSettings.localize.localizedString("blockBreak_errorFramePermission"));
			}
		}


	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onChunkUnloadEvent(ChunkUnloadEvent event) {
		Boolean persist = CivGlobal.isPersistChunk(event.getChunk());		
		if (persist != null && persist == true) {
			((Cancellable) event).setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onChunkLoadEvent(ChunkLoadEvent event) {
		ChunkCoord coord = new ChunkCoord(event.getChunk());
		FarmChunk fc = CivGlobal.getFarmChunk(coord);
		if (fc == null) {
			return;
		}

		for (org.bukkit.entity.Entity ent : event.getChunk().getEntities()) {
			if (ent.getType().equals(EntityType.ZOMBIE)) {
				ent.remove();
			}
		}

		class AsyncTask extends CivAsyncTask {

			FarmChunk fc;
			public AsyncTask(FarmChunk fc) {
				this.fc = fc;
			}

			@Override
			public void run() {
				if (fc.getMissedGrowthTicks() > 0) {
					fc.processMissedGrowths(false, this);
					fc.getFarm().saveMissedGrowths();
				}
			}

		}

		TaskMaster.syncTask(new AsyncTask(fc), 500);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event) {

		Pasture pasture = Pasture.pastureEntities.get(event.getEntity().getUniqueId());
		if (pasture != null) {
			pasture.onEntityDeath(event.getEntity());
		}
		
		Battledome battledome = Battledome.battledomeEntities.get(event.getEntity().getUniqueId());
		if (battledome != null) {
			battledome.onEntityDeath(event.getEntity());
		}
		return;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockGrowEvent(BlockGrowEvent event) {
		bcoord.setFromLocation(event.getBlock().getLocation().add(0, -1, 0));
		if (CivGlobal.vanillaGrowthLocations.contains(bcoord)) {
			/* Allow vanilla growth on these plots. */
			return;
		}

		Block b = event.getBlock();

		if (Farm.isBlockControlled(b)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityBreakDoor(EntityBreakDoorEvent event) {
		bcoord.setFromLocation(event.getBlock().getLocation());
		StructureBlock sb = CivGlobal.getStructureBlock(bcoord);
		if (sb != null) {
			event.setCancelled(true);
		}

		CampBlock cb = CivGlobal.getCampBlock(bcoord);
		if (cb != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
		if (War.isWarTime() && !event.getEntity().getType().equals(EntityType.HORSE)) {
			if (!event.getSpawnReason().equals(SpawnReason.BREEDING)){
				event.setCancelled(true);
				return;
			}
		}
		
		if (event.getEntity().getType().equals(EntityType.CHICKEN)) {
			if (event.getSpawnReason().equals(SpawnReason.EGG)) {
				event.setCancelled(true);
				return;
			}
			NBTTagCompound compound = new NBTTagCompound();
			if (compound.getBoolean("IsChickenJockey")) {
				event.setCancelled(true);
				return;			
			}
		}

		if (event.getEntity().getType().equals(EntityType.IRON_GOLEM) &&
			event.getSpawnReason().equals(SpawnReason.BUILD_IRONGOLEM)) {
				event.setCancelled(true);
				return;
		}

//		if (event.getEntity().getType().equals(EntityType.ZOMBIE) ||
//			event.getEntity().getType().equals(EntityType.SKELETON) ||
//			event.getEntity().getType().equals(EntityType.BAT) ||
//			event.getEntity().getType().equals(EntityType.CAVE_SPIDER) ||
//			event.getEntity().getType().equals(EntityType.SPIDER) ||
//			event.getEntity().getType().equals(EntityType.CREEPER) ||
//			event.getEntity().getType().equals(EntityType.WOLF) ||
//			event.getEntity().getType().equals(EntityType.SILVERFISH) ||
//			event.getEntity().getType().equals(EntityType.OCELOT) ||
//			event.getEntity().getType().equals(EntityType.WITCH) ||
//			event.getEntity().getType().equals(EntityType.ENDERMAN)) {
//
//			event.setCancelled(true);
//			return;
//		}

		if (event.getSpawnReason().equals(SpawnReason.SPAWNER)) {
			event.setCancelled(true);
			return;
		}
	}

	public boolean allowPistonAction(Location loc) {
		bcoord.setFromLocation(loc);
		StructureBlock sb = CivGlobal.getStructureBlock(bcoord);
		if (sb != null) {
			return false;
		}

		CampBlock cb = CivGlobal.getCampBlock(bcoord);
		if (cb != null) {
			return false;
		}

		/* 
		 * If we're next to an attached protected item frame. Disallow 
		 * we cannot break protected item frames.
		 * 
		 * Only need to check blocks directly next to us.
		 */
		BlockCoord bcoord2 = new BlockCoord(bcoord);
		bcoord2.setX(bcoord.getX() - 1);
		if (ItemFrameStorage.attachedBlockMap.containsKey(bcoord2)) {
			return false;
		}

		bcoord2.setX(bcoord.getX() + 1);
		if (ItemFrameStorage.attachedBlockMap.containsKey(bcoord2)) {
			return false;
		}

		bcoord2.setZ(bcoord.getZ() - 1);
		if (ItemFrameStorage.attachedBlockMap.containsKey(bcoord2)) {
			return false;
		}

		bcoord2.setZ(bcoord.getZ() + 1);
		if (ItemFrameStorage.attachedBlockMap.containsKey(bcoord2)) {
			return false;
		}

		coord.setFromLocation(loc);
		HashSet<Wall> walls = CivGlobal.getWallChunk(coord);

		if (walls != null) {
			for (Wall wall : walls) {
				if (wall.isProtectedLocation(loc)) {
					return false;
				}
			}
		}		

		return true;
	}

	@EventHandler(priority = EventPriority.HIGHEST) 
	public void onBlockPistonExtendEvent(BlockPistonExtendEvent event) {

		/* UGH. If we extend into 'air' it doesnt count them as blocks...
		 * we need to check air to prevent breaking of item frames...
		 */
		final int PISTON_EXTEND_LENGTH = 13;
		Block currentBlock = event.getBlock().getRelative(event.getDirection());
		for (int i = 0; i < PISTON_EXTEND_LENGTH; i++) {
			if(ItemManager.getType(currentBlock) == CivData.AIR) {
				if (!allowPistonAction(currentBlock.getLocation())) {
					event.setCancelled(true);
					return;
				}
			}

			currentBlock = currentBlock.getRelative(event.getDirection());
		}
		
		if (War.isWarTime()) {
			event.setCancelled(true);
			return;
		}

//		if (event.getBlocks().size() == 0) {
//			Block extendInto = event.getBlock().getRelative(event.getDirection());
//			if (!allowPistonAction(extendInto.getLocation())) {
//				event.setCancelled(true);
//				return;
//			}
//		}
		coord.setFromLocation(event.getBlock().getLocation());
		FarmChunk fc = CivGlobal.getFarmChunk(coord);
		if (fc == null) {
			event.setCancelled(true);
			
		}
		
		for (Block block : event.getBlocks()) {
			if (!allowPistonAction(block.getLocation())) {
				event.setCancelled(true);
				break;

			}
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST) 
	public void onBlockPistonRetractEvent(BlockPistonRetractEvent event) {
		for (Block block: event.getBlocks())
		{
			if (!allowPistonAction(block.getLocation())) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST) 
	public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
		if (!(event.getEntity() instanceof ThrownPotion)) {
			return;
		}
		ThrownPotion potion = (ThrownPotion) event.getEntity();
		if (!(potion.getShooter() instanceof Player)) {
			//Get Ruffian type here and change damage type based on the potion thrown
			//Also change effect based on ruffian type
			String entityName = null;
			LivingEntity shooter = (LivingEntity) potion.getShooter();
			Witch witch = (Witch) shooter;
			
			if (!(witch.getTarget() instanceof Player)) {
				return;
			}
			if (potion.getShooter() instanceof LivingEntity) {
				entityName = shooter.getCustomName();
			}
			if (entityName != null && entityName.endsWith(" Ruffian")) {
				EntityInsentient nmsEntity = (EntityInsentient) ((CraftLivingEntity) shooter).getHandle();
		    	AttributeModifiable attribute = nmsEntity.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE);
		    	Double damage = attribute.getValue();
				
				class RuffianProjectile {
					Location loc;
					Location target;
					org.bukkit.entity.Entity attacker;
					int speed = 1;
					double damage;
					int splash = 6;
					
					public RuffianProjectile(Location loc, Location target, org.bukkit.entity.Entity attacker, double damage) {
						this.loc = loc;
						this.target = target;
						this.attacker = attacker;
						this.damage = damage;
					}

					public Vector getVectorBetween(Location to, Location from) {
						Vector dir = new Vector();
						
						dir.setX(to.getX() - from.getX());
						dir.setY(to.getY() - from.getY());
						dir.setZ(to.getZ() - from.getZ());
					
						return dir;
					}
					
					public boolean advance() {
						Vector dir = getVectorBetween(target, loc).normalize();
						double distance = loc.distanceSquared(target);		
						dir.multiply(speed);
						
						loc.add(dir);
						loc.getWorld().createExplosion(loc, 0.0f, false);
						distance = loc.distanceSquared(target);
						
						if (distance < speed*1.5) {
							loc.setX(target.getX());
							loc.setY(target.getY());
							loc.setZ(target.getZ());
							this.onHit();
							return true;
						}
						
						return false;
					}
					
					public void onHit() {				
						int spread = 3;
						int[][] offset = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
						for (int i = 0; i < 4; i++) {
							int x = offset[i][0]*spread;
							int y = 0;
							int z = offset[i][1]*spread;
							
							Location location = new Location(loc.getWorld(), loc.getX(),loc.getY(), loc.getZ());
							location = location.add(x, y, z);
							
							launchExplodeFirework(location);
							//loc.getWorld().createExplosion(location, 1.0f, true);
							//setFireAt(location, spread);
						}
						
						launchExplodeFirework(loc);
						//loc.getWorld().createExplosion(loc, 1.0f, true);
						damagePlayers(loc, splash);
						//setFireAt(loc, spread);		
					}
					
					@SuppressWarnings("deprecation")
					private void damagePlayers(Location loc, int radius) {
						double x = loc.getX()+0.5;
						double y = loc.getY()+0.5;
						double z = loc.getZ()+0.5;
						double r = (double)radius;
						
						CraftWorld craftWorld = (CraftWorld)attacker.getWorld();
						
						AxisAlignedBB bb = AxisAlignedBB(x-r, y-r, z-r, x+r, y+r, z+r);
						
						List<Entity> entities = craftWorld.getHandle().getEntities(((CraftEntity)attacker).getHandle(), bb);
						
						for (Entity e : entities) {
							if (e instanceof EntityPlayer) {
								EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(attacker, ((EntityPlayer)e).getBukkitEntity(), DamageCause.ENTITY_ATTACK, damage);
								Bukkit.getServer().getPluginManager().callEvent(event);
								e.damageEntity(DamageSource.GENERIC, (float) event.getDamage());
							}
						}
						
					}
					
					
//					private void setFireAt(Location loc, int radius) {
//						//Set the entire area on fire.
//						for (int x = -radius; x < radius; x++) {
//							for (int y = -3; y < 3; y++) {
//								for (int z = -radius; z < radius; z++) {
//									Block block = loc.getWorld().getBlockAt(loc.getBlockX()+x, loc.getBlockY()+y, loc.getBlockZ()+z);
//									if (ItemManager.getId(block) == CivData.AIR) {
//										ItemManager.setTypeId(block, CivData.FIRE);
//										ItemManager.setData(block, 0, true);
//									}
//								}
//							}
//						}
//					}

					private AxisAlignedBB AxisAlignedBB(double d, double e,
							double f, double g, double h, double i) {
						 return new AxisAlignedBB(d, e, f, g, h, i);
//						return null;
					}

					private void launchExplodeFirework(Location loc) {
						FireworkEffect fe = FireworkEffect.builder().withColor(Color.ORANGE).withColor(Color.YELLOW).flicker(true).with(Type.BURST).build();		
						TaskMaster.syncTask(new FireWorkTask(fe, loc.getWorld(), loc, 3), 0);
					}
				}
				
				
				class SyncFollow implements Runnable {
					public RuffianProjectile proj;
					
					@Override
					public void run() {
						
						if (proj.advance()) {
							proj = null;
							return;
						}
						TaskMaster.syncTask(this, 1);
					}
				}
				
				SyncFollow follow = new SyncFollow();
				RuffianProjectile proj = new RuffianProjectile(shooter.getLocation(), 
						witch.getTarget().getLocation(), (org.bukkit.entity.Entity) potion.getShooter(), damage);
				follow.proj = proj;
				TaskMaster.syncTask(follow);
				

				event.setCancelled(true);
			}
			return;
		} 
	}
	

	@EventHandler(priority = EventPriority.HIGHEST) 
	public void onPotionSplashEvent(PotionSplashEvent event) {
		ThrownPotion potion = event.getPotion();
		if (!(potion.getShooter() instanceof Player)) {
			return;
		} 

		Player attacker = (Player)potion.getShooter();

		for (PotionEffect effect : potion.getEffects()) {
			if (effect.getType().equals(PotionEffectType.INVISIBILITY)) {
				event.setCancelled(true);
				return;
			}
		}

		boolean protect = false;
		for (PotionEffect effect : potion.getEffects()) {
			if (effect.getType().equals(PotionEffectType.BLINDNESS) ||
				effect.getType().equals(PotionEffectType.CONFUSION) ||
				effect.getType().equals(PotionEffectType.HARM) ||
				effect.getType().equals(PotionEffectType.POISON) ||
				effect.getType().equals(PotionEffectType.SLOW) ||
				effect.getType().equals(PotionEffectType.SLOW_DIGGING) ||
				effect.getType().equals(PotionEffectType.WEAKNESS) ||
				effect.getType().equals(PotionEffectType.WITHER)) {

				protect = true;
				break;
			}
		}

		if (!protect) {
			return;
		}

		for (LivingEntity entity : event.getAffectedEntities()) {
			if (entity instanceof Player) {
				Player defender = (Player)entity;
				coord.setFromLocation(entity.getLocation());
				TownChunk tc = CivGlobal.getTownChunk(coord);
				if (tc == null) {
					continue;
				}

				switch(playersCanPVPHere(attacker, defender, tc)) {
				case ALLOWED:
					continue;
				case NOT_AT_WAR:
					CivMessage.send(attacker, CivColor.Rose+CivSettings.localize.localizedString("var_itemUse_potionError1",defender.getName()));
					event.setCancelled(true);
					return;
				case NEUTRAL_IN_WARZONE:
					CivMessage.send(attacker, CivColor.Rose+CivSettings.localize.localizedString("var_itemUse_potionError2",defender.getName()));
					event.setCancelled(true);
					return;
				case NON_PVP_ZONE:
					CivMessage.send(attacker, CivColor.Rose+CivSettings.localize.localizedString("var_itemUse_potionError3",defender.getName()));
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL) 
	public void onBlockRedstoneEvent(BlockRedstoneEvent event) {
		
		bcoord.setFromLocation(event.getBlock().getLocation());

		CampBlock cb = CivGlobal.getCampBlock(bcoord);
		if (cb != null) {
			if (ItemManager.getType(event.getBlock()) == CivData.WOOD_DOOR ||
					ItemManager.getType(event.getBlock()) == CivData.IRON_DOOR||
					ItemManager.getType(event.getBlock()) == CivData.SPRUCE_DOOR||
					ItemManager.getType(event.getBlock()) == CivData.BIRCH_DOOR||
					ItemManager.getType(event.getBlock()) == CivData.JUNGLE_DOOR||
					ItemManager.getType(event.getBlock()) == CivData.ACACIA_DOOR||
					ItemManager.getType(event.getBlock()) == CivData.DARK_OAK_DOOR) {
				event.setNewCurrent(0);
				return;
			}
		}
		
		if (War.isWarTime()) {
			event.setNewCurrent(0);
			return;
		}

	}

	private enum PVPDenyReason {
		ALLOWED,
		NON_PVP_ZONE,
		NOT_AT_WAR,
		NEUTRAL_IN_WARZONE
	}

	private PVPDenyReason playersCanPVPHere(Player attacker, Player defender, TownChunk tc) {
		Resident defenderResident = CivGlobal.getResident(defender);
		Resident attackerResident = CivGlobal.getResident(attacker);
		PVPDenyReason reason = PVPDenyReason.NON_PVP_ZONE;

		/* Outlaws can only pvp each other if they are declared at this location. */
		if (CivGlobal.isOutlawHere(defenderResident, tc) || 
			CivGlobal.isOutlawHere(attackerResident, tc)) {
			return PVPDenyReason.ALLOWED;
		}

		/* 
		 * If it is WarTime and the town we're in is at war, allow neutral players to be 
		 * targeted by anybody.
		 */
		if (War.isWarTime()) {
			if (tc.getTown().getCiv().getDiplomacyManager().isAtWar()) {
				/* 
				 * The defender is neutral if he is not in a town/civ, or not in his own civ AND not 'at war'
				 * with the attacker.
				 */
				if (!defenderResident.hasTown() || (!defenderResident.getTown().getCiv().getDiplomacyManager().atWarWith(tc.getTown().getCiv()) && 
						defenderResident.getTown().getCiv() != tc.getTown().getCiv())) {
					/* Allow neutral players to be hurt, but not hurt them back. */
					return PVPDenyReason.ALLOWED;
				} else if (!attackerResident.hasTown() || (!attackerResident.getTown().getCiv().getDiplomacyManager().atWarWith(tc.getTown().getCiv()) &&
						attackerResident.getTown().getCiv() != tc.getTown().getCiv())) {
					reason = PVPDenyReason.NEUTRAL_IN_WARZONE;
				}
			}
		}

		boolean defenderAtWarWithAttacker = false;
		if (defenderResident != null && defenderResident.hasTown()) {
			defenderAtWarWithAttacker = defenderResident.getTown().getCiv().getDiplomacyManager().atWarWith(attacker);
			/* 
			 * If defenders are at war with attackers allow PVP. Location doesnt matter. Allies should be able to help
			 * defend each other regardless of where they are currently located.
			 */
			if (defenderAtWarWithAttacker) {
				//if (defenderResident.getTown().getCiv() == tc.getTown().getCiv() ||
				//	attackerResident.getTown().getCiv() == tc.getTown().getCiv()) {
					return PVPDenyReason.ALLOWED;
				//}
			} else if (reason.equals(PVPDenyReason.NON_PVP_ZONE)) {
				reason = PVPDenyReason.NOT_AT_WAR;
			}
		}

		return reason;
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onMelting(BlockFadeEvent event) {
	  if (event.getBlock().getType().equals(Material.ICE))
	    event.setCancelled(true); 
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityPortalCreate(PortalCreateEvent event) {
		event.setCancelled(true);
	}

}