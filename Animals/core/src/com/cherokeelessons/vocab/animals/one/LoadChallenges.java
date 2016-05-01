package com.cherokeelessons.vocab.animals.one;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.cherokeelessons.vocab.animals.one.GraduatedIntervalQueue.SortSizeAscendingAlpha;

public class LoadChallenges {

	public static class AudioSet {
		public final List<FileHandle> audio = new ArrayList<FileHandle>(1);
	}

	private class CurrChallenges {
		final public List<String> list = new ArrayList<String>();
	}

	public static class ImageSet {
		public final List<FileHandle> images = new ArrayList<FileHandle>(11);
	}

	private static final String AUDIO_CHALLENGES = "audio/challenges/";

	private static int challengesPerChallengeSet = 7;
	private static final String IMAGES = "images/challenges/";

	/*
	 * how many screens to split each challenge queue across
	 */
	private static final int screensPerChallengeSet = 3;
	final private Map<String, AudioSet> audioDecks;
	final private Map<Integer, CurrChallenges> cache_curr = new HashMap<Integer, CurrChallenges>();
	final private Map<Integer, String> cache_levelname = new HashMap<Integer, String>();
	// final private Map<String, AudioSet> challengeAudio;
	final private Map<String, ImageSet> challengeImages;

	final private List<String> challenges;
	final private Map<String, ImageSet> imageDecks;
	private boolean testmode = false;

	public LoadChallenges() {
		List<FileHandle> audio = loadChallengesByAudio();
		matchUpAudioFilesToImages(audio);
		// challengeAudio = new HashMap<String, AudioSet>();
		challengeImages = new HashMap<String, ImageSet>();
		imageDecks = new HashMap<String, ImageSet>();
		audioDecks = new HashMap<String, AudioSet>();
		challenges = new ArrayList<String>();

		for (FileHandle afile : audio) {
			String name = afile.nameWithoutExtension();
			challenges.add(name);
			// AudioSet aset = getAudioSetFor(name);
			// challengeAudio.put(name, aset);
			ImageSet iset = getImageSetFor(name);
			challengeImages.put(name, iset);
		}
		challenges.sort(new SortSizeAscendingAlpha());
	}

	private List<String> calculateSeed(int start, int end) {
		List<String> seed = new ArrayList<String>();
		for (int ix = start; ix < end; ix++) {
			int iy = ix % challenges.size();
			String challenge = challenges.get(iy);
			seed.add(challenge);
		}
		if (start > challengesPerChallengeSet) {
			for (int ix = start - challengesPerChallengeSet; ix < end - challengesPerChallengeSet; ix++) {
				int iy = ix % challenges.size();
				String challenge = challenges.get(iy);
				seed.add(challenge);
			}
		} else {
			for (int ix = start; ix < end; ix++) {
				int iy = ix % challenges.size();
				String challenge = challenges.get(iy);
				seed.add(challenge);
			}
		}
		return seed;
	}

	public Set<String> getPreviousChallengesFor(int level) {
		Set<String> wasBefore = new HashSet<String>();
		for (int ix = 0; ix < level; ix++) {
			wasBefore.addAll(getChallengesFor(ix));
		}
		return wasBefore;
	}

	// private AudioSet getAudioSetFor(String name) {
	// AudioSet set = new AudioSet();
	// for (int ix = -1; ix < 10; ix++) {
	// String sfx = (ix != -1 ? "_" + ix : "");
	// //trust the plist!
	// FileHandle audio = Gdx.files.internal(AUDIO_CHALLENGES + name + sfx
	// + ".ogg");
	// set.audio.add(audio);
	// }
	// return set;
	// }

	public List<String> getChallengesFor(int level) {
		if (cache_curr.containsKey(level)) {
			return cache_curr.get(level).list;
		}
		final List<String> challengeList = new ArrayList<String>();
		int challengeSet = level / screensPerChallengeSet + 1;
		int subSet = level % screensPerChallengeSet;
		int start = (challengeSet - 1) * challengesPerChallengeSet;
		int end = start + challengesPerChallengeSet;
		List<String> seed = calculateSeed(start, end);

		GraduatedIntervalQueue giq = new GraduatedIntervalQueue();
		giq.setBriefList(true);
		giq.load(seed);

		challengeList.clear();
		List<String> list = giq.getIntervalQueue();
		int split = list.size() / screensPerChallengeSet;
		int setStart = split * subSet;
		int nextSet = split * (subSet + 1);
		for (int ix = setStart; ix < nextSet; ix++) {
			challengeList.add(list.get(ix));
		}
		if (testmode) {
			challengeList.subList(1, challengeList.size()).clear();
		}
		CurrChallenges clist = new CurrChallenges();
		clist.list.addAll(challengeList);
		cache_curr.put(level, clist);
		return challengeList;
	}

	private ImageSet getImageSetFor(String name) {
		ImageSet set = new ImageSet();
		for (int ix = -1; ix < 10; ix++) {
			String sfx = (ix != -1 ? "_" + ix : "");
			FileHandle pic = Gdx.files.internal(IMAGES + name + sfx + ".png");
			try {
				if (pic.length() > 0) {
					set.images.add(pic);
					continue;
				}
			} catch (Exception e) {
			}
		}
		return set;
	}

	public String getLevelNameFor(int level) {
		if (cache_levelname.containsKey(level)) {
			return cache_levelname.get(level);
		}
		Set<String> wasBefore = getPreviousChallengesFor(level);
		List<String> now = getChallengesFor(level);
		String result = now.get(0);
		for (String challenge : now) {
			if (!wasBefore.contains(challenge)) {
				result = challenge;
				break;
			}
		}
		cache_levelname.put(level, result);
		return result;
	}

	public boolean isTestmode() {
		return testmode;
	}

	/*
	 * how many levels do we have? *
	 */
	public int levelcount() {
		int sets = challenges.size() / challengesPerChallengeSet;
		int partial = challenges.size() % challengesPerChallengeSet;
		if (partial != 0) {
			sets++;
		}
		return sets * screensPerChallengeSet;
	}

	private List<FileHandle> loadChallengesByAudio() {
		String audio_plist = AUDIO_CHALLENGES + "00-plist.txt";
		String txt = Gdx.files.internal(audio_plist).readString("UTF-8");
		String[] plist = txt.split("\n");
		txt = null;
		final List<FileHandle> temp = new ArrayList<FileHandle>(plist.length);
		for (int ix = 0; ix < plist.length; ix++) {
			String e = plist[ix];
			if (e == null) {
				continue;
			}
			e = e.trim();
			if (e.length() == 0) {
				continue;
			}
			if (e.contains("plist")) {
				continue;
			}
			if (e.contains("_")) {
				continue;
			}
			// trust the plist!
			FileHandle f = Gdx.files.internal(AUDIO_CHALLENGES + e);
			temp.add(f);
		}
		plist = null;
		return temp;
	}

	private void matchUpAudioFilesToImages(List<FileHandle> challenges) {
		Iterator<FileHandle> i = challenges.iterator();
		while (i.hasNext()) {
			String name = i.next().nameWithoutExtension();
			try {
				FileHandle pic = Gdx.files.internal(IMAGES + name + ".png");
				if (pic.length() > 0) {
					continue;
				}
			} catch (Exception e) {
			}
			System.out.println("MISSING PICTURE: '" + name + "'");
			i.remove();
		}
	}

	public FileHandle nextAudio(String name) {
		AudioSet set = audioDecks.get(name);
		if (set == null) {
			set = new AudioSet();
			audioDecks.put(name, set);
		}
		if (set.audio.size() == 0) {
			set.audio.addAll(challengeImages.get(name).images);
			Collections.shuffle(set.audio);
		}
		return set.audio.remove(0);
	}

	public FileHandle nextImage(String name) {
		ImageSet set = imageDecks.get(name);
		if (set == null) {
			set = new ImageSet();
			imageDecks.put(name, set);
		}
		if (set.images.size() == 0) {
			set.images.addAll(challengeImages.get(name).images);
			Collections.shuffle(set.images);
		}
		return set.images.remove(0);
	}

	/*
	 * try and force the level cout to be this by auto adjusting
	 * challengesPerChallengeSet
	 */
	public void setLevelCount(int count) {
		int sets = (count - 1) / screensPerChallengeSet + 1;
		challengesPerChallengeSet = (int) Math.ceil((float) challenges.size() / (float) sets);
		// precalculate and cache this stuff
		cache_levelname.clear();
		cache_curr.clear();
		int levels = levelcount();
		for (int ix = 0; ix < levels; ix++) {
			getLevelNameFor(ix);
			getPreviousChallengesFor(ix);
			getChallengesFor(ix);
		}
	}

	public void setTestmode(boolean testmode) {
		this.testmode = testmode;
	}
}
