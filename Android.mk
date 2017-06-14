
LOCAL_PATH:= $(call my-dir)

#include $(call all-makefiles-under,$(LOCAL_PATH))

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_PACKAGE_NAME := RKUpdateService

LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true
LOCAL_DEX_PREOPT := false

LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_JNI_SHARED_LIBRARIES := librockchip_update_jni
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_JAVA_LIBRARIES := core-libart conscrypt okhttp core-junit bouncycastle ext

include $(BUILD_PACKAGE)


include $(call all-makefiles-under,$(LOCAL_PATH))
