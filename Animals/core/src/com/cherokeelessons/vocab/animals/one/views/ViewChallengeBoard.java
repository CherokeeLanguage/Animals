package com.cherokeelessons.vocab.animals.one.views;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.cherokeelessons.common.FontGenerator;
import com.cherokeelessons.common.GameColor;

/*
 * Displays challenge word/phrase
 */

public class ViewChallengeBoard extends Group {

	private LabelStyle displayStyle = null;
	private Label displayText = null;
	private BitmapFont font;

	private FontGenerator fontGen;
	private int fontSize = 96;

	final private Rectangle screenSize = new Rectangle();
	private int sideMargin = 15;

	private int topMargin = 18 + 7;

	// private Group viewGroup=null;
	public ViewChallengeBoard(Rectangle screenSize) {
		super();

		this.screenSize.set(screenSize);
		fontGen = new FontGenerator();
		font = fontGen.gen(fontSize);
		displayStyle = new LabelStyle(font, GameColor.GREEN);
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

	public void setDisplayText(String text) {
		displayText.setText(text);
		displayText.pack();
		displayText.setX(sideMargin);
		displayText.setY(screenSize.height - topMargin
				- displayText.getHeight());
	}

	/**
	 * @param fontSize
	 *            the fontSize to set
	 */
	public void setFontSize(int fontSize) {
		FontGenerator fg = new FontGenerator();
		this.fontSize = fontSize;
		font = fg.gen(fontSize);
		displayStyle.font = font;
		displayText.setStyle(displayStyle);
	}
}
