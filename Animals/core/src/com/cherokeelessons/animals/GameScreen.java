package com.cherokeelessons.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.common.DisplaySize;
import com.cherokeelessons.common.GameColor;

import aurelienribon.tweenengine.TweenManager;

public abstract class GameScreen implements Screen {
	protected TweenManager tmanager;
	protected AssetManager assets;
	protected CherokeeAnimals game = null;
	public Rectangle fullscan = new Rectangle();
	protected boolean showOverscan = false;
	private ShapeRenderer tv_box = null;

	final protected Rectangle screenSize;
	final protected Color clearColor;
	protected Batch batch;

	protected Stage gameStage;

	private boolean isPaused = false;

	public boolean isPaused() {
		return isPaused;
	}

	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}

	private void log(String message) {
		Gdx.app.log(this.getClass().getName(), message);
	}

	public GameScreen(final CherokeeAnimals game) {
		this.game = game;

		showOverscan = game.prefs.getBoolean("showscreenSize", false);
		game.prefs.putBoolean("showscreenSize", showOverscan);
		game.prefs.flush();

		assets = new AssetManager();

		screenSize = DisplaySize._1080p.overscansize();
		fullscan = DisplaySize._1080p.size();

		clearColor = new Color(Color.WHITE);

		tmanager = new TweenManager();

		gameStage = new Stage(new FitViewport(fullscan.width, fullscan.height)) {
			@Override
			public boolean keyDown(int keyCode) {
				if (keyCode == Input.Keys.ESCAPE) {
					game.gameEvent(GameEvent.Done);
					return true;
				}
				if (keyCode == Input.Keys.BACK) {
					game.gameEvent(GameEvent.Done);
					return true;
				}
				if (keyCode == Input.Keys.MENU) {
					game.gameEvent(GameEvent.Menu);
					return true;
				}
				if (keyCode == Input.Keys.F1) {
					game.gameEvent(GameEvent.Menu);
					return true;
				}
				if (mapToGamepad(keyCode)) {
					return true;
				}
				log("keyDown: " + keyCode);
				return super.keyDown(keyCode);
			}

		};
		gameStage.getRoot().setX(screenSize.x);
		gameStage.getRoot().setY(screenSize.y);
		gameStage.getRoot().setTouchable(Touchable.enabled);
	}

	private boolean mapToGamepad(int keyCode) {
		if (!(this instanceof DpadInterface)) {
			return false;
		}
		DpadInterface dpad = (DpadInterface) this;
		switch (keyCode) {
		case Input.Keys.DPAD_CENTER:
		case Input.Keys.DPAD_DOWN:
		case Input.Keys.DPAD_LEFT:
		case Input.Keys.DPAD_RIGHT:
		case Input.Keys.DPAD_UP:
			return dpad.dpad(keyCode);
		case Input.Keys.ENTER:
		case Input.Keys.NUMPAD_5:
			return dpad.dpad(Input.Keys.DPAD_CENTER);
		default:
		}
		return false;
	}

	protected void clearScreen() {
		Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	private void disconnectInputProcessor() {
		if (Gdx.input.getInputProcessor() == null) {
			return;
		}
		if (Gdx.input.getInputProcessor().equals(gameStage)) {
			Gdx.input.setInputProcessor(null);
		}
	}

	@Override
	public void dispose() {
		disconnectInputProcessor();
		tmanager.killAll();
		gameStage.clear();
		batch.dispose();
	}

	protected int o_pad = 200;

	protected void drawOverscan() {
		if (!isShowOverscan())
			return;
		if (tv_box == null)
			return;
		tv_box.setProjectionMatrix(gameStage.getCamera().combined);
		tv_box.setColor(GameColor.FIREBRICK);
		tv_box.begin(ShapeType.Filled);
		tv_box.rect(-o_pad, -o_pad, fullscan.width + o_pad, screenSize.y + o_pad);
		tv_box.rect(-o_pad, screenSize.height + screenSize.y, fullscan.width + o_pad, screenSize.y + o_pad);
		tv_box.rect(-o_pad, -o_pad, screenSize.x + o_pad, fullscan.height + o_pad);
		tv_box.rect(screenSize.width + screenSize.x, 0, screenSize.x + o_pad, fullscan.height);
		tv_box.end();
	}

	@Override
	public void hide() {
		Gdx.app.log(this.getClass().getName(), "hide");
		disconnectInputProcessor();
		if (tv_box != null) {
			tv_box.dispose();
			tv_box = null;
		}
		tmanager.pause();
	}

	public boolean isShowOverscan() {
		return showOverscan;
	}

	@Override
	public void pause() {
//		if (game.musicPlayer != null) {
//			if (!Gdx.app.getType().equals(ApplicationType.Desktop)) {
//				game.musicPlayer.pause();
//			}
//		}
		disconnectInputProcessor();
	}

	@Override
	public void render(float delta) {
		if (!isPaused) {
			gameStage.act(delta);
			tmanager.update(delta);
		}
		clearScreen();
		if (showOverscan) {
			drawOverscan();
		}
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log(this.getClass().getName(), "resize: " + width + "x" + height);
		gameStage.getViewport().update(width, height, true);
		batch.setProjectionMatrix(gameStage.getCamera().combined);
	}

	@Override
	public void resume() {
//		if (game.musicPlayer != null) {
//			if (!Gdx.app.getType().equals(ApplicationType.Desktop)) {
//				game.musicPlayer.resume();
//				game.musicPlayer
//						.setVolume((float) game.prefs.getMasterVolume() * (float) game.prefs.getMusicVolume() / 10000f);
//			}
//		}
		tmanager.resume();
		Gdx.input.setInputProcessor(gameStage);
	}

	public void setShowOverscan(boolean showscreenSize) {
		this.showOverscan = showscreenSize;
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(gameStage);
		tv_box = new ShapeRenderer();
		batch = gameStage.getBatch();
	}
}
