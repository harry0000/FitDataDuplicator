package com.github.harry0000.Fit;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.garmin.fit.Factory;
import com.garmin.fit.FieldDefinition;
import com.garmin.fit.Fit;
import com.garmin.fit.Mesg;

class Field extends com.garmin.fit.Field {

    /** Invalid data values. (little endian) */
    private static final Map<Integer, byte[]> INVALID_VALUES;
    static {
        final Map<Integer, byte[]> m = new HashMap<>();
        m.put(Fit.BASE_TYPE_ENUM,    new byte[]{ (byte) 0xFF });
        m.put(Fit.BASE_TYPE_SINT8,   new byte[]{ (byte) 0x7F });
        m.put(Fit.BASE_TYPE_UINT8,   new byte[]{ (byte) 0xFF });
        m.put(Fit.BASE_TYPE_SINT16,  new byte[]{ (byte) 0xFF, (byte) 0x7F });
        m.put(Fit.BASE_TYPE_UINT16,  new byte[]{ (byte) 0xFF, (byte) 0xFF });
        m.put(Fit.BASE_TYPE_SINT32,  new byte[]{ (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x7F });
        m.put(Fit.BASE_TYPE_UINT32,  new byte[]{ (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF });
        m.put(Fit.BASE_TYPE_STRING,  new byte[]{ 0x00 });
        m.put(Fit.BASE_TYPE_FLOAT32, new byte[]{ (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF });
        m.put(Fit.BASE_TYPE_FLOAT64, new byte[]{ (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF });
        m.put(Fit.BASE_TYPE_UINT8Z,  new byte[]{ 0x00 });
        m.put(Fit.BASE_TYPE_UINT16Z, new byte[]{ 0x00, 0x00 });
        m.put(Fit.BASE_TYPE_UINT32Z, new byte[]{ 0x00, 0x00, 0x00, 0x00 });
        m.put(Fit.BASE_TYPE_BYTE,    new byte[]{ (byte) 0xFF });
        INVALID_VALUES = Collections.unmodifiableMap(m);
    }

    /* (non-Javadoc)
     * @see com.garmin.fit.Field#write(java.io.OutputStream, com.garmin.fit.FieldDefinition)
     */
    @Override
    protected void write(final OutputStream out, final FieldDefinition fieldDef) {
        int bytesLeft = fieldDef.getSize() - getSize();

        write(out);

        // Fill remaining bytes with invalid.
        while (bytesLeft > 0) {
            writeValue(out, null);
            bytesLeft -= Fit.baseTypeSizes[type & Fit.BASE_TYPE_NUM_MASK];
        }
    }

    /* (non-Javadoc)
     * @see com.garmin.fit.Field#write(java.io.OutputStream)
     */
    @Override
    protected void write(final OutputStream out) {
        for (final Object value : values) {
            writeValue(out, value);
        }
    }

    /**
     * @param out
     * @param value
     * @see com.garmin.fit.Field#writeValue(java.io.OutputStream, java.lang.Object)
     */
    private void writeValue(final OutputStream out, final Object value) {
        try {
            if (value == null) {
                final byte[] invalidValue = INVALID_VALUES.get(type);
                if (invalidValue != null) {
                    out.write(invalidValue);
                }
            } else { // if (value != null)
               switch (type) {
                  case Fit.BASE_TYPE_ENUM:
                  case Fit.BASE_TYPE_UINT8:
                  case Fit.BASE_TYPE_UINT8Z:
                  case Fit.BASE_TYPE_SINT8:
                  case Fit.BASE_TYPE_BYTE: {
                      if (value instanceof String) {
                          System.err.printf("Field.write(): Field %s value should not be string value %s\n", name, value);
                      }
                      final ByteBuffer bb = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN);
                      out.write(bb.put((byte)Math.round(((Number) value).doubleValue())).array());
                      break;
                  }
                  case Fit.BASE_TYPE_SINT16:
                  case Fit.BASE_TYPE_UINT16:
                  case Fit.BASE_TYPE_UINT16Z: {
                      final ByteBuffer bb = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
                      out.write(bb.putShort((short)Math.round(((Number) value).doubleValue())).array());
                      break;
                  }
                  case Fit.BASE_TYPE_SINT32:
                  case Fit.BASE_TYPE_UINT32:
                  case Fit.BASE_TYPE_UINT32Z: {
                      final ByteBuffer bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
                      final byte[] b = bb.putInt((int)Math.round(((Number) value).doubleValue())).array();
                      out.write(b);
                      break;
                  }
                  case Fit.BASE_TYPE_STRING: {
                      final OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
                      writer.write(value.toString());
                      writer.flush();
                      out.write(0);
                      break;
                  }
                  case Fit.BASE_TYPE_FLOAT32: {
                      final ByteBuffer bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
                      out.write(bb.putFloat(((Number) value).floatValue()).array());
                      break;
                  }
                  case Fit.BASE_TYPE_FLOAT64: {
                      final ByteBuffer bb = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
                      out.write(bb.putDouble(((Number) value).doubleValue()).array());
                      break;
                  }
                  default:
                     break;
               }
            } // if (value != null)
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param mesg
     * @param fieldDef
     * @return
     */
    public static Field getField(final Mesg mesg, final FieldDefinition fieldDef) {
        final com.garmin.fit.Field field = mesg.getField(fieldDef.getNum());
        if (field != null) {
            return new Field(field);
        }

        final com.garmin.fit.Field created = Factory.createField(mesg.getNum(), fieldDef.getNum());
        if (!"unknown".equals(created.getName())) {
            return new Field(created);
        }

        return new Field(created, fieldDef);
    }

    /**
     * @param field
     */
    private Field(final com.garmin.fit.Field field) {
        super(field);
    }

    /**
     * @param field
     * @param fieldDef
     */
    private Field(final com.garmin.fit.Field field, final FieldDefinition fieldDef) {
        super(field);

        this.type = fieldDef.getType();
    }
}
