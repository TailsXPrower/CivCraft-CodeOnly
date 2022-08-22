package ru.tailsxcraft.civcraft.structure;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;

import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.object.Town;

public class Stadium extends Structure {

	protected Stadium(Location center, String id, Town town)
			throws CivException {
		super(center, id, town);
	}

	public Stadium(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}

	@Override
	public String getDynmapDescription() {
		return null;
	}
	
	@Override
	public String getMarkerIconName() {
		return "flower";
	}
}
