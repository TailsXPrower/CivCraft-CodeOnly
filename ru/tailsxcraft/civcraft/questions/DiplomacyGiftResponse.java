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
package ru.tailsxcraft.civcraft.questions;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.main.CivLog;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Civilization;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.util.CivColor;

public class DiplomacyGiftResponse implements QuestionResponseInterface {

	public Object giftedObject;
	public Civilization fromCiv;
	public Civilization toCiv;
	
	@Override
	public void processResponse(String param) {
		if (param.equalsIgnoreCase("accept")) {
			
			if (giftedObject instanceof Town) {
				Town town = (Town)giftedObject;
				
				if (!toCiv.getTreasury().hasEnough(town.getGiftCost())) {
					CivMessage.sendCiv(toCiv, CivColor.Rose+CivSettings.localize.localizedString("var_diplomacy_gift_ErrorTooPoor",town.getName(),town.getGiftCost(),CivSettings.CURRENCY_NAME));
					CivMessage.sendCiv(fromCiv, CivColor.Rose+CivSettings.localize.localizedString("var_diplomacy_gift_ErrorTooPoor2",toCiv.getName(),town.getName(),town.getGiftCost(),CivSettings.CURRENCY_NAME));
					return;
				}
				
				toCiv.getTreasury().withdraw(town.getGiftCost());
				town.changeCiv(toCiv);
				CivMessage.sendCiv(fromCiv, CivColor.LightGray+CivSettings.localize.localizedString("var_diplomacy_gift_accept",toCiv.getName(),town.getName()));
				return;
			} else if (giftedObject instanceof Civilization) {
				int coins = fromCiv.getMergeCost();
				
				if (!toCiv.getTreasury().hasEnough(coins)) {
					CivMessage.sendCiv(toCiv, CivColor.Rose+CivSettings.localize.localizedString("var_diplomacy_merge_ErrorTooPoor",fromCiv.getName(),coins,CivSettings.CURRENCY_NAME));
					CivMessage.sendCiv(fromCiv, CivColor.Rose+CivSettings.localize.localizedString("var_diplomacy_merge_ErrorTooPoor2",toCiv.getName(),fromCiv.getName(),coins,CivSettings.CURRENCY_NAME));
					return;
				}
				
				toCiv.getTreasury().withdraw(coins);
				CivMessage.sendCiv(fromCiv, CivColor.Yellow+CivSettings.localize.localizedString("var_diplomacy_merge_offerAccepted",toCiv.getName()));
				toCiv.mergeInCiv(fromCiv);
				CivMessage.global(CivSettings.localize.localizedString("var_diplomacy_merge_SuccessAlert1",fromCiv.getName(),toCiv.getName()));
				return;
			} else {
				CivLog.error(CivSettings.localize.localizedString("diplomacy_merge_UnexpectedError")+" "+giftedObject);
				return;
			}
		} else {
			CivMessage.sendCiv(fromCiv, CivColor.LightGray+CivSettings.localize.localizedString("var_RequestDecline",toCiv.getName()));
		}
		
	}
	@Override
	public void processResponse(String response, Resident responder) {
		processResponse(response);		
	}
}
