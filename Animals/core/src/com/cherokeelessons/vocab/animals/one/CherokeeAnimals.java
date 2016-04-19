package com.cherokeelessons.vocab.animals.one;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.cherokeelessons.vocab.animals.one.DisplaySize.Resolution;
import com.cherokeelessons.vocab.animals.one.GameEvent.EventList;

public class CherokeeAnimals extends Game {

	private IAP iap=new IapNOP();
	
	public void setIap(IAP iap) {
		this.iap=iap;
	}
	
	public IAP getIap(){
		return iap;
	}
	
	enum ChallengeWordMode {
		Latin, None, Syllabary
	}

	public enum ScreenMap {
		Gameplay, Leaderboard, LevelComplete, Levelselect, libGdx, Loading, Main, Mystats, Options, Paused, Quit, Trainer, Instructions
	}

	public enum ScreenOrientation {
		landscape, portrait
	}

	enum SoundEffectVolume {
		Low, Off, On
	}

	public enum TrainingScreenMode {
		Brief, Long, Off
	}

	public static final TextureFilter filter = TextureFilter.Linear;

	public static enum FontStyle {
		FreeSans, Script;
	}
	
	public static ArrayList<String> readAssetDir(String dir) {
		ArrayList<String> temp = new ArrayList<String>();
		String txt = Gdx.files.internal(dir + "/00-plist.txt").readString(
				"UTF-8");
		String[] plist = txt.split("\n");
		for (int ix = 0; ix < plist.length; ix++) {
			String e = plist[ix];
			if (e == null) {
				continue;
			}
			if (e.contains("plist")) {
				continue;
			}
			e = e.trim();
			if (e.length() == 0) {
				continue;
			}
			FileHandle f = Gdx.files.internal(dir + "/" + e);
			if (!f.exists()) {
				continue;
			}
			temp.add(f.path());
		}
		return temp;
	}

	private ScreenMap activeScreen = null;

	private ArrayList<String> animalList = null;

	GraduatedIntervalQueue animalQueue = null;

	private HashMap<String, String> animalsLat = null;

	private HashMap<String, String> animalsSyl = null;

	private boolean challengeAudio = true;

	private ChallengeWordMode challengeWord = ChallengeWordMode.Syllabary;

	String dirAudio = "sounds/animals";

	String dirImages = "images/animals";

	private int displayVolume = 100;

	final private ArrayList<String> effectsList = new ArrayList<String>();
	private int effectsListIX = 0;
	private int highScore;
	private HashMap<String, FileHandle> imageFiles;
	private HashMap<String, ArrayList<String>> imagesPerAnimal;
	private Runnable init = new Runnable() {
		@Override
		public void run() {
			if (initStepDone) {
				return;
			}
			/*
			 * initialize stuff in chunks then call self again
			 */
			switch (initStep++) {
			case 0:
				/*
				 * Trigger background IAP init stuff
				 */
				iap.getPublicKey();
				iap.loadUUID(null);
				iap.loadReceipts(null);
				break;
			case 1:
				System.out.println("init");
				System.out.println("vol");
				setVolume(getOptions().getInteger(GSetting.MasterVolume.name(),
						90));
				System.out.println("startup music");
				startupMusic = Gdx.audio.newMusic(Gdx.files
						.internal("sounds/effects/startup.ogg"));
				startupMusic.setLooping(false);
				startupMusic.setVolume(getMasterVolume());
				startupMusic.play();
				break;
			case 2:
				System.out.println("load options");
				setHighScore(getOptions().getInteger("highScore", 0));
				setChallengeAudio(getOptions().getBoolean(
						GSetting.ChallengeAudioMode.name(), true));
				setChallengeWord(getOptions().getString(
						GSetting.ChallengeWordMode.name(),
						ChallengeWordMode.Syllabary.name()));
				setSoundEffectsVolume(getOptions().getString(
						GSetting.SoundEffectsOn.name(),
						SoundEffectVolume.Low.name()));
				TrainingScreenMode mode = TrainingScreenMode.Brief;
				try {
					String s_mode = getOptions().getString(
							GSetting.ShowTrainingScreen.name());
					mode = TrainingScreenMode.valueOf(s_mode);
				} catch (Exception e) {
				}
				setShowTrainingScreen(mode);
				break;
			case 3:
				System.out.println("load level accuracy");
				loadLevelAccuracy();
				break;
			case 4:
				System.out.println("create translation maps");
				syllabaryMap = translationMaps();
				break;
			case 5:
				System.out.println("soundmanager create");
				soundManager = new SoundManager();
				effectsList.addAll(soundManager.getEffectsList());
				break;
			case 6:
				System.out.println("load animal data");
				loadAnimalData();
				break;
			case 7:
				System.out.println("match up audio to images");
				matchUpAudioFilesToImages();
				break;
			case 8:
				System.out.println("load image hashmap");
				loadAnimalImageHashMap();
				break;
			case 9:
				System.out.println("calculate animal list");
				calculateAnimalList();
				break;
			case 10:
				System.out.println("calculate challenge queue");
				calculateChallengeQueue();
				break;
			case 11:
				System.out.println("sound setting ON");
				enforceSoundSettings();
				break;
			case 12:
				if (effectsListIX < effectsList.size()) {
					initStep--;
					String effect = effectsList.get(effectsListIX);
					System.out.println("Preloading sound effect: " + effect);
					soundManager.loadEffect(effect);
					effectsListIX++;
				}
				break;
			case 13:
				// start scroll thing on loading screen
				event(EventList.DoScroller);
				break;
			case 17:
//				iap.purchaseGame(null);
				break;
			case 18:
				if (startupMusic.isPlaying()) {
					initStep--;
				}
				break;
			case 19:
				initStepDone = true;
				System.out.println("init done");
				event(EventList.InitDone);
				break;
			}
			if (!initStepDone) {
				Gdx.app.postRunnable(init);
			}
		}
	};

	private int initStep = 0;
	private volatile boolean initStepDone = false;
	private int[] levelAccuracy;
	private int levelOn = 1;
	final public int levels;
	final public Logger log = new Logger("CherokeeAnimals", Logger.DEBUG);
	private float masterVolume = 1;
	private int minPercentAdvance = 80;
	private boolean newGameScreen = true;
	public Preferences options;

	final private int queueRevision = 2;

	private HashMap<String, ArrayList<String>> randomDecks = new HashMap<String, ArrayList<String>>();

	public int screenHeight = DisplaySize._1080p.size().h;

	private Stack<ScreenMap> screenHistory = new Stack<CherokeeAnimals.ScreenMap>();

	protected HashMap<ScreenMap, ScreenGameCore> screenMap = new HashMap<CherokeeAnimals.ScreenMap, ScreenGameCore>();

	public Vector2 screenSize = null;

	public int screenWidth = DisplaySize._1080p.size().w;

	private TrainingScreenMode showTrainingScreen = TrainingScreenMode.Brief;

	private SoundEffectVolume soundEffectsVolume;

	private SoundManager soundManager;

	Music startupMusic = null;

	private HashMap<String, String> syllabaryMap = null;

	public CherokeeAnimals() {
		super();
		System.out.println("Platform: " + OS.platform.name());
		Resolution stageSize = DisplaySize._1080p.size();
		screenWidth = stageSize.w;
		screenHeight = stageSize.h;
		screenSize = new Vector2(screenWidth, screenHeight);

		levels = 18;
		levelAccuracy = new int[levels];
	}

	public String asOnlySyllabary(String string) {
		string = invertSyllabary(string);
		string = string.replaceAll("[^Ꭰ-Ᏼ ]", "");
		return string;
	}

	public String asOnlyText(String string) {
		string = string.replaceAll("[^Ꭰ-Ᏼa-zA-Z ]", "");
		return string;
	}

	private void calculateAnimalList() {
		animalList = new ArrayList<String>();
		HashMap<String, FileHandle> audioFiles = soundManager
				.getChallengeAudioList();
		for (FileHandle value : audioFiles.values()) {
			animalList.add(value.nameWithoutExtension());
		}
	}

	private void calculateChallengeQueue() {
		animalQueue = new GraduatedIntervalQueue();
		animalQueue.setAnimalsSyl(animalsSyl);
		animalQueue.load(animalList);
		animalQueue.calculateLevelStarts(levels);
	}

	@Override
	public void create() {
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);
		event(EventList.libGdx);		
	}

	@Override
	public void dispose() {
		saveLevelAccuracies();
		getOptions().putInteger("highScore", getHighScore());
		getOptions().flush();
		super.dispose();
	}

	public void enforceSoundSettings() {
		soundManager.setChallengeEnabled(isChallengeAudio());
		switch (getSoundEffectsVolume()) {
		case Off:
			soundManager.setEffectsEnabled(false);
			break;
		case Low:
			soundManager.setEffectsEnabled(true);
			soundManager.setEffectVolume(0.3f * getMasterVolume());
			break;
		default:
			soundManager.setEffectsEnabled(true);
			soundManager.setEffectVolume(1f * getMasterVolume());
			break;
		}
		soundManager.setChallengeVolume(getMasterVolume());
	}

	public void event(final EventList event) {
		if (event.equals(EventList.Pause)){
			System.out.println("event: "+event.name());
		}
		Gdx.app.postRunnable(new Runnable() {			
			@Override
			public void run() {
				GameEvent evt = new GameEvent();
				evt.setEvent(event);
				CherokeeAnimals.this.handleEvent(evt);
			}
		});
	}

	public ScreenMap getActiveScreen() {
		return activeScreen;
	}

	public ArrayList<String> getAnimalList() {
		return animalList;
	}

	public HashMap<String, String> getAnimalsLat() {
		return animalsLat;
	}

	public HashMap<String, String> getAnimalsSyl() {
		return animalsSyl;
	}

	public ChallengeWordMode getChallengeWord() {
		return challengeWord;
	}

	public String getCurrentChallenge() {
		ScreenGameplay s;
		s = ((ScreenGameplay) screenMap.get(ScreenMap.Gameplay));
		if (s == null)
			return "";
		return s.getCurrentChallenge();
	}

	public String getDirAudio() {
		return dirAudio;
	}

	public String getDirImages() {
		return dirImages;
	}

	/**
	 * @return the highScore
	 */
	public int getHighScore() {
		return highScore;
	}

	public HashMap<String, FileHandle> getImageFiles() {
		return imageFiles;
	}

	public int getLevelAccuracy(int ix) {
		if (ix < 0 || ix >= levelAccuracy.length) {
			System.out.println("OUT OF BOUNDS!");
			return 0;
		}
		return levelAccuracy[ix];
	}

	public int getLevelOn() {
		return levelOn;
	}

	public float getMasterVolume() {
		return masterVolume;
	}

	public int getMinPercentAdvance() {
		return minPercentAdvance;
	}

	public Preferences getOptions() {
		if (options == null) {
			options = Gdx.app
					.getPreferences(this.getClass().getCanonicalName());
		}
		return options;
	}

	public TrainingScreenMode getShowTrainingScreen() {
		return showTrainingScreen;
	}

	public SoundEffectVolume getSoundEffectsVolume() {
		return soundEffectsVolume;
	}

	public SoundManager getSoundManager() {
		return soundManager;
	}

	public int getVolume() {
		return displayVolume;
	}

	private void handleEvent(GameEvent event) {
		ScreenMap newScreen = null;
		System.out.println("Event: " + event.getEvent().name());
		switch (event.getEvent()) {
		case libGdx:
			newScreen = ScreenMap.libGdx;
			screenMap.put(newScreen, new ScreenPoweredBy(this));
			break;
		case DoScroller:
			ScreenLoading loader;
			loader = (ScreenLoading) screenMap.get(ScreenMap.Loading);
			loader.doScroll(17.4f - (float) startupMusic.getPosition() / 1000f);
			break;
		case Training:
			newScreen = ScreenMap.Trainer;
			if (!screenMap.containsKey(newScreen)) {
				screenMap.put(newScreen, new ScreenTrainer(this));
			}
			break;
		case LevelComplete:
			newScreen = ScreenMap.LevelComplete;
			if (!screenMap.containsKey(newScreen)) {
				screenMap.put(newScreen, new ScreenLevelComplete(this));
			}
			break;
		case Pause:
			if (getActiveScreen() == ScreenMap.libGdx) {
				return;
			}
			if (getActiveScreen() == ScreenMap.Paused) {
				return;
			}
			if (getActiveScreen() == ScreenMap.Main) {
				return;
			}
			if (getActiveScreen() == ScreenMap.Loading) {
				return;
			}
			if (getActiveScreen() == ScreenMap.Options) {
				return;
			}
			saveScreenShot();
			newScreen = ScreenMap.Paused;
			break;
		case GoMenu:
			if (getActiveScreen() == ScreenMap.Loading) {
				return;
			}
			event(EventList.ShowOptions);
			break;
		case GoBack:
			// go back to previous screen,
			// unless special exception exists
			if (getActiveScreen() == ScreenMap.Main) {
				event(EventList.QuitGame);
				return;
			}
			if (getActiveScreen() == ScreenMap.Loading) {
				event(EventList.QuitGame);
				return;
			}
			if (getActiveScreen() == ScreenMap.libGdx) {
				event(EventList.QuitGame);
				return;
			}
			if (getActiveScreen() == ScreenMap.LevelComplete) {
				// level complete is only after game screen,
				// so throw away game screen if going back
				screenHistory.pop();
			}
			if (screenHistory.size() > 0) {
				// get previous screen
				newScreen = screenHistory.pop();
				/*
				 * discard the screen we are going back from to prevent history
				 * storage
				 */
				setActiveScreen(null);
			}
			if (newScreen == null) {
				System.out.println("BAD STACK ENTRY");
				event(EventList.ShowMainMenu);
			}
			break;
		case ShowOptions:
			newScreen = ScreenMap.Options;
			if (!screenMap.containsKey(newScreen)) {
				screenMap.put(newScreen, new ScreenOptionsMenu(this));
			}
			break;
		case FirstRun:
			/*
			 * show "ᎦᏓᏅᎡᎭ..." while "thinking"
			 */
			newScreen = ScreenMap.Loading;
			if (!screenMap.containsKey(newScreen)) {
				screenMap.put(newScreen, new ScreenLoading(this));
			}
			Gdx.app.postRunnable(init);
			break;
		case ShowMainMenu:
			/*
			 * switch to main menu
			 */
			newScreen = ScreenMap.Main;
			if (!screenMap.containsKey(newScreen)) {
				screenMap.put(newScreen, new ScreenMainMenu(this));
			}
			break;
		case InitDone:
			event(EventList.ShowMainMenu);
			break;
		case LevelSelect:
			if (screenHistory.contains(ScreenMap.Levelselect)) {
				/*
				 * jump backwards in stack till at previous Levelselect
				 */
				while (screenHistory.peek() != ScreenMap.Levelselect) {
					screenHistory.pop();
				}
				/*
				 * pop it also as we are going there
				 */
				screenHistory.pop();
				/*
				 * we also don't want the current active screen in the stack
				 * either
				 */
				setActiveScreen(null);
			}
			newScreen = ScreenMap.Levelselect;
			if (!screenMap.containsKey(newScreen)) {
				screenMap.put(newScreen, new ScreenLevelSelect(this));
			}
			enforceSoundSettings();
			break;
		case ShowGameBoard:
			newScreen = ScreenMap.Gameplay;
			if (!screenMap.containsKey(newScreen)) {
				screenMap.put(newScreen, new ScreenGameplay(this));
			}
			enforceSoundSettings();
			if (isNewGameScreen()) {
				ScreenGameplay sg = (ScreenGameplay) screenMap.get(newScreen);
				sg.initLevel(getLevelOn());
				ScreenTrainer st = (ScreenTrainer) screenMap
						.get(ScreenMap.Trainer);
				if (st != null) {
					st.reset();
				}
				setNewGameScreen(false);
			}
			break;
		case QuitGame:
			/*
			 * switch to blank screen then exit;
			 */
			Gdx.app.exit();
			newScreen = ScreenMap.Quit;
			if (!screenMap.containsKey(newScreen)) {
				screenMap.put(newScreen, new ScreenQuit(this));
			}
			break;
		case ShowInstructions:
			newScreen = ScreenMap.Instructions;
			if (!screenMap.containsKey(newScreen)) {
				screenMap.put(newScreen, new ScreenInstructions(this));
			}
			break;
		default:
			break;
		}
		// don't do screen load logic if screen is already loaded
		// this also prevents "getting stuck on a screen,
		// when hitting back"
		if (newScreen != null && newScreen != getActiveScreen()) {
			if (getActiveScreen() != null) {
				screenHistory.push(getActiveScreen());
			}
			setActiveScreen(newScreen);
			setScreen(screenMap.get(newScreen));
		}
	}

	private String invertSyllabary(String string) {
		String newString = "";
		String key = "";

		while (string.length() > 0) {
			if (string.length() >= 3) {
				key = string.substring(0, 3);
				if (syllabaryMap.containsKey(key)) {
					newString += syllabaryMap.get(key);
					string = string.substring(3);
					continue;
				}
			}
			if (string.length() >= 2) {
				key = string.substring(0, 2);
				if (syllabaryMap.containsKey(key)) {
					newString += syllabaryMap.get(key);
					string = string.substring(2);
					continue;
				}
			}
			if (string.length() >= 1) {
				key = string.substring(0, 1);
				if (syllabaryMap.containsKey(key)) {
					newString += syllabaryMap.get(key);
					string = string.substring(1);
					continue;
				}
				newString += key;
				string = string.substring(1);
			}
		}
		return newString;
	}

	public boolean isChallengeAudio() {
		return challengeAudio;
	}

	/**
	 * @return the initStepDone
	 */
	public boolean isInitStepDone() {
		return initStepDone;
	}

	public boolean isNewGameScreen() {
		return newGameScreen;
	}

	private void loadAnimalData() {
		String animalName;
		String animalNameClean;
		String animalSyllabary;
		ArrayList<FileHandle> temp;

		temp = loadAnimalsByAudio();

		animalsSyl = new HashMap<String, String>();
		animalsLat = new HashMap<String, String>();

		HashMap<String, FileHandle> audioFiles = soundManager
				.getChallengeAudioList();
		for (int ix = 0; ix < temp.size(); ix++) {
			animalName = temp.get(ix).nameWithoutExtension();
			audioFiles.put(animalName, temp.get(ix));

			animalSyllabary = asOnlySyllabary(animalName);
			animalsSyl.put(animalName, animalSyllabary);
			animalsSyl.put(animalSyllabary, animalName);

			animalNameClean = asOnlyText(animalName);
			animalsLat.put(animalName, animalNameClean);
			animalsLat.put(animalNameClean, animalName);
		}

	}

	private void loadAnimalImageHashMap() {
		int ix; // , len;
		String image;
		String name;
		String name_alt;
		FileHandle fh;
		ArrayList<String> dirListing;
		ArrayList<String> list;

		imageFiles = new HashMap<String, FileHandle>();
		imagesPerAnimal = new HashMap<String, ArrayList<String>>();
		dirListing = readAssetDir(dirImages + "/");

		HashMap<String, FileHandle> audioFiles = soundManager
				.getChallengeAudioList();
		for (FileHandle value : audioFiles.values()) {
			list = new ArrayList<String>();
			name = value.nameWithoutExtension();
			for (ix = -1; ix < 10; ix++) {
				if (ix == -1) {
					name_alt = name;
				} else {
					name_alt = name + "_" + ix;
				}
				image = dirImages + "/" + name_alt + ".png";
				if (dirListing.contains(image)) {
					fh = Gdx.files.internal(image);
					imageFiles.put(name_alt, fh);
					list.add(name_alt);
				}
			}
			imagesPerAnimal.put(name, list);
		}
	}

	private ArrayList<FileHandle> loadAnimalsByAudio() {
		final ArrayList<FileHandle> temp = new ArrayList<FileHandle>();
		String txt = Gdx.files.internal("sounds/animals/" + "00-plist.txt")
				.readString("UTF-8");
		String[] plist = txt.split("\n");
		for (int ix = 0; ix < plist.length; ix++) {
			String e = plist[ix];
			if (e == null) {
				continue;
			}
			if (e.contains("plist")) {
				continue;
			}
			e = e.trim();
			if (e.length() == 0) {
				continue;
			}
			FileHandle f = Gdx.files.internal("sounds/animals/" + e);
			if (!f.exists()) {
				System.out.println("Missing file for plist entry: " + e);
				continue;
			}
			temp.add(f);
		}
		return temp;
	}

	private void loadLevelAccuracy() {
		int ix;
		/*
		 * version check, if version older than level arrangement version, reset
		 * to all 0%
		 */
		if (getOptions().getInteger("queueRevision", 0) < queueRevision) {
			for (ix = 0; ix < levelAccuracy.length; ix++) {
				setLevelAccuracy(ix, 0);
			}
			getOptions().putInteger("queueRevision", queueRevision);
			getOptions().flush();
			return;
		}

		/*
		 * version check passed, load previous values.
		 */
		for (ix = 0; ix < levelAccuracy.length; ix++) {
			levelAccuracy[ix] = getOptions().getInteger(
					GSetting.LevelAccuracy.name() + ix, 0);
		}
	}

	/**
	 * remove audio files for which there is no picture and log it
	 * 
	 * @return
	 */
	private Integer matchUpAudioFilesToImages() {
		int ix;
		HashMap<String, FileHandle> audioFiles = soundManager
				.getChallengeAudioList();
		ArrayList<String> animals = new ArrayList<String>();
		animals.addAll(audioFiles.keySet());
		for (ix = 0; ix < animals.size(); ix++) {
			if (!Gdx.files.internal(dirImages + "/" + animals.get(ix) + ".png")
					.exists()) {
				audioFiles.remove(animals.get(ix));
				System.out.println("MISSING PICTURE: " + animals.get(ix));
			}
		}
		return audioFiles.size();
	}

	@Override
	public void pause() {
		super.pause();
	}

	public String randomAnimalByName(String name) {
		ArrayList<String> theDeck;
		String theChosenOne;

		if (!randomDecks.containsKey(name)) {
			randomDecks.put(name, new ArrayList<String>());
		}
		theDeck = randomDecks.get(name);

		if (theDeck.size() < 1) {
			theDeck.addAll(imagesPerAnimal.get(name));
			Collections.shuffle(theDeck);
		}

		theChosenOne = theDeck.get(0);
		theDeck.remove(0);

		return theChosenOne;
	}

	public FileHandle randomAnimalImageByName(String name) {
		String theChosenOne;
		theChosenOne = randomAnimalByName(name);
		return imageFiles.get(theChosenOne);
	}

	public void saveLevelAccuracies() {
		int ix, len;
		for (ix = 0, len = levelAccuracy.length; ix < len; ix++) {
			setLevelAccuracy(ix, getLevelAccuracy(ix));
		}
		getOptions().flush();
	}

	private void saveScreenShot() {
		if (!screenMap.containsKey(ScreenMap.Paused)) {
			screenMap.put(ScreenMap.Paused, new ScreenPaused(this));
		}
		// no screenshot change when already on paused screen
		if (getActiveScreen() == ScreenMap.Paused) {
			return;
		}
		// get screenshot
		((ScreenPaused) screenMap.get(ScreenMap.Paused)).takeScreenshot();
	}

	public void setActiveScreen(ScreenMap activeScreen) {
		this.activeScreen = activeScreen;
	}

	public void setChallengeAudio(boolean challengeAudio) {
		this.challengeAudio = challengeAudio;
		getOptions().putBoolean(GSetting.ChallengeAudioMode.name(),
				challengeAudio);
		getOptions().flush();
	}

	public void setChallengeWord(ChallengeWordMode challengeWord) {
		this.challengeWord = challengeWord;
		getOptions().putString(GSetting.ChallengeWordMode.name(),
				challengeWord.name());
		getOptions().flush();
	}

	public void setChallengeWord(String challengeWord) {
		try {
			setChallengeWord(ChallengeWordMode.valueOf(challengeWord));
		} catch (Exception e) {
			setChallengeWord(ChallengeWordMode.Syllabary);
		}
	}

	public void setGameScreenLevel(int level) {
		if (level < 1)
			level = 1;
		if (level > levels)
			level = levels;
		setLevelOn(level);
		setNewGameScreen(true);
	}

	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}

	public void setLevelAccuracy(int ix, int percent) {
		if (ix < 0 || ix > levelAccuracy.length) {
			return;
		}
		if (percent < 0) {
			percent = 0;
		}
		if (percent > 100) {
			percent = 100;
		}
		levelAccuracy[ix] = percent;
		getOptions().putInteger(GSetting.LevelAccuracy.name() + ix, percent);
	}

	private void setLevelOn(int levelOn) {
		this.levelOn = levelOn;
	}

	public void setNewGameScreen(boolean newGameScreen) {
		this.newGameScreen = newGameScreen;
	}

	public void setShowTrainingScreen(TrainingScreenMode showTrainingScreen) {
		this.showTrainingScreen = showTrainingScreen;
		getOptions().putString(GSetting.ShowTrainingScreen.name(),
				showTrainingScreen.name());
		getOptions().flush();
	}

	public void setSoundEffectsVolume(SoundEffectVolume soundEffectsVolume) {
		this.soundEffectsVolume = soundEffectsVolume;
		getOptions().putString(GSetting.SoundEffectsOn.name(),
				soundEffectsVolume.name());
		getOptions().flush();
	}

	public void setSoundEffectsVolume(String soundEffectsVolume) {
		try {
			setSoundEffectsVolume(SoundEffectVolume.valueOf(soundEffectsVolume));
		} catch (Exception e) {
			setSoundEffectsVolume(SoundEffectVolume.On);
		}
	}

	public void setSoundManager(SoundManager soundManager) {
		this.soundManager = soundManager;
	}

	public void setVolume(int newVolume) {
		if (newVolume < 0) {
			newVolume = 0;
		}
		if (newVolume > 100) {
			newVolume = 100;
		}
		displayVolume = newVolume;
		masterVolume = ((float) newVolume) / 100f;
		getOptions().putInteger(GSetting.MasterVolume.name(), displayVolume);
		getOptions().flush();
	}

	private HashMap<String, String> translationMaps() {
		int ix = 0;
		String letter;
		String prefix;
		char chrStart = 'Ꭰ';
		String[] vowels = new String[6];

		HashMap<String, String> syllabary2latin = new HashMap<String, String>();

		vowels[0] = "a";
		vowels[1] = "e";
		vowels[2] = "i";
		vowels[3] = "o";
		vowels[4] = "u";
		vowels[5] = "v";

		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, vowels[ix]);
			syllabary2latin.put(vowels[ix], letter);
		}

		syllabary2latin.put("ga", "Ꭶ");
		syllabary2latin.put("Ꭶ", "ga");

		syllabary2latin.put("ka", "Ꭷ");
		syllabary2latin.put("Ꭷ", "ka");

		prefix = "g";
		chrStart = 'Ꭸ';
		for (ix = 1; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix - 1));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "k";
		chrStart = 'Ꭸ';
		for (ix = 1; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix - 1));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "h";
		chrStart = 'Ꭽ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "l";
		chrStart = 'Ꮃ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "m";
		chrStart = 'Ꮉ';
		for (ix = 0; ix < 5; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		syllabary2latin.put("Ꮎ", "na");
		syllabary2latin.put("na", "Ꮎ");
		syllabary2latin.put("Ꮏ", "hna");
		syllabary2latin.put("hna", "Ꮏ");
		syllabary2latin.put("Ꮐ", "nah");
		syllabary2latin.put("nah", "Ꮐ");

		prefix = "n";
		chrStart = 'Ꮑ';
		for (ix = 1; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix - 1));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "qu";
		chrStart = 'Ꮖ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "gw";
		chrStart = 'Ꮖ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		syllabary2latin.put("Ꮜ", "sa");
		syllabary2latin.put("sa", "Ꮜ");
		syllabary2latin.put("Ꮝ", "s");
		syllabary2latin.put("s", "Ꮝ");

		prefix = "s";
		chrStart = 'Ꮞ';
		for (ix = 1; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix - 1));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		syllabary2latin.put("da", "Ꮣ");
		syllabary2latin.put("Ꮣ", "da");
		syllabary2latin.put("ta", "Ꮤ");
		syllabary2latin.put("Ꮤ", "ta");
		syllabary2latin.put("de", "Ꮥ");
		syllabary2latin.put("Ꮥ", "de");
		syllabary2latin.put("te", "Ꮦ");
		syllabary2latin.put("Ꮦ", "te");
		syllabary2latin.put("di", "Ꮧ");
		syllabary2latin.put("Ꮧ", "di");
		syllabary2latin.put("ti", "Ꮨ");
		syllabary2latin.put("Ꮨ", "ti");
		syllabary2latin.put("do", "Ꮩ");
		syllabary2latin.put("Ꮩ", "do");
		syllabary2latin.put("to", "Ꮩ");
		syllabary2latin.put("Ꮩ", "to");
		syllabary2latin.put("du", "Ꮪ");
		syllabary2latin.put("Ꮪ", "du");
		syllabary2latin.put("tu", "Ꮪ");
		syllabary2latin.put("Ꮪ", "tu");
		syllabary2latin.put("dv", "Ꮫ");
		syllabary2latin.put("Ꮫ", "dv");
		syllabary2latin.put("tv", "Ꮫ");
		syllabary2latin.put("Ꮫ", "tv");
		syllabary2latin.put("dla", "Ꮬ");
		syllabary2latin.put("Ꮬ", "dla");

		prefix = "hl";
		chrStart = 'Ꮭ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "tl";
		chrStart = 'Ꮭ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "j";
		chrStart = 'Ꮳ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "ts";
		chrStart = 'Ꮳ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "w";
		chrStart = 'Ꮹ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}

		prefix = "y";
		chrStart = 'Ꮿ';
		for (ix = 0; ix < 6; ix++) {
			letter = Character.toString((char) (chrStart + ix));
			syllabary2latin.put(letter, prefix + vowels[ix]);
			syllabary2latin.put(prefix + vowels[ix], letter);
		}
		return syllabary2latin;
	}

	public static BitmapFont getFont(FontStyle style, int fontSize) {
		System.out.println("FONT: "+style+" ["+fontSize+"]");
		BitmapFont bf = new BitmapFont(Gdx.files.internal("fonts/"+style.name()+fontSize+".fnt"));
		return bf;
	}

	public static BitmapFont getFixedFont(FontStyle style, int fontSize) {
		System.out.println("FIXED: "+style+" ["+fontSize+"]");
		BitmapFont bf = new BitmapFont(Gdx.files.internal("fonts/"+style.name()+fontSize+".fnt"));
		bf.setFixedWidthGlyphs("01234567890-+,");
		return bf;
	}
}

enum GSetting {
	ChallengeAudioMode, ChallengeWordMode, LevelAccuracy, MasterVolume, ShowTrainingScreen, SoundEffectsOn,
}
