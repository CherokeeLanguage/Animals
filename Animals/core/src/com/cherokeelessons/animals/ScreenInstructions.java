package com.cherokeelessons.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.common.FontLoader;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Gamepads;
import com.cherokeelessons.common.Utils;

public class ScreenInstructions extends GameScreen implements DpadInterface {
	
	private static final int fontSize=64;

	final private Table instructions = new Table();

	private ControllerAdapter exitScreen = new ControllerAdapter() {
		@Override
		public boolean buttonDown(Controller controller, int buttonCode) {
			game.gameEvent(GameEvent.Done);
			return true;
		}
	};
	
	@Override
	public boolean dpad(int keyCode) {
		switch (keyCode) {
		case Keys.DPAD_CENTER:
			game.gameEvent(GameEvent.Done);
			return true;
		}
		return false;
	}

	final Array<Sprite> wall = new Array<Sprite>();
	private TextureAtlas wall_atlas;

	public ScreenInstructions(CherokeeAnimals game) {
		super(game);
	}

	private void initScreen() {
		gameStage.clear();
		gameStage.addActor(instructions);
		wall_atlas = Utils.initBackdrop(wall);
		init();
		instructions.setWidth(screenSize.width);
		instructions.setHeight(screenSize.height);
	}

	private FontLoader fg;
			

	public void init() {
		fg = new FontLoader();
		
		String tmp = Gdx.files.internal("data/instructions.txt").readString("UTF-8");
		LabelStyle style = new LabelStyle(fg.get(fontSize), new Color(GameColor.INSTRUCTIONS_TEXT));
		
		instructions.row();
		Label textButton = new Label(tmp, style);
		textButton.setAlignment(Align.center);
		textButton.setWrap(true);
		instructions.add(textButton).center().pad(0).space(0).expand().fill();
		
		TextButtonStyle tstyle = new TextButtonStyle();
		tstyle.font=fg.get(fontSize);
		tstyle.fontColor=new Color(GameColor.INSTRUCTIONS_TEXT);
		TextButton btnExit = new TextButton("[BACK]", tstyle);
		
		instructions.row();
		instructions.add(btnExit).center().pad(0).space(0).bottom();
		
		btnExit.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				game.gameEvent(GameEvent.Done);
				return true;
			}
		});
	}

	

	@Override
	public void dispose() {
		super.dispose();
	}

	private void discardResources() {
		wall_atlas.dispose();
		wall_atlas = null;
		instructions.clear();
		gameStage.clear();
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
		Gamepads.addListener(exitScreen);
	}

}
