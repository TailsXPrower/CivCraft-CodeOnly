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
package ru.tailsxcraft.civcraft.tasks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivData;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.TradeGood;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.util.ItemManager;

public class TradeGoodSignCleanupTask implements Runnable {

	String playerName;
	int xoff = 0;
	int zoff = 0;
	
	public TradeGoodSignCleanupTask(String playername, int xoff, int zoff) {
		this.playerName = playername;
		this.xoff = xoff;
		this.zoff = zoff;
	}
	
	@Override
	public void run() {
		Player player;
		try {
			player = CivGlobal.getPlayer(playerName);
		} catch (CivException e) {
			e.printStackTrace();
			return;
		}
		
		int count = 0;
		int i = 0;
		//BlockCoord bcoord2 = new BlockCoord();
		World world = Bukkit.getWorld("world");

		
		
		
	//	for(ChunkCoord coord : CivGlobal.preGenerator.goodPicks.keySet()) {
		for (TradeGood tg : CivGlobal.getTradeGoods()) { 
			BlockCoord bcoord2 = tg.getCoord();			
			bcoord2.setX(bcoord2.getX() + xoff);
			bcoord2.setZ(bcoord2.getZ() + zoff);
			bcoord2.setY(0);
			
		//	Chunk chunk = world.getChunkAt(coord.getX(), coord.getZ());
		//	int centerX = (chunk.getX() << 4) + 8;
		//	int centerZ = (chunk.getZ() << 4) + 8;
		//	int centerY = world.getHighestBlockYAt(centerX, centerZ);
			
		//	bcoord2.setWorldname("world");
		//	bcoord2.setX(centerX);
		//	bcoord2.setY(centerY);
		//	bcoord2.setZ(centerZ);
			
			while(bcoord2.getY() < 256) {
				Block top = world.getBlockAt(bcoord2.getX(), bcoord2.getY(), bcoord2.getZ());
				ItemManager.setType(top, CivData.AIR);
	    			ItemManager.setData(top, Bukkit.createBlockData(CivData.AIR), true);
	    			bcoord2.setY(bcoord2.getY() + 1);
	    			
	    			top = top.getRelative(BlockFace.NORTH);
	    			if (ItemManager.getType(top) == CivData.WALL_SIGN || ItemManager.getType(top) == CivData.SIGN) {
	    				count++;
	    			}
	    			ItemManager.setType(top, CivData.AIR);
		    		ItemManager.setData(top, Bukkit.createBlockData(CivData.AIR), true);

	    			top = top.getRelative(BlockFace.SOUTH);
	    			if (ItemManager.getType(top) == CivData.WALL_SIGN || ItemManager.getType(top) == CivData.SIGN) {
	    				count++;
	    				ItemManager.setType(top, CivData.AIR);
			    		ItemManager.setData(top, Bukkit.createBlockData(CivData.AIR), true);
	    			}
	    			
	    		
		    		top = top.getRelative(BlockFace.EAST);
	    			if (ItemManager.getType(top) == CivData.WALL_SIGN || ItemManager.getType(top) == CivData.SIGN) {
	    				count++;
	    				ItemManager.setType(top, CivData.AIR);
			    		ItemManager.setData(top, Bukkit.createBlockData(CivData.AIR), true);

	    			}
	    			
	    		
		    		top = top.getRelative(BlockFace.WEST);
	    			if (ItemManager.getType(top) == CivData.WALL_SIGN || ItemManager.getType(top) == CivData.SIGN) {
	    				count++;
	    				ItemManager.setType(top, CivData.AIR);
			    		ItemManager.setData(top, Bukkit.createBlockData(CivData.AIR), true);
	    			}
			}
			
			i++;
			if ((i % 80) == 0) {
				CivMessage.send(player, "Goodie:"+i+" cleared "+count+" signs...");
			//	TaskMaster.syncTask(new TradeGoodPostGenTask(playerName, (i)));
			//	return;
			}
			
		}
		
		CivMessage.send(player, CivSettings.localize.localizedString("Finished"));
	}

}
