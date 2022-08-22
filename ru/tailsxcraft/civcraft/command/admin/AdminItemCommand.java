package ru.tailsxcraft.civcraft.command.admin;

import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ru.tailsxcraft.civcraft.command.CommandBase;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.loreenhancements.LoreEnhancement;
import ru.tailsxcraft.civcraft.loreenhancements.LoreEnhancementAttack;
import ru.tailsxcraft.civcraft.loreenhancements.LoreEnhancementDefense;
import ru.tailsxcraft.civcraft.loreenhancements.LoreEnhancementSoulBound;
import ru.tailsxcraft.civcraft.lorestorage.LoreCraftableMaterial;
import ru.tailsxcraft.civcraft.lorestorage.LoreMaterial;
import ru.tailsxcraft.civcraft.main.CivData;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.util.ItemManager;

public class AdminItemCommand extends CommandBase {

	@Override
	public void init() {
		command = "/ad item";
		displayName = CivSettings.localize.localizedString("adcmd_item_cmdDesc");
		
		commands.put("enhance", CivSettings.localize.localizedString("adcmd_item_enhanceDesc"));
		commands.put("give", CivSettings.localize.localizedString("adcmd_item_giveDesc"));
	}

	public void give_cmd() throws CivException {
		Resident resident = getNamedResident(1);
		String id = getNamedString(2, CivSettings.localize.localizedString("adcmd_item_givePrompt")+" materials.yml");
		int amount = getNamedInteger(3);
		
		Player player = CivGlobal.getPlayer(resident);
		
		LoreCraftableMaterial craftMat = LoreCraftableMaterial.getCraftMaterialFromId(id);
		if (craftMat == null) {
			throw new CivException(CivSettings.localize.localizedString("adcmd_item_giveInvalid")+id);
		}
		
		ItemStack stack = LoreCraftableMaterial.spawn(craftMat);
		
		stack.setAmount(amount);
		HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(stack);
		for (ItemStack is : leftovers.values()) {
			player.getWorld().dropItem(player.getLocation(), is);
		}
		
		CivMessage.sendSuccess(player, CivSettings.localize.localizedString("adcmd_item_giveSuccess"));
	}
	
	public void enhance_cmd() throws CivException {
		Player player = getPlayer();
		HashMap<String, LoreEnhancement> enhancements = new HashMap<String, LoreEnhancement>();
		ItemStack inHand = getPlayer().getInventory().getItemInMainHand();
		
		enhancements.put("soulbound", new LoreEnhancementSoulBound());
		enhancements.put("attack", new LoreEnhancementAttack());
		enhancements.put("defence", new LoreEnhancementDefense());

		if (inHand == null || ItemManager.getType(inHand) == CivData.AIR) {
			throw new CivException(CivSettings.localize.localizedString("adcmd_item_enhanceNoItem"));
		}
		
		if (args.length < 2) {
			CivMessage.sendHeading(sender, CivSettings.localize.localizedString("adcmd_item_enhancementList"));
			String out = "";
			for (String str : enhancements.keySet()) {
				out += str + ", ";
			}
			CivMessage.send(sender, out);
			return;
		}
		
		String name = getNamedString(1, "enchantname");
		name.toLowerCase();
		for (String str : enhancements.keySet()) {
			if (name.equals(str)) {
				LoreEnhancement enh = enhancements.get(str);
				ItemStack stack = LoreMaterial.addEnhancement(inHand, enh);
				player.getInventory().setItemInMainHand(stack);
				CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_adcmd_item_enhanceSuccess",name));
				return;
			}
		}
	}
	
	@Override
	public void doDefaultAction() throws CivException {
		showHelp();
	}

	@Override
	public void showHelp() {
		showBasicHelp();
	}

	@Override
	public void permissionCheck() throws CivException {
		
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

}
