package com.cherokeelessons.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.common.GamepadMap;

public class CtlrLevelComplete implements ControllerListener {

	private static void log(String msg) {
		Gdx.app.log(CtlrLevelComplete.class.getCanonicalName(), msg);
	}

	// private IntFloatMap axisMovedMap = new IntFloatMap();
	private float deadzone = 0.7f;

	private PovDirection lastDirection = PovDirection.center;

	private GamepadMap map;

	private ScreenLevelComplete menu;

	public CtlrLevelComplete(GamepadMap map, ScreenLevelComplete menu) {
		Gdx.app.log("ControllerMainMenu",
				"Initializing with map: " + map.model.name());
		this.map = map;
		this.menu = menu;
	}

	@Override
	public boolean accelerometerMoved(Controller controller,
			int accelerometerCode, Vector3 value) {
		log("accelerometerMoved: " + controller.getName() + ", "
				+ accelerometerCode + ", " + value);
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		if (axisCode == map.AXIS_LEFT_TRIGGER
				|| axisCode == map.AXIS_RIGHT_TRIGGER) {
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
			if (value >= -deadzone
					&& value <= deadzone
					&& (lastDirection.equals(PovDirection.north) || lastDirection
							.equals(PovDirection.south))) {
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
			if (value >= -deadzone
					&& value <= deadzone
					&& (lastDirection.equals(PovDirection.east) || lastDirection
							.equals(PovDirection.west))) {
				lastDirection = PovDirection.center;
				return povMoved(controller, 0, PovDirection.center);
			}
		}
		return false;
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		if (buttonCode == map.BUTTON_BACK || buttonCode == map.BUTTON_B) {
			menu.game.gameEvent(GameEvent.MainMenu);
			return true;
		}
		if (buttonCode == map.BUTTON_X) {
			menu.game.gameEvent(GameEvent.ShowGameBoard);
			return true;
		}
		if (buttonCode == map.BUTTON_A) {
			menu.game.gameEvent(GameEvent.NewGame);
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
	public boolean buttonUp(Controller controller, int buttonCode) {
		log("buttonUp: " + controller.getName() + ", " + buttonCode);
		return false;
	}

	@Override
	public void connected(Controller controller) {
		log("connected: " + controller.getName());
	}

	@Override
	public void disconnected(Controller controller) {
		log("disconnected: " + controller.getName());
	}

	@Override
	public boolean povMoved(Controller controller, int povCode,
			PovDirection value) {
		log("povModed: " + controller.getName() + ", " + povCode + ", "
				+ value.name());
		switch (value) {
		case north:
			// menu.hud_moveNorth();
			break;
		case south:
			// menu.hud_moveSouth();
			break;
		case east:
			// menu.hud_moveRight();
			break;
		case west:
			// menu.hud_moveLeft();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode,
			boolean value) {
		log("xSliderMoved: " + controller.getName() + ", " + sliderCode + ", "
				+ value);
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode,
			boolean value) {
		log("ySliderMoved: " + controller.getName() + ", " + sliderCode + ", "
				+ value);
		return false;
	}

}
