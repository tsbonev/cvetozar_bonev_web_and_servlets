package com.clouway.bankapp.adapter.mysql;

import com.clouway.bankapp.adapter.jdbc.ConnectionCreator;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

public class MySQLConnectionCreator implements ConnectionCreator {


    private MysqlConnectionPoolDataSource dataSource;

    private final String url;
    private final String username;
    private final String password;

    public MySQLConnectionCreator(String url, String username, String password){
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Returns a jdbc datasource.
     *
     * @return a connection
     */
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
