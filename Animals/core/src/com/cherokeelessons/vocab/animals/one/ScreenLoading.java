package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class ScreenLoading extends ScreenGameCore {

	private Attributions creditScroller;

	private Attributions shadow;

	private Texture splash = null;
	private Image splashImage = null;

	private TextureRegion splashRegion = null;
	final Array<Sprite> wall = new Array<Sprite>();
	private TextureAtlas wall_atlas;

	private boolean wasPlaying;

	public ScreenLoading(CherokeeAnimals game) {
		super(game);		
	}

	private void initScreen() {
		float scaleX, scaleY;

		System.out.println("ScreenLoading");
		splash = new Texture("info_thinking.png");
		splash.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		scaleY = (float) overscan.height / (float) splash.getHeight();
		scaleX = (float) overscan.width / (float) splash.getWidth();
		System.out.println("SCALEX/Y:" + scaleX + "/" + scaleY);
		if (scaleY > scaleX) {
			scaleY = scaleX;
		} else {
			scaleX = scaleY;
		}
		System.out.println("Splash Scale: " + scaleX);
		splashRegion = new TextureRegion(splash);
		splashImage = new Image(splashRegion);
		splashImage.setScaleX(scaleX);
		splashImage.setScaleY(scaleY);
		splashImage.setX((overscan.width - scaleX * splashImage.getWidth())
				* 0.5f + overscan.x);
		splashImage.setY((overscan.height - scaleY * splashImage.getHeight())
				* 0.5f + overscan.y);

		splashImage.pack();

		gameStage.clear();

		shadow = new Attributions(overscan);
		shadow.setFontColor(Color.BLACK);
		shadow.getColor().a = .9f;
		shadow.setxOffset(4);
		shadow.setyOffset(-4);

		creditScroller = new Attributions(overscan);
		creditScroller.setFontColor(GameColor.GREEN);
		
		creditScroller.setTouchable(Touchable.enabled);
		creditScroller.addListener(new EventListener() {
			@Override
			public boolean handle(Event event) {
				game.startupMusic.stop();
				return true;
			}
		});

		initBackdrop();

		gameStage.addActor(splashImage);
		gameStage.addActor(shadow);
		gameStage.addActor(creditScroller);
	}

	@Override
	public void dispose() {		
		game.startupMusic.dispose();
		game.startupMusic = null;
		super.dispose();
	}

	private void discardResources() {
		wall_atlas.dispose();
		wall_atlas = null;
		splash.dispose();
		splash = null;
		creditScroller.clear();
		creditScroller = null;
		shadow.clear();
		shadow = null;
	}

	public void doScroll(float time) {
		splashImage.addAction(Actions.fadeOut(1));
		creditScroller.init();
		creditScroller.scroll(time);
		shadow.init();
		shadow.scroll(time);
	}

	@Override
	public void hide() {
		super.hide();
		if (game.startupMusic != null) {
			wasPlaying = game.startupMusic.isPlaying();
			System.out.println("PAUSING MUSIC (hide)");
			game.startupMusic.pause();
		}
		discardResources();
	}

	private void initBackdrop() {
		wall.clear();
		PixmapPacker pack = new PixmapPacker(1024, 1024, Format.RGBA8888, 2,
				true);
		for (int i = 0; i < 32; i++) {
			pack.pack(
					i + "",
					new Pixmap(Gdx.files.internal("images/backdrops/p_" + i
							+ "_dsci2549.png")));
		}
		wall_atlas = pack.generateTextureAtlas(TextureFilter.Linear,
				TextureFilter.Linear, false);

		int px = 0;
		int py = 0;
		final int perRow = 8;
		final int columns = 4;
		for (int x = 0; x < perRow; x++) {
			py = 0;
			Sprite i = null;
			for (int y = 0; y < columns; y++) {
				int z = columns - (y + 1);
				int p = z * perRow + x;
				final AtlasRegion piece = wall_atlas.findRegion(p + "");
				i = new Sprite(piece, 0, 0, piece.getRegionWidth(),
						piece.getRegionHeight());
				i.setX(px);
				i.setY(py);
				i.setColor(1f, 1f, 1f, 0.35f);
				py += i.getHeight();
				wall.add(i);
			}
			px += i.getWidth();
		}
	}

	@Override
	public void render(float delta) {
		clearScreen();
		if (showOverScan) {
			drawOverscan();
		}
		batch.begin();
		for (Sprite s : wall) {
			s.draw(batch);
		}
		batch.end();
		gameStage.act();
		gameStage.draw();
	}

	@Override
	public void show() {
		super.show();
		initScreen();
		if (wasPlaying) {
			System.out.println("RESUMING MUSIC (show)");
			game.startupMusic.play();
		}
	}

}
