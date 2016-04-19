package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.cherokeelessons.vocab.animals.one.CherokeeAnimals.FontStyle;

public class Attributions extends Group {
	private Rectangle bbox;
	private String[] credits = {
			"Cherokee Language Animals OUYA",
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
			"http://www.CherokeeLessons.com/", "", "Game Version 4.01 (2013)" };

	private Color fontColor = Color.BLACK;
	private int fontSize;
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

	private void calculateFontSize() {
		// starting size
		TextButton testLabel = null;
		TextButtonStyle testStyle;
		int size;
		int ix;
		BitmapFont font;
		float scale = 0;
		float maxWidth = 0;

		size = 72;
		font =  CherokeeAnimals.getFont(CherokeeAnimals.FontStyle.FreeSans, 72);
		testStyle = new TextButtonStyle();
		testStyle.font = font;
		testStyle.fontColor = new Color(fontColor);
		testLabel = new TextButton("", testStyle);
		font = CherokeeAnimals.getFont(CherokeeAnimals.FontStyle.FreeSans, size);
		for (ix = 0; ix < credits.length; ix++) {
			if (credits[ix].length() < 1)
				continue;
			testLabel.setText(credits[ix]);
			testLabel.setStyle(testStyle);
			testLabel.pack();
			if (maxWidth < testLabel.getWidth()) {
				maxWidth = testLabel.getWidth();
			}

		}
		scale = 0.95f * bbox.width / maxWidth;
		size = (int) (scale * (float) size);
		fontSize = size;
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

		calculateFontSize();
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
