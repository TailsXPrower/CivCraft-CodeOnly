package ru.tailsxcraft.civcraft.object;

import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.structure.Buildable;
import ru.tailsxcraft.civcraft.util.BlockCoord;

public interface BuildableDamageBlock {
	public Buildable getOwner();
	public void setOwner(Buildable owner);
	public Town getTown();
	public Civilization getCiv();
	public BlockCoord getCoord();
	public void setCoord(BlockCoord coord);
	public int getX();
	public int getY();
	public int getZ();
	public String getWorldname();
	public boolean isDamageable();
	public void setDamageable(boolean damageable);
	public boolean canDestroyOnlyDuringWar();
	public boolean allowDamageNow(Player player);
}
