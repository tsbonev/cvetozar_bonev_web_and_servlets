package com.clouway.bankapp.adapter.mysql.util;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.clouway.bankapp.adapter.jdbc.ConnectionCreator;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

@SuppressWarnings("Duplicates")
public class TestConnectionCreator implements ConnectionCreator {


    private MysqlConnectionPoolDataSource dataSource;

    private final String url;
    private final String username;
    private final String password;

    public TestConnectionCreator(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public ConnectionPoolDataSource get() {
        if(this.dataSource == null){
            try {

                dataSource = new MysqlConnectionPoolDataSource();

                dataSource.setUrl(url);
                dataSource.setUser(username);
                dataSource.setPassword(password);

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return dataSource;
    }
}
