#pragma once

#include <jni.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

void installExitHook(JNIEnv *env);

#ifdef __cplusplus
}
#endif
