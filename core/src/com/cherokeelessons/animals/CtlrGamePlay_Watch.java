package com.cherokeelessons.animals;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.common.GamepadMap;
import com.cherokeelessons.common.GamepadMap.Model;

public class CtlrGamePlay_Watch extends ControllerAdapter {

	final private static Array<CtlrGamePlay> listeners = new Array<>();

	GamepadMap map_xbox = new GamepadMap(Model.Xbox);

	private final ScreenGameplay menu;

	public CtlrGamePlay_Watch(final ScreenGameplay menu) {
		this.menu = menu;
	}

	@Override
	public void connected(final Controller controller) {
		super.connected(controller);
		final String name = controller.getName().toLowerCase();
		CtlrGamePlay listener;
		if (name.contains("xbox") || name.contains("x-box") || name.contains("360")) {
			listener = new CtlrGamePlay(map_xbox, menu);
			controller.addListener(listener);
			listeners.add(listener);
			return;
		}
		// fallback
		if (ApplicationType.Android.equals(Gdx.app.getType())) {
			final GamepadMap map_atv = new GamepadMap(Model.AndroidTv);
			listener = new CtlrGamePlay(map_atv, menu);
		} else {
			listener = new CtlrGamePlay(map_xbox, menu);
		}
		controller.addListener(listener);
		listeners.add(listener);
	}

	@Override
	public void disconnected(final Controller controller) {
		super.disconnected(controller);
		for (final CtlrGamePlay listener : listeners) {
			controller.removeListener(listener);
		}
	}

}
