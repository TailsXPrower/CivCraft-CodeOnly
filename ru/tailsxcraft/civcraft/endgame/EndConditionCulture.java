package ru.tailsxcraft.civcraft.endgame;

import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.object.Civilization;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.structure.wonders.Wonder;

public class EndConditionCulture extends EndGameCondition {

	private int requiredCultureLevel;
	private int numberOfTownsAtCulture;
	private int numberOfWonders;
	
	@Override
	public void onLoad() {
		requiredCultureLevel = Integer.valueOf(this.getString("culture_level"));
		numberOfTownsAtCulture = Integer.valueOf(this.getString("towns"));
		numberOfWonders = Integer.valueOf(this.getString("wonders"));
	}
	
	@Override
	public String getSessionKey() {
		return "endgame:cultural";
	}
	
	@Override
	public boolean check(Civilization civ) {
		
		/* Verify Civ has correct number of towns at the right culture level. */
		int townCount = 0;
		for (Town town : civ.getTowns()) {
			if (town.getMotherCiv() != null) {
				continue;
			}
			
			if (town.getCultureLevel() >= requiredCultureLevel) {
				townCount++;
			}
		}
		
		if (townCount < numberOfTownsAtCulture) {
			/* Not enough towns. */
			return false;
		}
		
		/* Verify the Civ owns enough wonders. */
		int wonderCount = 0;
		for (Wonder wonder : CivGlobal.getWonders()) {
			if (wonder.getCiv() == civ) {
				wonderCount++;
			}
		}
		
		if (wonderCount < numberOfWonders) {
			/* Not enough wonders. */
			return false;
		}
		
		/* Cannot be conquered. */
		if (civ.isConquered()) {
			return false;
		}
		
		return true; 
	}

	@Override
	protected void onWarDefeat(Civilization civ) {
		this.onFailure(civ);
	}

	
}
