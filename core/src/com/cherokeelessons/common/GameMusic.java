package com.cherokeelessons.common;

import com.badlogic.gdx.audio.Music;

public class GameMusic implements Music {

	private final Music music;

	public GameMusic(final Music m) {
		music = m;
	}

	@Override
	public void dispose() {
		music.dispose();

	}

	@Override
	public float getPosition() {
		return music.getPosition();
	}

	@Override
	public float getVolume() {
		return music.getVolume();
	}

	@Override
	public boolean isLooping() {
		return music.isLooping();
	}

	@Override
	public boolean isPlaying() {
		return music.isPlaying();
	}

	@Override
	public void pause() {
		music.pause();

	}

	@Override
	public void play() {
		music.play();
	}

	@Override
	public void setLooping(final boolean isLooping) {
		music.setLooping(isLooping);
	}

	@Override
	public void setOnCompletionListener(final OnCompletionListener listener) {
		music.setOnCompletionListener(listener);

	}

	@Override
	public void setPan(final float pan, final float volume) {
		music.setPan(pan, volume);

	}

	@Override
	public void setPosition(final float position) {
		music.setPosition(position);
	}

	@Override
	public void setVolume(final float volume) {
		music.setVolume(volume);
	}

	@Override
	public void stop() {
		music.stop();
	}

}
