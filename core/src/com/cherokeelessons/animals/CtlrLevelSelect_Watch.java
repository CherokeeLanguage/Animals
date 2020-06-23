package com.cherokeelessons.animals;

import com.badlogic.gdx.controllers.ControllerListener;
import com.cherokeelessons.common.GamepadMap;

public class CtlrLevelSelect_Watch extends GamepadAdapter<ScreenLevelSelect> {

	public CtlrLevelSelect_Watch(final ScreenLevelSelect menu) {
		super(menu);
	}

	@Override
	public ControllerListener factoryControllerListener(final GamepadMap map, final ScreenLevelSelect menu) {
		return new CtlrLevelSelect(map, menu);
	}

}
