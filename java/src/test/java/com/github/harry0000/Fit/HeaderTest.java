package com.github.harry0000.Fit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class HeaderTest {

    private static final byte[] HEADER          = { 0x0E, 0x10, 0x2B, 0x03, 0x00, 0x00, 0x00, 0x00, 0x2E, 0x46, 0x49, 0x54, 0x00, 0x00 };
    private static final byte[] HEADER_WITH_CRC = { 0x0E, 0x10, 0x2B, 0x03, 0x00, 0x00, 0x00, 0x00, 0x2E, 0x46, 0x49, 0x54, 0x07, (byte) 0xFA };

    @Test
    public void readHeader() throws IOException {
        try (final ByteArrayInputStream in = new ByteArrayInputStream(HEADER_WITH_CRC)) {
            final Header h = new Header();
            h.readHeader(in);

            assertThat(h.getHeaderSize(),      is((byte) 0x0E));
            assertThat(h.getProtocolVersion(), is((byte) 0x10));
            assertThat(h.getProfileVersion(),  is((short) 0x032B));
            assertThat(h.getDataSize(),        is((int) 0x00000000));
            assertThat(h.getDataType(),        is(".FIT"));
            assertThat(h.getCRC(),             is((short) 0xFA07));
        }
    }

    @Test
    public void updateDataSize() throws IOException {
        final int expected = 123456;

        try (final ByteArrayInputStream in = new ByteArrayInputStream(HEADER)) {
            final Header h = new Header();
            h.readHeader(in);

            h.setDataSize(expected);
            assertThat(h.getDataSize(), is(expected));
        }
    }

    @Test
    public void updateCRC() throws IOException {
        try (final ByteArrayInputStream in = new ByteArrayInputStream(HEADER)) {
            final Header h = new Header();
            h.readHeader(in);
            assertThat(h.getCRC(), is((short) 0x0000));

            h.updateCRC();
            assertThat(h.getCRC(), is((short) 0xFA07));
        }
    }

    @Test
    public void isValid() throws IOException {
        try (final ByteArrayInputStream in = new ByteArrayInputStream(HEADER)) {
            final Header h = new Header();
            h.readHeader(in);

            assertThat(h.isValid(), is(true));
        }
    }

    @Test
    public void writeHeader() throws IOException {
        final Header h = new Header();
        try (final ByteArrayInputStream in = new ByteArrayInputStream(HEADER_WITH_CRC)) {
            h.readHeader(in);
        }

        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            h.writeHeader(out);
            assertArrayEquals(HEADER_WITH_CRC, out.toByteArray());
        }
    }

}
