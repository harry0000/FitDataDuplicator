package com.github.harry0000;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.garmin.fit.Decode;
import com.github.harry0000.Fit.Duplicator;

public class FitDataDuplicator {

    private static final Logger logger = LoggerFactory.getLogger(FitDataDuplicator.class);
    private static final String DEST_FILE_SUFFIX = "_duplicated";

    /**
     * @param srcFile
     * @return
     */
    protected static String GetDestFilePath(final File srcFile) {
        final Path p = srcFile.toPath();
        final String name = p.getFileName().toString();
        final int idx = name.indexOf('.');
        if (idx >= 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append(p.getParent().toString())
              .append("\\")
              .append(name.substring(0, idx))
              .append(DEST_FILE_SUFFIX)
              .append(name.substring(idx));
            return sb.toString();
        }

        return srcFile.getPath() + DEST_FILE_SUFFIX;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        for (final String arg : args) {
            final File fitFile = new File(arg);
            if (!fitFile.exists()) {
                logger.warn("\"{}\" does not exist.", arg);
                continue;
            }

            try (final FileInputStream src = new FileInputStream(fitFile)) {
                if (!Decode.isFit(src)) {
                    logger.warn("\"{}\" is not FIT File.", arg);
                    continue;
                }
            } catch (final Exception e) {
                logger.error("Exception at \"" + arg + "\".", e);
                continue;
            }

            final boolean result = new Duplicator().duplicate(new File(GetDestFilePath(fitFile)), fitFile);
            logger.info("\"{}\" is duplicated. result: {}", arg, result);
        }
    }
}