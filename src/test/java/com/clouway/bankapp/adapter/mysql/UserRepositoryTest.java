package com.clouway.bankapp.adapter.mysql;

import com.clouway.bankapp.adapter.jdbc.JdbcTemplate;
import com.clouway.bankapp.adapter.mysql.util.TestTable;
import com.clouway.bankapp.core.User;
import com.clouway.bankapp.adapter.mysql.util.TestConnectionCreator;
import com.clouway.bankapp.adapter.jdbc.ConnectionCreator;
import com.clouway.bankapp.adapter.jdbc.ConnectionProvider;
import com.clouway.bankapp.core.UserAlreadyExistsException;
import com.clouway.bankapp.core.UserRegistrationRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class UserRepositoryTest {

    ConnectionCreator creator = new TestConnectionCreator(
            "jdbc:mysql://localhost:3306/banktest?useSSL=false&useLegacyDatetimeCode=false&serverTimezone=Europe/Sofia",
            "user",
            "password"
    );
    Connection connection;
    ConnectionProvider provider = new MySQLConnectionProvider(){
        @Override
        public Connection get(){
            return connection;
        }
    };
    JdbcTemplate jdbcTemplate = new MySQLTemplate(provider);
    MySQLUserRepository userRepository = new MySQLUserRepository(jdbcTemplate);
    TestTable testTable = new TestTable();

    @Before
    public void setUp() throws SQLException {

        connection = creator.get().getPooledConnection().getConnection();
        testTable.createTables(connection);

    }

    @After
    public void cleanRows() throws SQLException {
        testTable.cleanTables(connection);
        connection.close();
    }

    @Test
    public void getByUsername() throws UserAlreadyExistsException {

        User user = new User("admin", "admin");

        this.userRepository.registerIfNotExists(new UserRegistrationRequest(user.getUsername(),
                user.getPassword()));

        assertThat(this.userRepository.getByUsername("admin").get().getPassword(), is(user.getPassword()));

    }

    @Test
    public void saveUser() throws UserAlreadyExistsException {

        User user = new User("admin", "admin");

        this.userRepository.registerIfNotExists(new UserRegistrationRequest(user.getUsername(),
                user.getPassword()));

        assertThat(this.userRepository.getAll().get(0).getUsername(), is(user.getUsername()));

    }

    @Test
    public void updateUser() throws UserAlreadyExistsException {

        User user = new User("admin", "admin");

        this.userRepository.registerIfNotExists(new UserRegistrationRequest(user.getUsername(),
                user.getPassword()));

        User updatedUser = this.userRepository.getByUsername("admin").get();
        updatedUser.setUsername("new admin");

        this.userRepository.update(updatedUser);

        assertThat(this.userRepository.getAll().get(0).getUsername(), is(updatedUser.getUsername()));

    }

    @Test
    public void deleteUser() throws UserAlreadyExistsException {

        User user = new User("admin", "admin");

        this.userRepository.registerIfNotExists(new UserRegistrationRequest(user.getUsername(),
                user.getPassword()));
        this.userRepository.deleteById(this.userRepository.getAll().get(0).getId());

        assertThat(this.userRepository.getAll().size(), is(0));

    }

    @Test
    public void correctPassword() throws UserAlreadyExistsException {

        User user = new User("admin", "admin");

        this.userRepository.registerIfNotExists(new UserRegistrationRequest(user.getUsername(),
                user.getPassword()));

        assertThat(this.userRepository.checkPassword(user), is(true));

    }

    @Test
    public void incorrectPassword() throws UserAlreadyExistsException {

        User user = new User("admin", "admin");

        this.userRepository.registerIfNotExists(new UserRegistrationRequest(user.getUsername(),
                user.getPassword()));

        user.setPassword("not a correct password");

        assertThat(this.userRepository.checkPassword(user), is(false));

    }

}
