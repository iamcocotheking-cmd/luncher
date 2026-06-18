# v16 Error Fix

Fixed Kotlin compile error:

Unresolved reference 'verticalScroll'

Cause:
DurbinFirebaseHubActivity.kt used:
.verticalScroll(rememberScrollState())

But the import for verticalScroll was missing.

Added:
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
