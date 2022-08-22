package ru.tailsxcraft.civcraft.object;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigMobSpawner;
import ru.tailsxcraft.civcraft.database.SQL;
import ru.tailsxcraft.civcraft.database.SQLUpdate;
import ru.tailsxcraft.civcraft.exception.InvalidNameException;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.spawning.spawners.SpawnerManager;

public class MobSpawner extends SQLObject {

    private ConfigMobSpawner spawner;
    private Civilization civ;
    private BlockCoord coord;
    private int buildable = 0;
    private Boolean active;
    
    public MobSpawner(ConfigMobSpawner spawner, BlockCoord coord) {
        this.setSpawner(spawner);
        this.setCoord(coord);
        try {
            this.setName(this.getSpawner().id);
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }
        this.setCiv(null);
        this.setActive(true);
    }

    public MobSpawner(ResultSet rs) throws SQLException, InvalidNameException {
        this.load(rs);
    }

    public static final String TABLE_NAME = "MOB_SPAWNERS";
    public static void init() throws SQLException {
        if (!SQL.hasTable(TABLE_NAME)) {
            String table_create = "CREATE TABLE " + SQL.tb_prefix + TABLE_NAME+" (" + 
                    "`id` int(11) unsigned NOT NULL auto_increment," +
                    "`name` VARCHAR(64) NOT NULL," + 
                    "`buildable_id` int(11), " +
                    "`coord` mediumtext DEFAULT NULL,"+
					"`active` boolean DEFAULT true,"+
                    "PRIMARY KEY (`id`)" + ")";
            
            SQL.makeTable(table_create);
            CivLog.info("Created "+TABLE_NAME+" table");
        } else {
            CivLog.info(TABLE_NAME+" table OK!");
        }
    }

    
    @Override
    public void load(ResultSet rs) throws SQLException, InvalidNameException {
        this.setId(rs.getInt("id"));
        this.setSpawner(CivSettings.spawners.get(this.getName()));
        this.setName(rs.getString("name"));
        this.setCoord(new BlockCoord(rs.getString("coord")));
        this.setBuildable(rs.getInt("buildable_id"));
        
        this.setActive(this.getBuildable() == 0);
    }

    @Override
    public void save() {
        SQLUpdate.add(this);
    }
    
    @Override
    public void saveNow() throws SQLException {
        HashMap<String, Object> hashmap = new HashMap<String, Object>();
        
        hashmap.put("name", this.getName());
        hashmap.put("coord", this.coord.toString());
        hashmap.put("active", this.active);
        if (this.getBuildable() == 0) {
            hashmap.put("buildable_id", null);
        } else {
            hashmap.put("buildable_id", this.getBuildable());
        }
        
        SQL.updateNamedObject(this, hashmap, TABLE_NAME);
    }
    
    @Override
    public void delete() throws SQLException {      
    }

    public Civilization getCiv() {
        return civ;
    }


    public void setCiv(Civilization civ) {
        this.civ = civ;
    }


    public ConfigMobSpawner getSpawner() {
        return spawner;
    }


    public void setSpawner(ConfigMobSpawner spawner) {
        this.spawner = spawner;
    }


    public BlockCoord getCoord() {
        return coord;
    }


    public void setCoord(BlockCoord coord) {
        this.coord = coord;
    }
    
    public int getBuildable() {
        return buildable;
    }

    public void setBuildable(int buildable) {
        this.buildable = buildable;
    }

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
		SpawnerManager spawnerEditor = MythicBukkit.inst().getSpawnerManager();
		 if (spawnerEditor != null) {
			if (this.active) {
				MythicSpawner spawner = spawnerEditor.getSpawnerAtLocation(BukkitAdapter.adapt(this.getCoord().getLocation()));
				if (spawner != null) {
//		            CivLog.warning("Unable to create Spawner; " + spawner.toString() + " spawner exists.");
					return;
				}
				/*
		        if (mob == null) {

		            CivLog.warning("Unable to create Spawner; " + this.getName() + " does not exist");
		            return;
		        }*/
		        MythicSpawner spawn = spawnerEditor.createSpawner(this.getSpawner().name+"_"+coord.toString(), this.getCoord().getLocation(), this.getName());
		        spawn.setMaxMobs(PlaceholderInt.of(""+this.getSpawner().maxmobs));
		        spawn.setCooldownSeconds(this.getSpawner().cooldown);
		        spawn.setMobsPerSpawn(this.getSpawner().mobsperspawn);
		        spawn.setSpawnRadius(this.getSpawner().radius);
			} else {
				MythicSpawner spawner = spawnerEditor.getSpawnerAtLocation(BukkitAdapter.adapt(this.getCoord().getLocation()));
				if (spawner != null) {
		            CivLog.debug("Spawner Disabled at "+this.getCoord().getLocation());
					spawnerEditor.removeSpawner(spawner);
				}
			}
		} else {

            CivLog.warning("Unable to create Spawners; MythicMobs does not exist");
        }
	}
    
    
}
