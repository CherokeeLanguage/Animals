package com.cherokeelessons.vocab.animals.one;

import java.util.HashSet;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.cherokeelessons.vocab.animals.one.CherokeeAnimals.TrainingScreenMode;
import com.cherokeelessons.vocab.animals.one.GameEvent.EventList;

public class ScreenGameplay extends ScreenGameCore {

	enum WritingMode {
		Latin, None, Syllabary
	}

	private ViewGameBoard activehud;

	private HashSet<String> alreadySeen = new HashSet<String>();

	private int badPoints = 0;
	FileHandle button_highlight = Gdx.files
			.internal("buttons/square_white.png");
	private String[] buttonPicture = new String[6];
	private String[] buttonPictureCopy = new String[6];
	private ViewChallengeBoard challengeBoard;
	private String currentChallenge;
	private float elapsedTime = 0;
	private int end;

	private float failsafeTimer = 0;
	private FileHandle fat_check = Gdx.files
			.internal("buttons/checkmarkfat_white.png");
	private FileHandle fat_x = Gdx.files.internal("buttons/fat_x_white.png");
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
	private GraduatedIntervalQueue queue = null;

	private boolean repeatAudio = true;
	private FileHandle[] savedPictureFH = new FileHandle[6];

	private ViewScoreBoard scoreBoard;

	private SoundManager sm;
	private int start;

	private float timeLimit = 5;

	private WritingMode writingMode;

	public ScreenGameplay(CherokeeAnimals game) {
		super(game);

		queue = game.animalQueue;

		sm = game.getSoundManager();

		pbar = new ViewProgressBar(overscan);
		scoreBoard = new ViewScoreBoard(overscan, sm);
		gameControls = new ViewInGameControls(overscan);
		gameBoard = new ViewGameBoard(overscan);
		challengeBoard = new ViewChallengeBoard(overscan);
		activehud = new ViewGameBoard(overscan);

		gameStage.addActor(pbar);
		gameStage.addActor(scoreBoard);
		gameStage.addActor(gameControls);
		gameStage.addActor(gameBoard);
		gameStage.addActor(activehud);
		gameStage.addActor(challengeBoard);

		gameStage.getRoot().setX(overscan.x);
		gameStage.getRoot().setY(overscan.y);

	}

	private void buttonAsCorrect(int button) {
		buttonPicture[button] = "";
		gameBoard.setImage(button, fat_check);
		gameBoard.setColor(button, GameColor.GREEN);
		markedCorrect++;
	}

	private void buttonAsWrong(int button) {
		buttonPicture[button] = "";
		gameBoard.setImage(button, fat_x);
		gameBoard.setColor(button, GameColor.FIREBRICK);
		markedWrong++;
	}

	private void checkButton(int button) {
		if (buttonPicture[button].equals("")) {
			return;
		}
		int bonus = 0;
		elapsedTime = 0;
		if (!buttonPicture[button].equals(currentChallenge)) {
			buttonAsWrong(button);
			scoreBoard.changeScoreBy(-badPoints - 1);
			badPoints = (badPoints + 1) % 5;
			goodPoints = 0;
			sm.playEffect("buzzer2");
			return;
		}
		buttonAsCorrect(button);
		gameBoard.spin(button);
		scoreBoard.changeScoreBy(goodPoints + 1);
		badPoints = 0;
		goodPoints = (goodPoints + 1) % 5;
		if (getCorrectCount() < 1) {
			for (button = 0; button < 6; button++) {
				if (!buttonPicture[button].equals("")) {
					bonus++;
					buttonPicture[button] = "";
//					gameBoard.setClickHandler(button, null);
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
			ix = r.nextInt(6);
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
			sm.playEffect("alarm");
			break;
		case 1:
			sm.playEffect("bark");
			break;
		case 2:
			sm.playEffect("dialogerror");
			break;
		}
	}

	private int getCorrectCount() {
		int ix = 0, count = 0;
		for (ix = 0; ix < 6; ix++) {
			if (buttonPicture[ix].equals(currentChallenge)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * @return the currentChallenge
	 */
	public String getCurrentChallenge() {
		return currentChallenge;
	}

	public WritingMode getWritingMode() {
		return writingMode;
	}

	private int getWrongCount() {
		int ix = 0, count = 0;
		for (ix = 0; ix < 6; ix++) {
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
		alreadySeen.add(currentChallenge);
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

	private void hud_showIndicator() {
		game.getSoundManager().playEffect("box_moved");

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
		System.out.println("init level: " + level);
		int ix;
		String previous;
		level--;

		start = queue.getLevelStartPosition(level);
		end = queue.getLevelEndPosition(level);
		ip = start;
		updateProgress2();
		alreadySeen.clear();
		setChallenge();
		/*
		 * load up all entries from previous levels as "already seen"
		 */
		for (ix = 0; ix < start; ix++) {
			previous = queue.getEntry(ix);
			alreadySeen.add(previous);
		}

		scoreBoard.reset();

		elapsedTime = 0;

		markedWrong = 0;
		markedCorrect = 0;
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
		game.setLevelAccuracy(game.getLevelOn() - 1, (int) (100f * percent));
		/*
		 * do we have a new high score?
		 */
		if (scoreBoard.getScore() > game.getHighScore()) {
			game.setHighScore(scoreBoard.getScore());
		}
		/*
		 * do event LAST, so LevelSelect is working with up-to-date info!
		 */
		sm.playEffect("cash_out");
		game.event(EventList.LevelComplete);
	}

	private void loadBoard() {
		int ix;
		Random r;
		String name;
		boolean keepLooping = true;

		r = new Random();

		/*
		 * keep picking random pictures until we have at least one correct
		 */
		while (keepLooping) {
			for (ix = 0; ix < 6; ix++) {
				/*
				 * only show pix from previous challenges to prevent confusion
				 * issues with plurals and singulars
				 */
				name = queue.getEntry(r.nextInt(ip + 1));
				if (name.equals(currentChallenge)) {
					keepLooping = false;
				}
				buttonPicture[ix] = name;
			}
		}
		for (ix = 0; ix < 6; ix++) {
			buttonPictureCopy[ix] = buttonPicture[ix];
			savedPictureFH[ix] = game
					.randomAnimalImageByName(buttonPicture[ix]);
			gameBoard.setImage(ix, savedPictureFH[ix]);
		}
		pbar.setProgress1(0);
		updateProgress2();
		pbar.setProgress3(1);
	}

	private void loadNewBoard() {
		setChallenge();
		updateChallengeBoard();
		nextScreen = false;
		elapsedTime = 0;
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		if (sm.isChallengePlaying(currentChallenge)) {
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
			sm.playChallenge(currentChallenge);
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
		currentChallenge = queue.getEntry(ip);
		loadBoard();
		if (switchToTrainer()) {
			game.event(EventList.Training);
		} else {
			if (sm.isChallengeEnabled()) {
				sm.playChallenge(currentChallenge);
				noTick = true;
				repeatAudio = true;
			}
		}
	}

	public void setWritingMode(WritingMode writingMode) {
		this.writingMode = writingMode;
	}

	@Override
	public void show() {
		super.show();
		updateChallengeBoard();
		if (getCorrectCount() > 0) {
			/*
			 * only replay challenge audio if there are still correct buttons
			 */
			if (!switchToTrainer()) {
				sm.playChallenge(currentChallenge);
			}
		}
		hud_showIndicator();
	}

	private void showNextBoardCheck() {
		if (elapsedTime > 2) {
			if (ip > end) {
				levelComplete();
			} else {
				loadNewBoard();
			}
		}
	}

	private boolean switchToTrainer() {
		if (game.getShowTrainingScreen().equals(TrainingScreenMode.Off)) {
			return false;
		}
		System.out.println("alreadySeen: "
				+ alreadySeen.contains(currentChallenge) + ", "
				+ currentChallenge);
		return !alreadySeen.contains(currentChallenge);
	}

	private void tooMuchTimePassed() {
		dropWrongAnswer();
		sm.playChallenge(currentChallenge);
	}

	private void updateChallengeBoard() {
		String challenge;
		switch (game.getChallengeWord()) {
		case Syllabary:
			challenge = game.getAnimalsSyl().get(currentChallenge);
			break;
		case Latin:
			challenge = game.getAnimalsLat().get(currentChallenge);
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
