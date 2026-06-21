#pragma once

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_net_kdt_pojavlaunch_Logger_begin(JNIEnv *env, jclass clazz, jstring logPath);
JNIEXPORT void JNICALL Java_net_kdt_pojavlaunch_Logger_appendToLog(JNIEnv *env, jclass clazz, jstring text);
JNIEXPORT void JNICALL Java_net_kdt_pojavlaunch_Logger_setLogListener(JNIEnv *env, jclass clazz, jobject log_listener);

#ifdef __cplusplus
}
#endif
