package com.cherokeelessons.common;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class FontLoader {
	
	private final AssetManager fonts;
	private final AssetManager fonts_fixed;
	private final BitmapFontParameter param;
	private final Map<String, BitmapFont> cache_fonts;
	private final Map<String, BitmapFont> cache_fonts_fixed;
	public FontLoader() {
		fonts = new AssetManager();
		fonts_fixed = new AssetManager();
		param=new BitmapFontParameter();
		param.magFilter=TextureFilter.Linear;
		param.minFilter=TextureFilter.Linear;
		cache_fonts=new HashMap<String, BitmapFont>();
		cache_fonts_fixed=new HashMap<String, BitmapFont>();
	}

	public BitmapFont get(int size) {
		String fileName = "fonts/script-heavy-"+size+".fnt";
		Gdx.app.log(this.getClass().getName(), "get: "+fileName);
		if (!fonts.isLoaded(fileName)) {
			fonts.load(fileName, BitmapFont.class, param);
			fonts.finishLoading();
			BitmapFont bitmapFont = fonts.get(fileName, BitmapFont.class);
			cache_fonts.put(fileName, bitmapFont);
		}
		return cache_fonts.get(fileName);
	}

	public BitmapFont getFixed(int size) {
		String fileName = "fonts/script-heavy-"+size+".fnt";
		Gdx.app.log(this.getClass().getName(), "getFixed: "+fileName);
		if (!fonts_fixed.isLoaded(fileName)) {
			fonts_fixed.load(fileName, BitmapFont.class, param);
			fonts_fixed.finishLoading();
			BitmapFont bitmapFont = fonts_fixed.get(fileName, BitmapFont.class);
			bitmapFont.setFixedWidthGlyphs("01234567890-+");
			cache_fonts_fixed.put(fileName, bitmapFont);
		}
		return cache_fonts_fixed.get(fileName);
	}

}
