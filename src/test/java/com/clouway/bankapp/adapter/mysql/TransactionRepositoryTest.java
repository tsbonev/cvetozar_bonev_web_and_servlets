package com.clouway.bankapp.adapter.mysql;

import com.clouway.bankapp.adapter.jdbc.JdbcTemplate;
import com.clouway.bankapp.adapter.mysql.util.TestTable;
import com.clouway.bankapp.core.*;
import com.clouway.bankapp.adapter.mysql.util.TestConnectionCreator;
import com.clouway.bankapp.adapter.jdbc.ConnectionCreator;
import com.clouway.bankapp.adapter.jdbc.ConnectionProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TransactionRepositoryTest {

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
    MySQLTransactionRepository transactionRepository = new MySQLTransactionRepository(jdbcTemplate);
    TestTable testTable = new TestTable();

    final User user = new User("admin", "admin");
    Transaction transaction;


    @Before
    public void setUp() throws UserAlreadyExistsException, SQLException {

        connection = creator.get().getPooledConnection().getConnection();

        testTable.createTables(connection);

        userRepository.registerIfNotExists(new UserRegistrationRequest(user.getUsername(), user.getPassword()));
        user.setId(userRepository.getAll().get(0).getId());

        transaction = new Transaction();
        transaction.setAmount(200.0d);
        transaction.setOperation(Operation.DEPOSIT);
        java.util.Date utilDate = Date.from(Instant.now());
        java.sql.Date sqlDate = new Date(utilDate.getTime());
        transaction.setDate(sqlDate);
        transaction.setUserId(user.getId());
    }

    @After
    public void cleanUp() throws SQLException {
        testTable.cleanTables(connection);
        connection.close();
    }

    @Test
    public void addTransaction() {

        transactionRepository.save(transaction);

        assertThat(transactionRepository.getUserTransactions(1, 1, 10)
                .get(0).getUsername(), is(user.getUsername()));

    }

    @Test
    public void getTransactionByUserId(){

        transactionRepository.save(transaction);

        List<Transaction> dbTransactions = transactionRepository.
                getUserTransactions(transaction.getUserId(), 1, 20);

        assertThat(dbTransactions.size(), is(1));
        assertThat(dbTransactions.get(0).getAmount(), is(transaction.getAmount()));

    }


    @Test
    public void paginateTransactions(){

        int pageSize = 10;

        for(int i = 0; i < 44; i++){
            transaction.setAmount(i);
            transactionRepository.save(transaction);
        }

        List<Transaction> transactions = transactionRepository.getUserTransactions(1,
                1, pageSize);

        assertThat(transactions.size(), is(pageSize + 1));

    }

}
