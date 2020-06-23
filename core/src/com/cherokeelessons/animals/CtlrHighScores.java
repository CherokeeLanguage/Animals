package com.cherokeelessons.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.common.GamepadMap;

public class CtlrHighScores implements ControllerListener {

	private static void log(final String msg) {
		Gdx.app.log(CtlrHighScores.class.getCanonicalName(), msg);
	}

	private final float deadzone = 0.7f;

	private PovDirection lastDirection = PovDirection.center;

	private final GamepadMap map;

	private final ScreenHighScores menu;

	public CtlrHighScores(final GamepadMap map, final ScreenHighScores menu) {
		this.map = map;
		this.menu = menu;
	}

	@Override
	public boolean accelerometerMoved(final Controller controller, final int accelerometerCode, final Vector3 value) {
		log("accelerometerMoved: " + controller.getName() + ", " + accelerometerCode + ", " + value);
		return false;
	}

	@Override
	public boolean axisMoved(final Controller controller, final int axisCode, final float value) {
		if (menu.isPaused()) {
			return false;
		}
		if (axisCode == map.AXIS_LEFT_TRIGGER || axisCode == map.AXIS_RIGHT_TRIGGER) {
			return false;
		}

		if (axisCode == map.AXIS_LEFT_Y) {
			if (value <= -deadzone && !lastDirection.equals(PovDirection.north)) {
				lastDirection = PovDirection.north;
				return povMoved(controller, 0, PovDirection.north);
			}
			if (value >= deadzone && !lastDirection.equals(PovDirection.south)) {
				lastDirection = PovDirection.south;
				return povMoved(controller, 0, PovDirection.south);
			}
			if (value >= -deadzone && value <= deadzone
					&& (lastDirection.equals(PovDirection.north) || lastDirection.equals(PovDirection.south))) {
				lastDirection = PovDirection.center;
				return povMoved(controller, 0, PovDirection.center);
			}
		}
		if (axisCode == map.AXIS_LEFT_X) {
			if (value <= -deadzone && !lastDirection.equals(PovDirection.west)) {
				lastDirection = PovDirection.west;
				return povMoved(controller, 0, PovDirection.west);
			}
			if (value >= deadzone && !lastDirection.equals(PovDirection.east)) {
				lastDirection = PovDirection.east;
				return povMoved(controller, 0, PovDirection.east);
			}
			if (value >= -deadzone && value <= deadzone
					&& (lastDirection.equals(PovDirection.east) || lastDirection.equals(PovDirection.west))) {
				lastDirection = PovDirection.center;
				return povMoved(controller, 0, PovDirection.center);
			}
		}
		return false;
	}

	@Override
	public boolean buttonDown(final Controller controller, final int buttonCode) {
		if (menu.isPaused()) {
			if (buttonCode == map.BUTTON_A) {
				menu.setPaused(false);
				return true;
			}
			if (buttonCode == map.BUTTON_BACK || buttonCode == map.BUTTON_B) {
				menu.setPaused(false);
				return true;
			}
			return true;
		}
		if (buttonCode == map.BUTTON_BACK || buttonCode == map.BUTTON_B) {
			menu.game.gameEvent(GameEvent.Done);
			return true;
		}
		if (buttonCode == map.BUTTON_MENU) {
			menu.game.gameEvent(GameEvent.Menu);
		}
		if (buttonCode == map.BUTTON_DPAD_UP) {
			return povMoved(controller, 0, PovDirection.north);
		}
		if (buttonCode == map.BUTTON_DPAD_DOWN) {
			return povMoved(controller, 0, PovDirection.south);
		}

		log("buttonDown: " + controller.getName() + ", " + buttonCode);
		return true;
	}

	@Override
	public boolean buttonUp(final Controller controller, final int buttonCode) {
		return false;
	}

	@Override
	public void connected(final Controller controller) {
		log("connected: " + controller.getName());
	}

	@Override
	public void disconnected(final Controller controller) {
		log("disconnected: " + controller.getName());
	}

	@Override
	public boolean povMoved(final Controller controller, final int povCode, final PovDirection value) {
		if (menu.isPaused()) {
			return false;
		}
		switch (value) {
		case north:
			menu.hud_moveNorth();
			break;
		case south:
			menu.hud_moveSouth();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean xSliderMoved(final Controller controller, final int sliderCode, final boolean value) {
		log("xSliderMoved: " + controller.getName() + ", " + sliderCode + ", " + value);
		return false;
	}

	@Override
	public boolean ySliderMoved(final Controller controller, final int sliderCode, final boolean value) {
		log("ySliderMoved: " + controller.getName() + ", " + sliderCode + ", " + value);
		return false;
	}

}
