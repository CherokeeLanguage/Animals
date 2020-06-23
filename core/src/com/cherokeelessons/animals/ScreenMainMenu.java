package com.cherokeelessons.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.common.FontLoader;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Gamepads;
import com.cherokeelessons.common.Utils;

public class ScreenMainMenu extends GameScreen implements DpadInterface {
	
	@Override
	public boolean dpad(int keyCode) {
		switch (keyCode) {
		case Keys.DPAD_CENTER:
			hud_select();
			return true;
		case Keys.DPAD_DOWN:
			hud_moveSouth();
			return true;
		case Keys.DPAD_UP:
			hud_moveNorth();
			return true;
		}
		return false;
	}
	
	public static final String INDICATOR = "images/indicators/da-gi-si_2.png";
	public static final float INDI_SCALE=.45f; 
	private static class MenuLabel extends Label {

		private Runnable menu_action = null;

		public MenuLabel(CharSequence text, LabelStyle style) {
			super(text, style);
		}

		public void doRun() {
			if (menu_action != null) {
				Gdx.app.postRunnable(menu_action);
			}
		}

		public void setRun(Runnable listener) {
			menu_action = listener;
		}
	}

	private static final String GAME_TITLE = "ᎠᏂᏣᎳᎩ ᎡᎿᎢ!";
	private static final String NEW_GAME = "New Game - ᎢᏤ ᏗᏁᎶᏗᎢ";
	private static final String LEADERS = "High Scores - ᏬᏍᏓ ᏗᏎᏍᏗ";
	private static final String INSTRUCTIONS = "Instructions - ᏗᏕᏲᏗ";
	private static final String OPTIONS = "Options - ᎠᏑᏰᏍᏗᎢ";
	private static final String CREDITS = "About - ᎢᎸᏢ";
	private static final String QUIT = "Quit - ᎠᏑᎶᎪᏍᏗ";

	private Array<MenuLabel> btns = new Array<MenuLabel>();

	private Texture indicator;

	private Image left_indicator = new Image();
	private Runnable newGame = new Runnable() {
		@Override
		public void run() {
			game.music.pause();
			game.sm.playEffect("menu-click");
			game.gameEvent(GameEvent.NewGame);
		}
	};
	public int optionsButton;
	private Runnable performQuit = new Runnable() {
		@Override
		public void run() {
			game.sm.playEffect("menu-click");
			game.gameEvent(GameEvent.QuitGame);
			Gdx.app.exit();			
		}
	};

	final public int quitButton;

	private Image right_indicator = new Image();

	private int selected_btn = 0;

	private Runnable showOptions = new Runnable() {
		@Override
		public void run() {
			game.sm.playEffect("menu-click");
			game.gameEvent(GameEvent.Menu);
		}
	};

	private final Array<Sprite> wall = new Array<Sprite>();
	private TextureAtlas wall_atlas;

	final private CtlrMainMenu_Watch watcher = new CtlrMainMenu_Watch(
			this);
	private Runnable showInstructions=new Runnable() {
		@Override
		public void run() {
			game.sm.playEffect("menu-click");
			game.gameEvent(GameEvent.ShowInstructions);			
		}
	};
	private Runnable showCredits=new Runnable() {
		@Override
		public void run() {
			game.sm.playEffect("menu-click");
			game.gameEvent(GameEvent.ShowCredits);
		}
	};
	private Runnable showLeaderBoard=new Runnable() {
		@Override
		public void run() {
			game.sm.playEffect("menu-click");
			game.gameEvent(GameEvent.ShowLeaderBoard);
		}
	};
	

	public ScreenMainMenu(CherokeeAnimals game) {
		super(game);
		
		float currentY;
		float linesOfText = 6;
		float skipAmount = screenSize.height / (linesOfText);
		int fontSize = 96;
		float graphicsHeight = 0;
		float emptyHeight = 0;
		FontLoader fg = new FontLoader();

		BitmapFont bmFont;
		bmFont = fg.get(fontSize);

		BitmapFont hsFont;
		hsFont = fg.getFixed(fontSize / 2);

		Color textColor = GameColor.MAIN_TEXT;

		MenuLabel titleText = null;
		LabelStyle titleStyle = null;
		LabelStyle hsStyle = null;

		MenuLabel btn_NewGame = null;
		MenuLabel btn_Instructions = null;
		MenuLabel btn_Leaders = null;
		MenuLabel btn_Options = null;
		MenuLabel btn_Credits = null;
		MenuLabel btn_Quit = null;
		LabelStyle buttonStyle = null;

		titleStyle = new LabelStyle(bmFont, textColor);
		hsStyle = new LabelStyle(hsFont, new Color(textColor));
		hsStyle.fontColor.a = 0.5f;

		buttonStyle = new LabelStyle();
		buttonStyle.font = bmFont;
		buttonStyle.fontColor = textColor;

		titleText = new MenuLabel(GAME_TITLE, titleStyle);

		btn_NewGame = new MenuLabel(NEW_GAME, buttonStyle);
		btn_Instructions = new MenuLabel(INSTRUCTIONS, buttonStyle);
		btn_Leaders = new MenuLabel(LEADERS, buttonStyle);
		btn_Options = new MenuLabel(OPTIONS, buttonStyle);
		btn_Credits = new MenuLabel(CREDITS, buttonStyle);
		btn_Quit = new MenuLabel(QUIT, buttonStyle);

		/*
		 * calculate needed empty gap between menu items for even up to down
		 * spacing
		 */

		graphicsHeight = titleText.getHeight() + btn_NewGame.getHeight()
				+ btn_Options.getHeight() + btn_Quit.getHeight() + btn_Instructions.getHeight()
				+ btn_Credits.getHeight() + btn_Leaders.getHeight();
		emptyHeight = screenSize.height - graphicsHeight;
		skipAmount = emptyHeight / (linesOfText + 1);

		/*
		 * center each line
		 */
		titleText
				.setX(((screenSize.width - titleText.getWidth()) / 2));
		btn_NewGame.setX((screenSize.width - btn_NewGame.getWidth())
				/ 2);
		btn_Leaders.setX((screenSize.width-btn_Leaders.getWidth())/2);
		btn_Instructions.setX((screenSize.width - btn_Instructions.getWidth())/2);
		btn_Options.setX((screenSize.width - btn_Options.getWidth())
				/ 2);
		btn_Credits.setX((screenSize.width - btn_Credits.getWidth())
				/ 2);
		btn_Quit.setX((screenSize.width - btn_Quit.getWidth()) / 2);

		/*
		 * position each one equal distant based on screen height
		 */
		// start at top of screen
		currentY = screenSize.height;
		// subtract empty gap + line height before placement
		currentY -= (titleText.getHeight() + skipAmount);
		titleText.setY(currentY);

		currentY -= (btn_NewGame.getHeight() + skipAmount);
		btn_NewGame.setY(currentY);
		
		currentY -= (btn_Leaders.getHeight() + skipAmount);
		btn_Leaders.setY(currentY);
		
		currentY -= (btn_Instructions.getHeight() + skipAmount);
		btn_Instructions.setY(currentY);

		currentY -= (btn_Options.getHeight() + skipAmount);
		btn_Options.setY(currentY);
		
		currentY -= (btn_Options.getHeight() + skipAmount);
		btn_Credits.setY(currentY);

		currentY -= (btn_Quit.getHeight() + skipAmount);
		btn_Quit.setY(currentY);

		/*
		 * add buttons to buttons menu array
		 */
		btns.add(btn_NewGame);
		btns.add(btn_Leaders);
		btns.add(btn_Instructions);
		optionsButton = btns.size;
		btns.add(btn_Options);
		btns.add(btn_Credits);
		quitButton = btns.size;
		btns.add(btn_Quit);
		
		/*
		 * connect touch handlers
		 */
		for (int ix=0; ix<btns.size; ix++) {
			MenuLabel menuItem = btns.get(ix);
			Gdx.app.log(this.getClass().getName(), "touch handlers: "+menuItem.getText());
			final int button = ix;
			menuItem.pack();
			menuItem.setTouchable(Touchable.enabled);
			menuItem.addListener(new InputListener(){
				private int btn = button;				
				@Override
				public boolean touchDown(InputEvent event, float ix, float emptyHeight,
						int pointer, int button) {
					highlight_button(btn, true);
					hud_select();
					return true;
				}
			});
		}

		/*
		 * register click handlers
		 */
		btn_NewGame.setRun(newGame);
		btn_Leaders.setRun(showLeaderBoard);
		btn_Quit.setRun(performQuit);
		btn_Options.setRun(showOptions);
		btn_Credits.setRun(showCredits);
		btn_Instructions.setRun(showInstructions);

		/*
		 * add to "stage" for display
		 */
		gameStage.addActor(titleText);
		gameStage.addActor(btn_NewGame);
		gameStage.addActor(btn_Leaders);
		gameStage.addActor(btn_Instructions);
		gameStage.addActor(btn_Options);
		gameStage.addActor(btn_Credits);
		gameStage.addActor(btn_Quit);
		
		gameStage.addActor(left_indicator);
		gameStage.addActor(right_indicator);

		wall_atlas=Utils.initBackdrop(wall);
	}
	
	@Override
	public void dispose() {
		wall_atlas.dispose();
		super.dispose();
	}

	public void hud_select() {
		btns.get(selected_btn).doRun();
	}

	public int getSelected_btn() {
		return selected_btn;
	}

	@Override
	public void hide() {
		for (Controller controller : Gamepads.getControllers()) {
			watcher.disconnected(controller);
		}
		Gamepads.clearListeners();
		indicator.dispose();
		indicator = null;
		super.hide();
	}

	private void highlight_button(int button, boolean quiet) {
		selected_btn=button;
		highlight_button(quiet);
	}
	
	private void highlight_button() {
		highlight_button(false);
	}
	
	private void highlight_button(boolean quiet) {
		if (!quiet) {
			game.sm.playEffect("box_moved");
		}
		MenuLabel label = btns.get(selected_btn);
		float left = label.getX();
		float bottom = label.getY();
		float right = label.getX() + label.getWidth();
		left_indicator.setPosition(left - left_indicator.getWidth() + 20 ,
				bottom);
		right_indicator.setPosition(right - 20, bottom);
	}

	public void hud_moveSouth() {
		selected_btn++;
		if (selected_btn >= btns.size) {
			selected_btn = 0;
		}
		highlight_button();
	}

	public void hud_moveNorth() {
		selected_btn--;
		if (selected_btn < 0) {
			selected_btn = btns.size - 1;
		}
		highlight_button();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		gameStage.act(delta);		
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
		float masterVolume = ((float)game.prefs.getMasterVolume())/100f;
		float musicVolume = ((float)game.prefs.getMusicVolume())/100f;
		game.music.play(masterVolume*musicVolume);
		Gamepads.addListener(watcher);
		for (Controller c : Gamepads.getControllers()) {
			watcher.connected(c);
		}

		TextureRegionDrawable temp;
		indicator = new Texture(INDICATOR);
		indicator.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		temp = new TextureRegionDrawable(new TextureRegion(indicator));

		left_indicator.setDrawable(temp);
		left_indicator.pack();
		
		right_indicator.setDrawable(temp);
		right_indicator.pack();
		
		left_indicator.setOrigin(0, 0);
		left_indicator.setOrigin(left_indicator.getWidth()/2, 0);
		left_indicator.setScaleX(INDI_SCALE);
		left_indicator.setScaleY(INDI_SCALE);
		
		right_indicator.setOrigin(right_indicator.getWidth()/2, 0);
		right_indicator.setScaleX(-INDI_SCALE);
		right_indicator.setScaleY(INDI_SCALE);

		highlight_button();
	}

	public void maybeQuit() {
		if (selected_btn==quitButton) {
			Gdx.app.postRunnable(performQuit);
			return;
		}
		selected_btn=quitButton;
		highlight_button();
	}
}
