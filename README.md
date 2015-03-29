#Extension-GameCircle

OpenFL extension for "Game Circle" on Android and Kindle devices.

###Main features

* Achievements (Complete, send progress, reveal/unhide, display achievements screen).
* Scoreboards (Submit scores, display scoreboard).
* Game Circle sign - in (Log - in whit Amazon).
* Cloud storage support - Whispersync (for storing progress / scores).
* Callback events for onCloudComplete (read from cloud) and onCloudConflict (version conflict on cloud).
* Manual conflict resolution. You can change that by implementing the onCloudConflict method.

###Simple use example

```haxe
// This example show a simple use case.

import extension.gc.GameCircle;

class MainClass {

	function init() {
		// First of all, call init on the main method.
		// The only parameter is to enable Whispersync.
		// Note, the init method automatically make the Login With Amazon. 
		GooglePlayGames.init(true);
	}

	function displayScoreboard() {
		// To open one specific scoreboard, the scoreboard id is in your "GameCircle Developer Console".
		GameCircle.displayScoreboard("Your-scoreboard-id"); 
		
		// To show all scoreboards.
		//GameCircle.displayAllScoreboards(); 
	}
	
	function displayAchievements() {
		// To display the achievements.
		GameCircle.displayAchievements();
	}

	function submitScoresAndAchievements() {
		// To set 234 points on scoreboard (Int data type).
		GameCircle.setScore("Your-scoreboard-id", 234); 
		
		// To set 234 points on scoreboard (Long data type).
		GameCircle.setScore64("Your-scoreboard-id", 234); 
		
		// To set one achievement to progress to 30.
		GameCircle.setSteps("Your-achievement-id",30); 
		
		// To unlock / complete one achievement.
		GameCircle.unlock("Your-achievement-id");

		// Please note that all this functions returns false if the onServicesNotReady in initialize method is invoked.
		// However, return true if the client has successfully initialized (onServiceReady in initialize method).
	}
	
}
```

###Cloud Storage use example

```haxe
// This example show a simple use case.

import extension.gc.GameCircle;

class SomeClass {

	function new() {
		GameCircle.onCloudComplete = onCloudComplete;
		GameCircle.onCloudConflict = onCloudConflict;
	}
	
	function saveToCloud(id:String, data:String) {
		GameCircle.cloudSet(id, data);
	}
	
	function loadFromCloud(id:String) {
		GameCircle.cloudGet(id);
	}
	
	function onCloudComplete(id:String, data:String) {
		trace("Data on record: "+id+" is: "+data);
	}

	function onCloudConflict(id:String, localValue:String, serverValue:String) {
		trace("Conflict on record: "+id+". Local: "+localValue+" - Server: "+serverValue);
	}
	
}
```

###Get player score example

```haxe
// This example show a simple use case whit the method getPlayerScore.
// Explanations whit the methods getCurrentAchievementSteps and
// getAchievementStatus.

import extension.gc.GameCircle;

class MainClass {

	function new() {
		// First of all, call init on the main method.
		// The only parameter is to enable Whispersync.
		// Note, the init method automatically make the Login With Amazon.
		GameCircle.init(true);
		
		// Set up the player score result event callback first, always before init().
		// Work with Int data type.
		GameCircle.onGetPlayerScore = playerScoreCallback;
		
		// Work with Long data type.
		GameCircle.onGetPlayerScore64 = playerScore64Callback; 
	}
	
	function getPlayerScoreFromScoreboard() {
		// Call getPlayerScore passing the idScoreboard.
		// This function returns false if the onServicesNotReady in initialize method is invoked.
		// Same function for both data types (Int/Long).
		GameCircle.getPlayerScore("Your-scoreboard-id"); 
	}
	
	function playerScoreCallback(idScoreboard:String, score:Int):Void {
		// This function must be adapted to your game logic.
		Lib.trace("ID Scoreboard: "+ idScoreboard +". Score: "+ score);
	}
	
	function playerScoreCallback64(idScoreboard:String, score:Int64):Void {
		// This function must be adapted to your game logic.
		Lib.trace("ID Scoreboard: "+ idScoreboard +". Score: "+ score);
	}
	
	// Note that, the functions:
	//			* GameCircle.getCurrentAchievementSteps("your-achievement-id")
	//			* GameCircle.getAchievementStatus("your-achievement-id")
	// Works with the same logic. Both must be set up the result event callback first.
	//			* GameCircle.onGetPlayerAchievementStatus = callbackStatus;
	//			* GameCircle.onGetPlayerCurrentSteps = callbackSteps;
	// Both functions returns false if the user is not logged into the game.
	//			* function callbackStatus(idAchievement:String, status:String): Void
	//			* function callbackSteps(idAchievement:String, steps:Int): Void
	
}
```

###How to Install

Once this is done, you just need to add this to your project.xml

```xml
<haxelib name="extension-gamecircle" />
<assets path="assets/api_key.txt" rename="api_key.txt" if="android"/> <!-- Replace this with your GameCircle ApyKey generated in "GameCircle Developer Console"! -->
```


###Disclaimer

Amazon is a registered trademark of Amazon Technologies, Inc.
http://unibrander.com/united-states/12554US/amazon.html

###License

The MIT License (MIT) - [LICENSE.md](LICENSE.md)

Copyright &copy; 2015 Cristian Costa
