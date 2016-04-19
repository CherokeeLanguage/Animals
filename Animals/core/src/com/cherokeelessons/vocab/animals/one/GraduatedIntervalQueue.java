package com.cherokeelessons.vocab.animals.one;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.math.Vector2;

public class GraduatedIntervalQueue {

	private HashMap<String, String> animalsSyl = null;

	private HashMap<String, Integer> bounderiesByName;

	private HashMap<Integer, String> bounderiesByPosition;

	private ArrayList<String> bounderiesNameList;

	private boolean debug = true;
	private ArrayList<String> intervalQueue;

	private ArrayList<Vector2> levelMarks;

	private ArrayList<String> startingEntries;

	protected GraduatedIntervalQueue() {
		super();
	}

	public void calculateLevelStarts(int levels) {
		int startingPoint;
		String item;
		int listSize = 0;
		int ix, index;

		levelMarks = new ArrayList<Vector2>();
		Vector2 thisLevelMarks = null;
		Vector2 prevLevelMarks = null;

		listSize = bounderiesNameList.size();
		for (ix = 0; ix < levels; ix++) {
			index = (int) Math.ceil((float) listSize
					* ((float) ix / (float) levels));
			item = bounderiesNameList.get(index);
			startingPoint = bounderiesByName.get(item);
			thisLevelMarks = new Vector2(startingPoint,
					intervalQueue.size() - 1);
			if (prevLevelMarks != null) {
				prevLevelMarks.y = startingPoint - 1;
			}
			prevLevelMarks = thisLevelMarks;
			levelMarks.add(thisLevelMarks);
		}
	}

	private ArrayList<String> dedupeAndSort(ArrayList<String> list) {
		ArrayList<String> newList;
		newList = new ArrayList<String>();
		newList = new ArrayList<String>(new HashSet<String>(list));
		Collections.sort(newList);
		return newList;
	}

	@SuppressWarnings("unused")
	private void dumpList(ArrayList<String> listToDump) {
		int ix, len;
		if (!debug) {
			return;
		}
		System.out.println("===================================");
		for (ix = 0, len = listToDump.size(); ix < len; ix++) {
			System.out.println(ix + ": " + listToDump.get(ix));
		}
		System.out.println("===================================");
		System.out.println();
	}

	public HashMap<String, String> getAnimalsSyl() {
		return animalsSyl;
	}

	public String getEntry(int ix) {
		if (intervalQueue.size() > ix && ix >= 0) {
			return intervalQueue.get(ix);
		}
		return "";
	}

	int getLevelCount() {
		return levelMarks.size();
	}

	int getLevelEndPosition(int level) {
		return (int) levelMarks.get(level).y;
	}

	String getLevelStartName(int level) {
		int position;
		position = getLevelStartPosition(level);
		return bounderiesByPosition.get(position);
	}

	int getLevelStartPosition(int level) {
		return (int) levelMarks.get(level).x;
	}

	/**
	 * based on getOffsetsReal from 'translations.php'
	 * 
	 * @return ArrayList<Integer>
	 */
	private ArrayList<Integer> getOffsets() {
		ArrayList<Integer> o1;
		int ip, depth = 4, stagger = 2, ix;

		o1 = new ArrayList<Integer>();

		for (ix = 0; ix < stagger; ix++) {
			for (ip = 0; ip <= depth; ip++) {
				o1.add((int) Math.pow(2 + ix, ip));
			}
		}
		return o1;
	}

	private ArrayList<String> getQueue(ArrayList<String> samplesIn) {
		int ix, iy, ia;
		ArrayList<Integer> offsets;
		ArrayList<String> newQueue = null;
		ArrayList<String> samples;

		newQueue = new ArrayList<String>();
		samples = new ArrayList<String>();
		offsets = getOffsets();

		samples.addAll(samplesIn);
		// mixUpSamples(samples);
		// orderBySizeAsc(samples);
		orderBySizeAscCustom(samples);
		rearrangeForPlurals(samples);
		for (String entry: samples) {
			System.out.println(animalsSyl.get(entry));
		}
		/**
		 * process samples creating non-random work queue
		 */
		for (ix = 0; ix < samples.size(); ix++) {
			ia = 0;
			for (iy = 0; iy < offsets.size(); iy++) {
				while (newQueue.size() < ia + 1)
					newQueue.add("");
				while (newQueue.get(ia).compareTo("") != 0) {
					ia++;
					while (newQueue.size() < ia + 1)
						newQueue.add("");
				}
				newQueue.set(ia, samples.get(ix));
				ia += offsets.get(iy);
			}
		}
		removeGaps(newQueue);

		return newQueue;
	}

	private void rearrangeForPlurals(ArrayList<String> samples) {
		ArrayList<String> tmp = new ArrayList<String>();
		tmp.addAll(samples);
		samples.clear();
		for (int ix=0; ix<tmp.size(); ix++) {
			String a1, a2, s1, s2;
			a1 = tmp.get(ix);
			samples.add(a1);
			if (animalsSyl.containsKey(a1)){
				s1 = animalsSyl.get(a1);
			} else {
				s1=a1;
			}
			if (s1.contains(" ")){
				String[] x = s1.split(" ");
				s1=x[x.length-1];
			}
			if (s1.length()<3) {
				continue;
			}
			s1=s1.substring(1);
			for (int iy=ix+1; iy<tmp.size(); iy++) {
				a2 = tmp.get(iy);
				if (animalsSyl.containsKey(a2)){
					s2 = animalsSyl.get(a2);
				} else {
					s2=a2;
				}
				if (s2.contains(" ")){
					String[] x = s2.split(" ");
					s2=x[x.length-1];
				}
				if (s2.endsWith(s1)) {
					samples.add(a2);
					tmp.remove(iy);
					break;
				}
			}
		}
		
	}

	public boolean isDebug() {
		return debug;
	}

	public void load(ArrayList<String> _startingEntries) {
		bounderiesNameList = new ArrayList<String>();
		bounderiesByName = new HashMap<String, Integer>();
		bounderiesByPosition = new HashMap<Integer, String>();
		startingEntries = dedupeAndSort(_startingEntries);
		intervalQueue = getQueue(startingEntries);
		locateBounderies();
		calculateLevelStarts(18);
	}

	private void locateBounderies() {
		int ix, len;
		String item;

		for (ix = 0, len = intervalQueue.size(); ix < len; ix++) {
			item = intervalQueue.get(ix);
			if (bounderiesNameList.contains(item)) {
				continue;
			}
			bounderiesNameList.add(item);
			bounderiesByName.put(item, ix);
			bounderiesByPosition.put(ix, item);
		}
	}

	@SuppressWarnings("unused")
	private void mixUpSamples(ArrayList<String> samples) {
		ArrayList<String> mixedUp;
		ArrayList<Integer> offsets;

		mixedUp = new ArrayList<String>();
		offsets = getOffsets();
		/**
		 * re-arrange samples into a non-random, non-alpha order
		 */
		for (int ix = 0; ix < 10; ix++) {
			while (samples.size() > 0) {
				for (int iy = 0; samples.size() > 0 && iy < offsets.size(); iy++) {
					int ia = offsets.get(iy);
					while (mixedUp.size() > ia && mixedUp.get(ia) != "")
						ia++;
					while (mixedUp.size() < ia + 1)
						mixedUp.add("");
					mixedUp.set(ia, samples.get(0));
					samples.remove(0);
				}
			}
			removeGaps(mixedUp); // removes "holes"
			Collections.reverse(mixedUp);
			samples.clear();
			samples.addAll(mixedUp);
			mixedUp.clear();
		}
	}

	private void orderBySizeAsc(ArrayList<String> samples) {
		Collections.sort(samples, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if (o1.length() < o2.length())
					return -1;
				if (o1.length() > o2.length())
					return 1;
				return (o1.compareTo(o2));
			}
		});
	}

	private void orderBySizeAscCustom(ArrayList<String> samples) {
		if (animalsSyl == null) {
			orderBySizeAsc(samples);
			return;
		}
		Collections.sort(samples, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if (animalsSyl.containsKey(o1))
					o1 = animalsSyl.get(o1);
				if (animalsSyl.containsKey(o2))
					o2 = animalsSyl.get(o2);
				if (o1.length() < o2.length())
					return -1;
				if (o1.length() > o2.length())
					return 1;
				return (o1.compareTo(o2));
			}
		});
	}

	public void removeGaps(ArrayList<String> queue) {
		int ix = 0, repeat;
		ArrayList<String> vx1 = null;
		ArrayList<String> vx2 = null;
		boolean hasDupes = true;
		String prev = "";
		String current = "";

		vx1 = new ArrayList<String>();
		vx2 = new ArrayList<String>();

		/**
		 * scan for and try and prevent "repeats"
		 */
		for (repeat = 0; hasDupes && repeat < 10; repeat++) {
			prev = "";
			vx1.clear();
			vx2.clear();
			hasDupes = false;
			for (ix = 0; ix < queue.size(); ix++) {
				if (queue.get(ix).compareTo("") == 0)
					continue;
				current = queue.get(ix);
				if (current.compareTo(prev) != 0) {
					vx1.add(current);
					prev = current;
				} else {
					vx2.add(current);
					hasDupes = true;
				}
			}
			queue.clear();
			queue.addAll(vx1);
			queue.addAll(vx2);
		}

		vx1.clear();
		vx2.clear();
	}

	public void setAnimalsSyl(HashMap<String, String> animalsSyl) {
		this.animalsSyl = animalsSyl;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
