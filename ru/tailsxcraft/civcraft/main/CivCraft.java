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
package ru.tailsxcraft.civcraft.main;

import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tailsxcraft.civcraft.populators.MobSpawnerPopulator;

import pvptimer.PvPListener;
import pvptimer.PvPTimer;
import ru.tailsxcraft.anticheat.ACManager;
import ru.tailsxcraft.civcraft.civilization.GoldenAgeSweeper;
import ru.tailsxcraft.civcraft.command.AcceptCommand;
import ru.tailsxcraft.civcraft.command.BuildCommand;
import ru.tailsxcraft.civcraft.command.DenyCommand;
import ru.tailsxcraft.civcraft.command.EconCommand;
import ru.tailsxcraft.civcraft.command.HereCommand;
import ru.tailsxcraft.civcraft.command.KillCommand;
import ru.tailsxcraft.civcraft.command.PayCommand;
import ru.tailsxcraft.civcraft.command.SelectCommand;
import ru.tailsxcraft.civcraft.command.admin.AdminCommand;
import ru.tailsxcraft.civcraft.command.camp.CampCommand;
import ru.tailsxcraft.civcraft.command.civ.CivChatCommand;
import ru.tailsxcraft.civcraft.command.civ.CivCommand;
import ru.tailsxcraft.civcraft.command.debug.DebugCommand;
import ru.tailsxcraft.civcraft.command.market.MarketCommand;
import ru.tailsxcraft.civcraft.command.plot.PlotCommand;
import ru.tailsxcraft.civcraft.command.resident.ResidentCommand;
import ru.tailsxcraft.civcraft.command.town.TownChatCommand;
import ru.tailsxcraft.civcraft.command.town.TownCommand;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.database.SQL;
import ru.tailsxcraft.civcraft.database.SQLUpdate;
import ru.tailsxcraft.civcraft.endgame.EndConditionNotificationTask;
import ru.tailsxcraft.civcraft.event.EventTimerTask;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;
import ru.tailsxcraft.civcraft.fishing.FishingListener;
import ru.tailsxcraft.civcraft.listener.BlockListener;
import ru.tailsxcraft.civcraft.listener.BonusGoodieManager;
import ru.tailsxcraft.civcraft.listener.ChatListener;
import ru.tailsxcraft.civcraft.listener.CustomItemManager;
import ru.tailsxcraft.civcraft.listener.DebugListener;
import ru.tailsxcraft.civcraft.listener.DisableXPListener;
import ru.tailsxcraft.civcraft.listener.MarkerPlacementManager;
import ru.tailsxcraft.civcraft.listener.MobsListener;
import ru.tailsxcraft.civcraft.listener.PlayerListener;
import ru.tailsxcraft.civcraft.listener.TagAPIListener;
import ru.tailsxcraft.civcraft.listener.armor.ArmorListener;
import ru.tailsxcraft.civcraft.lorestorage.LoreCraftableMaterialListener;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiBuildInventoryListener;
import ru.tailsxcraft.civcraft.lorestorage.LoreGuiItemListener;
import ru.tailsxcraft.civcraft.nocheat.NoCheatPlusSurvialFlyHandler;
import ru.tailsxcraft.civcraft.populators.TradeGoodPopulator;
import ru.tailsxcraft.civcraft.randomevents.RandomEventSweeper;
import ru.tailsxcraft.civcraft.sessiondb.SessionDBAsyncTimer;
import ru.tailsxcraft.civcraft.siege.CannonListener;
import ru.tailsxcraft.civcraft.structure.Farm;
import ru.tailsxcraft.civcraft.structure.farm.FarmGrowthSyncTask;
import ru.tailsxcraft.civcraft.structure.farm.FarmPreCachePopulateTimer;
import ru.tailsxcraft.civcraft.structurevalidation.StructureValidationChecker;
import ru.tailsxcraft.civcraft.structurevalidation.StructureValidationPunisher;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.threading.sync.SyncBuildUpdateTask;
import ru.tailsxcraft.civcraft.threading.sync.SyncGetChestInventory;
import ru.tailsxcraft.civcraft.threading.sync.SyncGrowTask;
import ru.tailsxcraft.civcraft.threading.sync.SyncLoadChunk;
import ru.tailsxcraft.civcraft.threading.sync.SyncUpdateChunks;
import ru.tailsxcraft.civcraft.threading.sync.SyncUpdateInventory;
import ru.tailsxcraft.civcraft.threading.tasks.ArrowProjectileTask;
import ru.tailsxcraft.civcraft.threading.tasks.InventoryUpdateTask;
import ru.tailsxcraft.civcraft.threading.tasks.ProjectileComponentTimer;
import ru.tailsxcraft.civcraft.threading.tasks.ScoutTowerTask;
import ru.tailsxcraft.civcraft.threading.timers.AnnouncementTimer;
import ru.tailsxcraft.civcraft.threading.timers.BeakerTimer;
import ru.tailsxcraft.civcraft.threading.timers.ChangeGovernmentTimer;
import ru.tailsxcraft.civcraft.threading.timers.PlayerLocationCacheUpdate;
import ru.tailsxcraft.civcraft.threading.timers.PlayerProximityComponentTimer;
import ru.tailsxcraft.civcraft.threading.timers.ReduceExposureTimer;
import ru.tailsxcraft.civcraft.threading.timers.RegenTimer;
import ru.tailsxcraft.civcraft.threading.timers.UnitTrainTimer;
import ru.tailsxcraft.civcraft.threading.timers.UpdateEventTimer;
import ru.tailsxcraft.civcraft.threading.timers.UpdateMinuteEventTimer;
import ru.tailsxcraft.civcraft.threading.timers.WindmillTimer;
import ru.tailsxcraft.civcraft.util.BukkitObjects;
import ru.tailsxcraft.civcraft.util.ChunkCoord;
import ru.tailsxcraft.civcraft.util.TimeTools;
import ru.tailsxcraft.civcraft.war.WarListener;
import ru.tailsxcraft.global.scores.CalculateScoreTimer;
import ru.tailsxcraft.sls.SLSManager;

public final class CivCraft extends JavaPlugin {

	private boolean isError = false;	
	private static JavaPlugin plugin;	
	public static boolean isDisable = false;
	
	private void startTimers() {
		
		TaskMaster.asyncTask("SQLUpdate", new SQLUpdate(), 0);
		
		// Sync Timers
		TaskMaster.syncTimer(SyncBuildUpdateTask.class.getName(), 
				new SyncBuildUpdateTask(), 0, 1);
		
		TaskMaster.syncTimer(SyncUpdateChunks.class.getName(), 
				new SyncUpdateChunks(), 0, TimeTools.toTicks(1));
		
		TaskMaster.syncTimer(SyncLoadChunk.class.getName(), 
				new SyncLoadChunk(), 0, 1);
		
		TaskMaster.syncTimer(SyncGetChestInventory.class.getName(),
				new SyncGetChestInventory(), 0, 1);
		
		TaskMaster.syncTimer(SyncUpdateInventory.class.getName(),
				new SyncUpdateInventory(), 0, 1);
		
		TaskMaster.syncTimer(SyncGrowTask.class.getName(),
				new SyncGrowTask(), 0, 1);
		
		TaskMaster.syncTimer(PlayerLocationCacheUpdate.class.getName(), 
				new PlayerLocationCacheUpdate(), 0, 10);
		
		TaskMaster.asyncTimer("RandomEventSweeper", new RandomEventSweeper(), 0, TimeTools.toTicks(10));
		TaskMaster.asyncTimer("GoldenAgeSweeper", new GoldenAgeSweeper(), 0, TimeTools.toTicks(10));
		
		// Structure event timers
		TaskMaster.asyncTimer("UpdateEventTimer", new UpdateEventTimer(), TimeTools.toTicks(1));
		TaskMaster.asyncTimer("UpdateMinuteEventTimer", new UpdateMinuteEventTimer(), TimeTools.toTicks(20));
		TaskMaster.asyncTimer("RegenTimer", new RegenTimer(), TimeTools.toTicks(5));

		TaskMaster.syncTimer("BeakerTimer", new BeakerTimer(60), TimeTools.toTicks(60));
		TaskMaster.syncTimer("UnitTrainTimer", new UnitTrainTimer(), TimeTools.toTicks(1));
		TaskMaster.asyncTimer("ReduceExposureTimer", new ReduceExposureTimer(), 0, TimeTools.toTicks(5));

		try {
			double arrow_firerate = CivSettings.getDouble(CivSettings.warConfig, "arrow_tower.fire_rate");
			TaskMaster.syncTimer("arrowTower", new ProjectileComponentTimer(), (int)(arrow_firerate*20));	
			TaskMaster.asyncTimer("ScoutTowerTask", new ScoutTowerTask(), TimeTools.toTicks(1));
			
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			return;
		}
		TaskMaster.syncTimer("arrowhomingtask", new ArrowProjectileTask(), 5);
			
		// Global Event timers		
		TaskMaster.syncTimer("FarmCropCache", new FarmPreCachePopulateTimer(), TimeTools.toTicks(30));
	
		TaskMaster.asyncTimer("FarmGrowthTimer",
				new FarmGrowthSyncTask(), TimeTools.toTicks(Farm.GROW_RATE));

		TaskMaster.asyncTimer("announcer", new AnnouncementTimer("tips.txt", 5), 0, TimeTools.toTicks(60*60));
		TaskMaster.asyncTimer("announcerwar", new AnnouncementTimer("war.txt", 60), 0, TimeTools.toTicks(60*60));
		
		TaskMaster.asyncTimer("ChangeGovernmentTimer", new ChangeGovernmentTimer(), TimeTools.toTicks(60));
		TaskMaster.asyncTimer("CalculateScoreTimer", new CalculateScoreTimer(), 0, TimeTools.toTicks(60));
		
		TaskMaster.syncTimer(PlayerProximityComponentTimer.class.getName(), 
				new PlayerProximityComponentTimer(), TimeTools.toTicks(1));
		
		TaskMaster.asyncTimer(EventTimerTask.class.getName(), new EventTimerTask(), TimeTools.toTicks(5));

//		if (PlatinumManager.isEnabled()) {
//			TaskMaster.asyncTimer(PlatinumManager.class.getName(), new PlatinumManager(), TimeTools.toTicks(5));
//		}
		
		TaskMaster.syncTimer("WindmillTimer", new WindmillTimer(), TimeTools.toTicks(60));
		TaskMaster.asyncTimer("EndGameNotification", new EndConditionNotificationTask(), TimeTools.toTicks(3600));
				
		TaskMaster.asyncTask(new StructureValidationChecker(), TimeTools.toTicks(120));
		TaskMaster.asyncTimer("StructureValidationPunisher", new StructureValidationPunisher(), TimeTools.toTicks(3600));
		TaskMaster.asyncTimer("SessionDBAsyncTimer", new SessionDBAsyncTimer(), 10);
		TaskMaster.asyncTimer("pvptimer", new PvPTimer(), TimeTools.toTicks(30));

	}
	
	private void registerEvents() {
		final PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(new BlockListener(), this);
		pluginManager.registerEvents(new ChatListener(), this);
		pluginManager.registerEvents(new BonusGoodieManager(), this);
		pluginManager.registerEvents(new MarkerPlacementManager(), this);
		pluginManager.registerEvents(new CustomItemManager(), this);
		pluginManager.registerEvents(new PlayerListener(), this);		
		pluginManager.registerEvents(new MobsListener(), this);
		pluginManager.registerEvents(new DebugListener(), this);
		pluginManager.registerEvents(new LoreCraftableMaterialListener(), this);
		pluginManager.registerEvents(new LoreGuiItemListener(), this);
		pluginManager.registerEvents(new LoreGuiBuildInventoryListener(), this);
		
		Boolean useEXPAsCurrency= true;
		try {
			useEXPAsCurrency = CivSettings.getBoolean(CivSettings.civConfig, "global.use_exp_as_currency");
			
		} catch (InvalidConfiguration e) {
			useEXPAsCurrency = true;
			CivLog.error("Unable to check if EXP should be enabled. Disabling.");
			e.printStackTrace();
		}
		
		if (useEXPAsCurrency) {
			pluginManager.registerEvents(new DisableXPListener(), this);
		}
		pluginManager.registerEvents(new CannonListener(), this);
		pluginManager.registerEvents(new WarListener(), this);
		pluginManager.registerEvents(new FishingListener(), this);	
		pluginManager.registerEvents(new PvPListener(), this);

		if ((hasPlugin("iTag") || hasPlugin("TagAPI")) && hasPlugin("ProtocolLib")) {
			CivSettings.hasITag = true;
			pluginManager.registerEvents(new TagAPIListener(), this);
			CivLog.debug("TagAPI Registered");
		} else {
			CivLog.warning("TagAPI not found, not registering TagAPI hooks. This is fine if you're not using TagAPI.");

		}
		pluginManager.registerEvents(new ArmorListener(getConfig().getStringList("blocked")), this);
	}
	
	private void registerNPCHooks() {
		NoCheatPlusSurvialFlyHandler.init();
	}
	
	@Override
	public void onEnable() {
		setPlugin(this);
		
		this.saveDefaultConfig();
		
		CivLog.init(this);
		BukkitObjects.initialize(this);
		
		//Load World Populators
		BukkitObjects.getWorlds().get(0).getPopulators().add(new TradeGoodPopulator());
		BukkitObjects.getWorlds().get(0).getPopulators().add(new MobSpawnerPopulator());
				
		try {
			CivSettings.init(this);
			
			SQL.initialize();
			SQL.initCivObjectTables();
			ChunkCoord.buildWorldList();
			CivGlobal.loadGlobals();
			
			ACManager.init();
			try {
				SLSManager.init();
			} catch (CivException e1) {
				e1.printStackTrace();
			} catch (InvalidConfiguration e1) {
				e1.printStackTrace();
			}
			

		} catch (InvalidConfiguration | SQLException | IOException | InvalidConfigurationException | CivException | ClassNotFoundException e) {
			e.printStackTrace();
			setError(true);
			return;
			//TODO disable plugin?
		}
		
		// Init commands
		getCommand("town").setExecutor(new TownCommand());
		getCommand("town").setTabCompleter(new TownCommand());
		getCommand("resident").setExecutor(new ResidentCommand());
		getCommand("dbg").setExecutor(new DebugCommand());
		getCommand("plot").setExecutor(new PlotCommand());
		getCommand("accept").setExecutor(new AcceptCommand());
		getCommand("deny").setExecutor(new DenyCommand());
		getCommand("civ").setExecutor(new CivCommand());
		getCommand("civ").setTabCompleter(new CivCommand());
		getCommand("tc").setExecutor(new TownChatCommand());
		getCommand("cc").setExecutor(new CivChatCommand());
		//getCommand("gc").setExecutor(new GlobalChatCommand());
		getCommand("ad").setExecutor(new AdminCommand());
		getCommand("econ").setExecutor(new EconCommand());
		getCommand("pay").setExecutor(new PayCommand());
		getCommand("build").setExecutor(new BuildCommand());
		getCommand("build").setTabCompleter(new BuildCommand());
		getCommand("market").setExecutor(new MarketCommand());
		getCommand("select").setExecutor(new SelectCommand());
		getCommand("here").setExecutor(new HereCommand());
		getCommand("camp").setExecutor(new CampCommand());
		getCommand("kill").setExecutor(new KillCommand());
	
		registerEvents();
		
		if (hasPlugin("NoCheatPlus")) {
			registerNPCHooks();
		} else {
			CivLog.warning("NoCheatPlus not found, not registering NCP hooks. This is fine if you're not using NCP.");
		}
		
		startTimers();
				
		//creativeInvPacketManager.init(this);		
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		isDisable = true;
		SQLUpdate.save();
	}
	
	public boolean hasPlugin(String name) {
		Plugin p;
		p = getServer().getPluginManager().getPlugin(name);
		return (p != null);
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}


	public static JavaPlugin getPlugin() {
		return plugin;
	}


	public static void setPlugin(JavaPlugin plugin) {
		CivCraft.plugin = plugin;
	}


	
	
}
