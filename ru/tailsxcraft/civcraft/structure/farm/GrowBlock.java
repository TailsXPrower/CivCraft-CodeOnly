package ru.tailsxcraft.civcraft.structure.farm;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import ru.tailsxcraft.civcraft.util.BlockCoord;

public class GrowBlock {
	
	public GrowBlock(String world, int x, int y, int z, Material typeid2, BlockData data2, boolean spawn2) {
		this.bcoord = new BlockCoord(world, x, y, z);
		this.typeId = typeid2;
		this.data = data2;
		this.spawn = spawn2;
	}
	
	public BlockCoord bcoord;
	public Material typeId;
	public BlockData data;
	public boolean spawn;
}
