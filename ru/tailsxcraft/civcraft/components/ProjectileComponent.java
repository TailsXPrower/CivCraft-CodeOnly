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
package ru.tailsxcraft.civcraft.components;

import java.util.HashSet;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.MovingObjectPosition;
import net.minecraft.server.v1_16_R3.MovingObjectPosition.EnumMovingObjectType;
import ru.tailsxcraft.civcraft.cache.PlayerLocationCache;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.object.Buff;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.structure.Buildable;
import ru.tailsxcraft.civcraft.structure.TownHall;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import net.minecraft.server.v1_16_R3.MovingObjectPositionBlock;
import net.minecraft.server.v1_16_R3.Vec3D;

import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public abstract class ProjectileComponent extends Component {

	protected int damage;
	protected double range;
	protected double min_range;	
	protected Buildable buildable;
	protected PlayerProximityComponent proximityComponent;
	private Location turretCenter;
	
	private HashSet<BlockCoord> turrets = new HashSet<BlockCoord>();

	public ProjectileComponent(Buildable buildable, Location turretCenter) {
		this.buildable = buildable;
		proximityComponent = new PlayerProximityComponent();
		proximityComponent.createComponent(buildable);
		this.turretCenter = turretCenter;
		loadSettings();
	}
		
	@Override
	public void onLoad() {
	}

	@Override
	public void onSave() {
	}
	
	/*
	 * We're overriding the create component class here so that all child-classes
	 * will register with this method rather than the default. This is done so that the key
	 * used in components by type will get all instances of this base class rather than having
	 * to search for the children in the componentByType list.
	 */
	@Override
	public void createComponent(Buildable buildable, boolean async) {
		if (async) {
			TaskMaster.asyncTask(new RegisterComponentAsync(buildable, this, ProjectileComponent.class.getName(), true), 0);
		} else {
			new RegisterComponentAsync(buildable, this, ProjectileComponent.class.getName(), true).run();
		}
	}
	
	@Override
	public void destroyComponent() {
		TaskMaster.asyncTask(new RegisterComponentAsync(null, this, ProjectileComponent.class.getName(), false), 0);
	}	
	
	public void setTurretLocation(BlockCoord absCoord) {	
		turrets.add(absCoord);
	}

	public Vector getVectorBetween(Location to, Location from) {
		Vector dir = new Vector();
		
		dir.setX(to.getX() - from.getX());
		dir.setY(to.getY() - from.getY());
		dir.setZ(to.getZ() - from.getZ());
	
		return dir;
	}
	
	public int getDamage() {
		double rate = 1;
		rate += this.getBuildable().getTown().getBuffManager().getEffectiveDouble(Buff.FIRE_BOMB);
		return (int)(this.damage*rate);
	}
	
	public void setDamage(int damage) {
		this.damage = damage;
	}
	
	private Location getNearestTurret(Location playerLoc) {
		
		double distance = Double.MAX_VALUE;
		BlockCoord nearest = null;
		for (BlockCoord turretCoord : turrets) {
			Location turretLoc = turretCoord.getLocation();
			if (playerLoc.getWorld() != turretLoc.getWorld()) {
				return null;
			}
			
			double tmp = turretLoc.distance(playerLoc);
			if (tmp < distance) {
				distance = tmp;
				nearest = turretCoord;
			}
			
		}
		if (nearest == null) {
			return null;
		}
		return nearest.getLocation();
	}
	
	private  boolean isWithinRange(Location residentLocation, double range) {
		
		if (residentLocation.getWorld() != turretCenter.getWorld()) {
			return false;
		}
		
		if (residentLocation.distance(turretCenter) <= range ) {
			return true;
		}
		return false;
	}
	
	private boolean isLit(Player player) {
		Location loc1 = player.getLocation();
		return ((loc1.getWorld()).getBlockAt(loc1).getLightFromSky() >= 15);

	}
	
	private boolean canSee(Player player, Location loc2) {
		Location loc1 = player.getLocation(); 
		Vec3D vec1 = new Vec3D(loc1.getX(), loc1.getY()+player.getEyeHeight(), loc1.getZ());
		Vec3D vec2 = new Vec3D(loc2.getX(), loc2.getY(), loc2.getZ());
		World world = player.getWorld();

		MovingObjectPosition movingobjectposition = ((CraftWorld)world).getHandle().rayTrace(new net.minecraft.server.v1_16_R3.RayTrace(vec1, vec2, net.minecraft.server.v1_16_R3.RayTrace.BlockCollisionOption.OUTLINE, net.minecraft.server.v1_16_R3.RayTrace.FluidCollisionOption.NONE, ((CraftPlayer)player).getHandle()));
		return movingobjectposition.getType() == EnumMovingObjectType.MISS;
	}
	
	protected Location adjustTurretLocation(Location turretLoc, Location playerLoc) {
		// Keep the y position the same, but advance 1 block in the direction of the player..?
		int diff = 2;
		
		int xdiff = 0;
		int zdiff = 0;
		if (playerLoc.getBlockX() > turretLoc.getBlockX()) {
			xdiff = diff;
		} else if (playerLoc.getBlockX() < turretLoc.getBlockX()){
			xdiff = -diff;
		}  
		
		if (playerLoc.getBlockZ() > turretLoc.getBlockZ()) {
			zdiff = diff;
		} else if (playerLoc.getBlockZ() < turretLoc.getBlockZ()){
			zdiff = -diff;
		} 
				
		return turretLoc.getBlock().getRelative(xdiff, 0, zdiff).getLocation();
	}
	

	public void process() {
		if (!buildable.isActive()) {
			return;
		}
		
		if (buildable instanceof TownHall) {
			if ( !buildable.getTown().isCapitol() ) {
				if ( buildable.getTown().getLevel() < 6 ) {
					return;
				}
			}
		}
		
		Player nearestPlayer = null;
		double nearestDistance = Double.MAX_VALUE;
		
		Location turretLoc = null;
		for (PlayerLocationCache pc : proximityComponent.tryGetNearbyPlayers(false)) {
			if (pc == null || pc.isDead()) {
				continue;
			}
		    
			if (!buildable.getTown().isOutlaw(pc.getName())) {
				Resident resident = pc.getResident();
				// Try to exit early by making sure this resident is at war.
				if (resident == null || (!resident.hasTown())) {
					continue;
				}
				
				if (!buildable.getCiv().getDiplomacyManager().isHostileWith(resident)) {
					continue;
				}
			}
			
			Location playerLoc = pc.getCoord().getLocation();
			turretLoc = getNearestTurret(playerLoc);
			if (turretLoc == null) {
				// No nearest turret, player is probably not in the same world as the turret.
				return;
			}

			Player player;
			try {
				player = CivGlobal.getPlayer(pc.getName());
			} catch (CivException e) {
				return;
			}
			
			if (player.getGameMode() != GameMode.SURVIVAL) {
				return;
			}
			
			if (!this.getBuildable().getConfigId().equals("s_teslatower")) {
				// XXX todo convert this to not use a player so we can async...
				if (!this.canSee(player, turretLoc)) {
					continue;
				}
			} else {
				if (!this.isLit(player)) {
					continue;
				}
			}
		
			if (isWithinRange(player.getLocation(), range)) {
				if (isWithinRange(player.getLocation(), min_range)) {
					continue;
				}
				
				double distance = player.getLocation().distance(this.turretCenter);
				if (distance < nearestDistance) {
					nearestPlayer = player;
					nearestDistance = distance;
				}
			}
		}
		
		if (turretLoc == null) {
			return;
		}
		
		if (nearestPlayer == null) {
			return;
		}
		
		fire(turretLoc, nearestPlayer);	
	}
	
	public abstract void fire(Location turretLoc, Entity targetEntity);
	public abstract void loadSettings();

	public Buildable getBuildable() {
		return buildable;
	}
	
	public void setBuildable(Buildable buildable) {
		this.buildable = buildable;
	}

	public Location getTurretCenter() {
		return turretCenter;
	}

	public void setTurretCenter(Location turretCenter) {
		this.turretCenter = turretCenter;
	}
	
}
