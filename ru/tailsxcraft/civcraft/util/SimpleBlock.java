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
package ru.tailsxcraft.civcraft.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;

import ru.tailsxcraft.civcraft.structure.Buildable;

public class SimpleBlock {
	
	//public static final int SIGN = 1;
	//public static final int CHEST = 2;
	//public static final int SIGN_LITERAL = 3;
	
	public enum Type {
		NORMAL,
		COMMAND,
		LITERAL,
	}
	
	private Material type = null;
	private BlockData data = null;
	//public int special = 0;
//	public int special_id = -1;
	public int x;
	public int y;
	public int z;
	
	public Type specialType;
	public String command; 
	public String message[] = new String[4];
	public String worldname;
	public Buildable buildable;
	public Map<String, String> keyvalues = new HashMap<String, String>();
	
	/**
	 * Construct the block with its type.
	 *
	 * @param block
	 */
	    public SimpleBlock(Block block) {
	        this.x = block.getX();
	        this.y = block.getY();
	        this.z = block.getZ();
	        this.worldname = block.getWorld().getName();
	        this.type = ItemManager.getType(block);
	        this.data = ItemManager.getData(block);
	        this.specialType = Type.NORMAL;
	    }
	    
	    public SimpleBlock(String hash, Material type, BlockData data) {
		    String[] split = hash.split(",");
			this.worldname = split[0];
			this.x = Integer.valueOf(split[1]);
			this.y = Integer.valueOf(split[2]);
			this.z = Integer.valueOf(split[3]);
			this.type = type;
			this.data = data;
	        this.specialType = Type.NORMAL;
	    }
	
	public String getKey() {
		return this.worldname+","+this.x+","+this.y+","+this.z;
	}
	
	public static String getKeyFromBlockCoord(BlockCoord coord) {
		return coord.getWorldname()+","+coord.getX()+","+coord.getY()+","+coord.getZ();
	}
	    
	/**
	 * Construct the block with its type and data.
	 *
	 * @param type
	 * @param data
	 */
	public SimpleBlock(Material type, BlockData data) {
	    this.type = (Material) type;
	    this.data = (BlockData) data;
        this.specialType = Type.NORMAL;

	}
	
	/**
	 * @return the type
	 */
	public Material getType() {
	    return (Material) type;
	}
	
	@SuppressWarnings("deprecation")
	public Material getMaterial() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(Material type) {
	    this.type = (Material) type;
	}
	
	public void setTypeAndData(Material type, BlockData data) {
		this.type = (Material) type;
		this.data = (BlockData) data;
	}
	/**
	 * @return the data
	 */
	public BlockData getData() {
	    return (BlockData) data;
	}
	
	/**
	 * @return the data
	 */
	public BlockFace getDirection() {
	    return ((Rotatable) data).getRotation();
	}
	
	/**
	 * @param data the data to set
	 */
	public void setData(BlockData data) {
	    this.data = (BlockData) data;
	}
	
	/**
	 * Returns true if it's air.
	 *
	 * @return if air
	 */
	public boolean isAir() {
	    return type == Material.AIR;
	}

	public String getKeyValueString() {
		String out = "";
		
		for (String key : keyvalues.keySet()) {
			String value = keyvalues.get(key);
			out += key+":"+value+",";
		}
		
		return out;
	}

	public Location getLocation() {
		return new Location(Bukkit.getWorld(this.worldname), this.x, this.y, this.z);
	}

}
