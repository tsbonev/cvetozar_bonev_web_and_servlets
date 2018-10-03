package com.clouway.bankapp.adapter.mysql;

import com.clouway.bankapp.adapter.jdbc.ConnectionProvider;
import com.clouway.bankapp.adapter.jdbc.JdbcTemplate;
import com.clouway.bankapp.adapter.jdbc.RowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLTemplate implements JdbcTemplate {

    private final ConnectionProvider connectionProvider;

    public MySQLTemplate (ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    private PreparedStatement statement = null;
    private Connection conn = null;
    private boolean calledWithArguments = false;

    @Override
    public void execute(String query, Object... arguments) {

        PreparedStatement statement = null;

        try {
            Connection conn = connectionProvider.get();
            statement = conn.prepareStatement(query);

            int index = 1;

            for (Object entry : arguments) {
                statement.setObject(index, entry);
                index++;
            }

            statement.execute();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(statement != null){
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public <T> List<T> executeQuery(String query, RowMapper<T> rowMapper) {
        ResultSet rs = null;
        List<T> result = new ArrayList<>();

        try {

            if(!calledWithArguments){
                conn = connectionProvider.get();
                statement = conn.prepareStatement(query);
            }

            calledWithArguments = false;

            rs = statement.executeQuery();

            while (rs.next()) {
                T row = rowMapper.map(rs);
                result.add(row);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    public <T> List<T> executeQuery(String query, RowMapper<T> rowMapper, Object... arguments) {
        calledWithArguments = true;

        try {
            conn = connectionProvider.get();
            statement = conn.prepareStatement(query);

            int index = 1;

            for (Object entry : arguments) {
                statement.setObject(index++, entry);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return executeQuery(query, rowMapper);
    }

}
