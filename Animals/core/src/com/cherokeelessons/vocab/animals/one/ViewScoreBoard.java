package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.cherokeelessons.vocab.animals.one.CherokeeAnimals.FontStyle;
import com.cherokeelessons.vocab.animals.one.ScreenGameCore.GameColor;

/*
 * displays running score, level percent accuracy
 */
public class ViewScoreBoard extends Group {

	final Rectangle bbox = new Rectangle();

	private BitmapFont bitmapFont = null;

	private LabelStyle boxStyle = null;

	private int displayScore = 0;

	private float elapsedTime = 0;

	private int fontSize = 96;
	private int prevDisplayScore = -1;

	private int score = -1;

	private Label scoreBox = null;

	private float sideMargin = 15;
	private SoundManager sm;

	private float topMargin = 11 + 7;

	public Action updateScoreBoardAction = new Action() {

		private float tickGap = .2f;

		@Override
		public boolean act(float delta) {
			float deltaScore;
			float jumpBy;

			if (displayScore == score) {
				tickGap = .2f;
				return false;
			}
			elapsedTime += delta;
			if (elapsedTime < tickGap) {
				return false;
			}
			elapsedTime = 0;
			tickGap = .9f * tickGap;
			deltaScore = score - displayScore;
			if (deltaScore > 0) {
				// do partial additive operation
				jumpBy = (deltaScore / 3);
				if (jumpBy < 1) {
					jumpBy = 1;
				}
				soundGood();
			} else {
				// do partial subtractive operation
				jumpBy = (deltaScore / 2);
				if (jumpBy > -1) {
					jumpBy = -1;
				}
				soundBad();
			}
			displayScore += jumpBy;
			updateScoreDisplay();
			return false;
		}
	};

	// public Group viewGroup = null;

	protected ViewScoreBoard(Rectangle overscan, SoundManager sm) {
		super();
		// String glyphs="0123456789";
		// String glyphs2=" +%-";
		bbox.set(overscan);
		this.sm = sm;

		bitmapFont = CherokeeAnimals.getFont(CherokeeAnimals.FontStyle.Script,fontSize);// ;fg.genFixedNumbers(fontSize);
		boxStyle = new LabelStyle(bitmapFont, GameColor.GREEN);
		scoreBox = new Label("", boxStyle);
		setScore(0);
		fixUpPosition();

		// viewGroup = new Group();

		scoreBox.addAction(updateScoreBoardAction);
		addActor(scoreBox);

	}

	public void changeScoreBy(int points) {
		this.score += points;
		return;
	}

	private void fixUpPosition() {
		// recalculate dimensions
		scoreBox.pack();
		// set position accordingly
		scoreBox.setX(bbox.width - scoreBox.getWidth() - sideMargin);
		scoreBox.setY(bbox.height - scoreBox.getHeight() - topMargin);
	}

	public int getScore() {
		return score;
	}

	public void reset() {
		score = 0;
		displayScore = 0;
		prevDisplayScore = -1;
		updateScoreDisplay();
	}

	public void setScore(int score) {
		this.score = score;
	}

	private void soundBad() {
		sm.playEffect("whip_pop");
	}

	private void soundGood() {
		sm.playEffect("ding");
	}

	private synchronized void updateScoreDisplay() {
		if (displayScore != prevDisplayScore) {
			prevDisplayScore = displayScore;
			scoreBox.setText(String.format("% 9d", displayScore));
			fixUpPosition();
		}
	}

}
