@echo off
SET dir=%~dp0
cd %dir%
haxelib remove extension-gamecircle
haxelib local extension-gamecircle.zip
