/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <stdio.h>
#include <jni.h>
#include <dlfcn.h>
#include <android/log.h>

static void* handler;
static int (*addFunc)(int, int);
const char* LOG_TAG = "jni-loader";

jint
Java_com_comeplus_droidincupdate_demo_MainActivity_load( JNIEnv*  env,
                                      jobject  this,
                                      jstring  libpath)
{
	char *c_libpath = NULL;
	c_libpath = (*env)->GetStringUTFChars(env, libpath, 0);
	__android_log_print(ANDROID_LOG_INFO, LOG_TAG, "begin to load lib: %s", c_libpath);
	if((handler = dlopen(c_libpath, RTLD_NOW)) == NULL) {
		__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "load lib failed: lib=%s, err=%s", c_libpath, dlerror());
		return -1;
	}
	if((addFunc = dlsym(handler, "add")) == NULL) {
		return -2;
	}
    return 0;
}

jint
Java_com_comeplus_droidincupdate_demo_MainActivity_add( JNIEnv*  env,
                                      jobject  this,
                                      jint     x,
                                      jint     y)
{
	return (*addFunc)(x, y);
}

jint
Java_com_comeplus_droidincupdate_demo_MainActivity_unload( JNIEnv*  env,
                                      jobject  this,
                                      jstring  libpath)
{
	char *c_libpath = NULL;
	c_libpath = (*env)->GetStringUTFChars(env, libpath, 0);
	if(handler != NULL) {
		if(dlclose(handler) != 0) {
			__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "unload lib failed: lib=%s, err=%s", c_libpath, dlerror());
			return 1;
		} else {
			addFunc = NULL;
			handler = NULL;
		}
	} else {
		__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "unload lib, lib not loaded: lib=%s", c_libpath);
	}
	return 0;
}
