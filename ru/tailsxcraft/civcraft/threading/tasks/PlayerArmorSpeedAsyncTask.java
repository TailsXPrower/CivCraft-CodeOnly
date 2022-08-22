package ru.tailsxcraft.civcraft.threading.tasks;

import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.object.Resident;

public class PlayerArmorSpeedAsyncTask implements Runnable {

	Player player;
	
	public PlayerArmorSpeedAsyncTask(Player player) {
		this.player = player;
	}

	@Override
	public void run() {		
		doArmorSpeedCheck();
	}
	
	public void doArmorSpeedCheck() {
		Resident resident = CivGlobal.getResident(this.player);
		resident.calculateWalkingModifier(this.player);
	}

}
