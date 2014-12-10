
INC_TOOLS_DIR=../../tools

build:
	ant debug

build-with-native:
	./build_native.sh
	ant debug

install:
	ant install -r ./bin/RebornBird-debug.apk


build-install: build-with-native install


inc-init:
	mv assets/ assets.1/ && mkdir assets/ && mv assets.1/ assets/res && mkdir assets/incupdatelibs


rebuild-cocos:
	./build_native.sh NDK_DEBUG=1
	mv -v libs/armeabi/libcocos2dcpp.so assets/incupdatelibs/
	ant debug && adb install -r bin/RebornBird-debug.apk && adb shell am start -n 'io.bright.rebornbird/.UpdateActivity'


update-version:
	./build_native.sh NDK_DEBUG=1
	mv -v libs/armeabi/libcocos2dcpp.so assets/incupdatelibs/
	${INC_TOOLS_DIR}/zip_update.py ./

