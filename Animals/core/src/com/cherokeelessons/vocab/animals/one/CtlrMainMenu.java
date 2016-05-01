package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.cherokeelessons.common.GamepadMap;
import com.cherokeelessons.vocab.animals.one.enums.GameEvent;

public class CtlrMainMenu implements ControllerListener {

	private void log(String msg) {
		Gdx.app.log(this.getClass().getSimpleName(), msg);
	}

	private PovDirection lastDirection = PovDirection.center;

	private GamepadMap map;

	private ScreenMainMenu menu;

	public CtlrMainMenu(GamepadMap map, ScreenMainMenu menu) {
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
		log("axisMoved: "+controller.getName()+" axis="+axisCode+", value="+value);
		
		if (axisCode == map.AXIS_LEFT_TRIGGER
				|| axisCode == map.AXIS_RIGHT_TRIGGER) {
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
			if (value >= -.5 && value <= .5
					&& !lastDirection.equals(PovDirection.center)) {
				lastDirection = PovDirection.center;
				return povMoved(controller, 0, PovDirection.center);
			}
		}
		return false;
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		log("buttonDown: "+controller.getName()+" buttonCode="+buttonCode);
		bswitch: {			
			if (buttonCode == map.BUTTON_A) {
				menu.hud_select();
				break bswitch;
			}
			if (buttonCode == map.BUTTON_MENU) {
				menu.game.gameEvent(GameEvent.ShowOptions);
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
	public boolean buttonUp(Controller controller, int buttonCode) {

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
		log("povMoved: "+controller.getName()+" povCode="+povCode);
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
