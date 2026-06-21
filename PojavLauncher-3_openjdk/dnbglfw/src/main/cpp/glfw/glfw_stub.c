#include <jni.h>
#include <stdint.h>
#include <android/log.h>

#define LOG_TAG "DURBIN_GLFW_STUB"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    (void)vm; (void)reserved;
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL Java_git_artdeell_dnbootstrap_glfw_GLFW_initialize(JNIEnv* env, jclass clazz) {
    (void)env; (void)clazz;
    LOGI("GLFW Java bridge initialized");
}

JNIEXPORT void JNICALL Java_git_artdeell_dnbootstrap_glfw_GLFW_sendMousePosition0(JNIEnv* env, jclass clazz, jdouble x, jdouble y) {
    (void)env; (void)clazz; (void)x; (void)y;
}

JNIEXPORT void JNICALL Java_git_artdeell_dnbootstrap_glfw_GLFW_sendKeyEvent(JNIEnv* env, jclass clazz, jint glfwCode, jint state, jint mods) {
    (void)env; (void)clazz; (void)glfwCode; (void)state; (void)mods;
}

JNIEXPORT void JNICALL Java_git_artdeell_dnbootstrap_glfw_GLFW_sendRawKeyEvent(JNIEnv* env, jclass clazz, jint androidCode, jint state, jint mods, jchar codepoint) {
    (void)env; (void)clazz; (void)androidCode; (void)state; (void)mods; (void)codepoint;
}

JNIEXPORT void JNICALL Java_git_artdeell_dnbootstrap_glfw_GLFW_sendMouseEvent(JNIEnv* env, jclass clazz, jint glfwMouseKey, jint state, jint mods) {
    (void)env; (void)clazz; (void)glfwMouseKey; (void)state; (void)mods;
}

JNIEXPORT void JNICALL Java_git_artdeell_dnbootstrap_glfw_GLFW_sendBulkUnicodeEvent(JNIEnv* env, jclass clazz, jstring input, jint mods) {
    (void)env; (void)clazz; (void)input; (void)mods;
}

JNIEXPORT void JNICALL Java_git_artdeell_dnbootstrap_glfw_GLFW_sendScrollEvent(JNIEnv* env, jclass clazz, jdouble xoffset, jdouble yoffset) {
    (void)env; (void)clazz; (void)xoffset; (void)yoffset;
}

JNIEXPORT void JNICALL Java_git_artdeell_dnbootstrap_glfw_GLFW_nativeSurfaceCreated(JNIEnv* env, jclass clazz, jobject surface) {
    (void)env; (void)clazz; (void)surface;
}

JNIEXPORT void JNICALL Java_git_artdeell_dnbootstrap_glfw_GLFW_nativeSurfaceDestroyed(JNIEnv* env, jclass clazz) {
    (void)env; (void)clazz;
}

JNIEXPORT void JNICALL Java_git_artdeell_dnbootstrap_glfw_GLFW_nativeSurfaceUpdated(JNIEnv* env, jclass clazz) {
    (void)env; (void)clazz;
}

JNIEXPORT void JNICALL Java_git_artdeell_dnbootstrap_glfw_GLFW_nativeNotifyGamepadConnected(JNIEnv* env, jclass clazz) {
    (void)env; (void)clazz;
}

__attribute__((visibility("default"))) int glfwInit(void) { return 1; }
__attribute__((visibility("default"))) void glfwTerminate(void) {}
__attribute__((visibility("default"))) const char* glfwGetVersionString(void) { return "Durbin GLFW build stub"; }
__attribute__((visibility("default"))) void glfwPollEvents(void) {}
__attribute__((visibility("default"))) void glfwWaitEvents(void) {}
__attribute__((visibility("default"))) void glfwPostEmptyEvent(void) {}
__attribute__((visibility("default"))) void glfwSwapBuffers(void* window) { (void)window; }
__attribute__((visibility("default"))) int glfwWindowShouldClose(void* window) { (void)window; return 0; }
__attribute__((visibility("default"))) void glfwSetWindowShouldClose(void* window, int value) { (void)window; (void)value; }
__attribute__((visibility("default"))) void* glfwGetCurrentContext(void) { return 0; }
__attribute__((visibility("default"))) void glfwMakeContextCurrent(void* window) { (void)window; }
__attribute__((visibility("default"))) void glfwSwapInterval(int interval) { (void)interval; }
__attribute__((visibility("default"))) void glfwGetFramebufferSize(void* window, int* width, int* height) { (void)window; if(width) *width = 1; if(height) *height = 1; }
__attribute__((visibility("default"))) void glfwGetWindowSize(void* window, int* width, int* height) { (void)window; if(width) *width = 1; if(height) *height = 1; }
__attribute__((visibility("default"))) void glfwSetWindowTitle(void* window, const char* title) { (void)window; (void)title; }
__attribute__((visibility("default"))) void* glfwCreateWindow(int width, int height, const char* title, void* monitor, void* share) { (void)width; (void)height; (void)title; (void)monitor; (void)share; return (void*)0x1; }
__attribute__((visibility("default"))) void glfwDestroyWindow(void* window) { (void)window; }
