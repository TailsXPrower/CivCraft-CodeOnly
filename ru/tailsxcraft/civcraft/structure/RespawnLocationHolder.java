package ru.tailsxcraft.civcraft.structure;

import java.util.List;

import ru.tailsxcraft.civcraft.util.BlockCoord;

public interface RespawnLocationHolder {

	public String getRespawnName();
	public List<BlockCoord> getRespawnPoints();
	public BlockCoord getRandomRevivePoint();
	
}
