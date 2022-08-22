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
package ru.tailsxcraft.civcraft.main;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Chest.Type;

import ru.tailsxcraft.civcraft.exception.InvalidBlockLocation;
import ru.tailsxcraft.civcraft.template.Template;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.util.BlockSnapshot;
import ru.tailsxcraft.civcraft.util.ItemManager;
import ru.tailsxcraft.civcraft.util.SimpleBlock;

public class CivData {
	//TODO make this an enum
	public static final Material WALL_SIGN = Material.OAK_WALL_SIGN;
	public static final Material SIGN = Material.OAK_SIGN;
	public static final Material CHEST = Material.CHEST;
	public static final Material TRAPPED_CHEST = Material.TRAPPED_CHEST;
	public static final Material WOOD = Material.OAK_WOOD;
	public static final Material PLANKS = Material.OAK_PLANKS;
	public static final Material LEAF = Material.OAK_LEAVES;
	
	
	public static final byte DATA_OAK = 0;
	public static final byte DATA_PINE = 1;
	public static final byte DATA_BIRCH = 2;
	public static final byte DATA_JUNGLE = 3;

	public static final Material DIRT = Material.DIRT;

	public static final Material COARSE_DIRT = Material.COARSE_DIRT;
	public static final Material PODZOL = Material.PODZOL;
	
	public static final Material SALMON = Material.SALMON;
	public static final Material CLOWNFISH = Material.TROPICAL_FISH;
	public static final Material PUFFERFISH = Material.PUFFERFISH;
	
	public static final Material GOLD_ORE = Material.GOLD_ORE;
	public static final Material IRON_ORE = Material.IRON_ORE;
	public static final Material IRON_INGOT = Material.IRON_INGOT;
	public static final Material GOLD_INGOT = Material.GOLD_INGOT;
	public static final Material WATER = Material.WATER;
	public static final Material WATER_RUNNING = Material.WATER;
	public static final Material FENCE = Material.OAK_FENCE;
	public static final Material BEDROCK = Material.BEDROCK;
	public static final Material RAILROAD = Material.RAIL;
	public static final Material LAVA = Material.LAVA;
	public static final Material LAVA_RUNNING = Material.LAVA;
	public static final Material COBBLESTONE = Material.COBBLESTONE;
	public static final Material MOSS_STONE = Material.MOSSY_COBBLESTONE;
	public static final Material EMERALD = Material.EMERALD;
	public static final Material DIAMOND = Material.DIAMOND;
	public static final Material GRAVEL = Material.GRAVEL;
	public static final Material AIR = Material.AIR;
	public static final Material DISPENSER = Material.DISPENSER;
	public static final Material REDSTONE_DUST = Material.REDSTONE;
	public static final Material WHEAT = Material.WHEAT;
	public static final Material SUGARCANE = Material.SUGAR_CANE;
	public static final Material PUMPKIN_STEM = Material.PUMPKIN_STEM;
	public static final Material MELON_STEM = Material.MELON_STEM;
	public static final Material CARROTS = Material.CARROTS;
	public static final Material POTATOES = Material.POTATOES;
	public static final Material NETHERWART = Material.NETHER_WART;
	public static final Material COCOAPOD = Material.COCOA_BEANS;
	public static final Material REDMUSHROOM = Material.RED_MUSHROOM;
	public static final Material BROWNMUSHROOM = Material.BROWN_MUSHROOM;
	public static final Material FARMLAND = Material.FARMLAND;
	public static final Material MELON = Material.MELON;
	public static final Material PUMPKIN = Material.PUMPKIN;
	public static final Material PUBLISHED_BOOK = Material.WRITTEN_BOOK;
	public static final Material ROTTEN_FLESH = Material.ROTTEN_FLESH;
	public static final Material TORCH = Material.TORCH;
	public static final Material WATER_BUCKET = Material.WATER_BUCKET;
	public static final Material EMPTY_BUCKET = Material.BUCKET;
	public static final Material ENDER_PEARL = Material.ENDER_PEARL;
	public static final Material ENDER_CHEST = Material.ENDER_CHEST;
	public static final Material BEACON = Material.BEACON;
	public static final Material GUNPOWDER = Material.GUNPOWDER;
	
	public static final Material MONSTER_EGG = Material.VILLAGER_SPAWN_EGG;
	public static final String BOOK_UNDERLINE = "§n";
	public static final String BOOK_BOLD = "§l";
	public static final String BOOK_ITALIC = "§o";
	public static final String BOOK_NORMAL = "§r";
	
	public static final byte DATA_SIGN_EAST = 0x5;
	public static final int DATA_SIGN_WEST = 0x4;
	public static final int DATA_SIGN_NORTH = 0x2;
	public static final int DATA_SIGN_SOUTH = 0x3;
	
	public static final Material ITEMFRAME = Material.ITEM_FRAME;
	public static final Material EMERALD_BLOCK = Material.EMERALD_BLOCK;
	public static final Material GOLD_BLOCK = Material.GOLD_BLOCK;
	public static final Material DIAMOND_BLOCK = Material.DIAMOND_BLOCK;
	public static final Material REDSTONE_BLOCK = Material.REDSTONE_BLOCK;
	public static final Material LAPIS_BLOCK = Material.LAPIS_BLOCK;
	public static final Material COAL_BLOCK = Material.COAL_BLOCK;
	public static final Material WOOL = Material.WHITE_WOOL;
	public static final Material SPONGE = Material.SPONGE;
	public static final Material HAY_BALE = Material.HAY_BLOCK;
	public static final byte DATA_WOOL_BLACK = 0xF;
	public static final Material COOKED_FISH = Material.COOKED_COD;
	public static final Material OBSIDIAN = Material.OBSIDIAN;
	public static final Material NETHER_WART_BLOCK = Material.NETHER_WART_BLOCK;
	public static final Material FIRE = Material.FIRE;
	public static final Material FISHING_ROD = Material.FISHING_ROD;
	public static final Material FISH_RAW = Material.COD;
	public static final Material MUTTON_RAW = Material.MUTTON;
	public static final Material BREAD = Material.BREAD;
	public static final Material GLOWSTONE = Material.GLOWSTONE;
	public static final Material DYE = Material.RED_DYE;
	public static final Material REDSTONE_TORCH_OFF = Material.LEGACY_REDSTONE_TORCH_OFF;
	public static final Material STONE_BRICK = Material.STONE_BRICKS;
	public static final Material PRISMARINE = Material.PRISMARINE;
	public static final byte PRISMARINE_BRICKS = 0x1;
	public static final byte DARK_PRISMARINE = 0x2;
	public static final Material ICE = Material.ICE;
	public static final Material SNOW = Material.SNOW;
	public static final Material PACKED_ICE = Material.PACKED_ICE;
	public static final Material SANDSTONE = Material.SANDSTONE;
	public static final byte CHISELED_SANDSTONE = 0x1;
	public static final byte SMOOTH_SANDSTONE = 0x2;
	
	public static final byte CHEST_NORTH = 0x2;
	public static final byte CHEST_SOUTH = 0x3;
	public static final byte CHEST_WEST = 0x4;
	public static final byte CHEST_EAST = 0x5;
	
	public static final byte SIGNPOST_NORTH = 0x8;
	public static final byte SIGNPOST_SOUTH = 0x0;
	public static final byte SIGNPOST_WEST = 0x4;
	public static final byte SIGNPOST_EAST = 0xC;
	public static final Material BREAD_SEED = Material.WHEAT_SEEDS;
	public static final Material CARROT_ITEM = Material.CARROT;
	public static final Material POTATO_ITEM = Material.POTATO;
	
	public static final Material LEATHER_HELMET = Material.LEATHER_HELMET;
	public static final Material LEATHER_CHESTPLATE = Material.LEATHER_CHESTPLATE;
	public static final Material LEATHER_LEGGINGS = Material.LEATHER_LEGGINGS;
	public static final Material LEATHER_BOOTS = Material.LEATHER_BOOTS;

	public static final Material IRON_HELMET = Material.IRON_HELMET;
	public static final Material IRON_CHESTPLATE = Material.IRON_CHESTPLATE;
	public static final Material IRON_LEGGINGS = Material.IRON_LEGGINGS;
	public static final Material IRON_BOOTS = Material.IRON_BOOTS;
	
	public static final Material DIAMOND_HELMET = Material.DIAMOND_HELMET;
	public static final Material DIAMOND_CHESTPLATE = Material.DIAMOND_CHESTPLATE;
	public static final Material DIAMOND_LEGGINGS = Material.DIAMOND_LEGGINGS;
	public static final Material DIAMOND_BOOTS = Material.DIAMOND_BOOTS;
	
	public static final Material GOLD_HELMET = Material.GOLDEN_HELMET;
	public static final Material GOLD_CHESTPLATE = Material.GOLDEN_CHESTPLATE;
	public static final Material GOLD_LEGGINGS = Material.GOLDEN_LEGGINGS;
	public static final Material GOLD_BOOTS = Material.GOLDEN_BOOTS;
	
	public static final Material CHAIN_HELMET = Material.CHAINMAIL_HELMET;
	public static final Material CHAIN_CHESTPLATE = Material.CHAINMAIL_CHESTPLATE;
	public static final Material CHAIN_LEGGINGS = Material.CHAINMAIL_LEGGINGS;
	public static final Material CHAIN_BOOTS = Material.CHAINMAIL_BOOTS;
	public static final Material WOOD_SWORD = Material.WOODEN_SWORD;
	public static final Material STONE_SWORD = Material.STONE_SWORD;
	public static final Material IRON_SWORD = Material.IRON_SWORD;
	public static final Material DIAMOND_SWORD = Material.DIAMOND_SWORD;
	public static final Material GOLD_SWORD = Material.GOLDEN_SWORD;
	
	public static final Material WOOD_AXE = Material.WOODEN_AXE;
	public static final Material STONE_AXE = Material.STONE_AXE;
	public static final Material IRON_AXE = Material.IRON_AXE;
	public static final Material DIAMOND_AXE = Material.DIAMOND_AXE;
	public static final Material GOLD_AXE = Material.GOLDEN_AXE;
	
	public static final Material WOOD_PICKAXE = Material.WOODEN_PICKAXE;
	public static final Material STONE_PICKAXE = Material.STONE_PICKAXE;
	public static final Material IRON_PICKAXE = Material.IRON_PICKAXE;
	public static final Material DIAMOND_PICKAXE = Material.DIAMOND_PICKAXE;
	public static final Material GOLD_PICKAXE = Material.GOLDEN_PICKAXE;
	public static final byte DATA_WOOL_GREEN = 0x5;
	public static final Material LADDER = Material.LADDER;
	public static final Material COAL = Material.COAL;
	public static final Material WOOD_DOOR = Material.OAK_DOOR;
	public static final Material IRON_DOOR = Material.IRON_DOOR;
	public static final Material SPRUCE_DOOR = Material.SPRUCE_DOOR;
	public static final Material BIRCH_DOOR = Material.BIRCH_DOOR;
	public static final Material JUNGLE_DOOR = Material.JUNGLE_DOOR;
	public static final Material ACACIA_DOOR = Material.ACACIA_DOOR;
	public static final Material DARK_OAK_DOOR = Material.DARK_OAK_DOOR;
	public static final Material NETHERRACK = Material.NETHERRACK;
	public static final Material BOW = Material.BOW;
	public static final Material ANVIL = Material.ANVIL;
	public static final Material IRON_BLOCK = Material.IRON_BLOCK;
	public static final Material COBWEB = Material.COBWEB;
	public static final Material STONE = Material.STONE;
	public static final Material GRANITE = Material.GRANITE;
	public static final Material POLISHED_GRANITE = Material.POLISHED_GRANITE;
	public static final Material DIORITE = Material.DIORITE;
	public static final Material POLISHED_DIORITE = Material.POLISHED_DIORITE;
	public static final Material ANDESITE = Material.ANDESITE;
	public static final Material POLISHED_ANDESITE = Material.POLISHED_ANDESITE;
	
	
	public static final short MUNDANE_POTION_DATA = 8192;
	public static final short MUNDANE_POTION_EXT_DATA = 64;
	public static final short THICK_POTION_DATA = 32;
	public static final short DATA_WOOL_RED = 14;
	public static final short DATA_WOOL_WHITE = 0;
	public static final Material GOLDEN_APPLE = Material.GOLDEN_APPLE;
	public static final Material TNT = Material.TNT;
	
	public static String getDisplayName(Material itemId) {
		
		if (itemId == Material.GOLD_ORE)
			return "Gold Ore";
		if (itemId == Material.IRON_ORE)
			return "Iron Ore";
		if (itemId == Material.IRON_INGOT)
			return "Iron";
		if (itemId == Material.GOLD_INGOT)
			return "Gold";
		
		return "Unknown_Id";
	}
	
	
	public static boolean canGrowFromStem(BlockSnapshot bs) {
		int[][] offset = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
		boolean hasAir = false;
		for (int i = 0; i < 4; i++) {
			BlockSnapshot nextBs;
			try {
				nextBs = bs.getRelative(offset[i][0], 0, offset[i][1]);
			} catch (InvalidBlockLocation e) {
				/* 
				 * The block is on the edge of this farm plot. 
				 * it _could_ grow but lets not say it can to be safe.
				 */
				return false;
			}
			//Block nextBlock = blockState.getBlock().getRelative(offset[i][0], 0, offset[i][1]);
			//int nextType = snapshot.getBlockData(arg0, arg1, arg2)
			
			
			if (nextBs.getType() == CivData.AIR) {
				hasAir = true;
			}
			
			if ((nextBs.getType() == CivData.MELON && 
					bs.getType() == CivData.MELON_STEM) ||
					(nextBs.getType() == CivData.PUMPKIN &&
							bs.getType() == CivData.PUMPKIN_STEM)) {
				return false;
			}
		}
		return hasAir;
	}

	public static boolean canGrowMushroom(BlockState blockState) {
		int[][] offset = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
		boolean hasAir = false;
		for (int i = 0; i < 4; i++) {
			Block nextBlock = blockState.getBlock().getRelative(offset[i][0], 0, offset[i][1]);
			if (ItemManager.getType(nextBlock) == CivData.AIR) {
				hasAir = true;
			}
		}
		return hasAir;
	}
	
//	public static boolean canGrowSugarcane(Block blockState) {
//		int total = 1; //include our block
//		Block nextBlock = blockState.getBlock();
//		// Get # of sugarcanes above us
//		//Using a for loop to prevent possible infinite loop
//		for (int i = 0; i <= Farm.MAX_SUGARCANE_HEIGHT; i++) {
//			nextBlock = nextBlock.getRelative(0, 1, 0);
//			if (nextBlock.getTypeId() == CivData.SUGARCANE) {
//				total++;
//			} else {
//				break;
//			}
//		}
//		
//		nextBlock = blockState.getBlock();
//		// Get # of sugarcanes below us
//		for (int i = 0; i <= Farm.MAX_SUGARCANE_HEIGHT; i++) {
//			nextBlock = nextBlock.getRelative(0, -1, 0);
//			if (nextBlock.getTypeId() == CivData.SUGARCANE) {
//				total++;
//			} else {
//				break;
//			}
//		}
//		
//		// Compare total+1 with max height.
//		if (total < Farm.MAX_SUGARCANE_HEIGHT) {
//			return true;
//		}
//
//		return false;
//	}
	
	@SuppressWarnings("incomplete-switch")
	public static boolean canGrow(BlockSnapshot bs) {
		switch (bs.getType()) {
		case WHEAT:
		case CARROTS:		
		case POTATOES:		
			return true;
		
		case BEETROOTS:
		case NETHER_WART:
			return true;
		
		case COCOA_BEANS:
			return true;
		
		case MELON_STEM:
		case PUMPKIN_STEM:
			return canGrowFromStem(bs);
		
		//case REDMUSHROOM:
		//case BROWNMUSHROOM:
		//	return canGrowMushroom(blockState);
			
		//case SUGARCANE:	
	//		return canGrowSugarcane(bs);
		}
		
		return false;
	}
	
	public static BlockData convertSignDataToDoorDirectionDataUpper(SimpleBlock block) {
		return convertSignDataToDoorDirectionDataUpper(block, Material.OAK_DOOR);	
	}
	
    public static BlockData convertSignDataToDoorDirectionDataUpper(SimpleBlock block, Material convertTo) {
		
		BlockData wallSignData = block.getData();
		BlockData doorDirectionData = null;
		
		if ( wallSignData.getAsString().contains("rotation=0"))
		{
			doorDirectionData = Bukkit.createBlockData("minecraft:oak_door[facing=north,half=upper,hinge=left]");
		}
		
		if ( wallSignData.getAsString().contains("rotation=4"))
		{
			doorDirectionData = Bukkit.createBlockData("minecraft:oak_door[facing=east,half=upper,hinge=left]");
		}
		
		if ( wallSignData.getAsString().contains("rotation=8") )
		{
			doorDirectionData = Bukkit.createBlockData("minecraft:oak_door[facing=south,half=upper,hinge=left]");
		}
		
		if ( wallSignData.getAsString().contains("rotation=12") )
		{
			doorDirectionData = Bukkit.createBlockData("minecraft:oak_door[facing=west,half=upper,hinge=left]");
		}
		
		return doorDirectionData;
	}
	
	public static BlockData convertSignDataToDoorDirectionData(SimpleBlock block) {
		return convertSignDataToDoorDirectionData(block, Material.OAK_DOOR);	
	}

	public static BlockData convertSignDataToDoorDirectionData(SimpleBlock block, Material convertTo) {
		
		BlockData wallSignData = block.getData();
		BlockData doorDirectionData = convertTo.createBlockData();
		
		if ( wallSignData.getAsString().contains("rotation=0"))
		{
			if (doorDirectionData instanceof Directional) {
			  ((Directional) doorDirectionData).setFacing(BlockFace.NORTH);
			}
		}
		
		if ( wallSignData.getAsString().contains("rotation=4"))
		{
			if (doorDirectionData instanceof Directional) {
			  ((Directional) doorDirectionData).setFacing(BlockFace.EAST);
	    	}
		}
		
		if ( wallSignData.getAsString().contains("rotation=8") )
		{
			if (doorDirectionData instanceof Directional) {
			  ((Directional) doorDirectionData).setFacing(BlockFace.SOUTH);
			}
		}
		
		if ( wallSignData.getAsString().contains("rotation=12") )
		{
			if (doorDirectionData instanceof Directional) {
			  ((Directional) doorDirectionData).setFacing(BlockFace.WEST);
			}
		}
		
		return doorDirectionData;
	}
	
	public static BlockData convertSignDataToDoubleChestData(SimpleBlock block, Template tpl, BlockCoord relativeCoord) {
		return convertSignDataToDoubleChestData(block, Material.CHEST, tpl, relativeCoord);	
	}

	public static BlockData convertSignDataToDoubleChestData(SimpleBlock block, Material convertTo, Template tpl, BlockCoord relativeCoord) {
		
		BlockData wallSignData = block.getData();
		Chest chestDirectionData = (Chest)convertTo.createBlockData();
		
		if ( wallSignData.getAsString().contains("rotation=0"))
		{
			if ( tpl.blocks[relativeCoord.getX()-1][relativeCoord.getY()][relativeCoord.getZ()].getType() == Material.CHEST || 
					tpl.blocks[relativeCoord.getX()-1][relativeCoord.getY()][relativeCoord.getZ()].getType() == Material.OAK_SIGN) {
				chestDirectionData.setType(Type.LEFT);
				chestDirectionData.setFacing(BlockFace.SOUTH);
			} else if ( tpl.blocks[relativeCoord.getX()+1][relativeCoord.getY()][relativeCoord.getZ()].getType() == Material.CHEST || 
					tpl.blocks[relativeCoord.getX()+1][relativeCoord.getY()][relativeCoord.getZ()].getType() == Material.OAK_SIGN) {
				chestDirectionData.setType(Type.RIGHT);
				chestDirectionData.setFacing(BlockFace.SOUTH);
			} else {
				chestDirectionData.setType(Type.SINGLE);
				chestDirectionData.setFacing(BlockFace.SOUTH);
			}
		}
		
		if ( wallSignData.getAsString().contains("rotation=4"))
		{
			if ( tpl.blocks[relativeCoord.getX()][relativeCoord.getY()][relativeCoord.getZ()-1].getType() == Material.CHEST || 
					tpl.blocks[relativeCoord.getX()][relativeCoord.getY()][relativeCoord.getZ()-1].getType() == Material.OAK_SIGN) {
				chestDirectionData.setType(Type.LEFT);
				chestDirectionData.setFacing(BlockFace.WEST);
			} else if ( tpl.blocks[relativeCoord.getX()][relativeCoord.getY()][relativeCoord.getZ()+1].getType() == Material.CHEST || 
					tpl.blocks[relativeCoord.getX()][relativeCoord.getY()][relativeCoord.getZ()+1].getType() == Material.OAK_SIGN) {
				chestDirectionData.setType(Type.RIGHT);
				chestDirectionData.setFacing(BlockFace.WEST);
			} else {
				chestDirectionData.setType(Type.SINGLE);
				chestDirectionData.setFacing(BlockFace.WEST);
			}
		}
		
		if ( wallSignData.getAsString().contains("rotation=8") )
		{
			if ( tpl.blocks[relativeCoord.getX()-1][relativeCoord.getY()][relativeCoord.getZ()].getType() == Material.CHEST || 
					tpl.blocks[relativeCoord.getX()-1][relativeCoord.getY()][relativeCoord.getZ()].getType() == Material.OAK_SIGN) {
				chestDirectionData.setType(Type.RIGHT);
				chestDirectionData.setFacing(BlockFace.NORTH);
			} else if ( tpl.blocks[relativeCoord.getX()+1][relativeCoord.getY()][relativeCoord.getZ()].getType() == Material.CHEST || 
					tpl.blocks[relativeCoord.getX()+1][relativeCoord.getY()][relativeCoord.getZ()].getType() == Material.OAK_SIGN) {
				chestDirectionData.setType(Type.LEFT);
				chestDirectionData.setFacing(BlockFace.NORTH);
			} else {
				chestDirectionData.setType(Type.SINGLE);
				chestDirectionData.setFacing(BlockFace.NORTH);
			}
		}
		
		if ( wallSignData.getAsString().contains("rotation=12") )
		{
			if ( tpl.blocks[relativeCoord.getX()][relativeCoord.getY()][relativeCoord.getZ()-1].getType() == Material.CHEST || 
					tpl.blocks[relativeCoord.getX()][relativeCoord.getY()][relativeCoord.getZ()-1].getType() == Material.OAK_SIGN) {
				chestDirectionData.setType(Type.RIGHT);
				chestDirectionData.setFacing(BlockFace.EAST);
			} else if ( tpl.blocks[relativeCoord.getX()][relativeCoord.getY()][relativeCoord.getZ()+1].getType() == Material.CHEST || 
					tpl.blocks[relativeCoord.getX()][relativeCoord.getY()][relativeCoord.getZ()+1].getType() == Material.OAK_SIGN) {
				chestDirectionData.setType(Type.LEFT);
				chestDirectionData.setFacing(BlockFace.EAST);
			} else {
				chestDirectionData.setType(Type.SINGLE);
				chestDirectionData.setFacing(BlockFace.EAST);
			}
		}
		
		return chestDirectionData;
		
		
//		switch (data) {
//		case 0x0:
//			return 0x3;
//		case 0x4:
//			return 0x4;
//		case 0x8:
//			return 0x2;
//		case 0xC:
//			return 0x5;
//		}
		
	}
	
	public static BlockData convertSignDataToChestData(SimpleBlock block) {
		return convertSignDataToChestData(block, Material.CHEST);	
	}

	public static BlockData convertSignDataToChestData(SimpleBlock block, Material convertTo) {
		/* Chests are 
		 * 0x2: Facing north (for ladders and signs, attached to the north side of a block)
		 * 0x3: Facing south
		 * 0x4: Facing west
		 * 0x5: Facing east
		 */
		
		/* Signposts are
		 * 0x0: south
			0x4: west
			0x8: north
			0xC: east
		 */
		
		BlockData wallSignData = block.getData();
		BlockData chestDirectionData = convertTo.createBlockData();
		
		if ( wallSignData.getAsString().contains("rotation=0"))
		{
			if (chestDirectionData instanceof Directional) {
			  ((Directional) chestDirectionData).setFacing(BlockFace.SOUTH);
			}
		}
		
		if ( wallSignData.getAsString().contains("rotation=4"))
		{
			if (chestDirectionData instanceof Directional) {
			  ((Directional) chestDirectionData).setFacing(BlockFace.WEST);
	    	}
		}
		
		if ( wallSignData.getAsString().contains("rotation=8") )
		{
			if (chestDirectionData instanceof Directional) {
			  ((Directional) chestDirectionData).setFacing(BlockFace.NORTH);
			}
		}
		
		if ( wallSignData.getAsString().contains("rotation=12") )
		{
			if (chestDirectionData instanceof Directional) {
			  ((Directional) chestDirectionData).setFacing(BlockFace.EAST);
			}
		}
		
		return chestDirectionData;
		
		
//		switch (data) {
//		case 0x0:
//			return 0x3;
//		case 0x4:
//			return 0x4;
//		case 0x8:
//			return 0x2;
//		case 0xC:
//			return 0x5;
//		}
		
	}
	
}
