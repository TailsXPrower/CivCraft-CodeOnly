package ru.tailsxcraft.civcraft.loreenhancements;

import gpl.AttributeUtil;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.main.CivCraft;
import ru.tailsxcraft.civcraft.util.CivColor;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class LoreEnhancementSoulBound extends LoreEnhancement {
	
	public AttributeUtil add(AttributeUtil attrs) {
		// attrs.addEnhancement(new NamespacedKey(CivCraft.getPlugin(), "LoreEnhancementSoulBound"), null, null);
		attrs.addEnhancement("LoreEnhancementSoulBound", null, null);
		attrs.addLore(CivColor.Gold+getDisplayName());
		return attrs;
	}
	
	public boolean onDeath(final PlayerDeathEvent event, final ItemStack stack) {
		event.getDrops().remove(stack);
		return true;
	};
	
	public boolean canEnchantItem(ItemStack item) {
		return isWeaponOrArmor(item);
	}
	
	@SuppressWarnings("deprecation")
	public boolean hasEnchantment(ItemStack item) {
		AttributeUtil attrs = new AttributeUtil(item);
		//attrs.hasEnhancement(Enchantment.getByName("LoreEnhancementSoulBound"));
		return attrs.hasEnhancement("LoreEnhancementSoulBound");
	}
	
	public String getDisplayName() {
		return CivSettings.localize.localizedString("itemLore_Soulbound");
	}
	
	public String getName() {
		return "LoreEnhancementSoulBound";
	}
	
	@Override
	public String serialize(ItemStack stack) {
		return "";
	}

	@Override
	public ItemStack deserialize(ItemStack stack, String data) {
		return stack;
	}
}
