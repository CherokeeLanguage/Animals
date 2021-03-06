package com.cherokeelessons.animals;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.animals.enums.ChallengeWordMode;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.animals.enums.SoundEffectVolume;
import com.cherokeelessons.animals.enums.TrainingMode;
import com.cherokeelessons.common.FontLoader;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Gamepads;
import com.cherokeelessons.common.Prefs;

/**
 * Orientation (Landscape, Portrait) Master Game Volume (0% - 100%) Sound Word
 * Mode: (On, Off) + Written Word Mode: (Latin, Syllabary, None) Sound Effects
 * Mode: (On, Off) Training Screen: (On, Off) Reset Statistics About Program
 * (Software Used, Fonts Used, etc)
 */

public class ScreenOptionsMenu extends GameScreen {
	
	private static final String PRESS_A_RESET = "Pressing [A] or [Select] will reset your progress";

	private static final String PRESS_A = "Press [A] or [Select] to cycle through options";

	private static final String LEFT_OR_RIGHT = "Move left for [-] | Move right for [+]";

	@Override
	protected boolean useBackdrop() {
		return true;
	}

	private static final String INDICATOR = ScreenMainMenu.INDICATOR;

	private String touchscreenInstructions() {
		return game.isTelevision()?"":"[BACK]";
	}

	private static final float INDI_SCALE = ScreenMainMenu.INDI_SCALE;

	private final float[] baseLines;
	private final int baseLines_cnt;

	private final MenuLabel btn_challengeSoundMode;
	private final MenuLabel btn_challengeWordMode;
	private final MenuLabel btn_resetStatistics;
	private final MenuLabel btn_soundEffects;
	private final MenuLabel btn_trainingScreen;
	
	private final MenuLabel btn_zoom;
	private final MenuLabel btn_musicVolume;
	private final MenuLabel btn_masterVolume;
	
	private final Array<MenuLabel> btns = new Array<>();

	private final LabelStyle buttonStyle;
	private BitmapFont font = null;
	private Texture indicator;
	private final LabelStyle instructStyle;

	private final MenuLabel lbl_instructions;

	private final Image left_indicator = new Image();

	private final Integer optionItemSize = 74;
	public int optionsButton;

	private final Image right_indicator = new Image();

	private int selected_btn = 0;

	private final Color textColor;

	final private CtlrOptions_Watch watcher = new CtlrOptions_Watch(this);

	final private Prefs prefs;

	public ScreenOptionsMenu(final CherokeeAnimals _game) {
		super(_game);
		prefs = game.prefs;

		baseLines_cnt = 8;
		baseLines = new float[baseLines_cnt];

		int displayLine;

		final FontLoader fg = new FontLoader();
		font = fg.get(optionItemSize);
		final BitmapFont ifont = fg.get(64);

		textColor = GameColor.MAIN_TEXT;

		buttonStyle = new LabelStyle();
		buttonStyle.font = font;
		buttonStyle.fontColor = textColor;

		instructStyle = new LabelStyle();
		instructStyle.font = ifont;
		instructStyle.fontColor = textColor;

		displayLine = 0;
		lbl_instructions = new MenuLabel(touchscreenInstructions(), instructStyle, "Press [A] or [Back] to exit");
		lbl_instructions.pack();
		lbl_instructions.setX((fullZoneBox.width - lbl_instructions.getWidth()) / 2);
		lbl_instructions.menu_action_east = new Runnable() {
			@Override
			public void run() {
				game.gameEvent(GameEvent.EXIT_SCREEN);
			}
		};
		lbl_instructions.menu_action_west = new Runnable() {
			@Override
			public void run() {
				game.gameEvent(GameEvent.EXIT_SCREEN);
			}
		};

		final float optionsHeight = lbl_instructions.getHeight();
		calculateBaseLines(optionsHeight*1.1f);

		// start off with 100% volume level for width calculations
		btn_masterVolume = new MenuLabel(getVolumeLabel(100), buttonStyle, LEFT_OR_RIGHT);
		btn_masterVolume.setTouchable(Touchable.enabled);
		btn_masterVolume.pack();
		btn_masterVolume.setX((fullZoneBox.width - btn_masterVolume.getWidth()) / 2);
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
		
		btn_zoom = new MenuLabel(getZoomLabel(game.zoom()), buttonStyle, LEFT_OR_RIGHT);
		btn_zoom.setTouchable(Touchable.enabled);
		btn_zoom.pack();
		btn_zoom.setX((fullZoneBox.width - btn_zoom.getWidth()) / 2);
		
		btn_zoom.menu_action_east = new Runnable() {
			@Override
			public void run() {
				game.zoomInc();
				game.resize();
				btn_zoom.setText(getZoomLabel(game.zoom()));
			}
		};
		btn_zoom.menu_action_west = new Runnable() {
			@Override
			public void run() {
				game.zoomDec();
				game.resize();
				btn_zoom.setText(getZoomLabel(game.zoom()));
			}
		};
		
		// start off with 100% volume level for width calculations
		btn_musicVolume = new MenuLabel(getMusicLabel(100), buttonStyle, LEFT_OR_RIGHT);
		btn_musicVolume.setTouchable(Touchable.enabled);
		btn_musicVolume.pack();
		btn_musicVolume.setX((fullZoneBox.width - btn_musicVolume.getWidth()) / 2);
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

		btn_challengeSoundMode = new MenuLabel("", buttonStyle, PRESS_A);
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

		btn_challengeWordMode = new MenuLabel("", buttonStyle, PRESS_A);
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

		btn_soundEffects = new MenuLabel("", buttonStyle, PRESS_A);
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

		btn_trainingScreen = new MenuLabel("", buttonStyle, PRESS_A);
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
		
		btn_zoom.setY(getBaseLine(displayLine++));

		btn_resetStatistics = new MenuLabel("", buttonStyle, PRESS_A_RESET);
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
		btns.add(btn_zoom);
		btns.add(btn_trainingScreen);
		btns.add(btn_soundEffects);
		btns.add(btn_challengeWordMode);
		btns.add(btn_challengeSoundMode);
		btns.add(btn_musicVolume);
		btns.add(btn_masterVolume);
		btns.add(lbl_instructions);

		gameStage.clear();
		// gameStage.addActor(btn_aboutProgram);
		gameStage.addActor(btn_resetStatistics);
		gameStage.addActor(btn_zoom);
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

	private CharSequence getZoomLabel(int zoom) {
		String newText = "[-] Zoom Out: ";
		newText = newText + ((Integer) zoom).toString() + "%";
		newText += " [+]";
		return newText;
	}

	private void calculateBaseLines(final float bottomMargin) {
		float
		lineHeight = (fullZoneBox.height - bottomMargin) / baseLines.length;
		for (int ix = 0; ix < baseLines.length; ix++) {
			baseLines[ix] = bottomMargin + ix * lineHeight + (lineHeight - font.getLineHeight()) / 2f;
		}
		for (float bl: baseLines) {
			log("baseline: "+bl);
		}
	}

	public void doMenuItem(final PovDirection direction) {
		btns.get(selected_btn).doRun(direction);
	}

	@Override
	public boolean dpad(final int keyCode) {
		switch (keyCode) {
		case Keys.DPAD_CENTER:
			doMenuItem(PovDirection.east);
			return true;
		case Keys.DPAD_DOWN:
			hud_moveSouth();
			return true;
		case Keys.DPAD_LEFT:
			doMenuItem(PovDirection.west);
			return true;
		case Keys.DPAD_RIGHT:
			doMenuItem(PovDirection.east);
			return true;
		case Keys.DPAD_UP:
			hud_moveNorth();
			return true;
		default:
			break;
		}
		return false;
	}

	private float getBaseLine(final int ix) {
		if (ix < 0 || ix >= baseLines.length) {
			return 0f;
		}
		return baseLines[ix];
	}

	private String getMusicLabel(final int newVolume) {
		String newText = "[-] Music Volume: ";
		if (newVolume < 100) {
			newText += " ";
		}
		if (newVolume < 10) {
			newText += " ";
		}
		newText = newText + ((Integer) newVolume).toString() + "%";
		newText += " [+]";
		return newText;
	}

	public int getSelected_btn() {
		return selected_btn;
	}

	private String getVolumeLabel(final int newVolume) {
		String newText = "[-] Master Volume: ";
		if (newVolume < 100) {
			newText += " ";
		}
		if (newVolume < 10) {
			newText += " ";
		}
		newText = newText + ((Integer) newVolume).toString() + "%";
		newText += " [+]";
		return newText;
	}

	public void goBack() {
		game.sm.playEffect("menu-click");
		game.gameEvent(GameEvent.EXIT_SCREEN);
	}

	@Override
	public void hide() {
		for (final Controller controller : Gamepads.getControllers()) {
			watcher.disconnected(controller);
		}
		Gamepads.clearListeners();
		indicator.dispose();
		indicator = null;
		super.hide();
	}

	private void highlight_button() {
		highlight_button(false);
	}

	private void highlight_button(final boolean quiet) {
		if (!quiet) {
			game.sm.playEffect("box_moved");
		}
		final MenuLabel label = btns.get(selected_btn);
		final float left = label.getX();
		final float bottom = label.getY();
		final float right = label.getX() + label.getWidth();
		left_indicator.setPosition(left - left_indicator.getWidth() + 20, bottom);
		right_indicator.setPosition(right - 20, bottom);
		updateInstructions();
	}

	public void hud_moveNorth() {
		selected_btn--;
		if (selected_btn < 0) {
			selected_btn = btns.size - 1;
		}
		highlight_button();
	}

	public void hud_moveSouth() {
		selected_btn++;
		if (selected_btn >= btns.size) {
			selected_btn = 0;
		}
		highlight_button();
	}

	@Override
	public void render(final float delta) {
		super.render(delta);
	}

	private void resetMusicVolume() {
		final float masterVolume = game.prefs.getMasterVolume() / 100f;
		final float musicVolume = game.prefs.getMusicVolume() / 100f;
		game.music.setVolume(masterVolume * musicVolume);
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
		update_btn_resetStatistics();

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
		for (final Controller c : Gamepads.getControllers()) {
			watcher.connected(c);
		}

		for (int ix = 0; ix < btns.size; ix++) {
			final int _button = ix;
			final MenuLabel btn = btns.get(ix);
			btn.clearListeners();
			btn.addListener(new ClickListener() {
				@Override
				public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
						final int button) {
					selected_btn = _button;
					highlight_button(true);
					final float w = event.getListenerActor().getWidth();
					if (x / w >= .5f) {
						doMenuItem(PovDirection.east);
					} else {
						doMenuItem(PovDirection.west);
					}
					return true;
				}
			});
		}
		
		super.show();
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
		case Off:
		default:
			prefs.setTrainingMode(TrainingMode.Brief);
		}
		updateTrainingScreenDisplay();
		game.sm.playEffect("menu-click");
	}

	private void update_btn_resetStatistics() {
		update_btn_resetStatistics("Reset Statistics");
	}

	private void update_btn_resetStatistics(final String msg) {
		btn_resetStatistics.setText(msg);
		btn_resetStatistics.pack();
		btn_resetStatistics.setX((fullZoneBox.width - btn_resetStatistics.getWidth()) / 2);
	}

	private void updateChallengeModeDisplay() {
		if (prefs.getChallengeAudio()) {
			btn_challengeSoundMode.setText("Challenge Audio: ON");
		} else {
			btn_challengeSoundMode.setText("Challenge Audio: OFF");
		}
		btn_challengeWordMode.setText("Challenge Word Display: " + prefs.getChallengeMode().name());

		btn_challengeSoundMode.pack();
		btn_challengeWordMode.pack();
		btn_challengeSoundMode.setX((fullZoneBox.width - btn_challengeSoundMode.getWidth()) / 2);
		btn_challengeWordMode.setX((fullZoneBox.width - btn_challengeWordMode.getWidth()) / 2);
	}

	public void updateInstructions() {
		final MenuLabel label = btns.get(selected_btn);
		if (game.isTelevision()) {
			lbl_instructions.setText(label.getInstructions());
		} else {
			lbl_instructions.setText(touchscreenInstructions());
		}
		lbl_instructions.pack();
		lbl_instructions.setX((fullZoneBox.width - lbl_instructions.getWidth()) / 2);
	}

	private void updateSoundEffectsDisplay() {
		btn_soundEffects.setText("Sound Effects: " + prefs.getEffectsVolume().name());
		btn_soundEffects.pack();
		btn_soundEffects.setX((fullZoneBox.width - btn_soundEffects.getWidth()) / 2);
	}

	private void updateTrainingScreenDisplay() {
		btn_trainingScreen.setText("Training Mode: " + prefs.getTrainingMode().name());
		btn_trainingScreen.pack();
		btn_trainingScreen.setX((fullZoneBox.width - btn_trainingScreen.getWidth()) / 2);
	}

	public void wordMode() {
		switch (prefs.getChallengeMode()) {
		case Latin:
			prefs.setChallengeMode(ChallengeWordMode.Syllabary);
			break;
		case Syllabary:
			prefs.setChallengeMode(ChallengeWordMode.None);
			break;
		case None:
			prefs.setChallengeMode(ChallengeWordMode.Latin);
			break;
		default:
			break;
		}
		updateChallengeModeDisplay();
		game.sm.playEffect("menu-click");
	}
}
