package com.cherokeelessons.vocab.animals.one;

import com.cherokeelessons.animals.TvDetector;

import android.annotation.TargetApi;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


public class AndroidTvDetector implements TvDetector {
	
	private final Context context;

	public AndroidTvDetector(Context context) {
		this.context = context;
	}
	
	private static final String TAG = "DeviceTypeRuntimeCheck";

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	@Override
	public boolean isTelevision() {
		/**
		 * Checks if the device is a TV.
		 */
			UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
			if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
			    Log.d(TAG, "Running on a TV Device");
			    return true;
			}
			Log.d(TAG, "Running on a non-TV Device");
			return false;
	}

}
