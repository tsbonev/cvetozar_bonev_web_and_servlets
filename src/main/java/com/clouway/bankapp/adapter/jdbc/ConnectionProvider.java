package com.clouway.bankapp.adapter.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionProvider {

    Connection get() throws SQLException;

}
