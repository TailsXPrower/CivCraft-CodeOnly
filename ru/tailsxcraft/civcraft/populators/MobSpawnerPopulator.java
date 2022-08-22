package ru.tailsxcraft.civcraft.populators;

import java.sql.SQLException;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.generator.BlockPopulator;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigMobSpawner;
import ru.tailsxcraft.civcraft.main.CivData;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.object.ProtectedBlock;
import ru.tailsxcraft.civcraft.object.StructureSign;
import ru.tailsxcraft.civcraft.object.MobSpawner;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.util.ChunkCoord;
import ru.tailsxcraft.civcraft.util.ItemManager;

public class MobSpawnerPopulator extends BlockPopulator {
    
    //private static final int RESOURCE_CHANCE = 400; 
    private static final int FLAG_HEIGHT = 3;
//    private static final double MIN_DISTANCE = 400.0;
    

    public static void buildMobSpawner(ConfigMobSpawner spawner, BlockCoord coord, World world, boolean sync) {
        MobSpawner newSpawner = new MobSpawner(spawner, coord);            
        CivGlobal.addMobSpawner(newSpawner);

        BlockFace direction = null;
        Block top = null;
        Random random = new Random();
        int dir = random.nextInt(4);
        if (dir == 0) {
            direction = BlockFace.NORTH;
        } else if (dir == 1) {
            direction = BlockFace.EAST;
        } else if (dir == 2) {
            direction = BlockFace.SOUTH;
        } else {
            direction = BlockFace.WEST;
        }

        //clear any stack goodies
        for (int y = coord.getY(); y < 256; y++) {
            top = world.getBlockAt(coord.getX(), y, coord.getZ());
            if (ItemManager.getType(top) == CivData.BEDROCK) {
                ItemManager.setType(top, CivData.AIR);
            }
        }
        
        for (int y = coord.getY(); y < coord.getY() + FLAG_HEIGHT-1; y++) {
            top = world.getBlockAt(coord.getX(), y, coord.getZ());
            top.setType(Material.BEDROCK);

            ProtectedBlock pb = new ProtectedBlock(new BlockCoord(top), ProtectedBlock.Type.MOB_SPAWNER_MARKER);
            CivGlobal.addProtectedBlock(pb);
            if (sync) {
            try {
                pb.saveNow();
            } catch (SQLException e) {
                CivLog.warning("Unable to Protect Mob Spawner Block");
                e.printStackTrace();
            }    
            } else {
                pb.save();
            }
        }
        
        top = world.getBlockAt(coord.getX(), coord.getY()+FLAG_HEIGHT-1, coord.getZ());
        if ( spawner.id.contains("T1")) {
        	top.setType(Material.IRON_BLOCK);
        } else if ( spawner.id.contains("T2")) {
        	top.setType(Material.GOLD_BLOCK);
        } else if ( spawner.id.contains("T3")) {
        	top.setType(Material.DIAMOND_BLOCK);
        } else if ( spawner.id.contains("T4")) {
        	top.setType(Material.EMERALD_BLOCK);
        } else {
        	top.setType(Material.NETHER_WART_BLOCK);
        }
        
        ProtectedBlock pb = new ProtectedBlock(new BlockCoord(top), ProtectedBlock.Type.MOB_SPAWNER_MARKER);
        CivGlobal.addProtectedBlock(pb);
        if (sync) {
        try {
            pb.saveNow();
        } catch (SQLException e) {
            CivLog.warning("Unable to Protect Mob Spawner Block");
            e.printStackTrace();
        }    
        } else {
            pb.save();
        }

        Block signBlock = top.getRelative(direction);
        signBlock.setType(Material.OAK_WALL_SIGN);
        //TODO make sign a structure sign?
                //          Civ.protectedBlockTable.put(Civ.locationHash(signBlock.getLocation()), 
        //                  new ProtectedBlock(signBlock, null, null, null, ProtectedBlock.Type.TRADE_MARKER));

        BlockState state = signBlock.getState();

        if (state instanceof Sign) {
            Sign sign = (Sign)state;
            sign.setLine(0, CivSettings.localize.localizedString("MobSpawnerSign_Heading"));
            sign.setLine(1, "----");
            if ( spawner.name.length() > 15 ) {
				String[] split = spawner.name.split(" ");
				sign.setLine(2, split[0]);
				sign.setLine(3, split[1]);
			} else {
				sign.setLine(2, spawner.name);
				sign.setLine(3, "");
			}
            sign.update(true);
            
            WallSign data = (WallSign)signBlock.getBlockData();
    		data.setFacing(direction);
    		signBlock.setBlockData(data);

            StructureSign structSign = new StructureSign(new BlockCoord(signBlock), null);
            structSign.setAction("");
            structSign.setType("");
            structSign.setText(sign.getLines());
            structSign.setDirection((Directional)signBlock.getBlockData());
            CivGlobal.addStructureSign(structSign);
            ProtectedBlock pbsign = new ProtectedBlock(new BlockCoord(signBlock), ProtectedBlock.Type.MOB_SPAWNER_MARKER);
            CivGlobal.addProtectedBlock(pbsign);
            if (sync) {
                try {
                    pbsign.saveNow();
                    structSign.saveNow();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                pbsign.save();
                structSign.save();
            }
        }
        
        if (sync) {
            try {
            	newSpawner.saveNow();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
        	newSpawner.save();
        }
    }

    public boolean checkForDuplicateMobSpawner(String worldName, int centerX, int centerY, int centerZ) {
        /* 
         * Search downward to bedrock for any mob spawners here. If we find one, don't generate. 
         */
        
        BlockCoord coord = new BlockCoord(worldName, centerX, centerY, centerZ);
        for (int y = centerY; y > 0; y--) {
            coord.setY(y);          
            
            if (CivGlobal.getMobSpawner(coord) != null) {
                /* Already a mob spawner here. DONT Generate it. */
                return true;
            }       
        }
        return false;
    }
    
    @Override
    public void populate(World world, Random random, Chunk source) {
        
        ChunkCoord cCoord = new ChunkCoord(source);
        MobSpawnerPick pick = CivGlobal.mobSpawnerPreGenerator.spawnerPicks.get(cCoord);
        if (pick != null) {
            int centerX = (source.getX() << 4) + 8;
            int centerZ = (source.getZ() << 4) + 8;
            int centerY = world.getHighestBlockYAt(centerX, centerZ);
            BlockCoord coord = new BlockCoord(world.getName(), centerX, centerY, centerZ);

            if (checkForDuplicateMobSpawner(world.getName(), centerX, centerY, centerZ)) {
                return;
            }
            
            // Determine if we should be a water good.
            ConfigMobSpawner spawner;
            if (ItemManager.getBlockTypeAt(world, centerX, centerY-1, centerZ) == CivData.WATER || 
                ItemManager.getBlockTypeAt(world, centerX, centerY-1, centerZ) == CivData.WATER_RUNNING) {
                spawner = pick.waterPick;
            }  else {
                spawner = pick.landPick;
            }
            
            // Randomly choose a land or water good.
            if (spawner == null) {
                System.out.println("Could not find suitable mob spawner type during populate! aborting.");
                return;
            }
            
            // Create a copy and save it in the global hash table.
            buildMobSpawner(spawner, coord, world, false);
        }
    
    }

}