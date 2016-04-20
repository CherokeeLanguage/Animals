package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.cherokeelessons.vocab.animals.one.GameEvent.EventList;
import com.cherokeelessons.vocab.animals.one.IAP.Callback;
import com.cherokeelessons.vocab.animals.one.View3x3Selector.onClick;

public class ScreenLevelSelect extends ScreenGameCore {

	private int activeHud = 0;

	private Label[][] btn_labels = new Label[game.levels / 9][9];
	FileHandle button_highlight = Gdx.files
			.internal("buttons/square_white.png");
	private FileHandle didGood = Gdx.files
			.internal("buttons/checkmarkfat_white.png");
	private BitmapFont font;

	private Color fontColor = GameColor.GREEN;

	private int fontSize = 44;

	private int level_highlighted = 0;
	private Group masterGroup = new Group();
	private int minPercentAdvance = 80;
	private FileHandle levelLockedPic = Gdx.files
			.internal("buttons/Padlock-red.png");
	private FileHandle levelNotPurchasedPic = Gdx.files.internal("buttons/credit-card-front.png");
	private Label[] panelSwitch = new Label[2];

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
	private String title_unlocked = "This level is unlocked. Press [O] to play.";
	private String title_locked = "This level is locked until you get at least "+minPercentAdvance+"%+ on the previous level.";
	private String title_premium = "This is premium content. Press [O] to purchase the full game.";

	private Callback update_select_display=new Callback() {		
		@Override
		public void onSuccess(final String success) {
			Gdx.app.postRunnable(new Runnable() {				
				@Override
				public void run() {
					showEnabledLevels();
					game.getSoundManager().playEffect("osda");
					hud_showIndicator();
					Gdx.app.log(Gdx.app.getClass().getCanonicalName(), success!=null?success:"");
				}
			});
		}
		
		@Override
		public void onFailure(int errorCode, String errorMessage) {
		}
		
		@Override
		public void onCancel() {
		}
	};

	public ScreenLevelSelect(CherokeeAnimals game) {
		super(game);		
		int ix;
		LabelStyle ls = new LabelStyle();
		LabelStyle ps = new LabelStyle();
		int panel;
		float bottomMargin;

		minPercentAdvance = game.getMinPercentAdvance();

		font = CherokeeAnimals.getFont(CherokeeAnimals.FontStyle.Script,fontSize);
		ls.font = font;
		ls.fontColor = new Color(fontColor);
		ls.fontColor.a = 1f;

		ps.font = font;
		ps.fontColor = new Color(GameColor.GREEN);
		ps.fontColor.a = 1f;
		panelSwitch[0] = new Label(
				"DPAD - Navigate, [O] - Select, [Y] - Show Levels 10-18, [A] - Main Menu", ps);
		panelSwitch[1] = new Label("DPAD - Navigate, [O] - Select, [U] - Show Levels 1-9, [A] - Main Menu",
				ps);
		panelSwitch[0].pack();
		panelSwitch[1].pack();
		panelSwitch[0].setX(overscan.x
				+ (overscan.width - panelSwitch[0].getWidth()) / 2);
		panelSwitch[0].setY(overscan.y);
		panelSwitch[1].setX(overscan.x
				+ (overscan.width - panelSwitch[0].getWidth()) / 2);
		panelSwitch[1].setY(overscan.y);
		bottomMargin = panelSwitch[0].getHeight();

		selectViewLevelIndicator = new View3x3Selector[2];
		selectViewLevelIndicator[0] = new View3x3Selector(overscan);
		selectViewLevelIndicator[0].setPosition(overscan.x, overscan.y);
//		selectViewLevelIndicator[0].setTitle(title);
		selectViewLevelIndicator[0].setBottomMargin(bottomMargin);

		selectViewLevelIndicator[1] = new View3x3Selector(overscan);
		selectViewLevelIndicator[1].setPosition(overscan.x, overscan.y);
//		selectViewLevelIndicator[1].setTitle(title);
		selectViewLevelIndicator[1].setBottomMargin(bottomMargin);

		selectViewGraphic = new View3x3Selector[2];
		selectViewGraphic[0] = new View3x3Selector(overscan);
//		selectViewGraphic[0].setTitle(title);
		selectViewGraphic[0].setBottomMargin(bottomMargin);
		selectViewGraphic[0].setPosition(overscan.x, overscan.y);

		selectViewGraphic[1] = new View3x3Selector(overscan);
//		selectViewGraphic[1].setTitle(title);
		selectViewGraphic[1].setBottomMargin(bottomMargin);
		selectViewGraphic[1].setPosition(overscan.x, overscan.y);

		selectViewOverlay = new View3x3Selector[2];
		selectViewOverlay[0] = new View3x3Selector(overscan);
//		selectViewOverlay[0].setTitle(title);
		selectViewOverlay[0].setBottomMargin(bottomMargin);
		selectViewOverlay[0].setPosition(overscan.x, overscan.y);
		selectViewOverlay[0].setHandler(startAtLevel_1_to_9);

		selectViewOverlay[1] = new View3x3Selector(overscan);
//		selectViewOverlay[1].setTitle(title);
		selectViewOverlay[1].setBottomMargin(bottomMargin);
		selectViewOverlay[1].setPosition(overscan.x, overscan.y);
		selectViewOverlay[1].setHandler(startAtLevel_10_to_18);

		selectViewHUD = new View3x3Selector[2];
		selectViewHUD[0] = new View3x3Selector(overscan);
		selectViewHUD[0].setTitle(title_unlocked);
		selectViewHUD[0].setBottomMargin(bottomMargin);
		selectViewHUD[0].setPosition(overscan.x, overscan.y);
		selectViewHUD[0].setHandler(startAtLevel_1_to_9);

		selectViewHUD[1] = new View3x3Selector(overscan);
		selectViewHUD[1].setTitle(title_unlocked);
		selectViewHUD[1].setBottomMargin(bottomMargin);
		selectViewHUD[1].setPosition(overscan.x, overscan.y);
		selectViewHUD[1].setHandler(startAtLevel_10_to_18);

		selectViewLevelIndicator[0].setBoxMargin(4);
		selectViewGraphic[0].setBoxMargin(24);

		selectViewLevelIndicator[1].setBoxMargin(4);
		selectViewGraphic[1].setBoxMargin(24);

		selectViewOverlay[0].setBoxMargin(24);
		selectViewOverlay[1].setBoxMargin(24);

		for (ix = 0; ix < game.levels; ix++) {
			panel = ix / 9;
			btn_labels[panel][ix % 9] = new Label("", ls);
			selectViewLevelIndicator[panel].addActor(btn_labels[panel][ix % 9]);
		}

		/*
		 * move the second set off screen to the right..
		 */
		final int extra_offset = 100;
		selectViewLevelIndicator[1].setX(screenWidth
				+ selectViewLevelIndicator[1].getX() + extra_offset);
		selectViewGraphic[1].setX(screenWidth + selectViewGraphic[1].getX()
				+ extra_offset);
		selectViewOverlay[1].setX(screenWidth + selectViewOverlay[1].getX()
				+ extra_offset);
		selectViewHUD[1].setX(screenWidth + selectViewHUD[1].getX()
				+ extra_offset);
		panelSwitch[1].setX(screenWidth + panelSwitch[1].getX() + extra_offset);

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

	@Override
	public void hide() {
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
		masterGroup.addAction(Actions.moveTo(-screenWidth - 100, 0, .25f));
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
		hud_showIndicator();		
	}

	private void showEnabledLevels() {
		int ix;
		int current;
		float alpha;
		int panel;
		View3x3Selector viewPanel;

		for (ix = 0; ix < game.levels; ix++) {
			panel = ix / 9;
			viewPanel = selectViewOverlay[panel];
			current = game.getLevelAccuracy(ix);
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
		game.getSoundManager().playEffect("box_moved");
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
				selectViewHUD[activeHud].setTitle(title_premium);
				break;
			}
			if (!isLevelUnlocked(level)) {
				selectViewHUD[activeHud].setTitle(title_locked);
				break;
			}
			selectViewHUD[activeHud].setTitle(title_unlocked);
			break;
		} while(false);
	}

	private void showLevelImages() {
		int ix;
		String imageName;
		FileHandle imageFile;
		int panel;
		View3x3Selector viewPanel;

		for (ix = 0; ix < game.animalQueue.getLevelCount(); ix++) {
			panel = ix / 9;
			viewPanel = selectViewGraphic[panel];
			imageName = game.animalQueue.getLevelStartName(ix);
			imageFile = game.randomAnimalImageByName(imageName);
			viewPanel.setImage(ix, imageFile);
		}
	}

	private void showLevelNumbers() {
		int ix;
		String imageName;
		int panel;
		View3x3Selector viewPanel;

		for (ix = 0; ix < game.animalQueue.getLevelCount(); ix++) {
			panel = ix / 9;
			viewPanel = selectViewLevelIndicator[panel];
			imageName = "images/backgrounds/bg_" + (ix + 1) + ".png";
			if (Gdx.files.internal(imageName).exists()) {
				viewPanel.setImage(ix, Gdx.files.internal(imageName));
			}
		}
	}

	private void showLevelPercents() {
		int ix;
		int percent;
		Label label;
		Rectangle bbox;
		int panel;
		View3x3Selector viewPanel;

		for (ix = 0; ix < game.levels; ix++) {
			panel = ix / 9;
			viewPanel = selectViewLevelIndicator[panel];
			bbox = viewPanel.getBoundingBox(ix);
			percent = game.getLevelAccuracy(ix);
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
			game.getSoundManager().playEffect("buzzer2");
			return;
		}
		game.setGameScreenLevel(level + 1);
		game.event(EventList.ShowGameBoard);
	}

	private boolean isLevelPurchased(int level) {
		return level<3 || game.getIap().isPurchased();
	}

	private boolean isLevelUnlocked(int level) {
		return !(level > 0 && game.getLevelAccuracy(level) == 0
				&& game.getLevelAccuracy(level - 1) < minPercentAdvance);
	}
}
