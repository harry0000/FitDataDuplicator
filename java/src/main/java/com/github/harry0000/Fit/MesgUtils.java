package com.github.harry0000.Fit;

import java.io.IOException;
import java.io.OutputStream;

import com.garmin.fit.FieldDefinition;
import com.garmin.fit.Fit;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgDefinition;

class MesgUtils {

    private static class FieldRemovableMesg extends Mesg {

        /**
         * @param mesg
         */
        public FieldRemovableMesg(final Mesg mesg) {
            super(mesg);
        }

        /**
         * @param fromIndex
         */
        public void removeFieldRange(final int fromIndex) {
            removeFieldRange(fromIndex, fields.size());
        }

        /**
         * @param fromIndex
         * @param toIndex
         */
        private void removeFieldRange(final int fromIndex, final int toIndex) {
            fields.subList(fromIndex, toIndex).clear();
        }
    }

    /**
     * @param mesg
     * @param mesgDef
     * @return
     */
    private static Mesg removeExpansionFields(final Mesg mesg, final MesgDefinition mesgDef) {
        final int rawSize = mesgDef.getFields().size();
        final int size = mesg.getFields().size();
        if (size > rawSize) {
            final FieldRemovableMesg frm = new FieldRemovableMesg(mesg);
            frm.removeFieldRange(rawSize);
            return frm;
        }

        return mesg;
    }

    /**
     * @param out
     * @param mesgDef
     * @throws IOException 
     */
    public static void write(final OutputStream out, final MesgDefinition mesgDef) throws IOException {
        if (mesgDef.getArch() == Fit.ARCH_ENDIAN_BIG) {
            mesgDef.write(out);
            return;
        }

        // Fit.ARCH_ENDIAN_LITTLE
        out.write(Fit.HDR_TYPE_DEF_BIT | (mesgDef.getLocalNum() & Fit.HDR_TYPE_MASK)); // Message definition record header.
        out.write(0); // Reserved
        out.write(mesgDef.getArch());
        out.write(mesgDef.getNum());      // LSB
        out.write(mesgDef.getNum() >> 8); // MSB
        out.write(mesgDef.getFields().size());

        for (final FieldDefinition field : mesgDef.getFields()) {
            out.write(field.getNum());
            out.write(field.getSize());
            out.write(field.getType());
        }
    }

    /**
     * @param out
     * @param mesg
     * @param mesgDef
     * @throws IOException 
     */
    public static void write(final OutputStream out, final Mesg mesg, final MesgDefinition mesgDef) throws IOException {
        // Remove expansion fields.
        final Mesg m = removeExpansionFields(mesg, mesgDef);

        if (mesgDef.getArch() == Fit.ARCH_ENDIAN_BIG) {
            m.write(out, mesgDef);
            return;
        }

        // Fit.ARCH_ENDIAN_LITTLE
        out.write(m.getLocalNum() & Fit.HDR_TYPE_MASK); // Message record header.

        for (final FieldDefinition fieldDef : mesgDef.getFields()) {
            // Write using a wrapper class.
            final Field field = Field.getField(m, fieldDef);
            field.write(out, fieldDef);
        }
    }

    private MesgUtils() {}
}
