#include <jni.h>
#include <android/log.h>

#define LOG_TAG "DURBIN_GLFW_STUB"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

JNIEXPORT void JNICALL
Java_git_artdeell_dnbootstrap_glfw_GLFW_initialize(JNIEnv *env, jclass clazz) {
    (void)env;
    (void)clazz;
    LOGI("GLFW fallback stub initialized");
}

JNIEXPORT void JNICALL
Java_git_artdeell_dnbootstrap_glfw_GLFW_sendMousePosition0(JNIEnv *env, jclass clazz, jdouble x, jdouble y) {
    (void)env;
    (void)clazz;
    (void)x;
    (void)y;
}

JNIEXPORT void JNICALL
Java_git_artdeell_dnbootstrap_glfw_GLFW_sendKeyEvent(JNIEnv *env, jclass clazz, jint key, jint state, jint mods) {
    (void)env;
    (void)clazz;
    (void)key;
    (void)state;
    (void)mods;
}

JNIEXPORT void JNICALL
Java_git_artdeell_dnbootstrap_glfw_GLFW_sendRawKeyEvent(JNIEnv *env, jclass clazz, jint androidCode, jint state, jint mods, jchar codepoint) {
    (void)env;
    (void)clazz;
    (void)androidCode;
    (void)state;
    (void)mods;
    (void)codepoint;
}

JNIEXPORT void JNICALL
Java_git_artdeell_dnbootstrap_glfw_GLFW_sendMouseEvent(JNIEnv *env, jclass clazz, jint button, jint state, jint mods) {
    (void)env;
    (void)clazz;
    (void)button;
    (void)state;
    (void)mods;
}

JNIEXPORT void JNICALL
Java_git_artdeell_dnbootstrap_glfw_GLFW_sendBulkUnicodeEvent(JNIEnv *env, jclass clazz, jstring input, jint mods) {
    (void)env;
    (void)clazz;
    (void)input;
    (void)mods;
}

JNIEXPORT void JNICALL
Java_git_artdeell_dnbootstrap_glfw_GLFW_sendScrollEvent(JNIEnv *env, jclass clazz, jdouble xoffset, jdouble yoffset) {
    (void)env;
    (void)clazz;
    (void)xoffset;
    (void)yoffset;
}

JNIEXPORT void JNICALL
Java_git_artdeell_dnbootstrap_glfw_GLFW_nativeSurfaceCreated(JNIEnv *env, jclass clazz, jobject surface) {
    (void)env;
    (void)clazz;
    (void)surface;
}

JNIEXPORT void JNICALL
Java_git_artdeell_dnbootstrap_glfw_GLFW_nativeSurfaceDestroyed(JNIEnv *env, jclass clazz) {
    (void)env;
    (void)clazz;
}

JNIEXPORT void JNICALL
Java_git_artdeell_dnbootstrap_glfw_GLFW_nativeSurfaceUpdated(JNIEnv *env, jclass clazz) {
    (void)env;
    (void)clazz;
}

JNIEXPORT void JNICALL
Java_git_artdeell_dnbootstrap_glfw_GLFW_nativeNotifyGamepadConnected(JNIEnv *env, jclass clazz) {
    (void)env;
    (void)clazz;
}
