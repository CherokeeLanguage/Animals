package com.cherokeelessons.vocab.animals.one;

import java.util.HashSet;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.cherokeelessons.vocab.animals.one.CherokeeAnimals.FontStyle;
import com.cherokeelessons.vocab.animals.one.CherokeeAnimals.TrainingScreenMode;
import com.cherokeelessons.vocab.animals.one.GameEvent.EventList;

public class ScreenTrainer extends ScreenGameCore {

	HashSet<FileHandle> alreadyShown;

	private LabelStyle buttonStyle;

	private String currentChallenge = "";

	private int currentIX = 0;

	private boolean doNextPic = false;
	private float elapsedTime = 0;
	final private float interval = 2.5f;
	private Label lbl_exitInfo;
	private View3x3Selector pictureChallenge;

	private SoundManager sm;

	private ViewChallengeBoard writtenChallenge;

	public ScreenTrainer(CherokeeAnimals game) {
		super(game);

		BitmapFont font;
		Integer fontSize = 48;

		font = CherokeeAnimals.getFixedFont(CherokeeAnimals.FontStyle.Script, fontSize);

		buttonStyle = new LabelStyle();
		buttonStyle.font = font;
		buttonStyle.fontColor = GameColor.GREEN;

		lbl_exitInfo = new Label("[O] Skip Training", buttonStyle);
		lbl_exitInfo.setTouchable(Touchable.enabled);
		lbl_exitInfo.setX(overscan.width - lbl_exitInfo.getWidth());
		lbl_exitInfo.setY(overscan.height - lbl_exitInfo.getHeight());
		lbl_exitInfo.pack();

		pictureChallenge = new View3x3Selector(overscan);
		pictureChallenge.setBoxMargin(4);
		pictureChallenge.setTitle("TRAINING");

		writtenChallenge = new ViewChallengeBoard(overscan);
		writtenChallenge.setDisplayText("New Challenge");

		gameStage.addActor(writtenChallenge);
		gameStage.addActor(pictureChallenge);
		gameStage.addActor(lbl_exitInfo);

		sm = game.getSoundManager();

		alreadyShown = new HashSet<FileHandle>();
	}

	private void doSkipTraining() {
		game.enforceSoundSettings();
		game.getSoundManager().playEffect("menu-click");
		while (currentIX < 9) {
			loadNextPic();
		}
		elapsedTime = interval;
	}

	@Override
	public void hide() {
		super.hide();
	}

	private void loadNextPic() {
		FileHandle newPic;
		newPic = game.randomAnimalImageByName(currentChallenge);
		alreadyShown.add(newPic);
		pictureChallenge.setImage(currentIX, newPic);
		currentIX++;
	}

	@Override
	protected boolean onBack() {
		doSkipTraining();
		return false;
	}

	@Override
	public void render(float delta) {
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
		if (sm.isChallengePlaying(currentChallenge)) {
			return;
		}
		if (currentIX == 3
				&& game.getShowTrainingScreen()
						.equals(TrainingScreenMode.Brief)) {
			doSkipTraining();
		}
		if (currentIX == 9) {
			currentIX++;
			sm.playEffect("ding-ding-ding");
			elapsedTime = 0;
			pictureChallenge.unFocusOn();
			pictureChallenge.scatter();
			lbl_exitInfo.setVisible(false);
			pictureChallenge.setTitle(" ");
			writtenChallenge.setVisible(false);
			return;
		}
		if (currentIX == 10) {
			game.event(EventList.GoBack);
			return;
		}
		sm.playChallenge(currentChallenge);
		doNextPic = true;
	}

	public void reset() {
		int ix;
		for (ix = 0; ix < 9; ix++) {
			pictureChallenge.setImage(ix, null);
		}
		elapsedTime = 0;
		currentIX = 0;
		currentChallenge = game.getCurrentChallenge();
		alreadyShown.clear();
		lbl_exitInfo.setVisible(true);
		pictureChallenge.setTitle("NEW CHALLENGE");
		writtenChallenge.setVisible(true);
	}

	@Override
	public void show() {
		super.show();
		if (!currentChallenge.equals(game.getCurrentChallenge())) {
			reset();
		}
		updateChallengeBoard();
		gameStage.getRoot().setX(overscan.x);
		gameStage.getRoot().setY(overscan.y);
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
		writtenChallenge.setDisplayText(challenge);
	}
}
