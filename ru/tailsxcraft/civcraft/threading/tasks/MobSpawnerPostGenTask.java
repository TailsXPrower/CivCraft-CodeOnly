package ru.tailsxcraft.civcraft.threading.tasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import ru.tailsxcraft.civcraft.config.ConfigMobSpawner;
import ru.tailsxcraft.civcraft.database.SQL;
import ru.tailsxcraft.civcraft.main.CivData;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.object.MobSpawner;
import ru.tailsxcraft.civcraft.populators.MobSpawnerPick;
import ru.tailsxcraft.civcraft.populators.MobSpawnerPopulator;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.util.ChunkCoord;
import ru.tailsxcraft.civcraft.util.ItemManager;

public class MobSpawnerPostGenTask implements Runnable {

    String playerName;
    int start;
    
    public MobSpawnerPostGenTask(String playerName, int start) {
        this.playerName = playerName;
        this.start = 0;
    }
    
    public void deleteAllMobSpawnersFromDB() {
        /* Delete all existing trade goods from DB. */
        Connection conn = null;
        PreparedStatement ps = null;
        try {
        try {
            conn = SQL.getGameConnection();
            String code = "TRUNCATE TABLE "+MobSpawner.TABLE_NAME;
            ps = conn.prepareStatement(code);
            ps.execute();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    @Override
    public void run() {
        CivLog.info("Generating/Clearing Mob Spawners...");
        CivLog.info("|- Organizing trade picks into a Queue.");
        
        deleteAllMobSpawnersFromDB();
        
        /* Generate Trade Good Pillars. */
        Queue<MobSpawnerPick> picksQueue = new LinkedList<MobSpawnerPick>();
        for (MobSpawnerPick pick : CivGlobal.mobSpawnerPreGenerator.spawnerPicks.values()) {
            picksQueue.add(pick);
        }
        
        int count = 0;
        int amount = 20;
        int totalSize = picksQueue.size();
        while (picksQueue.peek() != null) {
            CivLog.info("|- Placing/Picking spawners:"+count+"/"+totalSize+" current size:"+picksQueue.size());
            
            Queue<MobSpawnerPick> processQueue = new LinkedList<MobSpawnerPick>();
            for (int i = 0; i < amount; i++) {
                MobSpawnerPick pick = picksQueue.poll();
                if (pick == null) {
                    break;
                }
                
                count++;
                processQueue.add(pick);
            }
            
            TaskMaster.syncTask(new SyncMopSpawnerGenTask(processQueue, amount));
            
            try {
                while (processQueue.peek() != null) {
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                return;
            }
        }
        
        
        CivLog.info("Finished!");
    }

    class SyncMopSpawnerGenTask implements Runnable {
        public Queue<MobSpawnerPick> picksQueue;
        public int amount;
        
        public SyncMopSpawnerGenTask(Queue<MobSpawnerPick> picksQueue, int amount) {
            this.picksQueue = picksQueue;
            this.amount = amount;
        }
        
        @Override
        public void run() {
            World world = Bukkit.getWorld("world");
            BlockCoord bcoord2 = new BlockCoord();

            for(int i = 0; i < amount; i++) {
                MobSpawnerPick pick = picksQueue.poll();
                if (pick == null) {
                    return;
                }
                
                ChunkCoord coord = pick.chunkCoord;
                Chunk chunk = world.getChunkAt(coord.getX(), coord.getZ());
                
                int centerX = (chunk.getX() << 4) + 8;
                int centerZ = (chunk.getZ() << 4) + 8;
                int centerY = world.getHighestBlockYAt(centerX, centerZ);
                
                
                
                bcoord2.setWorldname("world");
                bcoord2.setX(centerX);
                bcoord2.setY(centerY - 1);
                bcoord2.setZ(centerZ);
                
                /* try to detect already existing mob spawners. */
                while(true) {
                    Block top = world.getBlockAt(bcoord2.getX(), bcoord2.getY(), bcoord2.getZ());
                    
                    if (!top.getChunk().isLoaded()) {
                        top.getChunk().load();
                    }
                    
                    if (ItemManager.getType(top) == CivData.BEDROCK) {
                        ItemManager.setType(top, CivData.AIR);
                        bcoord2.setY(bcoord2.getY() - 1);
                        
                        top = top.getRelative(BlockFace.NORTH);
                        if (ItemManager.getType(top) == CivData.WALL_SIGN) {
                        	ItemManager.setType(top, CivData.AIR);                
                        }
                        
                        top = top.getRelative(BlockFace.SOUTH);
                        if (ItemManager.getType(top) == CivData.WALL_SIGN) {
                        	ItemManager.setType(top, CivData.AIR);                  
                        }
                        
                        top = top.getRelative(BlockFace.EAST);
                        if (ItemManager.getType(top) == CivData.WALL_SIGN) {
                        	ItemManager.setType(top, CivData.AIR);                  
                        }
                        
                        top = top.getRelative(BlockFace.WEST);
                        if (ItemManager.getType(top) == CivData.WALL_SIGN) {
                        	ItemManager.setType(top, CivData.AIR); 
                        }
                    } else {
                        break;
                    }
                    
                }
                
                centerY = world.getHighestBlockYAt(centerX, centerZ);
                
                // Determine if we should be a water good.
                ConfigMobSpawner good;
                if (ItemManager.getBlockTypeAt(world, centerX, centerY-1, centerZ) == CivData.WATER || 
                    ItemManager.getBlockTypeAt(world, centerX, centerY-1, centerZ) == CivData.WATER_RUNNING) {
                    good = pick.waterPick;
                }  else {
                    good = pick.landPick;
                }
                
                // Randomly choose a land or water good.
                if (good == null) {
                    System.out.println("Could not find suitable mob spawner type during populate! aborting.");
                    continue;
                }
                
                // Create a copy and save it in the global hash table.
                BlockCoord bcoord = new BlockCoord(world.getName(), centerX, centerY, centerZ);
                MobSpawnerPopulator.buildMobSpawner(good, bcoord, world, true);
                
            }
        }
    }
    
}
