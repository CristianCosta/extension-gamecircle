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
import com.amazon.ags.api.whispersync.GameDataMap;
import com.amazon.ags.api.whispersync.WhispersyncEventListener;
import com.amazon.ags.api.whispersync.model.*;
import com.amazon.ags.constants.LeaderboardFilter;

import java.util.EnumSet;

public class GameCircle extends Extension {
	
	private static final String TAG = "EXTENSION-GAMECIRCLE";
	private static AmazonGamesStatus gamesStatus = AmazonGamesStatus.INITIALIZING;
	private static AmazonGamesClient agsClient = null;
	private static GameDataMap gameDataMap = null;
	private static EnumSet<AmazonGamesFeature> gameFeatures = null;
	private static boolean enableWhispersync = false;
	
	//////////////////////////////////////////////////////////////////////
	///////////// INIT
	//////////////////////////////////////////////////////////////////////

	private static void pause() {
		if (agsClient != null) agsClient.release();
	}

	private static void resume() {
		if(gameFeatures==null) return;
		
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				AmazonGamesClient.initialize(mainActivity, new AmazonGamesCallback() {
					@Override
					public void onServiceReady(AmazonGamesClient amazonGamesClient) {
						agsClient = amazonGamesClient;
						Log.i(TAG, "GameCircle: initialize Successful");
						if(enableWhispersync) {
							gameDataMap = AmazonGamesClient.getWhispersyncClient().getGameData();
						}
					}
					@Override
					public void onServiceNotReady(AmazonGamesStatus reason) {
						switch (reason) { 
						case CANNOT_AUTHORIZE: 
							Log.e(TAG, "GameCircle: CANNOT_AUTHORIZE");
							break;
						case CANNOT_BIND: 
							Log.e(TAG, "GameCircle: CANNOT_BIND");
							break;
						case CANNOT_INITIALIZE:
							Log.e(TAG, "GameCircle: CANNOT_INITIALIZE");
							break;
						case INITIALIZING:
							Log.e(TAG, "GameCircle: INITIALIZING");
							break;
						case INVALID_SESSION:
							Log.e(TAG, "GameCircle: INVALID_SESSION");
							break;
						case NOT_AUTHENTICATED: 
							Log.e(TAG, "GameCircle: NOT_AUTHENTICATED");
							break;
						case NOT_AUTHORIZED: 
							Log.e(TAG, "GameCircle: NOT_AUTHORIZED");
							break;
						case SERVICE_CONNECTED:
							Log.e(TAG, "GameCircle: SERVICE_CONNECTED");
							break;
						case SERVICE_DISCONNECTED:
							Log.e(TAG, "GameCircle: SERVICE_DISCONNECTED");
							break;
						case SERVICE_NOT_OPTED_IN:
							Log.e(TAG, "GameCircle: SERVICE_NOT_OPTED_IN");
							break;
						} 
					}
				}, gameFeatures);
			}
		});
	} 

	public static void init(boolean enableWhispersync) {
		Log.e(TAG, "GameCircle: INIT");
		GameCircle.enableWhispersync = enableWhispersync;
		try{
			if (enableWhispersync) {
				gameFeatures = EnumSet.of(AmazonGamesFeature.Achievements, AmazonGamesFeature.Leaderboards, AmazonGamesFeature.Whispersync);
			} else {
				gameFeatures = EnumSet.of(AmazonGamesFeature.Achievements, AmazonGamesFeature.Leaderboards);
			}
			resume();
		}catch(Exception e){
			Log.i(TAG, "GameCircle: init Exception");
			Log.i(TAG, e.toString());
			return;
		}
		Log.e(TAG, "GameCircle: INIT FINISH");
	}

	//////////////////////////////////////////////////////////////////////
	///////////// COULD STORAGE - WHISPERSYNC
	//////////////////////////////////////////////////////////////////////

	public static boolean cloudSet(String key, String value){
		try {
			gameDataMap.getDeveloperString(key).setValue(value);
		} catch (Exception e) {
			Log.i(TAG, "GameCircle: cloudSet Exception");
			Log.i(TAG, e.toString());
			return false;
		}
		return true;
	}

	public static boolean cloudGet(final String key, final HaxeObject callbackObject) {
		try {
			if(gameDataMap==null){
				Log.i(TAG, "GameCircle: handlePotentialGameDataConflicts - NOT INITIALIZED YET. DOING NOTHING!");
				return false;
			}
			SyncableDeveloperString developerString = gameDataMap.getDeveloperString(key);

			if (developerString==null) {
				callbackObject.call2("cloudGetCallback", key, null);			
			} else if (developerString.inConflict()) {
				String server = developerString.getCloudValue();
				String local = developerString.getValue();
				callbackObject.call3("cloudGetConflictCallback", key, local, server);
			} else {
				callbackObject.call2("cloudGetCallback", key, developerString.getValue());
			}

		} catch (Exception e) {
			Log.i(TAG, "GameCircle: cloudGet Exception");
			Log.i(TAG, e.toString());
			return false;
		}
		return true;
	}

	public static boolean markConflictAsResolved(String key) {
		try {
			gameDataMap.getDeveloperString(key).markAsResolved();
		} catch (Exception e) {
			Log.i(TAG, "GameCircle: markConflictAsResolved Exception");
			Log.i(TAG, e.toString());
			return false;
		}
		return true;
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
			resume();
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
					}
				}
			});
		} catch (Exception e) {
			// Try connecting again
			Log.i(TAG, "GameCircle: getAchievementStatus Exception");
			Log.i(TAG, e.toString());
			resume();
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
			resume();
			return false;
		}
		return true;
	}

	//////////////////////////////////////////////////////////////////////
	///////////// SCOREBOARDS
	//////////////////////////////////////////////////////////////////////

	public static boolean displayScoreboard(String idScoreboard) {
		try {
			AmazonGamesClient.getInstance().getLeaderboardsClient().showLeaderboardOverlay(idScoreboard);
		} catch (Exception e) {
			Log.i(TAG, "GameCircle: displayScoreboard Exception");
			Log.i(TAG, e.toString());
			resume();
			return false;
		}
		return true;
	}

	public static boolean displayAllScoreboards() {
		try {
			AmazonGamesClient.getInstance().getLeaderboardsClient().showLeaderboardsOverlay();
		} catch (Exception e) {
			// Try connecting again
			Log.i(TAG, "GameCircle: displayScoreboard Exception");
			Log.i(TAG, e.toString());
			resume();
			return false;
		}
		return true;
	}
	
	public static boolean setScore(String leaderboardId, int high_score, int low_score){
		try {
			long score = (((long)high_score << 32) | ((long)low_score & 0xFFFFFFFF));
			AmazonGamesClient.getInstance().getLeaderboardsClient().submitScore(leaderboardId, score);
        } catch (Exception e) {
			Log.i(TAG, "GameCircle: setScore Exception");
			Log.i(TAG, e.toString());
			return false;
		}
		Log.i(TAG, "GameCircle: setScore complete");
    	return true;
	}

	public static boolean getPlayerScore(final String idScoreboard, final HaxeObject callbackObject) {
		try {
			AmazonGamesClient.getInstance().getLeaderboardsClient().getLocalPlayerScore(idScoreboard, LeaderboardFilter.GLOBAL_ALL_TIME).setCallback(new AGResponseCallback<GetPlayerScoreResponse>() {
				@Override
				public void onComplete(GetPlayerScoreResponse playerScore) {
					if (playerScore != null) {
						long score = playerScore.getScoreValue();
						int high_score = (int) (score >>> 32);
						int low_score = (int) (score & 0xFFFFFFFF);
						callbackObject.call3("onGetScoreboard", idScoreboard, high_score, low_score);
					}
				}
			});
		} catch (Exception e) {
			// Try connecting again
			Log.i(TAG, "GameCircle: getPlayerScore Exception");
			Log.i(TAG, e.toString());
			resume();
			return false;
		}
		return true;
	}	

	//////////////////////////////////////////////////////////////////////
	///////////// DEBUG
	//////////////////////////////////////////////////////////////////////

	public static boolean isInitialized() {
		try {
			return AmazonGamesClient.isInitialized();
		} catch (Exception e) {}
		return false;
	}

	//////////////////////////////////////////////////////////////////////
	///////////// EVENT SUSCRIPTIONS
	//////////////////////////////////////////////////////////////////////
	
	/**
	 * Called as part of the activity lifecycle when an activity is going into
	 * the background, but has not (yet) been killed.
	 */
	@Override public void onPause () {
		try{
			Log.i(TAG, "GameCircle: onPause 1");
			GameCircle.pause();
			Log.i(TAG, "GameCircle: onPause 2");
		}catch(Exception e){
			Log.i(TAG, "GameCircle: onPause Exception");
			Log.i(TAG, e.toString());
		}
	}
	
	
	/**
	 * Called after {@link #onRestart}, or {@link #onPause}, for your activity 
	 * to start interacting with the user.
	 */
	@Override public void onResume () {
		try{
			Log.i(TAG, "GameCircle: onResume 1");
			GameCircle.resume();
			Log.i(TAG, "GameCircle: onResume 2");
		}catch(Exception e){
			Log.i(TAG, "GameCircle: onResume Exception");
			Log.i(TAG, e.toString());
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////


}