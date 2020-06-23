package com.cherokeelessons.common;

import java.lang.reflect.Field;

public class OS {
	public static enum Platform {
		Android, Linux, Mac, Other, Ouya, Solaris, Unix, Windows;
	}

	final public static String name;
	final public static Platform platform;
	static {
		name = System.getProperty("os.name").toLowerCase();
		do {
			if (System.getProperty("java.vm.name").equalsIgnoreCase("Dalvik")) {
				/*
				 * Ouya check first (or it shows up as Android)
				 */
				String device = null;
				try {
					Class<?> buildClass = Class.forName("android.os.Build");
					Field deviceField = buildClass.getDeclaredField("DEVICE");
					Object o = deviceField.get(null);
					if (o != null) {
						device = o.toString().toLowerCase();
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