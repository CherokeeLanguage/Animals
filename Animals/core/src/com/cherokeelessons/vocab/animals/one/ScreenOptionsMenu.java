package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.common.FontLoader;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Gamepads;
import com.cherokeelessons.common.OS;
import com.cherokeelessons.common.Prefs;
import com.cherokeelessons.common.Utils;
import com.cherokeelessons.vocab.animals.one.enums.ChallengeWordMode;
import com.cherokeelessons.vocab.animals.one.enums.GameEvent;
import com.cherokeelessons.vocab.animals.one.enums.SoundEffectVolume;
import com.cherokeelessons.vocab.animals.one.enums.TrainingMode;

/**
 * Orientation (Landscape, Portrait) Master Game Volume (0% - 100%) Sound Word
 * Mode: (On, Off) + Written Word Mode: (Latin, Syllabary, None) Sound Effects
 * Mode: (On, Off) Training Screen: (On, Off) Reset Statistics About Program
 * (Software Used, Fonts Used, etc)
 */

public class ScreenOptionsMenu extends GameScreen {

	private void resetMusicVolume() {
		game.musicPlayer.setVolume((float) prefs.getMasterVolume()
				* (float) prefs.getMusicVolume() / 10000f);
	}

	private static final String INDICATOR = ScreenMainMenu.INDICATOR;

	private static class MenuLabel extends Label {

		private Runnable menu_action_east = null;
		private Runnable menu_action_west = null;

		public MenuLabel(CharSequence text, LabelStyle style) {
			super(text, style);
		}

		public void doRun(PovDirection direction) {
			if (menu_action_east != null && direction.equals(PovDirection.east)) {
				Gdx.app.postRunnable(menu_action_east);
				return;
			}
			if (menu_action_west != null && direction.equals(PovDirection.west)) {
				Gdx.app.postRunnable(menu_action_west);
				return;
			}
		}
	}

	private boolean isOuya = false;
	private static int idx_volume = 0;
	private static final String OUYA_INSTRUCT = "DPAD up/down select option, [O] Change value, [A] Back";
	private static final String OUYA_VOL_INSTRUCT = "DPAD up/down select option, DPAD left/right change value, [A] Back";

	private static final String TAB_INSTRUCT = "Tap an option to change its value. Tap here to exit option screen.";

	private static final float INDI_SCALE = ScreenMainMenu.INDI_SCALE;
	private float[] baseLines;

	private int baseLines_cnt;
	private MenuLabel btn_challengeSoundMode;
	private MenuLabel btn_challengeWordMode;
	private MenuLabel btn_resetStatistics;
	private MenuLabel btn_soundEffects;
	private MenuLabel btn_trainingScreen;
	private MenuLabel btn_musicVolume;
	private MenuLabel btn_masterVolume;

	private Array<MenuLabel> btns = new Array<MenuLabel>();
	private LabelStyle buttonStyle;
	private BitmapFont font = null;
	private Texture indicator;

	private LabelStyle instructStyle;

	private MenuLabel lbl_instructions;

	private Image left_indicator = new Image();

	private float lineHeight = 0;
	private float offset = 0;
	private Integer optionItemSize = 76;

	public int optionsButton;

	private Image right_indicator = new Image();

	private int selected_btn = 0;

	private Color textColor;

	final Array<Sprite> wall = new Array<Sprite>();

	private TextureAtlas wall_atlas;

	final private ControllerOptions_Watch watcher = new ControllerOptions_Watch(
			this);
	private int idx_music;

	final private Prefs prefs;

	public ScreenOptionsMenu(final CherokeeAnimals _game) {
		super(_game);
		prefs = game.prefs;
		isOuya = OS.Platform.Ouya.equals(OS.platform);

		baseLines_cnt = 7;
		baseLines = new float[baseLines_cnt];

		int displayLine;

		FontLoader fg = new FontLoader();
		font = fg.get(optionItemSize);
		BitmapFont ifont = fg.get((optionItemSize * 2) / 3);

		textColor = GameColor.GREEN;

		buttonStyle = new LabelStyle();
		buttonStyle.font = font;
		buttonStyle.fontColor = textColor;

		instructStyle = new LabelStyle();
		instructStyle.font = ifont;
		instructStyle.fontColor = textColor;

		displayLine = 0;

		lbl_instructions = new MenuLabel(isOuya ? OUYA_INSTRUCT : TAB_INSTRUCT,
				instructStyle);
		lbl_instructions.pack();
		lbl_instructions
				.setX((screenSize.width - lbl_instructions.getWidth()) / 2);
		lbl_instructions.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				game.gameEvent(GameEvent.Done);
				return true;
			}
		});

		float optionsHeight = lbl_instructions.getHeight();
		calculateBaseLines(optionsHeight);

		// start off with 100% volume level for width calculations
		btn_masterVolume = new MenuLabel(getVolumeLabel(100), buttonStyle);
		btn_masterVolume.setTouchable(Touchable.enabled);
		btn_masterVolume.pack();
		btn_masterVolume
				.setX((screenSize.width - btn_masterVolume.getWidth()) / 2);
		btn_masterVolume.setY(getBaseLine(displayLine++));

		btn_masterVolume.setText(getVolumeLabel(prefs.getMasterVolume()));
		btn_masterVolume.menu_action_east = new Runnable() {
			@Override
			public void run() {
				int volume = prefs.getMasterVolume();
				if (volume < 100) {
					volume = (volume / 5 + 1) * 5;
					prefs.setMasterVolume(volume);
					btn_masterVolume.setText(getVolumeLabel(volume));
					game.sm.playEffect("menu-click");
					highlight_button(true);
					resetMusicVolume();
				}
			}
		};
		btn_masterVolume.menu_action_west = new Runnable() {
			@Override
			public void run() {
				int volume = prefs.getMasterVolume();
				if (volume > 0) {
					volume = (volume / 5 - 1) * 5;
					prefs.setMasterVolume(volume);
					btn_masterVolume.setText(getVolumeLabel(volume));
					game.sm.playEffect("menu-click");
					highlight_button(true);
					resetMusicVolume();
				}
			}

		};

		// start off with 100% volume level for width calculations
		btn_musicVolume = new MenuLabel(getMusicLabel(100), buttonStyle);
		btn_musicVolume.setTouchable(Touchable.enabled);
		btn_musicVolume.pack();
		btn_musicVolume
				.setX((screenSize.width - btn_musicVolume.getWidth()) / 2);
		btn_musicVolume.setY(getBaseLine(displayLine++));
		btn_musicVolume.setText(getMusicLabel(prefs.getMusicVolume()));
		btn_musicVolume.menu_action_east = new Runnable() {
			@Override
			public void run() {
				int volume = prefs.getMusicVolume();
				if (volume < 100) {
					volume = (volume / 5 + 1) * 5;
					prefs.setMusicVolume(volume);
					btn_musicVolume.setText(getMusicLabel(volume));
					game.sm.playEffect("menu-click");
					highlight_button(true);
					resetMusicVolume();
				}
			}
		};
		btn_musicVolume.menu_action_west = new Runnable() {
			@Override
			public void run() {
				int volume = prefs.getMusicVolume();
				if (volume > 0) {
					volume = (volume / 5 - 1) * 5;
					prefs.setMusicVolume(volume);
					btn_musicVolume.setText(getMusicLabel(volume));
					game.sm.playEffect("menu-click");
					highlight_button(true);
					resetMusicVolume();
				}
			}
		};

		btn_challengeSoundMode = new MenuLabel("", buttonStyle);
		btn_challengeSoundMode.setY(getBaseLine(displayLine++));
		btn_challengeSoundMode.setTouchable(Touchable.enabled);
		btn_challengeSoundMode.menu_action_east = new Runnable() {
			@Override
			public void run() {
				prefs.setChallengeAudio(!prefs.getChallengeAudio());
				updateChallengeModeDisplay();
				game.sm.playEffect("menu-click");
				highlight_button(true);
			}
		};
		btn_challengeSoundMode.menu_action_west = btn_challengeSoundMode.menu_action_east;

		btn_challengeWordMode = new MenuLabel("", buttonStyle);
		btn_challengeWordMode.setY(getBaseLine(displayLine++));
		btn_challengeWordMode.menu_action_east = new Runnable() {
			@Override
			public void run() {
				wordMode();
				highlight_button(true);
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
				highlight_button(true);
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
				highlight_button(true);
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
				highlight_button(true);
			}
		};
		btn_resetStatistics.menu_action_west = btn_resetStatistics.menu_action_east;
		update_btn_resetStatistics();

		btns.add(btn_resetStatistics);
		btns.add(btn_trainingScreen);
		btns.add(btn_soundEffects);
		btns.add(btn_challengeWordMode);
		btns.add(btn_challengeSoundMode);
		idx_music = btns.size;
		btns.add(btn_musicVolume);
		idx_volume = btns.size;
		btns.add(btn_masterVolume);

		gameStage.clear();
		// gameStage.addActor(btn_aboutProgram);
		gameStage.addActor(btn_resetStatistics);
		gameStage.addActor(btn_trainingScreen);
		gameStage.addActor(btn_soundEffects);
		gameStage.addActor(btn_challengeWordMode);
		gameStage.addActor(btn_challengeSoundMode);
		gameStage.addActor(btn_musicVolume);
		gameStage.addActor(btn_masterVolume);
		gameStage.addActor(lbl_instructions);

		gameStage.addActor(left_indicator);
		gameStage.addActor(right_indicator);
	}

	private void calculateBaseLines(float bottomMargin) {
		int ix;
		lineHeight = (screenSize.height - bottomMargin) / baseLines.length;
		for (ix = 0; ix < baseLines.length; ix++) {
			baseLines[ix] = (float) Math.ceil(bottomMargin + ix * lineHeight
					+ offset + (lineHeight - font.getLineHeight()) / 2);
		}
	}

	public void doMenuItem(PovDirection direction) {
		btns.get(selected_btn).doRun(direction);
	}

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
		String newText = isOuya ? "Master Volume: " : "[-] Master Volume: ";
		if (newVolume < 100) {
			newText += " ";
		}
		if (newVolume < 10) {
			newText += " ";
		}
		newText = newText + ((Integer) newVolume).toString() + "%";
		newText += isOuya ? "" : " [+]";
		return newText;
	}

	private String getMusicLabel(int newVolume) {
		String newText = isOuya ? "Music Volume: " : "[-] Music Volume: ";
		if (newVolume < 100) {
			newText += " ";
		}
		if (newVolume < 10) {
			newText += " ";
		}
		newText = newText + ((Integer) newVolume).toString() + "%";
		newText += isOuya ? "" : " [+]";
		return newText;
	}

	public void goBack() {
		game.sm.playEffect("menu-click");
		game.gameEvent(GameEvent.Done);
	}

	@Override
	public void hide() {
		for (Controller controller : Gamepads.getControllers()) {
			watcher.disconnected(controller);
		}
		Gamepads.clearListeners();
		wall_atlas.dispose();
		wall_atlas = null;
		indicator.dispose();
		indicator = null;
		super.hide();
	}

	private void highlight_button(boolean quiet) {
		if (!quiet) {
			game.sm.playEffect("box_moved");
		}
		MenuLabel label = btns.get(selected_btn);
		float left = label.getX();
		float bottom = label.getY();
		float right = label.getX() + label.getWidth();
		left_indicator.setPosition(left - left_indicator.getWidth() + 20,
				bottom);
		right_indicator.setPosition(right - 20, bottom);
	}

	private void highlight_button() {
		highlight_button(false);
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
		super.render(delta);
		batch.begin();
		for (Sprite s : wall) {
			s.draw(batch);
		}
		batch.end();
		gameStage.draw();
	}

	public void resetStatistics() {
		int ix;
		game.sm.playEffect("menu-click");
		update_btn_resetStatistics("Statistics Were Reset");
		for (ix = 0; ix < game.getLevels(); ix++) {

			prefs.setLevelAccuracy(ix, 0);
		}
	}

	@Override
	public void show() {
		super.show();
		update_btn_resetStatistics();
		wall_atlas=Utils.initBackdrop(wall);

		indicator = new Texture(INDICATOR);
		indicator.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		TextureRegionDrawable temp;
		indicator = new Texture(INDICATOR);
		indicator.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		temp = new TextureRegionDrawable(new TextureRegion(indicator));

		left_indicator.setDrawable(temp);
		left_indicator.pack();

		right_indicator.setDrawable(temp);
		right_indicator.pack();

		left_indicator.setOrigin(0, 0);
		left_indicator.setOrigin(left_indicator.getWidth() / 2, 0);
		left_indicator.setScaleX(INDI_SCALE);
		left_indicator.setScaleY(INDI_SCALE);

		right_indicator.setOrigin(right_indicator.getWidth() / 2, 0);
		right_indicator.setScaleX(-INDI_SCALE);
		right_indicator.setScaleY(INDI_SCALE);

		highlight_button(true);

		Gamepads.addListener(watcher);
		for (Controller c : Gamepads.getControllers()) {
			watcher.connected(c);
		}

		for (int ix = 0; ix < btns.size; ix++) {
			final int _button = ix;
			MenuLabel btn = btns.get(ix);
			btn.clearListeners();
			btn.addListener(new ClickListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					selected_btn = _button;
					highlight_button(true);
					updateInstructions();
					float w = event.getListenerActor().getWidth();
					if (x / w >= .5f) {
						doMenuItem(PovDirection.east);
					} else {
						doMenuItem(PovDirection.west);
					}
					return true;
				}
			});
		}
	}

	public void soundEffects() {

		switch (prefs.getEffectsVolume()) {
		case High:
			prefs.setEffectsVolume(SoundEffectVolume.Low);
			break;
		case Low:
			prefs.setEffectsVolume(SoundEffectVolume.Off);
			break;
		case Off:
			prefs.setEffectsVolume(SoundEffectVolume.High);
			break;
		default:
			break;
		}
		updateSoundEffectsDisplay();
		game.sm.playEffect("menu-click");
	}

	public void trainingScreen() {

		switch (prefs.getTrainingMode()) {
		case Brief:
			prefs.setTrainingMode(TrainingMode.Long);
			break;
		case Long:
			prefs.setTrainingMode(TrainingMode.Off);
			break;
		default:
			prefs.setTrainingMode(TrainingMode.Brief);
		}
		updateTrainingScreenDisplay();
		game.sm.playEffect("menu-click");
	}

	private void update_btn_resetStatistics() {
		update_btn_resetStatistics("Reset Statistics");
	}

	private void update_btn_resetStatistics(String msg) {
		btn_resetStatistics.setText(msg);
		btn_resetStatistics.pack();
		btn_resetStatistics.setX((screenSize.width - btn_resetStatistics
				.getWidth()) / 2);
	}

	private void updateChallengeModeDisplay() {

		if (prefs.getChallengeAudio()) {
			btn_challengeSoundMode.setText("Challenge Audio: ON");
		} else {
			btn_challengeSoundMode.setText("Challenge Audio: OFF");
		}
		btn_challengeWordMode.setText("Challenge Word Display: "
				+ prefs.getChallengeMode().name());

		btn_challengeSoundMode.pack();
		btn_challengeWordMode.pack();
		btn_challengeSoundMode.setX((screenSize.width - btn_challengeSoundMode
				.getWidth()) / 2);
		btn_challengeWordMode.setX((screenSize.width - btn_challengeWordMode
				.getWidth()) / 2);
	}

	public void updateInstructions() {
		if (selected_btn == idx_volume || selected_btn == idx_music) {
			lbl_instructions.setText(isOuya ? OUYA_VOL_INSTRUCT : TAB_INSTRUCT);
		} else {
			lbl_instructions.setText(isOuya ? OUYA_INSTRUCT : TAB_INSTRUCT);
		}
		lbl_instructions.pack();
		lbl_instructions
				.setX((screenSize.width - lbl_instructions.getWidth()) / 2);
	}

	private void updateSoundEffectsDisplay() {

		btn_soundEffects.setText("Sound Effects: "
				+ prefs.getEffectsVolume().name());
		btn_soundEffects.pack();
		btn_soundEffects
				.setX((screenSize.width - btn_soundEffects.getWidth()) / 2);
	}

	private void updateTrainingScreenDisplay() {

		btn_trainingScreen.setText("Training Mode: "
				+ prefs.getTrainingMode().name());
		btn_trainingScreen.pack();
		btn_trainingScreen.setX((screenSize.width - btn_trainingScreen
				.getWidth()) / 2);
	}

	public void wordMode() {

		switch (prefs.getChallengeMode()) {
		case Esperanto:
			prefs.setChallengeMode(ChallengeWordMode.EsperantoX);
			break;
		case EsperantoX:
			prefs.setChallengeMode(ChallengeWordMode.None);
			break;
		case None:
			prefs.setChallengeMode(ChallengeWordMode.Esperanto);
			break;
		default:
			break;
		}
		updateChallengeModeDisplay();
		game.sm.playEffect("menu-click");
	}
}
