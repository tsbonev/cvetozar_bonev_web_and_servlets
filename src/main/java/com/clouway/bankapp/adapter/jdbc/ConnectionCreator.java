package com.clouway.bankapp.adapter.jdbc;

import javax.sql.ConnectionPoolDataSource;

public interface ConnectionCreator {

    ConnectionPoolDataSource get();

}
