package com.gcex;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;

import org.haxe.extension.Extension;
import org.haxe.lime.HaxeObject;

import com.amazon.ags.api.player.RequestPlayerResponse;
import com.amazon.ags.api.AGResponseCallback;
import com.amazon.ags.api.AmazonGamesClient;
import com.amazon.ags.api.AmazonGamesFeature;
import com.amazon.ags.api.AmazonGamesStatus;
import com.amazon.ags.api.AmazonGamesCallback;
import com.amazon.ags.api.overlay.PopUpLocation;

import java.util.EnumSet;

public class GameCircle extends Extension {
	
	private static final String TAG = "EXTENSION-GAMECIRCLE";
	private static AmazonGamesStatus gamesStatus = AmazonGamesStatus.INITIALIZING;
	private AmazonGamesClient agsClient = null;

	private static String estado = "Vacio.";
	
	public static String displayMessage () {
		//return "GOLA NUNDO.";
		return estado;
	}
	
	public static void init() {
		//try {
			mainActivity.runOnUiThread(new Runnable() {
				public void run() {
					AmazonGamesClient.initialize(mainActivity, new AmazonGamesCallback() {
						@Override
						public void onServiceReady(AmazonGamesClient amazonGamesClient) {
							//Esta todo bien.
							estado = "Cul";
						}
						@Override
						public void onServiceNotReady(AmazonGamesStatus reason) {
							//Esta todo mal.
							switch (reason) { 
							case CANNOT_AUTHORIZE: 
								estado = "onCreate: CANNOT_AUTHORIZE";
								break;
							case CANNOT_BIND: 
								estado = "onCreate: CANNOT_BIND";
								break;
							case CANNOT_INITIALIZE:
								estado = "onCreate: CANNOT_INITIALIZE";
								break;
							case INITIALIZING:
								estado = "onCreate: INITIALIZING";
								break;							
							case INVALID_SESSION:
								estado = "onCreate: INVALID_SESSION";
								break;						
							case NOT_AUTHENTICATED: 
								estado = "onCreate: NOT_AUTHENTICATED";
								break;
							case NOT_AUTHORIZED: 
								estado = "onCreate: NOT_AUTHORIZED";
								break;
							case SERVICE_CONNECTED:
								estado = "onCreate: SERVICE_CONNECTED";
								break;							
							case SERVICE_DISCONNECTED:
								estado = "onCreate: SERVICE_DISCONNECTED";
								break;							
							case SERVICE_NOT_OPTED_IN:
								estado = "onCreate: SERVICE_NOT_OPTED_IN";
								break;							
							} 
						}
					}, EnumSet.of(AmazonGamesFeature.Achievements, AmazonGamesFeature.Leaderboards));
				}
			});
		//} catch (Exception e) {}
	}

	public static boolean isInitialized() {
		try {
			return AmazonGamesClient.isInitialized();
		} catch (Exception e) {}
		return false;
	}

}