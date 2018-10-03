package com.clouway.bankapp.adapter.mysql;

import com.clouway.bankapp.adapter.jdbc.JdbcTemplate;
import com.clouway.bankapp.core.Operation;
import com.clouway.bankapp.core.Transaction;
import com.clouway.bankapp.core.TransactionRepository;
import com.clouway.bankapp.adapter.jdbc.RowMapper;

import java.util.*;

@SuppressWarnings("Duplicates")
public class MySQLTransactionRepository implements TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    public MySQLTransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Maps a result set iteration into a transaction object.
     */
    private RowMapper<Transaction> transactionRowMapper = rs -> new Transaction(
            rs.getInt("id"),
            Operation.valueOf(rs.getString("operation")),
            rs.getString("username"),
            rs.getDate("transactionDate"),
            rs.getDouble("amount")
            );

    /**
     * Saves a transaction in the database.
     *
     * @param transaction to registerSession
     */
    public void save(Transaction transaction) {

        String sql = "INSERT INTO transactions(amount, userId, transactionDate, operation)" +
                " VALUES(?, ?, ?, ?)";

        jdbcTemplate.execute(sql, transaction.getAmount(),
                transaction.getUserId(), transaction.getDate(),
                transaction.getOperation().name());
    }

    /**
     * Gets all the transactions of a user paginated
     * and joins the username associated with the userID.
     *
     * @param id   of the user
     * @param page to return
     * @return transactions in a given page
     */
    public List<Transaction> getUserTransactions(int id, int page, int pageSize) {

        String sql = "SELECT t.id, u.username, t.operation, t.amount, t.transactionDate " +
                "FROM transactions t " +
                "JOIN users u ON t.userId = u.id" +
                " WHERE userId = ? LIMIT ? OFFSET ?";

        return jdbcTemplate.executeQuery(sql, transactionRowMapper, id,
                pageSize + 1, ((page - 1) * pageSize));
    }

    /**
     * Gets all the transactions of a user non-paginated
     * and joins the username associated with the userID.
     *
     * @param id   of the user
     * @return user's transactions
     */
    public List<Transaction> getUserTransactions(int id) {

        String sql = "SELECT t.id, u.username, t.operation, t.amount, t.transactionDate " +
                "FROM transactions t " +
                "JOIN users u ON t.userId = u.id" +
                " WHERE userId = ?";

        return jdbcTemplate.executeQuery(sql, transactionRowMapper, id);
    }

}