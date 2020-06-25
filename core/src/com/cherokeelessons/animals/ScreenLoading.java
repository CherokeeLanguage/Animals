package com.cherokeelessons.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Utils;

public class ScreenLoading extends GameScreen implements DpadInterface {

	@Override
	protected boolean useBackdrop() {
		return false;
	}
	
	private static final int DesiredLevels = 18;
	private final String i_am_thinking = "ᎦᏓᏅᏖᎭ ...";
	private TextureAtlas ta;
	private Label loading;

	boolean gotPublicKey = false;

	boolean done = false;

	boolean challengesLoaded = false;

	boolean fontsCleared = false;

	boolean levelSet = false;
	boolean fontsPrecalc = false;
	private boolean syllabaryMapInit = false;
	private AssetManager am;
	private float elapsed = 0f;
	private final ControllerAdapter ca = new ControllerAdapter() {
		@Override
		public boolean buttonDown(final Controller controller, final int buttonIndex) {
			return true;
		}
	};

	public ScreenLoading(final CherokeeAnimals game) {
		super(game);
		clearColor.set(Color.BLACK);
	}

	@Override
	public boolean dpad(final int keyCode) {
		Gdx.app.log("DPAD: ", keyCode + "");
		return true;
	}

	@Override
	public void hide() {
		for (final Controller c : Controllers.getControllers()) {
			c.removeListener(this.ca);
		}
		am.clear();
		am = null;
		ta.dispose();
		super.hide();
	}

	protected TextureAtlas pack_9patches() {
		Gdx.app.log("Animals", "ScreenLoading");
		// IntBuffer buf = BufferUtils.newIntBuffer(16);
		// Gdx.gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, buf);
		final int packSize = Utils.getPackSize();
		Gdx.app.log("Animals", "Pack size: " + packSize);
		final PixmapPacker packer = new PixmapPacker(packSize, packSize, Format.RGBA8888, 2, true);
		final String t1 = Gdx.files.internal("9patch/00-plist.txt").readString("UTF-8");
		final String[] t2 = t1.split("\n");
		for (final String element : t2) {
			String t3 = element;
			t3 = t3.trim();
			if (t3.length() == 0) {
				continue;
			}
			packer.pack(t3.replace(".png", "") + "X", new Pixmap(Gdx.files.internal("9patch/" + t3)));
		}
		return packer.generateTextureAtlas(TextureFilter.Linear, TextureFilter.Linear, false);
	}

	@Override
	public void render(final float delta) {
		super.render(delta);
		if (!game.sm.preloadDone()) {
			return;
		}
		if (!syllabaryMapInit) {
			Utils.initTranslationMap();
			syllabaryMapInit = true;
			return;
		}
		if (!challengesLoaded) {
			challengesLoaded = true;
			game.challenges = new LoadChallenges();
			try {
				game.challenges.setTestmode(game.prefs.getBoolean("testmode", false));
			} catch (final Exception e) {
				game.challenges.setTestmode(false);
			} finally {
				game.prefs.putBoolean("testmode", game.challenges.isTestmode());
				game.prefs.flush();
			}
			return;
		}
		if (!levelSet) {
			levelSet = true;
			game.challenges.setLevelCount(DesiredLevels);
			game.setLevels(game.challenges.levelcount());
			return;
		}
		if (!done) {
			done = true;
			loading.addAction(Actions.fadeOut(1f));
			loading.addAction(Actions.delay(1.25f, Actions.run(new Runnable() {
				@Override
				public void run() {
					game.gameEvent(GameEvent.Done);
				}
			})));

		}
		elapsed += delta;
	}

	@Override
	public void show() {
		for (final Controller c : Controllers.getControllers()) {
			c.addListener(this.ca);
		}
		ta = pack_9patches();
		for (final AtlasRegion region : ta.getRegions()) {
			Gdx.app.log("Animals", "9pack region: " + region.name);
		}
		final AtlasRegion patch_texture = ta.findRegion("Blocks_01_64x64_Alt_04_003X");
		final NinePatch patch = new NinePatch(patch_texture, 15, 15, 15, 15);
		final NinePatchDrawable patch_draw = new NinePatchDrawable(patch);
		final BitmapFont bf = game.fg.get(128);
		final LabelStyle style = new LabelStyle();
		style.fontColor = GameColor.MAIN_TEXT;
		style.font = bf;
		style.background = patch_draw;
		loading = new Label(i_am_thinking, style);
		loading.pack();
		loading.getColor().a = 0f;
		loading.addAction(Actions.alpha(1f, .25f));
		float y;
		float x;
		x = (fullZoneBox.width - loading.getWidth()) / 2;
		y = (fullZoneBox.height - loading.getHeight()) / 2;
		loading.setPosition(x, y);
		gameStage.addActor(loading);

		if (am != null) {
			am.clear();
		}
		am = new AssetManager();
		am.finishLoading();
		super.show();
	}
}
