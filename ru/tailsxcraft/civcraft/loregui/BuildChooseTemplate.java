package ru.tailsxcraft.civcraft.loregui;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigBuildableInfo;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.main.CivData;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.structure.Structure;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.tutorial.CivTutorial;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.civcraft.util.ItemManager;
import ru.tailsxcraft.global.perks.Perk;

public class BuildChooseTemplate implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
		Resident resident = CivGlobal.getResident(player);
		ConfigBuildableInfo sinfo = CivSettings.structures.get(LoreGuiItem.getActionData(stack, "info"));
		Structure struct;
		try {
			struct = Structure.newStructure(player.getLocation(), sinfo.id, resident.getTown());
		} catch (CivException e) {
			e.printStackTrace();
			return;
		}
		
		/* Look for any custom template perks and ask the player if they want to use them. */
		ArrayList<Perk> perkList = struct.getTown().getTemplatePerks(struct, resident, struct.info);		
		ArrayList<Perk> personalUnboundPerks = resident.getUnboundTemplatePerks(perkList, struct.info);
		//if (perkList.size() != 0 || personalUnboundPerks.size() != 0) {
			/* Store the pending buildable. */
		resident.pendingBuildable = struct;
		
		/* Build an inventory full of templates to select. */
		Inventory inv = Bukkit.getServer().createInventory(player, CivTutorial.MAX_CHEST_SIZE*9);
		ItemStack infoRec = LoreGuiItem.build("Default "+struct.getDisplayName(), 
				ItemManager.getType(Material.WRITTEN_BOOK), 
				CivColor.Gold+CivSettings.localize.localizedString("loreGui_template_clickToBuild"));
		infoRec = LoreGuiItem.setAction(infoRec, "BuildWithTemplate");
		inv.addItem(infoRec);
		
		for (Perk perk : perkList) {
			if (!perk.getIdent().contains("template"))
			{
			infoRec = LoreGuiItem.build(perk.getDisplayName(), 
					perk.configPerk.type_id, 
					CivColor.Gold+CivSettings.localize.localizedString("loreGui_template_clickToBuild"),
					CivColor.Gray+CivSettings.localize.localizedString("loreGui_template_providedBy")+" "+CivColor.LightBlue+perk.provider);
			infoRec = LoreGuiItem.setAction(infoRec, "BuildWithTemplate");
			infoRec = LoreGuiItem.setActionData(infoRec, "perk", perk.getIdent());
			inv.addItem(infoRec);
			}
		}
		
		for (Perk perk : personalUnboundPerks) {
			if (!perk.getIdent().contains("template"))
			{
			infoRec = LoreGuiItem.build(perk.getDisplayName(), 
					CivData.BEDROCK, 
					CivColor.Gold+CivSettings.localize.localizedString("loreGui_template_clickToBuild"),
					CivColor.Gray+CivSettings.localize.localizedString("loreGui_template_unbound"),
					CivColor.Gray+CivSettings.localize.localizedString("loreGui_template_unbound2"),
					CivColor.Gray+CivSettings.localize.localizedString("loreGui_template_unbound3"),
					CivColor.Gray+CivSettings.localize.localizedString("loreGui_template_unbound4"),
					CivColor.Gray+CivSettings.localize.localizedString("loreGui_template_unbound5"));				
			infoRec = LoreGuiItem.setAction(infoRec, "ActivatePerk");
			infoRec = LoreGuiItem.setActionData(infoRec, "perk", perk.getIdent());
			}
		}
		
		TaskMaster.syncTask(new OpenInventoryTask(player, inv));
		return;		
	}
}
