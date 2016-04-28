package com.cherokeelessons.common;

import java.util.Arrays;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontGenerator {
	
	public FontGenerator() {
		
		int packSize=Utils.getPackSize();
		
		largestFont = 12*(packSize/256);
		if (largestFont==0) {
			largestFont=12;
		}
	}

	public static final String FONT_SANS = "fonts/FreeSans.otf";
	public static final String FONT_ROMAN = "fonts/FreeSerif.otf";

	private static HashMap<String, BitmapFont> fontCache = new HashMap<String, BitmapFont>();

	public final static String GlyphsEnglish = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890\"!`?'.,;:()[]{}<>|/@\\^$-%+=#_&~*";

	private static int largestFont = 30;

	public static void clear() {
		for (BitmapFont font : fontCache.values()) {
			try {
				font.dispose();
			} catch (Exception e) {
			}
		}
		fontCache.clear();
	}

	private FileHandle mainFont;

	public BitmapFont gen(int fontSize) {
		String esperanto = "ĈĜĤĴŜŬĉĝĥĵŝŭ";
		char[] jalagi = new char['Ᏼ' - 'Ꭰ' + 1];
		char ix;

		for (ix = 'Ꭰ'; ix <= 'Ᏼ'; ix++) {
			jalagi[ix - 'Ꭰ'] = ix;
		}

		return gen(fontSize, GlyphsEnglish + String.valueOf(jalagi) + esperanto);
	}

	public BitmapFont gen(int fontSize, String glyphs) {
		int requested_size = fontSize;
		char[] letters;
		int ix;
		FreeTypeFontGenerator fontGen;
		BitmapFont font;
		String cacheKey;
		float scale = 1;

		if (mainFont == null) {
			mainFont = Gdx.files.internal(FONT_SANS);
		}

		letters = glyphs.toCharArray();
		Arrays.sort(letters);
		for (ix = 0; ix < letters.length - 1; ix++) {
			if (letters[ix] == letters[ix + 1]) {
				letters[ix] = 0;
			}
		}
		glyphs = "";
		for (ix = 0; ix < letters.length; ix++) {
			if (letters[ix] > 0) {
				glyphs += Character.toString(letters[ix]);
			}
		}
		if (glyphs.length() < 1) {
			glyphs = "0";
		}

		cacheKey = mainFont.file() + "|" + requested_size + "|" + glyphs;
		if (fontCache.containsKey(cacheKey)) {
			return fontCache.get(cacheKey);
		}

		fontGen = new FreeTypeFontGenerator(mainFont);
		if (fontSize > largestFont) {
			scale = (float) fontSize / (float) largestFont;
			fontSize = largestFont;
		}
		font = fontGen.generateFont(fontSize, glyphs, false);
		font.setScale(scale);
		font.getRegion().getTexture()
				.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		fontGen.dispose();
		fontCache.put(cacheKey, font);
		String size = font.getRegion().getRegionWidth() + "x"
				+ font.getRegion().getRegionHeight();
		Gdx.app.log(this.getClass().getSimpleName(), "New cached font: "
				+ mainFont.name() + "|" + requested_size + ", texturesize: "
				+ size);
		return font;
	}

	public BitmapFont genFixedNumbers(int fontSize) {
		String glyphs;
		char[] jalagi = new char['Ᏼ' - 'Ꭰ' + 1];
		char ix;

		glyphs = FreeTypeFontGenerator.DEFAULT_CHARS;

		for (ix = 'Ꭰ'; ix <= 'Ᏼ'; ix++) {
			jalagi[ix - 'Ꭰ'] = ix;
		}

		return genFixedNumbers(fontSize, glyphs + String.valueOf(jalagi));
	}

	public BitmapFont genFixedNumbers(int fontSize, String glyphs) {
		int requested_size = fontSize;
		char[] letters;
		int ix;
		FreeTypeFontGenerator fontGen;
		BitmapFont font;
		String cacheKey;
		float scale = 1;

		if (mainFont == null) {
			mainFont = Gdx.files.internal(FONT_SANS);
		}

		letters = glyphs.toCharArray();
		Arrays.sort(letters);
		for (ix = 0; ix < letters.length - 1; ix++) {
			if (letters[ix] == letters[ix + 1]) {
				letters[ix] = 0;
			}
		}
		glyphs = "";
		for (ix = 0; ix < letters.length; ix++) {
			if (letters[ix] > 0) {
				glyphs += Character.toString(letters[ix]);
			}
		}
		if (glyphs.length() < 1) {
			glyphs = "0";
		}

		cacheKey = mainFont.file() + "|" + requested_size + "|"
				+ "fixed numbers" + "|" + glyphs;
		if (fontCache.containsKey(cacheKey)) {
			return fontCache.get(cacheKey);
		}

		fontGen = new FreeTypeFontGenerator(mainFont);
		if (fontSize > largestFont) {
			scale = (float) fontSize / (float) largestFont;
			fontSize = largestFont;
		}
		font = fontGen.generateFont(fontSize, glyphs, false);
		font.setScale(scale);
		font.getRegion().getTexture()
				.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		font.setFixedWidthGlyphs("0123456789+=-%");
		fontGen.dispose();
		String size = font.getRegion().getRegionWidth() + "x"
				+ font.getRegion().getRegionHeight();
		Gdx.app.log(this.getClass().getSimpleName(), "New cached font: "
				+ mainFont.name() + "|" + requested_size + "|"
				+ "fixed numbers" + ", texturesize: " + size);
		fontCache.put(cacheKey, font);
		return font;
	}

	public FileHandle getMainFont() {
		return mainFont;
	}

	public void setMainFont(FileHandle mainFont) {
		this.mainFont = mainFont;
	}

	public void setMainFont(String mainFont) {
		setMainFont(Gdx.files.internal(mainFont));
	}
}
