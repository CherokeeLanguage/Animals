package com.cherokeelessons.animals;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.animals.enums.GameEvent;
import com.cherokeelessons.common.FontLoader;
import com.cherokeelessons.common.GameEventMessage;
import com.cherokeelessons.common.GameMusic;
import com.cherokeelessons.common.ImageAccessor;
import com.cherokeelessons.common.MusicAccessor;
import com.cherokeelessons.common.Prefs;
import com.cherokeelessons.common.SoundManager;
import com.cherokeelessons.util.DreamLo;

import aurelienribon.tweenengine.Tween;

public class CherokeeAnimals implements ApplicationListener, TvDetector {
	
	public Prefs prefs;
	public TextureAtlas images_atlas;

	public CherokeeAnimals() {
		/**
		 * Assume *not* a TV unless set otherwise.
		 */
		tvDetector = new TvDetector() {
			@Override
			public boolean isTelevision() {
				return false;
			}
		};
	}

	public SoundManager sm;

	public FontLoader fg;

	public MusicPlayer music;

	@Override
	public void create() {
		
		Gdx.input.setCatchKey(Input.Keys.BACK, true);
		Gdx.input.setCatchKey(Input.Keys.MENU, true);

		prefs = new Prefs(this);
		
		Gdx.app.log(this.getClass().getName(), "DreamLo: "+new DreamLo(prefs).registerWithDreamLoBoard());
		
		Tween.registerAccessor(GameMusic.class, new MusicAccessor());
		Tween.registerAccessor(Image.class, new ImageAccessor());

		sm = new SoundManager(prefs);
		fg = new FontLoader();

		gameEvent(GameEvent.libGdx);
		
		music = new MusicPlayer();
		music.loadUsingPlist();
		float masterVolume = ((float)prefs.getMasterVolume())/100f;
		float musicVolume = ((float)prefs.getMusicVolume())/100f;
		music.setVolume(masterVolume*musicVolume);
	}

	protected float elapsed = 0;

	private ScreenLevelSelect screenLevelSelect;

	private ScreenMainMenu screenMainMenu;

	private ScreenGameplay screenGameplay;

	private ScreenTrainer screenTrainer;

	private ScreenLevelComplete screenLevelComplete;

	private ScreenInstructions screenInstructions;

	private ScreenOptionsMenu screenOptions;

	private ScreenCredits screenCredits;

	public void gameEvent(final GameEvent gameEvent) {
		Gdx.app.log(this.getClass().getName(), "gameEvent: "+gameEvent.name());
		gameEvent(new GameEventMessage(gameEvent));
	}
	
	public void gameEvent(final GameEventMessage event) {
		Gdx.app.log(this.getClass().getName(), "gameEventMessage: "+event.getEvent().name());
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				_gameEvent(event);
			}
		});
	}
	//@Subscribe
	private void _gameEvent(GameEventMessage event) {
		GameScreen activeScreen = getScreen();
		switch (event.getEvent()) {
		case ShowLeaderBoard:
			setScreen(new ScreenHighScores(this));
			break;
		case libGdx:
			setScreen(new ScreenPoweredBy(this));
			break;
		case Done:
			if (activeScreen==null) {
				break;
			}
			if (activeScreen instanceof ScreenMainMenu) {
				((ScreenMainMenu)activeScreen).maybeQuit();
				break;
			}
			if (activeScreen instanceof ScreenPoweredBy) {
				setScreen(new ScreenLoading(this));
				break;
			}
			if (activeScreen instanceof ScreenLoading) {
				gameEvent(GameEvent.MainMenu);
				break;
			}
			if (activeScreen instanceof ScreenLevelComplete) {
				gameEvent(GameEvent.MainMenu);
				break;
			}
			goPrevScreen();
			break;
		case NewGame:
			if (screenLevelSelect == null) {
				screenLevelSelect = new ScreenLevelSelect(this);
			}
			setScreen(screenLevelSelect, false);// no history
			break;
		case ShowGameBoard:
			if (screenGameplay != null) {
				screenGameplay.dispose();
			}
			screenGameplay=new ScreenGameplay(this);
			screenGameplay.initLevel(levelOn);
			setScreen(screenGameplay);
			break;
		case Training:
			if (screenTrainer == null) {
				screenTrainer = new ScreenTrainer(this);
			}
			setScreen(screenTrainer, false);// no history
			break;
		case LevelComplete:
			if (screenLevelComplete == null) {
				screenLevelComplete = new ScreenLevelComplete(this);
			}
			setScreen(screenLevelComplete, false);// no history
			break;
		case MainMenu:
			if (screenMainMenu == null) {
				screenMainMenu = new ScreenMainMenu(this);
			}
			setScreen(screenMainMenu);
			break;
		case NoEvent:
			break;
		case ShowInstructions:
			if (screenInstructions == null) {
				screenInstructions = new ScreenInstructions(this);
			}
			setScreen(screenInstructions, false);
			break;
		case Menu:
			if (activeScreen==null) {
				break;
			}
			if (activeScreen instanceof ScreenLevelComplete) {
				break;
			}
			if (activeScreen instanceof ScreenPoweredBy) {
				break;
			}
			if (activeScreen instanceof ScreenLoading) {
				break;
			}
			if (activeScreen instanceof ScreenGameplay) {
				ScreenGameplay sg = ((ScreenGameplay)activeScreen);
				sg.setPaused(!sg.isPaused());
				Gdx.app.log("PAUSED: ",Boolean.toString(sg.isPaused()));
				break;
			}
			if (screenOptions == null) {
				screenOptions = new ScreenOptionsMenu(this);
			}
			setScreen(screenOptions, false);
			break;		
		case QuitGame:
			setScreen(new ScreenQuit(this), false);
			Gdx.app.exit();
			break;
		case ShowCredits:
			if (screenCredits == null) {
				screenCredits = new ScreenCredits(this);
			}
			setScreen(screenCredits, false);
			break;
		default:
			System.out.println("Event: " + event.getEvent().name());
		}
	}

	protected GameScreen screen;

	@Override
	public void dispose() {
		if (screen != null) {
			screen.hide();
			screen = null;
		}
		if (music !=null) {
			music.pause();
			music.dispose();
			music = null;
		}
	}

	@Override
	public void pause() {
		if (screen != null) {
			screen.pause();
		}
	}

	@Override
	public void resume() {
		if (screen != null) {
			screen.resume();
		}
	}

	@Override
	public void render() {
		if (screen != null) {
			screen.render(Gdx.graphics.getDeltaTime());
		}
	}

	protected void log(String message) {
		Gdx.app.log(this.getClass().getName(), message);
	}
	
	@Override
	public void resize(int width, int height) {
		log("app resize: "+width+"x"+height);
		if (screen != null) {
			screen.resize(width, height);
		}
	}

	/**
	 * Sets the current screen. {@link Screen#hide()} is called on any old
	 * screen, and {@link Screen#show()} is called on the new screen, if any.
	 * 
	 * @param screen
	 *            may be {@code null}
	 */
	public void setScreen(GameScreen screen) {
		setScreen(screen, true);
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void recordScreen(GameScreen screen) {
		if (screen == null) {
			return;
		}
		prevScreen.add(screen);
	}

	public void removeFromHistory(GameScreen screen) {
		if (screen == null) {
			return;
		}
		for (int ix = 0; ix < prevScreen.size; ix++) {
			if (prevScreen.get(ix).equals(screen)) {
				prevScreen.removeIndex(ix);
				ix--;
			}
		}
	}

	public void setScreen(GameScreen newScreen, boolean keepHistory) {
		if (this.screen != null) {
			this.screen.hide();
		}
		this.screen = newScreen;
		if (this.screen != null) {
			removeFromHistory(this.screen);
			if (keepHistory) {
				recordScreen(this.screen);
			}
			this.screen.show();
		}
	}

	final Array<GameScreen> prevScreen = new Array<GameScreen>();

	public void goPrevScreen() {
		if (this.screen != null) {
			if (this.screen.equals(prevScreen.peek())) {
				prevScreen.pop();
			}
		}
		if (prevScreen.size > 0) {
			setScreen(prevScreen.peek());
		}
	}

	public GameScreen getPrevScreen() {
		if (prevScreen.size > 1) {
			return prevScreen.get(prevScreen.size - 2);
		}
		return null;
	}

	/** @return the currently active {@link Screen}. */
	public GameScreen getScreen() {
		return screen;
	}

	private int levelOn = 1;

	private int levels = 1;

	public LoadChallenges challenges;

	public String activeChallenge;

	private TvDetector tvDetector;

	public void setLevels(int levelcount) {
		levels = levelcount;
	}

	public int getLevels() {
		return levels;
	}

	public int getMinPercent() {
		return 80;
	}

	public int getLevelOn() {
		return levelOn;
	}

	public void setLevelOn(int levelOn) {
		this.levelOn = levelOn;
	}

	public void setIsTelevisionDetector(TvDetector detector) {
		this.tvDetector = detector;
	}
	
	private Boolean _isTelevision;
	@Override
	public boolean isTelevision() {
		if (_isTelevision==null) {
			_isTelevision = this.tvDetector.isTelevision();
		}
		return _isTelevision;
	}
}
