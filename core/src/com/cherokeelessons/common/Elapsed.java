package com.cherokeelessons.common;

import com.badlogic.gdx.Gdx;

public class Elapsed {

	private long start = 0;

	public Elapsed() {
		start = System.currentTimeMillis();
	}

	public long elapsed() {
		final long elapsed = System.currentTimeMillis() - start;
		start = System.currentTimeMillis();
		return elapsed;
	}

	public void log(final String msg) {
		Gdx.app.log(this.getClass().getSimpleName(), "[" + elapsed() + "] " + msg);
	}
}
