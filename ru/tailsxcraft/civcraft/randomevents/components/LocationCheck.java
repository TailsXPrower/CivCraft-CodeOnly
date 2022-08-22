package ru.tailsxcraft.civcraft.randomevents.components;

import java.util.List;

import ru.tailsxcraft.civcraft.cache.PlayerLocationCache;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.randomevents.RandomEventComponent;
import ru.tailsxcraft.civcraft.util.BlockCoord;

public class LocationCheck extends RandomEventComponent {

	@Override
	public void process() {
	}
	
	public boolean onCheck() { 
		
		String varname = this.getString("varname");
		String locString = this.getParent().componentVars.get(varname);
		
		if (locString == null) {
			//CivLog.warning("Couldn't get var name: "+varname+" for location check component.");
			return false;
		}
		
		BlockCoord bcoord = new BlockCoord(locString);
		double radiusSquared = 2500.0; /* 50 block radius */
		List<PlayerLocationCache> cache = PlayerLocationCache.getNearbyPlayers(bcoord, radiusSquared);
		
		for (PlayerLocationCache pc : cache) {
			Resident resident = CivGlobal.getResident(pc.getName());
			if (resident.getTown() == this.getParentTown()) {
				return true;
			}
		}
		
		return false; 
		
	}

}
