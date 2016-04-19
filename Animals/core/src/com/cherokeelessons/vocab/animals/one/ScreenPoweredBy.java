package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.cherokeelessons.vocab.animals.one.GameEvent.EventList;

public class ScreenPoweredBy extends ScreenGameCore {

	CherokeeAnimals app;

	private AlphaAction fadeIn;

	private AlphaAction fadeOut;

	Group logo = new Group();
	private Music music;
	private PixmapPacker pack;
	// private boolean fadeoutStarted=false;
	long start = 0;
	private TextureAtlas ta;

	public ScreenPoweredBy(CherokeeAnimals game) {
		super(game);
		app = game;
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void hide() {
		super.hide();
		musicStop();
		gameStage.clear();
		hud.clear();
		backDrop.clear();
		ta.dispose();
	}

	private void init() {
		pack = new PixmapPacker(512, 512, Format.RGBA8888, 2, true);
		for (int i = 0; i < 25; i++) {
			pack.pack(
					i + "",
					new Pixmap(Gdx.files.internal("libgdx/1080p_" + i + ".png")));
		}
		ta = pack.generateTextureAtlas(TextureFilter.Linear,
				TextureFilter.Linear, false);

		int px = 0;
		int py = 0;
		for (int x = 0; x < 5; x++) {
			py = 0;
			Image i = null;
			for (int y = 0; y < 5; y++) {
				int z = 4 - y;
				int p = z * 5 + x;
				i = new Image(ta.findRegion(p + ""));
				i.setX(px);
				i.setY(py);
				py += i.getHeight();
				logo.addActor(i);
			}
			px += i.getWidth();
		}
		logo.setSize(px, py);

		this.backgroundColor = Color.BLACK;
		start = System.currentTimeMillis();

		logo.setOrigin(logo.getWidth() / 2, logo.getHeight() / 2);

		float wscale = overscan.width / logo.getWidth();
		float hscale = overscan.height / logo.getHeight();
		if (wscale > hscale) {
			logo.setScale(hscale);
		} else {
			logo.setScale(wscale);
		}
		logo.setX(overscan.x + (overscan.width - logo.getWidth()) / 2);
		logo.setY(overscan.y + (overscan.height - logo.getHeight()) / 2);
		hud.addActor(logo);
		logo.setColor(1, 1, 1, 0);
		SequenceAction sequence = new SequenceAction();
		fadeIn = Actions.fadeIn(4);
		fadeOut = Actions.fadeOut(2);
		sequence.addAction(fadeIn);
		sequence.addAction(Actions.delay(4));
		sequence.addAction(fadeOut);
		sequence.addAction(Actions.run(new Runnable() {
			@Override
			public void run() {
				music.stop();
			}
		}));
		sequence.addAction(Actions.delay(2));
		sequence.addAction(Actions.run(new Runnable() {
			@Override
			public void run() {
				game.event(EventList.FirstRun);
			}
		}));

		logo.addAction(sequence);
	}

	private void musicStart() {
		music = Gdx.audio.newMusic(Gdx.files
				.internal("libgdx/atmoseerie03.ogg"));
		music.setLooping(true);
		music.setVolume(0f);
		music.play();
	}

	private void musicStop() {
		if (music != null) {
			music.stop();
			music.dispose();
			music = null;
		}
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		if (fadeIn.getActor() != null
				&& fadeIn.getTime() < fadeIn.getDuration()) {
			float volume = fadeIn.getTime() / fadeIn.getDuration();
			music.setVolume(volume);
			return;
		}
		if (fadeOut.getActor() != null
				&& fadeOut.getTime() < fadeOut.getDuration()) {
			float volume = (fadeOut.getDuration() - fadeOut.getTime())
					/ fadeOut.getDuration();
			music.setVolume(volume);
			return;
		}
	}

	@Override
	public void show() {
		super.show();
		musicStart();
		init();
	}
}
