package com.cherokeelessons.vocab.animals.one;

import java.lang.reflect.Field;

/**
 * Game Controller Mapping Object. Attempts to self adjust values based on OS
 * running on.
 * 
 * Usage:
 * 
 * GamepadMap ouya_map = new GamepadMap(Model.Ouya); GamepadMap xbox_map = new
 * GamepadMap(Model.Xbox);
 * 
 * A value of "-1" for an axis or button indicates no matching event is
 * generated. This is especially an issue for the DPAD which may show up in one
 * of THREE ways for the Xbox as POV, BUTTONS, or AXIS, depending on OS!
 * Additionally, the trigger buttons appear useless when using Windows, both are
 * mapped to the same axis number!
 * 
 * The L1 and L2 values are not the same as those found in
 * com.badlogic.gdx.controllers.mappings.Ouya!
 * 
 * L1 is supposed to be the bumper and L2 is supposed to be the trigger.
 * 
 * It is also ODD that the OUYA insists on generating TWO events for some
 * buttons into differing event handlers! The triggers show up BOTH as AXIS and
 * BUTTONS!
 * 
 * @author mjoyner
 * @version 0.0.1
 * 
 *          Public Domain
 */

public class GamepadMap {
	public static enum Model {
		Ouya, Xbox;
	}

	public static class OS {
		public static enum Platform {
			Android, Linux, Mac, Other, Ouya, Solaris, Unix, Windows;
		}

		final public static String name;
		final public static Platform platform;
		static {
			name = System.getProperty("os.name").toLowerCase();
			System.out.println("os.name: " + name);
			do {
				if (System.getProperty("java.vm.name").equalsIgnoreCase(
						"Dalvik")) {
					/*
					 * Ouya check first (or it shows up as Android)
					 */
					String device = null;
					try {
						Class<?> buildClass = Class.forName("android.os.Build");
						Field deviceField = buildClass
								.getDeclaredField("DEVICE");
						Object o = deviceField.get(null);
						if (o != null) {
							device = o.toString().toLowerCase();
							System.out.println("Device: " + device);
						}
					} catch (Exception e) {
					}
					if (device != null) {
						if (device.contains("ouya")) {
							platform = Platform.Ouya;
							break;
						}
					}
					platform = Platform.Android;
					break;
				}
				if (name.contains("linux")) {
					platform = Platform.Linux;
					break;
				}
				if (name.contains("win")) {
					platform = Platform.Windows;
					break;
				}
				if (name.contains("mac")) {
					platform = Platform.Mac;
					break;
				}
				if (name.contains("nix") || name.contains("nux")
						|| name.contains("aix")) {
					platform = Platform.Unix;
					break;
				}
				if (name.contains("sunos")) {
					platform = Platform.Solaris;
					break;
				}
				platform = Platform.Other;
			} while (false);
		}
	}

	public final int AXIS_DPAD_X;
	public final int AXIS_DPAD_Y;
	public final int AXIS_LEFT_TRIGGER;
	public final int AXIS_LEFT_X;
	public final int AXIS_LEFT_Y;
	public final int AXIS_RIGHT_TRIGGER;
	public final int AXIS_RIGHT_X;
	public final int AXIS_RIGHT_Y;
	public final int BUTTON_A;
	public final int BUTTON_BACK;
	public final int BUTTON_DPAD_DOWN;
	public final int BUTTON_DPAD_LEFT;
	public final int BUTTON_DPAD_RIGHT;
	public final int BUTTON_DPAD_UP;
	public final int BUTTON_L1 /* bumper */;
	public final int BUTTON_L2 /* trigger */;
	public final int BUTTON_L3 /* joystick */;
	public final int BUTTON_MENU;
	public final int BUTTON_O;
	public final int BUTTON_R1 /* bumper */;
	public final int BUTTON_R2 /* trigger */;
	public final int BUTTON_R3 /* joystick */;
	public final int BUTTON_START;
	public final int BUTTON_U;
	public final int BUTTON_Y;
	public final boolean DPAD_IS_AXIS;
	public final boolean DPAD_IS_BUTTON;
	public final boolean DPAD_IS_POV;

	public final Model model;

	public GamepadMap(Model model) {
		this.model = model;
		switch (model) {
		case Ouya:
		default:
			do {
				if (OS.platform.equals(OS.Platform.Linux)) {
					BUTTON_O = 3;
					BUTTON_U = 4;
					BUTTON_Y = 5;
					BUTTON_A = 6;
					BUTTON_MENU = 17;
					DPAD_IS_POV = false;
					DPAD_IS_BUTTON = true;
					BUTTON_DPAD_UP = 11;
					BUTTON_DPAD_DOWN = 12;
					BUTTON_DPAD_RIGHT = 14;
					BUTTON_DPAD_LEFT = 13;
					DPAD_IS_AXIS = false;
					AXIS_DPAD_X = -1;
					AXIS_DPAD_Y = -1;
					BUTTON_L1 /* bumper */= 7;
					BUTTON_L2 /* trigger */= 15;
					BUTTON_L3 /* joystick */= 9;
					BUTTON_R1 /* bumper */= 8;
					BUTTON_R2 /* trigger */= 16;
					BUTTON_R3 /* joystick */= 10;
					AXIS_LEFT_X = 0;
					AXIS_LEFT_Y = 1;
					AXIS_LEFT_TRIGGER = 2;
					AXIS_RIGHT_X = 3;
					AXIS_RIGHT_Y = 4;
					AXIS_RIGHT_TRIGGER = 5;
					BUTTON_BACK = -1;
					BUTTON_START = 18;
					break;
				}
				if (OS.platform.equals(OS.Platform.Windows)) {
					BUTTON_O = 0;
					BUTTON_U = 1;
					BUTTON_Y = 2;
					BUTTON_A = 3;
					BUTTON_MENU = 14;
					DPAD_IS_POV = false;
					DPAD_IS_BUTTON = true;
					BUTTON_DPAD_UP = 8;
					BUTTON_DPAD_DOWN = 9;
					BUTTON_DPAD_RIGHT = 11;
					BUTTON_DPAD_LEFT = 10;
					DPAD_IS_AXIS = false;
					AXIS_DPAD_X = -1;
					AXIS_DPAD_Y = -1;
					BUTTON_L1 /* bumper */= 4;
					BUTTON_L2 /* trigger */= -1;
					BUTTON_L3 /* joystick */= 6;
					BUTTON_R1 /* bumper */= 5;
					BUTTON_R2 /* trigger */= -1;
					BUTTON_R3 /* joystick */= 7;
					AXIS_LEFT_X = 1;
					AXIS_LEFT_Y = 0;
					AXIS_LEFT_TRIGGER = 4;
					AXIS_RIGHT_X = 3;
					AXIS_RIGHT_Y = 2;
					AXIS_RIGHT_TRIGGER = 5;
					BUTTON_BACK = -1;
					BUTTON_START = 15;
					break;
				}
				if (OS.platform.equals(OS.Platform.Ouya)) {
					BUTTON_O = 96;
					BUTTON_U = 99;
					BUTTON_Y = 100;
					BUTTON_A = 97;
					BUTTON_MENU = 82;
					DPAD_IS_POV = false;
					DPAD_IS_BUTTON = true;
					BUTTON_DPAD_UP = 19;
					BUTTON_DPAD_DOWN = 20;
					BUTTON_DPAD_RIGHT = 22;
					BUTTON_DPAD_LEFT = 21;
					DPAD_IS_AXIS = false;
					AXIS_DPAD_X = -1;
					AXIS_DPAD_Y = -1;
					BUTTON_L1 /* bumper */= 102;
					BUTTON_L2 /* trigger */= 104;
					BUTTON_L3 /* joystick */= 106;
					BUTTON_R1 /* bumper */= 103;
					BUTTON_R2 /* trigger */= 105;
					BUTTON_R3 /* joystick */= 107;
					AXIS_LEFT_X = 0;
					AXIS_LEFT_Y = 1;
					AXIS_LEFT_TRIGGER = 2;
					AXIS_RIGHT_X = 3;
					AXIS_RIGHT_Y = 4;
					AXIS_RIGHT_TRIGGER = 5;
					BUTTON_BACK = -1;
					BUTTON_START = -1;
					break;
				}
				if (OS.platform.equals(OS.Platform.Android)) {
					BUTTON_O = 96;
					BUTTON_U = 97;
					BUTTON_Y = 98;
					BUTTON_A = 99;
					BUTTON_MENU = 107;
					DPAD_IS_POV = false;
					DPAD_IS_BUTTON = true;
					BUTTON_DPAD_UP = 104;
					BUTTON_DPAD_DOWN = 105;
					BUTTON_DPAD_RIGHT = 108;
					BUTTON_DPAD_LEFT = 109;
					DPAD_IS_AXIS = false;
					AXIS_DPAD_X = -1;
					AXIS_DPAD_Y = -1;
					BUTTON_L1 /* bumper */= 100;
					BUTTON_L2 /* trigger */= 110;
					BUTTON_L3 /* joystick */= 102;
					BUTTON_R1 /* bumper */= 101;
					BUTTON_R2 /* trigger */= 106;
					BUTTON_R3 /* joystick */= 103;
					AXIS_LEFT_X = 0;
					AXIS_LEFT_Y = 1;
					AXIS_LEFT_TRIGGER = 2;
					AXIS_RIGHT_X = 3;
					AXIS_RIGHT_Y = 4;
					AXIS_RIGHT_TRIGGER = 5;
					BUTTON_BACK = -1;
					BUTTON_START = 0;
					break;
				}
				/* fallback values */
				BUTTON_O = 3;
				BUTTON_U = 4;
				BUTTON_Y = 5;
				BUTTON_A = 6;
				BUTTON_MENU = 17;
				DPAD_IS_POV = false;
				DPAD_IS_BUTTON = true;
				BUTTON_DPAD_UP = 11;
				BUTTON_DPAD_DOWN = 12;
				BUTTON_DPAD_RIGHT = 14;
				BUTTON_DPAD_LEFT = 13;
				DPAD_IS_AXIS = false;
				AXIS_DPAD_X = -1;
				AXIS_DPAD_Y = -1;
				BUTTON_L1 /* bumper */= 7;
				BUTTON_L2 /* trigger */= 15;
				BUTTON_L3 /* joystick */= 9;
				BUTTON_R1 /* bumper */= 8;
				BUTTON_R2 /* trigger */= 16;
				BUTTON_R3 /* joystick */= 10;
				AXIS_LEFT_X = 0;
				AXIS_LEFT_Y = 1;
				AXIS_LEFT_TRIGGER = 2;
				AXIS_RIGHT_X = 3;
				AXIS_RIGHT_Y = 4;
				AXIS_RIGHT_TRIGGER = 5;
				BUTTON_BACK = -1;
				BUTTON_START = 18;
			} while (false);
			break;
		case Xbox:
			do {
				if (OS.platform.equals(OS.Platform.Linux)) {
					BUTTON_O = 0;
					BUTTON_U = 2;
					BUTTON_Y = 3;
					BUTTON_A = 1;
					BUTTON_MENU = 8;
					DPAD_IS_POV = true;
					DPAD_IS_BUTTON = false;
					BUTTON_DPAD_UP = -1;
					BUTTON_DPAD_DOWN = -1;
					BUTTON_DPAD_RIGHT = -1;
					BUTTON_DPAD_LEFT = -1;
					DPAD_IS_AXIS = false;
					AXIS_DPAD_X = -1;
					AXIS_DPAD_Y = -1;
					BUTTON_L1 /* bumper */= 4;
					BUTTON_L2 /* trigger */= -1;
					BUTTON_L3 /* joystick */= 9;
					BUTTON_R1 /* bumper */= 5;
					BUTTON_R2 /* trigger */= -1;
					BUTTON_R3 /* joystick */= 10;
					AXIS_LEFT_X = 0;
					AXIS_LEFT_Y = 1;
					AXIS_LEFT_TRIGGER = 2;
					AXIS_RIGHT_X = 3;
					AXIS_RIGHT_Y = 4;
					AXIS_RIGHT_TRIGGER = 5;
					BUTTON_BACK = 6;
					BUTTON_START = 7;
					break;
				}
				if (OS.platform.equals(OS.Platform.Windows)) {
					BUTTON_O = 0;
					BUTTON_U = 2;
					BUTTON_Y = 3;
					BUTTON_A = 1;
					BUTTON_MENU = 7;
					DPAD_IS_POV = true;
					DPAD_IS_BUTTON = false;
					BUTTON_DPAD_UP = -1;
					BUTTON_DPAD_DOWN = -1;
					BUTTON_DPAD_RIGHT = -1;
					BUTTON_DPAD_LEFT = -1;
					DPAD_IS_AXIS = false;
					AXIS_DPAD_X = -1;
					AXIS_DPAD_Y = -1;
					BUTTON_L1 /* bumper */= 4;
					BUTTON_L2 /* trigger */= -1;
					BUTTON_L3 /* joystick */= 3;
					BUTTON_R1 /* bumper */= 5;
					BUTTON_R2 /* trigger */= -1;
					BUTTON_R3 /* joystick */= 9;
					AXIS_LEFT_X = 1;
					AXIS_LEFT_Y = 0;
					AXIS_LEFT_TRIGGER = 4;
					AXIS_RIGHT_X = 3;
					AXIS_RIGHT_Y = 2;
					AXIS_RIGHT_TRIGGER = 4;// This is for real, same axis # as
											// for LEFT trigger!
					BUTTON_BACK = 6;
					BUTTON_START = 7;
					break;
				}
				if (OS.platform.equals(OS.Platform.Ouya)) {
					BUTTON_O = 96;
					BUTTON_U = 99;
					BUTTON_Y = 100;
					BUTTON_A = 97;
					BUTTON_MENU = 82;
					DPAD_IS_POV = false;
					DPAD_IS_BUTTON = false;
					BUTTON_DPAD_UP = -1;
					BUTTON_DPAD_DOWN = -1;
					BUTTON_DPAD_RIGHT = -1;
					BUTTON_DPAD_LEFT = -1;
					DPAD_IS_AXIS = true;
					AXIS_DPAD_X = 6;
					AXIS_DPAD_Y = 7;
					BUTTON_L1 /* bumper */= 102;
					BUTTON_L2 /* trigger */= -1;
					BUTTON_L3 /* joystick */= 106;
					BUTTON_R1 /* bumper */= 103;
					BUTTON_R2 /* trigger */= -1;
					BUTTON_R3 /* joystick */= 107;
					AXIS_LEFT_X = 0;
					AXIS_LEFT_Y = 1;
					AXIS_LEFT_TRIGGER = 2;
					AXIS_RIGHT_X = 3;
					AXIS_RIGHT_Y = 4;
					AXIS_RIGHT_TRIGGER = 5;
					BUTTON_BACK = -1;
					BUTTON_START = 108;
					break;
				}
				if (OS.platform.equals(OS.Platform.Android)) {
					BUTTON_O = 96;
					BUTTON_U = 99;
					BUTTON_Y = 100;
					BUTTON_A = 97;
					BUTTON_MENU = 108;
					DPAD_IS_POV = false;
					DPAD_IS_BUTTON = false;
					BUTTON_DPAD_UP = -1;// axis(7)
					BUTTON_DPAD_DOWN = -1;// axis(7)
					BUTTON_DPAD_RIGHT = -1;// axis(6)
					BUTTON_DPAD_LEFT = -1;// axis(6)
					DPAD_IS_AXIS = true;
					AXIS_DPAD_X = 6;
					AXIS_DPAD_Y = 7;
					BUTTON_L1 /* bumper */= 102;
					BUTTON_L2 /* trigger */= -1; // axis(2)
					BUTTON_L3 /* joystick */= 106;
					BUTTON_R1 /* bumper */= 103;
					BUTTON_R2 /* trigger */= -1; // axis(5)
					BUTTON_R3 /* joystick */= 107;
					AXIS_LEFT_X = 0;
					AXIS_LEFT_Y = 1;
					AXIS_LEFT_TRIGGER = 2;
					AXIS_RIGHT_X = 3;
					AXIS_RIGHT_Y = 4;
					AXIS_RIGHT_TRIGGER = 5;
					BUTTON_BACK = 109;
					BUTTON_START = 110;
					break;
				}
				/* fallback values */
				BUTTON_O = 0;
				BUTTON_U = 2;
				BUTTON_Y = 3;
				BUTTON_A = 1;
				BUTTON_MENU = 8;
				DPAD_IS_POV = true;
				DPAD_IS_BUTTON = false;
				BUTTON_DPAD_UP = -1;
				BUTTON_DPAD_DOWN = -1;
				BUTTON_DPAD_RIGHT = -1;
				BUTTON_DPAD_LEFT = -1;
				DPAD_IS_AXIS = false;
				AXIS_DPAD_X = -1;
				AXIS_DPAD_Y = -1;
				BUTTON_L1 /* bumper */= 4;
				BUTTON_L2 /* trigger */= -1;
				BUTTON_L3 /* joystick */= 9;
				BUTTON_R1 /* bumper */= 5;
				BUTTON_R2 /* trigger */= -1;
				BUTTON_R3 /* joystick */= 10;
				AXIS_LEFT_X = 0;
				AXIS_LEFT_Y = 1;
				AXIS_LEFT_TRIGGER = 2;
				AXIS_RIGHT_X = 3;
				AXIS_RIGHT_Y = 4;
				AXIS_RIGHT_TRIGGER = 5;
				BUTTON_BACK = 6;
				BUTTON_START = 7;
			} while (false);
			break;
		}
	}
}
