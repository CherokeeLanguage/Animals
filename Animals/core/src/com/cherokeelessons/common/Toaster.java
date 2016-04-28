package com.cherokeelessons.common;

public interface Toaster {
	static public enum Length {
		Long, Short;
	}
	public void toast(String msg, Length len);
}
