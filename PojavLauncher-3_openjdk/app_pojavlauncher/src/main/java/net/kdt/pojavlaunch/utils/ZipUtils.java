package net.kdt.pojavlaunch.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ZipUtils {
    private ZipUtils() {}

    public static InputStream getEntryStream(ZipFile zipFile, String name) throws IOException {
        ZipEntry entry = zipFile.getEntry(name);
        if (entry == null) throw new IOException("Missing zip entry: " + name);
        return zipFile.getInputStream(entry);
    }

    public static void zipExtract(ZipFile zipFile, String prefix, File destination) throws IOException {
        if (prefix == null) prefix = "";
        final String cleanPrefix = prefix.endsWith("/") || prefix.isEmpty() ? prefix : prefix + "/";
        java.util.Enumeration<? extends ZipEntry> entries = zipFile.entries();
        byte[] buffer = new byte[8192];
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            if (!cleanPrefix.isEmpty()) {
                if (!name.startsWith(cleanPrefix)) continue;
                name = name.substring(cleanPrefix.length());
            }
            if (name.isEmpty()) continue;
            File outFile = new File(destination, name);
            if (entry.isDirectory()) {
                outFile.mkdirs();
                continue;
            }
            File parent = outFile.getParentFile();
            if (parent != null) parent.mkdirs();
            try (InputStream in = zipFile.getInputStream(entry);
                 FileOutputStream out = new FileOutputStream(outFile)) {
                int read;
                while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
            }
        }
    }
}
