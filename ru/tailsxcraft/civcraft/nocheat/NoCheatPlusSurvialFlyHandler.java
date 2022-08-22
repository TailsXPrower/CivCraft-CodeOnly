package ru.tailsxcraft.civcraft.nocheat;

import org.bukkit.entity.Player;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.checks.access.IViolationInfo;
import fr.neatmonster.nocheatplus.hooks.AbstractNCPHook;
import fr.neatmonster.nocheatplus.hooks.NCPHookManager;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.InvalidConfiguration;

public class NoCheatPlusSurvialFlyHandler extends AbstractNCPHook {
	
	public static void init() {
		NCPHookManager.addHook(CheckType.MOVING_SURVIVALFLY, new NoCheatPlusSurvialFlyHandler());
	}
	
	@Override
	public String getHookName() {
		return "CivCraft:"+this.getClass().getSimpleName();
	}

	@Override
	public String getHookVersion() {
		return "1.0";
	}

    @Override
    public boolean onCheckFailure(final CheckType checkType, final Player player, final IViolationInfo info) {
    	double violationGrace;
		try {
			violationGrace = CivSettings.getDouble(CivSettings.nocheatConfig, "nocheatplus.survivalfly.violation_grace");
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			return false;
		}
    	
    	if (info.getAddedVl() < violationGrace) {
    		return true;
    	}
    	
    	return false;
    }
	
}
