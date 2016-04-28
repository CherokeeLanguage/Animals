package com.cherokeelessons.common;

import com.badlogic.gdx.math.Rectangle;

public enum DisplaySize {
	_1080p, _240p, _480p, _720p;

	public Rectangle size() {
		Rectangle r = new Rectangle();
		switch (this) {
		case _1080p:
			r.width = 1920;
			r.height = 1080;
			break;
		case _720p:
			r.width = 1280;
			r.height = 720;
			break;
		case _480p:
			r.width = 853;
			r.height = 480;
			break;
		case _240p:
			r.width = 640;
			r.height = 360;
			break;
		default:
			break;
		}
		return r;
	}
	
	public Rectangle overscansize() {
		Rectangle r = new Rectangle();
		switch (this) {
		case _1080p:
			r.width = 1920;
			r.height = 1080;
			break;
		case _720p:
			r.width = 1280;
			r.height = 720;
			break;
		case _480p:
			r.width = 853;
			r.height = 480;
			break;
		case _240p:
			r.width = 640;
			r.height = 360;
			break;
		default:
			break;
		}
		int gap;
		gap = (int)(r.width*.075f);
		r.x=gap;
		r.width=(int)(r.width-2*gap);
		gap = (int)(r.height*.075f);
		r.y=gap;
		r.height=(int)(r.height-2*gap);
		return r;
	}
}