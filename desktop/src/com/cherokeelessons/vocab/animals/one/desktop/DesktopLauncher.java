package com.cherokeelessons.vocab.animals.one.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.math.Rectangle;
import com.cherokeelessons.animals.CherokeeAnimals;
import com.cherokeelessons.common.DisplaySize;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Rectangle desktopSize = DisplaySize._720p.size();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.allowSoftwareMode=true;
		config.audioDeviceBufferSize=4*1024;
		config.audioDeviceBufferCount=4;
		config.audioDeviceSimultaneousSources=4;
		config.forceExit=true;
		config.height=(int) desktopSize.height;
		config.resizable=true;
		config.title="Cherokee Animals";
		config.width=(int) desktopSize.width;
		config.addIcon("icon/icon128.png", FileType.Internal);
		config.addIcon("icon/icon32.png", FileType.Internal);
		config.addIcon("icon/icon16.png", FileType.Internal);
		new LwjglApplication(new CherokeeAnimals(), config);
	}
}
