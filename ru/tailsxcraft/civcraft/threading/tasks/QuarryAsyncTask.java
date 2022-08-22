package ru.tailsxcraft.civcraft.threading.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ru.tailsxcraft.civcraft.exception.CivTaskAbortException;
import ru.tailsxcraft.civcraft.lorestorage.LoreMaterial;
import ru.tailsxcraft.civcraft.main.CivData;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.object.StructureChest;
import ru.tailsxcraft.civcraft.structure.Quarry;
import ru.tailsxcraft.civcraft.structure.Structure;
import ru.tailsxcraft.civcraft.structure.Quarry.Mineral;
import ru.tailsxcraft.civcraft.threading.CivAsyncTask;
import ru.tailsxcraft.civcraft.threading.sync.request.UpdateInventoryRequest.Action;
import ru.tailsxcraft.civcraft.util.ItemManager;
import ru.tailsxcraft.civcraft.util.MultiInventory;

public class QuarryAsyncTask extends CivAsyncTask {

	Quarry quarry;
	
	public static HashSet<String> debugTowns = new HashSet<String>();

	public static void debug(Quarry quarry, String msg) {
		if (debugTowns.contains(quarry.getTown().getName())) {
			CivLog.warning("QuarryDebug:"+quarry.getTown().getName()+":"+msg);
		}
	}	
	
	public QuarryAsyncTask(Structure quarry) {
		this.quarry = (Quarry)quarry;
	}
	
//	private Boolean hasSilkTouch(ItemStack stack) {
//
//		if (stack.hasItemMeta()) {
//			ItemMeta testEnchantMeta = stack.getItemMeta();
//			if (testEnchantMeta.hasEnchant(Enchantment.SILK_TOUCH)) {
//
//				debug(quarry, "Pickaxe has SILK_TOUCH");
//				return true;
//				
//			}
//		}
//		return false;
//	}
	
	private int checkDigSpeed(ItemStack stack) {

		if (stack.hasItemMeta()) {
			ItemMeta testEnchantMeta = stack.getItemMeta();
			if (testEnchantMeta.hasEnchant(Enchantment.DIG_SPEED)) {

				debug(quarry, "Pickaxe has DIG_SPEED lvl: "+testEnchantMeta.getEnchantLevel(Enchantment.DIG_SPEED));
				return testEnchantMeta.getEnchantLevel(Enchantment.DIG_SPEED)+1;
				
			}
		}
		return 1;
	}
	
	public void processQuarryUpdate() {
		if (!quarry.isActive()) {
			debug(quarry, "quarry inactive...");
			return;
		}
		
		debug(quarry, "Processing Quarry...");
		// Grab each CivChest object we'll require.
		ArrayList<StructureChest> sources = quarry.getAllChestsById(0);
		ArrayList<StructureChest> destinations = quarry.getAllChestsById(1);
		
		if (sources.size() != 2 || destinations.size() != 2) {
			CivLog.error("Bad chests for quarry in town:"+quarry.getTown().getName()+" sources:"+sources.size()+" dests:"+destinations.size());
			return;
		}
		
		// Make sure the chunk is loaded before continuing. Also, add get chest and add it to inventory.
		MultiInventory source_inv = new MultiInventory();
		MultiInventory dest_inv = new MultiInventory();

		try {
			for (StructureChest src : sources) {
				//this.syncLoadChunk(src.getCoord().getWorldname(), src.getCoord().getX(), src.getCoord().getZ());				
				Inventory tmp;
				try {
					tmp = this.getChestInventory(src.getCoord().getWorldname(), src.getCoord().getX(), src.getCoord().getY(), src.getCoord().getZ(), false);
				} catch (CivTaskAbortException e) {
					//e.printStackTrace();
					CivLog.warning("Quarry:"+e.getMessage());
					return;
				}
				if (tmp == null) {
					quarry.skippedCounter++;
					return;
				}
				source_inv.addInventory(tmp);
			}
			
			boolean full = true;
			for (StructureChest dst : destinations) {
				//this.syncLoadChunk(dst.getCoord().getWorldname(), dst.getCoord().getX(), dst.getCoord().getZ());
				Inventory tmp;
				try {
					tmp = this.getChestInventory(dst.getCoord().getWorldname(), dst.getCoord().getX(), dst.getCoord().getY(), dst.getCoord().getZ(), false);
				} catch (CivTaskAbortException e) {
					//e.printStackTrace();
					CivLog.warning("Quarry:"+e.getMessage());
					return;
				}
				if (tmp == null) {
					quarry.skippedCounter++;
					return;
				}
				dest_inv.addInventory(tmp);
				
				for (ItemStack stack : tmp.getContents()) {
					if (stack == null) {
						full = false;
						break;
					}
				}
			}
			
			if (full) {
				/* Quarry destination chest is full, stop processing. */
				return;
			}
			
		} catch (InterruptedException e) {
			return;
		}

		debug(quarry, "Processing quarry:"+quarry.skippedCounter+1);
		ItemStack[] contents = source_inv.getContents();
		for (int i = 0; i < quarry.skippedCounter+1; i++) {
		
			for(ItemStack stack : contents) {
				if (stack == null) {
					continue;
				}
				int modifier = checkDigSpeed(stack);
				
				if (ItemManager.getType(stack) == CivData.WOOD_PICKAXE) {
					try {
						if ( quarry.currentItem == null ) {
							short damage = ItemManager.getData(stack);
							this.updateInventory(Action.REMOVE, source_inv, stack);
							damage+= modifier;
							stack.setDurability(damage);
							quarry.currentItem = stack;
						} else {
							short damage = ItemManager.getData(quarry.currentItem);
							damage+= modifier;
							quarry.currentItem.setDurability(damage);
							if (damage > 59 || quarry.currentItem.getAmount() != 1) {
								quarry.currentItem = null;
							}
						}
					} catch (InterruptedException e) {
						return;
					}
					
					// Attempt to get special resources
					Random rand = new Random();
					int randMax = Quarry.MAX_CHANCE;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					
					if (rand1 < ((int)((quarry.getChance(Mineral.COAL)/2)*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COAL, modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.OTHER)/2)*randMax))) {
						newItem = getOther(modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.COBBLESTONE)/2)*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, modifier);
					} else {
						newItem = getJunk(modifier);
					}
					
					//Try to add the new item to the dest chest, if we cant, oh well.
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
				if (this.quarry.getLevel() >= 2 && ItemManager.getType(stack) == CivData.STONE_PICKAXE) {
					try {
						if ( quarry.currentItem == null ) {
							short damage = ItemManager.getData(stack);
							this.updateInventory(Action.REMOVE, source_inv, stack);
							damage+= modifier;
							stack.setDurability(damage);
							quarry.currentItem = stack;
						} else {
							short damage = ItemManager.getData(quarry.currentItem);
							damage+= modifier;
							quarry.currentItem.setDurability(damage);
							if (damage > 131 || quarry.currentItem.getAmount() != 1) {
								quarry.currentItem = null;
							}
						}
					} catch (InterruptedException e) {
						return;
					}
					
					// Attempt to get special resources
					Random rand = new Random();
					int randMax = Quarry.MAX_CHANCE;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					
					if (rand1 < ((int)((quarry.getChance(Mineral.GOLD))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.IRON))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.IRON_INGOT, modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.COAL))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COAL, modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.OTHER))*randMax))) {
						newItem = getOther(modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.COBBLESTONE)/2)*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, modifier);
					} else {
						newItem = getJunk(modifier);
					}
					
					//Try to add the new item to the dest chest, if we cant, oh well.
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
				if (this.quarry.getLevel() >= 3 && ItemManager.getType(stack) == CivData.IRON_PICKAXE) {
					try {
						if ( quarry.currentItem == null ) {
							short damage = ItemManager.getData(stack);
							this.updateInventory(Action.REMOVE, source_inv, stack);
							damage+= modifier;
							stack.setDurability(damage);
							quarry.currentItem = stack;
						} else {
							short damage = ItemManager.getData(quarry.currentItem);
							damage+= modifier;
							quarry.currentItem.setDurability(damage);
							if (damage > 250 || quarry.currentItem.getAmount() != 1) {
								quarry.currentItem = null;
							}
						}
					} catch (InterruptedException e) {
						return;
					}
					
					// Attempt to get special resources
					Random rand = new Random();
					int randMax = Quarry.MAX_CHANCE;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					
					if (rand1 < ((int)((quarry.getChance(Mineral.RARE))*randMax))) {
						newItem = getRare(modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.TUNGSTEN))*randMax))) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_tungsten_ore"), modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.GOLD))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.REDSTONE))*randMax))) {
						int itemRand = rand.nextInt(5)+1;
						newItem = ItemManager.createItemStack(CivData.REDSTONE_DUST, itemRand*modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.IRON))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.IRON_INGOT, modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.COAL))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COAL, modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.OTHER))*randMax))) {
						newItem = getOther(modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.COBBLESTONE)/2)*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, modifier);
					} else {
						newItem = getJunk(modifier);
					}
					
					//Try to add the new item to the dest chest, if we cant, oh well.
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
				if (ItemManager.getType(stack) == CivData.GOLD_PICKAXE) {
					try {
						if ( quarry.currentItem == null ) {
							short damage = ItemManager.getData(stack);
							this.updateInventory(Action.REMOVE, source_inv, stack);
							damage+= modifier;
							stack.setDurability(damage);
							quarry.currentItem = stack;
						} else {
							short damage = ItemManager.getData(quarry.currentItem);
							damage+= modifier;
							quarry.currentItem.setDurability(damage);
							if (damage > 32 || quarry.currentItem.getAmount() != 1) {
								quarry.currentItem = null;
							}
						}
					} catch (InterruptedException e) {
						return;
					}
					
					// Attempt to get special resources
					Random rand = new Random();
					int randMax = Quarry.MAX_CHANCE;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					
					if (rand1 < ((int)((quarry.getChance(Mineral.COAL)/2)*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COAL, modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.OTHER)/2)*randMax))) {
						newItem = getOther(modifier);
					} else {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, modifier);
					}
					
					//Try to add the new item to the dest chest, if we cant, oh well.
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
				if (this.quarry.getLevel() >= 4 && ItemManager.getType(stack) == CivData.DIAMOND_PICKAXE) {
					try {
						if ( quarry.currentItem == null ) {
							short damage = ItemManager.getData(stack);
							this.updateInventory(Action.REMOVE, source_inv, stack);
							damage+= modifier;
							stack.setDurability(damage);
							quarry.currentItem = stack;
						} else {
							short damage = ItemManager.getData(quarry.currentItem);
							damage+= modifier;
							quarry.currentItem.setDurability(damage);
							if (damage > 1561 || quarry.currentItem.getAmount() != 1) {
								quarry.currentItem = null;
							}
						}
					} catch (InterruptedException e) {
						return;
					}
					
					// Attempt to get special resources
					Random rand = new Random();
					int randMax = Quarry.MAX_CHANCE;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					
					if (rand1 < ((int)((quarry.getChance(Mineral.RARE))*randMax))) {
						newItem = getRare(modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.TUNGSTEN))*randMax))) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_tungsten_ore"), modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.GOLD))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.REDSTONE))*randMax))) {
						int itemRand = rand.nextInt(5)+1;
						newItem = ItemManager.createItemStack(CivData.REDSTONE_DUST, itemRand*modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.IRON))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.IRON_INGOT, modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.COAL))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COAL, modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.OTHER))*randMax))) {
						newItem = getOther(modifier);
					} else if (rand1 < ((int)((quarry.getChance(Mineral.COBBLESTONE)/2)*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, modifier);
					} else {
						newItem = getJunk(modifier);
					}
					
					//Try to add the new item to the dest chest, if we cant, oh well.
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
		}	
	quarry.skippedCounter = 0;
	}
	
	@SuppressWarnings("deprecation")
	private ItemStack getJunk(int modifier) {
		int randMax = 10;
		Random rand = new Random();
		int rand2 = rand.nextInt(randMax);
		if (rand2 < (2)) {
			return ItemManager.createItemStack(CivData.DIRT, modifier);
		} else if (rand2 < (5)) {
			return ItemManager.createItemStack(CivData.DIRT, modifier);
		} else {
			return ItemManager.createItemStack(CivData.DIRT, modifier);
		}
	}
	
	@SuppressWarnings("deprecation")
	private ItemStack getOther(int modifier) {
		int randMax = Quarry.MAX_CHANCE;
		Random rand = new Random();
		int rand2 = rand.nextInt(randMax);
		if (rand2 < (randMax/8)) {
			return ItemManager.createItemStack(CivData.STONE, modifier);
		} else if (rand2 < (randMax/5)) {
			return ItemManager.createItemStack(CivData.STONE, modifier);
		} else {
			return ItemManager.createItemStack(CivData.STONE, modifier);
		}
	}
	
	private ItemStack getRare(int modifier) {
		int randMax = Quarry.MAX_CHANCE;
		Random rand = new Random();
		int rand2 = rand.nextInt(randMax);
		if (rand2 < (randMax/5)) {
			return ItemManager.createItemStack(CivData.EMERALD, modifier);
		} else {
			return ItemManager.createItemStack(CivData.DIAMOND, modifier);
		}
	}
	
	
	
	@Override
	public void run() {
		if (this.quarry.lock.tryLock()) {
			try {
				try {
					processQuarryUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} finally {
				this.quarry.lock.unlock();
			}
		} else {
			debug(this.quarry, "Failed to get lock while trying to start task, aborting.");
		}
	}

}
