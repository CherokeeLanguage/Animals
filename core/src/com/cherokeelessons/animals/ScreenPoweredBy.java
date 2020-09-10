package com.cherokeelessons.animals;

import java.util.ArrayList;
import java.util.List;

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
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.common.DisplaySize;
import com.cherokeelessons.common.GameMusic;
import com.cherokeelessons.common.Gamepads;
import com.cherokeelessons.common.Utils;

public class ScreenPoweredBy<E> extends GameScreen {
	
	@Override
	protected boolean useBackdrop() {
		return false;
	}

	private final Array<Image> logo = new Array<>();

	private PixmapPacker pack;
	private final ControllerAdapter skipScreen = new ControllerAdapter() {
		@Override
		public boolean buttonDown(final Controller controller, final int buttonCode) {
			game.gameEvent(GameEvent.EXIT_SCREEN);
			return true;
		}
	};
	// private boolean fadeoutStarted=false;
	long start = 0;
	private TextureAtlas ta;

	private final Runnable logoDone = new Runnable() {
		@Override
		public void run() {
			game.gameEvent(GameEvent.EXIT_SCREEN);
			audio.stop();
		}
	};

	private GameMusic audio;

	public ScreenPoweredBy(final CherokeeAnimals game) {
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
		final int packSize = Utils.getPackSize();
		audio = new GameMusic(Gdx.audio.newMusic(Gdx.files.internal("libgdx/atmoseerie03.mp3")));
		audio.setVolume(0f);
		o_pad = 0;
		pack = new PixmapPacker(packSize, packSize, Format.RGBA8888, 2, true);
		for (int i = 0; i < 25; i++) {
			pack.pack(i + "", new Pixmap(Gdx.files.internal("libgdx/1080p_" + i + ".png")));
		}
		ta = pack.generateTextureAtlas(TextureFilter.Linear, TextureFilter.Linear, false);

		int width = 0;
		int height = 0;
		for (int x = 0; x < 5; x++) {
			height = 0;
			Image i = null;
			for (int y = 0; y < 5; y++) {
				final int z = 4 - y;
				final int p = z * 5 + x;
				final AtlasRegion region = ta.findRegion(p + "");
				i = new Image(region);
				// i = new Sprite(ta.findRegion(p + ""));
				i.setPosition(width, height);
				height += i.getHeight();// region.getRegionHeight();
				i.setOrigin(0, 0);
				logo.add(i);
				gameStage.addActor(i);
			}
			if (i != null) {
				width += i.getWidth();// region.getRegionWidth();
			}
		}

		final Rectangle logobox = new Rectangle(0, 0, width, height);

		final Rectangle stageSize = DisplaySize._1080p.size();
		final Rectangle wantedLogoSize = DisplaySize._1080p.overscansize();
		logobox.fitInside(wantedLogoSize);
		float scaleXY = logobox.height / height;
		if (scaleXY > logobox.width / width) {
			scaleXY = logobox.width / width;
		}

		final float offsetX = (stageSize.width - logobox.width) / 2;
		final float offsetY = (stageSize.height - logobox.height) / 2;

		log("Logo scaleXY: " + scaleXY);
		log("Logo offsetXY: " + offsetX + "x" + offsetY);

		clearColor.set(Color.BLACK);
		start = System.currentTimeMillis();

		final Vector2 center = new Vector2();
		logobox.getCenter(center);
		for (int ix = 0; ix < logo.size; ix++) {
			final Image lImagePart = logo.get(ix);
			lImagePart.setScale(scaleXY);
			lImagePart.setX(lImagePart.getX() * scaleXY + offsetX);
			lImagePart.setY(lImagePart.getY() * scaleXY + offsetY);
			lImagePart.setColor(1f, 1f, 1f, 0f);

			List<Action> actions = new ArrayList<Action>();
			actions.add(Actions.delay(1f));
			actions.add(Actions.alpha(1f, 4f));
			actions.add(Actions.delay(4f));
			actions.add(Actions.alpha(0f, 2f));
			actions.add(Actions.delay(1f));
			SequenceAction sequence = Actions.sequence(actions.toArray(new Action[0]));
			lImagePart.addAction(sequence);
			
			if (ix == 0) {
				Actor audioActor = new Actor();
				
				List<Action> audioActions = new ArrayList<Action>();
				audioActions.add(Actions.delay(1f));
				audioActions.add(new AudioVolumeAction(audio, 1f, 4f));
				audioActions.add(Actions.delay(4f));
				audioActions.add(new AudioVolumeAction(audio, 0f, 2f));
				audioActions.add(Actions.delay(1f));
				audioActions.add(Actions.run(logoDone));
				SequenceAction audioSequence = Actions.sequence(audioActions.toArray(new Action[0]));
				audioActor.addAction(audioSequence);
				
				gameStage.addActor(audioActor);
			}
		}

		audio.play();
	}
	
	public static class AudioVolumeAction extends TemporalAction {
		private final GameMusic music;
		private final float endVolume;
		private float startVolume;
		public AudioVolumeAction(GameMusic music, float volume, float duration) {
			super(duration);
			this.music=music;
			this.endVolume = volume;
		}
		@Override
		protected void begin() {
			super.begin();
			startVolume = music.getVolume();
		}
		@Override
		protected void end() {
			super.end();
			music.setVolume(endVolume);
		}
		@Override
		protected void update(float percent) {
			float volume = (endVolume-startVolume)*percent+startVolume;
			music.setVolume(volume);
		}
	}

	@Override
	public void render(final float delta) {
		super.render(delta);
	}

	@Override
	public void show() {
		super.show();
		init();
		Gamepads.addListener(skipScreen);
	}

	@Override
	public boolean dpad(int keyCode) {
		//ignore
		return false;
	}
}
