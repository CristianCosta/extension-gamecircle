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
	public static var displayMessage(default,null):Void->String=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "displayMessage", "()Ljava/lang/String;");
	#else
		function():String{return "Nada.";}
	#end

	public static var isInitialized(default,null):Void->Bool=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "isInitialized", "()Z");
	#else
		function():Bool{return false;}
	#end

	public static var init(default,null):Void->Void=
	#if android
		openfl.utils.JNI.createStaticMethod("com/gcex/GameCircle", "init", "()V");
	#else
		function():Void{}
	#end
	
}