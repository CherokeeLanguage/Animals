package com.cherokeelessons.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.common.BackdropData;
import com.cherokeelessons.common.DisplaySize;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Utils;

import aurelienribon.tweenengine.TweenManager;

public abstract class GameScreen implements Screen, DpadInterface {
	
	protected ControllerAdapter gamepad;
	
	protected TweenManager tmanager;
	protected AssetManager assets;
	protected CherokeeAnimals game = null;
	public Rectangle fullZoneBox = new Rectangle();
	protected boolean showOverscan = false;
	private ShapeRenderer tv_box = null;

	final protected Rectangle safeZoneBox;
	final protected Color clearColor;
//	protected Batch batch;

	protected Stage gameStage;

	private boolean isPaused = false;

	protected int o_pad = 200;

	public GameScreen(final CherokeeAnimals game) {
		this.game = game;

		showOverscan = game.prefs.getBoolean("showscreenSize", false);
		game.prefs.putBoolean("showscreenSize", showOverscan);
		game.prefs.flush();

		assets = new AssetManager();

		safeZoneBox = DisplaySize._1080p.overscansize();
		fullZoneBox = DisplaySize._1080p.size();

		clearColor = new Color(Color.WHITE);

		tmanager = new TweenManager();

		gameStage = new Stage() {
			@Override
			public boolean keyDown(final int keyCode) {
				if (keyCode == Input.Keys.ESCAPE) {
					game.gameEvent(GameEvent.EXIT_SCREEN);
					return true;
				}
				if (keyCode == Input.Keys.BACK) {
					game.gameEvent(GameEvent.EXIT_SCREEN);
					return true;
				}
				if (mapToGamepad(keyCode)) {
					return true;
				}
				log("keyDown: " + keyCode);
				return super.keyDown(keyCode);
			}
		};
		//force resize to ensure *camera* is set correctly
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		gameStage.getRoot().setTouchable(Touchable.enabled);
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
	}

	protected void drawOverscan() {
		if (!isShowOverscan()) {
			return;
		}
		if (tv_box == null) {
			return;
		}
		tv_box.setProjectionMatrix(gameStage.getCamera().combined);
		tv_box.setColor(GameColor.FIREBRICK);
		tv_box.begin(ShapeType.Filled);
		tv_box.rect(-o_pad, -o_pad, fullZoneBox.width + o_pad, safeZoneBox.y + o_pad);
		tv_box.rect(-o_pad, safeZoneBox.height + safeZoneBox.y, fullZoneBox.width + o_pad, safeZoneBox.y + o_pad);
		tv_box.rect(-o_pad, -o_pad, safeZoneBox.x + o_pad, fullZoneBox.height + o_pad);
		tv_box.rect(safeZoneBox.width + safeZoneBox.x, 0, safeZoneBox.x + o_pad, fullZoneBox.height);
		tv_box.end();
	}

	public FitViewport getFitViewport(final Camera camera) {
		final Rectangle surrounds = DisplaySize._1080p.size();
		final FitViewport fitViewport = new FitViewport(surrounds.width, surrounds.height, camera);
		fitViewport.update((int) surrounds.width, (int) surrounds.height, true);
		log("Camera Size: " + (int) surrounds.getWidth() + "x" + (int) surrounds.getHeight());
		return fitViewport;
	}

	private BackdropData backdrop;
	@Override
	public void hide() {
		Gdx.app.log(this.getClass().getName(), "hide");
		disconnectInputProcessor();
		if (tv_box != null) {
			tv_box.dispose();
			tv_box = null;
		}
		tmanager.pause();
		if (backdrop!=null) {
			backdrop.getGroup().remove();
			backdrop.dispose();
		}
	}

	public boolean isPaused() {
		return isPaused;
	}

	public boolean isShowOverscan() {
		return showOverscan;
	}

	protected void log(final String message) {
		Gdx.app.log(this.getClass().getName(), message);
	}

	protected boolean mapToGamepad(final int keyCode) {
		switch (keyCode) {
		case Input.Keys.DPAD_CENTER:
		case Input.Keys.DPAD_DOWN:
		case Input.Keys.DPAD_LEFT:
		case Input.Keys.DPAD_RIGHT:
		case Input.Keys.DPAD_UP:
			if (isPaused()) {
				return false;
			}
			return dpad(keyCode);
		case Input.Keys.ENTER:
		case Input.Keys.NUMPAD_5:
			return dpad(Input.Keys.DPAD_CENTER);
		default:
		}
		return false;
	}

	@Override
	public void pause() {
		disconnectInputProcessor();
	}

	@Override
	public void render(final float delta) {
		if (!isPaused) {
			gameStage.act(delta);
			tmanager.update(delta);
		}
		clearScreen();
		gameStage.draw();
		if (showOverscan) {
			drawOverscan();
		}
	}

	@Override
	public void resize(int width, int height) {
		log("screen resize: " + width + "x" + height);
		/*
		 * do the actual resize
		 */
		Camera camera = gameStage.getCamera();
		if (camera instanceof OrthographicCamera) {
			((OrthographicCamera)camera).zoom=game.zoom()/100f;
		}
		gameStage.setViewport(getFitViewport(camera));
		gameStage.getViewport().update(width, height, true);
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

	public void setPaused(final boolean isPaused) {
		this.isPaused = isPaused;
	}

	public void setShowOverscan(final boolean showscreenSize) {
		this.showOverscan = showscreenSize;
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(gameStage);
		tv_box = new ShapeRenderer();
		if (useBackdrop()) {
			backdrop = Utils.backdrop();
			Group backdropGroup = backdrop.getGroup();
			gameStage.addActor(backdropGroup);
			backdropGroup.setZIndex(0);
		}
	}
	
	protected abstract boolean useBackdrop();
}
