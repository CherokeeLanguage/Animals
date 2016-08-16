package com.cherokeelessons.vocab.animals.one.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cherokeelessons.animals.CherokeeAnimals;

public class DesktopLauncher {
	public static void main (String[] arg) {
		float width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
		if (width<1) {
			width = CherokeeAnimals.size.size().width;
		}
		float height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
		if (height<1) {
			height = CherokeeAnimals.size.size().height;
		}
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.allowSoftwareMode=true;
		config.audioDeviceBufferSize=4*1024;
		config.audioDeviceBufferCount=4;
		config.audioDeviceSimultaneousSources=4;
		config.forceExit=true;
		config.height=(int)(height*.9f);
		config.resizable=true;
		config.title="Cherokee Animals";
		config.width=(int)(width*.9f);
		config.addIcon("icon/icon128.png", FileType.Internal);
		config.addIcon("icon/icon32.png", FileType.Internal);
		config.addIcon("icon/icon16.png", FileType.Internal);
		new LwjglApplication(new CherokeeAnimals(), config);
	}
}
