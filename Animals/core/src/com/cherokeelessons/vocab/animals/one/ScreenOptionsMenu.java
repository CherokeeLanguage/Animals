package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.vocab.animals.one.CherokeeAnimals.ChallengeWordMode;
import com.cherokeelessons.vocab.animals.one.CherokeeAnimals.FontStyle;
import com.cherokeelessons.vocab.animals.one.CherokeeAnimals.SoundEffectVolume;
import com.cherokeelessons.vocab.animals.one.CherokeeAnimals.TrainingScreenMode;
import com.cherokeelessons.vocab.animals.one.GameEvent.EventList;

/**
 * Orientation (Landscape, Portrait) Master Game Volume (0% - 100%) Sound Word
 * Mode: (On, Off) + Written Word Mode: (Latin, Syllabary, None) Sound Effects
 * Mode: (On, Off) Training Screen: (On, Off) Reset Statistics About Program
 * (Software Used, Fonts Used, etc)
 */

public class ScreenOptionsMenu extends ScreenGameCore {

	private static class MenuLabel extends Label {

		private Runnable menu_action_east = null;
		private Runnable menu_action_west = null;

		public MenuLabel(CharSequence text, LabelStyle style) {
			super(text, style);
		}

	}

	private static int idx_volume = 0;
	private static final String INSTRUCT_STANDARD = "DPAD up/down select option, [O] Change value, [A] Back";

	private static final String INSTRUCT_VOLUME = "DPAD up/down select option, DPAD left/right change value, [A] Back";
	private float[] baseLines;

	private int baseLines_cnt;
	private MenuLabel btn_challengeSoundMode;

	private MenuLabel btn_challengeWordMode;

	private MenuLabel btn_resetStatistics;

	private MenuLabel btn_soundEffects;

	private MenuLabel btn_trainingScreen;
	private MenuLabel btn_volumeLabel;

	private Array<MenuLabel> btns = new Array<MenuLabel>();
	private LabelStyle buttonStyle;
	private BitmapFont font = null;
	private Texture indicator;

	private LabelStyle instructStyle;

	private MenuLabel lbl_instructions;

	private Sprite left_indicator = new Sprite();

	private float lineHeight = 0;
	private float offset = 0;
	private Integer optionItemSize = 76;

	public int optionsButton;

	private Sprite right_indicator = new Sprite();

	private int selected_btn = 0;

	private Color textColor;

	final Array<Sprite> wall = new Array<Sprite>();

	private TextureAtlas wall_atlas;

	public ScreenOptionsMenu(final CherokeeAnimals game) {
		super(game);

		baseLines_cnt = 6;
		baseLines = new float[baseLines_cnt];

		int displayLine;

		font = CherokeeAnimals.getFixedFont(CherokeeAnimals.FontStyle.Script,optionItemSize);
		BitmapFont ifont = CherokeeAnimals.getFont(CherokeeAnimals.FontStyle.Script,(optionItemSize * 2) / 3);

		textColor = GameColor.GREEN;

		buttonStyle = new LabelStyle();
		buttonStyle.font = font;
		buttonStyle.fontColor = textColor;

		instructStyle = new LabelStyle();
		instructStyle.font = ifont;
		instructStyle.fontColor = textColor;

		displayLine = 0;

		lbl_instructions = new MenuLabel(INSTRUCT_STANDARD, instructStyle);
		lbl_instructions.pack();
		lbl_instructions.setX((screenWidth - lbl_instructions.getWidth()) / 2);
		lbl_instructions.setY(overscan.y);

		float optionsHeight = lbl_instructions.getHeight();
		calculateBaseLines(optionsHeight);

		// start off with 100% volume level for width calculations
		btn_volumeLabel = new MenuLabel(getVolumeLabel(100), buttonStyle);
		btn_volumeLabel.setTouchable(Touchable.enabled);
		btn_volumeLabel.pack();
		btn_volumeLabel.setX((screenWidth - btn_volumeLabel.getWidth()) / 2);
		btn_volumeLabel.setY(getBaseLine(displayLine++));
		btn_volumeLabel.setText(getVolumeLabel(game.getVolume()));
		btn_volumeLabel.menu_action_east = new Runnable() {
			@Override
			public void run() {
				int volume = game.getVolume();
				if (volume < 100) {
					volume = (volume / 5 + 1) * 5;
					game.setVolume(volume);
					btn_volumeLabel.setText(getVolumeLabel(volume));
					game.enforceSoundSettings();
					game.getSoundManager().playEffect("menu-click");
					highlight_button();
				}
			}
		};
		btn_volumeLabel.menu_action_west = new Runnable() {
			@Override
			public void run() {
				int volume = game.getVolume();
				if (volume > 0) {
					volume = (volume / 5 - 1) * 5;
					game.setVolume(volume);
					btn_volumeLabel.setText(getVolumeLabel(volume));
					game.enforceSoundSettings();
					game.getSoundManager().playEffect("menu-click");
					highlight_button();
				}
			}
		};

		btn_challengeSoundMode = new MenuLabel("", buttonStyle);
		btn_challengeSoundMode.setY(getBaseLine(displayLine++));
		btn_challengeSoundMode.setTouchable(Touchable.enabled);
		btn_challengeSoundMode.menu_action_east = new Runnable() {
			@Override
			public void run() {
				game.setChallengeAudio(!game.isChallengeAudio());
				updateChallengeModeDisplay();
				game.enforceSoundSettings();
				game.getSoundManager().playEffect("menu-click");
			}
		};
		btn_challengeSoundMode.menu_action_west = btn_challengeSoundMode.menu_action_east;

		btn_challengeWordMode = new MenuLabel("", buttonStyle);
		btn_challengeWordMode.setY(getBaseLine(displayLine++));
		btn_challengeWordMode.menu_action_east = new Runnable() {
			@Override
			public void run() {
				wordMode();
			}
		};
		btn_challengeWordMode.menu_action_west = btn_challengeWordMode.menu_action_east;
		updateChallengeModeDisplay();

		btn_soundEffects = new MenuLabel("", buttonStyle);
		btn_soundEffects.setY(getBaseLine(displayLine++));
		btn_soundEffects.menu_action_east = new Runnable() {
			@Override
			public void run() {
				soundEffects();
			}
		};
		btn_soundEffects.menu_action_west = btn_soundEffects.menu_action_east;
		updateSoundEffectsDisplay();

		btn_trainingScreen = new MenuLabel("", buttonStyle);
		btn_trainingScreen.setY(getBaseLine(displayLine++));
		btn_trainingScreen.menu_action_east = new Runnable() {
			@Override
			public void run() {
				trainingScreen();
			}
		};
		btn_trainingScreen.menu_action_west = btn_trainingScreen.menu_action_east;
		updateTrainingScreenDisplay();

		btn_resetStatistics = new MenuLabel("", buttonStyle);
		btn_resetStatistics.setY(getBaseLine(displayLine++));
		btn_resetStatistics.menu_action_east = new Runnable() {
			@Override
			public void run() {
				resetStatistics();
			}
		};
		btn_resetStatistics.menu_action_west = btn_resetStatistics.menu_action_east;
		update_btn_resetStatistics();

		btns.add(btn_resetStatistics);
		btns.add(btn_trainingScreen);
		btns.add(btn_soundEffects);
		btns.add(btn_challengeWordMode);
		btns.add(btn_challengeSoundMode);
		idx_volume = btns.size;
		btns.add(btn_volumeLabel);

		gameStage.clear();
		// gameStage.addActor(btn_aboutProgram);
		gameStage.addActor(btn_resetStatistics);
		gameStage.addActor(btn_trainingScreen);
		gameStage.addActor(btn_soundEffects);
		gameStage.addActor(btn_challengeWordMode);
		gameStage.addActor(btn_challengeSoundMode);
		// gameStage.addActor(btn_orient);
		gameStage.addActor(btn_volumeLabel);
		gameStage.addActor(lbl_instructions);
	}

	private void calculateBaseLines(float bottomMargin) {
		int ix;
		lineHeight = (overscan.height - bottomMargin) / baseLines.length;
		for (ix = 0; ix < baseLines.length; ix++) {
			baseLines[ix] = (float) Math.ceil(bottomMargin + overscan.y + ix
					* lineHeight + offset + (lineHeight - font.getLineHeight())
					/ 2);
		}
	}

	// private void update_btn_aboutProgram(){
	// btn_aboutProgram.setText("About Program");
	// btn_aboutProgram.pack();
	// btn_aboutProgram.x=(screenWidth-btn_aboutProgram.width)/2;
	// }

	private float getBaseLine(int ix) {
		if (ix < 0 || ix >= baseLines.length) {
			return 0f;
		}
		return baseLines[ix];
	}

	public int getSelected_btn() {
		return selected_btn;
	}

	private String getVolumeLabel(int newVolume) {
		String newText = "Volume: ";
		if (newVolume < 100) {
			newText += " ";
		}
		if (newVolume < 10) {
			newText += " ";
		}
		newText = newText + ((Integer) newVolume).toString() + "% ";
		return newText;
	}

	public void goBack() {
		game.enforceSoundSettings();
		game.getSoundManager().playEffect("menu-click");
		game.event(EventList.GoBack);
	}

	@Override
	public void hide() {
		wall_atlas.dispose();
		wall_atlas=null;
		indicator.dispose();
		indicator=null;
		super.hide();
	}

	private void highlight_button() {
		game.getSoundManager().playEffect("box_moved");

		MenuLabel label = btns.get(selected_btn);
		float left = label.getX();
		float bottom = label.getY();
		float right = label.getX() + label.getWidth();

		left_indicator.setPosition(left - left_indicator.getWidth() - 20,
				bottom);
		right_indicator.setPosition(right + 20, bottom);
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

	public void nextMenuItem() {
		selected_btn++;
		if (selected_btn >= btns.size) {
			selected_btn = 0;
		}
		highlight_button();
		updateInstructions();
	}

	public void prevMenuItem() {
		selected_btn--;
		if (selected_btn < 0) {
			selected_btn = btns.size - 1;
		}
		highlight_button();
		updateInstructions();
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
		batch.enableBlending();
		batch.end();
		gameStage.act();
		gameStage.draw();
		batch.begin();
		left_indicator.draw(batch);
		right_indicator.draw(batch);
		batch.end();
	}

	public void resetStatistics() {
		int ix;

		game.enforceSoundSettings();
		game.getSoundManager().playEffect("menu-click");
		update_btn_resetStatistics("Statistics Were Reset");
		for (ix = 0; ix < game.levels; ix++) {
			game.setLevelAccuracy(ix, 0);
		}
		
		game.saveLevelAccuracies();
		game.getOptions().putInteger("highScore", game.getHighScore());
		game.getOptions().flush();
		
		highlight_button();
	}

	@Override
	public void show() {
		super.show();
		update_btn_resetStatistics();
		initBackdrop();
		TextureRegion temp;
		indicator = new Texture("buttons/da-gi-si_2.png");
		indicator.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		System.out.println("texture size: "
				+ new Vector2(indicator.getWidth(), indicator.getHeight()));

		temp = new TextureRegion(indicator);
		System.out.println("temp size: "
				+ new Vector2(temp.getRegionWidth(), temp.getRegionHeight()));

		left_indicator.setRegion(temp);
		left_indicator.setBounds(0, 0, temp.getRegionWidth(),
				temp.getRegionHeight());

		System.out.println("indicator size: "
				+ new Vector2(left_indicator.getWidth(), left_indicator
						.getHeight()));

		right_indicator.setRegion(temp);
		right_indicator.setBounds(0, 0, temp.getRegionWidth(),
				temp.getRegionHeight());
		right_indicator.flip(true, false);

		highlight_button();
	}

	public void soundEffects() {

		switch (game.getSoundEffectsVolume()) {
		case On:
			game.setSoundEffectsVolume(SoundEffectVolume.Low);
			break;
		case Low:
			game.setSoundEffectsVolume(SoundEffectVolume.Off);
			break;
		case Off:
			game.setSoundEffectsVolume(SoundEffectVolume.On);
			break;
		default:
			break;
		}
		updateSoundEffectsDisplay();
		game.enforceSoundSettings();
		game.getSoundManager().playEffect("menu-click");
		highlight_button();
	}

	public void trainingScreen() {
		switch (game.getShowTrainingScreen()) {
		case Brief:
			game.setShowTrainingScreen(TrainingScreenMode.Long);
			break;
		case Long:
			game.setShowTrainingScreen(TrainingScreenMode.Off);
			break;
		default:
			game.setShowTrainingScreen(TrainingScreenMode.Brief);
		}
		updateTrainingScreenDisplay();
		game.enforceSoundSettings();
		game.getSoundManager().playEffect("menu-click");
		highlight_button();
	}

	private void update_btn_resetStatistics() {
		update_btn_resetStatistics("Reset Statistics");
	}

	private void update_btn_resetStatistics(String msg) {
		btn_resetStatistics.setText(msg);
		btn_resetStatistics.pack();
		btn_resetStatistics
				.setX((screenWidth - btn_resetStatistics.getWidth()) / 2);
	}

	private void updateChallengeModeDisplay() {
		if (game.isChallengeAudio()) {
			btn_challengeSoundMode.setText("Challenge Audio: ON");
		} else {
			btn_challengeSoundMode.setText("Challenge Audio: OFF");
		}
		btn_challengeWordMode.setText("Challenge Word Display: "
				+ game.getChallengeWord().name());

		btn_challengeSoundMode.pack();
		btn_challengeWordMode.pack();
		btn_challengeSoundMode.setX((screenWidth - btn_challengeSoundMode
				.getWidth()) / 2);
		btn_challengeWordMode.setX((screenWidth - btn_challengeWordMode
				.getWidth()) / 2);
	}

	public void updateInstructions() {
		if (selected_btn == idx_volume) {
			lbl_instructions.setText(INSTRUCT_VOLUME);
		} else {
			lbl_instructions.setText(INSTRUCT_STANDARD);
		}
		lbl_instructions.pack();
		lbl_instructions.setX((screenWidth - lbl_instructions.getWidth()) / 2);
	}

	private void updateSoundEffectsDisplay() {
		btn_soundEffects.setText("Sound Effects: "
				+ game.getSoundEffectsVolume().name());
		btn_soundEffects.pack();
		btn_soundEffects.setX((screenWidth - btn_soundEffects.getWidth()) / 2);
	}

	private void updateTrainingScreenDisplay() {
		btn_trainingScreen.setText("Training Mode: "
				+ game.getShowTrainingScreen().name());
		btn_trainingScreen.pack();
		btn_trainingScreen
				.setX((screenWidth - btn_trainingScreen.getWidth()) / 2);
	}

	public void volumeDec() {

		game.setVolume(game.getVolume() - 1);
		btn_volumeLabel.setText(getVolumeLabel(game.getVolume()));
		game.enforceSoundSettings();
		game.getSoundManager().playEffect("menu-click");
		highlight_button();
	}

	public void volumeInc() {

		game.setVolume(game.getVolume() + 1);
		btn_volumeLabel.setText(getVolumeLabel(game.getVolume()));
		game.enforceSoundSettings();
		game.getSoundManager().playEffect("menu-click");
		highlight_button();
	}

	public void wordMode() {
		switch (game.getChallengeWord()) {
		case Syllabary:
			game.setChallengeWord(ChallengeWordMode.Latin);
			break;
		case Latin:
			game.setChallengeWord(ChallengeWordMode.None);
			break;
		case None:
			game.setChallengeWord(ChallengeWordMode.Syllabary);
			break;
		default:
			break;
		}
		updateChallengeModeDisplay();
		game.enforceSoundSettings();
		game.getSoundManager().playEffect("menu-click");
		highlight_button();
	}
}
