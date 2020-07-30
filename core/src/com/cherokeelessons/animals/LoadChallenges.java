package com.cherokeelessons.animals;

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
import com.cherokeelessons.animals.GraduatedIntervalQueue.SortSizeAscendingAlpha;
import com.cherokeelessons.common.Utils;

public class LoadChallenges {

	public static class AudioSet {
		public final List<FileHandle> audio = new ArrayList<>(1);
	}

	private class CurrChallenges {
		final public List<String> list = new ArrayList<>();
	}

	public static class ImageSet {
		public final List<FileHandle> images = new ArrayList<>(11);
	}

	private static final String AUDIO_CHALLENGES = "audio/challenges/";
	private static final String NAME_MAPPINGS_FILE = "espeak.tsv";

	private static int challengesPerChallengeSet = 7;
	private static final String IMAGES = "images/challenges/";

	/*
	 * how many screens to split each challenge queue across
	 */
	private static final int screensPerChallengeSet = 3;
	final private Map<String, AudioSet> audioDecks;
	final private Map<Integer, CurrChallenges> cache_curr = new HashMap<>();
	final private Map<Integer, String> cache_levelname = new HashMap<>();
	// final private Map<String, AudioSet> challengeAudio;
	final private Map<String, ImageSet> challengeImages;

	final private List<String> challenges;
	final private Map<String, ImageSet> imageDecks;
	private boolean testmode = false;

	public LoadChallenges() {
		Utils.setFileChrMapping(loadChrMapping());
		Utils.setFileLatinMapping(loadLatinMapping());
		final List<FileHandle> audio = loadChallengesByAudio();
		matchUpAudioFilesToImages(audio);
		// challengeAudio = new HashMap<String, AudioSet>();
		challengeImages = new HashMap<>();
		imageDecks = new HashMap<>();
		audioDecks = new HashMap<>();
		challenges = new ArrayList<>();

		for (final FileHandle afile : audio) {
			final String name = afile.nameWithoutExtension();
			challenges.add(name);
			// AudioSet aset = getAudioSetFor(name);
			// challengeAudio.put(name, aset);
			final ImageSet iset = getImageSetFor(name);
			challengeImages.put(name, iset);
		}
		Collections.sort(challenges, new SortSizeAscendingAlpha());
	}

	private Map<String, String> loadChrMapping() {
		Map<String, String> map = new HashMap<String, String>();
		String txt = Gdx.files.internal(NAME_MAPPINGS_FILE).readString("UTF-8");
		String lines[] = txt.split("\n");
		for (String line: lines) {
			String[] fields = line.split("\t");
			if (fields==null || fields.length<3) {
				continue;
			}
			String nameWithoutExtension = Gdx.files.internal(fields[2].trim()).nameWithoutExtension();
			map.put(nameWithoutExtension, fields[0].trim().replaceAll("(?i)[^Ꭰ-Ᏼ ]", ""));
		}
		return map;
	}
	
	private Map<String, String> loadLatinMapping() {
		Map<String, String> map = new HashMap<String, String>();
		String txt = Gdx.files.internal(NAME_MAPPINGS_FILE).readString("UTF-8");
		String lines[] = txt.split("\n");
		for (String line: lines) {
			String[] fields = line.split("\t");
			if (fields==null || fields.length<3) {
				continue;
			}
			String nameWithoutExtension = Gdx.files.internal(fields[2].trim()).nameWithoutExtension();
			String latin = fields[1].trim().toLowerCase();
			latin = latin.replace("ạ", "a");
			latin = latin.replace("ẹ", "e");
			latin = latin.replace("ị", "i");
			latin = latin.replace("ọ", "o");
			latin = latin.replace("ụ", "u");
			latin = latin.replace("ṿ", "v");
			latin = latin.replaceAll("(?i)[^a-z ]", "");
			map.put(nameWithoutExtension, latin);
		}
		return map;
	}


	private List<String> calculateSeed(final int start, final int end) {
		final List<String> seed = new ArrayList<>();
		for (int ix = start; ix < end; ix++) {
			final int iy = ix % challenges.size();
			final String challenge = challenges.get(iy);
			seed.add(challenge);
		}
		if (start > challengesPerChallengeSet) {
			for (int ix = start - challengesPerChallengeSet; ix < end - challengesPerChallengeSet; ix++) {
				final int iy = ix % challenges.size();
				final String challenge = challenges.get(iy);
				seed.add(challenge);
			}
		} else {
			for (int ix = start; ix < end; ix++) {
				final int iy = ix % challenges.size();
				final String challenge = challenges.get(iy);
				seed.add(challenge);
			}
		}
		return seed;
	}

	public List<String> getChallengesFor(final int level) {
		if (cache_curr.containsKey(level)) {
			return new ArrayList<>(cache_curr.get(level).list);
		}
		final List<String> challengeList = new ArrayList<>();
		final int challengeSet = level / screensPerChallengeSet + 1;
		final int subSet = level % screensPerChallengeSet;
		final int start = (challengeSet - 1) * challengesPerChallengeSet;
		final int end = start + challengesPerChallengeSet;
		final List<String> seed = calculateSeed(start, end);

		final GraduatedIntervalQueue giq = new GraduatedIntervalQueue();
		giq.setBriefList(true);
		giq.load(seed);

		challengeList.clear();
		final List<String> list = giq.getIntervalQueue();
		final int split = list.size() / screensPerChallengeSet;
		final int setStart = split * subSet;
		final int nextSet = split * (subSet + 1);
		for (int ix = setStart; ix < nextSet; ix++) {
			challengeList.add(list.get(ix));
		}
		if (testmode) {
			challengeList.subList(1, challengeList.size()).clear();
		}
		final CurrChallenges clist = new CurrChallenges();
		clist.list.addAll(challengeList);
		cache_curr.put(level, clist);
		return new ArrayList<>(challengeList);
	}

	private ImageSet getImageSetFor(final String name) {
		final ImageSet set = new ImageSet();
		for (int ix = -1; ix < 10; ix++) {
			final String sfx = ix != -1 ? "_" + ix : "";
			final FileHandle pic = Gdx.files.internal(IMAGES + name + sfx + ".png");
			try {
				if (pic.length() > 0) {
					set.images.add(pic);
					continue;
				}
			} catch (final Exception e) {
			}
		}
		return set;
	}

	public String getLevelNameFor(final int level) {
		if (cache_levelname.containsKey(level)) {
			return cache_levelname.get(level);
		}
		final Set<String> wasBefore = getPreviousChallengesFor(level);
		final List<String> now = getChallengesFor(level);
		String result = now.get(0);
		for (final String challenge : now) {
			if (!wasBefore.contains(challenge)) {
				result = challenge;
				break;
			}
		}
		cache_levelname.put(level, result);
		return result;
	}

	public Set<String> getPreviousChallengesFor(final int level) {
		final Set<String> wasBefore = new HashSet<>();
		for (int ix = 0; ix < level; ix++) {
			wasBefore.addAll(getChallengesFor(ix));
		}
		return wasBefore;
	}

	public boolean isTestmode() {
		return testmode;
	}

	/*
	 * how many levels do we have? *
	 */
	public int levelcount() {
		int sets = challenges.size() / challengesPerChallengeSet;
		final int partial = challenges.size() % challengesPerChallengeSet;
		if (partial != 0) {
			sets++;
		}
		return sets * screensPerChallengeSet;
	}

	private List<FileHandle> loadChallengesByAudio() {
		final String audio_plist = AUDIO_CHALLENGES + "00-plist.txt";
		String txt = Gdx.files.internal(audio_plist).readString("UTF-8");
		String[] plist = txt.split("\n");
		txt = null;
		final List<FileHandle> temp = new ArrayList<>(plist.length);
		for (final String element : plist) {
			String e = element;
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
			final FileHandle f = Gdx.files.internal(AUDIO_CHALLENGES + e);
			temp.add(f);
		}
		plist = null;
		return temp;
	}

	private void matchUpAudioFilesToImages(final List<FileHandle> challenges) {
		final Iterator<FileHandle> i = challenges.iterator();
		while (i.hasNext()) {
			final String name = i.next().nameWithoutExtension();
			try {
				final FileHandle pic = Gdx.files.internal(IMAGES + name + ".png");
				if (pic.length() > 0) {
					continue;
				}
			} catch (final Exception e) {
			}
			System.out.println("MISSING PICTURE: '" + name + "'");
			i.remove();
		}
	}

	public FileHandle nextAudio(final String name) {
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

	public FileHandle nextImage(final String name) {
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
	public void setLevelCount(final int count) {
		final int sets = (count - 1) / screensPerChallengeSet + 1;
		challengesPerChallengeSet = (int) Math.ceil((float) challenges.size() / (float) sets);
		// precalculate and cache this stuff
		cache_levelname.clear();
		cache_curr.clear();
		final int levels = levelcount();
		for (int ix = 0; ix < levels; ix++) {
			getLevelNameFor(ix);
			getPreviousChallengesFor(ix);
			getChallengesFor(ix);
		}
	}

	public void setTestmode(final boolean testmode) {
		this.testmode = testmode;
	}
}
