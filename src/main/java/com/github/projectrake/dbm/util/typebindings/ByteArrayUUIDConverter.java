package com.github.projectrake.dbm.util.typebindings;

import org.jooq.Converter;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created on 25.11.2017.
 */
public class ByteArrayUUIDConverter implements Converter<byte[], UUID> {
    private final static UUIDByteArrayConverter INVERSE = new UUIDByteArrayConverter();

    @Override
    public UUID from(byte[] databaseObject) {
        ByteBuffer buf = ByteBuffer.wrap(databaseObject);

        return new UUID(buf.getLong(), buf.getLong());
    }

    @Override
    public byte[] to(UUID userObject) {
        byte[] uuidbytes = new byte[16];
        ByteBuffer buf = ByteBuffer.wrap(uuidbytes);

        buf.putLong(userObject.getMostSignificantBits());
        buf.putLong(userObject.getLeastSignificantBits());

        return uuidbytes;
    }

    @Override
    public Class<byte[]> fromType() {
        return byte[].class;
    }

    @Override
    public Class<UUID> toType() {
        return UUID.class;
    }

    @Override
    public Converter<UUID, byte[]> inverse() {
        return INVERSE;
    }
}
