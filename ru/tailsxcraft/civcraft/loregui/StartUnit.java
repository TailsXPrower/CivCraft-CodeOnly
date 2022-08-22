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
import ru.tailsxcraft.civcraft.config.ConfigUnit;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.main.CivData;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.sessiondb.SessionEntry;
import ru.tailsxcraft.civcraft.structure.Bank;
import ru.tailsxcraft.civcraft.structure.Barracks;
import ru.tailsxcraft.civcraft.structure.Library;
import ru.tailsxcraft.civcraft.structure.Structure;
import ru.tailsxcraft.civcraft.util.CivColor;

public class StartUnit implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
		Resident resident = CivGlobal.getResident(player);
	    if ( event.getView().getTitle().equalsIgnoreCase("Бараки")) {
	    	Town town = CivGlobal.getTownFromId(Integer.parseInt(LoreGuiItem.getActionData(stack, "townId")));
	    	String unitId = LoreGuiItem.getActionData(stack, "unitId");
	    	Barracks barracks = null;
	    	Collection<Structure> structs = town.getStructures();
	    	for ( Structure struct : structs ) {
	    		if ( struct instanceof Barracks ) {
	    			barracks = (Barracks)struct;
	    		}
	    	}
	    	
	    	for ( ConfigUnit unit : town.getAvailableUnits() ) {
				    if (unit == null) {
				    	player.closeInventory();
						CivMessage.send(player, CivColor.Rose+CivSettings.localize.localizedString("barracks_unknownUnit"));
						return;
					}
				    
				    if ( !unit.id.equalsIgnoreCase(unitId) ) continue;
					
					if (unit.limit != 0 && unit.limit < town.getUnitTypeCount(unit.id)) {
						player.closeInventory();
						CivMessage.send(player, CivColor.Rose+CivSettings.localize.localizedString("var_barracks_atLimit",unit.name));
						return;
					}
					
					if (!unit.isAvailable(town)) {
						player.closeInventory();
						CivMessage.send(player, CivColor.Rose+CivSettings.localize.localizedString("barracks_unavailable"));
						return;
					}
					
					if (barracks.getTrainingUnit() != null) {
						player.closeInventory();
						CivMessage.send(player, CivColor.Rose+CivSettings.localize.localizedString("var_barracks_inProgress",barracks.getTrainingUnit().name));
						return;
					}

					int previousSettlers = 1;
					double coinCost = unit.cost;
					if (unit.id.equals("u_settler")) {
						if (!barracks.getCiv().getLeaderGroup().hasMember(resident) && !barracks.getCiv().getAdviserGroup().hasMember(resident)) {
							player.closeInventory();
							CivMessage.send(player, CivColor.Rose+CivSettings.localize.localizedString("barracks_trainSettler_NoPerms"));
							return;
						}
						
						ArrayList<SessionEntry> entries = CivGlobal.getSessionDB().lookup("settlers:"+barracks.getCiv().getName());
						if (entries != null) {
							CivLog.debug("entries: "+entries.size());
							for (SessionEntry entry : entries) {
								CivLog.debug("value: "+entry.value);
								previousSettlers += Integer.parseInt(entry.value);
							}
						}

						CivLog.debug("previousSettlers: "+previousSettlers);
						coinCost *= previousSettlers;
						CivLog.debug("unit.cost: "+coinCost);
					}
					
					if (!town.getTreasury().hasEnough(coinCost)) {
						player.closeInventory();
						CivMessage.send(player, CivColor.Rose+CivSettings.localize.localizedString("var_barracks_tooPoor",unit.name,coinCost,CivSettings.CURRENCY_NAME));
						return;
					}
					
					
					town.getTreasury().withdraw(coinCost);
					
					player.closeInventory();
					
					barracks.setCurrentHammers(0.0);
					barracks.setTrainingUnit(unit);
					CivMessage.sendTown(town, CivSettings.localize.localizedString("var_barracks_begin",unit.name));
					barracks.updateTraining();
					if (unit.id.equals("u_settler")) {
						CivGlobal.getSessionDB().add("settlers:"+barracks.getCiv().getName(), "1" , barracks.getCiv().getId(), barracks.getCiv().getId(), barracks.getId());
					}
			}
	    }
	}



}
