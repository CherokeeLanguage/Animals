package com.cherokeelessons.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.common.DisplaySize;
import com.cherokeelessons.common.GameMusic;
import com.cherokeelessons.common.Gamepads;
import com.cherokeelessons.common.ImageAccessor;
import com.cherokeelessons.common.MusicAccessor;
import com.cherokeelessons.common.Utils;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

public class ScreenPoweredBy extends GameScreen {

	private final Array<Image> logo = new Array<>();
	
	private PixmapPacker pack;
	private ControllerAdapter skipScreen = new ControllerAdapter() {
		@Override
		public boolean buttonDown(Controller controller, int buttonCode) {
			game.gameEvent(GameEvent.Done);
			return true;
		}
	};
	// private boolean fadeoutStarted=false;
	long start = 0;
	private TextureAtlas ta;

	private TweenCallback logoDone=new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			if (type!=TweenCallback.COMPLETE) {
				return;
			}
			game.gameEvent(GameEvent.Done);
			audio.stop();
		}
	};

	private GameMusic audio;

	public ScreenPoweredBy(CherokeeAnimals game) {
		super(game);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void hide() {
		super.hide();
		Gamepads.clearListeners();
		ta.dispose();
		audio.stop();
		audio.dispose();
	}

	private void init() {
		int packSize=Utils.getPackSize();
		audio = new GameMusic(Gdx.audio.newMusic(Gdx.files.internal("libgdx/atmoseerie03.mp3")));
		audio.setVolume(0f);
		o_pad=0;
		pack = new PixmapPacker(packSize, packSize, Format.RGBA8888, 2, true);
		for (int i = 0; i < 25; i++) {
			pack.pack(
					i + "",
					new Pixmap(Gdx.files.internal("libgdx/1080p_" + i + ".png")));
		}
		ta = pack.generateTextureAtlas(TextureFilter.Linear,
				TextureFilter.Linear, false);

		int width = 0;
		int height = 0;
		for (int x = 0; x < 5; x++) {
			height = 0;
			Image i = null;
			for (int y = 0; y < 5; y++) {
				int z = 4 - y;
				int p = z * 5 + x;
				final AtlasRegion region = ta.findRegion(p + "");
				i = new Image(region);
				//i = new Sprite(ta.findRegion(p + ""));
				i.setPosition(width, height);
				height += i.getHeight();// region.getRegionHeight();				
				i.setOrigin(0, 0);
				logo.add(i);
				gameStage.addActor(i);
			}
			if (i!=null) {
				width += i.getWidth();// region.getRegionWidth();
			}
		}
		
		Rectangle logobox = new Rectangle(0, 0, width, height);		
		
		Rectangle stageSize=DisplaySize._1080p.size();
		Rectangle wantedLogoSize=DisplaySize._1080p.overscansize();
		logobox.fitInside(wantedLogoSize);
		float scaleXY=logobox.height/height;
		if (scaleXY>logobox.width/width) scaleXY=logobox.width/width;
		
		float offsetX = (stageSize.width-logobox.width)/2;
		float offsetY = (stageSize.height-logobox.height)/2;
		
		log("Logo scaleXY: "+scaleXY);
		log("Logo offsetXY: "+offsetX+"x"+offsetY);
		
		clearColor.set(Color.BLACK);
		start = System.currentTimeMillis();

		Vector2 center=new Vector2();
		logobox.getCenter(center);
		for(int ix=0; ix<logo.size; ix++) {
			Image lImagePart=logo.get(ix);
			Timeline tl = Timeline.createSequence();
			lImagePart.setScale(scaleXY);
			lImagePart.setX(lImagePart.getX()*scaleXY+offsetX);
			lImagePart.setY(lImagePart.getY()*scaleXY+offsetY);
			lImagePart.setColor(1f, 1f, 1f, 0f);
			
			tl.pushPause(1f);
			tl.push(Tween.to(lImagePart, ImageAccessor.Alpha, 4f).target(1f));
			tl.pushPause(4f);
			tl.push(Tween.to(lImagePart, ImageAccessor.Alpha, 2f).target(0f));
			tl.pushPause(1f);
			tl.start(tmanager);
			
			if (ix==0) {				
				Timeline al = Timeline.createSequence();
				al.pushPause(1f);
				al.push(Tween.to(audio, MusicAccessor.Volume, 4f).target(.7f));
				al.pushPause(4f);
				al.push(Tween.to(audio, MusicAccessor.Volume, 2f).target(0f));
				al.setCallback(logoDone);
				al.pushPause(1f);
				al.start(tmanager);
			}
		}	
		
		
		audio.play();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
	}

	@Override
	public void show() {
		super.show();
		init();
		Gamepads.addListener(skipScreen);
	}
}
