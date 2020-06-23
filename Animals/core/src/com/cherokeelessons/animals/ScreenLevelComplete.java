package com.cherokeelessons.animals;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.common.FontLoader;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Gamepads;
import com.cherokeelessons.common.Utils;
import com.cherokeelessons.util.Callback;
import com.cherokeelessons.util.DreamLo;
import com.cherokeelessons.util.StringUtils;

public class ScreenLevelComplete extends GameScreen implements DpadInterface {

	@Override
	public boolean dpad(int keyCode) {
		switch (keyCode) {
		case Keys.DPAD_CENTER:
			game.gameEvent(GameEvent.Done);
			return true;
		}
		return false;
	}

	private static final String SCORE = "SCORE: ";
	private static final String COMPLETE = "Complete!";
	private static final String CORRECT = "% Correct!";
	private static final String LEVEL = "Level";
	private static final String TABLET_MAIN = "[BACK]";

	private BitmapFont font;

	private int fontSize = 88;
	private Label gotoMainMenu;
	private LabelStyle lStyle;
	private Label msg_accuracy;
	private Label msg_score;
	public int optionsButton;
	private Label msg_elasped_time;

	private LabelStyle tbStyle;

	final Array<Sprite> wall = new Array<Sprite>();

	private TextureAtlas wall_atlas;

	final private CtlrLevelComplete_Watch watcher = new CtlrLevelComplete_Watch(this);
	private int levelOn;
	private int correct;
	private long elapsed;
	private long elapsed_sec;
	private long elapsed_min;
	private Callback<Void> show_ranking = new Callback<Void>() {
		@Override
		public void success(Void result) {
			// updateRanking(score);
		}
	};
	private int score;

	public ScreenLevelComplete(final CherokeeAnimals game) {
		super(game);

		FontLoader fg = new FontLoader();

		font = fg.get(fontSize);

		lStyle = new LabelStyle(font, GameColor.MAIN_TEXT);
		msg_accuracy = new Label(LEVEL + " " + (game.getLevelOn() + 1) + " " + COMPLETE, lStyle);

		msg_elasped_time = new Label(game.prefs.getLevelAccuracy(game.getLevelOn()) + CORRECT, lStyle);

		msg_score = new Label(SCORE + "0", lStyle);
		msg_score.pack();

		tbStyle = new LabelStyle();
		tbStyle.font = font;
		tbStyle.fontColor = GameColor.MAIN_TEXT;

		gotoMainMenu = new Label(TABLET_MAIN, tbStyle);
		gotoMainMenu.addCaptureListener(new ClickListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				game.gameEvent(GameEvent.MainMenu);
				return true;
			}

		});
		gotoMainMenu.pack();
		gameStage.addActor(msg_score);
		gameStage.addActor(msg_accuracy);
		gameStage.addActor(msg_elasped_time);
		gameStage.addActor(gotoMainMenu);
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
		final int linesDisplayed = 4;
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
		msg_elasped_time.setX(midX - msg_elasped_time.getWidth() / 2);
		msg_elasped_time.setY(line - msg_elasped_time.getHeight() / 2);
		line += lineGap;
		msg_accuracy.setX(midX - msg_accuracy.getWidth() / 2);
		msg_accuracy.setY(line - msg_accuracy.getHeight() / 2);
		line += lineGap;
		msg_score.setX(midX - msg_score.getWidth() / 2);
		msg_score.setY(line - msg_score.getHeight() / 2);
	}

	@Override
	public void render(float delta) {
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
		positionItems();
		wall_atlas = Utils.initBackdrop(wall);
		Gamepads.addListener(watcher);
		for (Controller c : Gamepads.getControllers()) {
			watcher.connected(c);
		}
		final DreamLo lb = new DreamLo(game.prefs);
		levelOn = game.getLevelOn();
		score = game.prefs.getLastScore(levelOn);
		correct = game.prefs.getLevelAccuracy(levelOn);
		elapsed = game.prefs.getLevelTime(levelOn);
		elapsed_sec = elapsed / 1000l;
		elapsed_min = elapsed_sec / 60;
		elapsed_sec -= elapsed_min * 60;
		if (game.prefs.isLeaderBoardEnabled()) {
			lb.lb_submit((levelOn + 1) + "", score, correct, "", show_ranking);
		}
		updateDisplay();
	}

	private void updateDisplay() {
		int midX = (int) (screenSize.width / 2);
		msg_accuracy
				.setText("Level " + (levelOn + 1) + ": " + game.prefs.getLevelAccuracy(game.getLevelOn()) + CORRECT);
		msg_accuracy.pack();
		msg_accuracy.setX(midX - msg_accuracy.getWidth() / 2);
		msg_elasped_time.setText("Time elapsed: " + elapsed_min + " min " + elapsed_sec + " sec.");
		msg_elasped_time.pack();
		msg_elasped_time.setX(midX - msg_elasped_time.getWidth() / 2);

		String str_score = score + "";
		str_score = StringUtils.reverse(str_score);
		str_score = str_score.replaceAll("(\\d{3})", "$1,");
		str_score = StringUtils.reverse(str_score);
		str_score = StringUtils.strip(str_score, ",");
		msg_score.setText(SCORE + str_score);
		msg_score.pack();
		msg_score.setX(midX - msg_score.getWidth() / 2);
	}
}
