package com.cherokeelessons.animals;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerListener;
import com.cherokeelessons.common.GamepadMap;
import com.cherokeelessons.common.GamepadMap.Model;

public abstract class GamepadAdapter<S extends Screen> extends ControllerAdapter {

	protected final S menu;
	protected final List<ControllerListener> listeners = new ArrayList<>();
	protected final GamepadMap map_ouya = new GamepadMap(Model.Ouya);
	protected final GamepadMap map_xbox = new GamepadMap(Model.Xbox);

	public GamepadAdapter(final S menu) {
		this.menu = menu;
	}

	@Override
	public void connected(final Controller controller) {
		super.connected(controller);
		final String name = controller.getName().toLowerCase();
		ControllerListener listener;
		if (name.contains("xbox") || name.contains("x-box") || name.contains("360")) {
			listener = factoryControllerListener(map_xbox, menu);
			controller.addListener(listener);
			listeners.add(listener);
			return;
		}
		// fallback
		if (ApplicationType.Android.equals(Gdx.app.getType())) {
			final GamepadMap map_atv = new GamepadMap(Model.AndroidTv);
			listener = factoryControllerListener(map_atv, menu);
		} else {
			listener = factoryControllerListener(map_xbox, menu);
		}
		controller.addListener(listener);
		listeners.add(listener);
	}

	@Override
	public void disconnected(final Controller controller) {
		super.disconnected(controller);
		for (final ControllerListener listener : listeners) {
			controller.removeListener(listener);
		}
	}

	public abstract ControllerListener factoryControllerListener(GamepadMap map, S menu);

	public boolean hasControllers() {
		return listeners.size() > 0;
	}

}
