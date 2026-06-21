package org.apache.commons.compress.archivers.tar;
import java.io.*;
public class TarArchiveInputStream extends InputStream {
    private final InputStream in;
    public TarArchiveInputStream(InputStream in){ this.in = in; }
    public TarArchiveEntry getNextTarEntry() throws IOException { return null; }
    public int read() throws IOException { return in.read(); }
    public int read(byte[] b, int off, int len) throws IOException { return in.read(b, off, len); }
    public void close() throws IOException { in.close(); }
}
