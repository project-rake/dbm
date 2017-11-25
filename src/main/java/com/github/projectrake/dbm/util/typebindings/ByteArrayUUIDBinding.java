package com.github.projectrake.dbm.util.typebindings;

import org.jooq.*;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Created on 25.11.2017.
 */
public class ByteArrayUUIDBinding implements Binding<byte[], UUID> {
    private static final Converter<byte[], UUID> UUID_CONVERTER = new ByteArrayUUIDConverter();

    @Override
    public Converter<byte[], UUID> converter() {
        return UUID_CONVERTER;
    }

    @Override
    public void sql(BindingSQLContext<UUID> ctx) throws SQLException {

    }

    @Override
    public void register(BindingRegisterContext<UUID> ctx) throws SQLException {

    }

    @Override
    public void set(BindingSetStatementContext<UUID> ctx) throws SQLException {

    }

    @Override
    public void set(BindingSetSQLOutputContext<UUID> ctx) throws SQLException {

    }

    @Override
    public void get(BindingGetResultSetContext<UUID> ctx) throws SQLException {

    }

    @Override
    public void get(BindingGetStatementContext<UUID> ctx) throws SQLException {

    }

    @Override
    public void get(BindingGetSQLInputContext<UUID> ctx) throws SQLException {

    }
}
