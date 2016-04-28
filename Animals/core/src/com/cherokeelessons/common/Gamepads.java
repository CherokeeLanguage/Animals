package com.cherokeelessons.common;

import java.util.HashSet;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Array;

public class Gamepads extends Controllers {

	final public static HashSet<ControllerListener> listenerList = new HashSet<ControllerListener>();

	public static void addListener(ControllerListener listener) {
		if (listenerList.contains(listener)) {
			return;
		}
		listenerList.add(listener);
		Controllers.addListener(listener);
	}

	public static void clearListeners() {
		for (ControllerListener listener : listenerList) {
			Controllers.removeListener(listener);
		}
		listenerList.clear();
	}

	public static Array<Controller> getControllers() {
		return Controllers.getControllers();
	}

	public static void removeListener(ControllerListener listener) {
		Controllers.removeListener(listener);
		listenerList.remove(listener);
	}

	public Gamepads() {
		super();
	}

}
