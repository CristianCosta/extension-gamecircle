package extension.gc;

import haxe.Int64;

class GameCircle {

	public static inline var ACHIEVEMENT_STATUS_LOCKED:Int = 0;
	public static inline var ACHIEVEMENT_STATUS_UNLOCKED:Int = 1;

	//////////////////////////////////////////////////////////////////////
	///////////// INIT
	//////////////////////////////////////////////////////////////////////	

	public static var javaInit(default,null):Void->Void=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "init", "()V");
	#else
		function():Void{}
	#end

	public static var javaPause(default,null):Void->Void=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "pause", "()V");
	#else
		function():Void{}
	#end

	public static function pause() {
		trace("Pausing");
		javaPause();
	}

	public static function init() {
		trace("Initializing");
		javaInit();
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
	
	public static var setSteps(default,null):String->Float->Bool=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "setSteps", "(Ljava/lang/String;F)Z");
	#else
		function(id:String,steps:Int):Bool{return false;}
	#end
	
	public static var increment(default,null):String->Float->Bool=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "increment", "(Ljava/lang/String;F)Z");
	#else
		function(id:String,step:Int):Bool{return false;}
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
	
	///////////// ACHIEVEMENTS CURRENT STEPS
	// Los steps son de tipo Float (En GPG son Int)
	
	public static var onGetPlayerCurrentSteps:String->Float->Void=null;

	public static function getCurrentAchievementSteps(id:String):Bool {
		return javaGetCurrentAchievementSteps(id, getInstance());
	}

	private static var javaGetCurrentAchievementSteps(default,null):String->GameCircle->Bool=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "getCurrentAchievementSteps", "(Ljava/lang/String;Lorg/haxe/lime/HaxeObject;)Z");
	#else
		function(id:String, callback:GameCircle):Bool{return false;}
	#end

	public function onGetAchievementSteps(idAchievement:String, steps:Float) {
		if (onGetPlayerCurrentSteps != null) onGetPlayerCurrentSteps(idAchievement, steps);
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

