package com.clouway.bankapp.adapter.mysql.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestTable {

    /**
     * Creates the test tables.
     * @param conn
     */
    public void createTables(Connection conn){
        createUserTable(conn);
        createTransactionTable(conn);
        createSessionTable(conn);
    }

    /**
     * Cleans up the test tables.
     * @param conn
     */
    public void cleanTables(Connection conn){
        dropTransactionTable(conn);
        dropSessionTable(conn);
        dropUserTable(conn);
    }

    /**
     * Creates a transaction table in the database.
     */
    private static void createTransactionTable(Connection conn) {

        try {
            PreparedStatement create = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS transactions(" +
                            "id int NOT NULL AUTO_INCREMENT," +
                            "userId int NOT NULL," +
                            "operation varchar(255) NOT NULL," +
                            "amount double NOT NULL," +
                            "transactionDate date NOT NULL," +
                            "PRIMARY KEY(id)," +
                            "FOREIGN KEY(userId) REFERENCES users(id))"
            );
            create.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Creates a user table in the database.
     */
    private static void createUserTable(Connection conn) {

        try {
            PreparedStatement create = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS users(" +
                            "id int NOT NULL AUTO_INCREMENT," +
                            "username varchar(255) NOT NULL," +
                            "password varchar(255) NOT NULL," +
                            "PRIMARY KEY(id))"
            );
            create.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Creates a session table in the database.
     */
    private static void createSessionTable(Connection conn) {

        try {
            PreparedStatement create = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS sessions(" +
                            "id nvarchar(255) NOT NULL UNIQUE," +
                            "userId int NOT NULL," +
                            "expiresOn timestamp NOT NULL," +
                            "PRIMARY KEY(id))"
            );
            create.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Drops transaction table form the database.
     */
    private static void dropTransactionTable(Connection conn) {

        try {
            PreparedStatement drop = conn.prepareStatement(
                    "DROP TABLE transactions"
            );

            drop.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Drops the user table from the database.
     */
    private static void dropUserTable(Connection conn) {

        try {
            PreparedStatement drop = conn.prepareStatement(
                    "DROP TABLE users"
            );

            drop.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Drops the session table from the database.
     */
    private static void dropSessionTable(Connection conn) {

        try {
            PreparedStatement drop = conn.prepareStatement(
                    "DROP TABLE sessions"
            );

            drop.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
