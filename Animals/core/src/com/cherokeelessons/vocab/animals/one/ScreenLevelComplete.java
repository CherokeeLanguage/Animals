package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.common.FontLoader;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Gamepads;
import com.cherokeelessons.common.Leader;
import com.cherokeelessons.common.Leader.CB_AddScore;
import com.cherokeelessons.common.OS;
import com.cherokeelessons.common.Utils;
import com.cherokeelessons.leaderboards.pojo.BoardScore;
import com.cherokeelessons.vocab.animals.one.enums.GameEvent;

public class ScreenLevelComplete extends GameScreen {

	private static final String OUYA_NEXT_LEVEL = "[O] - Choose Another Level";
	private static final String TABLET_NEXT_LEVEL = "Tap Here to Choose Another Level";
	private static final String COMPLETE = "Complete!";
	private static final String CORRECT = "% Correct!";
	private static final String LEVEL = "Level";
	private static final String OUYA_PLAY_AGAIN = "[U] - Play Level Again";
	private static final String TABLET_PLAY_AGAIN = "Tap Here to Play Level Again";
	private static final String OUYA_MAIN = "[A] - Return to Main Menu";
	private static final String TABLET_MAIN = "Tap here to Return to Main Menu";

	private BitmapFont font;

	private int fontSize = 88;
	private Label goLevelSelect;
	private Label gotoMainMenu;
	private LabelStyle lStyle;
	private Label msg_accuracy;
	public int optionsButton;
	private Label msg_elasped_time;
	
	private Label msg_ranking;

	private Label playLevelAgain;

	private LabelStyle tbStyle;

	final Array<Sprite> wall = new Array<Sprite>();

	private TextureAtlas wall_atlas;

	final private ControllerLevelComplete_Watch watcher = new ControllerLevelComplete_Watch(
			this);
	private int levelOn;
	private int correct;
	private long elapsed;
	private long elapsed_sec;
	private long elapsed_min;
	private CB_AddScore show_ranking=new CB_AddScore() {
		@Override
		public void run(BoardScore score) {
			updateRanking(score);
		}
	};
	public ScreenLevelComplete(final CherokeeAnimals game) {
		super(game);

		FontLoader fg = new FontLoader();

		font = fg.get(fontSize);

		lStyle = new LabelStyle(font, GameColor.GREEN);
		msg_accuracy = new Label(LEVEL + " " + (game.getLevelOn() + 1) + " "
				+ COMPLETE, lStyle);

		msg_elasped_time = new Label(game.prefs.getLevelAccuracy(game
				.getLevelOn()) + CORRECT, lStyle);
		
		msg_ranking = new Label("Level Ranking: ??, Overall Ranking: ??", lStyle);

		tbStyle = new LabelStyle();
		tbStyle.font = font;
		tbStyle.fontColor = GameColor.GREEN;

		boolean ouya = OS.Platform.Ouya.equals(OS.platform);
		playLevelAgain = new Label(ouya ? OUYA_PLAY_AGAIN : TABLET_PLAY_AGAIN,
				tbStyle);
		playLevelAgain.pack();
		playLevelAgain.addCaptureListener(new ClickListener(){

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				game.gameEvent(GameEvent.ShowGameBoard);
				return true;
			}
			
		});

		goLevelSelect = new Label(ouya ? OUYA_NEXT_LEVEL : TABLET_NEXT_LEVEL,
				tbStyle);
		goLevelSelect.addCaptureListener(new ClickListener(){

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				game.gameEvent(GameEvent.NewGame);
				return true;
			}
			
		});
		goLevelSelect.pack();

		gotoMainMenu = new Label(ouya ? OUYA_MAIN : TABLET_MAIN, tbStyle);
		gotoMainMenu.addCaptureListener(new ClickListener(){

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				game.gameEvent(GameEvent.MainMenu);
				return true;
			}
			
		});
		gotoMainMenu.pack();

		gameStage.addActor(msg_accuracy);
		gameStage.addActor(msg_elasped_time);
		gameStage.addActor(msg_ranking);
		gameStage.addActor(gotoMainMenu);
		gameStage.addActor(playLevelAgain);
		gameStage.addActor(goLevelSelect);
		gameStage.getRoot().setX(screenSize.x);
		gameStage.getRoot().setY(screenSize.y);
	}

	@Override
	public void hide() {
		wall.clear();
		wall_atlas.dispose();
		for (Controller controller : Gamepads.getControllers()) {
			watcher.disconnected(controller);
		}
		Gamepads.clearListeners();
		super.hide();
	}

	private void positionItems() {
		final int linesDisplayed = 6;
		int lineGap;
		int midX;
		int line;

		lineGap = (int) (screenSize.height / linesDisplayed);
		line = lineGap / 2;
		midX = (int) (screenSize.width / 2);

		updateDisplay();

		gotoMainMenu.setX(midX - gotoMainMenu.getWidth() / 2);
		gotoMainMenu.setY(line - gotoMainMenu.getHeight() / 2);
		line += lineGap;
		goLevelSelect.setX(midX - goLevelSelect.getWidth() / 2);
		goLevelSelect.setY(line - goLevelSelect.getHeight() / 2);
		line += lineGap;
		playLevelAgain.setX(midX - playLevelAgain.getWidth() / 2);
		playLevelAgain.setY(line - playLevelAgain.getHeight() / 2);
		line += lineGap;
		msg_ranking.setX(midX - msg_ranking.getWidth() / 2);
		msg_ranking.setY(line - msg_ranking.getHeight() / 2);
		line += lineGap;
		msg_elasped_time.setX(midX - msg_elasped_time.getWidth() / 2);
		msg_elasped_time.setY(line - msg_elasped_time.getHeight() / 2);
		line += lineGap;
		msg_accuracy.setX(midX - msg_accuracy.getWidth() / 2);
		msg_accuracy.setY(line - msg_accuracy.getHeight() / 2);
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
		positionItems();
		wall_atlas=Utils.initBackdrop(wall);
		Gamepads.addListener(watcher);
		for (Controller c : Gamepads.getControllers()) {
			watcher.connected(c);
		}
		final Leader lb = new Leader();
		levelOn=game.getLevelOn();
		correct = game.prefs.getLevelAccuracy(levelOn);
		elapsed = game.prefs.getLevelTime(levelOn);
		elapsed_sec = elapsed/1000l;
		elapsed_min = elapsed_sec/60;
		elapsed_sec -= elapsed_min*60;
		//record the score, if the leaderboard enabled
		if (game.prefs.isLeaderBoardEnabled()) {
			lb.addScore(levelOn+1, correct, elapsed, show_ranking);
		}
		updateDisplay();
	}

	private void updateDisplay() {
		int midX = (int) (screenSize.width / 2);
		msg_accuracy.setText("Level "+(levelOn+1)+": "+game.prefs.getLevelAccuracy(game.getLevelOn())
				+ CORRECT);
		msg_accuracy.pack();
		msg_accuracy.setX(midX - msg_accuracy.getWidth() / 2);
		msg_elasped_time.setText("Time elapsed: "+elapsed_min+" min "+elapsed_sec+" sec.");
		msg_elasped_time.pack();
		msg_elasped_time.setX(midX - msg_elasped_time.getWidth() / 2);
	}
	
	private void updateRanking(BoardScore score) {
		//"Level Ranking: ??, Overall Ranking: ??"
		int midX = (int) (screenSize.width / 2);
		msg_ranking.setText("Level Ranking: #"+score.rank_level+", Overall Ranking: #"+score.rank_overall);
		msg_ranking.pack();
		msg_ranking.setX(midX - msg_ranking.getWidth() / 2);
	}
}
