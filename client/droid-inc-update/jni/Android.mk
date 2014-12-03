LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := DroidBSDiff
LOCAL_SRC_FILES := DroidBSDiff.cpp blocksort.c bzlib.c compress.c crctable.c decompress.c huffman.c randtable.c bspatch.c

include $(BUILD_SHARED_LIBRARY)
