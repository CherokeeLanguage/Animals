package com.cherokeelessons.vocab.animals.one;

public class GameEvent {

	public enum EventList {
		DoScroller, FirstRun, GoBack, GoMenu, InitDone, LevelComplete, LevelSelect, libGdx, NoEvent, Pause, QuitGame, ShowGameBoard, ShowMainMenu, ShowOptions, Training, ShowInstructions, ShowCredits, ShowLeaderBoard
	}

	private EventList message = EventList.NoEvent;

	public EventList getEvent() {
		return message;
	}

	public void setEvent(EventList message) {
		this.message = message;
	}
}
