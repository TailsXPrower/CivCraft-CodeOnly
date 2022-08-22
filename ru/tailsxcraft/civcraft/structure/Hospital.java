package ru.tailsxcraft.civcraft.structure;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.object.TownChunk;

public class Hospital extends Structure {

	protected Hospital(Location center, String id, Town town)
			throws CivException {
		super(center, id, town);
	}

	public Hospital(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}

	@Override
	public String getDynmapDescription() {
		return null;
	}
	
	@Override
	public String getMarkerIconName() {
		return "bed";
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		for (Town t : this.getTown().getCiv().getTowns()) {
			for (Resident res : t.getResidents()) {
				try {
					Player player = CivGlobal.getPlayer(res);
					
					if (player.isDead() || !player.isValid() || !player.isOnline()) {
						continue;
					}
					if (player.getFoodLevel() >= 20) {
						continue;
					}
					
					TownChunk tc = CivGlobal.getTownChunk(player.getLocation());
					if (tc == null || tc.getTown() != this.getTown()) {
						continue;
					}
					
					if (player.getFoodLevel() < 10.0) {
						player.setFoodLevel(player.getFoodLevel() + 1);
					}
				} catch (CivException e) {
					//Player not online;
				}
				
			}
		}
	}
}
