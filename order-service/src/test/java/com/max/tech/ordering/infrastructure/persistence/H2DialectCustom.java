package com.max.tech.ordering.infrastructure.persistence;

import org.hibernate.dialect.H2Dialect;

import java.sql.Types;

public class H2DialectCustom extends H2Dialect {

    public H2DialectCustom() {
        super();
        registerColumnType(Types.BIGINT, "varbinary");
        registerColumnType(Types.BINARY, "varbinary");
    }

}
