#!/bin/bash
dir=`dirname "$0"`
cd "$dir"
rm -f extension-gamecircle.zip
zip -0r extension-gamecircle.zip extension haxelib.json include.xml dependencies 
