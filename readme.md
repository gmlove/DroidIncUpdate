# DroidIncUpdate

This is a simple project providing incremental updating facilities for android apps.

With the updating feature, you can update your app, which has already been installed on your users' android devices, dynamically at any time. With dynamically updating, you'll no more need to depend on the app market to update your app, you'll no more need to send new version of your app to app market to review or release. This feature is so great if you release your app frequently, and want new features delivered to your users as soon as possible.

With the incremental updating feature, your users only need to download the changes from his old version to the new version to start the app. This will help your user save a lot of time and money.

But, there are also some limitations for incremental updating:
* Only the native code can be updated.

These features are urgently needed especially by games developed in c++.

## How does these features achieved?

As we all know, Android provides native code development ability for developers. When we write code in native code (c or c++), Android build tools will compile the code into `.so` file, and Android app load them while running. In fact, we can update the so file before running the app, then the app can run with newer code now. Dynamically app updating achieved!

Thanks to bsdiff and bspatch tools, we can use bsdiff to generate the changes between two different binary files, and use bspatch with the patch file and old file to generate the new file. With these tools, incremental app updating achieved!

## Source code

├── client                             // all source code needed by client
│   ├── droid-inc-update               // lib android project that app must integrate
│   ├── droid-inc-update-demo          // a simple demo
│   ├── droid-inc-update-demo-cocos    // a cocos2dx demo with incremental updating functionality
│   ├── incupdatedata                  // directory to save the generated updating files
│   └── tools                          // tools needed to generate updating files
└── server
    ├── config                         // config files
    ├── deploy.sh                      // deploy script
    ├── npm-install.sh                 // npm install script
    └── src                            // server source code written in nodejs

## Usage

1. Add project reference to your project.properties in your android project directory.
```
android.library.reference.1=relative/path/to/droid-inc-update
```

2. Write bindings in c for all your native methods declared in Java code, and generate `loader.so` file. Why is this required? If you use `System.loadLibrary` or `System.load` API to load your `.so` file, it will not be reloaded even if you replace the so file and restart the app. Because Android operating system controls the loading process of `.so` file. So, we need a `loader.so` to load the real `.so` file, and proxy all native methods (This is what called binding before.) used in Java code.
 With `dlopen` `dlsym` `dlclose` API, this mechanism will be achieved. Refer `client/droid-inc-update-demo/jni/loader.c` to implement your loader.

3. Build your native code into `.so` file, and move `.so` file into a directory in assets. Make directory tree like below.

├── assets
│   ├── incupdatelibs                  // directory to store your `.so` files which need to be updated
│   │   ├── libdemo.so                 // your compiled `.so` file
│   └── res                            // root directory of all of your resource files need to be updated

4. Use `client/tools/zip_update.py` to generate a version of all your resource files and `.so` files.

5. Build your project into an apk. This apk actually supports incremental updating now.

6. Change source code of your `.so` file, like `libdemo.so`, and build it. Replace the file in the assets directory (`assets/incupdatelibs/libdemo.so`). Use `client/tools/zip_update.py` tool to generate a new version of your resources. Start server with `incupdata` directory configured. Start the apk which was built in step 5, and you will realize that the changes actually being applied to the apk.

## Cocos2dx binding

Cocos2d-x binding has already being done by the droid-inc-update project. You can take a look at the code in the `client/droid-inc-update/jni/cocosloader` directory. The binding is based on version 2.2.2 of Cocos2d-x. Other versions of Cocos2d-x may need to change something more.

However, Cocos2d-x binding need to change some source code of Cocos2d-x. File `cocos2dx/platform/android/CCFileUtilsAndroid.cpp` in the Cocos2d-x root directory need to be replaced with `client/cocos-changes/CCFileUtilsAndroid.cpp`. Directory `cocos2dx/platform/android/java/src/` need to be replaced with `client/java`. These changes should be done, because Cocos2d-x load resources from `assets/` in the apk, and we need to load newest resource from file system.

Changes in the `CCFileUtilsAndroid.cpp` file:

* Add initPath function `Java_org_cocos2dx_lib_Config_initPath`, add static variable `s_assetsPath` `s_resDirRootPath` `s_useAssets`.
* Change the implementation of `CCFileUtilsAndroid::isFileExist` `CCFileUtilsAndroid::doGetFileData` to change the resource search path.

Changes in the Java code:

* Add `Config.java` to initPath from Java code and provides fixPath function for other Java code to change resource in apk assets to resource in file system.
* Change all occurrences of getting data from assets to use Config.fixPath first.

## Debug

If you need to debug the code, and do not want to use the updating feature, you can set `com.comeplus.droidincupdate.Config.FORCE_EXTRACT` to true. In this way, you do not need to generate an updating version every time you update your code. Everything will be newest when you build and run your debuggable apk.

## Server

Server is developed with nodejs, and makes full use of the asynchrony of nodejs. So, it'll be very efficient and supports high concurrency requests.

Also, this project already provides very useful functionality for you:

* Client app updating can be limited by percentage or other property of the client android runtime like `API level` or `country` `timezone` and so on.

When you want to test a new feature in a small group of your users, like 10%, or when you want to deliver the new version only to version 5.0 Android users, you can use `client/tool/update_limit.py` to limit the delivery.



