package ru.tailsxcraft.civcraft.util;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;


/*
 * The ItemManager class is going to be used to wrap itemstack operations that have now
 * been deprecated by Bukkit. If bukkit ever actually takes these methods away from us,
 * we'll just have to use NMS or be a little creative. Doing it on spot (here) will be 
 * better than having fragile code scattered everywhere. 
 * 
 * Additionally it gives us an opportunity to unit test certain item operations that we
 * want to use with our new custom item stacks.
 */

public class ItemManager {

	public static ItemStack createItemStack(Material typeId, int amount) {
		if(typeId != null) return new ItemStack(typeId, amount);
		return null;
	}

	@SuppressWarnings("deprecation")
	public static MaterialData getMaterialData(Material type_id, int data) {
		return new MaterialData(type_id, (byte)data);
	}
	
	public static Enchantment getEnchantById(NamespacedKey id) {
		return Enchantment.getByKey(id);
	}
	
	public static Material getType(Material material) {
		return material;
	}
	
	public static NamespacedKey getType(Enchantment e) {
		return e.getKey();
	}
	
	public static Material getType(ItemStack stack) {
		return stack.getType();
	}
	
	public static Material getType(Block block) {
		return block.getType();
	}
	
	public static void setType(Block block, Material typeId) {
		block.setType(typeId);
	}
	
	public static void setType(BlockState block, Material typeId) {
		block.setType(typeId);;
	}
	
	public static BlockData getData(Block block) {
		return block.getBlockData();
	}
	
	@SuppressWarnings("deprecation")
	public static short getData(ItemStack stack) {
		return stack.getDurability();
	}
	
	@SuppressWarnings("deprecation")
	public static byte getData(MaterialData data) {
		return data.getData();
	}

	public static BlockData getData(BlockState state) {
		return state.getBlockData();
	}
	
	public static void setData(Block block, BlockData data) {
		block.setBlockData(data);
	}

	public static void setData(Block block, BlockData data, boolean update) {
		block.setBlockData(data, update);
	}
	
	public static Material getMaterial(String material) {
		return Material.matchMaterial(material);
	}
	
	public static Material getBlockType(ChunkSnapshot snapshot, int x, int y, int z) {
		return snapshot.getBlockType(x, y, z);
	}
	
	public static BlockData getBlockData(ChunkSnapshot snapshot, int x, int y, int z) {
		return snapshot.getBlockData(x, y, z);
	}
	
	public static void sendBlockChange(Player player, Location loc, BlockData data) {
		player.sendBlockChange(loc, data);;
	}
	
	public static Material getBlockTypeAt(World world, int x, int y, int z) {
		return world.getBlockAt(x, y, z).getType();
	}

	public static Material getType(BlockState newState) {
		return newState.getType();
	}

	@SuppressWarnings("deprecation")
	public static short getId(EntityType entity) {
		return entity.getTypeId();
	}

	@SuppressWarnings("deprecation")
	public static void setData(MaterialData data, byte chestData) {
		data.setData(chestData);
	}

	public static void setTypeIdAndData(Block block, Material type, BlockData data, boolean update) {
		block.setType(type, update);
		block.setBlockData(data, update);
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack spawnPlayerHead(String playerName, String itemDisplayName) {		
		ItemStack skull = ItemManager.createItemStack(Material.SKELETON_SKULL, 1);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner(playerName);
		meta.setDisplayName(itemDisplayName);
		skull.setItemMeta(meta);
		return skull;
	}

	public static boolean removeItemFromPlayer(Player player, Material mat, int amount) {
		ItemStack m = new ItemStack(mat, amount);
		if (player.getInventory().contains(mat)) {
			player.getInventory().removeItem(m);
			player.updateInventory();
			return true;
		}
		return false;
	}
	
}
