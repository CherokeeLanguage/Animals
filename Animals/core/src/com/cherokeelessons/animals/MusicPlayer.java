package com.cherokeelessons.animals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;

public class MusicPlayer {
	private static final String WILD_PERSPECTIVE = "musix-wild-perspective.mp3";
	private final AssetManager am;
	private int activeSong = 0;
	private Music m = null;

	public MusicPlayer() {
		am = new AssetManager();
	}

	public void pause() {
		if (m != null) {
			m.pause();
		}
	}

	public void resume() {
		if (m != null) {
			m.play();
		}
	}

	public void setVolume(float f) {
		volume = f;
		if (m!=null) {
			m.setVolume(volume);
		}
	}

	private final List<String> songs = new ArrayList<String>();
	private OnCompletionListener nextSong = new OnCompletionListener() {
		@Override
		public void onCompletion(Music music) {
			activeSong = ((activeSong + 1) % songs.size());
			if (activeSong==0) {
				Collections.shuffle(songs);
			}
			play(volume);
		}
	};

	private float volume = 1f;

	public void loadUsingPlist() {
		String songsPlist = Gdx.files.internal("music/00-plist.txt").readString("UTF-8");
		songs.clear();
		songs.addAll(Arrays.asList(songsPlist.split("\n")));
		Collections.shuffle(songs);
		for (int ix = 1; ix < songs.size(); ix++) {
			if (songs.get(ix).equals(WILD_PERSPECTIVE)) {
				songs.set(ix, songs.get(0));
				songs.set(0, WILD_PERSPECTIVE);
				break;
			}
		}
	}

	public void play(float f) {
		volume = f;
		if (m != null && m.isPlaying()) {
			m.setVolume(volume);
			return;
		}
		if (m != null) {
			m.stop();
		}
		am.clear();
		am.load("music/" + songs.get(activeSong), Music.class);
		am.finishLoading();
		m = am.get("music/" + songs.get(activeSong), Music.class);
		m.setOnCompletionListener(nextSong);
		m.setVolume(volume);
		m.setLooping(false);
		m.play();
	}

	public void dispose() {
		if (m!=null) {
			m.stop();
			m=null;
		}
		if (am!=null) {
			am.clear();
			am.dispose();
		}
	}
}
