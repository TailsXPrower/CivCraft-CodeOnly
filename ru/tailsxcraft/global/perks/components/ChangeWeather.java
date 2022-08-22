package ru.tailsxcraft.global.perks.components;

import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.interactive.InteractiveConfirmWeatherChange;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.util.CivColor;

public class ChangeWeather extends PerkComponent {

	@Override
	public void onActivate(Resident resident) {
		Player player;
		try {
			player = CivGlobal.getPlayer(resident);
		} catch (CivException e) {
			return;
		}
		if (!player.getWorld().isThundering() && !player.getWorld().hasStorm()) {
			CivMessage.sendError(resident, CivSettings.localize.localizedString("weather_isSunny"));
			return;
		}
		
		CivMessage.sendHeading(resident, CivSettings.localize.localizedString("weather_heading"));
		CivMessage.send(resident, CivColor.Green+CivSettings.localize.localizedString("weather_confirmPrompt"));
		CivMessage.send(resident, CivColor.LightGray+CivSettings.localize.localizedString("weather_confirmPrompt2"));
		resident.setInteractiveMode(new InteractiveConfirmWeatherChange(this));
	}
}
