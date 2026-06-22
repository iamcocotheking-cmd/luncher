# DURBIN v32 Server Compile Fix

Fixed compile errors from v31:

DurbinServerListOverlay.kt:
- Added import android.graphics.Bitmap
- Added import androidx.compose.foundation.border

Resources:
- Added missing drawable ic_px_play.xml

These fix:
- Unresolved reference 'border'
- Unresolved reference 'Bitmap'
- Unresolved reference 'ic_px_play'
- asImageBitmap receiver mismatch caused by missing Bitmap import
