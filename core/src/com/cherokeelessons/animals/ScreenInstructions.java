package com.cherokeelessons.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.common.BackdropData;
import com.cherokeelessons.common.FontLoader;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Gamepads;

public class ScreenInstructions extends GameScreen implements DpadInterface {
	
	@Override
	protected boolean useBackdrop() {
		return true;
	}

	private static final int fontSize = 64;

	final private Table instructions = new Table();

	private final ControllerAdapter exitScreen = new ControllerAdapter() {
		@Override
		public boolean buttonDown(final Controller controller, final int buttonCode) {
			game.gameEvent(GameEvent.EXIT_SCREEN);
			return true;
		}
	};

	private BackdropData wall_atlas;
	private FontLoader fg;

	public ScreenInstructions(final CherokeeAnimals game) {
		super(game);
	}

	private void discardResources() {
		if (wall_atlas!=null) {
			wall_atlas.dispose();
		}
		wall_atlas = null;
		instructions.clear();
		gameStage.clear();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public boolean dpad(final int keyCode) {
		switch (keyCode) {
		case Keys.DPAD_CENTER:
			game.gameEvent(GameEvent.EXIT_SCREEN);
			return true;
		default:
			break;
		}
		return false;
	}

	@Override
	public void hide() {
		super.hide();
		Gamepads.clearListeners();
		discardResources();
	}

	public void init() {
		fg = new FontLoader();

		FileHandle instructionsFileHandle = Gdx.files.internal(game.isTelevision()?"data/instructions-tv.txt":"data/instructions.txt");
		final String tmp = instructionsFileHandle.readString("UTF-8");
		final LabelStyle style = new LabelStyle(fg.get(fontSize), new Color(GameColor.INSTRUCTIONS_TEXT));

		instructions.row();
		final Label textButton = new Label(tmp, style);
		textButton.setAlignment(Align.center);
		textButton.setWrap(true);
		instructions.add(textButton).center().pad(0).space(0).expand().fill();

		final TextButtonStyle tstyle = new TextButtonStyle();
		tstyle.font = fg.get(fontSize);
		tstyle.fontColor = new Color(GameColor.INSTRUCTIONS_TEXT);
		final TextButton btnExit = new TextButton(game.isTelevision()?"Press [A] or [Select] to exit":"[BACK]", tstyle);

		instructions.row();
		instructions.add(btnExit).center().pad(0).space(0).bottom();

		btnExit.addListener(new ClickListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
					final int button) {
				game.gameEvent(GameEvent.EXIT_SCREEN);
				return true;
			}
		});
	}

	private void initScreen() {
		gameStage.clear();
		gameStage.addActor(instructions);
		init();
		instructions.setWidth(fullZoneBox.width);
		instructions.setHeight(fullZoneBox.height);
	}

	@Override
	public void render(final float delta) {
		super.render(delta);
	}

	@Override
	public void show() {
		initScreen();
		Gamepads.addListener(exitScreen);
		super.show();
	}

}
