package com.cherokeelessons.common;

import com.badlogic.gdx.audio.Music;

public class GameMusic implements Music {

	private Music music;

	public GameMusic(Music m) {
		music = m;
	}

	@Override
	public void play() {
		music.play();
	}

	@Override
	public void pause() {
		music.pause();

	}

	@Override
	public void stop() {
		music.stop();
	}

	@Override
	public boolean isPlaying() {
		return music.isPlaying();
	}

	@Override
	public void setLooping(boolean isLooping) {
		music.setLooping(isLooping);
	}

	@Override
	public boolean isLooping() {
		return music.isLooping();
	}

	@Override
	public void setVolume(float volume) {
		music.setVolume(volume);
	}

	@Override
	public float getVolume() {
		return music.getVolume();
	}

	@Override
	public void setPan(float pan, float volume) {
		music.setPan(pan, volume);

	}

	@Override
	public float getPosition() {
		return music.getPosition();
	}

	@Override
	public void dispose() {
		music.dispose();

	}

	@Override
	public void setOnCompletionListener(OnCompletionListener listener) {
		music.setOnCompletionListener(listener);

	}

}
