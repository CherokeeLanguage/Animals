package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.controllers.ControllerListener;
import com.cherokeelessons.common.GamepadMap;

public class CtlrLevelSelect_Watch extends GamepadAdapter<ScreenLevelSelect> {

	public CtlrLevelSelect_Watch(ScreenLevelSelect menu) {
		super(menu);
	}

	@Override
	public ControllerListener factoryControllerListener(GamepadMap map, ScreenLevelSelect menu) {
		return new CtlrLevelSelect(map, menu);
	}
	
}
