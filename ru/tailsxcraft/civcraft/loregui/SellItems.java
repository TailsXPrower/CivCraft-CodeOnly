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
import ru.tailsxcraft.civcraft.structure.Library;
import ru.tailsxcraft.civcraft.structure.Structure;
import ru.tailsxcraft.civcraft.util.CivColor;

public class SellItems implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
		Resident resident = CivGlobal.getResident(player);
	    if ( event.getView().getTitle().equalsIgnoreCase("Банк")) {
	    	Inventory inv = event.getInventory();
	    	List<ItemStack> items = new ArrayList<ItemStack>();
	    	for ( int i = 0; i < 17; i++ ) {
	    		if ( i == 8 ) continue;
	    		ItemStack item = inv.getItem(i);
	    		if ( item == null || item.getType() == Material.AIR) continue;
	    		if ( item.getType() != Material.IRON_INGOT && item.getType() != Material.IRON_BLOCK 
	    				&& item.getType() != Material.GOLD_INGOT && item.getType() != Material.GOLD_BLOCK 
	    				&& item.getType() != Material.DIAMOND && item.getType() != Material.DIAMOND_BLOCK 
	    				&& item.getType() != Material.EMERALD && item.getType() != Material.EMERALD_BLOCK ) continue;
	    		items.add(item);
	    	}
	    	
	    	if ( items.size() == 0 ) {
	    		return;
	    	}
	    	
	    	Town usersTown = resident.getTown();
	    	
	    	Town bankTown = CivGlobal.getTownFromId(Integer.parseInt(LoreGuiItem.getActionData(stack, "townId")));
	    	Bank bank = null;
	    	Collection<Structure> structs = bankTown.getStructures();
	    	for ( Structure struct : structs ) {
	    		if ( struct instanceof Bank ) {
	    			bank = (Bank)struct;
	    		}
	    	}
	    	
	        int exchange_value = 0;
	        for ( ItemStack item : items ) {
	        	if ( item.getType() == Material.IRON_INGOT ) {
	        		exchange_value += CivSettings.iron_rate*item.getAmount();
	        	} else if ( item.getType() == Material.GOLD_INGOT ) {
	        		exchange_value += CivSettings.gold_rate*item.getAmount();
	        	} else if ( item.getType() == Material.DIAMOND ) {
	        		exchange_value += CivSettings.diamond_rate*item.getAmount();
	        	} else if ( item.getType() == Material.EMERALD ) {
	        		exchange_value += CivSettings.emerald_rate*item.getAmount();
	        	} else if ( item.getType() == Material.IRON_BLOCK ) {
	        		exchange_value += (CivSettings.iron_rate*9)*item.getAmount();
	        	} else if ( item.getType() == Material.GOLD_BLOCK ) {
	        		exchange_value += (CivSettings.gold_rate*9)*item.getAmount();
	        	} else if ( item.getType() == Material.DIAMOND_BLOCK ) {
	        		exchange_value += (CivSettings.diamond_rate*9)*item.getAmount();
	        	} else if ( item.getType() == Material.EMERALD_BLOCK ) {
	        		exchange_value += (CivSettings.emerald_rate*9)*item.getAmount();
	        	} 
	        }
	    	
	    	//CivSettings.gold_rate
	    	
	    	double exchange_rate = 0.0;
	    	
	    	exchange_rate = bank.getBankExchangeRate();
			
	    	for ( int i = 0; i < 17; i++ ) {
	    		if ( i == 8 ) continue;
	    		ItemStack item = inv.getItem(i);
	    		if ( item == null || item.getType() == Material.AIR) continue;
	    		if ( item.getType() != Material.IRON_INGOT && item.getType() != Material.IRON_BLOCK 
	    				&& item.getType() != Material.GOLD_INGOT && item.getType() != Material.GOLD_BLOCK 
	    				&& item.getType() != Material.DIAMOND && item.getType() != Material.DIAMOND_BLOCK 
	    				&& item.getType() != Material.EMERALD && item.getType() != Material.EMERALD_BLOCK ) continue;
	    		inv.clear(i);
	    	}
	    	
			// Resident is in his own town.
			if (usersTown == bankTown) {		
				resident.getTreasury().deposit((double)((int)((exchange_value)*exchange_rate)));
				CivMessage.send(player,
						CivColor.LightGreen + "Проданы ресурсы за "+(Math.round((exchange_value)*exchange_rate))+" "+CivSettings.CURRENCY_NAME);
				return;
			}
			
			// non-resident must pay the town's non-resident tax
			double giveToPlayer = (double)((int)((exchange_value)*exchange_rate));
			double giveToTown = (double)((int)giveToPlayer*bank.getNonResidentFee());
			giveToPlayer -= giveToTown;
			
			giveToTown = Math.round(giveToTown);
			giveToPlayer = Math.round(giveToPlayer);
			
			bankTown.depositDirect(giveToTown);
			resident.getTreasury().deposit(giveToPlayer);
			
			CivMessage.send(player, CivColor.LightGreen + "Проданы ресурсы за "+giveToPlayer+" "+CivSettings.CURRENCY_NAME);
			CivMessage.send(player,CivColor.Yellow+" "+CivSettings.localize.localizedString("var_taxes_paid",giveToTown,CivSettings.CURRENCY_NAME));
			return;
	    }
	}



}
