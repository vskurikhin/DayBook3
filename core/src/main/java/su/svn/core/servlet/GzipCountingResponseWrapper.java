/*
 * This file was last modified at 2026.05.07 14:57 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * GzipCountingResponseWrapper.java
 * $Id$
 */

package su.svn.core.servlet;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.experimental.FieldDefaults;

import java.io.IOException;
import java.io.PrintWriter;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
public class GzipCountingResponseWrapper extends HttpServletResponseWrapper {

    GzipCountingServletOutputStream stream;
    PrintWriter writer;

    public GzipCountingResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public jakarta.servlet.ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) throw new IllegalStateException("Writer already used");
        if (stream == null) {
            stream = new GzipCountingServletOutputStream(super.getOutputStream());
        }
        return stream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (stream != null) throw new IllegalStateException("Stream already used");
        stream = new GzipCountingServletOutputStream(super.getOutputStream());
        writer = new PrintWriter(stream, true);
        return writer;
    }

    public long getRawSize() {
        return stream != null ? stream.getRawSize() : 0;
    }

    public long getGzipSize() throws IOException {
        return stream != null ? stream.getGzipSize() : 0;
    }
}