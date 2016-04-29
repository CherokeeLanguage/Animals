package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.Application.ApplicationType;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.cherokeelessons.common.DisplaySize;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.vocab.animals.one.enums.GameEvent;

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
		
		gameStage = new Stage(new FitViewport(screenSize.width, screenSize.height)) {
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
					game.gameEvent(GameEvent.ShowOptions);
					return true;
				}
				if (keyCode == Input.Keys.F1) {
					game.gameEvent(GameEvent.ShowOptions);
					return true;
				}
				return super.keyDown(keyCode);
			}

		};
		gameStage.getRoot().setX(screenSize.x);
		gameStage.getRoot().setY(screenSize.y);

	}

	protected void clearScreen() {
		Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b,
				clearColor.a);
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
		tv_box.rect(-o_pad, -o_pad, fullscan.width + o_pad, screenSize.y
				+ o_pad);
		tv_box.rect(-o_pad, screenSize.height + screenSize.y, fullscan.width
				+ o_pad, screenSize.y + o_pad);
		tv_box.rect(-o_pad, -o_pad, screenSize.x + o_pad, fullscan.height
				+ o_pad);
		tv_box.rect(screenSize.width + screenSize.x, 0, screenSize.x + o_pad,
				fullscan.height);
		tv_box.end();
	}

	@Override
	public void hide() {
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
		if (game.musicPlayer != null) {
			if (!Gdx.app.getType().equals(ApplicationType.Desktop)) {
				game.musicPlayer.pause();
			}
		}
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
		float scale_h;
		float scale;
		float newWidth;
		float newHeight;

		/*
		 * http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=3422
		 */
		scale = (float) fullscan.width / (float) width;
		scale_h = (float) fullscan.height / (float) height;

		if (scale_h > scale) {
			scale = scale_h;
		}

		newWidth = (float) Math.ceil(scale * width);
		newHeight = (float) Math.ceil(scale * height);

		gameStage.setViewport(new FitViewport(newWidth, newHeight));
		gameStage.getCamera().viewportHeight = newHeight;
		gameStage.getCamera().viewportWidth = newWidth;
		gameStage.getCamera().position.set(fullscan.width / 2,
				fullscan.height / 2, 0);
		gameStage.getCamera().update();

		batch.setProjectionMatrix(gameStage.getCamera().combined);
	}

	@Override
	public void resume() {
		if (game.musicPlayer != null) {
			if (!Gdx.app.getType().equals(ApplicationType.Desktop)) {
				game.musicPlayer.resume();
				game.musicPlayer.setVolume((float) game.prefs.getMasterVolume()
						* (float) game.prefs.getMusicVolume() / 10000f);
			}
		}
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
