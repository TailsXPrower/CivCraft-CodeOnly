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
package ru.tailsxcraft.civcraft.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ru.tailsxcraft.civcraft.components.NonMemberFeeComponent;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigGrocerLevel;
import ru.tailsxcraft.civcraft.config.ConfigUnit;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.loreguiinventories.LoreGrocerInventory;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiBuildInventory;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiInventory;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItem;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItemListener;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.StructureSign;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.sessiondb.SessionEntry;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.civcraft.util.ItemManager;
import ru.tailsxcraft.civcraft.util.SimpleBlock;

public class Grocer extends Structure {

	private int level = 1;

	private NonMemberFeeComponent nonMemberFeeComponent; 
	
	protected Grocer(Location center, String id, Town town) throws CivException {
		super(center, id, town);
		nonMemberFeeComponent = new NonMemberFeeComponent(this);
		nonMemberFeeComponent.onSave();
		setLevel(town.saved_grocer_levels);
	}

	public Grocer(ResultSet rs) throws SQLException, CivException {
		super(rs);
		nonMemberFeeComponent = new NonMemberFeeComponent(this);
		nonMemberFeeComponent.onLoad();
	}

	@Override
	public String getDynmapDescription() {
		String out = "<u><b>"+this.getDisplayName()+"</u></b><br/>";

		for (int i = 0; i < level; i++) {
			ConfigGrocerLevel grocerlevel = CivSettings.grocerLevels.get(i+1);
			out += "<b>"+grocerlevel.itemName+"</b> "+CivSettings.localize.localizedString("Amount")+" "+grocerlevel.amount+ " "+CivSettings.localize.localizedString("Price")+" "+grocerlevel.price+" "+CivSettings.CURRENCY_NAME+".<br/>";
		}
		
		return out;
	}
	
	@Override
	public String getMarkerIconName() {
		return "cutlery";
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public double getNonResidentFee() {
		return nonMemberFeeComponent.getFeeRate();
	}

	public void setNonResidentFee(double nonResidentFee) {
		this.nonMemberFeeComponent.setFeeRate(nonResidentFee);
	}
	
	public String getNonResidentFeeString() {
		return CivColor.Gold+"Пошлина: "+CivColor.White+((int)(getNonResidentFee()*100) + "%").toString();		
	}
	
	public void sign_buy_material(Player player, String itemName, Material id, int amount, double price) {
		Resident resident;
		int payToTown = (int) Math.round(price*this.getNonResidentFee());
		try {
				
				resident = CivGlobal.getResident(player.getName());
				Town t = resident.getTown();
			
				if (t == this.getTown()) {
					// Pay no taxes! You're a member.
					resident.buyItem(itemName, id, price, amount);
					CivMessage.send(player, CivColor.LightGreen + CivSettings.localize.localizedString("var_grocer_msgBought",amount,itemName,price+" "+CivSettings.CURRENCY_NAME));
					return;
				} else {
					// Pay non-resident taxes
					resident.buyItem(itemName, id, price + payToTown, amount);
					getTown().depositDirect(payToTown);
					CivMessage.send(player, CivColor.LightGreen + CivSettings.localize.localizedString("var_grocer_msgBought",amount,itemName,price,CivSettings.CURRENCY_NAME));
					CivMessage.send(player, CivColor.Yellow + CivSettings.localize.localizedString("var_grocer_msgPaidTaxes",this.getTown().getName(),payToTown+" "+CivSettings.CURRENCY_NAME));
				}
			
			}
			catch (CivException e) {
				CivMessage.send(player, CivColor.Rose + e.getMessage());
			}
		return;
	}

	@Override
	public void processSignAction(Player player, StructureSign sign, PlayerInteractEvent event) {
		/*
		int special_id = Integer.valueOf(sign.getAction());
		if (special_id < this.level) {
			ConfigGrocerLevel grocerlevel = CivSettings.grocerLevels.get(special_id+1);
			sign_buy_material(player, grocerlevel.itemName, grocerlevel.itemId, 
					(byte)grocerlevel.itemData, grocerlevel.amount, grocerlevel.price);
		} else {
			CivMessage.send(player, CivColor.Rose+CivSettings.localize.localizedString("grocer_sign_needUpgrade"));
		}*/
		
		switch (sign.getAction()) {
		case "grocer":
		    LoreGrocerInventory.openInventory(player, this);
			break;
		}
	}
	
	@Override
	public void onPostBuild(BlockCoord absCoord, SimpleBlock sb) {
		StructureSign structSign;

		switch (sb.command) {
		case "/grocer":
			ItemManager.setType(absCoord.getBlock(), sb.getType());
			ItemManager.setData(absCoord.getBlock(), sb.getData());

			structSign = new StructureSign(absCoord, this);
			structSign.setText(CivColor.LightGreen+"Нажмите"+CivColor.Black+", чтобы "+"\n"+" купить еду");
			structSign.setDirection((Directional)sb.getData());
			structSign.setAction("grocer");
			structSign.update();
			this.addStructureSign(structSign);
			CivGlobal.addStructureSign(structSign);
			
			break;
	    }
	}
}
