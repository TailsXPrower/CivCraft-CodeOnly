package ru.tailsxcraft.civcraft.loregui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import gpl.AttributeUtil;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigTech;
import ru.tailsxcraft.civcraft.config.ConfigTownUpgrade;
import ru.tailsxcraft.civcraft.config.ConfigUnit;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.main.CivData;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Civilization;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.sessiondb.SessionEntry;
import ru.tailsxcraft.civcraft.structure.Bank;
import ru.tailsxcraft.civcraft.structure.Barracks;
import ru.tailsxcraft.civcraft.structure.Library;
import ru.tailsxcraft.civcraft.structure.Structure;
import ru.tailsxcraft.civcraft.util.CivColor;

public class BuyUpgrade implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
		Resident resident = CivGlobal.getResident(player);
	    
		Town town = null;
		if ( resident.getSelectedTown() != null ) {
		    town = resident.getSelectedTown();
		} else {
		    town = resident.getTown();
		}
		
		ConfigTownUpgrade upgrade = null;
		try {
			upgrade = CivSettings.getUpgradeByNameRegex(town, LoreGuiItem.getActionData(stack, "upgradeName"));
		} catch (CivException e1) {
			
		}
		
		if (upgrade == null) {
			player.closeInventory();
			CivMessage.sendError(player, "Улучшение с таким названием не доступно:"+" "+LoreGuiItem.getActionData(stack, "upgradeName"));
			return;
		}
		
		if (town.hasUpgrade(upgrade.id)) {
			player.closeInventory();
			CivMessage.sendError(player, CivSettings.localize.localizedString("cmd_town_upgrade_buyOwned"));
			return;
		}
		
		player.closeInventory();
	
		try {
			town.purchaseUpgrade(upgrade);
		} catch (CivException e) {
			
		}
		
		CivMessage.sendSuccess(player, CivSettings.localize.localizedString("var_cmd_town_upgrade_buySuccess",upgrade.name));
	}



}
