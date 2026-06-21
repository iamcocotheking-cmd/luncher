package net.kdt.pojavlaunch.modloaders.modpacks.imagecache;

import android.graphics.Bitmap;

/** Callback used by ModIconCache to return a loaded icon bitmap. */
@FunctionalInterface
public interface ImageReceiver {
    void onImageAvailable(Bitmap bitmap);
}
