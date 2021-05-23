package com.cherokeelessons.animals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.cherokeelessons.common.Utils;

public class GraduatedIntervalQueue {

	public static class Point {

		protected int x, y;

		public Point() {

			setPoint(0, 0);

		}

		public Point(final int coordx, final int coordy) {
			setPoint(coordx, coordy);
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public void setPoint(final int coordx, final int coordy) {
			x = coordx;
			y = coordy;
		}

		public String toPrint() {
			return "[" + x + "," + y + "]";
		}

	}

	public static final class SortSizeAscendingAlpha implements Comparator<String> {
		@Override
		public int compare(String o1, String o2) {
			if (o1.length() < o2.length()) {
				return -1;
			}
			if (o1.length() > o2.length()) {
				return 1;
			}
			return o1.compareTo(o2);
		}
	}

	private boolean debug = true;

	private boolean doubleMode = false;

	private List<String> startingEntries;

	private List<String> intervalQueue;

	private ArrayList<Point> levelMarks;
	private boolean briefList = false;

	private boolean shortList = false;

	public GraduatedIntervalQueue() {
		super();
	}

	public String getEntry(final int ix) {
		if (intervalQueue.size() > ix && ix >= 0) {
			return intervalQueue.get(ix);
		}
		return null;
	}

	public List<String> getIntervalQueue() {
		return intervalQueue;
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

	int getLevelCount() {
		return levelMarks.size();
	}

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

	int getLevelEndPosition(final int level) {
		return levelMarks.get(level).y;
	}

	int getLevelStartPosition(final int level) {
		return levelMarks.get(level).x;
	}

	/**
	 * based on getOffsetsReal from 'translations.php'
	 *
	 * @return ArrayList<Integer>
	 */
	private ArrayList<Integer> getOffsets() {
		ArrayList<Integer> o1;
		int ip, depth = 6, stagger = 2, ix, basePower = 2;
		o1 = new ArrayList<>();

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

//	String getLevelStartName(int level) {
//		int position;
//		position = getLevelStartPosition(level);
//		return bounderiesByPosition.get(position);
//	}

	/**
	 * based on getOffsetsReal from 'translations.php'
	 *
	 * @return ArrayList<Integer>
	 */
	private ArrayList<Integer> getOffsetsDoubled() {
		ArrayList<Integer> o1;
		int ip;
		final int depth = 6, stagger = 4;
		int ix;

		o1 = new ArrayList<>();

		for (ix = 0; ix < stagger; ix++) {
			for (ip = 0; ip <= depth; ip++) {
				o1.add((int) Math.pow(2 + ix, ip));
			}
		}
		return o1;
	}

	private List<String> getQueue(final List<String> startingEntries2) {
		int ix, iy, ia;
		ArrayList<Integer> offsets;
		List<String> newQueue = null;
		List<String> samples;

		newQueue = new ArrayList<>();
		samples = new ArrayList<>();
		if (isDoubleMode()) {
			offsets = getOffsetsDoubled();
		} else {
			offsets = getOffsets();
		}

		samples.addAll(startingEntries2);

		/**
		 * process samples creating non-random work queue
		 */
		for (ix = 0; ix < samples.size(); ix++) {
			ia = 0;
			for (iy = 0; iy < offsets.size(); iy++) {
				while (newQueue.size() < ia + 1) {
					newQueue.add(null);
				}
				while (newQueue.get(ia) != null) {
					ia++;
					while (newQueue.size() < ia + 1) {
						newQueue.add(null);
					}
				}
				newQueue.set(ia, samples.get(ix));
				ia += offsets.get(iy);
			}
		}
		removeGaps(newQueue);

		return newQueue;
	}

	public boolean isBriefList() {
		return briefList;
	}

	public boolean isDebug() {
		return debug;
	}

	public boolean isDoubleMode() {
		return doubleMode;
	}

	public boolean isShortList() {
		return shortList;
	}

	public void load(final List<String> _startingEntries) {
		startingEntries = new ArrayList<>();
//		bounderiesNameList = new Array<String>();
//		bounderiesByName = new HashMap<String, Integer>();
//		bounderiesByPosition = new HashMap<Integer, String>();
		startingEntries.addAll(_startingEntries);
		intervalQueue = getQueue(startingEntries);
//		locateBounderies();
//		calculateLevelStarts(18);
	}

	public void removeGaps(final List<String> queue) {
		int ix = 0, repeat;
		List<String> vx1 = null;
		List<String> vx2 = null;
		boolean hasDupes = true;
		String prev = null;
		String current = null;

		vx1 = new ArrayList<>();
		vx2 = new ArrayList<>();

		/**
		 * scan for and try and prevent "repeats"
		 */
		for (repeat = 0; hasDupes && repeat < 10; repeat++) {
			prev = null;
			vx1.clear();
			vx2.clear();
			hasDupes = false;
			for (ix = 0; ix < queue.size(); ix++) {
				if (queue.get(ix) == null) {
					continue;
				}
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

	public void setBriefList(final boolean briefList) {
		this.briefList = briefList;
	}

	public void setDebug(final boolean debug) {
		this.debug = debug;
	}

//	private static String asLatin(String raw_text) {
//		if (raw_text==null) {
//			return null;
//		}
//		raw_text=raw_text.replace("-", "");
//		String text=raw_text.substring(0, 1).toUpperCase();
//		if (raw_text.length()>1) {
//			text += raw_text.substring(1);
//		}
//		return text;
//	}

	public void setDoubleMode(final boolean doubleMode) {
		this.doubleMode = doubleMode;
	}

	public void setShortList(final boolean shortList) {
		this.shortList = shortList;
	}
}
