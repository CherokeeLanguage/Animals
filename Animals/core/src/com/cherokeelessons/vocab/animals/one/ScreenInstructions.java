package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.common.FontLoader;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Gamepads;
import com.cherokeelessons.common.Utils;
import com.cherokeelessons.vocab.animals.one.enums.GameEvent;

public class ScreenInstructions extends GameScreen {

	final private Group instructions = new Group();

	private ControllerAdapter exitScreen = new ControllerAdapter() {
		@Override
		public boolean buttonDown(Controller controller, int buttonCode) {
			game.gameEvent(GameEvent.Done);
			return true;
		}
	};

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
	}

	private FontLoader fg;
	private String[] instructionText = {
			"-Instructions-",
			"",
			"After starting a new game and selecting a starting level you will",
			"be presented with a training screen. The training screen will show",
			"a new challenge and sound out the new challenge's name several times."
			,"",
			"After the training screen completes you will then be presented",
			"a game board along with an audio challenge. Select each of the matching",
			"pictures on the game board. If only one picture matches the challenge,",
			"the board will clear and you will move on to the next challenge.",
			"",
			"If more than one picture matches the challenge, each correct picture",
			"selected will turn into a green checkmark. If you choose an incorrect",
			"picture, it will be replaced by an 'X'.",
			"",
			"You must get 80% or better on each level before",
			"you are allowed to advance to the next level.",
			"",
			"[EXIT]" };
	private TextButton[] textLines;
	private Color fontColor = GameColor.DARKGREEN;

	public void init() {
		fg = new FontLoader();

		textLines = new TextButton[instructionText.length];

		populateInstructionDisplay();
		if (instructions.getHeight() > screenSize.height) {
			float fontScale = screenSize.height / instructions.getHeight();
			Gdx.app.log(this.getClass().getName(), "Need to shrink font to "+(int) ((float) fontSize * fontScale)+"!");
		}
		textLines[instructionText.length - 1].addListener(new ClickListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				game.gameEvent(GameEvent.Done);
				return true;
			}

		});

	}

	private final int fontSize=44;

	private void populateInstructionDisplay() {
		int ix = 0;
		BitmapFont font;
		maxLineHeight = 0;
		TextButton creditLine;
		TextButtonStyle style;
		font = fg.get(fontSize);
		for (ix = 0; ix < instructionText.length; ix++) {
			if (instructionText[ix].length() < 1) {
				textLines[ix] = null;
				continue;
			}
			style = new TextButtonStyle();
			style.font = font;
			style.fontColor = new Color(fontColor);
			creditLine = new TextButton(instructionText[ix], style);
			creditLine.pack();
			textLines[ix] = creditLine;
			if (font.getLineHeight() > maxLineHeight)
				maxLineHeight = font.getLineHeight();

		}
		for (ix = 0; ix < instructionText.length; ix++) {
			creditLine = textLines[ix];
			if (creditLine == null) {
				continue;
			}
			creditLine.setX((screenSize.width - creditLine.getWidth()) / 2);
			creditLine.setY((instructionText.length - ix - 1) * maxLineHeight);
			instructions.addActor(creditLine);
		}
		// store my dimensions
		instructions.setWidth(screenSize.width);
		instructions.setHeight(maxLineHeight * instructionText.length);
		instructions.setOrigin(screenSize.width / 2,
				instructions.getHeight() / 2);
	}

	private float maxLineHeight;

	@Override
	public void dispose() {
		super.dispose();
	}

	private void discardResources() {
		textLines[textLines.length - 1].clearListeners();
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
