#include <stddef.h>
#include <dlfcn.h>

__attribute__((visibility("default")))
void app__pojav_linkerhook_pass_handles(void* a, void* b, void* c) {
    (void)a; (void)b; (void)c;
}

__attribute__((visibility("default")))
void app__pojav_linkerhook_stub(void) {}
