package com.cherokeelessons.animals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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

	private static final String AUDIO_DIR = "audio/challenges/";
	private static final String IMAGE_DIR = "images/challenges/";

	private static final String AUDIO_MAPPING_FILE = "text/challenge-audio.txt";
	private static final String IMAGE_MAPPING_FILE = "text/challenge-images.txt";

	private static int challengesPerChallengeSet = 7;

	/*
	 * how many screens to split each challenge queue across
	 */
	private static final int screensPerChallengeSet = 3;
	final private Map<String, AudioSet> audioDecks;
	final private Map<Integer, CurrChallenges> cache_curr = new HashMap<>();
	final private Map<Integer, String> cache_levelname = new HashMap<>();

	final private List<String> challenges;
	final private Map<String, ImageSet> imageDecks;
	private boolean testmode = false;

	public LoadChallenges() {
		Utils.setChallengeLookup(loadChallengeLookupTexts());
		
		imageDecks = new HashMap<>();
		audioDecks = new HashMap<>();
		
		challenges = new ArrayList<>(Utils.lookup.challenges);
		
		Collections.sort(challenges, new SortSizeAscendingAlpha());
	}

	private ChallengeLookup loadChallengeLookupTexts() {
		ChallengeLookup lookup = new ChallengeLookup();
		
		Set<String> audioChallenges = new HashSet<>();
		Set<String> imageChallenges = new HashSet<>();
		
		String audioTxt = Gdx.files.internal(AUDIO_MAPPING_FILE).readString("UTF-8");
		String lines[] = audioTxt.split("\n");
		for (String line : lines) {
			line = line.trim();
			if (line.startsWith("#")) {
				continue;
			}
			if (line.isEmpty()) {
				continue;
			}
			String[] fields = line.split("\\|");
			if (fields == null || fields.length < 4) {
				continue;
			}
			String syllabary = fields[0].trim();
			String pronounce = fields[1].trim();
			String latin = fields[2].trim();
			String mp3List = fields[3].trim();
			String[] mp3s = mp3List.split(";");
			if (mp3s == null || mp3s.length == 0) {
				System.err.println("BAD ENTRY: " + line);
				continue;
			}
			audioChallenges.add(syllabary);
			lookup.challenges.add(syllabary);
			lookup.pronounce.put(syllabary, pronounce);
			lookup.latin.put(syllabary, latin);
			lookup.audio.put(syllabary, new TreeSet<String>());
			for (String mp3 : mp3s) {
				mp3 = mp3.trim();
				if (mp3.isEmpty()) {
					continue;
				}
				lookup.audio.get(syllabary).add(mp3);
			}
		}
		String imagesTxt = Gdx.files.internal(IMAGE_MAPPING_FILE).readString("UTF-8");
		lines = imagesTxt.split("\n");
		for (String line : lines) {
			line = line.trim();
			if (line.startsWith("#")) {
				continue;
			}
			if (line.isEmpty()) {
				continue;
			}
			String[] fields = line.split("\\|");
			if (fields == null || fields.length < 4) {
				continue;
			}
			String syllabary = fields[0].trim();
			String imageList = fields[3].trim();
			String[] images = imageList.split(";");
			if (images == null || images.length == 0) {
				System.err.println("BAD ENTRY: " + line);
				continue;
			}
			imageChallenges.add(syllabary);
			lookup.images.put(syllabary, new TreeSet<String>());
			for (String image : images) {
				image = image.trim();
				if (image.isEmpty()) {
					continue;
				}
				lookup.images.get(syllabary).add(image);
			}
		}
		for (String audioChallenge: audioChallenges) {
			if (!imageChallenges.contains(audioChallenge)) {
				lookup.challenges.remove(audioChallenge);
				lookup.audio.remove(audioChallenge);
				System.err.println("No image for audio challenge "+audioChallenge);
				System.err.flush();
			}
		}
		for (String imageChallenge: imageChallenges) {
			if (!audioChallenges.contains(imageChallenge)) {
				lookup.challenges.remove(imageChallenge);
				lookup.images.remove(imageChallenge);
				System.err.println("No audio for image challenge "+imageChallenge);
				System.err.flush();
			}
		}
		System.out.println(lookup.challenges.size()+" challenges loaded.");
		return lookup;
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

	public FileHandle nextAudio(final String syllabary) {
		AudioSet set = audioDecks.get(syllabary);
		if (set == null) {
			set = new AudioSet();
			audioDecks.put(syllabary, set);
		}
		if (set.audio.size() == 0) {
			for (String file: Utils.lookup.audio.get(syllabary)) {
				FileHandle fh = Gdx.files.internal(AUDIO_DIR).child(file); 
				set.audio.add(fh);
			}
			Collections.shuffle(set.audio);
		}
		return set.audio.remove(0);
	}

	public FileHandle nextImage(final String syllabary) {
		System.out.println("nextImage: "+syllabary);
		ImageSet set = imageDecks.get(syllabary);
		if (set == null) {
			set = new ImageSet();
			imageDecks.put(syllabary, set);
		}
		if (set.images.size() == 0) {
			for (String file: Utils.lookup.images.get(syllabary)) {
				System.out.println(" - image file: "+file);
				FileHandle fh = Gdx.files.internal(IMAGE_DIR).child(file); 
				set.images.add(fh);
			}
			Collections.shuffle(set.images);
		}
		return set.images.remove(0);
	}

	/**
	 * try and force the level count to be this by auto adjusting
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
