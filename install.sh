#!/bin/bash
dir=`dirname "$0"`
cd "$dir"
haxelib remove extension-gamecircle
haxelib local extension-gamecircle.zip
