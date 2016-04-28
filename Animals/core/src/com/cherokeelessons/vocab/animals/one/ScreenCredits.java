package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.common.Attributions;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Gamepads;
import com.cherokeelessons.common.Utils;
import com.cherokeelessons.vocab.animals.one.enums.GameEvent;

public class ScreenCredits extends GameScreen {

	public float scrollTime=30f;
	
	private Attributions creditScroller;

	private Attributions shadow;

	private ControllerAdapter skipCredits = new ControllerAdapter() {
		@Override
		public boolean buttonDown(Controller controller, int buttonCode) {
			game.gameEvent(GameEvent.Done);
			return true;
		}
	};
	final Array<Sprite> wall = new Array<Sprite>();
	private TextureAtlas wall_atlas;

	public ScreenCredits(CherokeeAnimals game) {
		super(game);		
	}

	private void initScreen() {
		gameStage.clear();
		
		shadow = new Attributions(screenSize);
		shadow.setFontColor(Color.BLACK);
		shadow.getColor().a = .7f;
		shadow.setxOffset(2);
		shadow.setyOffset(-2);

		creditScroller = new Attributions(screenSize);
		creditScroller.setFontColor(GameColor.GREEN);

		wall_atlas=Utils.initBackdrop(wall);

		gameStage.addActor(shadow);
		gameStage.addActor(creditScroller);
	}

	@Override
	public void dispose() {		
		super.dispose();
	}

	private void discardResources() {
		wall_atlas.dispose();
		wall_atlas = null;
		creditScroller.clear();
		creditScroller = null;
		shadow.clear();
		shadow = null;
	}

	public void doScroll(float time) {
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
	public void hide() {
		super.hide();
		Gamepads.clearListeners();
		discardResources();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		batch.begin();
		for (Sprite s : wall) {
			s.draw(batch);
		}
		batch.end();
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
