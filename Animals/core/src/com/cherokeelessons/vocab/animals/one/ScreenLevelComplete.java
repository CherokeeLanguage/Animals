package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.vocab.animals.one.CherokeeAnimals.FontStyle;

public class ScreenLevelComplete extends ScreenGameCore {

	private static final String CHOOSE_NEXT_LEVEL = "[O] - Choose Another Level";
	private static final String COMPLETE = "Complete!";
	private static final String CORRECT = "% Correct!";
	private static final String LEVEL = "Level";
	private static final String PLAY_LEVEL_AGAIN = "[U] - Play Level Again";
	private static final String RETURN_TO_MAIN = "[A] - Return to Main";

	private BitmapFont font;

	private int fontSize = 96;
	private Label goLevelSelect;
	private Label gotoMainMenu;
	private LabelStyle lStyle;
	private Label message;
	public int optionsButton;
	private Label percentMessage;

	private Label playLevelAgain;

	private LabelStyle tbStyle;

	final Array<Sprite> wall = new Array<Sprite>();

	private TextureAtlas wall_atlas;

	public ScreenLevelComplete(CherokeeAnimals game) {
		super(game);

		font = CherokeeAnimals.getFont(CherokeeAnimals.FontStyle.Script,fontSize);

		lStyle = new LabelStyle(font, GameColor.GREEN);
		message = new Label(LEVEL + " " + game.getLevelOn() + " " + COMPLETE,
				lStyle);

		percentMessage = new Label(game.getLevelAccuracy(game.getLevelOn() - 1)
				+ CORRECT, lStyle);

		tbStyle = new LabelStyle();
		tbStyle.font = font;
		tbStyle.fontColor = GameColor.GREEN;

		playLevelAgain = new Label(PLAY_LEVEL_AGAIN, tbStyle);
		playLevelAgain.pack();

		goLevelSelect = new Label(CHOOSE_NEXT_LEVEL, tbStyle);
		goLevelSelect.pack();

		gotoMainMenu = new Label(RETURN_TO_MAIN, tbStyle);
		gotoMainMenu.pack();

		gameStage.addActor(message);
		gameStage.addActor(percentMessage);
		gameStage.addActor(gotoMainMenu);
		gameStage.addActor(playLevelAgain);
		gameStage.addActor(goLevelSelect);
		gameStage.getRoot().setX(overscan.x);
		gameStage.getRoot().setY(overscan.y);
	}

	@Override
	public void hide() {
		wall.clear();
		wall_atlas.dispose();
		super.hide();
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

	private void positionItems() {
		final int linesDisplayed = 5;
		int lineGap;
		int midX;
		int line;

		lineGap = (int) (overscan.height / linesDisplayed);
		line = lineGap / 2;
		midX = (int) (overscan.width / 2);

		updateDisplay();

		gotoMainMenu.setX(midX - gotoMainMenu.getWidth() / 2);
		gotoMainMenu.setY(line - gotoMainMenu.getHeight() / 2);
		line += lineGap;
		goLevelSelect.setX(midX - goLevelSelect.getWidth() / 2);
		goLevelSelect.setY(line - goLevelSelect.getHeight() / 2);
		line += lineGap;
		playLevelAgain.setX(midX - playLevelAgain.getWidth() / 2);
		playLevelAgain.setY(line - playLevelAgain.getHeight() / 2);
		line += lineGap;
		percentMessage.setX(midX - percentMessage.getWidth() / 2);
		percentMessage.setY(line - percentMessage.getHeight() / 2);
		line += lineGap;
		message.setX(midX - message.getWidth() / 2);
		message.setY(line - message.getHeight() / 2);
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
		gameStage.draw();
	}

	@Override
	public void show() {
		super.show();
		game.saveLevelAccuracies();
		game.getOptions().putInteger("highScore", game.getHighScore());
		game.getOptions().flush();
		positionItems();
		initBackdrop();
	}

	private void updateDisplay() {
		message.setText(LEVEL + " " + game.getLevelOn() + " " + COMPLETE);
		message.pack();
		percentMessage.setText(game.getLevelAccuracy(game.getLevelOn() - 1)
				+ CORRECT);
		percentMessage.pack();
	}
}
