package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.common.DisplaySize;
import com.cherokeelessons.common.FontLoader;
import com.cherokeelessons.common.GameEventMessage;
import com.cherokeelessons.common.GameMusic;
import com.cherokeelessons.common.IAP;
import com.cherokeelessons.common.IapNOP;
import com.cherokeelessons.common.Leader;
import com.cherokeelessons.common.MusicAccessor;
import com.cherokeelessons.common.Prefs;
import com.cherokeelessons.common.SoundManager;
import com.cherokeelessons.common.SpriteAccessor;
import com.cherokeelessons.vocab.animals.one.enums.GameEvent;

import aurelienribon.tweenengine.Tween;

public class CherokeeAnimals implements ApplicationListener {

//	private static final String BAD_GL_VERSION = "Sorry! But your device does not support GL ES 2.0!";

	final public static DisplaySize size = DisplaySize._1080p;

	public Prefs prefs;
	public TextureAtlas images_atlas;
	protected IAP iap = new IapNOP();

	public void setIap(IAP iap) {
		this.iap = iap;
	}

	public IAP getIap() {
		return iap;
	}

	public CherokeeAnimals() {
	}

	public SoundManager sm;

	public FontLoader fg;

	public MusicPlayer musicPlayer;

	@Override
	public void create() {
		
//		if (!Gdx.graphics.isGL20Available()) {
//			if (toaster!=null) {
//				toaster.toast(BAD_GL_VERSION, Length.Long);
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//				}
//				toaster.toast("Upgrade your hardware!", Length.Long);
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//				}
//				Gdx.app.exit();
//				try {
//					Thread.sleep(250);
//				} catch (InterruptedException e) {
//				}
//				System.exit(0);
//			}
//		}
		
		Gdx.app.log(this.getClass().getSimpleName(), "Initial Heap memory: "+Gdx.app.getJavaHeap());
		Gdx.app.log(this.getClass().getSimpleName(), "Initial Native memory: "+Gdx.app.getNativeHeap());
		Gdx.app.log(this.getClass().getSimpleName(), "Initial Free memory: "+Runtime.getRuntime().freeMemory());
		
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);

		prefs = new Prefs(this);
		
		Leader.uuid=prefs.getUuid();
		final Leader lb = new Leader();
		Runnable cb_uuid=new Runnable() {
			@Override
			public void run() {
				prefs.setUuid(Leader.uuid);
			}
		};
		lb.checkUuid(Leader.uuid, cb_uuid);
		
		Tween.registerAccessor(GameMusic.class, new MusicAccessor());
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());

		sm = new SoundManager(prefs);
		fg = new FontLoader();

		gameEvent(GameEvent.libGdx);
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
		switch (event.getEvent()) {
		case libGdx:
			setScreen(new ScreenPoweredBy(this));
			break;
		case Done:
			if (getScreen() instanceof ScreenMainMenu) {
				// ignore, ScreenMainMenu is the furthest back we allow
				break;
			}
			if (getScreen() instanceof ScreenPoweredBy) {
				setScreen(new ScreenLoading(this));
				break;
			}
			if (getScreen() instanceof ScreenLoading) {
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
			if (screenGameplay == null) {
				screenGameplay = new ScreenGameplay(this);
			}
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
		case ShowOptions:
			if (getScreen() instanceof ScreenPoweredBy) {
				break;
			}
			if (getScreen() instanceof ScreenLoading) {
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
		if (screen != null)
			screen.hide();
	}

	@Override
	public void pause() {
		if (screen != null)
			screen.pause();
	}

	@Override
	public void resume() {
		if (screen != null)
			screen.resume();
	}

	@Override
	public void render() {
		if (screen != null)
			screen.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void resize(int width, int height) {
		if (screen != null)
			screen.resize(width, height);
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
			this.screen.resize(Gdx.graphics.getWidth(),
					Gdx.graphics.getHeight());
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
}
