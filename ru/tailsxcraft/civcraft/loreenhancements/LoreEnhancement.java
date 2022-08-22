package ru.tailsxcraft.civcraft.loreenhancements;

import gpl.AttributeUtil;
import ru.tailsxcraft.civcraft.main.CivData;
import ru.tailsxcraft.civcraft.object.BuildableDamageBlock;
import ru.tailsxcraft.civcraft.util.ItemManager;

import java.util.HashMap;

import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public abstract class LoreEnhancement {
	public AttributeUtil add(AttributeUtil attrs) {
		return attrs;
	}
	
	public static HashMap<String, LoreEnhancement> enhancements = new HashMap<String, LoreEnhancement>();
	public HashMap<String, String> variables = new HashMap<String, String>();
	
	public static void init() {
		enhancements.put("LoreEnhancementSoulBound", new LoreEnhancementSoulBound());
		enhancements.put("LoreEnhancementAttack", new LoreEnhancementAttack());
		enhancements.put("LoreEnhancementDefense", new LoreEnhancementDefense());
		enhancements.put("LoreEnhancementPunchout", new LoreEnhancementPunchout());
	}
	
	public boolean onDeath(PlayerDeathEvent event, ItemStack stack) { return false; }

	public boolean canEnchantItem(ItemStack item) {
		return true;
	}
	
	public static boolean isWeapon(ItemStack item) {
		switch (ItemManager.getType(item)) {
		case WOODEN_SWORD:
		case STONE_SWORD:
		case IRON_SWORD:
		case GOLDEN_SWORD:
		case DIAMOND_SWORD:
		case WOODEN_AXE:
		case STONE_AXE:
		case IRON_AXE:
		case GOLDEN_AXE:
		case DIAMOND_AXE:
		case BOW:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isArmor(ItemStack item) {
		switch (ItemManager.getType(item)) {
		case LEATHER_BOOTS:
		case LEATHER_CHESTPLATE:
		case LEATHER_HELMET:
		case LEATHER_LEGGINGS:
		case IRON_BOOTS:
		case IRON_CHESTPLATE:
		case IRON_HELMET:
		case IRON_LEGGINGS:
		case DIAMOND_BOOTS:
		case DIAMOND_CHESTPLATE:
		case DIAMOND_HELMET:
		case DIAMOND_LEGGINGS:
		case CHAINMAIL_BOOTS:
		case CHAINMAIL_CHESTPLATE:
		case CHAINMAIL_HELMET:
		case CHAINMAIL_LEGGINGS:
		case GOLDEN_BOOTS:
		case GOLDEN_CHESTPLATE:
		case GOLDEN_HELMET:
		case GOLDEN_LEGGINGS:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isWeaponOrArmor(ItemStack item) {
		return isWeapon(item) || isArmor(item);
	}

	public boolean hasEnchantment(ItemStack item) {
		return false;
	}

	public String getDisplayName() {
		return "LoreEnchant";
	}
	
	public String getName() {
		return "LoreEnchant";
	}

	public int onStructureBlockBreak(BuildableDamageBlock dmgBlock, int damage) {
		return damage;
	}

	public double getLevel(AttributeUtil attrs) {	return 0; }
	public abstract String serialize(ItemStack stack);
	public abstract ItemStack deserialize(ItemStack stack, String data);
	
	
}
