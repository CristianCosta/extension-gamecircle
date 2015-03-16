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