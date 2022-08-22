package ru.tailsxcraft.civcraft.threading.timers;

import java.util.Iterator;
import java.util.Map.Entry;

import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.structure.Structure;
import ru.tailsxcraft.civcraft.structure.wonders.Wonder;
import ru.tailsxcraft.civcraft.util.BlockCoord;

public class RegenTimer implements Runnable {

	@Override
	public void run() {
		Iterator<Entry<BlockCoord, Structure>> iter = CivGlobal.getStructureIterator();
		
		while(iter.hasNext()) {
			Structure struct = iter.next().getValue();
			struct.processRegen();
		}
		
		for (Wonder wonder : CivGlobal.getWonders()) {
			wonder.processRegen();
		}
	}

}
