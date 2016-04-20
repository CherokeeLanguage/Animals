package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class ScreenPaused extends ScreenGameCore {

	private LabelStyle displayStyle;
	private Label displayText;
	private BitmapFont font;
	private int fontSize = 48;
	private String msg = "Paused. Press [O] to resume.";
	private float scaleX = 1;

	private float scaleY = 1;

	private Pixmap screenShot;
	private Group underlay = new Group();

	public ScreenPaused(CherokeeAnimals game) {
		super(game);

		Pixmap pm = getStageshot();
		screenShot = new Pixmap(pm.getWidth(), pm.getHeight(),
				Pixmap.Format.RGBA8888);
		screenShot.drawPixmap(pm, 0, 0);
		pm.dispose();

		font = CherokeeAnimals.getFont(CherokeeAnimals.FontStyle.Script,fontSize);
		displayStyle = new LabelStyle();
		displayStyle.font = font;
		displayStyle.fontColor = GameColor.GREEN;
		displayText = new Label(msg, displayStyle);

		gameStage.addActor(underlay);
		gameStage.addActor(displayText);

		updateBackground();
		updateMessage();
	}

	private Pixmap getScreenshot(int x, int y, int w, int h, boolean flipY) {
		Pixmap pixmap;
		byte[] lines;

		lines = ScreenUtils.getFrameBufferPixels(x, y, w, h, flipY);
		pixmap = new Pixmap(w, h, Format.RGBA8888);
		pixmap.getPixels().clear();
		pixmap.getPixels().put(lines);

		return pixmap;
	}

	private Pixmap getStageshot() {
		Pixmap pixmap;
		byte[] lines;

		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		lines = ScreenUtils.getFrameBufferPixels(0, 0, width, height, false);
		pixmap = new Pixmap(width, height, Format.RGBA8888);
		pixmap.getPixels().clear();
		pixmap.getPixels().put(lines);
		System.out.println("getStageshot size: " + pixmap.getWidth() + "x"
				+ pixmap.getHeight());
		final float viewportWidth = game.screenSize.x;
		final float viewportHeight = game.screenSize.y;
		System.out
				.println("game size: " + viewportWidth + "x" + viewportHeight);
		scaleX = viewportWidth / pixmap.getWidth();
		scaleY = viewportHeight / pixmap.getHeight();
		return pixmap;
	}

	@Override
	public void hide() {
		for (Texture t: textureCache) {
			try {
				t.dispose();
			} catch (Exception e) {}
		}
		textureCache.clear();
		super.hide();
	}

	@Override
	public void resume() {
		super.resume();
		updateBackground();
		updateMessage();
	}

	@Override
	public void show() {
		super.show();
		updateBackground();
		updateMessage();
	}

	public void takeScreenshot() {
		Pixmap screen;
		float x;
		float y;
		float scale = 1;
		float newWidth;
		float newHeight;
		int width, height;

		System.out.println("take screenshot");
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();

		/*
		 * http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=3422
		 */
		scale = (float) screenWidth / (float) width;
		if ((float) screenHeight / (float) height > scale) {
			scale = (float) screenHeight / (float) height;
		}
		newWidth = (float) Math.floor(screenWidth / scale);
		newHeight = (float) Math.floor(screenHeight / scale);

		x = (width - newWidth) / 2f;
		y = (height - newHeight) / 2f;

		screen = getScreenshot(0, 0, width, height, false);

		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		if (newWidth > screen.getWidth()) {
			newWidth = screen.getWidth();
		}
		if (newHeight > screen.getHeight()) {
			newHeight = screen.getHeight();
		}

		screenShot.setColor(Color.WHITE);
		screenShot.fill();
		screenShot.drawPixmap(screen, (int) x, (int) y, (int) newWidth,
				(int) newHeight, 0, 0, screenShot.getWidth(),
				screenShot.getHeight());
		screen.dispose();

	}

	final private Array<Texture> textureCache = new Array<Texture>();
	private void updateBackground() {
		int slice = 64;
		int w = screenShot.getWidth();
		int h = screenShot.getHeight();

		underlay.clear();
		underlay.setTransform(true);
		underlay.setOrigin(w / 2, h / 2);
		if (scaleX > scaleY) {
			underlay.setScale(scaleX);
		} else {
			underlay.setScale(scaleY);
		}
		underlay.setPosition(((game.screenSize.x - w) / 2),
				((game.screenSize.y - h) / 2));
		underlay.getColor().a = .25f;
		for (int x = 0; x < w; x += slice) {
			for (int y = 0; y < h; y += slice) {
				int pw = w - x;
				int ph = h - y;
				if (pw > slice)
					pw = slice;
				if (ph > slice)
					ph = slice;
				Pixmap pSlice = new Pixmap(pw, ph, Format.RGBA8888);
				pSlice.setColor(Color.BLACK);
				pSlice.fill();
				pSlice.drawPixmap(screenShot, 0, 0, x, y, pw, ph);
				Texture t = new Texture(pSlice);
				textureCache.add(t);
				t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				TextureRegion r = new TextureRegion(t);
				Image i = new Image(r);
				i.pack();
				i.setOrigin(i.getWidth() / 2, i.getHeight() / 2);
				i.setScaleY(-1f);
				i.setScaleX(1f);
				i.layout();
				i.setX(x);
				i.setY(y);
				underlay.addActor(i);
				pSlice.dispose();
			}
		}
	}

	private void updateMessage() {
		displayText.setStyle(displayStyle);
		displayText.setText(msg);
		displayText.pack();
		displayText.setX((screenWidth - displayText.getWidth()) / 2);
		displayText.setY((screenHeight - displayText.getHeight()) / 2);
	}

}
