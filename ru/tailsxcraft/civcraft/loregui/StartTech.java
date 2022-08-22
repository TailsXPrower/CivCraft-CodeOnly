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

public class StartTech implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
		Resident resident = CivGlobal.getResident(player);
	    
		Civilization civ = resident.getCiv();
		
		ConfigTech tech = CivSettings.getTechByName(LoreGuiItem.getActionData(stack, "techName"));
		if (tech == null) {
			player.closeInventory();
			CivMessage.sendError(player, CivSettings.localize.localizedString("var_cmd_civ_research_NotFound", LoreGuiItem.getActionData(stack, "techName")));
			return;
		}
		
		if (!civ.getTreasury().hasEnough(tech.getAdjustedTechCost(civ))) {
			player.closeInventory();
			CivMessage.sendError(player, CivSettings.localize.localizedString("var_cmd_civ_research_NotEnough1",CivSettings.CURRENCY_NAME,tech.name));
			return;
		}
		
		if(!tech.isAvailable(civ)) {
			player.closeInventory();
			CivMessage.sendError(player, CivSettings.localize.localizedString("cmd_civ_research_NotAllowedNow"));
			return;
		}
		
		if(civ.getResearchTech() == tech) {
			player.closeInventory();
			CivMessage.sendError(player, CivSettings.localize.localizedString("Мы уже изучаем эту технологию."));
			return;
		}
		
		if(civ.hasTechnology(tech.id)) {
			player.closeInventory();
			CivMessage.sendError(player, CivSettings.localize.localizedString("Мы уже изучили эту технологию."));
			return;
		}
		
		if (civ.getResearchTech() != null) {
			civ.setResearchProgress(0);
			CivMessage.send(player, CivColor.Rose+CivSettings.localize.localizedString("var_cmd_civ_research_lostProgress1",civ.getResearchTech().name));
			civ.setResearchTech(null);
		}
		
		player.closeInventory();
	
		try {
			civ.startTechnologyResearch(tech);
		} catch (CivException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CivMessage.sendMessageCiv(civ, CivColor.LightGreen+"Наша цивилизация начала изучение "+CivColor.LightBlue+tech.name);
		CivMessage.sendCiv(civ, CivSettings.localize.localizedString("var_cmd_civ_research_start",tech.name));
	}



}
