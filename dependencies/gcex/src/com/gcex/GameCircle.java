package com.gcex;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;

import org.haxe.extension.Extension;
import org.haxe.lime.HaxeObject;

import com.amazon.ags.api.AGResponseCallback;
import com.amazon.ags.api.AmazonGamesClient;
import com.amazon.ags.api.AmazonGamesFeature;
import com.amazon.ags.api.AmazonGamesStatus;
import com.amazon.ags.api.AmazonGamesCallback;
import com.amazon.ags.api.RequestResponse;
import com.amazon.ags.api.achievements.GetAchievementsResponse;
import com.amazon.ags.api.achievements.Achievement;
import com.amazon.ags.api.leaderboards.GetPlayerScoreResponse;
import com.amazon.ags.api.overlay.PopUpLocation;
import com.amazon.ags.constants.LeaderboardFilter;

import java.util.EnumSet;

public class GameCircle extends Extension {
	
	private static final String TAG = "EXTENSION-GAMECIRCLE";
	private static AmazonGamesStatus gamesStatus = AmazonGamesStatus.INITIALIZING;
	private static AmazonGamesClient agsClient = null;

	private static String estado = "Vacio.";
	private static int numerito = 0;
	private static int numerote = 1;
	
	//////////////////////////////////////////////////////////////////////
	///////////// INIT
	//////////////////////////////////////////////////////////////////////

	public static void pause() {
		if (agsClient != null) {
			agsClient.release();
		}
		numerote = numerote * 2;
	}

	public static void init() {
		numerito++;
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				AmazonGamesClient.initialize(mainActivity, new AmazonGamesCallback() {
					@Override
					public void onServiceReady(AmazonGamesClient amazonGamesClient) {
						//Esta todo bien.
						agsClient = amazonGamesClient;
						estado = "Successful";
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
	}

	//////////////////////////////////////////////////////////////////////
	///////////// ACHIEVEMENTS
	//////////////////////////////////////////////////////////////////////

	public static boolean displayAchievements() {
		try {
			AmazonGamesClient.getInstance().getAchievementsClient().showAchievementsOverlay();
		} catch (Exception e) {
			// Try connecting again
			Log.i(TAG, "GameCircle: displayAchievements Exception");
			Log.i(TAG, e.toString());
			init();
			return false;
		}
		return true;
	}

	public static boolean unlock(String idAchievement){
		try {
			AmazonGamesClient.getInstance().getAchievementsClient().updateProgress(idAchievement, 100.0f);
		} catch (Exception e) {
			Log.i(TAG, "GameCircle: unlock Exception");
			Log.i(TAG, e.toString());
			return false;
		}
		return true;
	}
	
	public static boolean setSteps(String idAchievement, float steps){
		try {
			AmazonGamesClient.getInstance().getAchievementsClient().updateProgress(idAchievement, steps);
		} catch (Exception e) {
			Log.i(TAG, "GameCircle: unlock Exception");
			Log.i(TAG, e.toString());
			return false;
		}
		return true;
	}

	public static boolean increment(final String idAchievement, final float steps){
		try {
			AmazonGamesClient.getInstance().getAchievementsClient().getAchievements().setCallback(new AGResponseCallback<GetAchievementsResponse>() {
				@Override
				public void onComplete(GetAchievementsResponse achievementsResponse) {
					for (Achievement ach: achievementsResponse.getAchievementsList()) {
						if (ach.getId().equals(idAchievement)) {
							float newProgress = steps + ach.getProgress();
							AmazonGamesClient.getInstance().getAchievementsClient().updateProgress(idAchievement, newProgress);
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
				}
			});
		} catch (Exception e) {
			Log.i(TAG, "GameCircle: increment Exception");
			Log.i(TAG, e.toString());
			return false;
		}
		return true;
	}

	public static boolean getAchievementStatus(final String idAchievement, final HaxeObject callbackObject) {
		try {
			AmazonGamesClient.getInstance().getAchievementsClient().getAchievements().setCallback(new AGResponseCallback<GetAchievementsResponse>() {
				@Override
				public void onComplete(GetAchievementsResponse achievementsResponse) {
					for (Achievement ach: achievementsResponse.getAchievementsList()) {
						if (ach.getId().equals(idAchievement)) {
							if (ach.isUnlocked()) callbackObject.call2("onGetAchievementStatus", idAchievement, 1);
							else callbackObject.call2("onGetAchievementStatus", idAchievement, 0);
						}
					}, EnumSet.of(AmazonGamesFeature.Achievements, AmazonGamesFeature.Leaderboards));
					}
				}
			});
		//} catch (Exception e) {}
		} catch (Exception e) {
			// Try connecting again
			Log.i(TAG, "GameCircle: getAchievementStatus Exception");
			Log.i(TAG, e.toString());
			init();
			return false;
		}
		return true;
	}
	
	public static boolean getCurrentAchievementSteps(final String idAchievement, final HaxeObject callbackObject) {
		try {
			AmazonGamesClient.getInstance().getAchievementsClient().getAchievements().setCallback(new AGResponseCallback<GetAchievementsResponse>() {
				@Override
				public void onComplete(GetAchievementsResponse achievementsResponse) {
					for (Achievement ach: achievementsResponse.getAchievementsList()) {
						if (ach.getId().equals(idAchievement)) {
							callbackObject.call2("onGetAchievementSteps", idAchievement, ach.getProgress());
						}
					}
				}
			});
		} catch (Exception e) {
			// Try connecting again
			Log.i(TAG, "GameCircle: getCurrentAchievementSteps Exception");
			Log.i(TAG, e.toString());
			init();
			return false;
		}
		return true;
	}
	}

	public static boolean isInitialized() {
		try {
			return AmazonGamesClient.isInitialized();
		} catch (Exception e) {}
		return false;
	}

}