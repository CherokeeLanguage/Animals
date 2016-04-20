package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.vocab.animals.one.GameEvent.EventList;

public class ScreenMainMenu extends ScreenGameCore {

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

	private static final String CHEROKEE_ANIMALS = "Cherokee Animals OUYA";
	private static final String HIGH_SCORE_000000000 = "HIGH SCORE: 000000000";
	private static final String NEW_GAME = "New Game";
	private static final String INSTRUCTIONS = "Instructions";
	private static final String OPTIONS = "Options";

	private static final String QUIT = "Quit";

	private Array<MenuLabel> btns = new Array<MenuLabel>();

	private MenuLabel highScore = null;

	private Texture indicator;

	private Sprite left_indicator = new Sprite();
	private Runnable newGame = new Runnable() {
		@Override
		public void run() {
			game.getSoundManager().playEffect("menu-click");
			game.event(EventList.LevelSelect);
		}
	};
	public int optionsButton;
	private Runnable performQuit = new Runnable() {
		@Override
		public void run() {
			game.event(EventList.QuitGame);
			game.getSoundManager().playEffect("menu-click");
			Gdx.app.exit();
			System.exit(0);
		}
	};

	final public int quitButton;

	private Sprite right_indicator = new Sprite();

	private int selected_btn = 0;

	private Runnable showOptions = new Runnable() {
		@Override
		public void run() {
			game.getSoundManager().playEffect("menu-click");
			game.event(EventList.ShowOptions);
		}
	};

	final Array<Sprite> wall = new Array<Sprite>();

	private TextureAtlas wall_atlas;

	private Runnable showInstructions=new Runnable() {
		@Override
		public void run() {
			game.getSoundManager().playEffect("menu-click");
			game.event(EventList.ShowInstructions);			
		}
	};

	public ScreenMainMenu(CherokeeAnimals game) {
		super(game);
		float currentY;
		float linesOfText = 4;
		float skipAmount = overscan.height / (linesOfText);
		int fontSize = 128;
		float graphicsHeight = 0;
		float emptyHeight = 0;

		BitmapFont bmFont;
		System.out.println("bmFont create");
		bmFont = CherokeeAnimals.getFont(CherokeeAnimals.FontStyle.Script,fontSize);

		BitmapFont hsFont;
		System.out.println("hsFont create");
		hsFont = CherokeeAnimals.getFixedFont(CherokeeAnimals.FontStyle.Script,fontSize / 2);

		Color textColor = GameColor.GREEN;

		MenuLabel titleText = null;
		LabelStyle titleStyle = null;
		LabelStyle hsStyle = null;

		MenuLabel btn_NewGame = null;
		MenuLabel btn_Instructions = null;
		MenuLabel btn_Options = null;
		MenuLabel btn_Quit = null;
		LabelStyle buttonStyle = null;

		titleStyle = new LabelStyle(bmFont, textColor);
		hsStyle = new LabelStyle(hsFont, new Color(textColor));
		hsStyle.fontColor.a = 0.5f;

		buttonStyle = new LabelStyle();
		buttonStyle.font = bmFont;
		buttonStyle.fontColor = textColor;

		titleText = new MenuLabel(CHEROKEE_ANIMALS, titleStyle);
		highScore = new MenuLabel(HIGH_SCORE_000000000, hsStyle);

		btn_NewGame = new MenuLabel(NEW_GAME, buttonStyle);
		btn_Instructions = new MenuLabel(INSTRUCTIONS, buttonStyle);
		btn_Options = new MenuLabel(OPTIONS, buttonStyle);
		btn_Quit = new MenuLabel(QUIT, buttonStyle);

		/*
		 * calculate needed empty gap between menu items for even up to down
		 * spacing
		 */

		graphicsHeight = titleText.getHeight() + btn_NewGame.getHeight()
				+ btn_Options.getHeight() + btn_Quit.getHeight() + btn_Instructions.getHeight();
		emptyHeight = overscan.height - graphicsHeight;
		skipAmount = emptyHeight / (linesOfText + 1);

		/*
		 * center each line
		 */
		titleText
				.setX((overscan.x + (overscan.width - titleText.getWidth()) / 2));
		btn_NewGame.setX(overscan.x + (overscan.width - btn_NewGame.getWidth())
				/ 2);
		btn_Instructions.setX(overscan.x+(overscan.width - btn_Instructions.getWidth())/2);
		btn_Options.setX(overscan.x + (overscan.width - btn_Options.getWidth())
				/ 2);
		btn_Quit.setX(overscan.x + (overscan.width - btn_Quit.getWidth()) / 2);

		/*
		 * position each one equal distant based on screen height
		 */
		// start at top of screen
		currentY = overscan.height + overscan.y;
		// subtract empty gap + line height before placement
		currentY -= (titleText.getHeight() + skipAmount);
		titleText.setY(currentY);

		currentY -= (btn_NewGame.getHeight() + skipAmount);
		btn_NewGame.setY(currentY);
		
		currentY -= (btn_Instructions.getHeight() + skipAmount);
		btn_Instructions.setY(currentY);

		currentY -= (btn_Options.getHeight() + skipAmount);
		btn_Options.setY(currentY);

		currentY -= (btn_Quit.getHeight() + skipAmount);
		btn_Quit.setY(currentY);

		/*
		 * add buttons to buttons menu array
		 */
		btns.add(btn_NewGame);
		btns.add(btn_Instructions);
		optionsButton = btns.size;
		btns.add(btn_Options);
		quitButton = btns.size;
		btns.add(btn_Quit);

		/*
		 * register click handlers
		 */
		btn_NewGame.setRun(newGame);
		btn_Quit.setRun(performQuit);
		btn_Options.setRun(showOptions);
		btn_Instructions.setRun(showInstructions);

		/*
		 * add to "stage" for display
		 */
		gameStage.addActor(highScore);
		gameStage.addActor(titleText);
		gameStage.addActor(btn_NewGame);
		gameStage.addActor(btn_Instructions);
		gameStage.addActor(btn_Options);
		gameStage.addActor(btn_Quit);

		game.getSoundManager().loadEffect("howa");

		initBackdrop();

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
		super.hide();
		indicator.dispose();
		indicator = null;
	}

	private void highlight_button() {
		game.getSoundManager().playEffect("box_moved");

		MenuLabel label = btns.get(selected_btn);
		float left = label.getX();
		float bottom = label.getY();
		float right = label.getX() + label.getWidth();

		left_indicator.setPosition(left - left_indicator.getWidth() - 20,
				bottom);
		right_indicator.setPosition(right + 20, bottom);
	}

	private void initBackdrop() {
		wall.clear();
		PixmapPacker pack = new PixmapPacker(1024, 1024, Format.RGBA8888, 2,
				true);
		for (int i = 0; i < 32; i++) {
			pack.pack(
					i + "",
					new Pixmap(Gdx.files.internal("images/backdrops/p_" + i
							+ "_dsci2549.png")));
		}
		wall_atlas = pack.generateTextureAtlas(TextureFilter.Linear,
				TextureFilter.Linear, false);

		int px = 0;
		int py = 0;
		final int perRow = 8;
		final int columns = 4;
		for (int x = 0; x < perRow; x++) {
			py = 0;
			Sprite i = null;
			for (int y = 0; y < columns; y++) {
				int z = columns - (y + 1);
				int p = z * perRow + x;
				final AtlasRegion piece = wall_atlas.findRegion(p + "");
				i = new Sprite(piece, 0, 0, piece.getRegionWidth(),
						piece.getRegionHeight());
				i.setX(px);
				i.setY(py);
				i.setColor(1f, 1f, 1f, 0.35f);
				py += i.getHeight();
				wall.add(i);
			}
			px += i.getWidth();
		}
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

	@Override
	public void render(float delta) {
		clearScreen();
		if (showOverScan) {
			drawOverscan();
		}
		batch.begin();
		for (Sprite s : wall) {
			s.draw(batch);
		}
		batch.enableBlending();
		batch.end();
		gameStage.act();
		gameStage.draw();
		batch.begin();
		left_indicator.draw(batch);
		right_indicator.draw(batch);
		batch.end();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cherokeelessons.OUYA.animals.ScreenGameCore#show()
	 */
	@Override
	public void show() {
		super.show();
		String text;
		game.getSoundManager().playEffect("howa");
		text = String.format("HIGH SCORE: %09d", game.getHighScore());
		highScore.setText(text);
		highScore.pack();
		highScore
				.setY(overscan.y + overscan.height - highScore.getHeight() - 5);
		highScore
				.setX(overscan.x + (overscan.width - highScore.getWidth()) / 2);

		TextureRegion temp;
		indicator = new Texture("buttons/da-gi-si_2.png");
		indicator.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		System.out.println("texture size: "
				+ new Vector2(indicator.getWidth(), indicator.getHeight()));

		temp = new TextureRegion(indicator);
		System.out.println("temp size: "
				+ new Vector2(temp.getRegionWidth(), temp.getRegionHeight()));

		left_indicator.setRegion(temp);
		left_indicator.setBounds(0, 0, temp.getRegionWidth(),
				temp.getRegionHeight());

		System.out.println("indicator size: "
				+ new Vector2(left_indicator.getWidth(), left_indicator
						.getHeight()));

		right_indicator.setRegion(temp);
		right_indicator.setBounds(0, 0, temp.getRegionWidth(),
				temp.getRegionHeight());
		right_indicator.flip(true, false);

		highlight_button();

	}

}
