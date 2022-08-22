package ru.tailsxcraft.civcraft.civilization;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.database.SQL;
import ru.tailsxcraft.civcraft.database.SQLUpdate;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.exception.InvalidNameException;
import ru.tailsxcraft.civcraft.exception.InvalidObjectException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Civilization;
import ru.tailsxcraft.civcraft.object.SQLObject;

public class GoldenAge extends SQLObject {
	
	private Civilization civ = null;
	private Date startDate = null;
	private boolean active = false;
	
	private int lenght = 8;
	
	private int time = 0;

	public static final String TABLE_NAME = "GOLDENAGE";
	public static void init() throws SQLException {
		if (!SQL.hasTable(TABLE_NAME)) {
			String table_create = "CREATE TABLE " + SQL.tb_prefix + TABLE_NAME+" (" + 
					"`civ_id` int(11)," + 
					"`time` int(11)," +
					"`start_date` long NOT NULL," +
					"`active` boolean DEFAULT false," +
					"PRIMARY KEY (`civ_id`)" + ")";
			
			SQL.makeTable(table_create);
			CivLog.info("Created "+TABLE_NAME+" table");
		} else {
			SQL.makeCol("active", "boolean", TABLE_NAME);
		}
	}

	@Override
	public void load(ResultSet rs) throws SQLException, InvalidNameException,
			InvalidObjectException, CivException {
		this.setId(rs.getInt("id"));
		
		this.civ = CivGlobal.getCivFromId(rs.getInt("civ_id"));
		if (this.civ == null) {
			this.delete();
			throw new CivException("Couldn't find civilization id:"+rs.getInt("civ_id")+" while loading golden age.");
		}
		
		this.time = rs.getInt("time");
		this.startDate = new Date(rs.getLong("start_date"));
		this.active = rs.getBoolean("active");
		
		GoldenAgeSweeper.register(this);	
	}

	@Override
	public void save() {
		SQLUpdate.add(this);
	}


	@Override
	public void saveNow() throws SQLException {
		HashMap<String, Object> hashmap = new HashMap<String, Object>();
		
		hashmap.put("civ_id", this.getCiv().getId());	
		hashmap.put("time", this.time);
		hashmap.put("start_date", this.startDate.getTime());
		hashmap.put("active", this.active);
		
		SQL.updateNamedObject(this, hashmap, TABLE_NAME);
	}


	@Override
	public void delete() throws SQLException {
		SQL.deleteNamedObject(this, TABLE_NAME);		
	}
	
	public GoldenAge(ResultSet rs) throws SQLException, InvalidNameException, InvalidObjectException, CivException {
		this.load(rs);
		
		/* Place ourselves back in the civ we just loaded. */
		this.civ.setActiveGoldenAge(this);
	}
	
	public GoldenAge() {

	}

	/*
	 * Private for now, since we only allow golden age on civ atm.
	 */
	private void start() {
				
		this.active = true;
		
		/* Register this random event with the sweeper until complete. */
		GoldenAgeSweeper.register(this);
		
		this.time = civ.getTimeGoldenAge();
		
		/* Setup start date. */
		this.startDate = new Date();
		
		this.save();
	}

	public Civilization getCiv() {
		return civ;
	}

	public void setCiv(Civilization civ) {
		this.civ = civ;
	}

	public void cleanup() {
		civ.setActiveGoldenAge(null);
		civ.setTimeGoldenAge(civ.getTimeGoldenAge()+1);
		try {
			this.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public int getLength() {
		return lenght;
	}
	
	public void setLength(int length) {
		this.lenght = length;
	}

	public void start(Civilization civ) {
		this.civ = civ;
		
		/* Show message to town */
		CivMessage.sendCivHeading(civ, "Золотой век!");
		CivMessage.playSoundCiv(civ, "golden.age");
		CivMessage.sendCiv(civ, "Наша цивилизация вошла в Золотой век! Наука, продуктивность, урожайность, производство культуры увеличено на 35%. Золотой век продлится 8 часов.");
		
		
		civ.setActiveGoldenAge(this);
		this.start();
	}

	public Date getEndDate() {
		Date end = new Date(this.startDate.getTime() + (this.getLength() * GoldenAgeSweeper.MILLISECONDS_PER_HOUR));
		return end;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public void activate() throws CivException {
		if (this.active) {
			throw new CivException(CivSettings.localize.localizedString("re_alreadyActive"));
		}
		
		this.active = true;
		/* Start by processing all of the action components. */
		
		this.save();
	}
	
	/* Check civ for golden age */
	public static boolean checkGoldenAge(Civilization civ, int happiness) {
		if ( civ.getTimeGoldenAge() == 0 ) {
			if ( happiness < 1500 ) return false;
		} else if ( civ.getTimeGoldenAge() == 1 ) {
			if ( happiness < 4500 ) return false;
		} else if ( civ.getTimeGoldenAge() == 3 ) {
			if ( happiness < 9000 ) return false;
		} else if ( civ.getTimeGoldenAge() == 4 ) {
			if ( happiness < 13500 ) return false;
		} else if ( civ.getTimeGoldenAge() >= 5 ) {
			return false;
		}
		
		return true;
	}
}
