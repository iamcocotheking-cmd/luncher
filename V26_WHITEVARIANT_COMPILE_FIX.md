# DURBIN v26 WhiteVariant Compile Fix

Fixed Kotlin compile error:

Unresolved reference 'WhiteVariant'

Cause:
Some UI text replacements accidentally created:
Color.WhiteVariant

Fix:
Replaced all instances with:
Color.White.copy(alpha = 0.70f)

Patched files:
- AuthenticationScreens.kt
- DirectoryManagerScreen.kt
