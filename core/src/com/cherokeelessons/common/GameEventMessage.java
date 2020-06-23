package com.cherokeelessons.common;

import com.cherokeelessons.animals.enums.GameEvent;

public class GameEventMessage {
	private GameEvent message = GameEvent.NoEvent;

	public GameEventMessage() {
	}

	public GameEventMessage(final GameEvent message) {
		this.message = message;
	}

	public GameEvent getEvent() {
		return message;
	}

	public void setEvent(final GameEvent message) {
		this.message = message;
	}
}
