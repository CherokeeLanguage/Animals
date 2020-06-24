package com.cherokeelessons.animals;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.animals.enums.TrainingMode;
import com.cherokeelessons.animals.views.View3x3Selector;
import com.cherokeelessons.animals.views.ViewChallengeBoard;
import com.cherokeelessons.common.FontLoader;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.GamepadMap;
import com.cherokeelessons.common.Gamepads;
import com.cherokeelessons.common.Utils;

public class ScreenTrainer extends GameScreen implements DpadInterface {

	private Set<FileHandle> alreadyShown;

	private LabelStyle buttonStyle;

	private String currentChallenge = "";

	private int currentIX = 0;

	private boolean doNextPic = false;
	private float elapsedTime = 0;
	final private float interval = 2.5f;
	private Label lbl_exitInfo;
	private View3x3Selector pictureChallenge;

	private final ControllerAdapter skipTraining = new ControllerAdapter() {
		@Override
		public boolean buttonDown(final Controller controller, final int buttonCode) {
			doSkipTraining();
			return true;
		}
	};

	private final GamepadAdapter<ScreenTrainer> watcher = new GamepadAdapter<ScreenTrainer>(this) {
		@Override
		public ControllerListener factoryControllerListener(final GamepadMap map, final ScreenTrainer menu) {
			return skipTraining;
		}
	};

	private ViewChallengeBoard writtenChallenge;

	public ScreenTrainer(final CherokeeAnimals game) {
		super(game);
		final FontLoader fg = new FontLoader();
		BitmapFont font;
		final Integer fontSize = 48;

		font = fg.getFixed(fontSize);

		buttonStyle = new LabelStyle();
		buttonStyle.font = font;
		buttonStyle.fontColor = GameColor.MAIN_TEXT;

		String textSkip;
		if (game.isTelevision() || watcher.hasControllers()) {
			textSkip = "[FIRE] or [ENTER] to skip.";
		} else {
			textSkip = "[SKIP]";
		}
		lbl_exitInfo = new Label(textSkip, buttonStyle);
		lbl_exitInfo.setTouchable(Touchable.enabled);
		lbl_exitInfo.setX(safeZoneSize.width - lbl_exitInfo.getWidth());
		lbl_exitInfo.setY(safeZoneSize.height - lbl_exitInfo.getHeight());
		lbl_exitInfo.pack();
		lbl_exitInfo.addListener(new ClickListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
					final int button) {
				game.gameEvent(GameEvent.Done);
				return true;
			}
		});

		pictureChallenge = new View3x3Selector(safeZoneSize);
		pictureChallenge.setBoxMargin(4);
		pictureChallenge.setTitle("TRAINING");

		writtenChallenge = new ViewChallengeBoard(safeZoneSize);
		writtenChallenge.setDisplayText("New Challenge");

		gameStage.addActor(writtenChallenge);
		gameStage.addActor(pictureChallenge);
		gameStage.addActor(lbl_exitInfo);

		alreadyShown = new HashSet<>();
	}

	private void doSkipTraining() {
		game.sm.playEffect("menu-click");
		while (currentIX < 9) {
			loadNextPic();
		}
		elapsedTime = interval;
	}

	@Override
	public boolean dpad(final int keyCode) {
		switch (keyCode) {
		case Keys.DPAD_CENTER:
			doSkipTraining();
			return true;
		}
		return false;
	}

	@Override
	public void hide() {
		for (final Controller controller : Gamepads.getControllers()) {
			watcher.disconnected(controller);
		}
		Gamepads.clearListeners();
		super.hide();
	}

	private void loadNextPic() {
		FileHandle newPic;
		newPic = game.challenges.nextImage(currentChallenge);
		alreadyShown.add(newPic);
		pictureChallenge.setImage(currentIX, newPic);
		currentIX++;
	}

	@Override
	public void render(final float delta) {
		super.render(delta);
		if (doNextPic) {
			loadNextPic();
			pictureChallenge.focusOn(currentIX - 1);
			doNextPic = false;
			elapsedTime = 0;
			return;
		}
		elapsedTime += delta;
		if (elapsedTime < interval && currentIX > 0) {
			return;
		}
		if (game.sm.isChallengePlaying(currentChallenge)) {
			return;
		}
		if (currentIX == 3 && game.prefs.getTrainingMode().equals(TrainingMode.Brief)) {
			doSkipTraining();
		}
		if (currentIX == 9) {
			currentIX++;
			game.sm.playEffect("ding-ding-ding");
			elapsedTime = 0;
			pictureChallenge.unFocusOn();
			pictureChallenge.scatter();
			lbl_exitInfo.setVisible(false);
			pictureChallenge.setTitle(" ");
			writtenChallenge.setVisible(false);
			return;
		}
		if (currentIX == 10) {
			game.gameEvent(GameEvent.Done);
			return;
		}
		game.sm.playChallenge(currentChallenge);
		doNextPic = true;
	}

	public void reset() {
		int ix;
		for (ix = 0; ix < 9; ix++) {
			pictureChallenge.setImage(ix, null);
		}
		elapsedTime = 0;
		currentIX = 0;
		currentChallenge = game.activeChallenge;
		alreadyShown.clear();
		lbl_exitInfo.setVisible(true);
		pictureChallenge.setTitle("NEW CHALLENGE");
		writtenChallenge.setVisible(true);
	}

	@Override
	public void show() {
		super.show();
		if (!currentChallenge.equals(game.activeChallenge)) {
			reset();
		}
		updateChallengeBoard();
		gameStage.getRoot().setX(safeZoneSize.x);
		gameStage.getRoot().setY(safeZoneSize.y);
		Gamepads.addListener(watcher);
		for (final Controller c : Gamepads.getControllers()) {
			watcher.connected(c);
		}
	}

	private void updateChallengeBoard() {
		String challenge;
		switch (game.prefs.getChallengeMode()) {
		case Latin:
			challenge = Utils.asLatin(currentChallenge);
			break;
		case Syllabary:
			challenge = Utils.asSyllabary(currentChallenge);
			break;
		default:
			challenge = "";
			break;
		}
		writtenChallenge.setDisplayText(challenge);
	}
}
