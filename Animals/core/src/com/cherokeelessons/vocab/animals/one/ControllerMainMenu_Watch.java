package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.common.GamepadMap;
import com.cherokeelessons.common.GamepadMap.Model;

public class ControllerMainMenu_Watch extends ControllerAdapter {

	final private static Array<ControllerMainMenu> listeners = new Array<ControllerMainMenu>();

	GamepadMap map_ouya = new GamepadMap(Model.Ouya);

	GamepadMap map_xbox = new GamepadMap(Model.Xbox);

	private ScreenMainMenu menu;

	public ControllerMainMenu_Watch(ScreenMainMenu menu) {
		this.menu = menu;
	}

	@Override
	public void connected(Controller controller) {
		super.connected(controller);
		String name = controller.getName().toLowerCase();
		ControllerMainMenu listener;
		if (name.contains("ouya")) {
			listener = new ControllerMainMenu(map_ouya, menu);
			controller.addListener(listener);
			listeners.add(listener);
			return;
		}
		if (name.contains("xbox") || name.contains("x-box")
				|| name.contains("360")) {
			listener = new ControllerMainMenu(map_xbox, menu);
			controller.addListener(listener);
			listeners.add(listener);
			return;
		}
		// fallback
		listener = new ControllerMainMenu(map_ouya, menu);
		controller.addListener(listener);
		listeners.add(listener);
	}

	@Override
	public void disconnected(Controller controller) {
		super.disconnected(controller);
		for (ControllerMainMenu listener : listeners) {
			controller.removeListener(listener);
		}
	}

}
