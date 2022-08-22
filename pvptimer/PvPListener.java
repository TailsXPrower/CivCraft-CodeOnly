package pvptimer;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;

public class PvPListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPvP(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();
			Resident damagerResident = CivGlobal.getResident(damager);
			
			if (damagerResident.isProtected() && (event.getEntity() instanceof Player)) {
				CivMessage.sendError(damager, CivSettings.localize.localizedString("pvpListenerError"));
				event.setCancelled(true);					
			}
		}
		if (event.getDamager() instanceof Arrow) {
			LivingEntity shooter = (LivingEntity) ((Arrow) event.getDamager()).getShooter();
			
			if ((shooter instanceof Player) && (event.getEntity() instanceof Player)) {
				Player damager = (Player) shooter;
				Resident damagerResident = CivGlobal.getResident(damager);

				if (damagerResident.isProtected()) {
					CivMessage.sendError(damager, CivSettings.localize.localizedString("pvpListenerError"));
					event.setCancelled(true);
				} else {
					Player defendingPlayer = (Player) event.getEntity();
					Resident defendingResident = CivGlobal.getResident(defendingPlayer);
					if (defendingResident.isProtected()) {
						CivMessage.sendError(damager, CivSettings.localize.localizedString("pvpListenerError2"));
						event.setCancelled(true);
					}
				}				
			}
		}
		if ((event.getEntity() instanceof Player) && !event.isCancelled() && (event.getDamager() instanceof Player)) {
			Player damager = (Player) event.getDamager();
			Player defendingPlayer = (Player) event.getEntity();
			Resident defendingResident = CivGlobal.getResident(defendingPlayer);
			if (event.getDamager() instanceof Player) {
				if (defendingResident.isProtected()) {
					event.setCancelled(true);
					CivMessage.sendError(damager, CivSettings.localize.localizedString("pvpListenerError2"));					
				}
			}
		}
	}
}
