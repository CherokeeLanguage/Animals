package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.cherokeelessons.vocab.animals.one.ScreenGameCore.GameColor;

/*
 * In game controls (pause, exit, mute)
 */
public class ViewInGameControls extends Group {
	private BitmapFont bitmapFont = null;

	Vector2 bottomCenter = null;

	Vector2 bottomLeft = null;

	private float bottomMargin = 5;

	Vector2 bottomRight = null;

	private Label btn_Options = null;

	private int fontSize = 48;
	// private Label lbl_pause = null;
	// private Label btn_Quit = null;
	private LabelStyle labelStyle = null;

	private float sideMargin = 20;

	protected ViewInGameControls(Rectangle overscan) {
		super();

		bottomRight = new Vector2(overscan.width, 0);
		bottomLeft = new Vector2(0, 0);
		bottomCenter = new Vector2(overscan.width / 2, 0);

		bitmapFont = CherokeeAnimals.getFont(CherokeeAnimals.FontStyle.Script, fontSize);

		labelStyle = new LabelStyle();
		labelStyle.font = bitmapFont;
		labelStyle.fontColor = GameColor.GREEN;

		btn_Options = new Label(
				"DPAD or left stick to move, [O] - Select answer, [Y] - Pause, [A] - Main Menu",
				labelStyle);
		btn_Options.setTouchable(Touchable.enabled);

		fixupPositions();

		addActor(btn_Options);
	}

	private void fixupPositions() {
		float y = 0;
		float x = 0;

		btn_Options.pack();

		/*
		 * layout on bottom left to right
		 */

		x = bottomLeft.x + sideMargin;
		y = bottomRight.y + bottomMargin;

		x = bottomCenter.x - btn_Options.getWidth() / 2;
		y = bottomCenter.y + bottomMargin;
		btn_Options.setX(x);
		btn_Options.setY(y);

	}
}
