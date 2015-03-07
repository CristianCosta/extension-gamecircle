package extension.gc;

class GameCircle {

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