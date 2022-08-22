package ru.tailsxcraft.civcraft.threading.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.block.Biome;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ru.tailsxcraft.civcraft.exception.CivTaskAbortException;
import ru.tailsxcraft.civcraft.lorestorage.LoreMaterial;
import ru.tailsxcraft.civcraft.main.CivData;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.object.StructureChest;
import ru.tailsxcraft.civcraft.structure.FishHatchery;
import ru.tailsxcraft.civcraft.structure.Structure;
import ru.tailsxcraft.civcraft.threading.CivAsyncTask;
import ru.tailsxcraft.civcraft.threading.sync.request.UpdateInventoryRequest.Action;
import ru.tailsxcraft.civcraft.util.ItemManager;
import ru.tailsxcraft.civcraft.util.MultiInventory;

public class FisheryAsyncTask extends CivAsyncTask {

	FishHatchery fishHatchery;
	
	public static HashSet<String> debugTowns = new HashSet<String>();

	public static void debug(FishHatchery fishHatchery, String msg) {
		if (debugTowns.contains(fishHatchery.getTown().getName())) {
			CivLog.warning("FishHatcheryDebug:"+fishHatchery.getTown().getName()+":"+msg);
		}
	}	
	
	public FisheryAsyncTask(Structure fishHatchery) {
		this.fishHatchery = (FishHatchery)fishHatchery;
	}
	
	public void processFisheryUpdate() {
		if (!fishHatchery.isActive()) {
			debug(fishHatchery, "Fish Hatchery inactive...");
			return;
		}
		
		debug(fishHatchery, "Processing Fish Hatchery...");
		// Grab each CivChest object we'll require.
		ArrayList<StructureChest> sources = fishHatchery.getAllChestsById(0);
		sources.addAll(fishHatchery.getAllChestsById(1));
		sources.addAll(fishHatchery.getAllChestsById(2));
		sources.addAll(fishHatchery.getAllChestsById(3));
		ArrayList<StructureChest> destinations = fishHatchery.getAllChestsById(4);
		
		if (sources.size() != 4 || destinations.size() != 2) {
			CivLog.error("Bad chests for fish hatchery in town:"+fishHatchery.getTown().getName()+" sources:"+sources.size()+" dests:"+destinations.size());
			return;
		}
		
		// Make sure the chunk is loaded before continuing. Also, add get chest and add it to inventory.
		MultiInventory source_inv0 = new MultiInventory();
		MultiInventory source_inv1 = new MultiInventory();
		MultiInventory source_inv2 = new MultiInventory();
		MultiInventory source_inv3 = new MultiInventory();
		MultiInventory dest_inv = new MultiInventory();

		try {
			for (StructureChest src : sources) {
				//this.syncLoadChunk(src.getCoord().getWorldname(), src.getCoord().getX(), src.getCoord().getZ());				
				Inventory tmp;
				try {
					tmp = this.getChestInventory(src.getCoord().getWorldname(), src.getCoord().getX(), src.getCoord().getY(), src.getCoord().getZ(), false);
				} catch (CivTaskAbortException e) {
					//e.printStackTrace();
					CivLog.warning("Fish Hatchery:"+e.getMessage());
					return;
				}
				if (tmp == null) {
					fishHatchery.skippedCounter++;
					return;
				}
				switch(src.getChestId()){
				case 0: source_inv0.addInventory(tmp);
			 	break;
				case 1: source_inv1.addInventory(tmp);
			 	break;
				case 2: source_inv2.addInventory(tmp);
			 	break;
				case 3: source_inv3.addInventory(tmp);
			 	break;
				}
			}
			
			boolean full = true;
			for (StructureChest dst : destinations) {
				//this.syncLoadChunk(dst.getCoord().getWorldname(), dst.getCoord().getX(), dst.getCoord().getZ());
				Inventory tmp;
				try {
					tmp = this.getChestInventory(dst.getCoord().getWorldname(), dst.getCoord().getX(), dst.getCoord().getY(), dst.getCoord().getZ(), false);
				} catch (CivTaskAbortException e) {
					//e.printStackTrace();
					CivLog.warning("Fish Hatchery:"+e.getMessage());
					return;
				}
				if (tmp == null) {
					fishHatchery.skippedCounter++;
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

		debug(fishHatchery, "Processing Fish Hatchery:"+fishHatchery.skippedCounter+1);
		ItemStack[] contents0 = source_inv0.getContents();
		ItemStack[] contents1 = source_inv1.getContents();
		ItemStack[] contents2 = source_inv2.getContents();
		ItemStack[] contents3 = source_inv3.getContents();
		for (int i = 0; i < fishHatchery.skippedCounter+1; i++) {
		
			for(ItemStack stack : contents0) {
				if (stack == null) {
					continue;
				}
				
				if (ItemManager.getType(stack) == CivData.FISHING_ROD) {
					try {
						if ( fishHatchery.currentItem1 == null ) {
							short damage = ItemManager.getData(stack);
							this.updateInventory(Action.REMOVE, source_inv0, stack);
							damage++;
							stack.setDurability(damage);
							if (damage < 64 && stack.getAmount() == 1) {
								fishHatchery.currentItem1 = stack;
							} 
						} else {
							short damage = ItemManager.getData(fishHatchery.currentItem1);
							damage++;
							fishHatchery.currentItem1.setDurability(damage);
							if (damage > 64 || fishHatchery.currentItem1.getAmount() != 1) {
								fishHatchery.currentItem1 = null;
							}
						}
					} catch (InterruptedException e) {
						return;
					}
					
					ItemStack newItem;
					
					newItem = this.getFishForBiome();
					
					//Try to add the new item to the dest chest, if we cant, oh well.
					try {
						debug(fishHatchery, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			
			if (this.fishHatchery.getLevel() >= 2)
			{
				for(ItemStack stack : contents1) {
					if (stack == null) {
						continue;
					}
					
					if (ItemManager.getType(stack) == CivData.FISHING_ROD) {
						try {
							if ( fishHatchery.currentItem2 == null ) {
								short damage = ItemManager.getData(stack);
								this.updateInventory(Action.REMOVE, source_inv1, stack);
								damage++;
								stack.setDurability(damage);
								if (damage < 64 && stack.getAmount() == 1) {
									fishHatchery.currentItem2 = stack;
								} 
							} else {
								short damage = ItemManager.getData(fishHatchery.currentItem2);
								damage++;
								fishHatchery.currentItem2.setDurability(damage);
								if (damage > 64 || fishHatchery.currentItem2.getAmount() != 1) {
									fishHatchery.currentItem2 = null;
								}
							}
						} catch (InterruptedException e) {
							return;
						}
						
						ItemStack newItem;
						
						newItem = this.getFishForBiome();
						
						//Try to add the new item to the dest chest, if we cant, oh well.
						try {
							debug(fishHatchery, "Updating inventory:"+newItem);
							this.updateInventory(Action.ADD, dest_inv, newItem);
						} catch (InterruptedException e) {
							return;
						}
						break;
					}
				}
			}
			
			if (this.fishHatchery.getLevel() >= 3)
			{
				for(ItemStack stack : contents2) {
					if (stack == null) {
						continue;
					}
					
					if (ItemManager.getType(stack) == CivData.FISHING_ROD) {
						try {
							if ( fishHatchery.currentItem3 == null ) {
								short damage = ItemManager.getData(stack);
								this.updateInventory(Action.REMOVE, source_inv2, stack);
								damage++;
								stack.setDurability(damage);
								if (damage < 64 && stack.getAmount() == 1) {
									fishHatchery.currentItem3 = stack;
								} 
							} else {
								short damage = ItemManager.getData(fishHatchery.currentItem3);
								damage++;
								fishHatchery.currentItem3.setDurability(damage);
								if (damage > 64 || fishHatchery.currentItem3.getAmount() != 1) {
									fishHatchery.currentItem3 = null;
								}
							}
						} catch (InterruptedException e) {
							return;
						}
						
						ItemStack newItem;
						
						newItem = this.getFishForBiome();
						
						//Try to add the new item to the dest chest, if we cant, oh well.
						try {
							debug(fishHatchery, "Updating inventory:"+newItem);
							this.updateInventory(Action.ADD, dest_inv, newItem);
						} catch (InterruptedException e) {
							return;
						}
						break;
					}
				}
			}
			if (this.fishHatchery.getLevel() >= 4)
			{
				for(ItemStack stack : contents3) {
					if (stack == null) {
						continue;
					}
					
					if (ItemManager.getType(stack) == CivData.FISHING_ROD) {
						try {
							if ( fishHatchery.currentItem4 == null ) {
								short damage = ItemManager.getData(stack);
								this.updateInventory(Action.REMOVE, source_inv3, stack);
								damage++;
								stack.setDurability(damage);
								if (damage < 64 && stack.getAmount() == 1) {
									fishHatchery.currentItem4 = stack;
								} 
							} else {
								short damage = ItemManager.getData(fishHatchery.currentItem4);
								damage++;
								fishHatchery.currentItem4.setDurability(damage);
								if (damage > 64 || fishHatchery.currentItem4.getAmount() != 1) {
									fishHatchery.currentItem4 = null;
								}
							}
						} catch (InterruptedException e) {
							return;
						}
						
						ItemStack newItem = this.getFishForBiome();
						
						//Try to add the new item to the dest chest, if we cant, oh well.
						try {
							debug(fishHatchery, "Updating inventory:"+newItem);
							this.updateInventory(Action.ADD, dest_inv, newItem);
						} catch (InterruptedException e) {
							return;
						}
						break;
					}
				}
			}
		}	
		fishHatchery.skippedCounter = 0;
	}
	
	private int getBiome() {
		Biome biome = this.fishHatchery.getBiome();
		
		if (biome.equals(Biome.BIRCH_FOREST_HILLS) ||
				biome.equals(Biome.TALL_BIRCH_FOREST) ||
				biome.equals(Biome.TALL_BIRCH_HILLS) ||
				biome.equals(Biome.SNOWY_TAIGA) ||
				biome.equals(Biome.MOUNTAIN_EDGE) ||
				biome.equals(Biome.SNOWY_TAIGA_MOUNTAINS ) ||
				biome.equals(Biome.SNOWY_MOUNTAINS) ||
				biome.equals(Biome.MODIFIED_JUNGLE) ||
				biome.equals(Biome.JUNGLE_HILLS) ||
				biome.equals(Biome.MODIFIED_JUNGLE) ||
				biome.equals(Biome.MODIFIED_BADLANDS_PLATEAU) ||
				biome.equals(Biome.MODIFIED_WOODED_BADLANDS_PLATEAU) ||
				biome.equals(Biome.ERODED_BADLANDS) ||
				biome.equals(Biome.BADLANDS) ||
				biome.equals(Biome.SHATTERED_SAVANNA) ||
				biome.equals(Biome.SHATTERED_SAVANNA_PLATEAU) ||
				biome.equals(Biome.SWAMP_HILLS) ||
				biome.equals(Biome.SWAMP) ||
				biome.equals(Biome.TAIGA))
		{
			return 1;
		}
		else if (biome.equals(Biome.BIRCH_FOREST) ||
				biome.equals(Biome.MOUNTAINS) ||
				biome.equals(Biome.FOREST) ||
				biome.equals(Biome.GRAVELLY_MOUNTAINS) ||
				biome.equals(Biome.SNOWY_TAIGA_HILLS) ||
				biome.equals(Biome.SNOWY_TAIGA_MOUNTAINS) ||
				biome.equals(Biome.ICE_SPIKES) ||
				biome.equals(Biome.JUNGLE) ||
				biome.equals(Biome.JUNGLE_EDGE) ||
				biome.equals(Biome.GIANT_SPRUCE_TAIGA) ||
				biome.equals(Biome.GIANT_SPRUCE_TAIGA_HILLS) ||
				biome.equals(Biome.GIANT_TREE_TAIGA) ||
				biome.equals(Biome.GIANT_TREE_TAIGA_HILLS) ||
				biome.equals(Biome.DARK_FOREST) ||
				biome.equals(Biome.BADLANDS) ||
				biome.equals(Biome.BADLANDS_PLATEAU) ||
				biome.equals(Biome.ERODED_BADLANDS ) ||
				biome.equals(Biome.TAIGA_MOUNTAINS) ||
				biome.equals(Biome.TALL_BIRCH_FOREST) ||
				biome.equals(Biome.SAVANNA) ||
				biome.equals(Biome.SAVANNA_PLATEAU) ||
				biome.equals(Biome.TAIGA) ||
				biome.equals(Biome.TAIGA_HILLS))
		{
			return 2;
		}
		else if (biome.equals(Biome.BEACH) ||
				biome.equals(Biome.SNOWY_BEACH) ||
				biome.equals(Biome.SNOWY_TAIGA) ||
				biome.equals(Biome.DEEP_OCEAN) ||
				biome.equals(Biome.DESERT) ||
				biome.equals(Biome.DESERT_HILLS) ||
				biome.equals(Biome.FROZEN_OCEAN) ||
				biome.equals(Biome.FROZEN_RIVER) ||
				biome.equals(Biome.MUSHROOM_FIELDS) ||
				biome.equals(Biome.MUSHROOM_FIELD_SHORE) ||
				biome.equals(Biome.OCEAN) ||
				biome.equals(Biome.WARM_OCEAN) ||
				biome.equals(Biome.LUKEWARM_OCEAN) ||
				biome.equals(Biome.PLAINS) ||
				biome.equals(Biome.RIVER) ||
				biome.equals(Biome.SWAMP) )
		{
			return 3;
		}
		else
		{
			return 0;
		}
	}
	
	private ItemStack getFishForBiome() {
		

		Random rand = new Random();
		int maxRand = FishHatchery.MAX_CHANCE;
		int baseRand = rand.nextInt(maxRand);
		ItemStack newItem = null;
		int fisheryLevel = this.fishHatchery.getLevel();
		int fishTier;
		if (baseRand < ((int)((fishHatchery.getChance(FishHatchery.FISH_T4_RATE))*maxRand)) && fisheryLevel >= 4) {
			fishTier = 4;
		} else if (baseRand < ((int)((fishHatchery.getChance(FishHatchery.FISH_T3_RATE))*maxRand)) && fisheryLevel >= 3) {
			fishTier = 3;
		} else if (baseRand < ((int)((fishHatchery.getChance(FishHatchery.FISH_T2_RATE))*maxRand)) && fisheryLevel >= 2) {
			fishTier = 2;
		} else if (baseRand < ((int)((fishHatchery.getChance(FishHatchery.FISH_T1_RATE))*maxRand))) {
			fishTier = 1;
		} else {
			fishTier = 0;
		}
		int biome = getBiome();

		int randMax = 100;
		int biomeRand = rand.nextInt(randMax);
		
		switch (fishTier) {
			case 0: //Fish Tier 0
				if (biomeRand >= 95) { 
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_pufferfish"));
				} else if (biomeRand > 85) {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_clownfish"));
				} else if (biomeRand > 75) {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_salmon"));
				} else if (biomeRand > 50) {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_fish"));
				} else {
					int junkRand = rand.nextInt(randMax);
					if (junkRand > 90)
					{
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_minnows"));
					}else if (junkRand > 70) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_tadpole"));
					} else if (junkRand > 50) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_seaweed"));
					} else if (junkRand > 30) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_tangled_string"));
					} else {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_pond_scum"));
					}
				}
				break;
			case 1: //Fish Tier 1
				switch (biome) {
				case 0: //Not ranked
					newItem = ItemManager.createItemStack(CivData.FISH_RAW, 1);
					break;

				case 1: //Mountains
					if (biomeRand < 90) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_brown_trout"));
					} else {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_gag_grouper"));
					}
					break;

				case 2: //Flatter Lands
					if (biomeRand < 90) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_atlantic_striped_bass"));
					} else {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_arrowtooth_flounder"));
					}
					break;

				case 3: // Oceans, Mushroom, Swamps, Ice
					if (biomeRand < 90) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_atlantic_cod"));
					} else {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_atlantic_surfclam"));
					}
					break;
			}
				break;

			case 2: //Fish Tier 2
				switch (biome) {
				case 0: //Not ranked
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_fish"));
					break;

				case 1: //Mountains
					if (biomeRand < 90) {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_brook_trout"));
					} else {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_red_grouper"));
					}
					break;

				case 2: //Flatter Lands
					if (biomeRand < 90) {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_pacific_ocean_perch"));
					} else {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_summer_flounder"));
					}
					break;

				case 3: // Oceans, Mushroom, Swamps, Ice
					if (biomeRand < 90) {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_pacific_cod"));
					} else {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_ocean_quahog"));
					}
					break;
			}
				break;

			case 3: //Fish Tier 3
				switch (biome) {
				case 0: //Not ranked
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_fish"));
					break;

				case 1: //Mountains
					if (biomeRand < 80) {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_cutthroat_trout"));
					} else {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_black_sea_bass"));
					}
					break;

				case 2: //Flatter Lands
					if (biomeRand < 80) {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_acadian_redfish"));
					} else {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_winter_flounder"));
					}
					break;

				case 3: // Oceans, Mushroom, Swamps, Ice
					if (biomeRand < 80) {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_lingcod"));
					} else {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_northern_quahog"));
					}
					break;
			}
				break;

			case 4: //Fish Tier 4
				switch (biome) {
				case 0: //Not ranked
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_fish"));
					break;

				case 1: //Mountains
					if (biomeRand < 80) {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_rainbow_trout"));
					} else {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_wreckfish"));
					}
					break;

				case 2: //Flatter Lands
					if (biomeRand < 80) {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_widow_rockfish"));
					} else {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_yellowtail_flounder"));
					}
					break;

				case 3: // Oceans, Mushroom, Swamps, Ice
					if (biomeRand < 80) {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_sablefish"));
					} else {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_geoduck"));
					}
					break;
			}
				break;
				
		}
		if (newItem == null)
		{
			CivLog.debug("Fish Hatchery: newItem was null");
			newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("mat_fish_fish"));
		}

		return newItem;
		
	}
	
	@Override
	public void run() {
		if (this.fishHatchery.lock.tryLock()) {
			try {
				try {
					processFisheryUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} finally {
				this.fishHatchery.lock.unlock();
			}
		} else {
			debug(this.fishHatchery, "Failed to get lock while trying to start task, aborting.");
		}
	}

}