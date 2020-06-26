package com.cherokeelessons.animals.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.cherokeelessons.common.FontLoader;
import com.cherokeelessons.common.GameColor;

/*
 * In game controls (pause, exit, mute)
 */
public class ViewInGameControls extends Group {

	private final String TABLET_INFO;
	private final String PAUSE_INFO;

	private BitmapFont bitmapFont = null;

	Vector2 bottomCenter = null;

	Vector2 bottomLeft = null;

	private final float bottomMargin = 5;

	Vector2 bottomRight = null;

	private Label btn_Options = null;
	private Label btn_Pause = null;

	private final int fontSize = 48;
	private LabelStyle labelStyle = null;

	private final float sideMargin = 20;

	private Runnable onPause;

	public ViewInGameControls(final Rectangle overscan, boolean tv) {
		super();
		if (tv) {
			TABLET_INFO = "Press [A] or [Select] to choose";
			PAUSE_INFO = "Press [Y] or [Back] to pause";
		} else {
			TABLET_INFO = "Tap picture(s) matching challenge.";
			PAUSE_INFO = "[PAUSE]";
		}

		bottomRight = new Vector2(overscan.width, 0);
		bottomLeft = new Vector2(0, 0);
		bottomCenter = new Vector2(overscan.width / 2, 0);

		final FontLoader fg = new FontLoader();
		bitmapFont = fg.get(fontSize);

		labelStyle = new LabelStyle();
		labelStyle.font = bitmapFont;
		labelStyle.fontColor = GameColor.MAIN_TEXT;

		btn_Options = new Label(TABLET_INFO, labelStyle);
		btn_Pause = new Label(PAUSE_INFO, labelStyle);

		fixupPositions();

		addActor(btn_Options);
		addActor(btn_Pause);
	}

	private void fixupPositions() {
		float y = 0;
		float x = 0;

		btn_Options.pack();
		btn_Pause.pack();

		/*
		 * layout: left, center, right
		 */
		x = bottomLeft.x + sideMargin;
		y = bottomRight.y + bottomMargin;
		btn_Pause.setX(x);
		btn_Pause.setY(y);
		btn_Pause.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x1, final float y1, final int pointer,
					final int button) {
				if (onPause != null) {
					Gdx.app.postRunnable(onPause);
				}
				return true;
			}
		});

		x = bottomRight.x - btn_Options.getWidth();
		y = bottomCenter.y + bottomMargin;
		btn_Options.setX(x);
		btn_Options.setY(y);
	}

	public void setOnPause(final Runnable runnable) {
		this.onPause = runnable;
	}
}
