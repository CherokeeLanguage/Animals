package com.cherokeelessons.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.common.GamepadMap;

public class CtlrMainMenu implements ControllerListener {

	private PovDirection lastDirection = PovDirection.center;

	private final GamepadMap map;

	private final ScreenMainMenu menu;

	public CtlrMainMenu(final GamepadMap map, final ScreenMainMenu menu) {
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
		log("axisMoved: " + controller.getName() + " axis=" + axisCode + ", value=" + value);

		if (axisCode == map.AXIS_LEFT_TRIGGER || axisCode == map.AXIS_RIGHT_TRIGGER) {
			return false;
		}

		if (axisCode == map.AXIS_LEFT_Y) {
			if (value <= -.5 && !lastDirection.equals(PovDirection.north)) {
				lastDirection = PovDirection.north;
				return povMoved(controller, 0, PovDirection.north);
			}
			if (value >= .5 && !lastDirection.equals(PovDirection.south)) {
				lastDirection = PovDirection.south;
				return povMoved(controller, 0, PovDirection.south);
			}
			if (value >= -.5 && value <= .5 && !lastDirection.equals(PovDirection.center)) {
				lastDirection = PovDirection.center;
				return povMoved(controller, 0, PovDirection.center);
			}
		}
		return false;
	}

	@Override
	public boolean buttonDown(final Controller controller, final int buttonCode) {
		log("buttonDown: " + controller.getName() + " buttonCode=" + buttonCode);
		bswitch: {
			if (buttonCode == map.BUTTON_A) {
				menu.hud_select();
				break bswitch;
			}
			if (buttonCode == map.BUTTON_MENU) {
				menu.game.gameEvent(GameEvent.EXIT_SCREEN);
				break bswitch;
			}
			if (buttonCode == map.BUTTON_BACK || buttonCode == map.BUTTON_B) {
				if (menu.getSelected_btn() == menu.quitButton) {
					menu.hud_select();
				}
				while (menu.getSelected_btn() != menu.quitButton) {
					menu.hud_moveSouth();
				}
				break bswitch;
			}
			if (buttonCode == map.BUTTON_DPAD_DOWN) {
				return povMoved(controller, 0, PovDirection.south);
			}
			if (buttonCode == map.BUTTON_DPAD_UP) {
				return povMoved(controller, 0, PovDirection.north);
			}
		}
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

	private void log(final String msg) {
		Gdx.app.log(this.getClass().getSimpleName(), msg);
	}

	@Override
	public boolean povMoved(final Controller controller, final int povCode, final PovDirection value) {
		log("povMoved: " + controller.getName() + " povCode=" + povCode);
		switch (value) {
		case north:
		case northEast:
		case northWest:
			menu.hud_moveNorth();
			break;
		case south:
		case southEast:
		case southWest:
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
