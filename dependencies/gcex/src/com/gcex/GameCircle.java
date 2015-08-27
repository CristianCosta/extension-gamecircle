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
	private static EnumSet<AmazonGamesFeature> gameFeatures = null;
	private static boolean enableWhispersync = false;
	
	//////////////////////////////////////////////////////////////////////
	///////////// INIT
	//////////////////////////////////////////////////////////////////////

	private static void pause() {
		if (agsClient != null) {
			agsClient.release();
			agsClient = null;
		}
	}

	private static void resume() {
		if(gameFeatures == null) return;
		agsClient = null;

		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				try{
					AmazonGamesClient.initialize(mainActivity, new AmazonGamesCallback() {
						@Override
						public void onServiceReady(AmazonGamesClient amazonGamesClient) {
							agsClient = amazonGamesClient;
							Log.i(TAG, "GameCircle: initialize Successful");
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
				}catch(Exception e){
					Log.i(TAG, "GameCircle: resume -> run Exception");
					Log.i(TAG, e.toString());				
				}
			}
		});
	} 

	public static void init(boolean enableWhispersync) {
		try{
			GameCircle.enableWhispersync = enableWhispersync;
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
	}

	//////////////////////////////////////////////////////////////////////
	///////////// COULD STORAGE - WHISPERSYNC
	//////////////////////////////////////////////////////////////////////

	public static boolean cloudSet(String key, String value){
		if(agsClient == null){
			Log.i(TAG, "GameCircle: unlock - agsClient is null... wait a bit more please!");
			return false;
		}
		if(!enableWhispersync){
			Log.i(TAG, "GameCircle: cloudSet - Whispersync is not enabled, ignoring!");
			return false;
		}
		try {
			SyncableDeveloperString developerString = AmazonGamesClient.getWhispersyncClient().getGameData().getDeveloperString(key);
			if (developerString==null) return false;
			developerString.setValue(value);
			return true;
		} catch (Exception e) {
			Log.i(TAG, "GameCircle: cloudSet Exception");
			Log.i(TAG, e.toString());
			return false;
		}
	}

	public static boolean cloudGet(final String key, final HaxeObject callbackObject) {
		if(agsClient == null){
			Log.i(TAG, "GameCircle: unlock - agsClient is null... wait a bit more please!");
			return false;
		}
		if(!enableWhispersync){
			Log.i(TAG, "GameCircle: cloudGet - Whispersync is not enabled, ignoring!");
			return false;
		}
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				try {
					SyncableDeveloperString developerString = AmazonGamesClient.getWhispersyncClient().getGameData().getDeveloperString(key);
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
				}
			}
		});
		return true;
	}

	public static boolean markConflictAsResolved(String key) {
		if(agsClient == null){
			Log.i(TAG, "GameCircle: unlock - agsClient is null... wait a bit more please!");
			return false;
		}
		if(!enableWhispersync){
			Log.i(TAG, "GameCircle: markConflictAsResolved - Whispersync is not enabled, ignoring!");
			return false;
		}
		try {
			AmazonGamesClient.getWhispersyncClient().getGameData().getDeveloperString(key).markAsResolved();
			return true;
		} catch (Exception e) {
			Log.i(TAG, "GameCircle: markConflictAsResolved Exception");
			Log.i(TAG, e.toString());
			return false;
		}
	}

	//////////////////////////////////////////////////////////////////////
	///////////// ACHIEVEMENTS
	//////////////////////////////////////////////////////////////////////

	public static boolean displayAchievements() {
		if(agsClient == null){
			Log.i(TAG, "GameCircle: displayAchievements - agsClient is null... wait a bit more please!");
			return false;
		}
		try {
			agsClient.getAchievementsClient().showAchievementsOverlay();
			return true;
		} catch (Exception e) {
			Log.i(TAG, "GameCircle: displayAchievements Exception");
			Log.i(TAG, e.toString());
			return false;
		}
	}

	public static boolean unlock(String idAchievement){
		if(agsClient == null){
			Log.i(TAG, "GameCircle: unlock - agsClient is null... wait a bit more please!");
			return false;
		}
		try {
			agsClient.getAchievementsClient().updateProgress(idAchievement, 100.0f);
			return true;
		} catch (Exception e) {
			Log.i(TAG, "GameCircle: unlock Exception");
			Log.i(TAG, e.toString());
			return false;
		}
	}
	
	public static boolean setSteps(String idAchievement, float steps){
		if(agsClient == null){
			Log.i(TAG, "GameCircle: setSteps - agsClient is null... wait a bit more please!");
			return false;
		}
		try {
			agsClient.getAchievementsClient().updateProgress(idAchievement, steps);
			return true;
		} catch (Exception e) {
			Log.i(TAG, "GameCircle: unlock Exception");
			Log.i(TAG, e.toString());
			return false;
		}
	}

	public static boolean getAchievementStatus(final String idAchievement, final HaxeObject callbackObject) {
		if(agsClient == null){
			Log.i(TAG, "GameCircle: getAchievementStatus - agsClient is null... wait a bit more please!");
			return false;
		}
		try {
			agsClient.getAchievementsClient().getAchievements().setCallback(new AGResponseCallback<GetAchievementsResponse>() {
				@Override
				public void onComplete(GetAchievementsResponse achievementsResponse) {
					try{
						for (Achievement ach: achievementsResponse.getAchievementsList()) {
							if (ach.getId().equals(idAchievement)) {
								if (ach.isUnlocked()) callbackObject.call2("onGetAchievementStatus", idAchievement, 1);
								else callbackObject.call2("onGetAchievementStatus", idAchievement, 0);
							}
						}
					}catch(Exception e){			
						Log.i(TAG, "GameCircle: getAchievementStatus -> onComplete Exception");
						Log.i(TAG, e.toString());
					}
				}
			});
			return true;
		} catch (Exception e) {
			Log.i(TAG, "GameCircle: getAchievementStatus Exception");
			Log.i(TAG, e.toString());
			return false;
		}
	}
	
	public static boolean getCurrentAchievementSteps(final String idAchievement, final HaxeObject callbackObject) {
		if(agsClient == null){
			Log.i(TAG, "GameCircle: getCurrentAchievementSteps - agsClient is null... wait a bit more please!");
			return false;
		}
		try {
			agsClient.getAchievementsClient().getAchievements().setCallback(new AGResponseCallback<GetAchievementsResponse>() {
				@Override
				public void onComplete(GetAchievementsResponse achievementsResponse) {
					try{
						for (Achievement ach: achievementsResponse.getAchievementsList()) {
							if (ach.getId().equals(idAchievement)) {
								callbackObject.call2("onGetAchievementSteps", idAchievement, ach.getProgress());
							}
						}
					}catch(Exception e){			
						Log.i(TAG, "GameCircle: getCurrentAchievementSteps -> onComplete Exception");
						Log.i(TAG, e.toString());
					}
				}
			});
			return true;
		} catch (Exception e) {
			Log.i(TAG, "GameCircle: getCurrentAchievementSteps Exception");
			Log.i(TAG, e.toString());
			return false;
		}
	}

	//////////////////////////////////////////////////////////////////////
	///////////// SCOREBOARDS
	//////////////////////////////////////////////////////////////////////

	public static boolean displayScoreboard(String idScoreboard) {
		if(agsClient == null){
			Log.i(TAG, "GameCircle: displayScoreboard - agsClient is null... wait a bit more please!");
			return false;
		}
		try {
			agsClient.getLeaderboardsClient().showLeaderboardOverlay(idScoreboard);
			return true;
		} catch (Exception e) {
			Log.i(TAG, "GameCircle: displayScoreboard Exception");
			Log.i(TAG, e.toString());
			return false;
		}
	}

	public static boolean displayAllScoreboards() {
		if(agsClient == null){
			Log.i(TAG, "GameCircle: displayAllScoreboards - agsClient is null... wait a bit more please!");
			return false;
		}
		try {
			agsClient.getLeaderboardsClient().showLeaderboardsOverlay();
			return true;
		} catch (Exception e) {
			Log.i(TAG, "GameCircle: displayAllScoreboards Exception");
			Log.i(TAG, e.toString());
			return false;
		}
	}
	
	public static boolean setScore(String leaderboardId, int high_score, int low_score){
		if(agsClient == null){
			Log.i(TAG, "GameCircle: setScore - agsClient is null... wait a bit more please!");
			return false;
		}
		try {
			long score = (((long)high_score << 32) | ((long)low_score & 0xFFFFFFFF));
			agsClient.getLeaderboardsClient().submitScore(leaderboardId, score);
		   	return true;
        } catch (Exception e) {
			Log.i(TAG, "GameCircle: setScore Exception");
			Log.i(TAG, e.toString());
			return false;
		}
	}

	public static boolean getPlayerScore(final String idScoreboard, final HaxeObject callbackObject) {
		if(agsClient == null){
			Log.i(TAG, "GameCircle: getPlayerScore - agsClient is null... wait a bit more please!");
			return false;
		}
		try {
			agsClient.getLeaderboardsClient().getLocalPlayerScore(idScoreboard, LeaderboardFilter.GLOBAL_ALL_TIME).setCallback(new AGResponseCallback<GetPlayerScoreResponse>() {
				@Override
				public void onComplete(GetPlayerScoreResponse playerScore) {
					try{
						if (playerScore != null) {
							long score = playerScore.getScoreValue();
							int high_score = (int) (score >>> 32);
							int low_score = (int) (score & 0xFFFFFFFF);
							callbackObject.call3("onGetScoreboard", idScoreboard, high_score, low_score);
						}
					} catch(Exception e) {
						Log.i(TAG, "GameCircle: getPlayerScore -> onComplete Exception");
						Log.i(TAG, e.toString());
					}
				}
			});
			return true;
		} catch (Exception e) {
			Log.i(TAG, "GameCircle: getPlayerScore Exception");
			Log.i(TAG, e.toString());
			return false;
		}
	}	

	//////////////////////////////////////////////////////////////////////
	///////////// DEBUG
	//////////////////////////////////////////////////////////////////////

	public static boolean isInitialized() {
		try {
			return AmazonGamesClient.isInitialized();
		} catch (Exception e) {
			return false;
		}
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
			GameCircle.pause();
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
			GameCircle.resume();
		}catch(Exception e){
			Log.i(TAG, "GameCircle: onResume Exception");
			Log.i(TAG, e.toString());
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////


}