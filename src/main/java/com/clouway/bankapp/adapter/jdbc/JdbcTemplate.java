package com.clouway.bankapp.adapter.jdbc;

import java.util.List;

public interface JdbcTemplate {

    void execute(String query, Object... arguments);

    <T> List<T> executeQuery(String query, RowMapper<T> rowMapper);

    <T> List<T> executeQuery(String query, RowMapper<T> rowMapper, Object... arguments);

}
