package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.common.GamepadMap;
import com.cherokeelessons.common.GamepadMap.Model;

public class ControllerLevelSelect_Watch extends ControllerAdapter {

	final private static Array<ControllerLevelSelect> listeners = new Array<ControllerLevelSelect>();

	GamepadMap map_ouya = new GamepadMap(Model.Ouya);

	GamepadMap map_xbox = new GamepadMap(Model.Xbox);

	private ScreenLevelSelect menu;

	public ControllerLevelSelect_Watch(ScreenLevelSelect menu) {
		this.menu = menu;
	}

	@Override
	public void connected(Controller controller) {
		super.connected(controller);
		String name = controller.getName().toLowerCase();
		ControllerLevelSelect listener;
		if (name.contains("ouya")) {
			listener = new ControllerLevelSelect(map_ouya, menu);
			controller.addListener(listener);
			listeners.add(listener);
			return;
		}
		if (name.contains("xbox") || name.contains("x-box")
				|| name.contains("360")) {
			listener = new ControllerLevelSelect(map_xbox, menu);
			controller.addListener(listener);
			listeners.add(listener);
			return;
		}
		// fallback
		listener = new ControllerLevelSelect(map_ouya, menu);
		controller.addListener(listener);
		listeners.add(listener);
	}

	@Override
	public void disconnected(Controller controller) {
		super.disconnected(controller);
		for (ControllerLevelSelect listener : listeners) {
			controller.removeListener(listener);
		}
	}

}
