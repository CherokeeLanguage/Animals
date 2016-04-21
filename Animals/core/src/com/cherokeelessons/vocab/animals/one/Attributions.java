package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class Attributions extends Group {
	private Rectangle bbox;
	private String[] credits = {
			"Cherokee Language Animals",
			"",
			"- Game Creator -",
			"Michael Joyner / ᎹᎦᎵ ᏗᏁᏍᎨᏍᎩ",
			"",
			"- Audio -",
			"Startup Sequence Flute - ᏣᎵ ᎡᎵᏂ ᎠᏍᎦᏳᏏᏩᏍᎩ",
			"Audio Challenges - Michael Joyner",
			"Audio Effects - freesound.org",
			// "Audio Effects - http://sampleswap.org/", (not yet)
			"All audio effects are cc-zero or public domain.", "", "- Fonts -",
			"Johnny Mac Scrawl BRK", "Digohweli", "", "- Language Reference -",
			"Language Assistance - ᏣᎵ ᎡᎵᏂ ᎠᏍᎦᏳᏏᏩᏍᎩ",
			"Cherokee-English Dictionary (Durbin-Feeling)", "", "- Pictures -",
			"OpenClipart.Org", "Commons.Wikimedia.Org",
			"All pictures are cc-zero or public domain.", "",
			"http://www.CherokeeLessons.com/", "", "Game Version 5.00 (2016)" };

	private Color fontColor = new Color(Color.BLACK);
	private final int fontSize=72;
	private float maxLineHeight;

	private TextButton[] scrollingCredits;

	private float xOffset = 0;

	private float yOffset = 0;

	public Attributions(Rectangle _bbox) {
		super();
		this.bbox = _bbox;
		this.setX(bbox.x);
		this.setY(bbox.y);
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
		scrollingCredits = new TextButton[credits.length];
		populateCreditDisplay();
	}

	private void populateCreditDisplay() {
		int ix = 0;
		BitmapFont font;
		maxLineHeight = 0;
		TextButton creditLine;
		TextButtonStyle style;
		font = CherokeeAnimals.getFont(CherokeeAnimals.FontStyle.Script, fontSize);
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

	public void scroll(float time) {
		reset();
		addAction(Actions.moveTo(bbox.x, bbox.y + bbox.height + maxLineHeight
				* 2, time));
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
