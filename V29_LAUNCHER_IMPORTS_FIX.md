# DURBIN v29 Launcher Imports Fix

Fixed compile error in LauncherScreen.kt.

Problem:
The new DURBIN download button animation used:
- MutableInteractionSource
- collectIsPressedAsState
- Modifier.scale

But LauncherScreen.kt did not import them.

Added:
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.scale
