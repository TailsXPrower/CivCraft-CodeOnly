package ru.tailsxcraft.civcraft.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import ru.tailsxcraft.civcraft.components.AttributeBiomeRadiusPerLevel;
import ru.tailsxcraft.civcraft.components.ConsumeLevelComponent;
import ru.tailsxcraft.civcraft.components.ConsumeLevelComponent.Result;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigMineLevel;
import ru.tailsxcraft.civcraft.config.ConfigUniversityLevel;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.exception.CivTaskAbortException;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Buff;
import ru.tailsxcraft.civcraft.object.StructureChest;
import ru.tailsxcraft.civcraft.object.StructureSign;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.threading.CivAsyncTask;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.civcraft.util.MultiInventory;

public class University extends Structure {
    
	private ConsumeLevelComponent consumeComp = null;
	
	protected University(Location center, String id, Town town) throws CivException {
		super(center, id, town);
	}

	public University(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}

	@Override
	public void loadSettings() {
		super.loadSettings();

	}
	
	public String getkey() {
		return getTown().getName()+"_"+this.getConfigId()+"_"+this.getCorner().toString(); 
	}
	
	@Override
	public String getMarkerIconName() {
		return "bronzestar";
	}
	
	private StructureSign getSignFromSpecialId(int special_id) {
		for (StructureSign sign : getSigns()) {
			int id = Integer.valueOf(sign.getAction());
			if (id == special_id) {
				return sign;
			}
		}
		return null;
	}
	
	@Override
	public void updateSignText() {
		int count = 0;
		for (; count < getSigns().size(); count++) {
			StructureSign sign = getSignFromSpecialId(count);
			if (sign == null) {
				CivLog.error("University sign was null");
				return;
			}
			
			sign.setText("\n"+CivSettings.localize.localizedString("university_sign")+"\n"+
					this.getTown().getName());
			
			sign.update();
		}
	}
	
	@Override
	public void processSignAction(Player player, StructureSign sign, PlayerInteractEvent event) {
		CivMessage.send(player, CivColor.Green+CivSettings.localize.localizedString("university_sign")+" "+this.getTown().getName());
	}
	
	public ConsumeLevelComponent getConsumeComponent() {
		if (consumeComp == null) {
			consumeComp = (ConsumeLevelComponent) this.getComponent(ConsumeLevelComponent.class.getSimpleName());
		}
		return consumeComp;
	}
	
	public Result consume(CivAsyncTask task) throws InterruptedException {
		
		//Look for the mine's chest.
		if (this.getChests().size() == 0)
			return Result.STAGNATE;	

		MultiInventory multiInv = new MultiInventory();
		
		ArrayList<StructureChest> chests = this.getAllChestsById(0);
		
		// Make sure the chest is loaded and add it to the multi inv.
		for (StructureChest c : chests) {
			task.syncLoadChunk(c.getCoord().getWorldname(), c.getCoord().getX(), c.getCoord().getZ());
			Inventory tmp;
			try {
				tmp = task.getChestInventory(c.getCoord().getWorldname(), c.getCoord().getX(), c.getCoord().getY(), c.getCoord().getZ(), true);
			} catch (CivTaskAbortException e) {
				return Result.STAGNATE;
			}
			multiInv.addInventory(tmp);
		}
		getConsumeComponent().setSource(multiInv);
		getConsumeComponent().setConsumeRate(1.0);
		try {
			Result result = getConsumeComponent().processConsumption();
			getConsumeComponent().onSave();		
			return result;
		} catch (IllegalStateException e) {
			CivLog.exception(this.getDisplayName()+" Process Error in town: "+this.getTown().getName()+" and Location: "+this.getCorner(), e);
			return Result.STAGNATE;
		}
	}
	
	public void process_university(CivAsyncTask task) throws InterruptedException {
		Result result = null;
		try {
			result = this.consume(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		switch (result) {
		case STARVE:
			CivMessage.sendTown(getTown(), CivColor.Rose+"В Университете "+getConsumeComponent().getLevel()+" уровня упала продуктивность. "+CivColor.LightGreen+getConsumeComponent().getCountString());
			break;
		case LEVELDOWN:
			CivMessage.sendTown(getTown(), CivColor.Rose+"В Университете закончились книги и его уровень понизился. Текущий уровень "+getConsumeComponent().getLevel());
			break;
		case STAGNATE:
			CivMessage.sendTown(getTown(), CivColor.Rose+"Университет "+getConsumeComponent().getLevel()+" уровня простаивает. "+CivColor.LightGreen+getConsumeComponent().getCountString());
			break;
		case GROW:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"В Университете "+getConsumeComponent().getLevel()+" уровня выросла продуктивность. "+getConsumeComponent().getCountString());
			break;
		case LEVELUP:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"Университет повысил свой уровень. Текущий уровень "+getConsumeComponent().getLevel());
			break;
		case MAXED:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"Университет "+getConsumeComponent().getLevel()+" уровня достиг максимума. "+CivColor.LightGreen+getConsumeComponent().getCountString());
			break;
		default:
			break;
		}
	}
	
	public double getBonusBeakers() {
		if (!this.isComplete()) {
			return 0.0;
		}
		int level = getLevel();
		
		ConfigUniversityLevel lvl = CivSettings.universityLevels.get(level);
		return lvl.beakers;	
	}

	public int getLevel() {
		if (!this.isComplete()) {
			return 1;
		}
		return this.getConsumeComponent().getLevel();
	}
	
	public double getBeakersPerTile() {
		AttributeBiomeRadiusPerLevel attrBiome = (AttributeBiomeRadiusPerLevel)this.getComponent("AttributeBiomeRadiusPerLevel");
		double base = 1.0;
		
		if (attrBiome != null) {
			base = attrBiome.getBaseValue();
		}
	
		double rate = 1;
		rate += this.getTown().getBuffManager().getEffectiveDouble(Buff.ADVANCED_TOOLING);
		return (rate*base);
	}

	public int getCount() {
		return this.getConsumeComponent().getCount();
	}

	public int getMaxCount() {
		int level = getLevel();
		
		ConfigMineLevel lvl = CivSettings.mineLevels.get(level);
		return lvl.count;	
	}

	public Result getLastResult() {
		return this.getConsumeComponent().getLastResult();
	}


}
