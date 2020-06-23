package com.cherokeelessons.vocab.animals.one;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.cherokeelessons.animals.CherokeeAnimals;

import android.os.Bundle;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		CherokeeAnimals game = new CherokeeAnimals();
		game.setIsTelevisionDetector(new AndroidTvDetector(this.getApplicationContext()));
		initialize(game, config);
	}
}
