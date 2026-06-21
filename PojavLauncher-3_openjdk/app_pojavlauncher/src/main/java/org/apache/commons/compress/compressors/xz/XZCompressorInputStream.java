package org.apache.commons.compress.compressors.xz;
import java.io.*;
public class XZCompressorInputStream extends FilterInputStream {
    public XZCompressorInputStream(InputStream in){ super(in); }
}
