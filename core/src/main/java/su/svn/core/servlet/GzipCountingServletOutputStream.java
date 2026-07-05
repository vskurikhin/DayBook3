/*
 * This file was last modified at 2026.05.07 14:57 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * GzipCountingServletOutputStream.java
 * $Id$
 */

package su.svn.core.servlet;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.experimental.PackagePrivate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GzipCountingServletOutputStream extends ServletOutputStream {

    ServletOutputStream original;
    @PackagePrivate
    ByteArrayOutputStream gzipBuffer = new ByteArrayOutputStream();
    GZIPOutputStream gzipStream;

    @Getter
    @NonFinal
    long rawSize = 0;

    public GzipCountingServletOutputStream(ServletOutputStream original) throws IOException {
        this.original = original;
        this.gzipStream = new GZIPOutputStream(gzipBuffer);
    }

    @Override
    public void write(int b) throws IOException {
        original.write(b);
        gzipStream.write(b);
        rawSize++;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        original.write(b, off, len);
        gzipStream.write(b, off, len);
        rawSize += len;
    }

    public long getGzipSize() throws IOException {
        gzipStream.finish(); // важно завершить gzip
        return gzipBuffer.size();
    }

    @Override
    public boolean isReady() {
        return original.isReady();
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        original.setWriteListener(writeListener);
    }
}