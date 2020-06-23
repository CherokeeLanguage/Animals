package com.cherokeelessons.common;

import java.util.Map;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.cherokeelessons.animals.enums.ChallengeWordMode;
import com.cherokeelessons.animals.enums.SoundEffectVolume;
import com.cherokeelessons.animals.enums.TrainingMode;

public class Prefs implements com.badlogic.gdx.Preferences {

	public enum Setting {
		ChallengeAudio, ChallengeMode, EffectsVolume, LevelAccuracy, //
		LevelTime, MasterVolume, MusicVolume, TrainingMode, UUID, //
		LeaderBoardEnabled, LevelScore
	}

	private final com.badlogic.gdx.Preferences prefs;

	public Prefs(final ApplicationListener app) {
		prefs = Gdx.app.getPreferences(app.getClass().getCanonicalName());
	}

	@Override
	public void clear() {
		prefs.clear();
	}

	@Override
	public boolean contains(final String key) {
		return prefs.contains(key);
	}

	@Override
	public void flush() {
		prefs.flush();
	}

	@Override
	public Map<String, ?> get() {
		return prefs.get();
	}

	@Override
	public boolean getBoolean(final String key) {
		return prefs.getBoolean(key);
	}

	@Override
	public boolean getBoolean(final String key, final boolean defValue) {
		return prefs.getBoolean(key, defValue);
	}

	public boolean getChallengeAudio() {
		final String key = Setting.ChallengeAudio.name();
		boolean m = true;
		try {
			m = prefs.getBoolean(key, true);
		} catch (final Exception e) {
			prefs.putBoolean(key, true);
			prefs.flush();
		}
		return m;
	}

	public ChallengeWordMode getChallengeMode() {
		ChallengeWordMode mode = ChallengeWordMode.Syllabary;
		final String key = Setting.ChallengeMode.name();
		try {
			final String m = prefs.getString(key, mode.name());
			mode = ChallengeWordMode.valueOf(m);
		} catch (final Exception e) {
			prefs.putString(key, mode.name());
			prefs.flush();
		}
		return mode;
	}

	public SoundEffectVolume getEffectsVolume() {
		final String key = Setting.EffectsVolume.name();
		SoundEffectVolume vol = SoundEffectVolume.Low;
		try {
			final String tmp = prefs.getString(key, vol.name());
			vol = SoundEffectVolume.valueOf(tmp);
		} catch (final Exception e) {
			prefs.putString(key, vol.name());
			prefs.flush();
		}
		return vol;
	}

	@Override
	public float getFloat(final String key) {
		return prefs.getFloat(key);
	}

	@Override
	public float getFloat(final String key, final float defValue) {
		return prefs.getFloat(key, defValue);
	}

	@Override
	public int getInteger(final String key) {
		return prefs.getInteger(key);
	}

	@Override
	public int getInteger(final String key, final int defValue) {
		return prefs.getInteger(key, defValue);
	}

	public int getLastScore(final int level) {
		String key = Setting.LevelScore.name() + "_";
		if (level < 10) {
			key += "0";
		}
		key += level;
		try {
			return prefs.getInteger(key, 0);
		} catch (final Exception e) {
			prefs.putInteger(key, 0);
			prefs.flush();
		}
		return 0;
	}

	public int getLevelAccuracy(final int level) {
		int acc = 0;
		String key = Setting.LevelAccuracy.name() + "_";
		if (level < 10) {
			key += "0";
		}
		key += level;
		try {
			acc = prefs.getInteger(key, acc);
		} catch (final Exception e) {
			prefs.putInteger(key, acc);
			prefs.flush();
		}
		if (acc < 0 || acc > 100) {
			acc = 0;
		}
		return acc;
	}

	public long getLevelTime(final int level) {
		long ms = 24 * 60 * 60 * 1000;
		String key = Setting.LevelTime.name() + "_";
		if (level < 10) {
			key += "0";
		}
		key += level;
		try {
			ms = prefs.getLong(key, ms);
		} catch (final Exception e) {
			prefs.putLong(key, ms);
			prefs.flush();
		}
		if (ms < 0 || ms > 24 * 60 * 60 * 1000) {
			ms = 24 * 60 * 60 * 1000;
		}
		return ms;
	}

	@Override
	public long getLong(final String key) {
		return prefs.getLong(key);
	}

	@Override
	public long getLong(final String key, final long defValue) {
		return prefs.getLong(key, defValue);
	}

	public int getMasterVolume() {
		final String key = Setting.MasterVolume.name();
		int vol = 70;
		try {
			vol = prefs.getInteger(key, vol);
		} catch (final Exception e) {
			prefs.putInteger(key, vol);
			prefs.flush();
		}
		if (vol < 0 || vol > 100) {
			vol = 100;
		}
		return vol;
	}

	public int getMusicVolume() {
		final String key = Setting.MusicVolume.name();
		int vol = 30;
		try {
			vol = prefs.getInteger(key, vol);
		} catch (final Exception e) {
			prefs.putInteger(key, vol);
			prefs.flush();
		}
		if (vol < 0 || vol > 100) {
			vol = 100;
		}
		return vol;
	}

	@Override
	public String getString(final String key) {
		return prefs.getString(key);
	}

	@Override
	public String getString(final String key, final String defValue) {
		return prefs.getString(key, defValue);
	}

	public TrainingMode getTrainingMode() {
		final String key = Setting.TrainingMode.name();
		TrainingMode mode = TrainingMode.Brief;
		try {
			final String tmp = prefs.getString(key, mode.name());
			mode = TrainingMode.valueOf(tmp);
		} catch (final Exception e) {
			prefs.putString(key, mode.name());
			prefs.flush();
		}
		return mode;
	}

	public String getUuid() {
		String uuid = null;
		final String key = Setting.UUID.name();
		try {
			uuid = prefs.getString(key, "");
		} catch (final Exception e) {
			prefs.putString(key, uuid);
			prefs.flush();
		}
		return uuid;
	}

	public boolean isLeaderBoardEnabled() {
		boolean enabled = true;
		final String key = Setting.LeaderBoardEnabled.name();
		try {
			enabled = prefs.getBoolean(key, true);
		} catch (final Exception e) {
			prefs.putBoolean(key, true);
			prefs.flush();
		}
		return enabled;
	}

	@Override
	public Preferences put(final Map<String, ?> vals) {
		return prefs.put(vals);
	}

	@Override
	public Preferences putBoolean(final String key, final boolean val) {
		return prefs.putBoolean(key, val);
	}

	@Override
	public Preferences putFloat(final String key, final float val) {
		return prefs.putFloat(key, val);
	}

	@Override
	public Preferences putInteger(final String key, final int val) {
		return prefs.putInteger(key, val);
	}

	@Override
	public Preferences putLong(final String key, final long val) {
		return prefs.putLong(key, val);
	}

	@Override
	public Preferences putString(final String key, final String val) {
		return prefs.putString(key, val);
	}

	@Override
	public void remove(final String key) {
		prefs.remove(key);
	}

	public void setChallengeAudio(final boolean on) {
		final String key = Setting.ChallengeAudio.name();
		prefs.putBoolean(key, on);
		prefs.flush();
	}

	public void setChallengeMode(final ChallengeWordMode mode) {
		final String key = Setting.ChallengeMode.name();
		prefs.putString(key, mode.name());
		prefs.flush();
	}

	public void setEffectsVolume(final SoundEffectVolume vol) {
		final String key = Setting.EffectsVolume.name();
		prefs.putString(key, vol.name());
		prefs.flush();
	}

	public void setLastScore(final int level, final int score) {
		String key = Setting.LevelScore.name() + "_";
		if (level < 10) {
			key += "0";
		}
		key += level;
		prefs.putInteger(key, score);
		prefs.flush();
	}

	public void setLeaderBoard(final boolean enabled) {
		final String key = Setting.LeaderBoardEnabled.name();
		prefs.putBoolean(key, true);
		prefs.flush();
	}

	public void setLevelAccuracy(final int level, final int acc) {
		String key = Setting.LevelAccuracy.name() + "_";
		if (level < 10) {
			key += "0";
		}
		key += level;
		prefs.putInteger(key, acc);
		prefs.flush();
	}

	public void setLevelTime(final int level, final float sec) {
		final long ms = (long) (sec * 1000f);
		setLevelTime(level, ms);
	}

	public void setLevelTime(final int level, final long ms) {
		String key = Setting.LevelTime.name() + "_";
		if (level < 10) {
			key += "0";
		}
		key += level;
		prefs.putLong(key, ms);
		prefs.flush();
	}

	public void setMasterVolume(final int vol) {
		final String key = Setting.MasterVolume.name();
		prefs.putInteger(key, vol);
		prefs.flush();
	}

	public void setMusicVolume(final int vol) {
		final String key = Setting.MusicVolume.name();
		prefs.putInteger(key, vol);
		prefs.flush();
	}

	public void setTrainingMode(final TrainingMode mode) {
		final String key = Setting.TrainingMode.name();
		prefs.putString(key, mode.name());
		prefs.flush();
	}

	public void setUuid(final String uuid) {
		final String key = Setting.UUID.name();
		prefs.putString(key, uuid);
		prefs.flush();
	}
}
