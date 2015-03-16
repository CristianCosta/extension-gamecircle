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

}
