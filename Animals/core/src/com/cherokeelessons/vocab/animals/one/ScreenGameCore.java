package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cherokeelessons.vocab.animals.one.CherokeeAnimals.FontStyle;
import com.cherokeelessons.vocab.animals.one.GameEvent.EventList;
import com.cherokeelessons.vocab.animals.one.StageBase.OnBackHandler;
import com.cherokeelessons.vocab.animals.one.StageBase.onMenuHandler;

public abstract class ScreenGameCore implements Screen {

	public static class GameColor {
		public static final Color BROWN = new Color(165f / 255f, 42f / 255f,
				42f / 255f, 1);
		public static final Color CORNSILK4 = new Color(129f / 255f,
				136f / 255f, 120f / 255f, 1);
		public static final Color CRIMSON = new Color(220f / 255f, 20f / 255f,
				60f / 255f, 1);
		public static final Color FIREBRICK = new Color(178f / 255f,
				34f / 255f, 34f / 255f, 1);
		public static final Color GOLD2 = new Color(238f / 255f, 201f / 255f,
				0f / 255f, 1);
		public static final Color GOLD3 = new Color(205f / 255f, 173f / 255f,
				0f / 255f, 1);
		public static final Color GREEN = new Color(0, .5f, 0, 1);
		public static final Color DARKGREEN = new Color(0, .25f, 0, 1);
		public static final Color PURPLE = new Color(128f / 255f, 0f / 255f,
				128f / 255f, 1);
	}

	protected AssetManager assets;
	protected StageBase backDrop;
	protected Color backgroundColor = Color.WHITE;
	protected SpriteBatch batch;
	private float currentElapsed = 0;
	private boolean debug;
	private FPSLogger fps = new FPSLogger();
	protected CherokeeAnimals game = null;
	protected StageBase gameStage;
	protected OnBackHandler goBack = new OnBackHandler() {
		@Override
		public void doOnBack() {
			if (onBack()) {
				game.event(EventList.GoBack);
			}
		}
	};

	private float heap_ticker = 0f;

	protected StageBase hud;
	protected boolean isDisposed = false;
	private Label lfps;
	protected onMenuHandler onMenu = new onMenuHandler() {

		@Override
		public void doOnMenu() {
			if (onMenu()) {
				game.event(EventList.GoMenu);
			}
		}
	};
	final public Rectangle overscan = new Rectangle();
	protected int screenHeight;
	protected int screenWidth;

	private boolean showFPS;

	protected boolean showOverScan = false;

	protected SpriteBatch spriteBatch;

	protected StageBase stageSafeZone;

	protected DisplaySize.Resolution stageSize;

	private ShapeRenderer tv_box = null;

	final protected Vector2 viewPortSize = new Vector2();

	public ScreenGameCore(CherokeeAnimals game) {
		this.game = game;
		
		showOverScan=game.getOptions().getBoolean("showOverscan", false);
		game.getOptions().putBoolean("showOverscan", showOverScan);
		game.getOptions().flush();

		if (isDebug()) {
			System.out.println("Create: " + getClass().getSimpleName());
		}

		spriteBatch = new SpriteBatch();
		stageSize = DisplaySize._1080p.size();
		Viewport vp =  new FitViewport(stageSize.w, stageSize.h);
		gameStage = new StageBase(vp, spriteBatch);
		stageSafeZone = new StageBase(vp, spriteBatch);
		backDrop = new StageBase(vp, spriteBatch);
		hud = new StageBase(vp, spriteBatch);
		setDebug(false);
		setShowFPS(false);
		backgroundColor = new Color(Color.WHITE);
		currentElapsed = 0;
		fps = new FPSLogger();
		assets = new AssetManager();

		float gap = 0.075f;
		float gw = (float) stageSize.w * gap;
		float gh = (float) stageSize.h * gap;
		overscan.x = gw;
		overscan.y = gh;
		overscan.width = (float) stageSize.w - gw * 2;
		overscan.height = (float) stageSize.h - gh * 2;

		screenWidth = game.screenWidth;
		screenHeight = game.screenHeight;

		gameStage.setOnBack(goBack);
		gameStage.setOnMenu(onMenu);

		LabelStyle ls = new LabelStyle();
		ls.font = CherokeeAnimals.getFont(CherokeeAnimals.FontStyle.FreeSans, 24);
		ls.fontColor = new Color(Color.RED);
		lfps = new Label("", ls);
		lfps.setX(2);
		lfps.setY(2);

	}

	protected void clearScreen() {
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g,
				backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	private void disconnectInputProcessor() {
		if (isDebug()) {
			System.out.println("Disconnect: " + getClass().getSimpleName());
		}
		if (Gdx.input.getInputProcessor() == null) {
			return;
		}
		if (Gdx.input.getInputProcessor().equals(gameStage)) {
			Gdx.input.setInputProcessor(null);
		}
	}

	@Override
	public void dispose() {
		if (isDebug()) {
			System.out.println("Dispose: " + getClass().getSimpleName());
		}
		disconnectInputProcessor();
		gameStage.clear();
		gameStage.dispose();
		gameStage = null;
	}

	protected void drawOverscan() {
		final int padding = 200;
		Camera cam = stageSafeZone.getCamera();
		if (tv_box == null)
			return;
		tv_box.setProjectionMatrix(cam.combined);
		tv_box.setColor(GameColor.FIREBRICK);
		tv_box.begin(ShapeType.Filled);
		tv_box.rect(-padding, -padding, DisplaySize._1080p.width() + padding,
				overscan.y + padding);
		tv_box.rect(-padding, overscan.height + overscan.y,
				DisplaySize._1080p.width() + padding, overscan.y + padding);
		tv_box.rect(-padding, -padding, overscan.x + padding,
				DisplaySize._1080p.height() + padding);
		tv_box.rect(overscan.width + overscan.x, 0, overscan.x + padding,
				DisplaySize._1080p.height());
		tv_box.end();
	}

	@Override
	public void hide() {
		if (isDebug()) {
			System.out.println("Hide: " + getClass().getSimpleName());
		}
		disconnectInputProcessor();
		if (tv_box != null) {
			tv_box.dispose();
			tv_box = null;
		}
		if (batch != null) {
			batch.dispose();
			batch = null;
		}
	}

	protected boolean isDebug() {
		return debug;
	}

	protected boolean isShowFPS() {
		return showFPS;
	}

	public boolean isShowOverScan() {
		return showOverScan;
	}

	protected boolean onBack() {
		return true;
	}

	protected boolean onMenu() {
		return true;
	}

	@Override
	public void pause() {
		if (isDebug()) {
			System.out.println("Pause: " + getClass().getSimpleName());
		}
		disconnectInputProcessor();
	}

	@Override
	public void render(float delta) {
		heap_ticker += delta;
		if (heap_ticker >= 15) {
			heap_ticker = 0;
			Gdx.app.log("HEAP", (Gdx.app.getJavaHeap() / 1024)+", "+(Gdx.app.getNativeHeap()/1024));
		}

		gameStage.act(delta);
		hud.act(delta);

		clearScreen();

		if (showOverScan) {
			drawOverscan();
		}

		backDrop.draw();
		gameStage.draw();
		hud.draw();

		currentElapsed += delta;
		if (currentElapsed > 1) {
			currentElapsed = 0;
			if (isShowFPS()) {
				fps.log();
			}
			lfps.setText(String.valueOf(Gdx.graphics.getFramesPerSecond()));
			lfps.pack();
		}
	}

	@Override
	public void resize(int width, int height) {
		if (isDisposed)
			return;
		float scale_h;
		float scale;
		float newWidth;
		float newHeight;

		/*
		 * http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=3422
		 */
		scale = (float) stageSize.w / (float) width;
		scale_h = (float) stageSize.h / (float) height;

		if (scale_h > scale) {
			scale = scale_h;
		}

		newWidth = (float) Math.ceil(scale * width);
		newHeight = (float) Math.ceil(scale * height);

		viewPortSize.x = newWidth;
		viewPortSize.y = newHeight;

		if (isDebug()) {
			System.out.println("=============================");
			System.out.println("scale: " + scale);
			System.out.println("Width: " + newWidth + ", Height: " + newHeight);
			System.out.println("=============================");
		}

		Camera cam = gameStage.getCamera();
		cam.viewportHeight = newHeight;
		cam.viewportWidth = newWidth;
		cam.position.set(stageSize.w / 2, stageSize.h / 2, 0);
		cam.update();

		batch.setProjectionMatrix(cam.combined);

		Camera hudcam = hud.getCamera();
		hudcam.viewportHeight = newHeight;
		hudcam.viewportWidth = newWidth;
		hudcam.position.set(stageSize.w / 2, stageSize.h / 2, 0);
		hudcam.update();

		Camera scam = stageSafeZone.getCamera();
		scam.viewportHeight = newHeight;
		scam.viewportWidth = newWidth;
		scam.position.set(stageSize.w / 2, stageSize.h / 2, 0);
		scam.update();

	}

	@Override
	public void resume() {
		if (isDebug()) {
			System.out.println("Resume: " + getClass().getSimpleName());
		}
		Gdx.input.setInputProcessor(gameStage);
	}

	protected void setDebug(boolean debug) {
		this.debug = debug;
	}

	protected void setShowFPS(boolean showFPS) {
		this.showFPS = showFPS;
	}

	public void setShowOverScan(boolean showOverScan) {
		this.showOverScan = showOverScan;
	}

	@Override
	public void show() {
		if (isDebug()) {
			System.out.println("Show: " + getClass().getSimpleName());
		}
		Gdx.input.setInputProcessor(gameStage);
		tv_box = new ShapeRenderer();
		batch = new SpriteBatch();
	}
}

class StageBase extends Stage {
	interface OnBackHandler {
		void doOnBack();
	}

	interface onMenuHandler {
		void doOnMenu();
	}

	private OnBackHandler onBack = null;

	private onMenuHandler onMenu = null;

	protected StageBase(Viewport viewport) {
		super(viewport);
	}

	protected StageBase(Viewport viewport,
			SpriteBatch batch) {
		super(viewport, batch);
	}

	@Override
	public boolean keyDown(int character) {
		if ((character == Keys.ESCAPE || character == Keys.BACK)
				&& onBack != null) {
			onBack.doOnBack();
			return true;
		}
		if ((character == Keys.MENU || character == Keys.M || character == Keys.O)
				&& onMenu != null) {
			onMenu.doOnMenu();
			return true;
		}
		return super.keyDown(character);
	}

	public void setOnBack(OnBackHandler onBack) {
		this.onBack = onBack;
	}

	public void setOnMenu(onMenuHandler onMenu) {
		this.onMenu = onMenu;
	}
}
