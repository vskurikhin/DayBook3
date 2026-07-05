package su.svn.core.servlet;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class GzipCountingResponseWrapperTest {

    // mock ServletOutputStream
    static class TestServletOutputStreamAMock extends ServletOutputStream {

        private final ByteArrayOutputStream baos;

        TestServletOutputStreamAMock(ByteArrayOutputStream baos) {
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

    // mock HttpServletResponse
    static class TestHttpServletResponseAMock implements HttpServletResponse {

        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        @Override
        public String getCharacterEncoding() {
            return "";
        }

        @Override
        public String getContentType() {
            return "";
        }

        @Override
        public ServletOutputStream getOutputStream() {
            return new TestServletOutputStreamAMock(baos);
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return null;
        }

        @Override
        public void setCharacterEncoding(String charset) {

        }

        @Override
        public void setContentLength(int len) {

        }

        @Override
        public void setContentLengthLong(long length) {

        }

        @Override
        public void setContentType(String type) {

        }

        @Override
        public void setBufferSize(int size) {

        }

        @Override
        public int getBufferSize() {
            return 0;
        }

        @Override
        public void flushBuffer() throws IOException {

        }

        @Override
        public void resetBuffer() {

        }

        @Override
        public boolean isCommitted() {
            return false;
        }

        @Override
        public void reset() {

        }

        @Override
        public void setLocale(Locale loc) {

        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public void addCookie(Cookie cookie) {

        }

        @Override
        public boolean containsHeader(String name) {
            return false;
        }

        @Override
        public String encodeURL(String url) {
            return "";
        }

        @Override
        public String encodeRedirectURL(String url) {
            return "";
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {

        }

        @Override
        public void sendError(int sc) throws IOException {

        }

        @Override
        public void sendRedirect(String location) throws IOException {

        }

        @Override
        public void setDateHeader(String name, long date) {

        }

        @Override
        public void addDateHeader(String name, long date) {

        }

        @Override
        public void setHeader(String name, String value) {

        }

        @Override
        public void addHeader(String name, String value) {

        }

        @Override
        public void setIntHeader(String name, int value) {

        }

        @Override
        public void addIntHeader(String name, int value) {

        }

        @Override
        public void setStatus(int sc) {

        }

        @Override
        public int getStatus() {
            return 0;
        }

        @Override
        public String getHeader(String name) {
            return "";
        }

        @Override
        public Collection<String> getHeaders(String name) {
            return List.of();
        }

        @Override
        public Collection<String> getHeaderNames() {
            return List.of();
        }
    }

    @Test
    void shouldWriteViaOutputStreamAndCountRawSize() throws IOException {
        TestHttpServletResponseAMock response = new TestHttpServletResponseAMock();
        GzipCountingResponseWrapper wrapper = new GzipCountingResponseWrapper(response);

        ServletOutputStream out = wrapper.getOutputStream();

        out.write("Hello".getBytes());

        assertEquals(5, wrapper.getRawSize());
    }

    @Test
    void shouldWriteViaWriterAndCountRawSize() throws IOException {
        TestHttpServletResponseAMock response = new TestHttpServletResponseAMock();
        GzipCountingResponseWrapper wrapper = new GzipCountingResponseWrapper(response);

        PrintWriter writer = wrapper.getWriter();
        writer.write("HelloWorld");

        writer.flush();

        assertEquals(10, wrapper.getRawSize());
    }

    @Test
    void shouldThrowExceptionIfWriterUsedAfterOutputStream() throws IOException {
        TestHttpServletResponseAMock response = new TestHttpServletResponseAMock();
        GzipCountingResponseWrapper wrapper = new GzipCountingResponseWrapper(response);

        wrapper.getOutputStream();

        assertThrows(IllegalStateException.class, wrapper::getWriter);
    }

    @Test
    void shouldThrowExceptionIfOutputStreamUsedAfterWriter() throws IOException {
        TestHttpServletResponseAMock response = new TestHttpServletResponseAMock();
        GzipCountingResponseWrapper wrapper = new GzipCountingResponseWrapper(response);

        wrapper.getWriter();

        assertThrows(IllegalStateException.class, wrapper::getOutputStream);
    }

    @Test
    void shouldReturnZeroWhenStreamNotUsed() {
        TestHttpServletResponseAMock response = new TestHttpServletResponseAMock();
        GzipCountingResponseWrapper wrapper = new GzipCountingResponseWrapper(response);

        assertEquals(0, wrapper.getRawSize());
    }

    @Test
    void shouldReturnGzipSizeGreaterThanZero() throws IOException {
        TestHttpServletResponseAMock response = new TestHttpServletResponseAMock();
        GzipCountingResponseWrapper wrapper = new GzipCountingResponseWrapper(response);

        ServletOutputStream out = wrapper.getOutputStream();

        out.write("Hello compression test data".getBytes());

        long gzipSize = wrapper.getGzipSize();

        assertTrue(gzipSize > 0);
    }

    @Test
    void shouldReturnSameStreamInstanceOnMultipleCalls() throws IOException {
        TestHttpServletResponseAMock response = new TestHttpServletResponseAMock();
        GzipCountingResponseWrapper wrapper = new GzipCountingResponseWrapper(response);

        ServletOutputStream s1 = wrapper.getOutputStream();
        ServletOutputStream s2 = wrapper.getOutputStream();

        assertSame(s1, s2);
    }
}