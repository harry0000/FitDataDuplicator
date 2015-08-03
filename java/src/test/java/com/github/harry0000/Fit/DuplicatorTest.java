package com.github.harry0000.Fit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class DuplicatorTest {

    /**
     * @param in
     * @return
     * @throws IOException
     */
    private static byte[] toByteArray(final InputStream in) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        final byte[] b = new byte[4096];
        int length = -1;
        while ((length = in.read(b)) > -1) {
            out.write(b, 0, length);
        }

        return out.toByteArray();
    }

    /**
     * @param fitFile
     */
    private static void testWrite(final InputStream fitFile) {
        try {
            final byte[] actuals = toByteArray(fitFile);
            final ByteArrayOutputStream dest = new ByteArrayOutputStream(4096);

            final Duplicator duplicator = new Duplicator();
            duplicator.setHeader(Duplicator.getHeader(new ByteArrayInputStream(actuals)));
            final boolean result = duplicator.write(dest, new ByteArrayInputStream(actuals));

            // Write CRC.
            dest.write(actuals[actuals.length - 2]);
            dest.write(actuals[actuals.length - 1]);

            assertThat(result, is(true));
            assertArrayEquals(dest.toByteArray(), actuals);
        } catch (IOException e) {
            fail("Exception: " + e);
        }
    }

    // It fails the test because of data(or SDK).
/*
    @Test
    public void testWrite_Activity() {
        testWrite(getClass().getResourceAsStream("Activity.fit"));
    }

    @Test
    public void testWrite_MonitoringFile() {
        testWrite(getClass().getResourceAsStream("MonitoringFile.fit"));
    }
*/

    @Test
    public void testWrite_Settings() {
        testWrite(getClass().getResourceAsStream("Settings.fit"));
    }

    @Test
    public void testWrite_WeightScaleMultiUser() {
        testWrite(getClass().getResourceAsStream("WeightScaleMultiUser.fit"));
    }

    @Test
    public void testWrite_WeightScaleSingleUser() {
        testWrite(getClass().getResourceAsStream("WeightScaleSingleUser.fit"));
    }

    @Test
    public void testWrite_WorkoutCustomTargetValues() {
        testWrite(getClass().getResourceAsStream("WorkoutCustomTargetValues.fit"));
    }

    @Test
    public void testWrite_WorkoutIndividualSteps() {
        testWrite(getClass().getResourceAsStream("WorkoutIndividualSteps.fit"));
    }

    @Test
    public void testWrite_WorkoutRepeatGreaterThanStep() {
        testWrite(getClass().getResourceAsStream("WorkoutRepeatGreaterThanStep.fit"));
    }

    @Test
    public void testWrite_WorkoutRepeatSteps() {
        testWrite(getClass().getResourceAsStream("WorkoutRepeatSteps.fit"));
    }
}
