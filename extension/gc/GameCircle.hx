package extension.gc;

import haxe.Int64;
import flash.Lib;
import flash.events.Event;

class GameCircle {

	public static inline var ACHIEVEMENT_STATUS_LOCKED:Int = 0;
	public static inline var ACHIEVEMENT_STATUS_UNLOCKED:Int = 1;

	//////////////////////////////////////////////////////////////////////
	///////////// INIT
	//////////////////////////////////////////////////////////////////////	

	private static var javaInit(default,null):Bool->Void=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "init", "(Z)V");
	#else
		function(enableWhispersync:Bool):Void{}
	#end

	public static function init(enableWhispersync:Bool) {
		trace("Initializing");
		javaInit(enableWhispersync);
	}

	//////////////////////////////////////////////////////////////////////
	///////////// INSTANCE
	//////////////////////////////////////////////////////////////////////	

	private static var instance:GameCircle = null;

	private function new() {}

	private static function getInstance():GameCircle{
		if (instance == null) instance = new GameCircle();
		return instance;
	}

	//////////////////////////////////////////////////////////////////////
	///////////// COULD STORAGE - WHISPERSYNC
	//////////////////////////////////////////////////////////////////////

	public static var cloudSet(default,null):String->String->Bool=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "cloudSet", "(Ljava/lang/String;Ljava/lang/String;)Z");
	#else
		function(key:String,value:String):Bool{return false;}
	#end

	private static var javaCloudGet(default,null):String->GameCircle->Bool=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "cloudGet", "(Ljava/lang/String;Lorg/haxe/lime/HaxeObject;)Z");
	#else
		function(key:String, callback:GameCircle):Bool{return false;}
	#end

	public static function cloudGet(key:String):Bool{
		return javaCloudGet(key, getInstance());
	}

	public static var markConflictAsResolved(default,null):String->Bool=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "markConflictAsResolved", "(Ljava/lang/String;)Z");
	#else
		function(key:String):Bool{return false;}
	#end

	public static var onCloudGetComplete:String->String->Void=null;
	public static var onCloudGetConflict:String->String->String->Void=null;

	public function cloudGetCallback(key:String, value:String){
		if (onCloudGetComplete != null) onCloudGetComplete(key,value);
	}

	public function cloudGetConflictCallback(key:String, localValue:String, serverValue:String){
		trace ("Conflict versions on KEY: "+key+". Local: "+localValue+" - Server: "+serverValue);
		if (onCloudGetConflict != null) onCloudGetConflict(key,localValue,serverValue);
	}

	//////////////////////////////////////////////////////////////////////
	///////////// ACHIEVEMENTS
	//////////////////////////////////////////////////////////////////////

	public static var displayAchievements(default,null):Void->Bool=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "displayAchievements", "()Z");
	#else
		function():Bool{return false;}
	#end
	
	public static var unlock(default,null):String->Bool=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "unlock", "(Ljava/lang/String;)Z");
	#else
		function(id:String):Bool{return false;}
	#end
	
	public static var setProgress(default,null):String->Float->Bool=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "setProgress", "(Ljava/lang/String;F)Z");
	#else
		function(id:String,steps:Float):Bool{return false;}
	#end

	///////////// ACHIEVEMENT STATUS
	
	public static var onGetPlayerAchievementStatus:String->Int->Void=null;

	public static function getAchievementStatus(id:String):Bool {
		return javaGetAchievementStatus(id, getInstance());
	}

	private static var javaGetAchievementStatus(default,null):String->GameCircle->Bool=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "getAchievementStatus", "(Ljava/lang/String;Lorg/haxe/lime/HaxeObject;)Z");
	#else
		function(id:String, callback:GameCircle):Bool{return false;}
	#end

	public function onGetAchievementStatus(idAchievement:String, state:Int) {
		if (onGetPlayerAchievementStatus != null) onGetPlayerAchievementStatus(idAchievement, state);
	}
	
	///////////// ACHIEVEMENTS CURRENT PROGRESS
	
	public static var onGetPlayerCurrentProgress:String->Float->Void=null;

	public static function getAchievementProgress(id:String):Bool {
		return javaGetAchievementCurrentProgress(id, getInstance());
	}

	private static var javaGetAchievementCurrentProgress(default,null):String->GameCircle->Bool=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "getAchievementCurrentProgress", "(Ljava/lang/String;Lorg/haxe/lime/HaxeObject;)Z");
	#else
		function(id:String, callback:GameCircle):Bool{return false;}
	#end

	public function onGetAchievementProgress(idAchievement:String, progress:Float) {
		if (onGetPlayerCurrentProgress != null) onGetPlayerCurrentProgress(idAchievement, progress);
	}

	//////////////////////////////////////////////////////////////////////
	///////////// SCOREBOARDS
	//////////////////////////////////////////////////////////////////////

	public static var displayScoreboard(default,null):String->Bool=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "displayScoreboard", "(Ljava/lang/String;)Z");
	#else
		function(id:String):Bool{return false;}
	#end

	public static var displayAllScoreboards(default,null):Void->Bool=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "displayAllScoreboards", "()Z");
	#else
		function():Bool{return false;}
	#end

	private static var javaSetScore(default,null):String->Int->Int->Bool=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "setScore", "(Ljava/lang/String;II)Z");
	#else
		function(id:String,high_score:Int, low_score:Int):Bool{return false;}
	#end

	public static function setScore(id:String, score:Int):Bool {
		return javaSetScore(id, 0, score);
	}

	public static function setScore64(id:String, score:Int64):Bool {
		var low_score:Int = Int64.getLow(score);
		var high_score:Int = Int64.getHigh(score);
		return javaSetScore(id, high_score, low_score);
	}
	
	///////////// GET PLAYER SCORE
	
	public static var onGetPlayerScore:String->Int->Void=null;
	public static var onGetPlayerScore64:String->Int64->Void=null;

	public static function getPlayerScore(id:String):Bool {
		return javaGetPlayerScore(id, getInstance());
	}

	private static var javaGetPlayerScore(default,null):String->GameCircle->Bool=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "getPlayerScore", "(Ljava/lang/String;Lorg/haxe/lime/HaxeObject;)Z");
	#else
		function(id:String, callback:GameCircle):Bool{return false;}
	#end

	public function onGetScoreboard(idScoreboard:String, high_score:Int, low_score:Int) {
		if (onGetPlayerScore != null) onGetPlayerScore(idScoreboard, low_score);
		if (onGetPlayerScore64 != null) {
			var score:Int64 = Int64.make(high_score, low_score);
			onGetPlayerScore64(idScoreboard, score);
		}
	}

	//////////////////////////////////////////////////////////////////////
	///////////// DEBUG
	//////////////////////////////////////////////////////////////////////	

	public static var isInitialized(default,null):Void->Bool=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "isInitialized", "()Z");
	#else
		function():Bool{return false;}
	#end

}