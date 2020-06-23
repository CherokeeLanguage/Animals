package com.cherokeelessons.common;

import com.cherokeelessons.animals.enums.GameEvent;


public class GameEventMessage {
	private GameEvent message = GameEvent.NoEvent;

	public GameEventMessage() {
	}
	
	public GameEventMessage(GameEvent message) {
		this.message=message;
	}
	
	public GameEvent getEvent() {
		return message;
	}

	public void setEvent(GameEvent message) {
		this.message = message;
	}
}
