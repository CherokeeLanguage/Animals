package com.cherokeelessons.vocab.animals.one.views;

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
	
	private static final String TABLET_INFO = "Tap picture matching challenge.";

	private BitmapFont bitmapFont = null;

	Vector2 bottomCenter = null;

	Vector2 bottomLeft = null;

	private float bottomMargin = 5;

	Vector2 bottomRight = null;

	private Label btn_Options = null;
	private Label btn_Pause = null;
	private Label btn_Exit = null;

	private int fontSize = 48;
	private LabelStyle labelStyle = null;

	private float sideMargin = 20;

	private Runnable onExit;

	private Runnable onPause;

	public ViewInGameControls(Rectangle overscan) {
		super();

		bottomRight = new Vector2(overscan.width, 0);
		bottomLeft = new Vector2(0, 0);
		bottomCenter = new Vector2(overscan.width / 2, 0);

		FontLoader fg = new FontLoader();
		bitmapFont = fg.get(fontSize);

		labelStyle = new LabelStyle();
		labelStyle.font = bitmapFont;
		labelStyle.fontColor = GameColor.GREEN;

		btn_Options = new Label(TABLET_INFO, labelStyle);
		btn_Pause = new Label("[PAUSE]", labelStyle);
		btn_Exit = new Label("[EXIT]", labelStyle);

		fixupPositions();

		addActor(btn_Options);
		addActor(btn_Pause);
		addActor(btn_Exit);
	}

	private void fixupPositions() {
		float y = 0;
		float x = 0;

		btn_Options.pack();
		btn_Pause.pack();
		btn_Exit.pack();

		/*
		 * layout: left, center, right
		 */
		x = bottomLeft.x + sideMargin;
		y = bottomRight.y + bottomMargin;
		btn_Pause.setX(x);
		btn_Pause.setY(y);
		btn_Pause.addListener(new InputListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (onPause!=null) {
					Gdx.app.postRunnable(onPause);
				}
				return true;
			}
		});

		x = bottomCenter.x - btn_Options.getWidth() / 2;
		y = bottomCenter.y + bottomMargin;
		btn_Options.setX(x);
		btn_Options.setY(y);
		
		x = bottomRight.x - btn_Exit.getWidth();
		y = bottomRight.y + bottomMargin;
		btn_Exit.setX(x);
		btn_Exit.setY(y);
		btn_Exit.addListener(new InputListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (onExit!=null) {
					Gdx.app.postRunnable(onExit);
				}
				return true;
			}
		});
	}

	public void setOnPause(Runnable runnable) {
		this.onPause=runnable;
	}

	public void setOnExit(Runnable runnable) {
		this.onExit=runnable;
	}
}
