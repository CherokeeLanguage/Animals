package com.cherokeelessons.animals.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ViewProgressBar extends Group {
	enum Position {
		Bottom, Top
	}

	final private Rectangle bbox = new Rectangle();
	private final Image botLayer;

	private final FileHandle botLayerFH;
	private final TextureRegion botLayerR;
	private final Texture botLayerT;

	private final Image midLayer;
	private final FileHandle midLayerFH;
	private final TextureRegion midLayerR;

	private final Texture midLayerT;
	private Position position;
	private float progress1;

	private float progress2;
	private float progress3;

	private final float scaleWidth;

	private float sideMargins = 20;

	private float topBottomMargins = 15;

	private final Image topLayer;

	private final FileHandle topLayerFH;

	private final TextureRegion topLayerR;

	private final Texture topLayerT;

	private boolean visible;

	public ViewProgressBar(final Rectangle overscan) {
		super();
		float scaleBy;

		bbox.set(overscan);

		topLayerFH = Gdx.files.internal("buttons/002d_gold2.png");
		midLayerFH = Gdx.files.internal("buttons/002d_gold3.png");
		botLayerFH = Gdx.files.internal("buttons/002d_cornsilk4.png");

		botLayerT = new Texture(botLayerFH);
		midLayerT = new Texture(midLayerFH);
		topLayerT = new Texture(topLayerFH);

		topLayerR = new TextureRegion(topLayerT);
		midLayerR = new TextureRegion(midLayerT);
		botLayerR = new TextureRegion(botLayerT);

		topLayer = new Image(topLayerR);
		midLayer = new Image(midLayerR);
		botLayer = new Image(botLayerR);

		topLayer.getColor().a = 1f;
		midLayer.getColor().a = 1f;
		botLayer.getColor().a = 1f;

		botLayer.pack();
		midLayer.pack();
		topLayer.pack();

		addActor(botLayer);
		addActor(midLayer);
		addActor(topLayer);

		setVisible(true);
		setHeight(20);
		setTouchable(Touchable.disabled);
		setSideMargins(7);
		setTopBottomMargins(7);

		calculateDimensions();
		setPosition(Position.Top);

		scaleBy = getHeight() / botLayer.getHeight();
		botLayer.setScaleY(scaleBy);
		midLayer.setScaleY(scaleBy);
		topLayer.setScaleY(scaleBy);
		scaleWidth = getWidth() / botLayer.getWidth();

		setProgress1(0f);
		setProgress2(0f);
		setProgress3(1f);
	}

	private void calculateDimensions() {
		setWidth(bbox.width - sideMargins * 2);
		setX(sideMargins);
	}

	public Position getPosition() {
		return position;
	}

	public float getProgress1() {
		return progress1;
	}

	public float getProgress2() {
		return progress2;
	}

	public float getProgress3() {
		return progress3;
	}

	public float getSideMargins() {
		return sideMargins;
	}

	public float getTopBottomMargins() {
		return topBottomMargins;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	public void setPosition(final Position position) {
		this.position = position;
		switch (position) {
		case Top:
			setY(bbox.height - getHeight() - topBottomMargins);
			break;
		case Bottom:
			setY(topBottomMargins);
			break;
		}
	}

	public void setProgress1(final float progress1) {
		this.progress1 = progress1;
		topLayer.setScaleX(scaleWidth * progress1);
		topLayer.pack();
	}

	public void setProgress2(final float progress2) {
		this.progress2 = progress2;
		midLayer.setScaleX(scaleWidth * progress2);
		midLayer.pack();
	}

	public void setProgress3(final float progress3) {
		this.progress3 = progress3;
		botLayer.setScaleX(scaleWidth * progress3);
		botLayer.pack();
	}

	public void setSideMargins(final float sideMargins) {
		this.sideMargins = sideMargins;
	}

	public void setTopBottomMargins(final float topBottomMargins) {
		this.topBottomMargins = topBottomMargins;
	}

	@Override
	public void setVisible(final boolean visible) {
		this.visible = visible;
	}
}
