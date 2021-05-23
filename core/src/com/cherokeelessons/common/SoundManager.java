package com.cherokeelessons.common;

import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.cherokeelessons.animals.CherokeeAnimals;

public class SoundManager {

	private boolean challengeEnabled = true;

	final private HashMap<String, Music> challenges = new HashMap<>();

	final private HashMap<String, Sound> effects = new HashMap<>();

	private boolean effectsEnabled = true;

	final private HashMap<String, FileHandle> validEffects = new HashMap<>();
	private final Prefs prefs;
	protected CherokeeAnimals game = null;
	
	public SoundManager(CherokeeAnimals game) {
		this.game = game;
		this.prefs = game.prefs;
		loadEffectNames();
	}

	public float getChallengeVolume() {
		if (prefs.getChallengeAudio()) {
			return getMasterVolume();
		}
		return 0f;
	}

	public float getEffectVolume() {
		switch (prefs.getEffectsVolume()) {
		case Off:
			return 0f;
		case Low:
			return 0.3f * getMasterVolume();
		case High:
			return getMasterVolume();
		}
		return getMasterVolume();
	}

	private float getMasterVolume() {
		return prefs.getMasterVolume() / 100f;
	}

	public boolean isChallengeEnabled() {
		return challengeEnabled;
	}

	public boolean isChallengePlaying(final String challenge) {
		if (!challenges.containsKey(challenge)) {
			return false;
		}
		return challenges.get(challenge).isPlaying();
	}

	public boolean isEffectsEnabled() {
		return effectsEnabled;
	}

	private void loadChallenge(final String challenge) {
		FileHandle fh;
		Music msc;
		if (challenges.containsKey(challenge)) {
			return;
		}
		if (!Utils.lookup.challenges.contains(challenge)) {
			System.out.println("BAD AUDIO CHALLENGE: " + challenge);
			return;
		}
		/*
		 * make sure we don't overload the sound subsystem...
		 */
		if (challenges.size() > 5) {
			for (final String key : challenges.keySet()) {
				challenges.get(key).stop();
				challenges.get(key).dispose();
			}
			challenges.clear();
		}
		try {
			fh = game.challenges.nextAudio(challenge);
			msc = Gdx.audio.newMusic(fh);
			challenges.put(challenge, msc);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void loadEffect(final String effect) {
		Sound snd;
		if (effects.containsKey(effect)) {
			return;
		}
		if (!validEffects.containsKey(effect)) {
			return;
		}
		snd = Gdx.audio.newSound(validEffects.get(effect));
		if (snd != null) {
			effects.put(effect, snd);
		}
	}

	public void loadEffectNames() {
		FileHandle fh;
		int ix;

		final String t1 = Gdx.files.internal("audio/effects/00-plist.txt").readString("UTF-8");
		final String[] dirListing = t1.split("\n");
		for (ix = 0; ix < dirListing.length; ix++) {
			fh = Gdx.files.internal("audio/effects/" + dirListing[ix]);
			long length;
			try {
				length = fh.length();
			} catch (final Exception e) {
				length = 0;
			}
			if (length > 0) {
				validEffects.put(fh.nameWithoutExtension(), fh);
			}
		}
	}

	public void playChallenge(final String challenge) {
		if (!isChallengeEnabled()) {
			return;
		}
		loadChallenge(challenge);
		if (!challenges.containsKey(challenge)) {
			return;
		}
		if (challenges.get(challenge).isPlaying()) {
			return;
		}
		challenges.get(challenge).setVolume(getChallengeVolume());
		try {
			challenges.get(challenge).play();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void playEffect(final String effect) {
		if (!isEffectsEnabled()) {
			return;
		}
		if (!validEffects.containsKey(effect)) {
			System.out.println("Unknown: " + effect);
			return;
		}
		loadEffect(effect);
		final float effectVolume = getEffectVolume();
		effects.get(effect).play(effectVolume);
	}

	public boolean preloadDone() {
		boolean done = true;
		for (final String effect : validEffects.keySet()) {
			if (!effects.containsKey(effect)) {
				loadEffect(effect);
				done = false;
				break;
			}
		}
		return done;
	}

	public void reset() {
		Set<String> keys;

		keys = effects.keySet();
		for (final String key : keys) {
			effects.get(key).stop();
			effects.get(key).dispose();
			effects.remove(key);
		}
		keys = challenges.keySet();
		for (final String key : keys) {
			challenges.get(key).stop();
			challenges.get(key).dispose();
			challenges.remove(key);
		}
		loadEffectNames();
	}

	public void setChallengeEnabled(final boolean challengeEnabled) {
		this.challengeEnabled = challengeEnabled;
	}

	public void setEffectsEnabled(final boolean effectsEnabled) {
		this.effectsEnabled = effectsEnabled;
	}
}
