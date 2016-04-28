package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.common.GamepadMap;
import com.cherokeelessons.common.GamepadMap.Model;

public class ControllerOptions_Watch extends ControllerAdapter {

	final private static Array<ControllerOptions> listeners = new Array<ControllerOptions>();

	GamepadMap map_ouya = new GamepadMap(Model.Ouya);

	GamepadMap map_xbox = new GamepadMap(Model.Xbox);

	private ScreenOptionsMenu menu;

	public ControllerOptions_Watch(ScreenOptionsMenu menu) {
		this.menu = menu;
	}

	@Override
	public void connected(Controller controller) {
		super.connected(controller);
		String name = controller.getName().toLowerCase();
		System.out.println("Connected: " + name);
		ControllerOptions listener;
		if (name.contains("ouya")) {
			listener = new ControllerOptions(map_ouya, menu);
			controller.addListener(listener);
			listeners.add(listener);
			return;
		}
		if (name.contains("xbox") || name.contains("x-box")
				|| name.contains("360")) {
			listener = new ControllerOptions(map_xbox, menu);
			controller.addListener(listener);
			listeners.add(listener);
			return;
		}
		// fallback
		listener = new ControllerOptions(map_ouya, menu);
		controller.addListener(listener);
		listeners.add(listener);
	}

	@Override
	public void disconnected(Controller controller) {
		super.disconnected(controller);
		for (ControllerOptions listener : listeners) {
			controller.removeListener(listener);
		}
		String name = controller.getName().toLowerCase();
		System.out.println("Disconnected: " + name);
	}

}
