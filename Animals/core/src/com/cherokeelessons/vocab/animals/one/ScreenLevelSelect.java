package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cherokeelessons.common.FontGenerator;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Gamepads;
import com.cherokeelessons.common.IAP.Callback;
import com.cherokeelessons.common.OS;
import com.cherokeelessons.vocab.animals.one.enums.GameEvent;
import com.cherokeelessons.vocab.animals.one.views.View3x3Selector;
import com.cherokeelessons.vocab.animals.one.views.View3x3Selector.onClick;


public class ScreenLevelSelect extends GameScreen {

	private static final String OUYA_PANEL1_TEXT = "DPAD - Navigate," +
			" [O] - Select," +
			" [Y] - Show Levels 10-18," +
			" [A] - Main Menu";
	
	private static final String TAB_PANEL1_TEXT = "[Tap Here to Show Levels 10-18]";
	
	private static final String OUYA_PANEL2_TEXT = "DPAD - Navigate," +
			" [O] - Select," +
			" [U] - Show Levels 1-9," +
			" [A] - Main Menu";

	private static final String TAB_PANEL2_TEXT = "[Tap Here to Show Levels 1-9]";

	private int activeHud = 0;

	private Label[][] btn_labels = new Label[game.getLevels() / 9][9];
	FileHandle button_highlight = Gdx.files
			.internal("buttons/2610_white.png");
	private FileHandle didGood = Gdx.files
			.internal("buttons/2714_white.png");
	private BitmapFont font;

	private Color fontColor = GameColor.GREEN;

	private int fontSize = 44;

	private int level_highlighted = 0;
	private Group masterGroup = new Group();
	private int minPercentAdvance = 80;
	private FileHandle levelLockedPic = Gdx.files
			.internal("images/indicators/Padlock-red.png");
	private FileHandle levelNotPurchasedPic = Gdx.files.internal("images/indicators/credit-card-front.png");
	private Label[] panelSwitch;

	private View3x3Selector[] selectViewGraphic;

	private View3x3Selector[] selectViewHUD;

	private View3x3Selector[] selectViewLevelIndicator;

	private View3x3Selector[] selectViewOverlay;
	private View3x3Selector.onClick startAtLevel_1_to_9 = new onClick() {
		@Override
		public void handleClick(int level) {
			startGameLevel(level);
		}
	};
	private View3x3Selector.onClick startAtLevel_10_to_18 = new onClick() {
		@Override
		public void handleClick(int level) {
			startGameLevel(level + 9);
		}
	};
	
	private String ouya_title_unlocked = "This level is unlocked. Press [O] to play.";
	private String ouya_title_locked = "This level is locked until you get at least "+minPercentAdvance+"%+ on the previous level.";
	private String ouya_title_premium = game.getIap().isDemo() ? "Not available in the demo version." : "This is premium content. Press [O] to purchase the full game.";
	
	private String tab_title_unlocked = "This level is unlocked. Tap to play.";
	private String tab_title_locked = "This level is locked until you get at least "+minPercentAdvance+"%+ on the previous level.";
	private String tab_title_premium = game.getIap().isDemo() ? "Not available in the demo version." : "This is premium content. Tap to purchase the full game.";
	
	final private ControllerLevelSelect_Watch watcher = new ControllerLevelSelect_Watch(
			this);

	private Callback update_select_display=new Callback() {		
		@Override
		public void onSuccess(final String success) {
			Gdx.app.postRunnable(new Runnable() {				
				@Override
				public void run() {
					showEnabledLevels();
					game.sm.playEffect("cash_out");
					hud_showIndicator();
				}
			});
		}
		
		@Override
		public void onFailure(int errorCode, String errorMessage) {
			game.sm.playEffect("growl_0");
		}
		
		@Override
		public void onCancel() {
		}
	};

	private int panelCount;

	private boolean isOuya=false;
	public ScreenLevelSelect(CherokeeAnimals game) {
		super(game);
		isOuya=OS.Platform.Ouya.equals(OS.platform);
		panelCount=(int)Math.ceil(game.getLevels()/9);
		int ix;
		LabelStyle ls = new LabelStyle();
		LabelStyle ps = new LabelStyle();
		int panel;
		float bottomMargin;
		FontGenerator fg = new FontGenerator();

		panelSwitch = new Label[panelCount];
		
		minPercentAdvance = game.getMinPercent();

		font = fg.gen(fontSize);
		ls.font = font;
		ls.fontColor = new Color(fontColor);
		ls.fontColor.a = 1f;

		ps.font = font;
		ps.fontColor = new Color(GameColor.GREEN);
		ps.fontColor.a = 1f;
		panelSwitch[0] = new Label(isOuya?OUYA_PANEL1_TEXT:TAB_PANEL1_TEXT, ps);
		panelSwitch[1] = new Label(isOuya?OUYA_PANEL2_TEXT:TAB_PANEL2_TEXT,	ps);
		panelSwitch[0].pack();
		panelSwitch[1].pack();
		panelSwitch[0].setX((screenSize.width - panelSwitch[0].getWidth()) / 2);
		panelSwitch[0].setY(0);
		panelSwitch[1].setX((screenSize.width - panelSwitch[0].getWidth()) / 2);
		panelSwitch[1].setY(0);
		panelSwitch[0].addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				moveTo10To18();
				return true;
			}
		});
		panelSwitch[1].addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				moveTo1To9();
				return true;
			}
		});
		bottomMargin = panelSwitch[0].getHeight();

		selectViewLevelIndicator = new View3x3Selector[panelCount];
		selectViewLevelIndicator[0] = new View3x3Selector(screenSize);
		selectViewLevelIndicator[0].setTouchable(Touchable.enabled);
		selectViewLevelIndicator[0].setBottomMargin(bottomMargin);

		selectViewLevelIndicator[1] = new View3x3Selector(screenSize);
		selectViewLevelIndicator[1].setTouchable(Touchable.enabled);
		selectViewLevelIndicator[1].setBottomMargin(bottomMargin);

		selectViewGraphic = new View3x3Selector[panelCount];
		selectViewGraphic[0] = new View3x3Selector(screenSize);
		selectViewGraphic[0].setTouchable(Touchable.disabled);
		selectViewGraphic[0].setBottomMargin(bottomMargin);

		selectViewGraphic[1] = new View3x3Selector(screenSize);
		selectViewGraphic[1].setTouchable(Touchable.disabled);
		selectViewGraphic[1].setBottomMargin(bottomMargin);

		selectViewOverlay = new View3x3Selector[panelCount];
		selectViewOverlay[0] = new View3x3Selector(screenSize);
		selectViewOverlay[0].setTouchable(Touchable.disabled);
		selectViewOverlay[0].setBottomMargin(bottomMargin);
		selectViewOverlay[0].setHandler(startAtLevel_1_to_9);

		selectViewOverlay[1] = new View3x3Selector(screenSize);
		selectViewOverlay[1].setTouchable(Touchable.disabled);
		selectViewOverlay[1].setBottomMargin(bottomMargin);
		selectViewOverlay[1].setHandler(startAtLevel_10_to_18);

		selectViewHUD = new View3x3Selector[panelCount];
		selectViewHUD[0] = new View3x3Selector(screenSize);
		selectViewHUD[0].setTouchable(Touchable.disabled);
		selectViewHUD[0].setTitle(isOuya?ouya_title_unlocked:tab_title_unlocked);
		selectViewHUD[0].setBottomMargin(bottomMargin);
		selectViewHUD[0].setHandler(startAtLevel_1_to_9);

		selectViewHUD[1] = new View3x3Selector(screenSize);
		selectViewHUD[1].setTouchable(Touchable.disabled);
		selectViewHUD[1].setTitle(isOuya?ouya_title_unlocked:tab_title_unlocked);
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
		selectViewLevelIndicator[1].setX(fullscan.width
				+ selectViewLevelIndicator[1].getX() + extra_offset);
		selectViewGraphic[1].setX(fullscan.width + selectViewGraphic[1].getX()
				+ extra_offset);
		selectViewOverlay[1].setX(fullscan.width + selectViewOverlay[1].getX()
				+ extra_offset);
		selectViewHUD[1].setX(fullscan.width + selectViewHUD[1].getX()
				+ extra_offset);
		panelSwitch[1].setX(fullscan.width + panelSwitch[1].getX() + extra_offset);

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

	private void disconnectClickers() {
		for (int ia = 0; ia < 2; ia++) {
			for (int ib = 0; ib < selectViewLevelIndicator[ia].button_count(); ib++) {
				selectViewLevelIndicator[ia].clearListeners(ib);
			}
		}
	}
	
	private void connectClickers() {
		for (int ia = 0; ia < 2; ia++) {
			final int pnl=ia;
			for (int ib = 0; ib < selectViewLevelIndicator[ia].button_count(); ib++) {
				final int btn=ib;
				selectViewLevelIndicator[ia].addListener(btn, new ClickListener(){
					@Override
					public boolean touchDown(InputEvent event, float x,
							float y, int pointer, int button) {
						activeHud=pnl;
						level_highlighted=btn;
						hud_showIndicator(true);
						hud_selectLevel();
						return true;
					}
				});
			}
		}
	}

	@Override
	public void hide() {
		for (Controller controller : Gamepads.getControllers()) {
			watcher.disconnected(controller);
		}
		Gamepads.clearListeners();
		disconnectClickers();
		super.hide();
	}

	private void hud_clearIndicator() {
		View3x3Selector activehud = selectViewHUD[activeHud];
		for (int ix = 0; ix < activehud.button_count(); ix++) {
			activehud.setImage(ix, null);
		}
	}

	public void hud_moveLeft() {
		do {
			if (level_highlighted == 0) {
				break;
			}
			if (level_highlighted == 3) {
				break;
			}
			if (level_highlighted == 6) {
				break;
			}
			level_highlighted--;
			hud_showIndicator();
		} while (false);
	}

	public void hud_moveNorth() {
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
		do {
			if (level_highlighted == 2) {
				break;
			}
			if (level_highlighted == 5) {
				break;
			}
			if (level_highlighted == 8) {
				break;
			}
			level_highlighted++;
			hud_showIndicator();
		} while (false);
	}

	public void hud_moveSouth() {
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

	public void hud_selectLevel() {
		do {
			if (activeHud == 0) {
				startAtLevel_1_to_9.handleClick(level_highlighted);
				break;
			}
			startAtLevel_10_to_18.handleClick(level_highlighted);
		} while (false);
	}

	/* [Y] */
	public void moveTo10To18() {
		masterGroup.clearActions();
		masterGroup.addAction(Actions.moveTo(-fullscan.width - 100, 0, .25f));
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
	public void show() {
		super.show();
		showLevelPercents();
		showEnabledLevels();
		showLevelImages();
		showLevelNumbers();
		Gamepads.addListener(watcher);
		for (Controller c : Gamepads.getControllers()) {
			watcher.connected(c);
		}
		hud_showIndicator();
		connectClickers();
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
			if (isLevelPurchased(ix)) {
				if (isLevelUnlocked(ix)) {
					alpha = ((float) current - (float) minPercentAdvance)
							/ (100f - (float) minPercentAdvance);
					if (alpha < 0f)
						alpha = 0f;
					viewPanel.setImage(ix, didGood);
					viewPanel.setColor(ix, GameColor.GREEN);
					viewPanel.setAlpha(ix, alpha);
				} else {
					viewPanel.setImage(ix, levelLockedPic);
					viewPanel.setColor(ix, Color.WHITE);
					viewPanel.setAlpha(ix, .85f);
				}
			} else {
				viewPanel.setImage(ix, levelNotPurchasedPic);
				viewPanel.setColor(ix, Color.WHITE);
				viewPanel.setAlpha(ix, .85f);
			}
		}
	}

	private void hud_showIndicator() {
		hud_showIndicator(false);
	}
	private void hud_showIndicator(boolean quiet) {
		if (!quiet) {
			game.sm.playEffect("box_moved");
		}
		View3x3Selector activehud = selectViewHUD[activeHud];
		hud_clearIndicator();
		activehud.setImage(level_highlighted, button_highlight);
		activehud.setColor(level_highlighted, GameColor.GOLD2);
		activehud.setAlpha(level_highlighted, .7f);
		Color gold3_50 = new Color(GameColor.GOLD3);
		gold3_50.a = .7f;
		Color gold2_50 = new Color(GameColor.GOLD2);
		gold2_50.a = .7f;
		Action act_gold3 = Actions.color(gold3_50, 1f, Interpolation.sine);
		Action act_gold2 = Actions.color(gold2_50, 1f, Interpolation.sine);
		Action act_seq = Actions.sequence(act_gold3, act_gold2);
		Action act = Actions.forever(act_seq);
		activehud.addAction(level_highlighted, act);
		int level = activeHud*activehud.button_count()+level_highlighted;
		do {
			if (!isLevelPurchased(level)) {
				selectViewHUD[activeHud].setTitle(isOuya?ouya_title_premium:tab_title_premium);
				break;
			}
			if (!isLevelUnlocked(level)) {
				selectViewHUD[activeHud].setTitle(isOuya?ouya_title_locked:tab_title_locked);
				break;
			}
			selectViewHUD[activeHud].setTitle(isOuya?ouya_title_unlocked:tab_title_unlocked);
			break;
		} while(false);
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

	private void startGameLevel(int level) {
		if (!isLevelPurchased(level)) {
			game.getIap().purchaseGame(update_select_display);
			return;
		}
		if (!isLevelUnlocked(level)) {
			game.sm.playEffect("buzzer2");
			return;
		}
		game.setLevelOn(level);
		game.gameEvent(GameEvent.ShowGameBoard);
	}

	private boolean isLevelPurchased(int level) {
		return level<3 || game.getIap().isPurchased();
	}

	private boolean isLevelUnlocked(int level) {
		return !(level > 0 && game.prefs.getLevelAccuracy(level) == 0
				&& game.prefs.getLevelAccuracy(level - 1) < minPercentAdvance);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		gameStage.draw();
	}
}
