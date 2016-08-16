package com.cherokeelessons.animals;

import com.badlogic.gdx.Gdx;
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


public class ScreenLoading extends GameScreen {

	private static final int DesiredLevels = 18;
	private String i_am_thinking = "ᎦᏓᏅᏖᎭ ...";
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
		BitmapFont bf=game.fg.get(128);		
		LabelStyle style=new LabelStyle();
		style.fontColor=GameColor.MAIN_TEXT;
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
//		IntBuffer buf = BufferUtils.newIntBuffer(16);
//		Gdx.gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, buf);
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
	private boolean syllabaryMapInit=false;
	@Override
	public void render(float delta) {
		super.render(delta);
		batch.begin();
		loading.act(delta);
		loading.draw(batch, 1f);
		batch.end();
		if (!backgroundMusicStarted) {
			backgroundMusicStarted=true;
			game.musicPlayer = new MusicPlayer();
			game.musicPlayer.loadUsingPlist();
			game.musicPlayer.play((float)game.prefs.getMasterVolume()*(float)game.prefs.getMusicVolume()/10000f);
			return;
		}
		if (!game.sm.preloadDone()) {
			return;
		}
		if (!syllabaryMapInit) {
			Utils.initTranslationMap();
			syllabaryMapInit=true;
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