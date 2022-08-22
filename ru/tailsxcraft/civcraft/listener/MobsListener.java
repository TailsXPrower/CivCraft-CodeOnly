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
package ru.tailsxcraft.civcraft.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.config.ConfigMob;
import ru.tailsxcraft.civcraft.lorestorage.LoreMaterial;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.util.CivColor;

public class MobsListener implements Listener {
	
	List<ActiveMob> mobs = new ArrayList<ActiveMob>();
	
	@EventHandler
	public void onMythicMobSpawn(MythicMobSpawnEvent event)	{	
		ConfigMob mob = CivSettings.mobs.get(event.getMob().getMobType());
		
		if(mob != null)	{
			event.getMob().setLevel(mob.tier);
			
			mobs.add(event.getMob());
		    //event.register(new ExampleMechanic(event.getConfig()));
			//log.info("-- Registered Example mechanic!");
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event)	{
		if (event.getDamager() instanceof Player ) {
			
			Player player = (Player)event.getDamager();
			BukkitAPIHelper helper = new BukkitAPIHelper();
			if (!helper.isMythicMob(event.getEntity())) return;
			ActiveMob mob = helper.getMythicMobInstance(event.getEntity());
            ItemStack item = player.getInventory().getItemInMainHand();
            
            switch(item.getType()) {
            case WOODEN_SWORD:
            case WOODEN_AXE:
            case STONE_SWORD:
            case STONE_AXE:
            case IRON_SWORD:
            case IRON_AXE:
            case GOLDEN_SWORD:
            case GOLDEN_AXE:
            case DIAMOND_SWORD:
            case DIAMOND_AXE:
            	if ( mob.getLevel() == 1 ) {
            		LoreMaterial material = LoreMaterial.getMaterial(item);
    				if (item.getType() == Material.WOODEN_SWORD || material.getId().equalsIgnoreCase("mat_wood_ax")) {
    					event.setDamage(event.getDamage()*0.25);
    				}
    			} else if ( mob.getLevel() == 2 ) {
    				if ( !LoreMaterial.isCustom(item) ) return;
    				LoreMaterial material = LoreMaterial.getMaterial(item);
    				if (item.getType() == Material.WOODEN_SWORD || item.getType() == Material.STONE_SWORD || material.getId().equalsIgnoreCase("mat_wood_ax") || material.getId().equalsIgnoreCase("mat_stone_ax") ) {
    					CivMessage.send(player, CivColor.LightGray+"Вы недостаточно сильны, чтобы навредить этому существу");
    					event.setCancelled(true);
    				} else if (material.getId().equalsIgnoreCase("mat_iron_sword") || material.getId().equalsIgnoreCase("mat_iron_ax")) {
    					event.setDamage(event.getDamage()*0.25);
    				}
    			} else if ( mob.getLevel() == 3 ) {
    				if ( !LoreMaterial.isCustom(item) ) return;
    				LoreMaterial material = LoreMaterial.getMaterial(item);
    				if (item.getType() == Material.WOODEN_SWORD || item.getType() == Material.STONE_SWORD || material.getId().equalsIgnoreCase("mat_iron_sword") || material.getId().equalsIgnoreCase("mat_wood_ax") || material.getId().equalsIgnoreCase("mat_stone_ax") || material.getId().equalsIgnoreCase("mat_iron_ax")) {
    					CivMessage.send(player, CivColor.LightGray+"Вы недостаточно сильны, чтобы навредить этому существу");
    					event.setCancelled(true);
    				} else if (material.getId().equalsIgnoreCase("mat_steel_sword") || material.getId().equalsIgnoreCase("mat_steel_ax")) {
    					event.setDamage(event.getDamage()*0.25);
    				}
    			} else if ( mob.getLevel() == 4 ) {
    				if ( !LoreMaterial.isCustom(item) ) return;
    				LoreMaterial material = LoreMaterial.getMaterial(item);
    				if (item.getType() == Material.WOODEN_SWORD || item.getType() == Material.STONE_SWORD || material.getId().equalsIgnoreCase("mat_iron_sword") || material.getId().equalsIgnoreCase("mat_steel_sword") || material.getId().equalsIgnoreCase("mat_wood_ax") || material.getId().equalsIgnoreCase("mat_stone_ax") || material.getId().equalsIgnoreCase("mat_iron_ax") || material.getId().equalsIgnoreCase("mat_steel_ax") ) {
    					CivMessage.send(player, CivColor.LightGray+"Вы недостаточно сильны, чтобы навредить этому существу");
    					event.setCancelled(true);
    				} else if (material.getId().equalsIgnoreCase("mat_carbide_steel_sword") || material.getId().equalsIgnoreCase("mat_carbide_steel_ax")) {
    					event.setDamage(event.getDamage()*0.25);
    				}
    			} 
            	break;
			default:
				//CivMessage.send(player, CivColor.LightGray+"Вы недостаточно сильны, чтобы навредить этому существу");
				//event.setCancelled(true);
				break;
            }
			
			/*
			for ( ActiveMob mob : mobs ) {
				if ( mob.getEntity().getBukkitEntity() != event.getEntity() ) continue;
				CivLog.info("Mob "+mob.getDisplayName()+" Level is "+mob.getLevel());
			}*/
		} else if (event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow)event.getDamager();
			if ( arrow.getShooter() instanceof Player ) {
				Player player = (Player)arrow.getShooter();
				BukkitAPIHelper helper = new BukkitAPIHelper();
				if (!helper.isMythicMob(event.getEntity())) return;
				ActiveMob mob = helper.getMythicMobInstance(event.getEntity());
	            ItemStack item = player.getInventory().getItemInMainHand();
	            switch(item.getType()) {
	            case BOW:
	            	if ( mob.getLevel() == 2 ) {
	    				if ( !LoreMaterial.isCustom(item) ) return;
	    				LoreMaterial material = LoreMaterial.getMaterial(item);
	    				if (material.getId().equalsIgnoreCase("mat_hunting_bow")) {
	    					event.setDamage(event.getDamage()*0.25);
	    				}
	    			} else if ( mob.getLevel() == 3 ) {
	    				if ( !LoreMaterial.isCustom(item) ) return;
	    				LoreMaterial material = LoreMaterial.getMaterial(item);
	    				if (material.getId().equalsIgnoreCase("mat_hunting_bow")) {
	    					CivMessage.send(player, CivColor.LightGray+"Вы недостаточно сильны, чтобы навредить этому существу");
	    					event.setCancelled(true);
	    				} else if (material.getId().equalsIgnoreCase("mat_recurve_bow")) {
	    					event.setDamage(event.getDamage()*0.25);
	    				}
	    			} else if ( mob.getLevel() == 4 ) {
	    				if ( !LoreMaterial.isCustom(item) ) return;
	    				LoreMaterial material = LoreMaterial.getMaterial(item);
	    				if (material.getId().equalsIgnoreCase("mat_hunting_bow") || material.getId().equalsIgnoreCase("mat_recurve_bow") ) {
	    					CivMessage.send(player, CivColor.LightGray+"Вы недостаточно сильны, чтобы навредить этому существу");
	    					event.setCancelled(true);
	    				} else if (material.getId().equalsIgnoreCase("mat_longbow")) {
	    					event.setDamage(event.getDamage()*0.25);
	    				}
	    			} 
	            	break;
				default:
					CivMessage.send(player, CivColor.LightGray+"Вы недостаточно сильны, чтобы навредить этому существу");
					event.setCancelled(true);
					break;
	            }
			} else if (event.getEntity() instanceof Player){
				BukkitAPIHelper helper = new BukkitAPIHelper();
				if ( arrow.getShooter() != null ) {
					if ( helper.isMythicMob((Entity)arrow.getShooter()) ) {
						Player player = (Player)event.getEntity();
						/*
						for (ItemStack stack : player.getEquipment().getArmorContents()) {
							if (LoreMaterial.isCustom(stack)) {
								if ( stack == null || stack.getType() == Material.AIR ) continue;
								CivLog.info("Getting da damage of "+LoreMaterial.getMaterial(stack).getName());
								LoreMaterial.getMaterial(stack).onDefense(event, stack);
								arrow.setDamage(1);
							}
						}*/
					}
				}
			}
		} 
	}
}
