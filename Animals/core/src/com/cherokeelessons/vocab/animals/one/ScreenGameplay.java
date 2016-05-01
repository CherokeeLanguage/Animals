package com.cherokeelessons.vocab.animals.one;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.common.FontLoader;
import com.cherokeelessons.common.GameColor;
import com.cherokeelessons.common.Gamepads;
import com.cherokeelessons.common.Utils;
import com.cherokeelessons.vocab.animals.one.enums.GameEvent;
import com.cherokeelessons.vocab.animals.one.enums.TrainingMode;
import com.cherokeelessons.vocab.animals.one.views.ViewChallengeBoard;
import com.cherokeelessons.vocab.animals.one.views.ViewGameBoard;
import com.cherokeelessons.vocab.animals.one.views.ViewInGameControls;
import com.cherokeelessons.vocab.animals.one.views.ViewProgressBar;
import com.cherokeelessons.vocab.animals.one.views.ViewScoreBoard;

public class ScreenGameplay extends GameScreen {
	
	private float boardElapsed=0;

	enum WritingMode {
		Latin, None, Syllabary
	}

	private ViewGameBoard activehud;

	private HashSet<String> alreadySeen = new HashSet<String>();

	private int badPoints = 0;
	FileHandle button_highlight = Gdx.files.internal("buttons/2610_white.png");
	private String[] buttonPicture = new String[6];
	private String[] buttonPictureCopy = new String[buttonPicture.length];
	private ViewChallengeBoard challengeBoard;
	private String currentChallenge;
	private float elapsedTime = 0;
	private int end;

	private float failsafeTimer = 0;
	private FileHandle fat_check = Gdx.files.internal("buttons/2714_white.png");
	private FileHandle fat_x = Gdx.files.internal("buttons/2716_white.png");
	private ViewGameBoard gameBoard;

	private ViewInGameControls gameControls;

	private int goodPoints = 0;

	private int ip = 0;

	private int item_highlighted = 0;
	private int markedCorrect;

	private int markedWrong;

	private boolean nextScreen = false;

	private boolean noTick = false;

	ViewProgressBar pbar;

	private boolean repeatAudio = true;
	private FileHandle[] savedPictureFH = new FileHandle[buttonPicture.length];

	private ViewScoreBoard scoreBoard;

	private int start;

	private float timeLimit = 5;

	final private ControllerGamePlay_Watch watcher = new ControllerGamePlay_Watch(
			this);

	private WritingMode writingMode;

	private List<String> queue;

	private Texture pause_texture;

	public ScreenGameplay(final CherokeeAnimals game) {
		super(game);

		pbar = new ViewProgressBar(screenSize);
		scoreBoard = new ViewScoreBoard(screenSize, game.sm);
		gameControls = new ViewInGameControls(screenSize);
		gameBoard = new ViewGameBoard(screenSize);
		challengeBoard = new ViewChallengeBoard(screenSize);
		activehud = new ViewGameBoard(screenSize);
		activehud.setTouchable(Touchable.disabled);

		gameStage.addActor(pbar);
		gameStage.addActor(scoreBoard);
		gameStage.addActor(gameControls);
		gameStage.addActor(gameBoard);
		gameStage.addActor(activehud);
		gameStage.addActor(challengeBoard);
		gameStage.addActor(pauseOverlay);
		
		pauseOverlay.setX(-screenSize.x);
		pauseOverlay.setY(-screenSize.y);
		
		setPaused(false);

	}
	
	private Pixmap pause_mask;

	@Override
	public void setPaused(boolean isPaused) {
		super.setPaused(isPaused);
		if (isPaused()) {
			pauseOverlay.getColor().a = .1f;
			pauseOverlay.setTouchable(Touchable.enabled);
			game.musicPlayer.pause();
		} else {
			pauseOverlay.getColor().a = 0f;
			pauseOverlay.setTouchable(Touchable.disabled);
			game.musicPlayer.resume();
		}
	}

	private void buttonAsCorrect(int button) {
		buttonPicture[button] = "";
		gameBoard.setImage(button, fat_check);
		gameBoard.setColor(button, GameColor.MAIN_TEXT);
		markedCorrect++;
	}

	private void buttonAsWrong(int button) {
		buttonPicture[button] = "";
		gameBoard.setImage(button, fat_x);
		gameBoard.setColor(button, GameColor.FIREBRICK);
		markedWrong++;
	}

	private void checkButton(int button) {
		if (buttonPicture[button] == null || buttonPicture[button].equals("")) {
			return;
		}
		int bonus = 0;
		elapsedTime = 0;
		if (!buttonPicture[button].equals(currentChallenge)) {
			buttonAsWrong(button);
			scoreBoard.changeScoreBy(-badPoints - 1);
			badPoints = (badPoints + 1) % 5;
			goodPoints = 0;
			game.sm.playEffect("buzzer2");
			return;
		}
		buttonAsCorrect(button);
		gameBoard.spin(button);
		scoreBoard.changeScoreBy(goodPoints + 1);
		badPoints = 0;
		goodPoints = (goodPoints + 1) % 5;
		if (getCorrectCount() < 1) {
			for (button = 0; button < buttonPicture.length; button++) {
				if (!buttonPicture[button].equals("")) {
					bonus++;
					buttonPicture[button] = "";
					scoreBoard.changeScoreBy(bonus);
					buttonPicture[button] = "";
					gameBoard.flyaway(button);
				} else if (buttonPictureCopy[button].equals(currentChallenge)) {
					gameBoard.setImage(button, savedPictureFH[button]);
					gameBoard.spin(button);
				}
			}
			elapsedTime = 0;
			nextScreen = true;
			ip++;
			updateProgress2();
			pbar.setProgress1(0f);
		}
	}

	private void dropWrongAnswer() {
		int ix = 0;
		Random r = new Random();
		/*
		 * do we have any wrong answers left to drop?
		 */
		if (getWrongCount() < 1) {
			return;
		}
		while (true) {
			ix = r.nextInt(buttonPicture.length);
			if (buttonPicture[ix].equals(currentChallenge)) {
				continue;
			}
			if (buttonPicture[ix].equals("")) {
				continue;
			}
			break;
		}
		buttonAsWrong(ix);
		badPoints = (badPoints + 2) % 5;
		goodPoints = 0;
		scoreBoard.changeScoreBy(-badPoints - 1);
		switch (r.nextInt(3)) {
		case 0:
			game.sm.playEffect("alarm");
			break;
		case 1:
			game.sm.playEffect("bark");
			break;
		case 2:
			game.sm.playEffect("dialogerror");
			break;
		}
	}

	private int getCorrectCount() {
		int ix = 0, count = 0;
		for (ix = 0; ix < buttonPicture.length; ix++) {
			if (buttonPicture[ix] == null) {
				continue;
			}
			if (buttonPicture[ix].equals(currentChallenge)) {
				count++;
			}
		}
		return count;
	}

	public WritingMode getWritingMode() {
		return writingMode;
	}

	private int getWrongCount() {
		int ix = 0, count = 0;
		for (ix = 0; ix < buttonPicture.length; ix++) {
			if (buttonPicture[ix] == null) {
				continue;
			}
			if (buttonPicture[ix].equals(currentChallenge)) {
				continue;
			}
			if (buttonPicture[ix].equals("")) {
				continue;
			}
			count++;
		}
		return count;
	}

	@Override
	public void hide() {
		super.hide();
		pauseOverlay.clear();
		pause_texture.dispose();
		pause_mask.dispose();
		alreadySeen.add(currentChallenge);
		for (Controller controller : Gamepads.getControllers()) {
			watcher.disconnected(controller);
		}
		Gamepads.clearListeners();

		gameControls.clearListeners();
		disconnectClickhandlers();
	}

	private void hud_clearIndicator() {
		for (int ix = 0; ix < activehud.button_count(); ix++) {
			activehud.setImage(ix, null);
		}
	}

	public void hud_moveLeft() {
		do {
			if (item_highlighted == 0) {
				break;
			}
			if (item_highlighted == 3) {
				break;
			}
			item_highlighted--;
			hud_showIndicator();
		} while (false);
	}

	public void hud_moveNorth() {
		do {
			if (item_highlighted == 0) {
				break;
			}
			if (item_highlighted == 1) {
				break;
			}
			if (item_highlighted == 2) {
				break;
			}
			item_highlighted -= 3;
			hud_showIndicator();
		} while (false);
	}

	public void hud_moveRight() {
		do {
			if (item_highlighted == 2) {
				break;
			}
			if (item_highlighted == 5) {
				break;
			}
			item_highlighted++;
			hud_showIndicator();
		} while (false);
	}

	public void hud_moveSouth() {
		do {
			if (item_highlighted == 3) {
				break;
			}
			if (item_highlighted == 4) {
				break;
			}
			if (item_highlighted == 5) {
				break;
			}
			item_highlighted += 3;
			hud_showIndicator();
		} while (false);
	}

	public void hud_select() {
		checkButton(item_highlighted);
	}

	private void hud_setIndicator(int button) {
		item_highlighted = button;
	}

	private void hud_showIndicator() {
		hud_showIndicator(false);
	}

	private void hud_showIndicator(boolean quiet) {
		if (!quiet) {
			game.sm.playEffect("box_moved");
		}

		hud_clearIndicator();

		activehud.setImage(item_highlighted, button_highlight);
		activehud.setColor(item_highlighted, GameColor.GOLD2);
		activehud.setAlpha(item_highlighted, .7f);
		Color gold3_50 = new Color(GameColor.GOLD3);
		gold3_50.a = .7f;
		Color gold2_50 = new Color(GameColor.GOLD2);
		gold2_50.a = .7f;
		Action act_gold3 = Actions.color(gold3_50, 1f, Interpolation.sine);
		Action act_gold2 = Actions.color(gold2_50, 1f, Interpolation.sine);
		Action act_seq = Actions.sequence(act_gold3, act_gold2);
		Action act = Actions.forever(act_seq);
		activehud.addAction(item_highlighted, act);
	}

	public void initLevel(int level) {
		/*
		 * load up all entries from previous levels as "already seen"
		 */
		alreadySeen.clear();
		alreadySeen.addAll(game.challenges.getPreviousChallengesFor(level));

		/*
		 * get current challenge set
		 */
		this.queue = game.challenges.getChallengesFor(level);

		start = 0;
		end = queue.size();
		ip = start;
		setChallenge();
		updateProgress2();
		scoreBoard.reset();
		elapsedTime = 0;
		markedWrong = 0;
		markedCorrect = 0;
		boardElapsed=0f;
	}

	private void levelComplete() {
		float percent;
		int totalMarks;
		nextScreen = false;
		elapsedTime = 0;
		totalMarks = markedCorrect + markedWrong;
		if (totalMarks < 1) {
			totalMarks = 1;
		}
		percent = (float) markedCorrect / (float) totalMarks;
		game.prefs.setLevelAccuracy(game.getLevelOn(), (int) (100f * percent));
		game.prefs.setLevelTime(game.getLevelOn(), boardElapsed);
		game.prefs.setLastScore(game.getLevelOn(), scoreBoard.getScore());
		game.sm.playEffect("cash_out");
		game.gameEvent(GameEvent.LevelComplete);
	}

	private void loadBoard() {
		int ix;
		Random r;
		String name;
		r = new Random();
		int sz = alreadySeen.size() + 1;
		String[] pickFrom = alreadySeen.toArray(new String[sz]);
		pickFrom[sz - 1] = currentChallenge;
		Array<String> deck = new Array<String>();
		int correct = 0;
		for (ix = 0; ix < buttonPicture.length; ix++) {
			if (deck.size == 0) {
				deck.addAll(pickFrom);
			}
			deck.shuffle();
			name = deck.pop();
			if (name.equals(currentChallenge)) {
				correct++;
			}
			buttonPicture[ix] = name;
		}

		/*
		 * add a correct one if no correct one already there ... =OR= maybe add
		 * additional copies of the correct answer 1% of the remaining times
		 */
		for (int ix1 = 0; ix1 < buttonPicture.length; ix1++) {
			if (correct == 0 || r.nextInt(100) == 1) {
				int slot = r.nextInt(buttonPicture.length);
				if (buttonPicture[slot].equals(currentChallenge)) {
					continue;
				}
				buttonPicture[slot] = currentChallenge;
				correct++;
			}
		}
		boolean isPlural = (currentChallenge.endsWith("j") || currentChallenge
				.endsWith("J"));
		String altForm = isPlural ? currentChallenge.substring(0,
				currentChallenge.length() - 1) : currentChallenge + "j";
		while (alreadySeen.contains(altForm)) {
			boolean already = false;
			for (int iy = 0; iy < buttonPicture.length; iy++) {
				if (buttonPicture[iy].equals(altForm)) {
					already = true;
					break;
				}
			}
			if (!already) {
				int slot = r.nextInt(buttonPicture.length);
				if (correct == 1
						&& buttonPicture[slot].equals(currentChallenge)) {
					continue;
				}
				buttonPicture[slot] = altForm;
			}
			break;
		}

		for (ix = 0; ix < buttonPicture.length; ix++) {
			buttonPictureCopy[ix] = buttonPicture[ix];
			savedPictureFH[ix] = game.challenges.nextImage(buttonPicture[ix]);
			gameBoard.setImage(ix, savedPictureFH[ix]);
		}
		pbar.setProgress1(0);
		updateProgress2();
		pbar.setProgress3(1);
	}

	private void loadNewBoard() {
		setChallenge();
		nextScreen = false;
		elapsedTime = 0;
	}

	final protected Group pauseOverlay = new Group();

	@Override
	public void render(float delta) {
		super.render(delta);

		gameStage.draw();

		if (isPaused()) {
			pauseOverlay.getColor().a = 1f;
			return;
		}
		//total time board elapsed includes challenge sounding times
		boardElapsed += delta;

		if (game.sm.isChallengePlaying(currentChallenge)) {
			noTick = false;
			return;
		}
		if (noTick) {
			failsafeTimer += delta;
			if (failsafeTimer > 1) {
				failsafeTimer = 0;
				noTick = false;
			}
			return;
		}

		if (repeatAudio && pbar.getProgress1() > 0.5f) {
			repeatAudio = false;
			game.sm.playChallenge(currentChallenge);
		}

		if (!repeatAudio && pbar.getProgress1() < .1f) {
			repeatAudio = true;
		}

		elapsedTime += delta;		

		if (nextScreen) {
			showNextBoardCheck();
			return;
		}

		if (elapsedTime > timeLimit) {
			elapsedTime = 0;
			tooMuchTimePassed();
			pbar.setProgress1(0f);
			return;
		}
		pbar.setProgress1(elapsedTime / timeLimit);
	}

	@Override
	public void resume() {
		super.resume();
	}

	private void setChallenge() {
		currentChallenge = queue.get(ip);
		game.activeChallenge = currentChallenge;
		loadBoard();
		if (switchToTrainer()) {
			game.gameEvent(GameEvent.Training);
		} else {
			if (game.sm.isChallengeEnabled()) {
				game.sm.playChallenge(currentChallenge);
				noTick = true;
				repeatAudio = true;
			}
		}
		updateChallengeBoard();
	}

	public void setWritingMode(WritingMode writingMode) {
		this.writingMode = writingMode;
	}

	@Override
	public void show() {
		super.show();
		pause_mask=new Pixmap(1, 1, Format.RGBA8888);
		pause_mask.setColor(1f, 1f, 1f, .7f);
		pause_mask.fill();
		pause_texture = new Texture(pause_mask);
		Image pause_mask_image = new Image(pause_texture);
		pause_mask_image.pack();
		pause_mask_image.scaleBy(fullscan.width, fullscan.height);
		pauseOverlay.addActor(pause_mask_image);
		LabelStyle continueStyle = new LabelStyle(new FontLoader().get(72), GameColor.MAIN_TEXT);
		String pauseMsg = "[CONTINUE]";
		Label toContinue = new Label(pauseMsg, continueStyle);
		pauseOverlay.addActor(toContinue);
		toContinue.pack();
		toContinue.setX((fullscan.width-toContinue.getWidth())/2);
		toContinue.setY((fullscan.height-toContinue.getHeight())/2+toContinue.getHeight());
		toContinue.addListener(new ClickListener(){	
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				setPaused(false);
				return true;
			}
		});
		Label toExit = new Label("[EXIT]", continueStyle);
		pauseOverlay.addActor(toExit);
		toExit.pack();
		toExit.setX((fullscan.width-toExit.getWidth())/2);
		toExit.setY((fullscan.height-toExit.getHeight())/2-toExit.getHeight());
		toExit.addListener(new ClickListener(){	
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				setPaused(false);
				game.gameEvent(GameEvent.Done);
				return true;
			}
		});
		
		//make sure pause overlay is in correct state
		setPaused(isPaused());
		updateChallengeBoard();// in case of challenge display option change
		if (getCorrectCount() > 0) {
			/*
			 * only replay challenge audio if there are still correct buttons
			 */
			if (!switchToTrainer()) {
				game.sm.playChallenge(currentChallenge);
			}
		}
		Gamepads.addListener(watcher);
		for (Controller c : Gamepads.getControllers()) {
			watcher.connected(c);
		}
		hud_showIndicator();
		connectClickhandlers();
		gameControls.setOnPause(new Runnable() {
			@Override
			public void run() {
				setPaused(true);
			}
		});
		gameControls.setOnExit(new Runnable() {
			@Override
			public void run() {
				game.gameEvent(GameEvent.Done);
			}
		});
	}

	private void connectClickhandlers() {
		for (int ix = 0; ix < gameBoard.button_count(); ix++) {
			final int button = ix;
			gameBoard.clearListeners(button);
			gameBoard.addListener(button, new ClickListener() {
				private int btn = button;

				@Override
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					hud_setIndicator(btn);
					hud_showIndicator(true);
					hud_select();
					return true;
				}
			});
		}
	}

	private void disconnectClickhandlers() {
		for (int ix = 0; ix < gameBoard.button_count(); ix++) {
			final int button = ix;
			gameBoard.clearListeners(button);
		}
	}

	private void showNextBoardCheck() {
		if (elapsedTime > 2) {
			if (ip >= end) {
				levelComplete();
			} else {
				loadNewBoard();
			}
		}
	}

	private boolean switchToTrainer() {
		if (game.prefs.getTrainingMode().equals(TrainingMode.Off)) {
			alreadySeen.add(currentChallenge);
			return false;
		}
		return !alreadySeen.contains(currentChallenge);
	}

	private void tooMuchTimePassed() {
		dropWrongAnswer();
		game.sm.playChallenge(currentChallenge);
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
		challengeBoard.setDisplayText(challenge);
	}

	private void updateProgress2() {
		float percent;
		percent = ((float) ip - (float) start)
				/ ((float) end - (float) start + 1f);
		pbar.setProgress2(percent);
	}
}
