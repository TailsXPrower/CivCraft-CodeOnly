package ru.tailsxcraft.civcraft.loreenhancements;

import gpl.AttributeUtil;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.main.CivCraft;
import ru.tailsxcraft.civcraft.util.CivColor;

import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class LoreEnhancementDefense extends LoreEnhancement {
	
	public LoreEnhancementDefense() {
		this.variables.put("amount", "1.0");
	}
	
	public String getLoreString(double baseLevel) {
		return CivColor.Blue+"+"+baseLevel+" "+CivSettings.localize.localizedString("newItemLore_Defense");
	}
	
	@Override
	public AttributeUtil add(AttributeUtil attrs) {		
		
		/* 
		 * Look for any existing attack enhancements.
		 * If we have one, add to it instead of making a
		 * new one.
		 */
		double amount = Double.valueOf(this.variables.get("amount"));
		double baseLevel = amount;
        //attrs.hasEnhancement(Enchantment.getByKey(new NamespacedKey(CivCraft.getPlugin(), "LoreEnhancementDefense")))
		if (attrs.hasEnhancement("LoreEnhancementDefense")) {
			
			//attrs.getEnhancementData(new NamespacedKey(CivCraft.getPlugin(), "LoreEnhancementDefense"), "level")
			/* Get base Level. */
			baseLevel = Double.valueOf(attrs.getEnhancementData("LoreEnhancementDefense", "level"));
			
			/* Reset the lore. */
			String lore[] = attrs.getLore();
			for (int i = 0; i < lore.length; i++) {
				if (lore[i].equals(getLoreString(baseLevel))) {
					lore[i].replaceAll(lore[i], getLoreString(baseLevel+amount));
				}
			}
			attrs.setLore(lore);
			
			/* Reset the item name. */
			String split[] = attrs.getName().split("\\(");
			attrs.setName(split[0]+"(+"+(baseLevel+amount)+")");
			
			/* Store the data back in the enhancement. */
			attrs.setEnhancementData("LoreEnhancementDefense", "level", ""+(baseLevel+amount));
		} else {
			//attrs.addEnhancement(new NamespacedKey(CivCraft.getPlugin(), "LoreEnhancementDefense"), "level", ""+baseLevel);
			attrs.addEnhancement("LoreEnhancementDefense", "level", ""+baseLevel);
			attrs.addLore(getLoreString(baseLevel));
			attrs.setName(attrs.getName()+CivColor.LightBlue+"(+"+amount+")");
		}
		
		return attrs;
	}
	
	@Override
	public boolean canEnchantItem(ItemStack item) {
		return isArmor(item);
	}
	
	@Override
	public double getLevel(AttributeUtil attrs) {	
		//attrs.hasEnhancement(Enchantment.getByKey(new NamespacedKey(CivCraft.getPlugin(), "LoreEnhancementDefense")))
		if (attrs.hasEnhancement("LoreEnhancementDefense")) {
			/* Get base Level. */
			//attrs.getEnhancementData(new NamespacedKey(CivCraft.getPlugin(), "LoreEnhancementDefense"), "level")
			Double baseLevel = Double.valueOf(attrs.getEnhancementData("LoreEnhancementDefense", "level")); 
			return baseLevel;
		}
		return 1;
	}
	
	public double getExtraDefense(AttributeUtil attrs) {
		double m;
		try {
			m = CivSettings.getDouble(CivSettings.civConfig, "global.defense_catalyst_multiplier");
			return getLevel(attrs)*m;
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
		}
		
		return getLevel(attrs);
	}
	
	@Override
	public String serialize(ItemStack stack) {
		AttributeUtil attrs = new AttributeUtil(stack);
		//attrs.getEnhancementData(new NamespacedKey(CivCraft.getPlugin(), "LoreEnhancementDefense"), "level");
		return attrs.getEnhancementData("LoreEnhancementDefense", "level");
	}

	@Override
	public ItemStack deserialize(ItemStack stack, String data) {
		AttributeUtil attrs = new AttributeUtil(stack);
		attrs.setEnhancementData("LoreEnhancementDefense", "level", data);
		attrs.setName(attrs.getName()+CivColor.LightBlue+"(+"+Double.valueOf(data)+")");
		return attrs.getStack();
	}
}
