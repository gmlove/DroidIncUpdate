
// auto generated code, do not modify.

#include <stdio.h>
#include <jni.h>
#include <dlfcn.h>
#include <android/log.h>
#include "loader.h"

static jboolean (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeKeyDown_0)(JNIEnv * env, jobject thiz, jint keyCode) ;
static jstring (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeGetContentText_0)() ;
static void (*Java_org_cocos2dx_lib_Cocos2dxAccelerometer_onSensorChanged_0)(JNIEnv*  env, jobject thiz, jfloat x, jfloat y, jfloat z, jlong timeStamp) ;
static void (*Java_org_cocos2dx_lib_Cocos2dxBitmap_nativeInitBitmapDC_0)(JNIEnv*  env, jobject thiz, int width, int height, jbyteArray pixels);
static void (*Java_org_cocos2dx_lib_Cocos2dxETCLoader_nativeSetTextureInfo_0)(JNIEnv* env, jobject thiz, jint width, jint height, jbyteArray data, jint dataLength);
static void (*Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetApkPath_0)(JNIEnv*  env, jobject thiz, jstring apkPath) ;
static void (*Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetEditTextDialogResult_0)(JNIEnv * env, jobject obj, jbyteArray text) ;
static void (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeDeleteBackward_0)(JNIEnv* env, jobject thiz) ;
static void (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeInsertText_0)(JNIEnv* env, jobject thiz, jstring text) ;
static void (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeOnPause_0)() ;
static void (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeOnResume_0)() ;
static void (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeRender_0)(JNIEnv* env) ;
static void (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesBegin_0)(JNIEnv * env, jobject thiz, jint id, jfloat x, jfloat y) ;
static void (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesCancel_0)(JNIEnv * env, jobject thiz, jintArray ids, jfloatArray xs, jfloatArray ys) ;
static void (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesEnd_0)(JNIEnv * env, jobject thiz, jint id, jfloat x, jfloat y) ;
static void (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesMove_0)(JNIEnv * env, jobject thiz, jintArray ids, jfloatArray xs, jfloatArray ys) ;
static void (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeInit_0)(JNIEnv*  env, jobject thiz, jint w, jint h);
static void (*Java_org_cocos2dx_lib_Config_initPath_0)(JNIEnv* env, jobject thiz, jboolean useAssets, jstring resPath);
static int (*JNI_OnLoad_0)(JavaVM* vm, void* reserved);

int init_binding(JavaVM* vm, void* reserved, void* handler) {

    Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeKeyDown_0 = dlsym(handler, "Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeKeyDown");
    if((Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeKeyDown_0) == NULL) {
        return -2;
    }


    Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeGetContentText_0 = dlsym(handler, "Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeGetContentText");
    if((Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeGetContentText_0) == NULL) {
        return -2;
    }


    Java_org_cocos2dx_lib_Cocos2dxAccelerometer_onSensorChanged_0 = dlsym(handler, "Java_org_cocos2dx_lib_Cocos2dxAccelerometer_onSensorChanged");
    if((Java_org_cocos2dx_lib_Cocos2dxAccelerometer_onSensorChanged_0) == NULL) {
        return -2;
    }


    Java_org_cocos2dx_lib_Cocos2dxBitmap_nativeInitBitmapDC_0 = dlsym(handler, "Java_org_cocos2dx_lib_Cocos2dxBitmap_nativeInitBitmapDC");
    if((Java_org_cocos2dx_lib_Cocos2dxBitmap_nativeInitBitmapDC_0) == NULL) {
        return -2;
    }


    Java_org_cocos2dx_lib_Cocos2dxETCLoader_nativeSetTextureInfo_0 = dlsym(handler, "Java_org_cocos2dx_lib_Cocos2dxETCLoader_nativeSetTextureInfo");
    if((Java_org_cocos2dx_lib_Cocos2dxETCLoader_nativeSetTextureInfo_0) == NULL) {
        return -2;
    }


    Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetApkPath_0 = dlsym(handler, "Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetApkPath");
    if((Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetApkPath_0) == NULL) {
        return -2;
    }


    Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetEditTextDialogResult_0 = dlsym(handler, "Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetEditTextDialogResult");
    if((Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetEditTextDialogResult_0) == NULL) {
        return -2;
    }


    Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeDeleteBackward_0 = dlsym(handler, "Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeDeleteBackward");
    if((Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeDeleteBackward_0) == NULL) {
        return -2;
    }


    Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeInsertText_0 = dlsym(handler, "Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeInsertText");
    if((Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeInsertText_0) == NULL) {
        return -2;
    }


    Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeOnPause_0 = dlsym(handler, "Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeOnPause");
    if((Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeOnPause_0) == NULL) {
        return -2;
    }


    Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeOnResume_0 = dlsym(handler, "Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeOnResume");
    if((Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeOnResume_0) == NULL) {
        return -2;
    }


    Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeRender_0 = dlsym(handler, "Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeRender");
    if((Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeRender_0) == NULL) {
        return -2;
    }


    Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesBegin_0 = dlsym(handler, "Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesBegin");
    if((Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesBegin_0) == NULL) {
        return -2;
    }


    Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesCancel_0 = dlsym(handler, "Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesCancel");
    if((Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesCancel_0) == NULL) {
        return -2;
    }


    Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesEnd_0 = dlsym(handler, "Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesEnd");
    if((Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesEnd_0) == NULL) {
        return -2;
    }


    Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesMove_0 = dlsym(handler, "Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesMove");
    if((Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesMove_0) == NULL) {
        return -2;
    }


    Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeInit_0 = dlsym(handler, "Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeInit");
    if((Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeInit_0) == NULL) {
        return -2;
    }


    Java_org_cocos2dx_lib_Config_initPath_0 = dlsym(handler, "Java_org_cocos2dx_lib_Config_initPath");
    if((Java_org_cocos2dx_lib_Config_initPath_0) == NULL) {
        return -2;
    }


    JNI_OnLoad_0 = dlsym(handler, "JNI_OnLoad");
    if((JNI_OnLoad_0) == NULL) {
        return -2;
    }

    JNI_OnLoad_0(vm, reserved);
    return 0;
}



JNIEXPORT jboolean JNICALL Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeKeyDown(JNIEnv * env, jobject thiz, jint keyCode)  {
    return (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeKeyDown_0)(env,thiz,keyCode);
}




JNIEXPORT jstring JNICALL Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeGetContentText()  {
    return (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeGetContentText_0)();
}




JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxAccelerometer_onSensorChanged(JNIEnv*  env, jobject thiz, jfloat x, jfloat y, jfloat z, jlong timeStamp)  {
    return (*Java_org_cocos2dx_lib_Cocos2dxAccelerometer_onSensorChanged_0)(env,thiz,x,y,z,timeStamp);
}




JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxBitmap_nativeInitBitmapDC(JNIEnv*  env, jobject thiz, int width, int height, jbyteArray pixels) {
    return (*Java_org_cocos2dx_lib_Cocos2dxBitmap_nativeInitBitmapDC_0)(env,thiz,width,height,pixels);
}




JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxETCLoader_nativeSetTextureInfo(JNIEnv* env, jobject thiz, jint width, jint height, jbyteArray data, jint dataLength) {
    return (*Java_org_cocos2dx_lib_Cocos2dxETCLoader_nativeSetTextureInfo_0)(env,thiz,width,height,data,dataLength);
}




JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetApkPath(JNIEnv*  env, jobject thiz, jstring apkPath)  {
    return (*Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetApkPath_0)(env,thiz,apkPath);
}




JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetEditTextDialogResult(JNIEnv * env, jobject obj, jbyteArray text)  {
    return (*Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetEditTextDialogResult_0)(env,obj,text);
}




JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeDeleteBackward(JNIEnv* env, jobject thiz)  {
    return (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeDeleteBackward_0)(env,thiz);
}




JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeInsertText(JNIEnv* env, jobject thiz, jstring text)  {
    return (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeInsertText_0)(env,thiz,text);
}




JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeOnPause()  {
    return (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeOnPause_0)();
}




JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeOnResume()  {
    return (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeOnResume_0)();
}




JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeRender(JNIEnv* env)  {
    return (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeRender_0)(env);
}




JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesBegin(JNIEnv * env, jobject thiz, jint id, jfloat x, jfloat y)  {
    return (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesBegin_0)(env,thiz,id,x,y);
}




JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesCancel(JNIEnv * env, jobject thiz, jintArray ids, jfloatArray xs, jfloatArray ys)  {
    return (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesCancel_0)(env,thiz,ids,xs,ys);
}




JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesEnd(JNIEnv * env, jobject thiz, jint id, jfloat x, jfloat y)  {
    return (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesEnd_0)(env,thiz,id,x,y);
}




JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesMove(JNIEnv * env, jobject thiz, jintArray ids, jfloatArray xs, jfloatArray ys)  {
    return (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesMove_0)(env,thiz,ids,xs,ys);
}




JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeInit(JNIEnv*  env, jobject thiz, jint w, jint h) {
    return (*Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeInit_0)(env,thiz,w,h);
}




JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Config_initPath(JNIEnv* env, jobject thiz, jboolean useAssets, jstring resPath) {
    return (*Java_org_cocos2dx_lib_Config_initPath_0)(env,thiz,useAssets,resPath);
}




