package com.cherokeelessons.vocab.animals.one;

import java.util.ArrayList;
import java.util.Comparator;

import com.badlogic.gdx.utils.Array;

public class GraduatedIntervalQueue {

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	private boolean debug = true;

	private boolean doubleMode = false;

	public boolean isDoubleMode() {
		return doubleMode;
	}

	public void setDoubleMode(boolean doubleMode) {
		this.doubleMode = doubleMode;
	}

	private Array<String> startingEntries;
	private Array<String> intervalQueue;

	public Array<String> getIntervalQueue() {
		return intervalQueue;
	}

	public String getEntry(int ix) {
		if (intervalQueue.size > ix && ix >= 0) {
			return intervalQueue.get(ix);
		}
		return null;
	}

	public GraduatedIntervalQueue() {
		super();
	}

	public void load(Array<String> _startingEntries) {
		startingEntries = new Array<String>();
//		bounderiesNameList = new Array<String>();
//		bounderiesByName = new HashMap<String, Integer>();
//		bounderiesByPosition = new HashMap<Integer, String>();
		startingEntries.addAll(_startingEntries);
		intervalQueue = getQueue(startingEntries);
//		locateBounderies();
//		calculateLevelStarts(18);
	}

//	private Array<String> bounderiesNameList;
//	private HashMap<Integer, String> bounderiesByPosition;
//	private HashMap<String, Integer> bounderiesByName;

//	private void locateBounderies() {
//		int ix, len;
//		String item;
//
//		for (ix = 0, len = intervalQueue.size; ix < len; ix++) {
//			item = intervalQueue.get(ix);
//			if (bounderiesNameList.contains(item, false)) {
//				continue;
//			}
//			bounderiesNameList.add(item);
//			bounderiesByName.put(item, ix);
//			bounderiesByPosition.put(ix, item);
//		}
//	}

	private ArrayList<Point> levelMarks;

//	public void calculateLevelStarts(int levels) {
//		int startingPoint;
//		String item;
//		int listSize = 0;
//		int ix, index;
//
//		levelMarks = new ArrayList<Point>();
//		Point thisLevelMarks = null;
//		Point prevLevelMarks = null;
//
//		listSize = bounderiesNameList.size;
//		for (ix = 0; ix < levels; ix++) {
//			index = (int) Math.ceil((float) listSize
//					* ((float) ix / (float) levels));
//			if (index >= bounderiesNameList.size)
//				continue;
//			item = bounderiesNameList.get(index);
//			startingPoint = bounderiesByName.get(item);
//			thisLevelMarks = new Point(startingPoint, intervalQueue.size - 1);
//			if (prevLevelMarks != null) {
//				prevLevelMarks.y = startingPoint - 1;
//			}
//			prevLevelMarks = thisLevelMarks;
//			levelMarks.add(thisLevelMarks);
//		}
//	}

	int getLevelCount() {
		return levelMarks.size();
	}

	int getLevelStartPosition(int level) {
		return (int) levelMarks.get(level).x;
	}

	int getLevelEndPosition(int level) {
		return (int) levelMarks.get(level).y;
	}

//	String getLevelStartName(int level) {
//		int position;
//		position = getLevelStartPosition(level);
//		return bounderiesByPosition.get(position);
//	}

	public void removeGaps(Array<String> queue) {
		int ix = 0, repeat;
		Array<String> vx1 = null;
		Array<String> vx2 = null;
		boolean hasDupes = true;
		String prev = null;
		String current = null;

		vx1 = new Array<String>();
		vx2 = new Array<String>();

		/**
		 * scan for and try and prevent "repeats"
		 */
		for (repeat = 0; hasDupes && repeat < 10; repeat++) {
			prev = null;
			vx1.clear();
			vx2.clear();
			hasDupes = false;
			for (ix = 0; ix < queue.size; ix++) {
				if (queue.get(ix) == null)
					continue;
				current = queue.get(ix);
				if (!current.equals(prev)) {
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

	private boolean briefList = false;

	public boolean isBriefList() {
		return briefList;
	}

	public void setBriefList(boolean briefList) {
		this.briefList = briefList;
	}

	private boolean shortList = false;

	public boolean isShortList() {
		return shortList;
	}

	public void setShortList(boolean shortList) {
		this.shortList = shortList;
	}

	/**
	 * based on getOffsetsReal from 'translations.php'
	 * 
	 * @return ArrayList<Integer>
	 */
	private ArrayList<Integer> getOffsets() {
		ArrayList<Integer> o1;
		int ip, depth = 6, stagger = 2, ix, basePower = 2;
		o1 = new ArrayList<Integer>();

		if (isBriefList()) {
			depth = 6;
			stagger = 1;
			basePower = 3;
		}

		if (isShortList()) {
			depth = 2;
			stagger = 1;
			basePower = 2;
		}

		for (ix = 0; ix < stagger; ix++) {
			for (ip = 0; ip <= depth; ip++) {
				o1.add((int) Math.pow(basePower + ix, ip));
			}
		}
		return o1;
	}

	/**
	 * based on getOffsetsReal from 'translations.php'
	 * 
	 * @return ArrayList<Integer>
	 */
	private ArrayList<Integer> getOffsetsDoubled() {
		ArrayList<Integer> o1;
		int ip, depth = 6, stagger = 4, ix;

		o1 = new ArrayList<Integer>();

		for (ix = 0; ix < stagger; ix++) {
			for (ip = 0; ip <= depth; ip++) {
				o1.add((int) Math.pow(2 + ix, ip));
			}
		}
		return o1;
	}

	private Array<String> getQueue(Array<String> startingEntries2) {
		int ix, iy, ia;
		ArrayList<Integer> offsets;
		Array<String> newQueue = null;
		Array<String> samples;

		newQueue = new Array<String>();
		samples = new Array<String>();
		if (isDoubleMode()) {
			offsets = getOffsetsDoubled();
		} else {
			offsets = getOffsets();
		}

		samples.addAll(startingEntries2);

		/**
		 * process samples creating non-random work queue
		 */
		for (ix = 0; ix < samples.size; ix++) {
			ia = 0;
			for (iy = 0; iy < offsets.size(); iy++) {
				while (newQueue.size < ia + 1)
					newQueue.add(null);
				while (newQueue.get(ia) != null) {
					ia++;
					while (newQueue.size < ia + 1)
						newQueue.add(null);
				}
				newQueue.set(ia, samples.get(ix));
				ia += offsets.get(iy);
			}
		}
		removeGaps(newQueue);

		return newQueue;
	}

	private static String asLatin(String raw_text) {
		if (raw_text==null) {
			return null;
		}
		raw_text=raw_text.replace("-", "");
		String text=raw_text.substring(0, 1).toUpperCase();
		if (raw_text.length()>1) {
			text += raw_text.substring(1);
		}
		return text;
	}

	public static final class SortSizeAscendingAlpha implements
			Comparator<String> {
		@Override
		public int compare(String o1, String o2) {
			o1 = asLatin(o1);
			o2 = asLatin(o2);
			if (o1.length() < o2.length())
				return -1;
			if (o1.length() > o2.length())
				return 1;
			return (o1.compareTo(o2));
		}
	}

	public static class Point {

		protected int x, y;

		public Point() {

			setPoint(0, 0);

		}

		public Point(int coordx, int coordy) {
			setPoint(coordx, coordy);
		}

		public void setPoint(int coordx, int coordy) {
			x = coordx;
			y = coordy;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public String toPrint() {
			return "[" + x + "," + y + "]";
		}

	}

	public static String asSyllabary(String currentChallenge) {
		// TODO Auto-generated method stub
		return null;
	}

}
