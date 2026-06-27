# DURBIN v33 Exact Bitmap Import Fix

Fixed current v32 compile error:

DurbinServerListOverlay.kt:
- Unresolved reference 'Bitmap'
- Cannot infer type for mutableStateOf<Bitmap?>(null)
- asImageBitmap receiver mismatch

Cause:
v32 checked for the text `import android.graphics.Bitmap`, but `import android.graphics.BitmapFactory`
matched the substring. So the exact Bitmap import was not added.

Fix:
- Added exact import:
  import android.graphics.Bitmap
- Made state type explicit:
  var bitmap: Bitmap? by remember(url) { mutableStateOf<Bitmap?>(null) }
