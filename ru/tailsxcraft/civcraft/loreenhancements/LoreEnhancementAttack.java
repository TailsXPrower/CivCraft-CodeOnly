package ru.tailsxcraft.civcraft.loreenhancements;

import gpl.AttributeUtil;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.lorestorage.LoreCraftableMaterial;
import ru.tailsxcraft.civcraft.main.CivCraft;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.util.CivColor;

import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class LoreEnhancementAttack extends LoreEnhancement {
	
	public LoreEnhancementAttack() {
		this.variables.put("amount", "1.0");
	}
	
	public String getLoreString(double baseLevel) {
		double m;
		try {
			m = CivSettings.getDouble(CivSettings.civConfig, "global.attack_catalyst_multiplier");
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			m = 1;
		}
		return CivColor.Blue+"+"+(baseLevel*m)+" "+CivSettings.localize.localizedString("itemLore_Attack");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public AttributeUtil add(AttributeUtil attrs) {		
		/* 
		 * Look for any existing attack enhancements.
		 * If we have one, add to it instead of making a
		 * new one.
		 */
		
		double amount = Double.valueOf(this.variables.get("amount"));
		double baseLevel = amount;
		//Enchantment.getByKey(new NamespacedKey(CivCraft.getPlugin(), "LoreEnhancementAttack")))
		if (attrs.hasEnhancement("LoreEnhancementAttack")) {
			
			/* Get base Level. */
			//baseLevel = Double.valueOf(attrs.getEnhancementData(new NamespacedKey(CivCraft.getPlugin(), "LoreEnhancementAttack"), "level"));
			baseLevel = Double.valueOf(attrs.getEnhancementData("LoreEnhancementAttack", "level"));

			/* Reset the lore. */
			//List<String> lore = attrs.getLore();
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
			attrs.setEnhancementData("LoreEnhancementAttack", "level", ""+(baseLevel+amount));
		} else {
			//attrs.addEnhancement(new NamespacedKey(CivCraft.getPlugin(), "LoreEnhancementAttack"), "level", ""+baseLevel);
			attrs.addEnhancement("LoreEnhancementAttack", "level", ""+baseLevel);
			attrs.addLore(getLoreString(baseLevel));
			attrs.setName(attrs.getName()+CivColor.LightBlue+"(+"+amount+")");
		}

		LoreCraftableMaterial craftMat = LoreCraftableMaterial.getCraftMaterialFromId(attrs.getCivCraftProperty("mid"));
		if (craftMat == null) {
			CivLog.warning("Couldn't find craft mat with MID of:"+attrs.getCivCraftProperty("mid"));
			return attrs;
		}

		return attrs;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public double getLevel(AttributeUtil attrs) {	
		//attrs.hasEnhancement(Enchantment.getByKey(new NamespacedKey(CivCraft.getPlugin(), "LoreEnhancementAttack"))
		if (attrs.hasEnhancement("LoreEnhancementAttack")) {
			/* Get base Level. */
			//attrs.getEnhancementData(new NamespacedKey(CivCraft.getPlugin(), "LoreEnhancementAttack"), "level")
			Double baseLevel = Double.valueOf(attrs.getEnhancementData("LoreEnhancementAttack", "level")); 
			return baseLevel;
		}
		return 0;
	}


	@Override
	public boolean canEnchantItem(ItemStack item) {
		return isWeapon(item);
	}

	public double getExtraAttack(AttributeUtil attrs) {
		double m;
		try {
			m = CivSettings.getDouble(CivSettings.civConfig, "global.attack_catalyst_multiplier");
			return getLevel(attrs)*m;
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
		}
		
		return getLevel(attrs);
	}
	
	@Override
	public String serialize(ItemStack stack) {
		AttributeUtil attrs = new AttributeUtil(stack);
		//attrs.getEnhancementData(new NamespacedKey(CivCraft.getPlugin(), "LoreEnhancementAttack"), "level");
		return attrs.getEnhancementData("LoreEnhancementAttack", "level");
	}

	@Override
	public ItemStack deserialize(ItemStack stack, String data) {
		AttributeUtil attrs = new AttributeUtil(stack);
		attrs.setEnhancementData("LoreEnhancementAttack", "level", data);
		attrs.setName(attrs.getName()+CivColor.LightBlue+"(+"+Double.valueOf(data)+")");
		return attrs.getStack();
	}
}
