package ru.tailsxcraft.civcraft.loregui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import gpl.AttributeUtil;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigBuildableInfo;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.interactive.InteractiveRepairItem;
import ru.tailsxcraft.civcraft.items.components.RepairCost;
import ru.tailsxcraft.civcraft.loreenhancements.LoreEnhancement;
import ru.tailsxcraft.civcraft.lorestorage.LoreCraftableMaterial;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.lorestorage.LoreMaterial;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.structure.Library;
import ru.tailsxcraft.civcraft.structure.Structure;
import ru.tailsxcraft.civcraft.structure.wonders.Wonder;
import ru.tailsxcraft.civcraft.util.CivColor;

public class Build implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
		player.closeInventory();
		
		ConfigBuildableInfo sinfo = CivSettings.getBuildableInfoByName(LoreGuiItem.getActionData(stack, "buildName"));

		if (sinfo == null) {
			CivMessage.send(player, CivColor.Red+CivSettings.localize.localizedString("cmd_build_defaultUnknownStruct"));
		}
		
		Town town = getSelectedTown(player);
		
		if ( town == null ) return;
		
		if (sinfo.isWonder) {
			Wonder wonder = null;
			try {
				wonder = Wonder.newWonder(player.getLocation(), sinfo.id, town);
				wonder.buildPlayerPreview(player, player.getLocation());
			} catch (CivException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				CivMessage.send(player, CivSettings.localize.localizedString("internalIOException"));
			}
		} else {
			Structure struct = null;
			try {
				struct = Structure.newStructure(player.getLocation(), sinfo.id, town);
				struct.buildPlayerPreview(player, player.getLocation());
			} catch (CivException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				CivMessage.send(player, CivSettings.localize.localizedString("internalIOException"));
			}
		}
	}

	public Town getSelectedTown(Player player) {
		Resident res = CivGlobal.getResident(player);
		if (res != null && res.getTown() != null) {
			if (res.getSelectedTown() != null) {
				try {
					res.getSelectedTown().validateResidentSelect(res);
				} catch (CivException e) {
					CivMessage.send(player, CivColor.Yellow+CivSettings.localize.localizedString("var_cmd_townDeselectedInvalid",res.getSelectedTown().getName(),res.getTown().getName()));
					res.setSelectedTown(res.getTown());
					return res.getTown();
				}
				
				return res.getSelectedTown();
			} else {
				return res.getTown();
			}
		}
		CivMessage.send(player, CivSettings.localize.localizedString("cmd_notPartOfTown"));
		return null;
	}

}
