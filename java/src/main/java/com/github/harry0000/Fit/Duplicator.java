package com.github.harry0000.Fit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.garmin.fit.CRC;
import com.garmin.fit.Decode;
import com.garmin.fit.Fit;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgDefinition;
import com.garmin.fit.MesgDefinitionListener;
import com.garmin.fit.MesgListener;

public class Duplicator implements MesgDefinitionListener, MesgListener {

    private final Logger logger = LoggerFactory.getLogger(Duplicator.class);
    private final MesgDefinition[] mesgDefs = new MesgDefinition[Fit.MAX_LOCAL_MESGS];

    private Header header;
    private ByteArrayOutputStream messages;

    /**
     * @param src
     * @return
     * @throws IOException
     */
    protected static Header getHeader(final InputStream src) throws IOException {
        final Header header = new Header();
        header.readHeader(src);
        return header;
    }

    /**
     * @param destFile
     * @param srcFile
     * @return
     */
    public boolean duplicate(final File destFile, final File srcFile) {
        try (final FileInputStream src = new FileInputStream(srcFile)) {
            if (!Decode.checkIntegrity(src)) {
                logger.warn("FIT file integrity failed.");
                return false;
            }
        } catch (final Exception e) {
            logger.error("Failed to check FIT file integrity.", e);
            return false;
        }

        // Copy header.
        try (final FileInputStream src = new FileInputStream(srcFile)) {
            header = getHeader(src);
        } catch (final Exception e) {
            logger.error("Failed to read header.", e);
            return false;
        }

        // Write header and mesg.
        boolean result = false;
        try (final FileInputStream src = new FileInputStream(srcFile);
             final FileOutputStream dest = new FileOutputStream(destFile)) {

            result = write(dest, src);

        } catch (final Exception e) {
            logger.error("Failed to copy FIT data.", e);
            return false;
        }

        // Write CRC.
        try (final RandomAccessFile dest = new RandomAccessFile(destFile, "rw")) {
            int crc = 0;
            int data;
            while ((data = dest.read()) > -1) {
                crc = CRC.get16(crc, (byte) data);
            }
            dest.write((int) (crc & 0xFF));
            dest.write((int) ((crc >> 8) & 0xFF));
        } catch (final Exception e) {
            logger.error("Failed to write CRC.", e);
            return false;
        }

        return result;
    }

    /**
     * @param dest
     * @param src
     * @return
     * @throws IOException
     */
    protected boolean write(final OutputStream dest, final InputStream src) throws IOException {
        messages = new ByteArrayOutputStream(header.getDataSize());

        // Read messages.
        final Decode decode = new Decode();
        decode.addListener((MesgDefinitionListener)this);
        decode.addListener((MesgListener)this);

        final boolean result = decode.read(src);

        // Update header. (data size)
        header.setDataSize(messages.size());

        // Write header and mesg.
        header.writeHeader(dest);
        messages.writeTo(dest);

        messages = null;

        return result;
    }

    /* (non-Javadoc)
     * @see com.garmin.fit.MesgDefinitionListener#onMesgDefinition(com.garmin.fit.MesgDefinition)
     */
    @Override
    public void onMesgDefinition(final MesgDefinition mesgDef) {
        if (messages == null) {
            return;
        }

        // Store MesgDefinition.
        mesgDefs[mesgDef.getLocalNum()] = mesgDef;

        try {
            MesgUtils.write(messages, mesgDef);
        } catch (final IOException e) {
            logger.error("Failed to write MesgDefinition.", e);
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.garmin.fit.MesgListener#onMesg(com.garmin.fit.Mesg)
     */
    @Override
    public void onMesg(final Mesg mesg) {
        if (messages == null) {
            return;
        }

        final MesgDefinition mesgDef = mesgDefs[mesg.getLocalNum()];

        try {
            MesgUtils.write(messages, mesg, mesgDef);
        } catch (final IOException e) {
            logger.error("Failed to write Mesg.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the header
     */
    public Header getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(Header header) {
        this.header = header;
    }

}