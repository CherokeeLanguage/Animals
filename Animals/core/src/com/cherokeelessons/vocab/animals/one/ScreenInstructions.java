package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Array;
import com.cherokeelessons.vocab.animals.one.CherokeeAnimals.FontStyle;

public class ScreenInstructions extends ScreenGameCore {

	final private Group instructions=new Group();
	
	final Array<Sprite> wall = new Array<Sprite>();
	private TextureAtlas wall_atlas;

	public ScreenInstructions(CherokeeAnimals game) {
		super(game);		
	}

	private void initScreen() {
		gameStage.clear();
		gameStage.addActor(instructions);
		initBackdrop();
		init();
	}
	
	private String[] credits = {
			"-Instructions-",
			"",
			"Choose 'New Game' and then select a starting level with [O].",
			"After selecting a level you will be presented with a training screen.",
			"The training screen will show a new challenge and sound out its",
			"name several times. After the training screen completes you will",
			"be presented with a game board and will be given a challenge. Navigate",
			"the game board using the left stick on your game pad and press [O]",
			"when you have highlighted the correct answer. If only one picture",
			"matches the challenge, the board will clear and you will move on to the",
			"next challenge. If more than one picture matches the challenge,",
			"each correct picture chosen by pressing [O] will turn into a green",
			"checkmark. If you choose an incorrect picture, it will be replaced by an 'X'.",
			"",
			"You must get 80% or better on each level before",
			"you are allowed to advance to the next level.",
			"",
			"Press [A] to exit."
			};
	private TextButton[] scrollingCredits;
	private Color fontColor = GameColor.DARKGREEN;
	public void init() {

		scrollingCredits = new TextButton[credits.length];

		calculateFontSize();
		
		populateCreditDisplay();
		if (instructions.getHeight()>overscan.height) {
			float fontScale = overscan.height/instructions.getHeight();
			fontSize=(int)((float)fontSize*fontScale);
			instructions.clear();
			populateCreditDisplay();
		}	

	}
	private int fontSize;
	private void populateCreditDisplay() {
		int ix = 0;
		BitmapFont font;
		maxLineHeight = 0;
		TextButton creditLine;
		TextButtonStyle style;
		font = CherokeeAnimals.getFont(CherokeeAnimals.FontStyle.Script, fontSize);
		for (ix = 0; ix < credits.length; ix++) {
			if (credits[ix].length() < 1) {
				scrollingCredits[ix] = null;
				continue;
			}
			style = new TextButtonStyle();
			style.font = font;
			style.fontColor = new Color(fontColor);
			creditLine = new TextButton(credits[ix], style);
			creditLine.pack();
			scrollingCredits[ix] = creditLine;
			if (font.getLineHeight() > maxLineHeight)
				maxLineHeight = font.getLineHeight();

		}
		for (ix = 0; ix < credits.length; ix++) {
			creditLine = scrollingCredits[ix];
			if (creditLine == null) {
				continue;
			}
			creditLine.setX((overscan.width - creditLine.getWidth()) / 2);
			creditLine
					.setY((credits.length - ix - 1) * maxLineHeight);
			instructions.addActor(creditLine);
		}
		// store my dimensions
		instructions.setWidth(overscan.width);
		instructions.setHeight(maxLineHeight * credits.length);
		instructions.setX(overscan.x);
		instructions.setY(overscan.y);
		instructions.setOrigin(overscan.width/2, instructions.getHeight()/2);		
	}
	private float maxLineHeight;
	private void calculateFontSize() {
		// starting size
		TextButton testLabel = null;
		TextButtonStyle testStyle;
		int size;
		int ix;
		BitmapFont font;
		float scale = 0;
		float maxWidth = 0;

		size = 72;
		font = CherokeeAnimals.getFont(CherokeeAnimals.FontStyle.Script,size);
		testStyle = new TextButtonStyle();
		testStyle.font = font;
		testStyle.fontColor = new Color(fontColor);
		testLabel = new TextButton("", testStyle);
		font = CherokeeAnimals.getFont(CherokeeAnimals.FontStyle.Script,size);
		for (ix = 0; ix < credits.length; ix++) {
			if (credits[ix].length() < 1)
				continue;
			testLabel.setText(credits[ix]);
			testLabel.setStyle(testStyle);
			testLabel.pack();
			if (maxWidth < testLabel.getWidth()) {
				maxWidth = testLabel.getWidth();
			}

		}
		scale = 0.95f * overscan.width / maxWidth;
		size = (int) (scale * (float) size);
		fontSize = size;
	}

	@Override
	public void dispose() {		
		super.dispose();
	}

	private void discardResources() {
		wall_atlas.dispose();
		wall_atlas = null;
		instructions.clear();
		gameStage.clear();
	}

	@Override
	public void hide() {
		super.hide();
		discardResources();
	}

	private void initBackdrop() {
		wall.clear();
		PixmapPacker pack = new PixmapPacker(1024, 1024, Format.RGBA8888, 2,
				true);
		for (int i = 0; i < 32; i++) {
			pack.pack(
					i + "",
					new Pixmap(Gdx.files.internal("images/backdrops/p_" + i
							+ "_dsci2549.png")));
		}
		wall_atlas = pack.generateTextureAtlas(TextureFilter.Linear,
				TextureFilter.Linear, false);

		int px = 0;
		int py = 0;
		final int perRow = 8;
		final int columns = 4;
		for (int x = 0; x < perRow; x++) {
			py = 0;
			Sprite i = null;
			for (int y = 0; y < columns; y++) {
				int z = columns - (y + 1);
				int p = z * perRow + x;
				final AtlasRegion piece = wall_atlas.findRegion(p + "");
				i = new Sprite(piece, 0, 0, piece.getRegionWidth(),
						piece.getRegionHeight());
				i.setX(px);
				i.setY(py);
				i.setColor(1f, 1f, 1f, 0.35f);
				py += i.getHeight();
				wall.add(i);
			}
			px += i.getWidth();
		}
	}

	@Override
	public void render(float delta) {
		clearScreen();
		if (showOverScan) {
			drawOverscan();
		}
		batch.begin();
		for (Sprite s : wall) {
			s.draw(batch);
		}
		batch.end();
		gameStage.act();
		gameStage.draw();
	}

	@Override
	public void show() {
		super.show();
		initScreen();
	}

}
