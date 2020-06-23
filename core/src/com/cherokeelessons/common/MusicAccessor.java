package com.cherokeelessons.common;

import aurelienribon.tweenengine.TweenAccessor;

public class MusicAccessor implements TweenAccessor<GameMusic> {
	final public static int Volume = 0;

	@Override
	public int getValues(final GameMusic target, final int tweenType, final float[] returnValues) {
		returnValues[0] = target.getVolume();
		return 1;
	}

	@Override
	public void setValues(final GameMusic target, final int tweenType, final float[] newValues) {
		target.setVolume(newValues[0]);
	}

	@Override
	public String toString() {
		return this.getClass().getCanonicalName();
	}
}
