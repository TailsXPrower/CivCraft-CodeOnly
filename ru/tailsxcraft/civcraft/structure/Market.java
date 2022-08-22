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
import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigMarketItem;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.StructureSign;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.civcraft.util.ItemManager;
import ru.tailsxcraft.civcraft.util.SimpleBlock;

public class Market extends Structure {

	public HashMap<Integer, LinkedList<StructureSign>> signIndex = new HashMap<Integer, LinkedList<StructureSign>>();
	
	public static int BULK_AMOUNT = 64;
		
	protected Market(Location center, String id, Town town) throws CivException {
		super(center, id, town);
		CivGlobal.addMarket(this);
	}

	public Market(ResultSet rs) throws SQLException, CivException {
		super(rs);
		CivGlobal.addMarket(this);
	}
	
	@Override
	public void delete() throws SQLException {
		super.delete();
		CivGlobal.removeMarket(this);
	}
	
	public static void globalSignUpdate(int id) {
		for (Market market : CivGlobal.getMarkets()) {
			
			LinkedList<StructureSign> signs = market.signIndex.get(id);
			if (signs == null) {
				continue;
			}
			
			for (StructureSign sign : signs) {			
				ConfigMarketItem item = CivSettings.marketItems.get(id);
				if (item != null) {
					try {
					market.setSignText(sign, item);
					} catch (ClassCastException e) {
						CivLog.error("Can't cast structure sign to sign for market update. "+sign.getCoord().getX()+" "+sign.getCoord().getY()+" "+sign.getCoord().getZ());
						continue;
					}
				}
			}
		}
	}
	
	public void processBuy(Player player, Resident resident, int bulkCount, ConfigMarketItem item) throws CivException {
		item.buy(resident, player, bulkCount);
	}
	
	public void processSell(Player player, Resident resident, int bulkCount, ConfigMarketItem item) throws CivException {
		item.sell(resident, player, bulkCount);
	}
	
	@Override
	public void processSignAction(Player player, StructureSign sign, PlayerInteractEvent event) throws CivException {
		
		Integer id = Integer.valueOf(sign.getType());
		ConfigMarketItem item = CivSettings.marketItems.get(id);
		Resident resident = CivGlobal.getResident(player);

		if (resident == null) {
			CivMessage.sendError(player, CivSettings.localize.localizedString("market_invalidPlayer"));
			return;
		}
		
		if (item == null) {
			CivMessage.sendError(player, CivSettings.localize.localizedString("market_invalidID")+id);
			return;
		}
		
		switch (sign.getAction().toLowerCase()) {
		case "sellbig":
			processSell(player, resident, BULK_AMOUNT, item);
			break;
		case "sell":
			processSell(player, resident, 1, item);
			break;
		case "buy":
			processBuy(player, resident, 1, item);
			break;
		case "buybig":
			processBuy(player, resident, BULK_AMOUNT, item);
			break;
		}
	
		player.updateInventory();
		Market.globalSignUpdate(id);
	}
	
	public void setSignText(StructureSign sign, ConfigMarketItem item) throws ClassCastException {

		String itemColor;
		switch (item.lastaction) {
		case BUY:
			itemColor = CivColor.LightGreen;
			break;
		case SELL:
			itemColor = CivColor.Rose;
			break;
		default:
			itemColor = CivColor.Black;
			break;
		}
		
		try {
		Sign s;
		switch (sign.getAction().toLowerCase()) {
		case "sellbig":
			if (sign.getCoord().getBlock().getState() instanceof Sign) {
			s = (Sign)sign.getCoord().getBlock().getState();
			s.setLine(0, ChatColor.RED+CivSettings.localize.localizedString("market_sign_sellBulk"));
			if ( item.name.length() > 15 ) {
				String[] split = item.name.split(" ");
				s.setLine(1, split[0]);
				s.setLine(2, split[1]);
			} else {
				s.setLine(1, item.name);
			}
			s.setLine(3, CivColor.Gold+item.getSellCostForAmount(BULK_AMOUNT)+" "+CivSettings.CURRENCY_NAME);
			s.update();
			}
			break;
		case "sell":
			if (sign.getCoord().getBlock().getState() instanceof Sign) {
			s = (Sign)sign.getCoord().getBlock().getState();
			s.setLine(0, ChatColor.RED+CivSettings.localize.localizedString("market_sign_sell"));
			if ( item.name.length() > 15 ) {
				String[] split = item.name.split(" ");
				s.setLine(1, split[0]);
				s.setLine(2, split[1]);
			} else {
				s.setLine(1, item.name);
			}
			s.setLine(3, CivColor.Gold+item.getSellCostForAmount(1)+" "+CivSettings.CURRENCY_NAME);
			s.update();
			}
			break;
		case "buy":
			if (sign.getCoord().getBlock().getState() instanceof Sign) {
			s = (Sign)sign.getCoord().getBlock().getState();
			s.setLine(0, ChatColor.GREEN+CivSettings.localize.localizedString("market_sign_buy"));
			if ( item.name.length() > 15 ) {
				String[] split = item.name.split(" ");
				s.setLine(1, split[0]);
				s.setLine(2, split[1]);
			} else {
				s.setLine(1, item.name);
			}
			s.setLine(3, CivColor.Gold+item.getBuyCostForAmount(1)+" "+CivSettings.CURRENCY_NAME);
			s.update();
			}
			break;
		case "buybig":
			if (sign.getCoord().getBlock().getState() instanceof Sign) {
			s = (Sign)sign.getCoord().getBlock().getState();
			s.setLine(0, ChatColor.GREEN+CivSettings.localize.localizedString("market_sign_buyBulk"));
			if ( item.name.length() > 15 ) {
				String[] split = item.name.split(" ");
				s.setLine(1, split[0]);
				s.setLine(2, split[1]);
			} else {
				s.setLine(1, item.name);
			}
			s.setLine(3, CivColor.Gold+item.getBuyCostForAmount(BULK_AMOUNT)+" "+CivSettings.CURRENCY_NAME);
			s.update();
			}
			break;
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void buildSign(String action, Integer id, BlockCoord absCoord, ConfigMarketItem item, SimpleBlock commandBlock) {
		Block b = absCoord.getBlock();
		
		ItemManager.setTypeIdAndData(b, ItemManager.getType(Material.OAK_SIGN), commandBlock.getData(), false);
		
		StructureSign structSign = CivGlobal.getStructureSign(absCoord);
		if (structSign == null) {
			structSign = new StructureSign(absCoord, this);
		}
		
		structSign.setDirection((Directional)ItemManager.getData(b.getState()));
		structSign.setType(""+id);
		structSign.setAction(action);

		structSign.setOwner(this);
		this.addStructureSign(structSign);
		CivGlobal.addStructureSign(structSign);
		
		LinkedList<StructureSign> signs = this.signIndex.get(id);
		if (signs == null) {
			signs = new LinkedList<StructureSign>();
		}
	
		signs.add(structSign);
		this.signIndex.put(id, signs);
		this.setSignText(structSign, item);
	}
	
	@Override
	public void onPostBuild(BlockCoord absCoord, SimpleBlock commandBlock) {
		Integer id;
		ConfigMarketItem item;
		switch (commandBlock.command.toLowerCase().trim()) {
		case "/sellbig":
			id = Integer.valueOf(commandBlock.keyvalues.get("id"));
			item = CivSettings.marketItems.get(id);
			if (item != null) {
				if (item.isStackable()) {
					buildSign("sellbig", id, absCoord, item, commandBlock);
				}
			}
			break;
		case "/sell":
			id = Integer.valueOf(commandBlock.keyvalues.get("id"));
			
			item = CivSettings.marketItems.get(id);
			if (item != null) {
				buildSign("sell", id, absCoord, item, commandBlock);
			}		
			break;
		case "/buy":
			id = Integer.valueOf(commandBlock.keyvalues.get("id"));
			item = CivSettings.marketItems.get(id);
			if (item != null) {
				buildSign("buy", id, absCoord, item, commandBlock);
			}		
			break;
		case "/buybig":
			id = Integer.valueOf(commandBlock.keyvalues.get("id"));
			item = CivSettings.marketItems.get(id);
			if (item != null) {
				if (item.isStackable()) {
					buildSign("buybig", id, absCoord, item, commandBlock);
				}
			}		
			break;
		}
	}

	

	
	

	
}
