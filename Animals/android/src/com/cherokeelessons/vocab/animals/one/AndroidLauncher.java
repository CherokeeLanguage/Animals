package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.cherokeelessons.animals.CherokeeAnimals;

import android.app.UiModeManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		CherokeeAnimals game = new CherokeeAnimals();
		game.isTv=isAndroidTV();
		initialize(game, config);
	}

	public boolean isAndroidTV() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			UiModeManager uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
			return (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION);
		}
		return false;
	}
}
