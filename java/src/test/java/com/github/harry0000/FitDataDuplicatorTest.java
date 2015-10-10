package com.github.harry0000;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class FitDataDuplicatorTest {

    @Test
    public void getDestFilePath_onlyFileName() {
        final File srcFile = new File("1990-01-01-00-00-00.fit");

        final String actual = FitDataDuplicator.GetDestFilePath(srcFile);

        assertThat(actual, is("1990-01-01-00-00-00_duplicated.fit"));
    }

    @Test
    public void getDestFilePath_rootPath() {
        final File srcFile = new File("\\1990-01-01-00-00-00.fit");

        final String actual = FitDataDuplicator.GetDestFilePath(srcFile);

        assertThat(actual, is("\\1990-01-01-00-00-00_duplicated.fit"));
    }

    @Test
    public void getDestFilePath_relativePath() {
        final File srcFile = new File("..\\1990-01-01-00-00-00.fit");

        final String actual = FitDataDuplicator.GetDestFilePath(srcFile);

        assertThat(actual, is("..\\1990-01-01-00-00-00_duplicated.fit"));
    }

    @Test
    public void getDestFilePath_fullPath() {
        final File srcFile = new File("C:\\Garmin\\Activities\\1990-01-01-00-00-00.fit");

        final String actual = FitDataDuplicator.GetDestFilePath(srcFile);

        assertThat(actual, is("C:\\Garmin\\Activities\\1990-01-01-00-00-00_duplicated.fit"));
    }

    @Test
    public void getDestFilePath_noExtensions() {
        final File srcFile = new File("C:\\Garmin\\Activities\\1990-01-01-00-00-00");

        final String actual = FitDataDuplicator.GetDestFilePath(srcFile);

        assertThat(actual, is("C:\\Garmin\\Activities\\1990-01-01-00-00-00_duplicated"));
    }

    @Test
    public void getDestFilePath_multipleExtensions() {
        final File srcFile = new File("C:\\Garmin\\Activities\\1990-01-01-00-00-00.fit.bkup");

        final String actual = FitDataDuplicator.GetDestFilePath(srcFile);

        assertThat(actual, is("C:\\Garmin\\Activities\\1990-01-01-00-00-00_duplicated.fit.bkup"));
    }

}
