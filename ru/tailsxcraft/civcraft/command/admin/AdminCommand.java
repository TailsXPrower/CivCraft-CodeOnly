/*************************************************************************
 * 
 * AVRGAMING LLC
 * __________________
 * 
 *  [2013] AVRGAMING LLC
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of AVRGAMING LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to AVRGAMING LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from AVRGAMING LLC.
 */
package ru.tailsxcraft.civcraft.command.admin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ru.tailsxcraft.civcraft.command.CommandBase;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigGovernment;
import ru.tailsxcraft.civcraft.config.ConfigMaterial;
import ru.tailsxcraft.civcraft.config.ConfigMaterialCategory;
import ru.tailsxcraft.civcraft.config.ConfigUnit;
import ru.tailsxcraft.civcraft.endgame.EndGameCondition;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.lorestorage.LoreCraftableMaterial;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItemListener;
import ru.tailsxcraft.civcraft.lorestorage.LoreMaterial;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Civilization;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.sessiondb.SessionEntry;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.util.ChunkCoord;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.civcraft.util.ItemManager;
import ru.tailsxcraft.sls.SLSManager;

public class AdminCommand extends CommandBase {

	@Override
	public void init() {
		command = "/ad";
		displayName = CivSettings.localize.localizedString("adcmd_Name");
		
		commands.put("perm", CivSettings.localize.localizedString("adcmd_permDesc"));
		commands.put("sbperm", CivSettings.localize.localizedString("adcmd_adpermDesc"));
		commands.put("cbinstantbreak", CivSettings.localize.localizedString("adcmd_cbinstantbreakDesc"));

		commands.put("recover", CivSettings.localize.localizedString("adcmd_recoverDesc"));
		commands.put("server", CivSettings.localize.localizedString("adcmd_serverDesc"));
		commands.put("spawnunit", CivSettings.localize.localizedString("adcmd_spawnUnitDesc"));
		
		commands.put("civ", CivSettings.localize.localizedString("adcmd_civDesc"));
		commands.put("town", CivSettings.localize.localizedString("adcmd_townDesc"));
		commands.put("war", CivSettings.localize.localizedString("adcmd_warDesc"));
		commands.put("lag", CivSettings.localize.localizedString("adcmd_lagdesc"));	
		commands.put("camp", CivSettings.localize.localizedString("adcmd_campDesc"));
		commands.put("chat", CivSettings.localize.localizedString("adcmd_chatDesc"));
		commands.put("res", CivSettings.localize.localizedString("adcmd_resDesc"));
		commands.put("build", CivSettings.localize.localizedString("adcmd_buildDesc"));
		commands.put("items", CivSettings.localize.localizedString("adcmd_itemsDesc"));
		commands.put("item", CivSettings.localize.localizedString("adcmd_itemDesc"));
		commands.put("timer", CivSettings.localize.localizedString("adcmd_timerDesc"));
		commands.put("road", CivSettings.localize.localizedString("adcmd_roadDesc"));
		commands.put("clearendgame", CivSettings.localize.localizedString("adcmd_clearEndGameDesc"));
		commands.put("endworld", CivSettings.localize.localizedString("adcmd_endworldDesc"));
		commands.put("perk", CivSettings.localize.localizedString("adcmd_perkDesc"));
		commands.put("reloadgov", CivSettings.localize.localizedString("adcmd_reloadgovDesc"));
		commands.put("reloadac", CivSettings.localize.localizedString("adcmd_reloadacDesc"));
		commands.put("reload", "Перезагрузка конфигураций");
		commands.put("heartbeat", CivSettings.localize.localizedString("adcmd_heartbeatDesc"));
	}
	
	public void reload_cmd() throws FileNotFoundException, IOException, InvalidConfigurationException, InvalidConfiguration {
		CivSettings.reloadConfigFiles();
		
		CivMessage.send(sender, CivColor.Gold+"Перезагрузка конфигураций");
	}
	
	public void reloadgov_cmd() throws FileNotFoundException, IOException, InvalidConfigurationException, InvalidConfiguration {
		CivSettings.reloadGovConfigFiles();
		for (Civilization civ : CivGlobal.getCivs())
		{
			ConfigGovernment gov = civ.getGovernment();
			
			civ.setGovernment(gov.id);
		}
		CivMessage.send(sender, CivColor.Gold+CivSettings.localize.localizedString("adcmd_reloadgovSuccess"));
	}
	
	public void heartbeat_cmd() {
		SLSManager.sendHeartbeat();
	}
	
	public void reloadac_cmd() throws FileNotFoundException, IOException, InvalidConfigurationException, InvalidConfiguration {
		CivSettings.reloadNoCheat();
		CivMessage.send(sender, CivColor.Gold+CivSettings.localize.localizedString("adcmd_reloadacSuccess"));
	}
	
	
	public void perk_cmd() {
		AdminPerkCommand cmd = new AdminPerkCommand();	
		cmd.onCommand(sender, null, "perk", this.stripArgs(args, 1));
	}
	
	public void endworld_cmd() {
		CivGlobal.endWorld = !CivGlobal.endWorld;
		if (CivGlobal.endWorld) {			
			CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("adcmd_endworldOn"));
		} else {
			CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("adcmd_endworldOff"));
		}
	}
	
	public void clearendgame_cmd() throws CivException {
		String key = getNamedString(1, "enter key.");
		Civilization civ = getNamedCiv(2);
		
		ArrayList<SessionEntry> entries = CivGlobal.getSessionDB().lookup(key);
		if (entries.size() == 0) {
			throw new CivException(CivSettings.localize.localizedString("adcmd_clearEndGameNoKey"));
		}
		
		for (SessionEntry entry : entries) {
			if (EndGameCondition.getCivFromSessionData(entry.value) == civ) {
				CivGlobal.getSessionDB().delete(entry.request_id, entry.key);
				CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_adcmd_clearEndGameSuccess",civ.getName()));
			}
		}		
	}
	
	public void cbinstantbreak_cmd() throws CivException {
		Resident resident = getResident();
		
		resident.setControlBlockInstantBreak(!resident.isControlBlockInstantBreak());
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("adcmd_cbinstantbreakSuccess")+resident.isControlBlockInstantBreak());
	}
	
	public static Inventory spawnInventory = null; 
	public void items_cmd() throws CivException {
		Player player = getPlayer();
		
		if (spawnInventory == null) {
			spawnInventory = Bukkit.createInventory(player, LoreGuiItem.MAX_INV_SIZE, CivSettings.localize.localizedString("adcmd_itemsHeader"));
			
			/* Build the Category Inventory. */
			for (ConfigMaterialCategory cat : ConfigMaterialCategory.getCategories()) {
				Material identifier;
				if (cat.name.contains("Рыб")) {
					identifier = ItemManager.getType(Material.COD);
				}
				else if (cat.name.contains("Катализаторы")) {
					identifier = ItemManager.getType(Material.BOOK);
				}
				else if (cat.name.contains("Амуниция")) {
					identifier = ItemManager.getType(Material.IRON_SWORD);
				}
				else if (cat.name.contains("Материалы")) {
					identifier = ItemManager.getType(Material.OAK_SLAB);
				}
				else if (cat.name.contains("Инструменты")) {
					identifier = ItemManager.getType(Material.IRON_SHOVEL);
				}
				else if (cat.name.contains("Eggs")) {
					identifier = ItemManager.getType(Material.VILLAGER_SPAWN_EGG);
				}
				else {
					identifier = ItemManager.getType(Material.WRITTEN_BOOK);
				}
				ItemStack infoRec = LoreGuiItem.build(cat.name, 
						identifier, 
						CivColor.LightBlue+cat.materials.size()+" Items",
						CivColor.Gold+"<Click To Open>");
						infoRec = LoreGuiItem.setAction(infoRec, "OpenInventory");
						infoRec = LoreGuiItem.setActionData(infoRec, "invType", "showGuiInv");
						infoRec = LoreGuiItem.setActionData(infoRec, "invName", cat.name+" Spawn");
						spawnInventory.addItem(infoRec);
						
				/* Build a new GUI Inventory. */
				Inventory inv = Bukkit.createInventory(player, LoreGuiItem.MAX_INV_SIZE, cat.name+" Spawn");
				for (ConfigMaterial mat : cat.materials.values()) {
					LoreCraftableMaterial craftMat = LoreCraftableMaterial.getCraftMaterialFromId(mat.id);
					if ( craftMat.getId().contains("_alt")) continue;
					ItemStack stack = LoreMaterial.spawn(craftMat);
					stack = LoreGuiItem.asGuiItem(stack);
					stack = LoreGuiItem.setAction(stack, "SpawnItem");
					inv.addItem(stack);
					LoreGuiItemListener.guiInventories.put(cat.name+" Spawn", inv);			
				}
			}
			

		}
		
		player.openInventory(spawnInventory);
	}
	
	public void road_cmd() {
		AdminRoadCommand cmd = new AdminRoadCommand();	
		cmd.onCommand(sender, null, "camp", this.stripArgs(args, 1));
	}
	
	public void item_cmd() {
		AdminItemCommand cmd = new AdminItemCommand();	
		cmd.onCommand(sender, null, "camp", this.stripArgs(args, 1));
	}
	
	public void timer_cmd() {
		AdminTimerCommand cmd = new AdminTimerCommand();	
		cmd.onCommand(sender, null, "camp", this.stripArgs(args, 1));	
	}
	
	public void camp_cmd() {
		AdminCampCommand cmd = new AdminCampCommand();	
		cmd.onCommand(sender, null, "camp", this.stripArgs(args, 1));
	}
	
	public void spawnunit_cmd() throws CivException {		
		if (args.length < 2) {
			throw new CivException(CivSettings.localize.localizedString("adcmd_spawnUnitPrompt"));
		}
		
		ConfigUnit unit = CivSettings.units.get(args[1]);
		if (unit == null) {
			throw new CivException( CivSettings.localize.localizedString("var_adcmd_spawnUnitInvalid",args[1]));
		}
		
		Player player = getPlayer();
		Town town = getNamedTown(2);
		
//		if (args.length > 2) {
//			try {
//				player = CivGlobal.getPlayer(args[2]);
//			} catch (CivException e) {
//				throw new CivException("Player "+args[2]+" is not online.");
//			}
//		} else {
//			player = getPlayer();
//		}
		
		Class<?> c;
		try {
			c = Class.forName(unit.class_name);
			Method m = c.getMethod("spawn", Inventory.class, Town.class);
			m.invoke(null, player.getInventory(), town);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException 
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new CivException(e.getMessage());
		}

		
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("var_adcmd_spawnUnitSuccess",unit.name));
	}
	
	public void server_cmd() {
		CivMessage.send(sender, Bukkit.getServer().getName());
	}
	
	
	public void recover_cmd() {
		AdminRecoverCommand cmd = new AdminRecoverCommand();	
		cmd.onCommand(sender, null, "recover", this.stripArgs(args, 1));	
	}
	
	public void town_cmd() {
		AdminTownCommand cmd = new AdminTownCommand();	
		cmd.onCommand(sender, null, "town", this.stripArgs(args, 1));
	}
	
	public void civ_cmd() {
		AdminCivCommand cmd = new AdminCivCommand();	
		cmd.onCommand(sender, null, "civ", this.stripArgs(args, 1));
	}

	public void setfullmessage_cmd() {
		if (args.length < 2) {
			CivMessage.send(sender, CivSettings.localize.localizedString("Current")+CivGlobal.fullMessage);
			return;
		}
		
		synchronized(CivGlobal.maxPlayers) {
			CivGlobal.fullMessage = args[1];
		}
		
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("SetTo")+args[1]);
		
	}
	
	public void res_cmd() {
		AdminResCommand cmd = new AdminResCommand();	
		cmd.onCommand(sender, null, "war", this.stripArgs(args, 1));	}
	
	public void chat_cmd() {
		AdminChatCommand cmd = new AdminChatCommand();	
		cmd.onCommand(sender, null, "war", this.stripArgs(args, 1));
	}

	public void war_cmd() {
		AdminWarCommand cmd = new AdminWarCommand();	
		cmd.onCommand(sender, null, "war", this.stripArgs(args, 1));
	}
	
	public void lag_cmd() {
		AdminLagCommand cmd = new AdminLagCommand();	
		cmd.onCommand(sender, null, "war", this.stripArgs(args, 1));
	}
	
	public void build_cmd() {
		AdminBuildCommand cmd = new AdminBuildCommand();	
		cmd.onCommand(sender, null, "war", this.stripArgs(args, 1));
	}
	
	public void perm_cmd() throws CivException {
		Resident resident = getResident();
		
		if (resident.isPermOverride()) {
			resident.setPermOverride(false);
			CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("adcmd_permOff"));
			return;
		}
		
		resident.setPermOverride(true);
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("adcmd_permOn"));
		
	}
	
	public void sbperm_cmd() throws CivException {
		Resident resident = getResident();
		if (resident.isSBPermOverride()) {
			resident.setSBPermOverride(false);
			CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("adcmd_sbpermOff"));
			return;
		}
		
		resident.setSBPermOverride(true);
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("adcmd_sbpermOn"));
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
		
		if (sender instanceof Player) {
			if (((Player)sender).hasPermission(CivSettings.MINI_ADMIN)) {
				return;
			}
		}
		
		
		if (sender.isOp() == false) {
			throw new CivException(CivSettings.localize.localizedString("adcmd_NotAdmin"));			
		}
	}

	@Override
	public void doLogging() {
		CivLog.adminlog(sender.getName(), "/ad "+this.combineArgs(args));
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
