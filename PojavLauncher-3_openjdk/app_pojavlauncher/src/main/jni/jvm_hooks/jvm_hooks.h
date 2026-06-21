#pragma once

#include <jni.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

void hookExec(JNIEnv *env);
void installLwjglDlopenHook(JNIEnv *env);
void installEMUIIteratorMititgation(JNIEnv *env);

#ifdef __cplusplus
}
#endif
