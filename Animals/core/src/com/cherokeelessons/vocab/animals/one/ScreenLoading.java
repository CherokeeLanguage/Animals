package com.cherokeelessons.vocab.animals.one;

import java.nio.IntBuffer;
import java.security.PublicKey;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
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
import com.badlogic.gdx.utils.BufferUtils;
import com.cherokeelessons.common.FontGenerator;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.ModMusicPlayer;
import com.cherokeelessons.common.Utils;
import com.cherokeelessons.vocab.animals.one.enums.GameEvent;


public class ScreenLoading extends GameScreen {

	private static final int DesiredLevels = 18;
	private String i_am_thinking = "Mi estas pensanta ...";
	private TextureAtlas ta;
	private Label loading; 
	
	public ScreenLoading(CherokeeAnimals game) {
		super(game);
		clearColor.set(Color.BLACK);	
	}
	
	@Override
	public void show() {
		super.show();
		ta=pack_9patches();
		AtlasRegion patch_texture = ta.findRegion("Blocks_01_64x64_Alt_04_003");
		NinePatch patch = new NinePatch(patch_texture, 15, 15, 15, 15);
		NinePatchDrawable patch_draw = new NinePatchDrawable(patch);
		BitmapFont bf=game.fg.gen(144);		
		LabelStyle style=new LabelStyle();
		style.fontColor=GameColor.DARKGREEN;
		style.font=bf;
		style.background=patch_draw;
		loading=new Label(i_am_thinking, style);
		loading.pack();
		loading.getColor().a=0f;
		loading.addAction(Actions.alpha(1f, .25f));
		float y;
		float x;
		x=(fullscan.width-loading.getWidth())/2;
		y=(fullscan.height-loading.getHeight())/2;
		loading.setPosition(x, y);		
	}

	protected TextureAtlas pack_9patches() {
		IntBuffer buf = BufferUtils.newIntBuffer(16);
		Gdx.gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, buf);
		int packSize=Utils.getPackSize();
		PixmapPacker packer = new PixmapPacker(packSize, packSize, Format.RGBA8888, 2, true);
		String t1 = Gdx.files.internal("9patch/00-plist.txt").readString("UTF-8");
		String[] t2 = t1.split("\n");
		for (int i=0; i<t2.length; i++) {
			String t3 = t2[i];
			t3=t3.trim();
			if (t3.length()==0) {
				continue;
			}
			packer.pack(t3.replace(".png", ""), new Pixmap(Gdx.files.internal("9patch/"+t3)));
		}
		return packer.generateTextureAtlas(TextureFilter.Linear, TextureFilter.Linear, false);
	}
	
	@Override
	public void hide() {
		ta.dispose();
		super.hide();
	}
	
	boolean gotPublicKey=false;
	boolean done=false;
	boolean challengesLoaded=false;
	boolean fontsCleared=false;
	boolean levelSet=false;
	boolean fontsPrecalc=false;
	private boolean backgroundMusicStarted=false;
	@Override
	public void render(float delta) {
		super.render(delta);
		batch.begin();
		loading.act(delta);
		loading.draw(batch, 1f);
		batch.end();
		if (!backgroundMusicStarted) {
			backgroundMusicStarted=true;
			game.musicPlayer = new ModMusicPlayer();
			game.musicPlayer.loadUsingPlist();
			game.musicPlayer.play((float)game.prefs.getMasterVolume()*(float)game.prefs.getMusicVolume()/10000f);
			return;
		}
		if (!game.sm.preloadDone()) {
			return;
		}
		if (!gotPublicKey) {
			gotPublicKey = true;
			PublicKey pk = game.iap.getPublicKey();
			if (pk != null) {
				Gdx.app.log("PublicKey Format: ", pk.getFormat());
				Gdx.app.log("PublicKey Algorithm: ", pk.getAlgorithm());
			}
			return;
		}
		if (!challengesLoaded) {
			challengesLoaded=true;
			game.challenges = new LoadChallenges();
			try {
				game.challenges.setTestmode(game.prefs.getBoolean("testmode", false));
			} catch (Exception e) {
				game.challenges.setTestmode(false);
			} finally {
				game.prefs.putBoolean("testmode", game.challenges.isTestmode());
			}
			return;
		}
		if (!levelSet) {
			levelSet=true;
			game.challenges.setLevelCount(DesiredLevels);
			game.setLevels(game.challenges.levelcount());
			return;
		}
		if (!fontsPrecalc) {
			fontsPrecalc=true;
			int[] fixed = {18,48};
			int[] regular = {128,18,96,44,48,72,42,76,50};
			FontGenerator fg = new FontGenerator();
			for (int ix=0; ix<fixed.length; ix++) {
				fg.genFixedNumbers(fixed[ix]);
			}
			for (int ix=0; ix<regular.length; ix++) {
				fg.gen(regular[ix]);
			}
			return;
		}
		
		if (!done) {			
			done=true;			
			loading.addAction(Actions.fadeOut(1f));
			loading.addAction(Actions.delay(1.25f, Actions.run(new Runnable() {
				public void run() {
					game.gameEvent(GameEvent.Done);		
				}
			})));
			
		}
	}
}