package com.cherokeelessons.common;

import java.util.Map;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.cherokeelessons.animals.enums.ChallengeWordMode;
import com.cherokeelessons.animals.enums.SoundEffectVolume;
import com.cherokeelessons.animals.enums.TrainingMode;

public class Prefs implements com.badlogic.gdx.Preferences {

	public static enum Setting {
		ChallengeAudio, ChallengeMode, EffectsVolume, LevelAccuracy, //
		LevelTime, MasterVolume, MusicVolume, TrainingMode, UUID, //
		LeaderBoardEnabled, LevelScore
	}

	private com.badlogic.gdx.Preferences prefs;

	public Prefs(ApplicationListener app) {
		prefs = Gdx.app.getPreferences(app.getClass().getCanonicalName());
	}

	@Override
	public void clear() {
		prefs.clear();
	}

	@Override
	public boolean contains(String key) {
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
	public boolean getBoolean(String key) {
		return prefs.getBoolean(key);
	}

	@Override
	public boolean getBoolean(String key, boolean defValue) {
		return prefs.getBoolean(key, defValue);
	}

	public boolean getChallengeAudio() {
		String key = Setting.ChallengeAudio.name();
		boolean m = true;
		try {
			m = prefs.getBoolean(key, true);
		} catch (Exception e) {
			prefs.putBoolean(key, true);
			prefs.flush();
		}
		return m;
	}

	public boolean isLeaderBoardEnabled() {
		boolean enabled = true;
		String key = Setting.LeaderBoardEnabled.name();
		try {
			enabled = prefs.getBoolean(key, true);
		} catch (Exception e) {
			prefs.putBoolean(key, true);
			prefs.flush();
		}
		return enabled;
	}

	public void setLeaderBoard(boolean enabled) {
		String key = Setting.LeaderBoardEnabled.name();
		prefs.putBoolean(key, true);
		prefs.flush();
	}

	public ChallengeWordMode getChallengeMode() {
		ChallengeWordMode mode = ChallengeWordMode.Syllabary;
		String key = Setting.ChallengeMode.name();
		try {
			String m = prefs.getString(key, mode.name());
			mode = ChallengeWordMode.valueOf(m);
		} catch (Exception e) {
			prefs.putString(key, mode.name());
			prefs.flush();
		}
		return mode;
	}
	
	public int getLastScore(int level) {
		String key = Setting.LevelScore.name() + "_";
		if (level < 10) {
			key += "0";
		}
		key += level;
		try {
			return prefs.getInteger(key, 0);
		} catch (Exception e) {
			prefs.putInteger(key, 0);
			prefs.flush();
		}
		return 0;
	}

	public SoundEffectVolume getEffectsVolume() {
		String key = Setting.EffectsVolume.name();
		SoundEffectVolume vol = SoundEffectVolume.Low;
		try {
			String tmp = prefs.getString(key, vol.name());
			vol = SoundEffectVolume.valueOf(tmp);
		} catch (Exception e) {
			prefs.putString(key, vol.name());
			prefs.flush();
		}
		return vol;
	}

	@Override
	public float getFloat(String key) {
		return prefs.getFloat(key);
	}

	@Override
	public float getFloat(String key, float defValue) {
		return prefs.getFloat(key, defValue);
	}

	@Override
	public int getInteger(String key) {
		return prefs.getInteger(key);
	}

	@Override
	public int getInteger(String key, int defValue) {
		return prefs.getInteger(key, defValue);
	}

	public int getLevelAccuracy(int level) {
		int acc = 0;
		String key = Setting.LevelAccuracy.name() + "_";
		if (level < 10)
			key += "0";
		key += level;
		try {
			acc = prefs.getInteger(key, acc);
		} catch (Exception e) {
			prefs.putInteger(key, acc);
			prefs.flush();
		}
		if (acc < 0 || acc > 100) {
			acc = 0;
		}
		return acc;
	}

	public long getLevelTime(int level) {
		long ms = 24 * 60 * 60 * 1000;
		String key = Setting.LevelTime.name() + "_";
		if (level < 10)
			key += "0";
		key += level;
		try {
			ms = prefs.getLong(key, ms);
		} catch (Exception e) {
			prefs.putLong(key, ms);
			prefs.flush();
		}
		if (ms < 0 || ms > 24 * 60 * 60 * 1000) {
			ms = 24 * 60 * 60 * 1000;
		}
		return ms;
	}

	@Override
	public long getLong(String key) {
		return prefs.getLong(key);
	}

	@Override
	public long getLong(String key, long defValue) {
		return prefs.getLong(key, defValue);
	}

	public int getMasterVolume() {
		String key = Setting.MasterVolume.name();
		int vol = 70;
		try {
			vol = prefs.getInteger(key, vol);
		} catch (Exception e) {
			prefs.putInteger(key, vol);
			prefs.flush();
		}
		if (vol < 0 || vol > 100) {
			vol = 100;
		}
		return vol;
	}

	public int getMusicVolume() {
		String key = Setting.MusicVolume.name();
		int vol = 30;
		try {
			vol = prefs.getInteger(key, vol);
		} catch (Exception e) {
			prefs.putInteger(key, vol);
			prefs.flush();
		}
		if (vol < 0 || vol > 100) {
			vol = 100;
		}
		return vol;
	}

	@Override
	public String getString(String key) {
		return prefs.getString(key);
	}

	@Override
	public String getString(String key, String defValue) {
		return prefs.getString(key, defValue);
	}

	public TrainingMode getTrainingMode() {
		String key = Setting.TrainingMode.name();
		TrainingMode mode = TrainingMode.Brief;
		try {
			String tmp = prefs.getString(key, mode.name());
			mode = TrainingMode.valueOf(tmp);
		} catch (Exception e) {
			prefs.putString(key, mode.name());
			prefs.flush();
		}
		return mode;
	}

	public String getUuid() {
		String uuid = null;
		String key = Setting.UUID.name();
		try {
			uuid = prefs.getString(key, "");
		} catch (Exception e) {
			prefs.putString(key, uuid);
			prefs.flush();
		}
		return uuid;
	}

	@Override
	public Preferences put(Map<String, ?> vals) {
		return prefs.put(vals);
	}

	@Override
	public Preferences putBoolean(String key, boolean val) {
		return prefs.putBoolean(key, val);
	}

	@Override
	public Preferences putFloat(String key, float val) {
		return prefs.putFloat(key, val);
	}

	@Override
	public Preferences putInteger(String key, int val) {
		return prefs.putInteger(key, val);
	}

	@Override
	public Preferences putLong(String key, long val) {
		return prefs.putLong(key, val);
	}

	@Override
	public Preferences putString(String key, String val) {
		return prefs.putString(key, val);
	}

	@Override
	public void remove(String key) {
		prefs.remove(key);
	}

	public void setChallengeAudio(boolean on) {
		String key = Setting.ChallengeAudio.name();
		prefs.putBoolean(key, on);
		prefs.flush();
	}

	public void setChallengeMode(ChallengeWordMode mode) {
		String key = Setting.ChallengeMode.name();
		prefs.putString(key, mode.name());
		prefs.flush();
	}

	public void setEffectsVolume(SoundEffectVolume vol) {
		String key = Setting.EffectsVolume.name();
		prefs.putString(key, vol.name());
		prefs.flush();
	}

	public void setLevelAccuracy(int level, int acc) {
		String key = Setting.LevelAccuracy.name() + "_";
		if (level < 10)
			key += "0";
		key += level;
		prefs.putInteger(key, acc);
		prefs.flush();
	}

	public void setLevelTime(int level, long ms) {
		String key = Setting.LevelTime.name() + "_";
		if (level < 10)
			key += "0";
		key += level;
		prefs.putLong(key, ms);
		prefs.flush();
	}

	public void setLevelTime(int level, float sec) {
		long ms = (long) (sec * 1000f);
		setLevelTime(level, ms);
	}
	
	public void setLastScore(int level, int score) {
		String key = Setting.LevelScore.name() + "_";
		if (level < 10) {
			key += "0";
		}
		key += level;
		prefs.putInteger(key, score);
		prefs.flush();
	}

	public void setMasterVolume(int vol) {
		String key = Setting.MasterVolume.name();
		prefs.putInteger(key, vol);
		prefs.flush();
	}

	public void setMusicVolume(int vol) {
		String key = Setting.MusicVolume.name();
		prefs.putInteger(key, vol);
		prefs.flush();
	}

	public void setTrainingMode(TrainingMode mode) {
		String key = Setting.TrainingMode.name();
		prefs.putString(key, mode.name());
		prefs.flush();
	}

	public void setUuid(String uuid) {
		String key = Setting.UUID.name();
		prefs.putString(key, uuid);
		prefs.flush();
	}
}
