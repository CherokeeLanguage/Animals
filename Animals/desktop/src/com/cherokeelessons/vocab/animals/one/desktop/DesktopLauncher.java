package com.cherokeelessons.vocab.animals.one.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cherokeelessons.vocab.animals.one.CherokeeAnimals;
import com.cherokeelessons.vocab.animals.one.DisplaySize;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.allowSoftwareMode=true;
		config.audioDeviceSimultaneousSources=4;
		config.forceExit=true;
		config.height=DisplaySize._720p.height();
		config.resizable=true;
		config.title="Cherokee Animals";
		config.width=DisplaySize._720p.width();
		config.addIcon("icon/icon128.png", FileType.Internal);
		config.addIcon("icon/icon32.png", FileType.Internal);
		config.addIcon("icon/icon16.png", FileType.Internal);
		new LwjglApplication(new CherokeeAnimals(), config);
	}
}
