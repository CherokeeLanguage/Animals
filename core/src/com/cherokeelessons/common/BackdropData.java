package com.cherokeelessons.common;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class BackdropData implements Disposable {
	private TextureAtlas textureAtlas;
	private Array<Image> images;
	private final Group group = new Group();
	public TextureAtlas getTextureAtlas() {
		return textureAtlas;
	}
	public void setTextureAtlas(TextureAtlas textureAtlas) {
		this.textureAtlas = textureAtlas;
	}
	public Array<Image> getImages() {
		return images;
	}
	public void setImages(Array<Image> images) {
		this.images = images;
	}
	@Override
	public void dispose() {
		if (images!=null) {
			for (Image image: images) {
				image.setDrawable(null);
			}
		}
		if (textureAtlas!=null) {
			textureAtlas.dispose();
		}
	}
	public Group getGroup() {
		return group;
	}
}