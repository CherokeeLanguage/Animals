package com.cherokeelessons.animals.views;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.cherokeelessons.common.FontLoader;
import com.cherokeelessons.common.GameColor;

/*
 * Displays challenge word/phrase
 */

public class ViewChallengeBoard extends Group {

	private LabelStyle displayStyle = null;
	private Label displayText = null;
	private BitmapFont font;

	private final FontLoader fontGen;
	private int fontSize = 96;

	final private Rectangle screenSize = new Rectangle();
	private final int sideMargin = 15;

	private final int topMargin = 18 + 7;

	// private Group viewGroup=null;
	public ViewChallengeBoard(final Rectangle screenSize) {
		super();

		this.screenSize.set(screenSize);
		fontGen = new FontLoader();
		font = fontGen.get(fontSize);
		displayStyle = new LabelStyle(font, GameColor.MAIN_TEXT);
		displayText = new Label("", displayStyle);
		clear();
		addActor(displayText);
	}

	/**
	 * @return the fontSize
	 */
	public int getFontSize() {
		return fontSize;
	}

	// public Group getViewGroup() {
	// return viewGroup;
	// }

	public void setDisplayText(final String text) {
		displayText.setText(text);
		displayText.pack();
		displayText.setX(sideMargin);
		displayText.setY(screenSize.height - topMargin - displayText.getHeight());
	}

	/**
	 * @param fontSize the fontSize to set
	 */
	public void setFontSize(final int fontSize) {
		final FontLoader fg = new FontLoader();
		this.fontSize = fontSize;
		font = fg.get(fontSize);
		displayStyle.font = font;
		displayText.setStyle(displayStyle);
	}
}
