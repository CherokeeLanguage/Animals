package com.cherokeelessons.animals;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.graphics.Color;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.common.Attributions;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Gamepads;

public class ScreenCredits extends GameScreen {
	
	@Override
	protected boolean useBackdrop() {
		return true;
	}

	public float scrollTime = 30f;

	private Attributions creditScroller;

	private Attributions shadow;

	private final ControllerAdapter skipCredits = new ControllerAdapter() {
		@Override
		public boolean buttonDown(final Controller controller, final int buttonCode) {
			game.gameEvent(GameEvent.EXIT_SCREEN);
			return true;
		}
	};

	public ScreenCredits(final CherokeeAnimals game) {
		super(game);
		initScreen();
	}

	private void discardResources() {
		creditScroller.clear();
		creditScroller = null;
		shadow.clear();
		shadow = null;
	}

	@Override
	public void dispose() {
		discardResources();
		super.dispose();
	}

	public void doScroll(final float time) {
		creditScroller.setOnDone(new Runnable() {
			@Override
			public void run() {
				game.gameEvent(GameEvent.EXIT_SCREEN);
			}
		});
		creditScroller.scroll(time);
		shadow.scroll(time);
	}

	@Override
	public boolean dpad(final int keyCode) {
		if (keyCode != Input.Keys.DPAD_CENTER) {
			return false;
		}
		game.gameEvent(GameEvent.EXIT_SCREEN);
		return true;
	}

	@Override
	public void hide() {
		super.hide();
		Gamepads.clearListeners();
	}

	private void initScreen() {
		shadow = new Attributions(fullZoneBox.width, fullZoneBox.height);
		shadow.setFontColor(Color.BLACK);
		shadow.getColor().a = .65f;
		shadow.setxOffset(4);
		shadow.setyOffset(-4);

		creditScroller = new Attributions(fullZoneBox.width, fullZoneBox.height);
		creditScroller.setFontColor(GameColor.MAIN_TEXT);

		gameStage.addActor(shadow);
		gameStage.addActor(creditScroller);
	}

	@Override
	public void render(final float delta) {
		super.render(delta);
	}

	@Override
	public void show() {
		Gamepads.addListener(skipCredits);
		doScroll(scrollTime);
		super.show();
	}

}
