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
package ru.tailsxcraft.civcraft.threading.tasks;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;

import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivData;
import ru.tailsxcraft.civcraft.structure.Windmill;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.util.ItemManager;
import ru.tailsxcraft.civcraft.util.MultiInventory;

public class WindmillPostProcessSyncTask implements Runnable {

	ArrayList<BlockCoord> plantBlocks;
	Windmill windmill;
	int breadCount;
	int carrotCount;
	int potatoCount;
	MultiInventory source_inv;
	
	public WindmillPostProcessSyncTask(Windmill windmill, ArrayList<BlockCoord> plantBlocks,
			int breadCount, int carrotCount, int potatoCount, MultiInventory source_inv) {
		this.plantBlocks = plantBlocks;
		this.windmill = windmill;
		this.breadCount = breadCount;
		this.carrotCount = carrotCount;
		this.potatoCount = potatoCount;
		this.source_inv = source_inv;
	}
	
	@Override
	public void run() {
		Random rand = new Random();
		
		for (BlockCoord coord : plantBlocks) {
			
			int randomCropType = rand.nextInt(3);
			
			switch (randomCropType) {
			case 0:
				if (breadCount > 0) {
					/* bread seed */
					try {
						source_inv.removeItem(CivData.BREAD_SEED, 1, true);
					} catch (CivException e) {
						e.printStackTrace();
					}
					breadCount--;
					ItemManager.setType(coord.getBlock(), CivData.WHEAT);
					ItemManager.setData(coord.getBlock(), Bukkit.createBlockData(CivData.WHEAT), true);
					continue;
				}
			case 1:
				if (carrotCount > 0) {
					/* carrots */
					try {
						source_inv.removeItem(CivData.CARROT_ITEM, 1, true);
					} catch (CivException e) {
						e.printStackTrace();
					}
					carrotCount--;
					ItemManager.setType(coord.getBlock(), CivData.CARROTS);
					ItemManager.setData(coord.getBlock(), Bukkit.createBlockData(CivData.CARROTS), true);

					continue;
				}
				break;
			case 2: 
				if (potatoCount > 0) {
					/* potatoes */
					try {
						source_inv.removeItem(CivData.POTATO_ITEM, 1, true);
					} catch (CivException e) {
						e.printStackTrace();
					}
					potatoCount--;
					ItemManager.setType(coord.getBlock(), CivData.POTATOES);
					ItemManager.setData(coord.getBlock(), Bukkit.createBlockData(CivData.POTATOES), true);

					continue;
				}
			}	
			
			/* our randomly selected crop couldn't be placed, try them all now. */
			if (breadCount > 0) {
				/* bread seed */
				try {
					source_inv.removeItem(CivData.BREAD_SEED, 1, true);
				} catch (CivException e) {
					e.printStackTrace();
				}
				breadCount--;
				ItemManager.setType(coord.getBlock(), CivData.WHEAT);
				ItemManager.setData(coord.getBlock(), Bukkit.createBlockData(CivData.WHEAT), true);

				continue;
			}
			if (carrotCount > 0) {
				/* carrots */
				try {
					source_inv.removeItem(CivData.CARROT_ITEM, 1, true);
				} catch (CivException e) {
					e.printStackTrace();
				}
				carrotCount--;
				ItemManager.setType(coord.getBlock(), CivData.CARROTS);
				ItemManager.setData(coord.getBlock(), Bukkit.createBlockData(CivData.CARROTS), true);

				continue;
			}
			if (potatoCount > 0) {
				/* potatoes */
				try {
					source_inv.removeItem(CivData.POTATO_ITEM, 1, true);
				} catch (CivException e) {
					e.printStackTrace();
				}
				potatoCount--;
				ItemManager.setType(coord.getBlock(), CivData.POTATOES);
				ItemManager.setData(coord.getBlock(), Bukkit.createBlockData(CivData.POTATOES), true);
				continue;
			}
			
		}
		
	}

}
