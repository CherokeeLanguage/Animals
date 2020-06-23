package com.cherokeelessons.animals.views;

import java.util.Random;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cherokeelessons.common.FontLoader;
import com.cherokeelessons.common.GameColor;

public class View3x3Selector extends Group {

	public interface onClick {
		void handleClick(int button);
	}

	private final int baseFontSize = 48;
	private int bottomMargin = 0;
	private int boxMargin = 0;

	private final Group buttonGroup;

	private final Image[] buttons = new Image[9];

	FontLoader fg;

	private BitmapFont font = null;

	private onClick handler = null;

	final private Rectangle screenSize = new Rectangle();

	private final Label titleBox;
	private final LabelStyle titleStyle;

	private final float topMargin = 5;

	private final Texture[] textureCache = new Texture[buttons.length];

	public View3x3Selector(final Rectangle overscan) {
		super();
		screenSize.set(overscan);
		fg = new FontLoader();
		font = fg.get(baseFontSize);
		titleStyle = new LabelStyle(font, GameColor.MAIN_TEXT);
		titleBox = new Label(" ", titleStyle);
		this.addActor(titleBox);
		buttonGroup = new Group();
		this.addActor(buttonGroup);
		initializeButtons();
		setTitle(" ");
	}

	public void addAction(int buttonIX, final Action action) {
		buttonIX %= buttons.length;
		buttons[buttonIX].addAction(action);
	}

	public void addListener(final int button, final EventListener clickListener) {
		buttons[button].addListener(clickListener);
	}

	public int button_count() {
		return buttons.length;
	}

	public void clearActions(int buttonIX) {
		buttonIX %= buttons.length;
		buttons[buttonIX].clearActions();
	}

	public void clearListeners(final int button) {
		buttons[button].clearListeners();
	}

	public void focusOn(int buttonIX) {
		int ix;

		buttonIX %= buttons.length;
		for (ix = 0; ix < buttons.length; ix++) {
			buttons[ix].setVisible(false);
			buttons[ix].setRotation(0);
		}
		buttons[buttonIX].setVisible(true);
		positionButton(buttons[buttonIX], getRegionBox());
	}

	public int getBottomMargin() {
		return bottomMargin;
	}

	public Rectangle getBoundingBox(int buttonIX) {
		int row;
		int col;
		int colWidth;
		int rowHeight;
		Rectangle boundingBox;
		int perRow, perColumn;

		buttonIX %= buttons.length;

		perRow = 3;
		perColumn = 3;

		row = buttonIX / perRow;
		row = 2 - row;

		col = buttonIX % perColumn;

		colWidth = (int) screenSize.width / perRow;
		rowHeight = (int) (screenSize.height - titleStyle.font.getLineHeight()// titleBox.getHeight()
				- topMargin - bottomMargin) / perColumn;

		boundingBox = new Rectangle();

		boundingBox.x = col * colWidth + boxMargin;
		boundingBox.y = row * rowHeight + boxMargin + bottomMargin;
		boundingBox.height = rowHeight - boxMargin * 2;
		boundingBox.width = colWidth - boxMargin * 2;

		return boundingBox;
	}

	public int getBoxMargin() {
		return boxMargin;
	}

	public onClick getHandler() {
		return handler;
	}

	public Rectangle getRegionBox() {
		final Rectangle boundingBox = new Rectangle();

		boundingBox.x = getBoundingBox(0).x;
		boundingBox.y = getBoundingBox(8).y;
		boundingBox.width = getBoundingBox(8).x + getBoundingBox(8).width - boundingBox.x;
		boundingBox.height = getBoundingBox(0).y + getBoundingBox(0).height - boundingBox.y;

		return boundingBox;
	}

	private void initializeButtons() {
		int ix;
		for (ix = 0; ix < buttons.length; ix++) {
			buttons[ix] = new Image();
			buttonGroup.addActor(buttons[ix]);
		}
	}

	private void positionButton(final Image button, final Rectangle bbox) {
		float scaleX, scaleY;
		float width, height;

		if (button.getDrawable() == null) {
			return;
		}

		button.pack();

		width = button.getImageWidth();
		height = button.getImageHeight();
		button.setOriginX(width / 2);
		button.setOriginY(height / 2);

		float scaleSize = bbox.width;
		if (bbox.width > bbox.height) {
			scaleSize = bbox.height;
		}

		scaleX = scaleSize / width;
		scaleY = scaleSize / height;

		if (scaleX > scaleY) {
			scaleX = scaleY;
		} else {
			scaleY = scaleX;
		}

		button.clearActions();
		button.setX(bbox.x + bbox.width / 2 - width / 2);
		button.setY(bbox.y + bbox.height / 2 - height / 2);
		button.setScaleX(scaleX);
		button.setScaleY(scaleY);
	}

	private void resetAttributes(final int button) {
		final Image image = buttons[button];
		image.clearActions();
		image.setScale(1, 1);
		image.setRotation(0);
		image.setOrigin(0, 0);
		image.setColor(Color.WHITE);
	}

	public void scatter() {
		int ix;
		final Random r = new Random();
		Image button;
		float x, y;
		for (ix = 0; ix < buttons.length; ix++) {
			button = buttons[ix];
			do {
				switch (r.nextInt(3)) {
				case 0:
					x = 0;
					break;
				case 1:
					x = screenSize.width + screenSize.x * 3;
					break;
				default:
					x = -screenSize.width - screenSize.x * 3;
					break;
				}
				switch (r.nextInt(3)) {
				case 0:
					y = 0;
					break;
				case 1:
					y = screenSize.height + screenSize.y * 3;
					break;
				default:
					y = -screenSize.height - screenSize.y * 3;
					break;
				}
			} while (x == 0 && y == 0);
			Interpolation inter;
			switch (r.nextInt(9)) {
			case 0:
				inter = Interpolation.bounce;
				break;
			case 1:
				inter = Interpolation.bounceIn;
				break;
			case 2:
				inter = Interpolation.bounceOut;
				break;
			case 3:
				inter = Interpolation.circle;
				break;
			case 4:
				inter = Interpolation.circleIn;
				break;
			case 5:
				inter = Interpolation.circleOut;
				break;
			case 6:
				inter = Interpolation.swing;
				break;
			case 7:
				inter = Interpolation.swingIn;
				break;
			default:
				inter = Interpolation.swingOut;
				break;
			}
			button.addAction(Actions.moveTo(x, y, 2.5f, inter));
			button.addAction(Actions.rotateBy(360 + 360 * r.nextInt(5), 2.5f, Interpolation.elastic));
		}
	}

	public void setAlpha(int buttonIX, float alpha) {
		if (alpha < 0) {
			alpha = 0;
		}
		if (alpha > 1) {
			alpha = 1;
		}
		buttonIX %= buttons.length;
		buttons[buttonIX].getColor().a = alpha;
	}

	public void setBottomMargin(final float bottomMargin) {
		this.bottomMargin = (int) bottomMargin;
	}

	public void setBottomMargin(final int bottomMargin) {
		this.bottomMargin = bottomMargin;
	}

	public void setBoxMargin(final int boxMargin) {
		this.boxMargin = boxMargin;
	}

	public void setColor(int buttonIX, final Color color) {
		buttonIX %= buttons.length;
		buttons[buttonIX].getColor().set(color);
	}

	public void setHandler(final onClick handler) {
		this.handler = handler;
	}

	public void setImage(int buttonIX, final FileHandle imageFile) {
		Image button;
		Rectangle bbox;
		Texture image;
		TextureRegion imageRegion;

		buttonIX %= buttons.length;

		resetAttributes(buttonIX);

		button = buttons[buttonIX];
		if (imageFile == null) {
			button.setDrawable(null);
			return;
		}
		if (button.getDrawable() != null) {
			button.setDrawable(null);
		}
		bbox = getBoundingBox(buttonIX);
		if (textureCache[buttonIX] != null) {
			textureCache[buttonIX].dispose();
		}
		image = new Texture(imageFile);
		textureCache[buttonIX] = image;
		image.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		imageRegion = new TextureRegion(image);
		final TextureRegionDrawable trd = new TextureRegionDrawable(imageRegion);
		button.setDrawable(trd);
		positionButton(button, bbox);
	}

	public void setTitle(final String title) {
		float scale = 1;

		titleStyle.font = font;

		titleBox.setText(title);
		titleBox.setStyle(titleStyle);
		titleBox.pack();
		scale = screenSize.width / titleBox.getWidth();

		titleBox.setScaleX(scale);
		titleBox.setScaleY(scale);
		titleBox.setOriginX(titleBox.getWidth() / 2);
		titleBox.setOriginY(titleStyle.font.getLineHeight() / 2);// titleBox.getHeight()
																	// / 2);
		titleBox.setX((screenSize.width - titleBox.getWidth()) / 2);
		titleBox.setY(screenSize.height - topMargin - titleStyle.font.getLineHeight());// titleBox.getHeight());
	}

	public void unFocusOn() {
		int ix;
		for (ix = 0; ix < buttons.length; ix++) {
			buttons[ix].setVisible(true);
			buttons[ix].setRotation(0);
			positionButton(buttons[ix], getBoundingBox(ix));
		}
	}

}
