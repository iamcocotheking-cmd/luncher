# v22 Dialog Compile Fix

Fixed MainMenuScreen.kt compile error.

Problem:
The terminate dialog had duplicate containerColor parameters after v21.
That caused:
- @Composable invocations can only happen from the context of a @Composable function
- Argument already passed for this parameter

Fix:
- Replaced the terminate dialog with a clean fully-qualified Material3 AlertDialog.
- Removed duplicate containerColor.
- Kept white text.
