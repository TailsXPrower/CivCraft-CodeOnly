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

import org.bukkit.configuration.file.FileConfiguration;

import ru.tailsxcraft.civcraft.main.CivLog;

public class ConfigUniversityLevel {
	public int level;	/* Current level number */
	public int amount; /* Number of redstone this mine consumes */
	public int count; /* Number of times that consumes must be met to level up */
	public double beakers; /* hammers generated each time hour */
	
	public static void loadConfig(FileConfiguration cfg, Map<Integer, ConfigUniversityLevel> levels) {
		levels.clear();
		List<Map<?, ?>> university_levels = cfg.getMapList("university_levels");
		for (Map<?, ?> level : university_levels) {
			ConfigUniversityLevel university_level = new ConfigUniversityLevel();
			university_level.level = (Integer)level.get("level");
			university_level.amount = (Integer)level.get("amount");
			university_level.beakers = (Double)level.get("beakers");
			university_level.count = (Integer)level.get("count"); 
			levels.put(university_level.level, university_level);
		}
		CivLog.info("Loaded "+levels.size()+" university levels.");
	}
}