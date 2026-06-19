package net.kdt.pojavlaunch.durbin;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import net.kdt.pojavlaunch.Logger;
import net.kdt.pojavlaunch.instances.Instance;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * DURBIN bundled mod installer.
 *
 * It copies the real DURBIN Fabric mod packs from APK assets into the selected
 * Minecraft profile's mods folder. It only runs for Fabric/DURBIN profiles and
 * only for supported versions.
 */
public final class DurbinModPackInstaller {
    private static final String TAG = "DurbinModPackInstaller";
    private static final String ASSET_ROOT = "durbin_modpacks";

    private DurbinModPackInstaller() {
    }

    public static void installForVersion(Context context, Instance instance, String versionId) {
        if (context == null || instance == null || versionId == null) return;

        String lower = versionId.toLowerCase(Locale.US);
        boolean isFabricLike = lower.contains("fabric") || lower.contains("durbin");
        if (!isFabricLike) {
            Logger.appendToLog("Info: DURBIN mod pack skipped: selected profile is not Fabric/DURBIN");
            return;
        }

        String packVersion = null;
        if (lower.contains("1.21.11")) {
            packVersion = "1.21.11";
        } else if (lower.contains("1.20.1")) {
            packVersion = "1.20.1";
        }

        if (packVersion == null) {
            Logger.appendToLog("Info: DURBIN mod pack skipped: unsupported version " + versionId);
            return;
        }

        File gameDir = instance.getGameDirectory();
        File modsDir = new File(gameDir, "mods");
        if (!modsDir.isDirectory() && !modsDir.mkdirs()) {
            Logger.appendToLog("Warn: DURBIN could not create mods folder: " + modsDir.getAbsolutePath());
            return;
        }

        String assetZip = ASSET_ROOT + "/" + packVersion + ".zip";
        try {
            int copied = extractJars(context.getAssets(), assetZip, modsDir);
            Logger.appendToLog("Info: DURBIN " + packVersion + " bundled mods ready. Installed/updated: " + copied);
        } catch (Throwable t) {
            Log.e(TAG, "Failed to install DURBIN bundled mods", t);
            Logger.appendToLog("Warn: DURBIN bundled mods failed: " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private static int extractJars(AssetManager assets, String assetZip, File modsDir) throws Exception {
        int copied = 0;

        try (InputStream raw = assets.open(assetZip);
             ZipInputStream zip = new ZipInputStream(new BufferedInputStream(raw))) {

            ZipEntry entry;
            byte[] buffer = new byte[64 * 1024];

            while ((entry = zip.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;

                String name = entry.getName();
                if (!name.toLowerCase(Locale.US).endsWith(".jar")) continue;

                int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
                String fileName = slash >= 0 ? name.substring(slash + 1) : name;
                if (fileName.trim().isEmpty()) continue;

                File outFile = new File(modsDir, fileName);
                long expectedSize = entry.getSize();

                if (outFile.isFile() && expectedSize > 0 && outFile.length() == expectedSize) {
                    continue;
                }

                File tempFile = new File(modsDir, fileName + ".durbin_tmp");
                long written = 0;

                try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile))) {
                    int read;
                    while ((read = zip.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                        written += read;
                    }
                }

                if (outFile.exists() && !outFile.delete()) {
                    throw new IllegalStateException("Could not replace old mod: " + outFile.getName());
                }

                if (!tempFile.renameTo(outFile)) {
                    throw new IllegalStateException("Could not save mod: " + outFile.getName());
                }

                copied++;
                Logger.appendToLog("Info: DURBIN mod ready: " + fileName + " (" + written + " bytes)");
            }
        }

        return copied;
    }
}
