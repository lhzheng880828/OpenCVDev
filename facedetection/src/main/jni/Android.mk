LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#OPENCV_INSTALL_MODULES:=off

#OPENCV_ANDROID_SDK := ../../../../opencvlib
#OPENCV_LIB_TYPE:=SHARED
ifdef OPENCV_ANDROID_SDK
  ifneq ("","$(wildcard $(OPENCV_ANDROID_SDK)/OpenCV.mk)")
    include ${OPENCV_ANDROID_SDK}/OpenCV.mk
  else
    include ${OPENCV_ANDROID_SDK}/native/jni/OpenCV.mk
  endif
else
  #include ../../../../opencvlib/native/jni/OpenCV.mk
  include /home/linhu/code/MyAppCode/LinhuAndroidApp/OpenCVDev/opencvlib/native/jni/OpenCV.mk
endif

LOCAL_SRC_FILES  := DetectionBasedTracker_jni.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS     += -llog -ldl

LOCAL_MODULE     := detection_based_tracker

include $(BUILD_SHARED_LIBRARY)
