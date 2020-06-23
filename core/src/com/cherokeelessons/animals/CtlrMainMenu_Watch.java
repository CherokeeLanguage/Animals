package com.cherokeelessons.animals;

import com.badlogic.gdx.controllers.ControllerListener;
import com.cherokeelessons.common.GamepadMap;

public class CtlrMainMenu_Watch extends GamepadAdapter<ScreenMainMenu> {

	public CtlrMainMenu_Watch(ScreenMainMenu menu) {
		super(menu);
	}

	@Override
	public ControllerListener factoryControllerListener(GamepadMap map, ScreenMainMenu menu) {
		return new CtlrMainMenu(map, menu);
	}

}
