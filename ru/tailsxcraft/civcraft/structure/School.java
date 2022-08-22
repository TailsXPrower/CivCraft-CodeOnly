package ru.tailsxcraft.civcraft.structure;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;

import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.object.Town;

public class School extends Structure {

	
	protected School(Location center, String id, Town town) throws CivException {
		super(center, id, town);
	}

	public School(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}

	@Override
	public void loadSettings() {
		super.loadSettings();

	}
	
	@Override
	public String getMarkerIconName() {
		return "school";
	}

}
