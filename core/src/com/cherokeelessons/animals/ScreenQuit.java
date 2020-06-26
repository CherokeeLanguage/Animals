package com.cherokeelessons.animals;

public class ScreenQuit extends GameScreen {
	public ScreenQuit(final CherokeeAnimals game) {
		super(game);
	}
	@Override
	protected boolean useBackdrop() {
		return false;
	}
	@Override
	public boolean dpad(int keyCode) {
		//ignore
		return false;
	}
}
