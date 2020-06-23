package com.cherokeelessons.common;

import com.badlogic.gdx.Gdx;

public class Elapsed {

	private long start=0;
	public Elapsed() {
		start=System.currentTimeMillis();
	}

	public void log(String msg){
		Gdx.app.log(this.getClass().getSimpleName(), "["+elapsed()+"] "+msg);
	}
	
	public long elapsed(){
		long elapsed = System.currentTimeMillis()-start;
		start=System.currentTimeMillis();
		return elapsed;
	}
}
