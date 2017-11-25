package com.github.projectrake.dbm.util.typebindings;

import org.jooq.Converter;

import java.util.UUID;

/**
 * Created on 25.11.2017.
 */
public class UUIDByteArrayConverter implements Converter<UUID, byte[]> {
    private final static ByteArrayUUIDConverter INVERSE = new ByteArrayUUIDConverter();

    @Override
    public byte[] from(UUID databaseObject) {
        return INVERSE.to(databaseObject);
    }

    @Override
    public UUID to(byte[] userObject) {
        return INVERSE.from(userObject);
    }

    @Override
    public Class<UUID> fromType() {
        return UUID.class;
    }

    @Override
    public Class<byte[]> toType() {
        return byte[].class;
    }

    @Override
    public Converter<byte[], UUID> inverse() {
        return INVERSE;
    }

}
