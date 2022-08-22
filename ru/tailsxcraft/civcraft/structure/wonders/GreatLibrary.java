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
package ru.tailsxcraft.civcraft.structure.wonders;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigEnchant;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.loreenhancements.LoreEnhancement;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiInventory;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.lorestorage.LoreMaterial;
import ru.tailsxcraft.civcraft.main.CivData;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.LibraryEnchantment;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.StructureSign;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.civcraft.util.ItemManager;
import ru.tailsxcraft.civcraft.util.SimpleBlock;

public class GreatLibrary extends Wonder {

	public GreatLibrary(Location center, String id, Town town)
			throws CivException {
		super(center, id, town);
	}

	public GreatLibrary(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}

	@Override
	public void onLoad() {
		if (this.isActive()) {
			addBuffs();
		}
	}
	
	@Override
	public void onComplete() {
		addBuffs();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		removeBuffs();
	}
	
	@Override
	protected void removeBuffs() {
		this.removeBuffFromCiv(this.getCiv(), "buff_greatlibrary_extra_beakers");
		this.removeBuffFromTown(this.getTown(), "buff_greatlibrary_double_tax_beakers");
	}

	@Override
	protected void addBuffs() {
		this.addBuffToCiv(this.getCiv(), "buff_greatlibrary_extra_beakers");
		this.addBuffToTown(this.getTown(), "buff_greatlibrary_double_tax_beakers");
	}
	
	
	@Override
	public void updateSignText() {
		
		for (StructureSign sign : getSigns()) {
			ConfigEnchant enchant;
			switch (sign.getAction().toLowerCase()) {
			case "0":
				enchant = CivSettings.enchants.get("ench_fire_aspect");
				sign.setText(enchant.name+"\n\n"+CivColor.LightGreen+enchant.cost+" "+CivSettings.CURRENCY_NAME);
				break;
			case "1":
				enchant = CivSettings.enchants.get("ench_fire_protection");
				sign.setText(enchant.name+"\n\n"+CivColor.LightGreen+enchant.cost+" "+CivSettings.CURRENCY_NAME);
				break;
			case "2":
				enchant = CivSettings.enchants.get("ench_flame");
				sign.setText(enchant.name+"\n\n"+CivColor.LightGreen+enchant.cost+" "+CivSettings.CURRENCY_NAME);				
				break;			
			case "3":
				enchant = CivSettings.enchants.get("ench_punchout");
				sign.setText(enchant.name+"\n\n"+CivColor.LightGreen+enchant.cost+" "+CivSettings.CURRENCY_NAME);
				break;
			}
			
			sign.update();
		}
	}
	
	@Override
	public void processSignAction(Player player, StructureSign sign, PlayerInteractEvent event) {
		//int special_id = Integer.valueOf(sign.getAction());
		Resident resident = CivGlobal.getResident(player);
		
		if (resident == null) {
			return;
		}
		
		if (!resident.hasTown() || resident.getCiv() != this.getCiv()) {
			CivMessage.sendError(player, CivSettings.localize.localizedString("var_greatLibrary_nonMember",this.getCiv().getName()));
			return;
		}
		
		//ItemStack hand = player.getInventory().getItemInMainHand();
		//ConfigEnchant configEnchant;
		
		switch (sign.getAction()) {
		case "glibrary":
			Inventory libraryInv = LoreGuiInventory.createGuiInventory(player, 18, "Александрийская Библиотека");
			player.openInventory(libraryInv);
			
			ItemStack libraryInf = LoreGuiItem.build(CivColor.LightGreen+"Александрийская Библиотека", Material.PAPER);
			libraryInv.setItem(8, libraryInf);
			
			Integer count = 0;
			
			for (Entry<String, ConfigEnchant> mat : CivSettings.enchants.entrySet()) {
				ConfigEnchant cfg = mat.getValue();
				ItemStack item = LoreGuiItem.build(CivColor.Yellow+cfg.name, Material.ENCHANTED_BOOK, CivColor.Gold+"Стоимость: "+CivColor.White+cfg.cost);
				item = LoreGuiItem.setAction(item, "EnchantItemsGreatLibrary");
				item = LoreGuiItem.setActionData(item, "enchantId", mat.getKey());
				libraryInv.setItem(count, item);
				count++;
			}
			for ( int i = 0; i < 8; i++ ) {
				if ( libraryInv.getItem(i) != null ) continue;
					
				ItemStack item = LoreGuiItem.build(CivColor.Red+"Пусто", Material.RED_STAINED_GLASS_PANE);
				
				libraryInv.setItem(i, item);
			}
			break;
		/*
		case "0": /* fire aspect 
			if (!Enchantment.FIRE_ASPECT.canEnchantItem(hand)) {
				CivMessage.sendError(player, CivSettings.localize.localizedString("library_enchant_cannotEnchant"));
				return;
			}
			
			configEnchant = CivSettings.enchants.get("ench_fire_aspect");
			if (!resident.getTreasury().hasEnough(configEnchant.cost)) {
				CivMessage.send(player, CivColor.Rose+CivSettings.localize.localizedString("var_library_enchant_cannotAfford",configEnchant.cost,CivSettings.CURRENCY_NAME));
				return;
			}
			
			resident.getTreasury().withdraw(configEnchant.cost);
			hand.addEnchantment(Enchantment.FIRE_ASPECT, 2);			
			break;
		case "1": /* fire protection 
			if (!Enchantment.PROTECTION_FIRE.canEnchantItem(hand)) {
				CivMessage.sendError(player, CivSettings.localize.localizedString("library_enchant_cannotEnchant"));
				return;	
			}
			
			configEnchant = CivSettings.enchants.get("ench_fire_protection");
			if (!resident.getTreasury().hasEnough(configEnchant.cost)) {
				CivMessage.send(player, CivColor.Rose+CivSettings.localize.localizedString("var_library_enchant_cannotAfford",configEnchant.cost,CivSettings.CURRENCY_NAME));
				return;
			}
			
			resident.getTreasury().withdraw(configEnchant.cost);
			hand.addEnchantment(Enchantment.PROTECTION_FIRE, 3);			
			break;
		case "2": /* flame 
			if (!Enchantment.ARROW_FIRE.canEnchantItem(hand)) {
				CivMessage.sendError(player, CivSettings.localize.localizedString("library_enchant_cannotEnchant"));
				return;	
			}
			
			configEnchant = CivSettings.enchants.get("ench_flame");
			if (!resident.getTreasury().hasEnough(configEnchant.cost)) {
				CivMessage.send(player, CivColor.Rose+CivSettings.localize.localizedString("var_library_enchant_cannotAfford",configEnchant.cost,CivSettings.CURRENCY_NAME));
				return;
			}
			
			resident.getTreasury().withdraw(configEnchant.cost);
			hand.addEnchantment(Enchantment.ARROW_FIRE, 1);	
			break;
		case "3":
			switch (ItemManager.getType(hand)) {
			case WOODEN_PICKAXE:
			case STONE_PICKAXE:
			case IRON_PICKAXE:
			case DIAMOND_PICKAXE:
			case GOLDEN_PICKAXE:
				configEnchant = CivSettings.enchants.get("ench_punchout");
				
				if (!LoreMaterial.isCustom(hand)) {					
					CivMessage.sendError(player, CivSettings.localize.localizedString("library_enchant_nonEnchantable"));
					return;
				}
				
				if (LoreMaterial.hasEnhancement(hand, configEnchant.enchant_id)) {
					CivMessage.sendError(player, CivSettings.localize.localizedString("library_enchant_hasEnchantment"));
					return;
				}
				
				if (!resident.getTreasury().hasEnough(configEnchant.cost)) {
					CivMessage.send(player, CivColor.Rose+CivSettings.localize.localizedString("var_library_enchant_cannotAfford",configEnchant.cost,CivSettings.CURRENCY_NAME));
					return;
				}
				
				resident.getTreasury().withdraw(configEnchant.cost);
				ItemStack newItem = LoreMaterial.addEnhancement(hand, LoreEnhancement.enhancements.get(configEnchant.enchant_id));				
				player.getInventory().setItemInMainHand(newItem);
				break;
			default:
				CivMessage.sendError(player, CivSettings.localize.localizedString("library_enchant_cannotEnchant"));
				return;	
			}
			break;
		default:
			return;
			*/
		}
		
		//CivMessage.sendSuccess(player, CivSettings.localize.localizedString("library_enchantment_success"));
	}
	
	@Override
	public void onPostBuild(BlockCoord absCoord, SimpleBlock commandBlock) {
		StructureSign structSign;
		if (commandBlock.command.equals("/glibrary")) {
			ItemManager.setType(absCoord.getBlock(), commandBlock.getType());
			ItemManager.setData(absCoord.getBlock(), commandBlock.getData());
			structSign = new StructureSign(absCoord, this);
			structSign.setText(CivColor.Green+"Александрийская"+"\n"+CivColor.Green+"Библиотека"+"\n"+CivColor.Black+"Нажмите, чтобы"+"\n"+"зачаровать");
			if ( commandBlock.getType() == Material.OAK_SIGN ) {
				structSign.setRotation((Rotatable)commandBlock.getData());
			} else {
				structSign.setDirection((Directional)commandBlock.getData());
			}
			structSign.setAction("glibrary");
			structSign.update();
			this.addStructureSign(structSign);
			CivGlobal.addStructureSign(structSign);
		} 
	}

}
