package com.cherokeelessons.animals;

import com.badlogic.gdx.controllers.ControllerListener;
import com.cherokeelessons.common.GamepadMap;

public class CtlrLevelComplete_Watch extends GamepadAdapter<ScreenLevelComplete> {

	public CtlrLevelComplete_Watch(final ScreenLevelComplete menu) {
		super(menu);
	}

	@Override
	public ControllerListener factoryControllerListener(final GamepadMap map, final ScreenLevelComplete menu) {
		return new CtlrLevelComplete(map, menu);
	}

}
