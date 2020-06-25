package com.cherokeelessons.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

class MenuLabel extends Label {
	
	private final String instructions;
	Runnable menu_action_east = null;
	Runnable menu_action_west = null;

	public MenuLabel(final CharSequence text, final LabelStyle style, final String instructions) {
		super(text, style);
		this.instructions=instructions;
	}

	public void doRun(final PovDirection direction) {
		if (menu_action_east != null && direction.equals(PovDirection.east)) {
			Gdx.app.postRunnable(menu_action_east);
			return;
		}
		if (menu_action_west != null && direction.equals(PovDirection.west)) {
			Gdx.app.postRunnable(menu_action_west);
			return;
		}
	}

	public String getInstructions() {
		return instructions;
	}
}