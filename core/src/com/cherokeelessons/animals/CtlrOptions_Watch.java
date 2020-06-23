package com.cherokeelessons.animals;

import com.badlogic.gdx.controllers.ControllerListener;
import com.cherokeelessons.common.GamepadMap;

public class CtlrOptions_Watch extends GamepadAdapter<ScreenOptionsMenu> {

	public CtlrOptions_Watch(final ScreenOptionsMenu menu) {
		super(menu);
	}

	@Override
	public ControllerListener factoryControllerListener(final GamepadMap map, final ScreenOptionsMenu menu) {
		return new CtlrOptions(map, menu);
	}
}
