package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.common.FontGenerator;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Gamepads;
import com.cherokeelessons.common.OS;
import com.cherokeelessons.common.Utils;
import com.cherokeelessons.vocab.animals.one.enums.GameEvent;

public class ScreenMainMenu extends GameScreen {

	public static final String INDICATOR = "images/indicators/abelo_6.png";
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

	private static final String GAME_TITLE = "Esperanta Animaloj!";
	private static final String NEW_GAME = "Nova Ludo / New Game";
	private static final String LEADERS = "Altaj Poentaroj  / High Scores";
	private static final String INSTRUCTIONS = "Instruoj / Instructions";
	private static final String OPTIONS = "Agordoj / Options";
	private static final String CREDITS = "Pri / About";
	private static final String QUIT = "Halti / Quit";

	private Array<MenuLabel> btns = new Array<MenuLabel>();

	private Texture indicator;

	private Image left_indicator = new Image();
	private Runnable newGame = new Runnable() {
		@Override
		public void run() {
			game.sm.playEffect("menu-click");
			game.gameEvent(GameEvent.NewGame);
		}
	};
	public int optionsButton;
	private Runnable performQuit = new Runnable() {
		@Override
		public void run() {
			game.gameEvent(GameEvent.QuitGame);
			game.sm.playEffect("menu-click");
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
			game.gameEvent(GameEvent.ShowOptions);
		}
	};

	final Array<Sprite> wall = new Array<Sprite>();

	private TextureAtlas wall_atlas;

	final private ControllerMainMenu_Watch watcher = new ControllerMainMenu_Watch(
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
			game.gameEvent(GameEvent.ShowCredits);
		}
	};
	private Runnable showLeaderBoard=new Runnable() {
		@Override
		public void run() {
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
		FontGenerator fg = new FontGenerator();

		BitmapFont bmFont;
		bmFont = fg.gen(fontSize);

		BitmapFont hsFont;
		hsFont = fg.genFixedNumbers(fontSize / 2);

		Color textColor = GameColor.GREEN;

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
			final int button = ix;
			btns.get(ix).setTouchable(Touchable.enabled);
			btns.get(ix).addListener(new ClickListener(){
				private int btn = button;				
				@Override
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					highlight_button(btn, true);
					doMenuItem();
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

	public void doMenuItem() {
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

	public void nextMenuItem() {
		selected_btn++;
		if (selected_btn >= btns.size) {
			selected_btn = 0;
		}
		highlight_button();
	}

	public void prevMenuItem() {
		selected_btn--;
		if (selected_btn < 0) {
			selected_btn = btns.size - 1;
		}
		highlight_button();
	}

	private float nagtick=0f;
	@Override
	public void render(float delta) {
		super.render(delta);
		gameStage.act(delta);		
		batch.begin();
		for (Sprite s : wall) {
			s.draw(batch);
		}
		batch.end();
		gameStage.draw();
		if (game.isNag()) {
			nagtick+=delta;
			if (nagtick>45f) {
				nagtick=0f;
				showNextNag();				
			}
		}
	}

	private Array<String> nags=new Array<String>();
	private Label naglbl=null;
	private void showNextNag() {
		if (naglbl == null) {
			BitmapFont f = new FontGenerator().gen(48);
			LabelStyle ls = new LabelStyle();
			ls.font = f;
			ls.fontColor = GameColor.FIREBRICK;
			naglbl = new Label("", ls);
			naglbl.setTouchable(Touchable.disabled);
			gameStage.addActor(naglbl);
		}
		if (nags.size==0) {
			String tmp[] = Gdx.files.internal("data/desktop-nags.txt").readString("UTF-8").split("\n");
			for (String msg: tmp) {
				if (msg!=null && msg.length()>0) {
					nags.add(msg);
				}
				nags.shuffle();
			}			
		}
		final String nag = nags.get(0);
		System.out.println(nag);
		final Vector2 newpos = new Vector2();
		nags.removeIndex(0);
		final SequenceAction seq = Actions.sequence();
		seq.addAction(Actions.moveTo(0, -1024, 5f, Interpolation.bounceOut));
		seq.addAction(Actions.run(new Runnable() {			
			@Override
			public void run() {
				naglbl.setText(nag);
				naglbl.pack();
				newpos.y=-screenSize.y;
				newpos.x=(screenSize.width-naglbl.getWidth())/2;
				seq.addAction(Actions.moveTo(newpos.x, newpos.y, 5f, Interpolation.bounceIn));
			}
		}));
		naglbl.addAction(seq);
	}

	@Override
	public void show() {
		super.show();
		game.sm.playEffect("ding");		
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

		if (OS.Platform.Android.equals(OS.platform)) {
			game.setNag(false);
		}
		if (OS.Platform.Ouya.equals(OS.platform)) {
			game.setNag(false);
		}
	}
}
