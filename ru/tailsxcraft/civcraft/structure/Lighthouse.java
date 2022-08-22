package ru.tailsxcraft.civcraft.structure;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;

import ru.tailsxcraft.civcraft.components.AttributeBiomeRadiusPerLevel;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.object.Buff;
import ru.tailsxcraft.civcraft.object.Town;

public class Lighthouse extends Structure {

	
	protected Lighthouse(Location center, String id, Town town) throws CivException {
		super(center, id, town);
	}

	public Lighthouse(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}

	@Override
	public void loadSettings() {
		super.loadSettings();

	}
	
	@Override
	public String getMarkerIconName() {
		return "compass";
	}

	public double getHammersPerTile() {
		AttributeBiomeRadiusPerLevel attrBiome = (AttributeBiomeRadiusPerLevel)this.getComponent("AttributeBiomeBase");
		double base = attrBiome.getBaseValue();
	
		double rate = 1;
		rate += this.getTown().getBuffManager().getEffectiveDouble(Buff.ADVANCED_TOOLING);
		return (rate*base);
	}

}
