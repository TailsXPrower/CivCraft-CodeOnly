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

import org.bukkit.Location;

import ru.tailsxcraft.civcraft.components.ProjectileCannonComponent;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.object.Buff;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.util.BlockCoord;

public class CannonTower extends Structure {

	ProjectileCannonComponent cannonComponent;
	
	protected CannonTower(Location center, String id, Town town)
			throws CivException {
		super(center, id, town);
		this.hitpoints = this.getMaxHitPoints();
	}
	
	protected CannonTower(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}

	@Override
	public void loadSettings() {
		super.loadSettings();
		cannonComponent = new ProjectileCannonComponent(this, this.getCenterLocation().getLocation()); 
		cannonComponent.createComponent(this);
	}
	
	public int getDamage() {
		double rate = 1;
		rate += this.getTown().getBuffManager().getEffectiveDouble(Buff.FIRE_BOMB);
		return (int)(cannonComponent.getDamage()*rate);
	}
	
	@Override
	public int getMaxHitPoints() {
		double rate = 1;
		if (this.getTown().getBuffManager().hasBuff("buff_chichen_itza_tower_hp")) {
			rate += this.getTown().getBuffManager().getEffectiveDouble("buff_chichen_itza_tower_hp");
			rate += this.getTown().getBuffManager().getEffectiveDouble(Buff.BARRICADE);
		}
		return (int) (info.max_hitpoints * rate);
	}
	
//	public void setDamage(int damage) {
//		cannonComponent.setDamage(damage);
//	}


	public void setTurretLocation(BlockCoord absCoord) {
		cannonComponent.setTurretLocation(absCoord);
	}
	
	
//	@Override
//	public void fire(Location turretLoc, Location playerLoc) {
//		turretLoc = adjustTurretLocation(turretLoc, playerLoc);
//		Vector dir = getVectorBetween(playerLoc, turretLoc);
//		
//		Fireball fb = turretLoc.getWorld().spawn(turretLoc, Fireball.class);
//		fb.setDirection(dir);
//		// NOTE cannon does not like it when the dir is normalized or when velocity is set.
//		fb.setYield((float)yield);
//		CivCache.cannonBallsFired.put(fb.getUniqueId(), new CannonFiredCache(this, playerLoc, fb));
//	}
	
	@Override
	public void onCheck() throws CivException {
		try {
			double build_distance = CivSettings.getDouble(CivSettings.warConfig, "cannon_tower.build_distance");
			
			for (Town town : this.getTown().getCiv().getTowns()) {
				for (Structure struct : town.getStructures()) {
					if (struct instanceof CannonTower) {
						BlockCoord center = struct.getCenterLocation();
						double distance = center.distance(this.getCenterLocation());
						if (distance <= build_distance) {
							throw new CivException(CivSettings.localize.localizedString("var_buildable_tooCloseToCannonTower",(center.getX()+","+center.getY()+","+center.getZ())));
						}
					}
					if (struct instanceof CannonShip) {
						BlockCoord center = struct.getCenterLocation();
						double distance = center.distance(this.getCenterLocation());
						if (distance <= build_distance) {
							throw new CivException(CivSettings.localize.localizedString("var_buildable_tooCloseToCannonShip",(center.getX()+","+center.getY()+","+center.getZ())));
						}
					}
				}
			}
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			throw new CivException(e.getMessage());
		}
		
	}
	
}
