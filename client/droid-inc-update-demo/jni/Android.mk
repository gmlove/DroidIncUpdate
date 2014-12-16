LOCAL_PATH:= $(call my-dir)

# first lib, which will be built statically
#
#include $(CLEAR_VARS)

#LOCAL_MODULE    := lib1
#LOCAL_SRC_FILES := lib.c

#include $(BUILD_STATIC_LIBRARY)

# second lib, which will depend on and include the first one
#
#include $(CLEAR_VARS)

#LOCAL_MODULE    := demo
#LOCAL_SRC_FILES := demo.c

#LOCAL_STATIC_LIBRARIES := lib1

#include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)

LOCAL_MODULE    := lib1
LOCAL_SRC_FILES := lib.c
#LOCAL_CFLAGS += -g
include $(BUILD_SHARED_LIBRARY)


# second lib, which will depend on and include the first one
#
include $(CLEAR_VARS)

LOCAL_MODULE    := loader
LOCAL_SRC_FILES := loader.c

LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog

include $(BUILD_SHARED_LIBRARY)
