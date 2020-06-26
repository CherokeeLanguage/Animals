package com.cherokeelessons.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.common.GamepadMap;

public class CtlrGamePlay implements ControllerListener {

	private static void log(final String msg) {
		Gdx.app.log(CtlrGamePlay.class.getCanonicalName(), msg);
	}

	private final float deadzone = 0.7f;

	private PovDirection lastDirection = PovDirection.center;

	private final GamepadMap map;

	private final ScreenGameplay gameboard;

	public CtlrGamePlay(final GamepadMap map, final ScreenGameplay gameboard) {
		this.map = map;
		this.gameboard = gameboard;
	}

	@Override
	public boolean accelerometerMoved(final Controller controller, final int accelerometerCode, final Vector3 value) {
		log("accelerometerMoved: " + controller.getName() + ", " + accelerometerCode + ", " + value);
		return false;
	}

	@Override
	public boolean axisMoved(final Controller controller, final int axisCode, final float value) {
		if (gameboard.isPaused()) {
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
		Gdx.app.log(this.getClass().toGenericString(), "button down: "+buttonCode);
		if (gameboard.isPaused()) {
			if (buttonCode == map.BUTTON_A) {
				gameboard.setPaused(false);
				return true;
			}
			if (buttonCode == map.BUTTON_BACK || buttonCode == map.BUTTON_B) {
				gameboard.game.gameEvent(GameEvent.EXIT_SCREEN);
				return true;
			}
			return true;
		}
		if (buttonCode == map.BUTTON_Y) {
			gameboard.setPaused(true);
			return true;
		}
		if (buttonCode == map.BUTTON_X) {
			return true;
		}
		if (buttonCode == map.BUTTON_A) {
			gameboard.hud_select();
			return true;
		}
		if (buttonCode == map.BUTTON_DPAD_UP) {
			return povMoved(controller, 0, PovDirection.north);
		}
		if (buttonCode == map.BUTTON_DPAD_DOWN) {
			return povMoved(controller, 0, PovDirection.south);
		}
		if (buttonCode == map.BUTTON_DPAD_RIGHT) {
			return povMoved(controller, 0, PovDirection.east);
		}
		if (buttonCode == map.BUTTON_DPAD_LEFT) {
			return povMoved(controller, 0, PovDirection.west);
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
		if (gameboard.isPaused()) {
			return false;
		}
		switch (value) {
		case north:
			gameboard.hud_moveNorth();
			break;
		case south:
			gameboard.hud_moveSouth();
			break;
		case east:
			gameboard.hud_moveRight();
			break;
		case west:
			gameboard.hud_moveLeft();
			break;
		case center:
		case northEast:
		case northWest:
		case southEast:
		case southWest:
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
