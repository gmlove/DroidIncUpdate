#include <jni.h>
#include "bspatch.h"

extern "C" {

JNIEXPORT jint JNICALL
Java_com_comeplus_droidincupdate_BSPatch_bspatch(
		JNIEnv *env,
		jobject obj,
		jstring old_file,
		jstring new_file,
		jstring patch_file)
{
	const char *old_file_str;
	const char *new_file_str;
	const char *patch_file_str;

	old_file_str = env->GetStringUTFChars(old_file, 0);
	if (!old_file_str) {
		return 0;
	}

	new_file_str = env->GetStringUTFChars(new_file, 0);
	if (!new_file_str) {
		return 0;
	}

	patch_file_str = env->GetStringUTFChars(patch_file, 0);
	if (!patch_file_str) {
		return 0;
	}

	int bspatchSucceeded = bspatch(old_file_str, new_file_str, patch_file_str);

	env->ReleaseStringUTFChars(old_file, old_file_str);
	env->ReleaseStringUTFChars(new_file, new_file_str);
	env->ReleaseStringUTFChars(patch_file, patch_file_str);

	return bspatchSucceeded;
}

}
