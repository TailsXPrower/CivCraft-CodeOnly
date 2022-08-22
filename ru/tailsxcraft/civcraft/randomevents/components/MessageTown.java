package ru.tailsxcraft.civcraft.randomevents.components;


import ru.tailsxcraft.civcraft.randomevents.RandomEventComponent;

public class MessageTown extends RandomEventComponent {

	@Override
	public void process() {
		String message = this.getString("message");
		sendMessage(message);
	}	
}
