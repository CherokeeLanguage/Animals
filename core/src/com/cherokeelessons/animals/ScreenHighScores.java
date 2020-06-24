package com.cherokeelessons.animals;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.common.BackdropData;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Gamepads;
import com.cherokeelessons.common.Utils;
import com.cherokeelessons.util.Callback;
import com.cherokeelessons.util.DreamLo;
import com.cherokeelessons.util.GameScores;
import com.cherokeelessons.util.GameScores.GameScore;

public class ScreenHighScores extends GameScreen implements DpadInterface {

	private static final int FONTSIZE = 64;
	public static final String[] ranks = { "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th",
			"11th", "12th", "13th", "14th", "15th", "16th", "17th", "18th", "19th", "20th" };
	private Table container;
	private ScrollPane scroll;

	private Table scrolltable;

	private BackdropData wall_atlas;

	private final CtlrHighScores_Watch watcher = new CtlrHighScores_Watch(this);

	private final Callback<GameScores> showScores = new Callback<GameScores>() {
		@Override
		public void success(final GameScores result) {
			final BitmapFont font = game.fg.get(64);
			scrolltable.reset();
			scrolltable.defaults().expandX();
			final LabelStyle style = new LabelStyle(font, GameColor.SCORES_TEXT);
			final LabelStyle mestyle = new LabelStyle(font, GameColor.SCORES_TEXT_ME);
			String text = "Rank";
			scrolltable.add(new Label(text, style)).center();
			text = "Score";
			scrolltable.add(new Label(text, style)).center();
			text = "Level";
			scrolltable.add(new Label(text, style)).center();
			text = "Accuracy";
			scrolltable.add(new Label(text, style)).center();

			for (final GameScore score : result.list) {
				final LabelStyle tmp = score.isMe ? mestyle : style;
				scrolltable.row();
				scrolltable.add(new Label(score.rank, tmp)).center();
				scrolltable.add(new Label(score.score, tmp)).center();
				scrolltable.add(new Label(score.levelOn + "", tmp)).center();
				scrolltable.add(new Label(score.pctCorrect + "%", tmp)).center();
			}

			for (int ix = result.list.size(); ix < ranks.length; ix++) {
				scrolltable.row();
				scrolltable.add(new Label(ranks[ix], style)).center();
				scrolltable.add(new Label("", style)).center();
				scrolltable.add(new Label("", style)).center();
				scrolltable.add(new Label("", style)).center();
			}
		}
	};

	public ScreenHighScores(final CherokeeAnimals game) {
		super(game);
	}

	@Override
	public void dispose() {
		if (wall_atlas!=null) {
			wall_atlas.dispose();
		}
		super.dispose();
	}

	@Override
	public boolean dpad(final int keyCode) {
		switch (keyCode) {
		case Keys.DPAD_CENTER:
			game.gameEvent(GameEvent.Done);
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

	@Override
	public void hide() {
		super.hide();
		for (final Controller controller : Gamepads.getControllers()) {
			watcher.disconnected(controller);
		}
		Gamepads.clearListeners();
		gameStage.clear();
	}

	public void hud_moveNorth() {
		scroll.setScrollY(scroll.getScrollY() - 500);
	}

	public void hud_moveSouth() {
		scroll.setScrollY(scroll.getScrollY() + 500);
	}

	@Override
	public void render(final float delta) {
		super.render(delta);
	}

	@Override
	public void show() {
		super.show();
		Gamepads.addListener(watcher);
		for (final Controller c : Gamepads.getControllers()) {
			watcher.connected(c);
		}
		final LabelStyle lstyle = new LabelStyle();
		lstyle.font = game.fg.get(FONTSIZE);
		lstyle.fontColor = GameColor.MAIN_TEXT;
		String textExit;
		textExit = "[BACK]";
		final Label exit = new Label(textExit, lstyle);
		exit.addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
					final int button) {
				game.gameEvent(GameEvent.Done);
				return true;
			}
		});
		final DreamLo lb = new DreamLo(game.prefs);
		container = new Table();
		container.setWidth(fullZoneBox.width);
		container.setHeight(fullZoneBox.height);
		scrolltable = new Table();
		scroll = new ScrollPane(scrolltable);
		scroll.setFadeScrollBars(false);
		scroll.setSmoothScrolling(true);
		scroll.setForceScroll(false, true);
		container.row();
		container.add(scroll).expand().fill().top();
		container.row();
		container.add(exit).right();
		gameStage.addActor(container);
		gameStage.setScrollFocus(scroll);
		lb.lb_getScores(showScores);
		wall_atlas = Utils.initBackdrop();
		Group backdropGroup = new Group();
		for (Image image: wall_atlas.getImages()) {
			backdropGroup.addActor(image);
		}
		gameStage.addActor(backdropGroup);
		backdropGroup.setSize(gameStage.getWidth(), gameStage.getHeight());
		backdropGroup.setZIndex(0);
		backdropGroup.setColor(1f, 1f, 1f, 0.35f);
	}
}
