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

import org.bukkit.entity.Player;

import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.AlreadyRegisteredException;
import ru.tailsxcraft.civcraft.interactive.InteractiveTeleportPlayer;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.object.Town;
import ru.tailsxcraft.civcraft.util.CivColor;

public class JoinTownResponse implements QuestionResponseInterface {

	public Town town;
	public Resident resident;
	public Player sender;
	
	@Override
	public void processResponse(String param) {
		if (param.equalsIgnoreCase("accept")) {
			CivMessage.send(sender, CivColor.LightGray+CivSettings.localize.localizedString("var_joinTown_accepted",resident.getName()));
			
			try {
				town.addResident(resident);
			} catch (AlreadyRegisteredException e) {
				CivMessage.sendError(sender, CivSettings.localize.localizedString("var_joinTown_errorInTown",resident.getName()));
				return;
			}

			CivMessage.sendTown(town, CivSettings.localize.localizedString("var_joinTown_alert",resident.getName()));
			resident.save();
			
			CivMessage.send(resident, CivColor.LightPurple+"Вам доступна телепортация в Город. Хотите ли вы телепортироваться в него?");
			CivMessage.send(resident, CivColor.LightGray+"Для подтверждения, пропишите "+CivColor.LightBlue+"'yes'"+CivColor.LightGray+"."+" Проигнорируйте запрос для отмены.");
			resident.setInteractiveMode(new InteractiveTeleportPlayer());
			
			//Location townLocation = town.getTownHall().getRandomRevivePoint().getLocation();
			//Player player2;
			//try {
				//player2 = CivGlobal.getPlayer(resident);
				//player2.teleport(townLocation);		
				//TeleportToTownResponse teleport = new TeleportToTownResponse();
				//teleport.town = this.town;
				//teleport.resident = this.resident;
				
				//CivGlobal.questionTeleportPlayer( CivGlobal.getPlayer(resident), 
			 	//		CivSettings.localize.localizedString("var_cmd_town_addInvite",town.getName()),
				//		30000, teleport);
			//} catch (CivException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			//}
			
			//CivMessage.send(sender, "Нулевая хуита прошла проверку");
			
			//TeleportToTownResponse teleport = new TeleportToTownResponse();
			//teleport.town = town;
			//teleport.sender = sender;
			//teleport.resident = resident;
			
			//CivMessage.send(sender, "Первая хуита прошла проверку");
			
			//try {
			//	CivGlobal.questionPlayer(sender, CivGlobal.getPlayer(resident), 
			//			CivSettings.localize.localizedString("var_cmd_town_addInvite",town.getName()),
			//			30000, teleport);
			//} catch (CivException e) {
			//  TODO Auto-generated catch block
			//	e.printStackTrace();
			//}
			
			//CivMessage.send(sender, "Вторая хуита прошла проверку");
		} else {
			CivMessage.send(sender, CivColor.LightGray+CivSettings.localize.localizedString("var_joinTown_Declined",resident.getName()));
		}
	}
	
	@Override
	public void processResponse(String response, Resident responder) {
		processResponse(response);		
	}
}
