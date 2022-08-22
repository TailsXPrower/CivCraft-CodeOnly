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
package ru.tailsxcraft.civcraft.config;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Town;

public class ConfigMob {
	public String id;
	public String name;
	public int tier;
	
	public static void loadConfig(FileConfiguration cfg, Map<String, ConfigMob> mobs){
		mobs.clear();
		List<Map<?, ?>> configMobs = cfg.getMapList("mobs");
		for (Map<?, ?> b : configMobs) {
			
			ConfigMob mob = new ConfigMob();
			
			mob.id = (String)b.get("id");
			mob.tier = (int)b.get("tier");
			
			mobs.put(mob.id, mob);
		}
		
		CivLog.info("Loaded "+mobs.size()+" Mobs.");
	}
	
	/*
	public boolean isAvailable(Town town) {
		if (CivGlobal.testFileFlag("debug-norequire")) {
			CivMessage.global("Ignoring requirements! debug-norequire found.");
			return true;
		}
		
		if (town.hasTechnology(require_tech)) {
			if (town.hasUpgrade(require_upgrade)) {
				if (town.hasStructure(require_struct)) {
					if (limit == 0 || town.getUnitTypeCount(id) < limit) {
						return true;
					}
				}
			}
		}
		return false;
	}*/
}
