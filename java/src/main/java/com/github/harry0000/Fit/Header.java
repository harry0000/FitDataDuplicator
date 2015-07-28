package com.github.harry0000.Fit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.garmin.fit.CRC;
import com.garmin.fit.Fit;

public class Header {

    private static enum Format {
        HEADER_SIZE(0, 1),
        PROTOCOL_VERSION(1, 1),
        PROFILE_VERSION(2, 2),
        DATA_SIZE(4, 4),
        DATA_TYPE(8, 4),
        CRC(12, 2);

        private final int offset;
        private final int length;

        /**
         * @param offset
         * @param length
         */
        private Format(final int offset, final int length) {
            this.offset = offset;
            this.length = length;
        }

        /**
         * @return the offset
         */
        public int getOffset() {
            return offset;
        }

        /**
         * @return the length
         */
        public int getLength() {
            return length;
        }
    }

    private static final String FIT_DATA_TAYPE = ".FIT";
    private static final byte HEADER_WITH_CRC_SIZE = Fit.FILE_HDR_SIZE;
    private final byte[] header = new byte[Fit.FILE_HDR_SIZE];

    /**
     * 
     */
    public Header() {}

    /**
     * @param in
     * @throws IOException
     */
    public void readHeader(final InputStream in) throws IOException {
        final Format crc = Format.CRC;
        in.read(header, 0, HEADER_WITH_CRC_SIZE - crc.getLength());

        if (hasCRC()) {
            header[crc.getOffset()]     = (byte) in.read();
            header[crc.getOffset() + 1] = (byte) in.read();
        }
    }

    /**
     * @param out
     * @throws IOException
     */
    public void writeHeader(final OutputStream out) throws IOException {
        out.write(header, 0, (int)getHeaderSize());
    } 

    /**
     * @return
     */
    public byte[] getHeader() {
        return header;
    }

    /**
     * @return
     */
    public byte getHeaderSize() {
        return header[0];
    }

    /**
     * @return
     */
    public byte getProtocolVersion() {
        return header[1];
    }

    /**
     * @return
     */
    public short getProfileVersion() {
        return readShort(Format.PROFILE_VERSION);
    }

    /**
     * @return
     */
    public int getDataSize() {
        return readInt(Format.DATA_SIZE);
    }

    /**
     * @param dataSize
     */
    public void setDataSize(final int dataSize) {
        writeInt(dataSize, Format.DATA_SIZE);
    }

    /**
     * @return
     */
    public String getDataType() {
        final int offset = Format.DATA_TYPE.getOffset();
        final int length = Format.DATA_TYPE.getLength();

        final char[] type = new char[length];
        for (int i = 0; i < length; i++) {
            type[i] = (char) header[offset + i];
        }

        return String.valueOf(type);
    }

    /**
     * @return
     */
    public short getCRC() {
        if (hasCRC()) {
            return readShort(Format.CRC);
        }

        return 0x0000;
    }

    /**
     * 
     */
    public void updateCRC() {
        if (!hasCRC()) {
            return;
        }

        int crc = 0;
        for (int i = 0; i < HEADER_WITH_CRC_SIZE - Format.CRC.getLength(); i++) {
            crc = CRC.get16(crc, header[i]);
        }

        header[Format.CRC.getOffset()]     = (byte) (crc        & 0xFF);
        header[Format.CRC.getOffset() + 1] = (byte) ((crc >> 8) & 0xFF);
    }

    /**
     * 
     * @return
     */
    public boolean hasCRC() {
        return getHeaderSize() == HEADER_WITH_CRC_SIZE;
    }

    /**
     * @return
     */
    public boolean isValid() {
        return FIT_DATA_TAYPE.equals(getDataType());
    }

    /**
     * @param format
     * @return
     */
    private short readShort(final Format format) {
        assert format.getLength() >= 2;

        return ByteBuffer.wrap(header, format.getOffset(), format.getLength()).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    /**
     * @param format
     * @return
     */
    private int readInt(final Format format) {
        assert format.getLength() >= 4;

        return ByteBuffer.wrap(header, format.getOffset(), format.getLength()).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    /**
     * @param value
     * @param format
     */
    private void writeInt(final int value, final Format format) {
        assert format.getLength() >= 4;

        final int offset = format.getOffset();
        header[offset]     = (byte) (value       & 0xFF); // LSB
        header[offset + 1] = (byte) (value >> 8  & 0xFF);
        header[offset + 2] = (byte) (value >> 16 & 0xFF);
        header[offset + 3] = (byte) (value >> 24 & 0xFF); // MSB
    }

}
