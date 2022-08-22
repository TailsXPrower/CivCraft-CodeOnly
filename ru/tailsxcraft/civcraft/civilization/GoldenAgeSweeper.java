package ru.tailsxcraft.civcraft.civilization;

import java.util.Date;
import java.util.LinkedList;

import ru.tailsxcraft.civcraft.main.CivMessage;

public class GoldenAgeSweeper implements Runnable {

	private static LinkedList<GoldenAge> events = new LinkedList<GoldenAge>();
	public static final int MILLISECONDS_PER_HOUR = 60*60*1000;
	//public static final int MILLISECONDS_PER_HOUR = 1000;

	@Override
	public void run() {

		/*
		 * The random event sweeper runs periodically on any on-going random events in progress
		 * it executes their requirements components to check for success. If we find success we
		 * then run process() on all of the components that are successful.
		 * 
		 * If we did not have success, we check the time limit on this event. If its past the time then
		 * we're going to run the failures components.
		 */
		
		/* Iterate through requirements, use check() */
		LinkedList<GoldenAge> removed = new LinkedList<GoldenAge>();
		for (GoldenAge event : events) {
			/* Event didn't pass, might be expired. Check so. */
			Date now = new Date();
			
			long expireTime = (event.getStartDate().getTime() + (event.getLength() * MILLISECONDS_PER_HOUR));
			if (now.getTime() > expireTime) {
				/* event is expired. Run failures. */
				CivMessage.sendCiv(event.getCiv(), "Золотой век для нашей цивилизации закончился, всё вернулось в прежнее состояние.");
				event.cleanup();
				removed.add(event);
			}		
		}
		
		/* Unregister any removed events. */
		events.removeAll(removed);
		
	}

	public static void register(GoldenAge event) {
		events.add(event);
	}
	
	public static void remove(GoldenAge event) {
		events.remove(event);
	}

}
