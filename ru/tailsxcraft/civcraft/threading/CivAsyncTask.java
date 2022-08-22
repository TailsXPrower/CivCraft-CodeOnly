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
package ru.tailsxcraft.civcraft.threading;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ru.tailsxcraft.civcraft.exception.CivTaskAbortException;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.structure.farm.FarmChunk;
import ru.tailsxcraft.civcraft.structure.farm.GrowBlock;
import ru.tailsxcraft.civcraft.threading.sync.SyncBuildUpdateTask;
import ru.tailsxcraft.civcraft.threading.sync.SyncGetChestInventory;
import ru.tailsxcraft.civcraft.threading.sync.SyncGrowTask;
import ru.tailsxcraft.civcraft.threading.sync.SyncLoadChunk;
import ru.tailsxcraft.civcraft.threading.sync.SyncUpdateInventory;
import ru.tailsxcraft.civcraft.threading.sync.request.GetChestRequest;
import ru.tailsxcraft.civcraft.threading.sync.request.GrowRequest;
import ru.tailsxcraft.civcraft.threading.sync.request.LoadChunkRequest;
import ru.tailsxcraft.civcraft.threading.sync.request.UpdateInventoryRequest;
import ru.tailsxcraft.civcraft.threading.sync.request.UpdateInventoryRequest.Action;
import ru.tailsxcraft.civcraft.util.MultiInventory;
import ru.tailsxcraft.civcraft.util.SimpleBlock;

public abstract class CivAsyncTask implements Runnable {
	/*
	 *  This is really a terrible hack because Bukkit inst thread safe. What I *want* to do
	 * is to have the main thread run uninterrupted as much as possible and *delay* all other
	 * threads that rely on the same data. 
	 * I cannot rely on java's 'synchronization' since AFAIK they are not used in the correct places
	 * inside of craftbukkit, nor do I wish to block the main thread from one of my util threads.
	 * But since I can schedule tasks in the main thread, I'll create 'get' and 'set' tasks
	 * that will run synchronously, and when they finish they will set a return object inside
	 * the task that executed them, then notify them that it is finished. 
	 * 
	 * We can also limit the 'set' task to only perform a certain number of operations per-tick
	 * thus ensuring some level of TPS performance, (although we do have to worry about getting 
	 * too far behind in our task queue)
	 * 
	 * All of our async tasks will inherit from this object and use it's interface to interact
	 * with the get and set sync tasks that we will create.
	 * 
	 */

	/* Object returned from sync task. */
	public static final long TIMEOUT = 5000;
	
	protected boolean finished = true;
			
	public boolean isFinished() 
	{
		return finished;
	}
			
	public void syncLoadChunk(String worldname, int x, int z) throws InterruptedException {
		
		LoadChunkRequest request = new LoadChunkRequest(SyncLoadChunk.lock);
		request.worldName = worldname;
		request.x = x;
		request.z = z;
		
		this.finished = false;
		
		SyncLoadChunk.lock.lock();
		try {
			SyncLoadChunk.requestQueue.add(request);
			while(!request.finished) {
				/* 
				 * We await for the finished flag to be set, at this
				 * time the await function will give up the lock above
				 * and automagically re-lock when its finished.
				 */
				request.condition.await(TIMEOUT, TimeUnit.MILLISECONDS);
				if (!request.finished) {
					CivLog.warning("Couldn't load chunk in "+TIMEOUT+" milliseconds! Retrying.");
				}
			}
			
		} finally {
			this.finished = true;
			SyncLoadChunk.lock.unlock();
		}
		
	}
		
	public Inventory getChestInventory(String worldname, int x, int y, int z, boolean retry) throws InterruptedException, CivTaskAbortException {

		GetChestRequest request = new GetChestRequest(SyncGetChestInventory.lock);
		request.worldName = worldname;
		request.block_x = x;
		request.block_y = y;
		request.block_z = z;
		
		this.finished = false;
		
		SyncGetChestInventory.lock.lock();
		try {
			SyncGetChestInventory.requestQueue.add(request);
			while(!request.finished) {
				/* 
				 * We await for the finished flag to be set, at this
				 * time the await function will give up the lock above
				 * and automagically re-lock when its finished.
				 */
				request.condition.await(TIMEOUT, TimeUnit.MILLISECONDS);
				if (!request.finished) {
					if (!retry) {
						throw new CivTaskAbortException("Couldn't get chest in "+TIMEOUT+" milliseconds, aborting.");
					} else {
						CivLog.warning("Couldn't get chest in "+TIMEOUT+" milliseconds! Retrying.");
					}
				}
			}
			
			return (Inventory)request.result;
		} finally {
			this.finished = true;
			SyncGetChestInventory.lock.unlock();
		}
	}
	
	public void updateBlocksQueue(Queue<SimpleBlock> sbs) {

		SyncBuildUpdateTask.queueSimpleBlock(sbs);
		return;

		//		this.finished = false;
//		SimpleBlock sb;
//		while((sb = sbs.poll()) != null) {		
//			if (!SyncBuildUpdateTask.updateBlocks.offer(sb)) {
//				this.finished = true;
//				return false;
//			}
//		}
//		
//		this.finished = true;
//		return true;
	}
	
	public Boolean updateInventory(Action action, MultiInventory inv, ItemStack itemStack) throws InterruptedException {

		UpdateInventoryRequest request = new UpdateInventoryRequest(SyncUpdateInventory.lock);
		request.action = action;
		request.stack = itemStack;
		request.multiInv = inv;
		
		this.finished = false;
		
		SyncUpdateInventory.lock.lock();
		try {
			SyncUpdateInventory.requestQueue.add(request);
			while(!request.finished) {
				/* 
				 * We await for the finished flag to be set, at this
				 * time the await function will give up the lock above
				 * and automagically re-lock when its finished.
				 */
				request.condition.await(TIMEOUT, TimeUnit.MILLISECONDS);
				if (!request.finished) {
					CivLog.warning("Couldn't async update inventory in "+TIMEOUT+" milliseconds! Retrying.");
				}
			}
			
			return (Boolean)request.result;
		} finally {
			this.finished = true;
			SyncUpdateInventory.lock.unlock();
		}
	}
	
	public Boolean growBlocks(LinkedList<GrowBlock> growBlocks, FarmChunk farmChunk) throws InterruptedException {
		
		GrowRequest request = new GrowRequest(SyncGrowTask.lock);
		request.growBlocks = growBlocks;
		request.farmChunk = farmChunk;
		
		this.finished = false;
		SyncGrowTask.lock.lock();
		try {
			SyncGrowTask.requestQueue.add(request);
			while (!request.finished) {
				request.condition.await(TIMEOUT, TimeUnit.MILLISECONDS);
				if (!request.finished) {
					CivLog.warning("Couldn't grow block in "+TIMEOUT+" milliseconds! retrying.");
				}
			}
			
			return (Boolean)request.result;
		} finally {
			this.finished = true;
			SyncGrowTask.lock.unlock();
		}
	}
	
	
}