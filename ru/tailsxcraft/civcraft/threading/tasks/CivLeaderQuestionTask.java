package ru.tailsxcraft.civcraft.threading.tasks;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import ru.tailsxcraft.civcraft.config.CivSettings;
import ru.tailsxcraft.civcraft.exception.CivException;
import ru.tailsxcraft.civcraft.main.CivGlobal;
import ru.tailsxcraft.civcraft.main.CivMessage;
import ru.tailsxcraft.civcraft.object.Civilization;
import ru.tailsxcraft.civcraft.object.Resident;
import ru.tailsxcraft.civcraft.questions.QuestionBaseTask;
import ru.tailsxcraft.civcraft.questions.QuestionResponseInterface;
import ru.tailsxcraft.civcraft.util.CivColor;

public class CivLeaderQuestionTask extends QuestionBaseTask implements Runnable {
	Civilization askedCivilization; /* player who is being asked a question. */
	Player questionPlayer; /* player who has asked the question. */
	String question; /* Question being asked. */
	long timeout; /* Timeout after question expires. */
//	RunnableWithArg finishedTask; /* Task to run when a response has been generated. */
	QuestionResponseInterface finishedFunction;
	Resident responder;
	
	protected String response = new String(); /* Response to the question. */
	@SuppressWarnings("removal")
	protected Boolean responded = new Boolean(false); /*Question was answered. */
	
	
	public CivLeaderQuestionTask(Civilization askedplayer, Player questionplayer, String question, long timeout, 
			QuestionResponseInterface finishedFunction) {
		
		this.askedCivilization = askedplayer;
		this.questionPlayer = questionplayer;
		this.question = question;
		this.timeout = timeout;
		this.finishedFunction = finishedFunction;
		
	}
	
	@Override
	public void run() {	
		
		for (Resident resident : askedCivilization.getLeaderGroup().getMemberList()) {
			CivMessage.send(resident, CivColor.LightGray+CivSettings.localize.localizedString("civleaderQtast_prompt1")+" "+CivColor.LightBlue+questionPlayer.getName());
			CivMessage.send(resident, CivColor.LightPurple+CivColor.BOLD+question);
			TextComponent textComponent2 = Component.text()
					  .content("Используйте '").color(NamedTextColor.GRAY)
					  .append(Component.text().content("/accept").clickEvent(ClickEvent.runCommand("/accept")).hoverEvent(HoverEvent.showText(Component.text("Нажмите, чтобы принять").color(NamedTextColor.GREEN))).color(NamedTextColor.GREEN).build())
					  .append(Component.text("' или '").color(NamedTextColor.GRAY))
					  .append(Component.text().content("/deny").clickEvent(ClickEvent.runCommand("/deny")).hoverEvent(HoverEvent.showText(Component.text("Нажмите, чтобы отклонить").color(NamedTextColor.RED))).color(NamedTextColor.RED).build())
					  .append(Component.text("', чтобы ответить на запрос."))
					  .build();
			try {
				CivGlobal.getPlayer(resident).sendMessage(textComponent2);
			} catch (CivException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			synchronized(this) {
				this.wait(timeout);
			}
		} catch (InterruptedException e) {
			CivMessage.send(questionPlayer, CivColor.LightGray+CivSettings.localize.localizedString("civleaderQtast_interrupted"));
			cleanup();
			return;
		}
		
		if (responded) {
			finishedFunction.processResponse(response, responder);
			cleanup();
			return;
		}
		
		CivMessage.send(questionPlayer, CivColor.LightGray+CivSettings.localize.localizedString("civleaderQtast_noResponse"));
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
		CivGlobal.removeQuestion("civ:"+askedCivilization.getName());
	}

	public void setResponder(Resident resident) {
		this.responder = resident;
	}
}
