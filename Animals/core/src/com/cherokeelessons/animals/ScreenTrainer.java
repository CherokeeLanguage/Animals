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
	
	@Override
	public boolean dpad(int keyCode) {
		switch (keyCode) {
		case Keys.DPAD_CENTER:
			doSkipTraining();
			return true;
		}
		return false;
	}
	
	private ControllerAdapter skipTraining = new ControllerAdapter() { 
		@Override
		public boolean buttonDown(Controller controller, int buttonCode) {
			doSkipTraining();
			return true;
		}
	};

	private GamepadAdapter<ScreenTrainer> watcher = new GamepadAdapter<ScreenTrainer>(this) {
		@Override
		public ControllerListener factoryControllerListener(GamepadMap map, ScreenTrainer menu) {
			return skipTraining;
		}
	};

	private ViewChallengeBoard writtenChallenge;

	public ScreenTrainer(final CherokeeAnimals game) {
		super(game);
		FontLoader fg = new FontLoader();
		BitmapFont font;
		Integer fontSize = 48;

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
		lbl_exitInfo.setX(screenSize.width - lbl_exitInfo.getWidth());
		lbl_exitInfo.setY(screenSize.height - lbl_exitInfo.getHeight());
		lbl_exitInfo.pack();
		lbl_exitInfo.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				game.gameEvent(GameEvent.Done);
				return true;
			}
		});

		pictureChallenge = new View3x3Selector(screenSize);
		pictureChallenge.setBoxMargin(4);
		pictureChallenge.setTitle("TRAINING");

		writtenChallenge = new ViewChallengeBoard(screenSize);
		writtenChallenge.setDisplayText("New Challenge");

		gameStage.addActor(writtenChallenge);
		gameStage.addActor(pictureChallenge);
		gameStage.addActor(lbl_exitInfo);

		alreadyShown = new HashSet<FileHandle>();
	}

	private void doSkipTraining() {
		game.sm.playEffect("menu-click");
		while (currentIX < 9) {
			loadNextPic();
		}
		elapsedTime = interval;
	}

	@Override
	public void hide() {
		for (Controller controller : Gamepads.getControllers()) {
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
	public void render(float delta) {
		super.render(delta);
		gameStage.draw();
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
		if (currentIX == 3
				&& game.prefs.getTrainingMode()
						.equals(TrainingMode.Brief)) {
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
		gameStage.getRoot().setX(screenSize.x);
		gameStage.getRoot().setY(screenSize.y);
		Gamepads.addListener(watcher);
		for (Controller c : Gamepads.getControllers()) {
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
