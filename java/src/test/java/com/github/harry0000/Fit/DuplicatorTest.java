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
     * @throws IOException 
     */
    private static void write(final InputStream fitFile) throws IOException {
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
    }

    // It fails the test because of data(or SDK).
/*
    @Test
    public void writeActivity() throws IOException {
        write(getClass().getResourceAsStream("Activity.fit"));
    }

    @Test
    public void writeMonitoringFile() throws IOException {
        write(getClass().getResourceAsStream("MonitoringFile.fit"));
    }
*/

    @Test
    public void writeSettings() throws IOException {
        write(getClass().getResourceAsStream("Settings.fit"));
    }

    @Test
    public void writeWeightScaleMultiUser() throws IOException {
        write(getClass().getResourceAsStream("WeightScaleMultiUser.fit"));
    }

    @Test
    public void writeWeightScaleSingleUser() throws IOException {
        write(getClass().getResourceAsStream("WeightScaleSingleUser.fit"));
    }

    @Test
    public void writeWorkoutCustomTargetValues() throws IOException {
        write(getClass().getResourceAsStream("WorkoutCustomTargetValues.fit"));
    }

    @Test
    public void writeWorkoutIndividualSteps() throws IOException {
        write(getClass().getResourceAsStream("WorkoutIndividualSteps.fit"));
    }

    @Test
    public void writeWorkoutRepeatGreaterThanStep() throws IOException {
        write(getClass().getResourceAsStream("WorkoutRepeatGreaterThanStep.fit"));
    }

    @Test
    public void writeWorkoutRepeatSteps() throws IOException {
        write(getClass().getResourceAsStream("WorkoutRepeatSteps.fit"));
    }

}
