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
package ru.tailsxcraft.civcraft.command.civ;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import ru.tailsxcraft.civcraft.command.CommandBase;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Civilization;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.questions.DiplomacyGiftResponse;
import ru.tailsxcraft.civcraft.questions.QuestionResponseInterface;
import ru.tailsxcraft.civcraft.threading.TaskMaster;
import ru.tailsxcraft.civcraft.threading.tasks.CivQuestionTask;
import ru.tailsxcraft.civcraft.util.CivColor;
import ru.tailsxcraft.civcraft.war.War;

public class CivDiplomacyGiftCommand extends CommandBase {

	@Override
	public void init() {
		command = "/civ dip gift";
		displayName = CivSettings.localize.localizedString("cmd_civ_dipgift_name");
		
		commands.put("entireciv", CivSettings.localize.localizedString("cmd_civ_dipgift_entirecivDesc"));
		commands.put("town", CivSettings.localize.localizedString("cmd_civ_dipgift_townDesc"));
		
	}

	private void sendGiftRequest(Civilization toCiv, Civilization fromCiv, String message, 
			QuestionResponseInterface finishedFunction) throws CivException {
		CivQuestionTask task = CivGlobal.civQuestions.get(toCiv.getName()); 
		if (task != null) {
			/* Civ already has a question pending. Lets deny this question until it times out
			 * this will allow questions to come in on a pseduo 'first come first serve' and 
			 * prevents question spamming.
			 */
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_dipgift_sendHasPending"));			
		}
		
		task = new CivQuestionTask(toCiv, fromCiv, message, 30000, finishedFunction);
		CivGlobal.civQuestions.put(toCiv.getName(), task);
		TaskMaster.asyncTask("", task, 0);
	}
	
	public void entireciv_cmd() throws CivException {
		this.validLeader();
		Civilization fromCiv = getSenderCiv();
		Civilization toCiv = getNamedCiv(1);
		
		if (fromCiv == toCiv) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_dipgift_entirecivSelf"));
		}
		
		if (fromCiv.getDiplomacyManager().isAtWar() || toCiv.getDiplomacyManager().isAtWar()) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_dipgift_entirecivAtWar"));
		}
		
		fromCiv.validateGift();
		toCiv.validateGift();
		
		if (War.isWarTime()) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_dipgift_entirecivDuringWar"));
		}
		
		if (War.isWithinWarDeclareDays()) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_dipgift_entirecivClostToWar1")+" "+War.getTimeDeclareDays()+" "+CivSettings.localize.localizedString("cmd_civ_dip_declareTooCloseToWar4"));
		}
		
		
		DiplomacyGiftResponse dipResponse = new DiplomacyGiftResponse();
		dipResponse.giftedObject = fromCiv;
		dipResponse.fromCiv = fromCiv;
		dipResponse.toCiv = toCiv;
		
		sendGiftRequest(toCiv, fromCiv, 
				CivColor.Yellow+ChatColor.BOLD+CivSettings.localize.localizedString("var_cmd_civ_dipgift_entirecivRequest1",fromCiv.getName())+
						" "+CivSettings.localize.localizedString("var_cmd_civ_dipgift_entirecivRequest2",fromCiv.getMergeCost(),CivSettings.CURRENCY_NAME), dipResponse);
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("cmd_civ_dipgift_entirecivSuccess"));
	}
	
	public void town_cmd() throws CivException {
		this.validLeader();
		Civilization fromCiv = getSenderCiv();
		Town giftedTown = getNamedTown(1);
		Civilization toCiv = getNamedCiv(2);

		if (giftedTown.getCiv() != fromCiv) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_dipgift_townNotYours"));
		}
		
		if (giftedTown.getCiv() == toCiv) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_dipgift_townNotInCiv"));
		}
		
		if (giftedTown.getMotherCiv() != null && toCiv != giftedTown.getMotherCiv()) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_dipgift_townNotMother"));
		}
		
		if (giftedTown.isCapitol()) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_dipgift_townNotCapitol"));
		}
		
		if (War.isWarTime()) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_dipgift_townNotDuringWar"));
		}
		
		if (fromCiv.getDiplomacyManager().isAtWar() || toCiv.getDiplomacyManager().isAtWar()) {
			throw new CivException(CivSettings.localize.localizedString("cmd_civ_dipgift_townNotAtWar"));
		}
		
		fromCiv.validateGift();
		toCiv.validateGift();
		giftedTown.validateGift();
		
		DiplomacyGiftResponse dipResponse = new DiplomacyGiftResponse();
		dipResponse.giftedObject = giftedTown;
		dipResponse.fromCiv = fromCiv;
		dipResponse.toCiv = toCiv;
		
		sendGiftRequest(toCiv, fromCiv, 
				CivSettings.localize.localizedString("var_cmd_civ_dipgift_townRequest1",fromCiv.getName(),giftedTown.getName(),giftedTown.getGiftCost(),CivSettings.CURRENCY_NAME), dipResponse);
		CivMessage.sendSuccess(sender, CivSettings.localize.localizedString("cmd_civ_dipgift_entirecivSuccess"));
		
	}
	
	@Override
	public void doDefaultAction() throws CivException {
		showHelp();
	}

	@Override
	public void showHelp() {
		showBasicHelp();
	}

	@Override
	public void permissionCheck() throws CivException {
		// permission checked in parent command.
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

}
