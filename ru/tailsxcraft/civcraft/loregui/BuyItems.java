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
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.main.CivData;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.structure.Bank;
import ru.tailsxcraft.civcraft.structure.Barracks;
import ru.tailsxcraft.civcraft.structure.Grocer;
import ru.tailsxcraft.civcraft.structure.Library;
import ru.tailsxcraft.civcraft.structure.Structure;
import ru.tailsxcraft.civcraft.util.CivColor;

public class BuyItems implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
		Resident resident = CivGlobal.getResident(player);
		String itemName = LoreGuiItem.getActionData(stack, "itemName");
		Material itemId = Material.matchMaterial(LoreGuiItem.getActionData(stack, "itemId"));
		double price = Double.parseDouble(LoreGuiItem.getActionData(stack, "itemPrice"));
		int amount = Integer.parseInt(LoreGuiItem.getActionData(stack, "itemAmount"));
		Town town = CivGlobal.getTownFromId(Integer.parseInt(LoreGuiItem.getActionData(stack, "townId")));
    	Grocer grocer = null;
    	Collection<Structure> structs = town.getStructures();
    	for ( Structure struct : structs ) {
    		if ( struct instanceof Grocer ) {
    			grocer = (Grocer)struct;
    		}
    	}
		int payToTown = (int) Math.round((price*amount)*grocer.getNonResidentFee());
		try {
				resident = CivGlobal.getResident(player.getName());
				Town t = resident.getTown();
			
				if (t == town) {
					// Pay no taxes! You're a member.
					resident.buyItem(itemName, itemId, (price*amount), amount);
					CivMessage.send(player, CivColor.LightGreen + CivSettings.localize.localizedString("var_grocer_msgBought",amount,itemName,(price*amount)+" "+CivSettings.CURRENCY_NAME));
					return;
				} else {
					// Pay non-resident taxes
					resident.buyItem(itemName, itemId, (price*amount) + payToTown, amount);
					town.depositDirect(payToTown);
					CivMessage.send(player, CivColor.LightGreen + CivSettings.localize.localizedString("var_grocer_msgBought",amount,itemName,(price*amount),CivSettings.CURRENCY_NAME));
					CivMessage.send(player, CivColor.Yellow + CivSettings.localize.localizedString("var_grocer_msgPaidTaxes",town.getName(),payToTown+" "+CivSettings.CURRENCY_NAME));
				}
			
			}
			catch (CivException e) {
				CivMessage.send(player, CivColor.Rose + e.getMessage());
			}
		return;
	}



}
