package com.cherokeelessons.animals;

import com.badlogic.gdx.controllers.ControllerListener;
import com.cherokeelessons.common.GamepadMap;

public class CtlrMainMenu_Watch extends GamepadAdapter<ScreenMainMenu> {

	public CtlrMainMenu_Watch(final ScreenMainMenu menu) {
		super(menu);
	}

	@Override
	public ControllerListener factoryControllerListener(final GamepadMap map, final ScreenMainMenu menu) {
		return new CtlrMainMenu(map, menu);
	}

}
