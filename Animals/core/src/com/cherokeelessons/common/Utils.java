package com.cherokeelessons.common;

import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;

public class Utils {

	public Utils() {
	}

	public static TextureAtlas initBackdrop(Array<Sprite> wall) {
		TextureAtlas wall_atlas;
		IntBuffer buf = BufferUtils.newIntBuffer(16);
		Gdx.gl.glGetIntegerv(GL20.GL_MAX_TEXTURE_SIZE, buf);
		int packSize = buf.get();
		if (packSize > 1024)
			packSize = 1024;
		wall.clear();
		PixmapPacker pack = new PixmapPacker(packSize, packSize, Format.RGBA8888, 2, true);
		for (int i = 0; i < 32; i++) {
			Pixmap image = new Pixmap(Gdx.files.internal("images/backdrops/p_" + i + "_dsci2549.png"));
			pack.pack(i + "", image);
		}
		wall_atlas = pack.generateTextureAtlas(TextureFilter.Linear, TextureFilter.Linear, false);

		int px = 0;
		int py = 0;
		final int perRow = 8;
		final int columns = 4;
		for (int x = 0; x < perRow; x++) {
			py = 0;
			Sprite i = null;
			for (int y = 0; y < columns; y++) {
				int z = columns - (y + 1);
				int p = z * perRow + x;
				final AtlasRegion piece = wall_atlas.findRegion(p + "");
				i = new Sprite(piece, 0, 0, piece.getRegionWidth(), piece.getRegionHeight());
				i.setX(px);
				i.setY(py);
				i.setColor(1f, 1f, 1f, 0.35f);
				py += i.getHeight();
				wall.add(i);
			}
			px += i.getWidth();
		}
		return wall_atlas;
	}

	public static int getPackSize() {
		IntBuffer buf = BufferUtils.newIntBuffer(16);
		Gdx.gl.glGetIntegerv(GL20.GL_MAX_TEXTURE_SIZE, buf);
		int packSize = buf.get();
		if (packSize > 1024)
			packSize = 1024;
		if (packSize == 0) {
			packSize = 512;
		}
		return packSize;
	}

}
