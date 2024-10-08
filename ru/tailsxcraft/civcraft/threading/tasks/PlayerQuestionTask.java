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
package ru.tailsxcraft.civcraft.threading.tasks;


import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.questions.QuestionBaseTask;
import ru.tailsxcraft.civcraft.questions.QuestionResponseInterface;
import ru.tailsxcraft.civcraft.util.CivColor;

public class PlayerQuestionTask extends QuestionBaseTask implements Runnable {

	Player askedPlayer; /* player who is being asked a question. */
	Player questionPlayer; /* player who has asked the question. */
	String question; /* Question being asked. */
	long timeout; /* Timeout after question expires. */
//	RunnableWithArg finishedTask; /* Task to run when a response has been generated. */
	QuestionResponseInterface finishedFunction;
	
	protected String response = new String(); /* Response to the question. */
	@SuppressWarnings("removal")
	protected Boolean responded = new Boolean(false); /*Question was answered. */
	
	public PlayerQuestionTask() {
	}
	
	public PlayerQuestionTask(Player askedplayer, Player questionplayer, String question, long timeout, 
			QuestionResponseInterface finishedFunction) {
		
		this.askedPlayer = askedplayer;
		this.questionPlayer = questionplayer;
		this.question = question;
		this.timeout = timeout;
		this.finishedFunction = finishedFunction;
		
	}
	
	@Override
	public void run() {	
		CivMessage.send(askedPlayer, CivColor.LightGray+CivSettings.localize.localizedString("civleaderQtast_prompt1")+" "+CivColor.LightBlue+questionPlayer.getName());
		CivMessage.send(askedPlayer, CivColor.LightPurple+CivColor.BOLD+question);
		TextComponent textComponent2 = Component.text()
				  .content("����������� '").color(NamedTextColor.GRAY)
				  .append(Component.text().content("/accept").clickEvent(ClickEvent.runCommand("/accept")).hoverEvent(HoverEvent.showText(Component.text("�������, ����� �������").color(NamedTextColor.GREEN))).color(NamedTextColor.GREEN).build())
				  .append(Component.text("' ��� '").color(NamedTextColor.GRAY))
				  .append(Component.text().content("/deny").clickEvent(ClickEvent.runCommand("/deny")).hoverEvent(HoverEvent.showText(Component.text("�������, ����� ���������").color(NamedTextColor.RED))).color(NamedTextColor.RED).build())
				  .append(Component.text("', ����� �������� �� ������."))
				  .build();
		askedPlayer.sendMessage(textComponent2);
		
		try {
			synchronized(this) {
				this.wait(timeout);
			}
		} catch (InterruptedException e) {
			cleanup();
			return;
		}
		
		if (responded) {
			finishedFunction.processResponse(response);
			cleanup();
			return;
		}
		
		CivMessage.send(askedPlayer, CivColor.LightGray+CivSettings.localize.localizedString("var_PlayerQuestionTask_failedInTime",questionPlayer.getName()));
		CivMessage.send(questionPlayer, CivColor.LightGray+CivSettings.localize.localizedString("var_civQtast_NoResponse",askedPlayer.getName()));
		cleanup();
	}

	public Boolean getResponded() {
		synchronized(responded) {
			return responded;
		}
	}

	public void setResponded(Boolean response) {
		synchronized(this.responded) {
			this.responded = response;
		}
	}

	public String getResponse() {
		synchronized(response) {
			return response;
		}
	}

	public void setResponse(String response) {
		synchronized(this.response) {
			setResponded(true);
			this.response = response;
		}
	}
	
	/* When this task finishes, remove itself from the hashtable. */
	private void cleanup() {
		CivGlobal.removeQuestion(askedPlayer.getName());
	}
	
	
	
}
