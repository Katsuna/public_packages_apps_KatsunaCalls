LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, app/src/main)

LOCAL_MANIFEST_FILE := app/src/main/AndroidManifest.xml
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/app/src/main/res
LOCAL_RESOURCE_DIR += frameworks/support/v7/appcompat/res
LOCAL_RESOURCE_DIR += frameworks/support/v7/recyclerview/res
LOCAL_RESOURCE_DIR += frameworks/support/design/res

# Include KatsunaCommon into this app
LOCAL_REQUIRED_MODULES := KatsunaCommon
LOCAL_STATIC_JAVA_LIBRARIES := KatsunaCommon

LOCAL_STATIC_JAVA_LIBRARIES += android-support-v4
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-appcompat
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-recyclerview
LOCAL_STATIC_JAVA_LIBRARIES += android-support-design

# Include the specified aar, DEFINED in KatsunaCommon's Android.mk!
LOCAL_STATIC_JAVA_AAR_LIBRARIES := roundedimageview

LOCAL_AAPT_INCLUDE_ALL_RESOURCES := true
LOCAL_AAPT_FLAGS := --auto-add-overlay
LOCAL_AAPT_FLAGS += --generate-dependencies
# TODO: Don't know if actually needed
LOCAL_AAPT_FLAGS += --extra-packages com.katsuna.common
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.appcompat
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.recyclerview
LOCAL_AAPT_FLAGS += --extra-packages android.support.design
# This is to include the aar's RESOURCES into this app
# Notice the full packagename
LOCAL_AAPT_FLAGS += --extra-packages com.makeramen.roundedimageview

LOCAL_PACKAGE_NAME := KatsunaCalls
LOCAL_CERTIFICATE := platform
#LOCAL_PROGUARD_FLAG_FILES := app/proguard-rules.pro

LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

include $(call all-makefiles-under,$(LOCAL_PATH))
