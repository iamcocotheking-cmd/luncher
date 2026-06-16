package net.kdt.pojavlaunch.durbin;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public final class DurbinClientManager {
    // Replace this with your real raw GitHub/GitLab/CDN manifest URL later.
    public static final String DEFAULT_MANIFEST_URL = "https://example.com/durbin-client-manifest.json";

    private final File minecraftDir;
    private final File modsDir;

    public DurbinClientManager(File minecraftDir) {
        this.minecraftDir = minecraftDir;
        this.modsDir = new File(minecraftDir, "mods");
    }

    public interface ProgressCallback {
        void onProgress(String message);
    }

    public DurbinInstallResult ensureFromUrl(String manifestUrl, ProgressCallback callback) {
        try {
            progress(callback, "Loading DURBIN manifest...");
            String json = downloadText(manifestUrl);
            DurbinClientManifest manifest = DurbinClientManifest.fromJson(json);
            return ensureInstalled(manifest, callback);
        } catch (Exception e) {
            return DurbinInstallResult.fail("DURBIN setup failed: " + e.getMessage());
        }
    }

    public DurbinInstallResult ensureInstalled(DurbinClientManifest manifest, ProgressCallback callback) {
        try {
            if (!minecraftDir.exists()) {
                return DurbinInstallResult.fail("Minecraft folder not found: " + minecraftDir.getAbsolutePath());
            }

            if (!modsDir.exists() && !modsDir.mkdirs()) {
                return DurbinInstallResult.fail("Could not create mods folder: " + modsDir.getAbsolutePath());
            }

            List<DurbinClientManifest.ModFile> files = new ArrayList<>();
            files.add(manifest.durbinMod);
            files.addAll(manifest.dependencies);

            for (DurbinClientManifest.ModFile file : files) {
                ensureFile(file, callback);
            }

            return DurbinInstallResult.ok("DURBIN Client is ready.");
        } catch (Exception e) {
            return DurbinInstallResult.fail("DURBIN install failed: " + e.getMessage());
        }
    }

    private void ensureFile(DurbinClientManifest.ModFile modFile, ProgressCallback callback) throws Exception {
        if (modFile.fileName == null || modFile.fileName.trim().isEmpty()) {
            throw new IOException("Manifest has a mod/dependency with no fileName.");
        }
        if (modFile.downloadUrl == null || modFile.downloadUrl.trim().isEmpty()) {
            throw new IOException(modFile.fileName + " has no downloadUrl.");
        }

        File target = new File(modsDir, modFile.fileName);
        if (target.exists() && verifySha256IfPresent(target, modFile.sha256)) {
            progress(callback, modFile.name + " already installed.");
            return;
        }

        progress(callback, "Downloading " + modFile.name + "...");
        File temp = new File(modsDir, modFile.fileName + ".download");
        downloadFile(modFile.downloadUrl, temp);

        if (!verifySha256IfPresent(temp, modFile.sha256)) {
            safeDelete(temp);
            throw new IOException("SHA-256 check failed for " + modFile.fileName);
        }

        if (target.exists() && !target.delete()) {
            throw new IOException("Could not replace old file: " + target.getName());
        }
        if (!temp.renameTo(target)) {
            copyFile(temp, target);
            safeDelete(temp);
        }
        progress(callback, "Installed " + modFile.name + ".");
    }

    private static String downloadText(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(30000);
        connection.setRequestProperty("User-Agent", "DURBIN-Launcher");

        int code = connection.getResponseCode();
        if (code < 200 || code >= 300) {
            throw new IOException("HTTP " + code + " while downloading manifest");
        }

        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        } finally {
            reader.close();
            connection.disconnect();
        }
        return builder.toString();
    }

    private static void downloadFile(String url, File target) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(60000);
        connection.setRequestProperty("User-Agent", "DURBIN-Launcher");

        int code = connection.getResponseCode();
        if (code < 200 || code >= 300) {
            throw new IOException("HTTP " + code + " while downloading " + url);
        }

        InputStream in = new BufferedInputStream(connection.getInputStream());
        FileOutputStream out = new FileOutputStream(target);
        try {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } finally {
            out.close();
            in.close();
            connection.disconnect();
        }
    }

    private static boolean verifySha256IfPresent(File file, String expectedSha256) throws IOException, NoSuchAlgorithmException {
        if (expectedSha256 == null || expectedSha256.trim().isEmpty() || expectedSha256.startsWith("PUT_REAL")) {
            return true;
        }
        String actual = sha256(file);
        return expectedSha256.equalsIgnoreCase(actual);
    }

    private static String sha256(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        FileInputStream in = new FileInputStream(file);
        try {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        } finally {
            in.close();
        }

        byte[] hash = digest.digest();
        StringBuilder builder = new StringBuilder();
        for (byte b : hash) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    private static void copyFile(File source, File target) throws IOException {
        FileInputStream in = new FileInputStream(source);
        FileOutputStream out = new FileOutputStream(target);
        try {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } finally {
            out.close();
            in.close();
        }
    }

    private static void safeDelete(File file) {
        if (file != null && file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
    }

    private static void progress(ProgressCallback callback, String message) {
        if (callback != null) {
            callback.onProgress(message);
        }
    }
}
