package com.clouway.bankapp.adapter.mysql;

import com.clouway.bankapp.adapter.jdbc.ConnectionProvider;
import com.clouway.bankapp.adapter.web.filter.ConnectionPerRequestFilter;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLConnectionProvider implements ConnectionProvider {

    /**
     * Returns the connection in the CPR filter.
     *
     * @return connection to a jdbc database
     */
    @Override
    public Connection get() throws SQLException {

        return ConnectionPerRequestFilter.get();

    }
}
