package ru.tailsxcraft.civcraft.loreenhancements;

import gpl.AttributeUtil;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.main.CivCraft;
import ru.tailsxcraft.civcraft.object.BuildableDamageBlock;
import ru.tailsxcraft.civcraft.util.CivColor;

import java.util.Random;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class LoreEnhancementPunchout extends LoreEnhancement {
	
	public String getDisplayName() {
		return CivSettings.localize.localizedString("itemLore_Punchout");
	}
	
	public String getName() {
		return "LoreEnhancementPunchout";
	}
	
	public AttributeUtil add(AttributeUtil attrs) {
		// attrs.addEnhancement(new NamespacedKey(CivCraft.getPlugin(), "LoreEnhancementPunchout"), null, null);
		attrs.addEnhancement("LoreEnhancementPunchout", null, null);
		attrs.addLore(CivColor.Gold+getDisplayName());
		return attrs;
	}
	
	@Override
	public int onStructureBlockBreak(BuildableDamageBlock sb, int damage) {
		Random rand = new Random();
		
		if (damage <= 1) {
			if (rand.nextInt(100) <= 50) {
				damage += rand.nextInt(5)+1;
			}		
		}
		
		return damage; 
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