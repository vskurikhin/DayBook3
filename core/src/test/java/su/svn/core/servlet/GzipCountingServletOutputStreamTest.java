package su.svn.core.servlet;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.*;

class GzipCountingServletOutputStreamTest {

    // mock ServletOutputStream
    static class TestServletOutputStreamMock extends ServletOutputStream {

        private final ByteArrayOutputStream baos;

        TestServletOutputStreamMock(ByteArrayOutputStream baos) {
            this.baos = baos;
        }

        @Override
        public void write(int b) {
            baos.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) {
            baos.write(b, off, len);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {}
    }

    @Test
    void shouldCountRawBytesUsingWriteInt() throws IOException {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        GzipCountingServletOutputStream stream =
                new GzipCountingServletOutputStream(new TestServletOutputStreamMock(target));

        stream.write('A');
        stream.write('B');

        assertEquals(2, stream.getRawSize());
        assertArrayEquals(new byte[]{'A', 'B'}, target.toByteArray());
    }

    @Test
    void shouldCountRawBytesUsingByteArrayWrite() throws IOException {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        GzipCountingServletOutputStream stream =
                new GzipCountingServletOutputStream(new TestServletOutputStreamMock(target));

        byte[] data = "HelloWorld".getBytes();

        stream.write(data, 0, data.length);

        assertEquals(data.length, stream.getRawSize());
        assertArrayEquals(data, target.toByteArray());
    }

    @Test
    void shouldProduceGzipSizeGreaterThanZero() throws IOException {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        GzipCountingServletOutputStream stream =
                new GzipCountingServletOutputStream(new TestServletOutputStreamMock(target));

        stream.write("Hello GZIP compression test data".getBytes());

        long gzipSize = stream.getGzipSize();

        assertTrue(gzipSize > 0, "Gzip size should be greater than 0");
    }

    @Test
    void gzipSizeShouldBeSmallerThanRawSizeForLargeInput() throws IOException {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        GzipCountingServletOutputStream stream =
                new GzipCountingServletOutputStream(new TestServletOutputStreamMock(target));

        String text = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        byte[] data = text.getBytes();

        stream.write(data);

        long raw = stream.getRawSize();
        long gzip = stream.getGzipSize();

        assertTrue(gzip < raw, "Gzip size should be smaller than raw size for repetitive data");
    }

    @Test
    void shouldPreserveOriginalOutputStreamData() throws IOException {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        GzipCountingServletOutputStream stream =
                new GzipCountingServletOutputStream(new TestServletOutputStreamMock(target));

        String text = "TestData123";

        stream.write(text.getBytes());

        assertEquals(text, target.toString());
    }

    @Test
    void shouldProduceValidGzipContent() throws IOException {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        GzipCountingServletOutputStream stream =
                new GzipCountingServletOutputStream(new TestServletOutputStreamMock(target));

        String original = "Hello compression world";

        stream.write(original.getBytes());

        // важно: завершить gzip
        stream.getGzipSize();

        // проверяем что gzip реально декодируется
        byte[] gzipData = stream.gzipBuffer.toByteArray();

        GZIPInputStream gis =
                new GZIPInputStream(new java.io.ByteArrayInputStream(gzipData));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        gis.transferTo(out);

        assertEquals(original, out.toString());
    }

    static class TestServletOutputStream extends ServletOutputStream {

        private final ByteArrayOutputStream baos;
        private boolean ready;

        TestServletOutputStream(ByteArrayOutputStream baos, boolean ready) {
            this.baos = baos;
            this.ready = ready;
        }

        void setReady(boolean ready) {
            this.ready = ready;
        }

        @Override
        public void write(int b) {
            baos.write(b);
        }

        @Override
        public boolean isReady() {
            return ready;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {}
    }

    @Test
    void shouldReturnTrueWhenOriginalIsReady2() throws IOException {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        TestServletOutputStream original = new TestServletOutputStream(target, true);

        GzipCountingServletOutputStream stream =
                new GzipCountingServletOutputStream(original);

        assertTrue(stream.isReady());
    }

    @Test
    void shouldReturnFalseWhenOriginalIsNotReady2() throws IOException {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        TestServletOutputStream original = new TestServletOutputStream(target, false);

        GzipCountingServletOutputStream stream =
                new GzipCountingServletOutputStream(original);

        assertFalse(stream.isReady());
    }
}