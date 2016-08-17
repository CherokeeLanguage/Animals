package com.cherokeelessons.animals;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Gamepads;
import com.cherokeelessons.common.Utils;
import com.cherokeelessons.util.Callback;
import com.cherokeelessons.util.DreamLo;
import com.cherokeelessons.util.GameScores;
import com.cherokeelessons.util.GameScores.GameScore;

public class ScreenHighScores extends GameScreen implements DpadInterface {

	private static final int FONTSIZE = 64;
	private Table container;
	private ScrollPane scroll;
	private Table scrolltable;

	private final Array<Sprite> wall = new Array<Sprite>();
	private TextureAtlas wall_atlas;

	private final CtlrHighScores_Watch watcher = new CtlrHighScores_Watch(this);

	public ScreenHighScores(CherokeeAnimals game) {
		super(game);
		wall_atlas = Utils.initBackdrop(wall);
	}

	private Callback<GameScores> showScores = new Callback<GameScores>() {
		@Override
		public void success(GameScores result) {
			BitmapFont font = game.fg.get(64);
			scrolltable.reset();
			scrolltable.defaults().expandX();
			LabelStyle style = new LabelStyle(font, GameColor.SCORES_TEXT);
			LabelStyle mestyle = new LabelStyle(font, GameColor.SCORES_TEXT_ME);
			String text = "Rank";
			scrolltable.add(new Label(text, style)).center();
			text = "Score";
			scrolltable.add(new Label(text, style)).center();
			text = "Level";
			scrolltable.add(new Label(text, style)).center();
			text = "Accuracy";
			scrolltable.add(new Label(text, style)).center();

			for (GameScore score : result.list) {
				LabelStyle tmp = score.isMe?mestyle:style;
				scrolltable.row();
				scrolltable.add(new Label(score.rank, tmp)).center();
				scrolltable.add(new Label(score.score, tmp)).center();
				scrolltable.add(new Label(score.levelOn+"", tmp)).center();
				scrolltable.add(new Label(score.pctCorrect+"%", tmp)).center();
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
	
	public static final String[] ranks = { "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th", "11th", "12th", "13th", "14th", "15th", "16th", "17th", "18th", "19th", "20th" };

	@Override
	public void show() {
		super.show();
		Gamepads.addListener(watcher);
		for (Controller c : Gamepads.getControllers()) {
			watcher.connected(c);
		}
		LabelStyle lstyle=new LabelStyle();
		lstyle.font=game.fg.get(FONTSIZE);
		lstyle.fontColor=GameColor.MAIN_TEXT;
		String textExit;
		textExit = "[BACK]";
		Label exit = new Label(textExit, lstyle);
		exit.addListener(new InputListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				game.gameEvent(GameEvent.Done);
				return true;
			}
		});
		DreamLo lb = new DreamLo(game.prefs);
		container = new Table();
		container.setWidth(screenSize.width);
		container.setHeight(screenSize.height);
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
	}

	@Override
	public void hide() {
		super.hide();
		for (Controller controller : Gamepads.getControllers()) {
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
	public boolean dpad(int keyCode) {
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
	public void dispose() {
		wall_atlas.dispose();
		super.dispose();
	}

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
	}
}
