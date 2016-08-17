package com.cherokeelessons.vocab.animals.one;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.cherokeelessons.animals.CherokeeAnimals;

public class IOSLauncher extends IOSApplication.Delegate {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.allowIpod=true;
        config.orientationLandscape=true;
        config.orientationPortrait=false;
        
        return new IOSApplication(new CherokeeAnimals(), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        System.out.println("STARTUP");
        UIApplication.main(argv, null, IOSLauncher.class);
        System.out.println("SHUTDOWN");
        pool.close();
    }
}