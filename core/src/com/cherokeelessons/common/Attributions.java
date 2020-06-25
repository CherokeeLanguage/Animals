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
import com.badlogic.gdx.utils.Array;

public class Attributions extends Group {
	private final String[] credits;

	private final FontLoader fg;
	private Color fontColor = new Color(Color.BLACK);
	private final int fontSize = 88;
	private float maxLineHeight;

	private final Array<TextButton> scrollingCredits;

	private float xOffset = 0;

	private float yOffset = 0;

	private Runnable onDone;

	private final float stageWidth;
	private final float stageHeight;
	public Attributions(final float stageWidth, final float stageHeight) {
		super();
		this.stageWidth=stageWidth;
		this.stageHeight=stageHeight;
		final String tmp = Gdx.files.internal("data/credits.txt").readString("UTF-8");
		credits = tmp.split("\n");
		fg = new FontLoader();
		scrollingCredits = new Array<>(credits.length);
	}

	public Runnable getOnDone() {
		return onDone;
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

	private void populateCreditDisplay() {
		int ix = 0;
		BitmapFont font;
		maxLineHeight = 0;
		TextButton creditLine;
		TextButtonStyle style;
		font = fg.get(fontSize);
		for (ix = 0; ix < credits.length; ix++) {
			if (credits[ix].isEmpty()) {
				scrollingCredits.add(null);
				continue;
			}
			style = new TextButtonStyle();
			style.font = font;
			style.fontColor = new Color(fontColor);
			creditLine = new TextButton(credits[ix], style);
			creditLine.pack();
			scrollingCredits.add(creditLine);
			if (font.getLineHeight() > maxLineHeight) {
				maxLineHeight = font.getLineHeight();
			}

		}
		for (ix = 0; ix < credits.length; ix++) {
			creditLine = scrollingCredits.get(ix);
			if (creditLine == null) {
				continue;
			}
			creditLine.setX(xOffset + (stageWidth - creditLine.getWidth()) / 2);
			creditLine.setY(yOffset + (credits.length - ix - 1) * maxLineHeight);
			this.addActor(creditLine);
		}
		// store my new calculated size
		setWidth(stageWidth);
		setHeight(maxLineHeight * credits.length);
	}

	public void reset() {
		// populate text
		populateCreditDisplay();
		// clear out enay prior pending actions
		clearActions();
		// move myself below screen
		setY(-getHeight());
	}

	public void scroll(final float time) {
		reset();
		final SequenceAction seq = Actions.sequence();
		seq.addAction(Actions.moveTo(0, stageHeight + maxLineHeight * 2, time));
		seq.addAction(Actions.run(new Runnable() {
			@Override
			public void run() {
				if (onDone != null) {
					Gdx.app.postRunnable(onDone);
				}
			}
		}));
		addAction(seq);
	}

	public void setFontColor(final Color color) {
		this.fontColor = color;
	}

	public void setOnDone(final Runnable onDone) {
		this.onDone = onDone;
	}

	public void setxOffset(final float xOffset) {
		this.xOffset = xOffset;
	}

	/**
	 * @param yOffset the yOffset to set
	 */
	public void setyOffset(final float yOffset) {
		this.yOffset = yOffset;
	}
}
