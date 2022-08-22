package ru.tailsxcraft.civcraft.command.debug;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.command.CommandBase;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.util.ChunkCoord;

public class DebugWorldCommand extends CommandBase {

	@Override
	public void init() {
		command = "/dbg world";
		displayName = "Debug World";
		
		commands.put("create", "[name] - creates a new test world with this name.");
		commands.put("tp", "[name] teleports you to spawn at the specified world.");
		commands.put("list", "Lists worlds according to bukkit.");
	}
	
	public void list_cmd() {
		CivMessage.sendHeading(sender, "Worlds");
		for (World world : Bukkit.getWorlds()) {
			CivMessage.send(sender, world.getName());
		}
	}
	
	public void create_cmd() throws CivException {
		String name = getNamedString(1, "enter a world name");
		
		WorldCreator wc = new WorldCreator(name);
		wc.environment(Environment.NORMAL);
		wc.type(WorldType.FLAT);
		wc.generateStructures(false);
		
		World world = Bukkit.getServer().createWorld(wc);
		world.setSpawnFlags(false, false);
		ChunkCoord.addWorld(world);
		
		CivMessage.sendSuccess(sender, "World "+name+" created.");
		
	}
	
	public void tp_cmd() throws CivException {
		String name = getNamedString(1, "enter a world name");
		Player player = getPlayer();
		
		World world = Bukkit.getWorld(name);
		player.teleport(world.getSpawnLocation());
		
		CivMessage.sendSuccess(sender, "Teleported to spawn at world:"+name);
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
		
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

}
