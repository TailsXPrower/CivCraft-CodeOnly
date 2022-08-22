package ru.tailsxcraft.civcraft.structurevalidation;

import java.util.Iterator;
import java.util.Map.Entry;

import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.structure.Structure;
import ru.tailsxcraft.civcraft.util.BlockCoord;
import ru.tailsxcraft.civcraft.war.War;

public class StructureValidationChecker implements Runnable {

	@Override
	public void run() {
		Iterator<Entry<BlockCoord, Structure>> structIter = CivGlobal.getStructureIterator();
		while (structIter.hasNext()) {
			Structure struct = structIter.next().getValue();
			
			if (War.isWarTime()) {
				/* Don't do any work once it's war time. */
				break;
			}
			
			if (!struct.isActive()) {
				continue;
			}
			
			if (struct.isIgnoreFloating()) {
				continue;
			}
			
			try {
				CivLog.warning("Doing a structure validate... "+struct.getDisplayName());
				struct.validate(null);
			} catch (CivException e) {
				e.printStackTrace();
			}
			
			synchronized (this) {
				try {
					this.wait(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
