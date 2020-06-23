package com.cherokeelessons.animals.views;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/*
 * holds "buttons" to tap for matchup against challenge
 */

public class ViewGameBoard extends Group {

	private final Image[] board = new Image[6];
	private final Texture[] textureCache = new Texture[board.length];
	private final int bottomGap = 80;

	private final int boxMargin = 16;

	final private Rectangle screenSize = new Rectangle();
	private final int topGap = 80;

	public ViewGameBoard(final Rectangle overscan) {
		super();
		screenSize.set(overscan);
		setX(0);
		setY(bottomGap);
		populateBoard();
	}

	public void addAction(int buttonIX, final Action action) {
		buttonIX %= board.length;
		board[buttonIX].addAction(action);
	}

	public void addListener(final int ix, final EventListener listener) {
		board[ix].addListener(listener);
	}

//	public void click(int ix) {
//		if (ix < 0 || ix >= clickers.length) {
//			return;
//		}
//		if (ix < 0 || ix >= board.length) {
//			return;
//		}
//		if (board[ix] != null) {
//			if (clickers[ix] != null) {
//				clickers[ix].doClick(board[ix]);
//			}
//		}
//	}

	public int button_count() {
		return board.length;
	}

	public void clearListeners(final int ix) {
		board[ix].clearListeners();
	}

	public void flyaway(final int button) {
		board[button].addAction(Actions.scaleTo(0f, 0f, .4f));
		board[button].addAction(Actions.fadeOut(.3f));
	}

	public void flytowards(final int button) {
		board[button].addAction(Actions.scaleTo(5f, 5f, .4f));
		board[button].addAction(Actions.fadeOut(.3f));
	}

	private Rectangle getBoundingBox(final int buttonIX) {
		int row;
		int col;
		int colWidth;
		int rowHeight;
		Rectangle boundingBox;
		int perRow, perColumn;

		perRow = 3;
		perColumn = 2;

		row = button_count() / perRow - 1 - buttonIX / perRow;
		col = buttonIX % perRow;

		colWidth = (int) screenSize.width / perRow;
		rowHeight = (int) (screenSize.height - topGap - bottomGap) / perColumn;

		boundingBox = new Rectangle();

		boundingBox.x = col * colWidth + boxMargin;
		boundingBox.y = row * rowHeight + boxMargin;
		boundingBox.height = rowHeight - boxMargin * 2;
		boundingBox.width = colWidth - boxMargin * 2;

		return boundingBox;
	}

	private void populateBoard() {
		int ix;
		for (ix = 0; ix < board.length; ix++) {
			board[ix] = new Image();
			setImage(ix, null);
			addActor(board[ix]);
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
		board[button].clearActions();
		board[button].setScale(1, 1);
		board[button].getColor().a = 1;
		board[button].setRotation(0);
		board[button].setOrigin(0, 0);
		board[button].setColor(Color.WHITE);
	}

	public void setAlpha(final int button, final float alpha) {
		board[button].getColor().a = alpha;
	}

	public void setColor(final int button, final Color color) {
		board[button].setColor(color);
	}

	public void setImage(final int buttonIX, final FileHandle imageFile) {
		final Image button = board[buttonIX];
		final Rectangle bbox = getBoundingBox(buttonIX);
		Texture image;
		TextureRegion imageRegion;
		float scaleX, scaleY;

		resetAttributes(buttonIX);

		if (imageFile == null) {
			button.setDrawable(null);
			return;
		}
		if (textureCache[buttonIX] != null) {
			textureCache[buttonIX].dispose();
		}
		image = new Texture(imageFile);
		textureCache[buttonIX] = image;
		image.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		imageRegion = new TextureRegion(image);
		scaleX = bbox.width / image.getWidth();
		scaleY = bbox.height / image.getHeight();
		if (scaleX > scaleY) {
			scaleX = scaleY;
		} else {
			scaleY = scaleX;
		}
		final Drawable prev = button.getDrawable();
		if (prev != null && prev instanceof TextureRegionDrawable) {
			button.setDrawable(null);
		}

		button.setDrawable(new TextureRegionDrawable(imageRegion));
		positionButton(button, bbox);
	}

	public void spin(final int button) {
		board[button].addAction(Actions.rotateBy(360, 1));
	}

	public int whichButton(final Image actor) {
		int ix;
		for (ix = 0; ix < board.length; ix++) {
			if (board[ix].equals(actor)) {
				break;
			}
		}
		if (ix >= board.length) {
			return -1;
		}
		return ix;
	}
}