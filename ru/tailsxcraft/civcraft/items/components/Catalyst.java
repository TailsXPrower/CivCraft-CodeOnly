package ru.tailsxcraft.civcraft.items.components;

import gpl.AttributeUtil;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.loreenhancements.LoreEnhancement;
import ru.tailsxcraft.civcraft.lorestorage.LoreCraftableMaterial;
import ru.tailsxcraft.civcraft.lorestorage.LoreMaterial;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.util.CivColor;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class Catalyst extends ItemComponent {

	@Override
	public void onPrepareCreate(AttributeUtil attrUtil) {
		attrUtil.addLore(ChatColor.RESET+CivColor.Gold+CivSettings.localize.localizedString("itemLore_Catalyst"));
	}

	public ItemStack getEnchantedItem(ItemStack stack) {
		
		LoreCraftableMaterial craftMat = LoreCraftableMaterial.getCraftMaterial(stack);
		if (craftMat == null) {
			return null;
		}
		
		String materials[] = this.getString("allowed_materials").split(",");
		boolean found = false;
		for (String mat : materials) {
			mat = mat.replaceAll(" ", "");
			if (mat.equals(LoreMaterial.getMID(stack))) {
				found = true;
				break;
			}
		}
		
		if (!found) {
			return null;
		}
		
		String enhStr = this.getString("enhancement");

		LoreEnhancement enhance = LoreEnhancement.enhancements.get(enhStr);
		if (enhance == null) {
			CivLog.error("Couldn't find enhancement titled:"+enhStr);
			return null;
		}
		
		if (enhance != null) {
			if (enhance.canEnchantItem(stack)) {
				AttributeUtil attrs = new AttributeUtil(stack);
				enhance.variables.put("amount", getString("amount"));
				attrs = enhance.add(attrs);	
				return attrs.getStack();
			}
		}
		
		return null;
	}
	
	public int getEnhancedLevel(ItemStack stack) {
		String enhStr = this.getString("enhancement");

		LoreEnhancement enhance = LoreEnhancement.enhancements.get(enhStr);
		if (enhance == null) {
			CivLog.error("Couldn't find enhancement titled:"+enhStr);
			return 0;
		}
		
		return (int)enhance.getLevel(new AttributeUtil(stack));
	}

	public boolean enchantSuccess(ItemStack stack) {
		try {
			int free_catalyst_amount = CivSettings.getInteger(CivSettings.civConfig, "global.free_catalyst_amount");
			int extra_catalyst_amount = CivSettings.getInteger(CivSettings.civConfig, "global.extra_catalyst_amount");
			double extra_catalyst_percent = CivSettings.getDouble(CivSettings.civConfig, "global.extra_catalyst_percent");
			
			int level = getEnhancedLevel(stack);
			
			if (level <= free_catalyst_amount) {
				return true;
			}
			
			int chance = Integer.valueOf(getString("chance"));
			Random rand = new Random();
			int extra = 0;
			int n = rand.nextInt(100);			
			
			if (level <= extra_catalyst_amount) {
				n -= (int)(extra_catalyst_percent*100);
			}
			
			n += extra;
			
			if (n <= chance) {
				return true;
			}
			
			return false;
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
}
