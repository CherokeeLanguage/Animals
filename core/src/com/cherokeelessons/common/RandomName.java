package com.cherokeelessons.common;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.cherokeelessons.util.StringUtils;

public class RandomName {
	private static Random r = new Random();

	public static String getRandomName() {
		final String[] animals = Gdx.files.internal("text/animals-chr.txt").readString("UTF-8").split("\n");
		final String[] adjectives = Gdx.files.internal("text/adjectives-chr.txt").readString("UTF-8").split("\n");
		String name = animals[r.nextInt(animals.length)];
		if (r.nextInt(16) != 0) {
			name = adjectives[r.nextInt(adjectives.length)] + " " + name;
		}
		name = StringUtils.normalizeSpace(name);
		name = StringUtils.strip(name);
		return name;
	}
}
