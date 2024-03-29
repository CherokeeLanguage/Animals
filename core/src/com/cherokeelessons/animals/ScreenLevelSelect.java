package com.cherokeelessons.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.animals.views.View3x3Selector;
import com.cherokeelessons.animals.views.View3x3Selector.onClick;
import com.cherokeelessons.common.FontLoader;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Gamepads;

public class ScreenLevelSelect extends GameScreen implements DpadInterface {

	@Override
	protected boolean useBackdrop() {
		return false;
	}

	private static final String TAB_PANEL1_TEXT = "[Tap to Show Levels 10-18]";
	private static final String TAB_PANEL2_TEXT = "[Tap to Show Levels 1-9]";

	private static final String TV_PANEL1_TEXT = "Move Right to Show Levels 10-18";
	private static final String TV_PANEL2_TEXT = "Move Left to Show Levels 1-9";

	private int activeHud = 0;

	private final Label[][] btn_labels = new Label[game.getLevels() / 9][9];

	FileHandle button_highlight = Gdx.files.internal("buttons/2610_white.png");
	private final FileHandle didGood = Gdx.files.internal("buttons/2714_white.png");
	private BitmapFont font;
	private final Color fontColor = GameColor.MAIN_TEXT;

	private final int fontSize = 44;

	private int level_highlighted = 0;

	private final Group masterGroup = new Group();
	private int minPercentAdvance = 80;
	private final FileHandle levelLockedPic = Gdx.files.internal("images/indicators/Padlock-red.png");
	private Label[] panelSwitch;
	private View3x3Selector[] selectViewGraphic;

	private View3x3Selector[] selectViewHUD;

	private View3x3Selector[] selectViewLevelIndicator;

	private View3x3Selector[] selectViewOverlay;

	private final View3x3Selector.onClick startAtLevel_1_to_9 = new onClick() {
		@Override
		public void handleClick(final int level) {
			startGameLevel(level);
		}
	};
	private final View3x3Selector.onClick startAtLevel_10_to_18 = new onClick() {
		@Override
		public void handleClick(final int level) {
			startGameLevel(level + 9);
		}
	};
	private final String tab_title_unlocked = "This level is unlocked.";

	private final String tab_title_locked = "This level is locked until you get at least " + minPercentAdvance
			+ "%+ on the previous level.";
	private final CtlrLevelSelect_Watch watcher = new CtlrLevelSelect_Watch(this);

	private int panelCount;

	private boolean usingController;

	private boolean showSelector = false;

	public ScreenLevelSelect(final CherokeeAnimals game) {
		super(game);
		usingController = Gamepads.getControllers().size != 0;
		panelCount = (int) Math.ceil(game.getLevels() / 9);
		int ix;
		final LabelStyle ls = new LabelStyle();
		final LabelStyle ps = new LabelStyle();
		int panel;
		float bottomMargin;
		final FontLoader fg = new FontLoader();

		panelSwitch = new Label[panelCount];

		minPercentAdvance = game.getMinPercent();

		font = fg.get(fontSize);
		ls.font = font;
		ls.fontColor = new Color(fontColor);
		ls.fontColor.a = 1f;

		ps.font = font;
		ps.fontColor = new Color(GameColor.MAIN_TEXT);
		ps.fontColor.a = 1f;
		if (game.isTelevision() || usingController) {
			panelSwitch[0] = new Label(TV_PANEL1_TEXT, ps);
			panelSwitch[1] = new Label(TV_PANEL2_TEXT, ps);
		} else {
			panelSwitch[0] = new Label(TAB_PANEL1_TEXT, ps);
			panelSwitch[1] = new Label(TAB_PANEL2_TEXT, ps);
		}
		panelSwitch[0].pack();
		panelSwitch[1].pack();
		panelSwitch[0].setX((fullZoneBox.width - panelSwitch[0].getWidth()) / 2);
		panelSwitch[0].setY(0);
		panelSwitch[1].setX((fullZoneBox.width - panelSwitch[1].getWidth()) / 2);
		panelSwitch[1].setY(0);
		panelSwitch[0].addListener(new ClickListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
					final int button) {
				moveTo10To18();
				return true;
			}
		});
		panelSwitch[1].addListener(new ClickListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
					final int button) {
				moveTo1To9();
				return true;
			}
		});
		bottomMargin = panelSwitch[0].getHeight();

		selectViewLevelIndicator = new View3x3Selector[panelCount];
		selectViewLevelIndicator[0] = new View3x3Selector(fullZoneBox);
		selectViewLevelIndicator[0].setTouchable(Touchable.enabled);
		selectViewLevelIndicator[0].setBottomMargin(bottomMargin);

		selectViewLevelIndicator[1] = new View3x3Selector(fullZoneBox);
		selectViewLevelIndicator[1].setTouchable(Touchable.enabled);
		selectViewLevelIndicator[1].setBottomMargin(bottomMargin);

		selectViewGraphic = new View3x3Selector[panelCount];
		selectViewGraphic[0] = new View3x3Selector(fullZoneBox);
		selectViewGraphic[0].setTouchable(Touchable.disabled);
		selectViewGraphic[0].setBottomMargin(bottomMargin);

		selectViewGraphic[1] = new View3x3Selector(fullZoneBox);
		selectViewGraphic[1].setTouchable(Touchable.disabled);
		selectViewGraphic[1].setBottomMargin(bottomMargin);

		selectViewOverlay = new View3x3Selector[panelCount];
		selectViewOverlay[0] = new View3x3Selector(fullZoneBox);
		selectViewOverlay[0].setTouchable(Touchable.disabled);
		selectViewOverlay[0].setBottomMargin(bottomMargin);
		selectViewOverlay[0].setHandler(startAtLevel_1_to_9);

		selectViewOverlay[1] = new View3x3Selector(fullZoneBox);
		selectViewOverlay[1].setTouchable(Touchable.disabled);
		selectViewOverlay[1].setBottomMargin(bottomMargin);
		selectViewOverlay[1].setHandler(startAtLevel_10_to_18);

		selectViewHUD = new View3x3Selector[panelCount];

		selectViewHUD[0] = new View3x3Selector(fullZoneBox);
		selectViewHUD[0].setTouchable(Touchable.disabled);
		selectViewHUD[0].setTitle(unlockedText());
		selectViewHUD[0].setBottomMargin(bottomMargin);
		selectViewHUD[0].setHandler(startAtLevel_1_to_9);

		selectViewHUD[1] = new View3x3Selector(fullZoneBox);
		selectViewHUD[1].setTouchable(Touchable.disabled);
		selectViewHUD[1].setTitle(unlockedText());
		selectViewHUD[1].setBottomMargin(bottomMargin);
		selectViewHUD[1].setHandler(startAtLevel_10_to_18);

		selectViewLevelIndicator[0].setBoxMargin(4);
		selectViewGraphic[0].setBoxMargin(24);

		selectViewLevelIndicator[1].setBoxMargin(4);
		selectViewGraphic[1].setBoxMargin(24);

		selectViewOverlay[0].setBoxMargin(24);
		selectViewOverlay[1].setBoxMargin(24);

		for (ix = 0; ix < game.getLevels(); ix++) {
			panel = ix / 9;
			btn_labels[panel][ix % 9] = new Label("", ls);
			selectViewLevelIndicator[panel].addActor(btn_labels[panel][ix % 9]);
		}

		/*
		 * move the second set off screen to the right..
		 */
		final int extra_offset = 100;
		selectViewLevelIndicator[1].setX(fullZoneBox.width + selectViewLevelIndicator[1].getX() + extra_offset);
		selectViewGraphic[1].setX(fullZoneBox.width + selectViewGraphic[1].getX() + extra_offset);
		selectViewOverlay[1].setX(fullZoneBox.width + selectViewOverlay[1].getX() + extra_offset);
		selectViewHUD[1].setX(fullZoneBox.width + selectViewHUD[1].getX() + extra_offset);
		panelSwitch[1].setX(fullZoneBox.width + panelSwitch[1].getX() + extra_offset);

		masterGroup.addActor(selectViewGraphic[0]);
		masterGroup.addActor(selectViewGraphic[1]);

		masterGroup.addActor(selectViewOverlay[0]);
		masterGroup.addActor(selectViewOverlay[1]);

		masterGroup.addActor(selectViewLevelIndicator[0]);
		masterGroup.addActor(selectViewLevelIndicator[1]);

		masterGroup.addActor(panelSwitch[0]);
		masterGroup.addActor(panelSwitch[1]);

		masterGroup.addActor(selectViewHUD[0]);
		masterGroup.addActor(selectViewHUD[1]);

		gameStage.addActor(masterGroup);

	}

	private void connectClickers() {
		for (int ia = 0; ia < 2; ia++) {
			final int pnl = ia;
			for (int ib = 0; ib < selectViewLevelIndicator[ia].button_count(); ib++) {
				final int btn = ib;
				selectViewLevelIndicator[ia].addListener(btn, new ClickListener() {
					@Override
					public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
							final int button) {
						activeHud = pnl;
						level_highlighted = btn;
						hud_showIndicator(true);
						hud_select();
						return true;
					}
				});
			}
		}
	}

	private void disconnectClickers() {
		for (int ia = 0; ia < 2; ia++) {
			for (int ib = 0; ib < selectViewLevelIndicator[ia].button_count(); ib++) {
				selectViewLevelIndicator[ia].clearListeners(ib);
			}
		}
	}

	@Override
	public boolean dpad(final int keyCode) {
		if (!showSelector) {
			showSelector = true;
			hud_showIndicator(true);
		}
		switch (keyCode) {
		case Keys.DPAD_CENTER:
			hud_select();
			return true;
		case Keys.DPAD_DOWN:
			hud_moveSouth();
			return true;
		case Keys.DPAD_LEFT:
			hud_moveLeft();
			return true;
		case Keys.DPAD_RIGHT:
			hud_moveRight();
			return true;
		case Keys.DPAD_UP:
			hud_moveNorth();
			return true;
		default:
			break;
		}
		return false;
	}

	@Override
	public void hide() {
		for (final Controller controller : Gamepads.getControllers()) {
			watcher.disconnected(controller);
		}
		Gamepads.clearListeners();
		disconnectClickers();
		super.hide();
	}

	private void hud_clearIndicator() {
		final View3x3Selector activehud = selectViewHUD[activeHud];
		for (int ix = 0; ix < activehud.button_count(); ix++) {
			activehud.setImage(ix, null);
		}
	}

	public void hud_moveLeft() {
		if (!showSelector) {
			showSelector = true;
			hud_showIndicator(true);
		}
		atLeftCheck: {
			if (level_highlighted == 0) {
				if (activeHud == 1) {
					level_highlighted += 2;
					moveTo1To9();
				}
				break atLeftCheck;
			}
			if (level_highlighted == 3) {
				if (activeHud == 1) {
					level_highlighted += 2;
					moveTo1To9();
				}
				break atLeftCheck;
			}
			if (level_highlighted == 6) {
				if (activeHud == 1) {
					level_highlighted += 2;
					moveTo1To9();
				}
				break atLeftCheck;
			}
			level_highlighted--;
			hud_showIndicator();
		}
	}

	public void hud_moveNorth() {
		if (!showSelector) {
			showSelector = true;
			hud_showIndicator(true);
		}
		do {
			if (level_highlighted == 0) {
				break;
			}
			if (level_highlighted == 1) {
				break;
			}
			if (level_highlighted == 2) {
				break;
			}
			level_highlighted -= 3;
			hud_showIndicator();
		} while (false);
	}

	public void hud_moveRight() {
		if (!showSelector) {
			showSelector = true;
			hud_showIndicator(true);
		}
		do {
			if (level_highlighted == 2) {
				if (activeHud == 0) {
					level_highlighted -= 2;
					moveTo10To18();
				}
				break;
			}
			if (level_highlighted == 5) {
				if (activeHud == 0) {
					level_highlighted -= 2;
					moveTo10To18();
				}
				moveTo10To18();
				break;
			}
			if (level_highlighted == 8) {
				if (activeHud == 0) {
					level_highlighted -= 2;
					moveTo10To18();
				}
				moveTo10To18();
				break;
			}
			level_highlighted++;
			hud_showIndicator();
		} while (false);
	}

	public void hud_moveSouth() {
		if (!showSelector) {
			showSelector = true;
			hud_showIndicator(true);
		}
		do {
			if (level_highlighted == 6) {
				break;
			}
			if (level_highlighted == 7) {
				break;
			}
			if (level_highlighted == 8) {
				break;
			}
			level_highlighted += 3;
			hud_showIndicator();
		} while (false);
	}

	public void hud_select() {
		do {
			if (activeHud == 0) {
				startAtLevel_1_to_9.handleClick(level_highlighted);
				break;
			}
			startAtLevel_10_to_18.handleClick(level_highlighted);
		} while (false);
	}

	private void hud_showIndicator() {
		hud_showIndicator(false);
	}

	private void hud_showIndicator(final boolean quiet) {
		if (!quiet) {
			game.sm.playEffect("box_moved");
		}
		final View3x3Selector activehud = selectViewHUD[activeHud];
		hud_clearIndicator();
		activehud.setImage(level_highlighted, button_highlight);
		final Color gold3_50 = new Color(GameColor.GOLD3);
		gold3_50.a = .7f;
		final Color gold2_50 = new Color(GameColor.GOLD2);
		gold2_50.a = .1f;
		activehud.setColor(level_highlighted, gold2_50);
		activehud.setAlpha(level_highlighted, 0f);
		if (showSelector) {
			activehud.setAlpha(level_highlighted, 0.7f);
			final Action act_gold3 = Actions.color(gold3_50, 0.7f, Interpolation.smoother);
			final Action act_gold2 = Actions.color(gold2_50, 0.7f, Interpolation.smoother);
			final Action act_seq = Actions.sequence(act_gold2, act_gold3);
			final Action act = Actions.forever(act_seq);
			activehud.addAction(level_highlighted, act);
		}
		final int level = activeHud * activehud.button_count() + level_highlighted;
		if (!isLevelUnlocked(level)) {
			selectViewHUD[activeHud].setTitle(tab_title_locked);
		} else {
			selectViewHUD[activeHud].setTitle(unlockedText());
		}
	}

	private String unlockedText() {
		String unlockedTxt = tab_title_unlocked
				+ (game.isTelevision() ? " Press [A] or [Select] to play" : " Tap to play");
		return unlockedTxt;
	}

	private boolean isLevelUnlocked(final int level) {
		return !(level > 0 && game.prefs.getLevelAccuracy(level) == 0
				&& game.prefs.getLevelAccuracy(level - 1) < minPercentAdvance);
	}

	/* [Y] */
	public void moveTo10To18() {
		masterGroup.clearActions();
		masterGroup.addAction(Actions.moveTo(-fullZoneBox.width - 100, 0, .25f));
		hud_clearIndicator();
		activeHud = 1;
		hud_showIndicator();
	}

	/* [U] */
	public void moveTo1To9() {
		masterGroup.clearActions();
		masterGroup.addAction(Actions.moveTo(0, 0, .25f));
		hud_clearIndicator();
		activeHud = 0;
		hud_showIndicator();
	}

	@Override
	public void render(final float delta) {
		super.render(delta);
	}

	@Override
	public void show() {
		showLevelPercents();
		showEnabledLevels();
		showLevelImages();
		showLevelNumbers();
		Gamepads.addListener(watcher);
		for (final Controller c : Gamepads.getControllers()) {
			watcher.connected(c);
		}
		showSelector = false;
		connectClickers();
		if (game.isTelevision()) {
			showSelector = true;
		}
		hud_showIndicator();
		super.show();
	}

	private void showEnabledLevels() {
		int ix;
		int current;
		float alpha;
		int panel;
		View3x3Selector viewPanel;

		for (ix = 0; ix < game.getLevels(); ix++) {
			panel = ix / 9;
			viewPanel = selectViewOverlay[panel];
			current = game.prefs.getLevelAccuracy(ix);
			if (isLevelUnlocked(ix)) {
				alpha = ((float) current - (float) minPercentAdvance) / (100f - minPercentAdvance);
				if (alpha < 0f) {
					alpha = 0f;
				}
				viewPanel.setImage(ix, didGood);
				viewPanel.setColor(ix, GameColor.MAIN_TEXT);
				viewPanel.setAlpha(ix, alpha);
			} else {
				viewPanel.setImage(ix, levelLockedPic);
				viewPanel.setColor(ix, Color.WHITE);
				viewPanel.setAlpha(ix, .85f);
			}

		}
	}

	private void showLevelImages() {
		int ix;
		String imageName;
		FileHandle imageFile;
		int panel;
		View3x3Selector viewPanel;

		for (ix = 0; ix < game.getLevels(); ix++) {
			panel = ix / 9;
			viewPanel = selectViewGraphic[panel];
			imageName = game.challenges.getLevelNameFor(ix);
			imageFile = game.challenges.nextImage(imageName);
			viewPanel.setImage(ix, imageFile);
		}
	}

	private void showLevelNumbers() {
		int ix;
		String imageName;
		int panel;
		View3x3Selector viewPanel;

		for (ix = 0; ix < game.getLevels(); ix++) {
			panel = ix / 9;
			viewPanel = selectViewLevelIndicator[panel];
			imageName = "images/bgnumbers/bg_" + (ix + 1) + ".png";
			viewPanel.setImage(ix, Gdx.files.internal(imageName));
			viewPanel.setAlpha(ix, .7f);
		}
	}

	private void showLevelPercents() {
		int ix;
		int percent;
		Label label;
		Rectangle bbox;
		int panel;
		View3x3Selector viewPanel;

		for (ix = 0; ix < game.getLevels(); ix++) {
			panel = ix / 9;
			viewPanel = selectViewLevelIndicator[panel];
			bbox = viewPanel.getBoundingBox(ix);
			percent = game.prefs.getLevelAccuracy(ix);
			label = btn_labels[panel][ix % 9];
			label.setText("Correct: " + percent + "%");
			label.pack();
			label.setX(bbox.x + (bbox.width - label.getWidth()) / 2);
			label.setY(bbox.y);
		}
	}

	private void startGameLevel(final int level) {
		if (!isLevelUnlocked(level)) {
			game.sm.playEffect("buzzer2");
			return;
		}
		game.setLevelOn(level);
		game.gameEvent(GameEvent.GAMEBOARD);
	}
}
