#include <android/log.h>

__attribute__((visibility("default")))
void app__pojav_linkerhook_pass_handles(void* turnip_driver_handle, void* android_dlopen_ext_handle, void* android_get_exported_namespace_handle) {
    (void)turnip_driver_handle;
    (void)android_dlopen_ext_handle;
    (void)android_get_exported_namespace_handle;
    __android_log_print(ANDROID_LOG_INFO, "DURBIN", "linkerhook handles received");
}

__attribute__((visibility("default")))
void durbin_linkerhook_placeholder(void) {
    __android_log_print(ANDROID_LOG_INFO, "DURBIN", "linkerhook placeholder loaded");
}
