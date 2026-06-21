#pragma once

#ifdef __cplusplus
extern "C" {
#endif

typedef void* bytehook_stub_t;
#define BYTEHOOK_MODE_AUTOMATIC 0

static inline int bytehook_init(int mode, int debug) { (void)mode; (void)debug; return 0; }
static inline bytehook_stub_t bytehook_hook_single(const char* caller_path_name, const char* callee_path_name, const char* sym_name, void* new_func, void** orig_func) {
    (void)caller_path_name; (void)callee_path_name; (void)sym_name; (void)new_func; if (orig_func) *orig_func = 0; return 0;
}
static inline int bytehook_unhook(bytehook_stub_t stub) { (void)stub; return 0; }
static inline const char* bytehook_get_errmsg(int err) { (void)err; return "bytehook stub"; }

#ifdef __cplusplus
}
#endif
