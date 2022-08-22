package ru.tailsxcraft.civcraft.threading.timers;

import java.util.Iterator;
import java.util.Map.Entry;

import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.structure.Structure;
import ru.tailsxcraft.civcraft.structure.Windmill;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.war.War;

public class WindmillTimer implements Runnable {

	@Override
	public void run() {
		if (War.isWarTime()) {
			return;
		}
		
		Iterator<Entry<BlockCoord, Structure>> iter = CivGlobal.getStructureIterator();
		while(iter.hasNext()) {
			Structure struct = iter.next().getValue();
			if (struct instanceof Windmill) {
				((Windmill)struct).processWindmill();
			}
		}
	}

}
