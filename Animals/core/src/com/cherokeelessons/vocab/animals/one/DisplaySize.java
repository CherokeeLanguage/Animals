package com.cherokeelessons.vocab.animals.one;

public enum DisplaySize {
	_1080p, _240p, _480p, _720p;

	public static class Resolution {
		public int h = 0;
		public int w = 0;
	}

	public int height() {
		int h = 360;
		switch (this) {
		case _1080p:
			h = 1080;
			break;
		case _720p:
			h = 720;
			break;
		case _480p:
			h = 480;
			break;
		case _240p:
			h = 360;
			break;
		default:
			break;
		}
		return h;
	}

	public Resolution size() {
		Resolution r = new Resolution();
		switch (this) {
		case _1080p:
			r.w = 1920;
			r.h = 1080;
			break;
		case _720p:
			r.w = 1280;
			r.h = 720;
			break;
		case _480p:
			r.w = 853;
			r.h = 480;
			break;
		case _240p:
			r.w = 640;
			r.h = 360;
			break;
		default:
			break;
		}
		return r;
	}

	public int width() {
		int w = 640;
		switch (this) {
		case _1080p:
			w = 1920;
			break;
		case _720p:
			w = 1280;
			break;
		case _480p:
			w = 853;
			break;
		case _240p:
			w = 640;
			break;
		default:
			break;
		}
		return w;
	}
}