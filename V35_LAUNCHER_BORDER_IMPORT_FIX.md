# DURBIN v35 Launcher border import fix

Fixed current compile error:

LauncherScreen.kt:
- Unresolved reference 'border'

Cause:
v34 added a banner card on the DURBIN mod page using Modifier.border(...),
but LauncherScreen.kt did not import androidx.compose.foundation.border.

Fix:
- Added exact import:
  import androidx.compose.foundation.border

Keeps:
- v34 launch metadata fix
- Google guest fallback
- redesigned server list
- DURBIN banner on mod page
