package com.cherokeelessons.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class Attributions extends Group {
	final private Rectangle bbox=new Rectangle();
	private String[] credits;

	private FontLoader fg;
	private Color fontColor = new Color(Color.BLACK);
	private int fontSize=88;
	private float maxLineHeight;

	private TextButton[] scrollingCredits;

	private float xOffset = 0;

	private float yOffset = 0;

	public Attributions(Rectangle screenSize) {
		super();
		this.bbox.set(screenSize);
		String tmp = Gdx.files.internal("data/credits.txt").readString("UTF-8");
		credits = tmp.split("\n");
	}

	public float getxOffset() {
		return xOffset;
	}

	/**
	 * @return the yOffset
	 */
	public float getyOffset() {
		return yOffset;
	}

	public void init() {
		fg = new FontLoader();
		scrollingCredits = new TextButton[credits.length];
		populateCreditDisplay();

	}

	private void populateCreditDisplay() {
		int ix = 0;
		BitmapFont font;
		maxLineHeight = 0;
		TextButton creditLine;
		TextButtonStyle style;
		font = fg.get(fontSize);
		for (ix = 0; ix < credits.length; ix++) {
			if (credits[ix].length() < 1) {
				scrollingCredits[ix] = null;
				continue;
			}
			style = new TextButtonStyle();
			style.font = font;
			style.fontColor = new Color(fontColor);
			creditLine = new TextButton(credits[ix], style);
			creditLine.pack();
			scrollingCredits[ix] = creditLine;
			if (font.getLineHeight() > maxLineHeight)
				maxLineHeight = font.getLineHeight();

		}
		for (ix = 0; ix < credits.length; ix++) {
			creditLine = scrollingCredits[ix];
			if (creditLine == null) {
				continue;
			}
			creditLine.setX(xOffset + (bbox.width - creditLine.getWidth()) / 2);
			creditLine
					.setY(yOffset + (credits.length - ix - 1) * maxLineHeight);
			this.addActor(creditLine);
		}
		// store my dimensions
		setWidth(bbox.width);
		setHeight(maxLineHeight * credits.length);
		reset();
	}

	public void reset() {
		// move myself below screen
		clearActions();
		setY(-getHeight());
	}

	private Runnable onDone;
	
	public Runnable getOnDone() {
		return onDone;
	}

	public void setOnDone(Runnable onDone) {
		this.onDone = onDone;
	}

	public void scroll(float time) {
		reset();
		SequenceAction seq = Actions.sequence();
		seq.addAction(Actions.moveTo(0, bbox.height + maxLineHeight * 2, time));
		seq.addAction(Actions.run(new Runnable() {
			@Override
			public void run() {
				if (onDone!=null) {
					Gdx.app.postRunnable(onDone);
				}
			}
		}));
		addAction(seq);
	}

	public void setFontColor(Color color) {
		this.fontColor = color;
	}

	public void setxOffset(float xOffset) {
		this.xOffset = xOffset;
	}

	/**
	 * @param yOffset
	 *            the yOffset to set
	 */
	public void setyOffset(float yOffset) {
		this.yOffset = yOffset;
	}
}
