package com.clouway.bankapp.adapter.mysql;

import com.clouway.bankapp.adapter.jdbc.JdbcTemplate;
import com.clouway.bankapp.core.*;
import com.clouway.bankapp.adapter.jdbc.RowMapper;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class MySQLSessionRepository implements SessionRepository {

    private final JdbcTemplate jdbcTemplate;

    private Timestamp getRefreshedTime() {
        return Timestamp.valueOf(LocalDateTime.now().plusDays(1));
    }

    public MySQLSessionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Maps the rows from a result session
     * to a session object.
     */
    private RowMapper<Session> sessionRowMapper = rs -> new Session(
            rs.getInt("userId"),
            rs.getString("id"),
            rs.getTimestamp("expiresOn"),
            true
    );

    /**
     * Returns the amount of sessions
     * with a unique user id that have not
     * expired.
     *
     * @return count of unique user sessions
     */
    public int getActiveSessionsCount() {

        String sql = "SELECT COUNT(DISTINCT userId) as total FROM sessions" +
                " WHERE expiresOn > current_timestamp";

        int count = jdbcTemplate.executeQuery(sql,
                rs -> rs.getInt("total")).get(0);

        return count;

    }

    /**
     * Saves a session object into the database.
     *
     * @param session to registerSession
     */
    public void registerSession(Session session) {

        String sql = "INSERT INTO sessions(id, userId, expiresOn)" +
                " VALUES(?, ?, ?)";

        jdbcTemplate.execute(sql, session.getSessionId(),
                session.getUserId(), session.getExpiresOn());
    }

    /**
     * Saves a session object into the database.
     *
     * @param session to register
     */
    public void refreshSession(Session session) {

        String sql = "UPDATE sessions" +
                " SET expiresOn = ?" +
                " WHERE id = ?";

        jdbcTemplate.execute(sql, getRefreshedTime(),
                session.getSessionId());

    }

    /**
     * Removes a session from the database.
     *
     * @param sessionId
     */
    public void terminateSession(String sessionId) {
        String sql = "DELETE FROM sessions" +
                " WHERE id = ?";


        jdbcTemplate.execute(sql, sessionId);
    }

    /**
     * Deletes a session from the database.
     *
     * @param timestamp to delete session expiring after
     */
    @Override
    public void deleteSessionsExpiringAfter(Timestamp timestamp) {

        String sql = "DELETE FROM sessions" +
                " WHERE expiresOn < ?";

        jdbcTemplate.execute(sql, timestamp);

    }

    /**
     * Retrieves a session by a given session id.
     *
     * @param sessionId to search for
     * @param timestamp timestamp to compare to
     * @return found session
     */
    public Optional<Session> getSessionAvailableAt(String sessionId, Timestamp timestamp) {

        String sql = "SELECT * FROM sessions WHERE id = ? AND expiresOn > ?";

        return jdbcTemplate.executeQuery(sql, sessionRowMapper, sessionId, timestamp).stream().findFirst();

    }


}
