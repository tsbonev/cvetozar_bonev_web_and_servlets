package com.clouway.bankapp.adapter.mysql;

import com.clouway.bankapp.adapter.jdbc.JdbcTemplate;
import com.clouway.bankapp.core.User;
import com.clouway.bankapp.core.UserAlreadyExistsException;
import com.clouway.bankapp.core.UserRegistrationRequest;
import com.clouway.bankapp.core.UserRepository;
import com.clouway.bankapp.adapter.jdbc.RowMapper;

import java.util.*;

@SuppressWarnings("Duplicates")
public class MySQLUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public MySQLUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Maps the rows from a result session
     * to a user object.
     */
    private RowMapper<User> userRowMapper = rs -> new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password")
    );

    /**
     * Gets a user from the database by id.
     *
     * @param id of the user
     * @return found optional user
     */
    public Optional<User> getById(int id) {

        String sql = "SELECT * FROM users" +
                " WHERE id = ?";

        return jdbcTemplate.executeQuery(sql, userRowMapper, id).stream().findFirst();

    }

    /**
     * Returns all users from the database.
     *
     * @return list of all users
     */
    public List<User> getAll() {

        String sql = "SELECT * FROM users";

        return jdbcTemplate.executeQuery(sql, userRowMapper);

    }

    /**
     * Deletes a user by id.
     *
     * @param id of the user
     */
    public void deleteById(int id) {

        String sql = "DELETE FROM users" +
                " WHERE id = ?";

        jdbcTemplate.execute(sql, id);

    }

    /**
     * Updates a user in the database.
     *
     * @param user to be updated
     */
    public void update(User user) {

        String sql = "UPDATE users" +
                " SET username = ?," +
                "password = ?" +
                " WHERE id = ?";

        jdbcTemplate.execute(sql, user.getUsername(),
                user.getPassword(), user.getId());

    }

    /**
     * Gets a user by his username.
     *
     * @param username to search for
     * @return found optional user
     */
    public Optional<User> getByUsername(String username) {

        String sql = "SELECT * FROM users" +
                " WHERE username LIKE ?";

        return jdbcTemplate.executeQuery(sql, userRowMapper, username).stream().findFirst();

    }


    /**
     * Registers a user into the database if
     * the user does not already exist and
     * returns him
     *
     * @param registerRequest request for registration
     * @return registered user
     */
    public User registerIfNotExists(UserRegistrationRequest registerRequest) throws UserAlreadyExistsException {

        if(getByUsername(registerRequest.getUsername()).isPresent()){
            throw new UserAlreadyExistsException();
        }

        String sql = "INSERT INTO users(username, password)" +
                " VALUES(?, ?)";

        jdbcTemplate.execute(sql, registerRequest.getUsername(),
                registerRequest.getPassword());
        return getByUsername(registerRequest.getUsername()).get();

    }

    /**
     * Checks if a given user is in the database.
     *
     * @param user to check
     * @return whether or not the user exists in the given state
     */
    public boolean checkPassword(User user) {

        String sql = "SELECT * FROM users WHERE username LIKE ? AND password LIKE ?";

        return jdbcTemplate.executeQuery(sql, userRowMapper, user.getUsername(),
                user.getPassword()).stream().findFirst().isPresent();
    }
}