package com.cherokeelessons.animals;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.common.Attributions;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Gamepads;
import com.cherokeelessons.common.Utils;

public class ScreenCredits extends GameScreen implements DpadInterface {

	public float scrollTime = 30f;

	private Attributions creditScroller;

	private Attributions shadow;

	private final ControllerAdapter skipCredits = new ControllerAdapter() {
		@Override
		public boolean buttonDown(final Controller controller, final int buttonCode) {
			game.gameEvent(GameEvent.Done);
			return true;
		}
	};
	final Array<Sprite> wall = new Array<>();
	private TextureAtlas wall_atlas;

	public ScreenCredits(final CherokeeAnimals game) {
		super(game);
	}

	private void discardResources() {
		wall_atlas.dispose();
		wall_atlas = null;
		creditScroller.clear();
		creditScroller = null;
		shadow.clear();
		shadow = null;
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	public void doScroll(final float time) {
		creditScroller.init();
		creditScroller.setOnDone(new Runnable() {
			@Override
			public void run() {
				game.gameEvent(GameEvent.Done);
			}
		});
		creditScroller.scroll(time);
		shadow.init();
		shadow.scroll(time);
	}

	@Override
	public boolean dpad(final int keyCode) {
		if (keyCode != Input.Keys.DPAD_CENTER) {
			return false;
		}
		game.gameEvent(GameEvent.Done);
		return true;
	}

	@Override
	public void hide() {
		super.hide();
		Gamepads.clearListeners();
		discardResources();
	}

	private void initScreen() {
		gameStage.clear();

		shadow = new Attributions(safeZoneSize);
		shadow.setFontColor(Color.BLACK);
		shadow.getColor().a = .7f;
		shadow.setxOffset(2);
		shadow.setyOffset(-2);

		creditScroller = new Attributions(safeZoneSize);
		creditScroller.setFontColor(GameColor.MAIN_TEXT);

		wall_atlas = Utils.initBackdrop(wall);

		gameStage.addActor(shadow);
		gameStage.addActor(creditScroller);
	}

	@Override
	public void render(final float delta) {
		super.render(delta);
//		batch.begin();
//		for (Sprite s : wall) {
//			s.draw(batch);
//		}
//		batch.end();
		gameStage.draw();
	}

	@Override
	public void show() {
		super.show();
		initScreen();
		Gamepads.addListener(skipCredits);
		doScroll(scrollTime);
	}

}
