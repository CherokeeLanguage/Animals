package com.cherokeelessons.vocab.animals.one;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

public class SoundManager {

	final private HashMap<String, FileHandle> audioFiles = new HashMap<String, FileHandle>();
	private boolean challengeEnabled = true;

	final private HashMap<String, Music> challenges = new HashMap<String, Music>();

	private float challengeVolume = 1f;

	final private HashMap<String, Sound> effects = new HashMap<String, Sound>();

	private boolean effectsEnabled = true;

	private float effectVolume = 1f;
	final private HashMap<String, FileHandle> validEffects = new HashMap<String, FileHandle>();

	protected SoundManager() {
		super();
		preloadEffects();
	}

	public HashMap<String, FileHandle> getChallengeAudioList() {
		return audioFiles;
	}

	public float getChallengeVolume() {
		return challengeVolume;
	}

	public ArrayList<String> getEffectsList() {
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(validEffects.keySet());
		return list;
	}

	public float getEffectVolume() {
		return effectVolume;
	}

	public boolean isChallengeEnabled() {
		return challengeEnabled;
	}

	public boolean isChallengePlaying(String challenge) {
		if (!challenges.containsKey(challenge)) {
			return false;
		}
		return challenges.get(challenge).isPlaying();
	}

	public boolean isEffectsEnabled() {
		return effectsEnabled;
	}

	private void loadChallenge(String challenge) {
		FileHandle fh;
		Music msc;
		if (challenges.containsKey(challenge)) {
			return;
		}
		if (!audioFiles.containsKey(challenge)) {
			System.out.println("NO FILEHANDLE: " + challenge);
			return;
		}
		/*
		 * make sure we don't overload the sound subsystem...
		 */
		if (challenges.size() > 5) {
			for (String key : challenges.keySet()) {
				challenges.get(key).stop();
				challenges.get(key).dispose();
			}
			challenges.clear();
		}
		try {
			fh = audioFiles.get(challenge);
			msc = Gdx.audio.newMusic(fh);
			challenges.put(challenge, msc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadEffect(final String effect) {
		Sound snd;
		if (effects.containsKey(effect))
			return;
		snd = Gdx.audio.newSound(validEffects.get(effect));
		effects.put(effect, snd);
	}

	public void playChallenge(String challenge) {
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
		challenges.get(challenge).setVolume(challengeVolume);
		challenges.get(challenge).play();
	}

	public void playEffect(final String effect) {
		if (!isEffectsEnabled()) {
			return;
		}
		if (!validEffects.containsKey(effect)) {
			return;
		}
		loadEffect(effect);
		effects.get(effect).play(effectVolume);
	}

	public void preloadEffects() {
		String effectDir = "sounds/effects/";
		FileHandle fh;
		ArrayList<String> dirListing;
		int ix, len;

		dirListing = CherokeeAnimals.readAssetDir(effectDir);

		for (ix = 0, len = dirListing.size(); ix < len; ix++) {
			fh = Gdx.files.internal(dirListing.get(ix));
			validEffects.put(fh.nameWithoutExtension(), fh);
		}
	}

	public void reset() {
		Set<String> keys;

		keys = effects.keySet();
		for (String key : keys) {
			effects.get(key).stop();
			effects.get(key).dispose();
			effects.remove(key);
		}
		keys = challenges.keySet();
		for (String key : keys) {
			challenges.get(key).stop();
			challenges.get(key).dispose();
			challenges.remove(key);
		}
		preloadEffects();
	}

	public void setChallengeEnabled(boolean challengeEnabled) {
		this.challengeEnabled = challengeEnabled;
	}

	public void setChallengeVolume(float challengeVolume) {
		this.challengeVolume = challengeVolume;
	}

	public void setEffectsEnabled(boolean effectsEnabled) {
		this.effectsEnabled = effectsEnabled;
	}

	public void setEffectVolume(float effectVolume) {
		this.effectVolume = effectVolume;
	}

}
