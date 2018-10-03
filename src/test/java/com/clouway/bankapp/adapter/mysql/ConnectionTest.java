package com.clouway.bankapp.adapter.mysql;

import com.clouway.bankapp.adapter.jdbc.ConnectionCreator;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class ConnectionTest {

    @Test
    public void shouldConnect() throws SQLException {

        ConnectionCreator creator = new MySQLConnectionCreator(
                "jdbc:mysql://localhost:3306/banktest?useSSL=false&useLegacyDatetimeCode=false&serverTimezone=Europe/Sofia",
                "user",
                "password"
        );

        Connection conn = creator.get().getPooledConnection().getConnection();

        assertThat(conn, is(notNullValue()));

        conn.close();

    }

}
