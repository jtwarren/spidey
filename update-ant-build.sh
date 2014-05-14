#!/bin/sh

target="android-19"
projectname=`sed -n 's,.*name="app_name">\(.*\)<.*,\1,p' res/values/strings.xml`

# make sure your Android SDK tools path is set in SDK_BASE
android update lib-project --path external/google-play-services --target $target
android update lib-project --path external/showcaseview/library --target $target
android update lib-project --path external/cardsui/CardsUILib --target $target

android update project --path . --name $projectname --target $target --subprojects

cp libs/android-support-v4.jar external/showcaseview/library/libs/android-support-v4.jar
rm external/showcaseview/library/libs/robolectric-2.2-20130909.210745-40-jar-with-dependencies.jar
