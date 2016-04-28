package com.cherokeelessons.vocab.animals.one;

import java.nio.IntBuffer;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.cherokeelessons.common.GameMusic;
import com.cherokeelessons.common.Gamepads;
import com.cherokeelessons.common.MusicAccessor;
import com.cherokeelessons.common.SpriteAccessor;
import com.cherokeelessons.common.Utils;
import com.cherokeelessons.vocab.animals.one.enums.GameEvent;

public class ScreenPoweredBy extends GameScreen {

	private final Array<Sprite> logo = new Array<Sprite>();
	
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
		IntBuffer buf = BufferUtils.newIntBuffer(16);
		Gdx.gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, buf);
		int packSize=Utils.getPackSize();
		audio = new GameMusic(Gdx.audio.newMusic(Gdx.files.internal("libgdx/atmoseerie03.ogg")));
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
			Sprite i = null;
			for (int y = 0; y < 5; y++) {
				int z = 4 - y;
				int p = z * 5 + x;
				i = new Sprite(ta.findRegion(p + ""));
				i.setPosition(width, height);
				height += i.getRegionHeight();				
				i.setOrigin(0, 0);
				logo.add(i);
			}
			width += i.getWidth();
		}
		
		Rectangle logobox = new Rectangle(0, 0, width, height);		
		logobox.fitInside(screenSize);
		float scaleXY=logobox.height/height;
		if (scaleXY>logobox.width/width) scaleXY=logobox.width/width;
		
		float offsetX = screenSize.x+(screenSize.width-logobox.width)/2;
		float offsetY = screenSize.y+(screenSize.height-logobox.height)/2;
		
		clearColor.set(Color.BLACK);
		start = System.currentTimeMillis();

		Vector2 center=new Vector2();
		logobox.getCenter(center);
		for(int ix=0; ix<logo.size; ix++) {
			Sprite s=logo.get(ix);
			Timeline tl = Timeline.createSequence();
			s.setScale(scaleXY);
			s.setX(s.getX()*scaleXY+offsetX);
			s.setY(s.getY()*scaleXY+offsetY);
			s.setColor(1f, 1f, 1f, 0f);
			
			tl.pushPause(1f);
			tl.push(Tween.to(s, SpriteAccessor.Alpha, 4f).target(1f));
			tl.pushPause(4f);
			tl.push(Tween.to(s, SpriteAccessor.Alpha, 2f).target(0f));
			tl.pushPause(1f);
			tl.start(tmanager);
			
			if (ix==0) {				
				Timeline al = Timeline.createSequence();
				al.pushPause(1f);
				al.push(Tween.to(audio, MusicAccessor.Volume, 4f).target(1f));
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
		clearScreen();
		drawOverscan();

		batch.begin();
		for (Sprite s: logo) {
			s.draw(batch);
		}
		batch.end();
		
	}

	@Override
	public void show() {
		super.show();
		init();
		Gamepads.addListener(skipScreen);
	}
}
