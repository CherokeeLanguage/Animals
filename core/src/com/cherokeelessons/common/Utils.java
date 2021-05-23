package com.cherokeelessons.common;

import java.nio.IntBuffer;

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
import com.cherokeelessons.animals.ChallengeLookup;
import com.cherokeelessons.util.StringUtils;

public class Utils {
	
	public static ChallengeLookup lookup;
	
	public static void setChallengeLookup(ChallengeLookup lookup) {
		Utils.lookup = lookup;
	}

	public static String asLatin(String challenge) {
		if (StringUtils.isBlank(challenge)) {
			return challenge;
		}
		if (!lookup.latin.containsKey(challenge)) {
			return challenge;
		}
		return lookup.latin.get(challenge);
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

	public static BackdropData backdrop() {
		BackdropData _backdropData;
		_backdropData=initBackdrop();
		Group backdropGroup = _backdropData.getGroup();
		backdropGroup.setOrigin(DisplaySize._1080p.size().width/2, DisplaySize._1080p.size().height/2);
		backdropGroup.setSize(DisplaySize._1080p.size().width, DisplaySize._1080p.size().height);
		backdropGroup.setColor(1f, 1f, 1f, 0.35f);
		_backdropData.getGroup().setScale(1.21f);
		return _backdropData;
	}
	
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

	public Utils() {
	}
}
