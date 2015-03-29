@echo off
SET dir=%~dp0
cd %dir%
if exist extension-gamecircle.zip del /F extension-gamecircle.zip
winrar a -afzip extension-gamecircle.zip extension haxelib.json include.xml dependencies
pause