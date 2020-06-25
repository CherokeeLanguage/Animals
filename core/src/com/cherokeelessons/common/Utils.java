package com.cherokeelessons.common;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.cherokeelessons.animals.CherokeeAnimals;
import com.cherokeelessons.util.StringUtils;

public class Utils {

	private static Map<String, String> syllabaryMap = null;

	public static String asLatin(String raw_text) {
		if (raw_text == null) {
			return null;
		}
		raw_text = raw_text.replace("-", "");
		String text = raw_text.substring(0, 1).toUpperCase();
		if (raw_text.length() > 1) {
			text += raw_text.substring(1);
		}
		return text;
	}

	public static String asSyllabary(String string) {
		if (StringUtils.isBlank(string)) {
			return string;
		}
		string = invertSyllabary(string);
		string = string.replaceAll("[^Ꭰ-Ᏼ ]", "");
		return string;
	}

	public static int getPackSize() {
		final IntBuffer buf = BufferUtils.newIntBuffer(16);
		Gdx.gl.glGetIntegerv(GL20.GL_MAX_TEXTURE_SIZE, buf);
		int packSize = buf.get();
		if (packSize > 1024) {
			packSize = 1024;
		}
		if (packSize == 0) {
			packSize = 512;
		}
		return packSize;
	}

	public static BackdropData backdrop(CherokeeAnimals game) {
		if (_backdropData==null) {
			_backdropData=initBackdrop();
			Group backdropGroup = _backdropData.getGroup();
			backdropGroup.setOrigin(DisplaySize._1080p.size().width/2, DisplaySize._1080p.size().height/2);
			backdropGroup.setSize(DisplaySize._1080p.size().width, DisplaySize._1080p.size().height);
			backdropGroup.setColor(1f, 1f, 1f, 0.35f);
		}
		_backdropData.getGroup().setScale(1.21f);
		return _backdropData;
	}
	private static BackdropData _backdropData;
	private static BackdropData initBackdrop() {
		final Array<Image> wall = new Array<>();
		final int packSize = Utils.getPackSize();
		wall.clear();
		final PixmapPacker pack = new PixmapPacker(packSize, packSize, Format.RGBA8888, 2, true);
		for (int i = 0; i < 32; i++) {
			final Pixmap image = new Pixmap(Gdx.files.internal("images/backdrops/p_" + i + "_dsci2549.png"));
			pack.pack(i + "X", image);
		}
		final TextureAtlas atlas = pack.generateTextureAtlas(TextureFilter.Linear, TextureFilter.Linear, false);

		int px = 0;
		int py = 0;
		final int perRow = 8;
		final int columns = 4;
		for (int x = 0; x < perRow; x++) {
			py = 0;
			Image i = null;
			for (int y = 0; y < columns; y++) {
				final int z = columns - (y + 1);
				final int p = z * perRow + x;
				final AtlasRegion piece = atlas.findRegion(p + "X");
				i = new Image(piece);
				i.setX(px);
				i.setY(py);
				py += i.getHeight();
				wall.add(i);
			}
			if (i!=null) {
				px += i.getWidth();
			}
		}
		BackdropData data = new BackdropData();
		data.setImages(wall);
		data.setTextureAtlas(atlas);
		for (Image image: data.getImages()) {
			data.getGroup().addActor(image);
		}
		return data;
	}

	public static void initTranslationMap() {
		syllabaryMap = translationMap();
	}

	private static String invertSyllabary(String string) {
		String newString = "";
		String key = "";

		while (string.length() > 0) {
			if (string.length() >= 3) {
				key = string.substring(0, 3);
				if (syllabaryMap.containsKey(key)) {
					newString += syllabaryMap.get(key);
					string = string.substring(3);
					continue;
				}
			}
			if (string.length() >= 2) {
				key = string.substring(0, 2);
				if (syllabaryMap.containsKey(key)) {
					newString += syllabaryMap.get(key);
					string = string.substring(2);
					continue;
				}
			}
			if (string.length() >= 1) {
				key = string.substring(0, 1);
				if (syllabaryMap.containsKey(key)) {
					newString += syllabaryMap.get(key);
					string = string.substring(1);
					continue;
				}
				newString += key;
				string = string.substring(1);
			}
		}
		return newString;
	}

	private static Map<String, String> translationMap() {
		int ix = 0;
		String letter;
		String prefix;
		char chrStart = 'Ꭰ';
		final String[] vowels = new String[6];

		final HashMap<String, String> syllabary2latin = new HashMap<>();

		vowels[0] = "a";
		vowels[1] = "e";
		vowels[2] = "i";
		vowels[3] = "o";
		vowels[4] = "u";
		vowels[5] = "v";

		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, vowels[ix]);
			syllabary2latin.put(vowels[ix], letter);
		}

		syllabary2latin.put("ga", "Ꭶ");
		syllabary2latin.put("Ꭶ", "ga");

		syllabary2latin.put("ka", "Ꭷ");
		syllabary2latin.put("Ꭷ", "ka");

		prefix = "g";
		chrStart = 'Ꭸ';
		for (ix = 1; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix - 1));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "k";
		chrStart = 'Ꭸ';
		for (ix = 1; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix - 1));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "h";
		chrStart = 'Ꭽ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "l";
		chrStart = 'Ꮃ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "m";
		chrStart = 'Ꮉ';
		for (ix = 0; ix < 5; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		syllabary2latin.put("Ꮎ", "na");
		syllabary2latin.put("na", "Ꮎ");
		syllabary2latin.put("Ꮏ", "hna");
		syllabary2latin.put("hna", "Ꮏ");
		syllabary2latin.put("Ꮐ", "nah");
		syllabary2latin.put("nah", "Ꮐ");

		prefix = "n";
		chrStart = 'Ꮑ';
		for (ix = 1; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix - 1));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "qu";
		chrStart = 'Ꮖ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "gw";
		chrStart = 'Ꮖ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		syllabary2latin.put("Ꮜ", "sa");
		syllabary2latin.put("sa", "Ꮜ");
		syllabary2latin.put("Ꮝ", "s");
		syllabary2latin.put("s", "Ꮝ");

		prefix = "s";
		chrStart = 'Ꮞ';
		for (ix = 1; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix - 1));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		syllabary2latin.put("da", "Ꮣ");
		syllabary2latin.put("Ꮣ", "da");
		syllabary2latin.put("ta", "Ꮤ");
		syllabary2latin.put("Ꮤ", "ta");
		syllabary2latin.put("de", "Ꮥ");
		syllabary2latin.put("Ꮥ", "de");
		syllabary2latin.put("te", "Ꮦ");
		syllabary2latin.put("Ꮦ", "te");
		syllabary2latin.put("di", "Ꮧ");
		syllabary2latin.put("Ꮧ", "di");
		syllabary2latin.put("ti", "Ꮨ");
		syllabary2latin.put("Ꮨ", "ti");
		syllabary2latin.put("do", "Ꮩ");
		syllabary2latin.put("Ꮩ", "do");
		syllabary2latin.put("to", "Ꮩ");
		syllabary2latin.put("Ꮩ", "to");
		syllabary2latin.put("du", "Ꮪ");
		syllabary2latin.put("Ꮪ", "du");
		syllabary2latin.put("tu", "Ꮪ");
		syllabary2latin.put("Ꮪ", "tu");
		syllabary2latin.put("dv", "Ꮫ");
		syllabary2latin.put("Ꮫ", "dv");
		syllabary2latin.put("tv", "Ꮫ");
		syllabary2latin.put("Ꮫ", "tv");
		syllabary2latin.put("dla", "Ꮬ");
		syllabary2latin.put("Ꮬ", "dla");

		prefix = "hl";
		chrStart = 'Ꮭ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "tl";
		chrStart = 'Ꮭ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "j";
		chrStart = 'Ꮳ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "ts";
		chrStart = 'Ꮳ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "w";
		chrStart = 'Ꮹ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "y";
		chrStart = 'Ꮿ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}
		return syllabary2latin;
	}

	public Utils() {
	}

}
