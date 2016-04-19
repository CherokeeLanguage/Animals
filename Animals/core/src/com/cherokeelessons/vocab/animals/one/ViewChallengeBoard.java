package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.cherokeelessons.vocab.animals.one.CherokeeAnimals.FontStyle;
import com.cherokeelessons.vocab.animals.one.ScreenGameCore.GameColor;

/*
 * Displays challenge word/phrase
 */

public class ViewChallengeBoard extends Group {

	private LabelStyle displayStyle = null;
	private Label displayText = null;
	private BitmapFont font;

	private int fontSize = 96;

	final private Rectangle screenSize = new Rectangle();
	private int sideMargin = 15;

	private int topMargin = 18 + 7;

	// private Group viewGroup=null;
	protected ViewChallengeBoard(Rectangle screenSize) {
		super();
		this.screenSize.set(screenSize);
		font = CherokeeAnimals.getFont(CherokeeAnimals.FontStyle.FreeSans,fontSize);
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
		this.fontSize = fontSize;
		font = CherokeeAnimals.getFont(CherokeeAnimals.FontStyle.FreeSans,fontSize);
		displayStyle.font = font;
		displayText.setStyle(displayStyle);
	}
}
