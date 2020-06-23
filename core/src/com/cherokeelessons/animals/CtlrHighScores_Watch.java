package com.cherokeelessons.animals;

import com.badlogic.gdx.controllers.ControllerListener;
import com.cherokeelessons.common.GamepadMap;

public class CtlrHighScores_Watch extends GamepadAdapter<ScreenHighScores> {
	public CtlrHighScores_Watch(final ScreenHighScores menu) {
		super(menu);
	}

	@Override
	public ControllerListener factoryControllerListener(final GamepadMap map, final ScreenHighScores menu) {
		return new CtlrHighScores(map, menu);
	}
}
