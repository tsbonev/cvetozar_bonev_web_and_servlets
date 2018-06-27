package com.clouway.bankapp.adapter.mysql;

import com.clouway.bankapp.adapter.jdbc.JdbcTemplate;
import com.clouway.bankapp.adapter.mysql.util.TestTable;
import com.clouway.bankapp.core.Session;
import com.clouway.bankapp.core.SessionRepository;
import com.clouway.bankapp.adapter.jdbc.ConnectionCreator;
import com.clouway.bankapp.adapter.jdbc.ConnectionProvider;
import com.clouway.bankapp.adapter.mysql.util.TestConnectionCreator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class SessionRepositoryTest {

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
    SessionRepository sessionRepo  = new MySQLSessionRepository(jdbcTemplate);
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

    final Session session = new Session(1, "123",
            Timestamp.valueOf(LocalDateTime.now().plusHours(1)), true);

    @Test
    public void registerSession(){

        sessionRepo.registerSession(session);

        assertThat(sessionRepo.getSessionAvailableAt(session.getSessionId()
        , Timestamp.valueOf(LocalDateTime.now())).isPresent(), is(true));

    }

    @Test
    public void removeStaleSession(){

        session.setExpiresOn(Timestamp.valueOf(LocalDateTime.now().minusDays(2)));

        sessionRepo.registerSession(session);

        sessionRepo.deleteSessionsExpiringAfter(Timestamp.valueOf(LocalDateTime.now()));

        assertThat(sessionRepo.getSessionAvailableAt(session.getSessionId(),
                Timestamp.valueOf(LocalDateTime.now())).isPresent(), is(false));

    }

    @Test
    public void shouldCountUniqueActiveSessions(){

        Timestamp hourInTheFuture = Timestamp.valueOf(LocalDateTime.now().plusHours(1));
        Timestamp hourInThePast = Timestamp.valueOf(LocalDateTime.now().minusHours(1));

        Session userOneFirstSession = new Session(1, "123", hourInTheFuture, true);
        Session userOneSecondSession = new Session(1, "234", hourInTheFuture, true);
        Session userTwoSession = new Session(2, "345", hourInTheFuture, true);
        Session staleSession = new Session(3, "456", hourInThePast, true);

        sessionRepo.registerSession(userOneFirstSession);
        sessionRepo.registerSession(userOneSecondSession);
        sessionRepo.registerSession(userTwoSession);
        sessionRepo.registerSession(staleSession);

        assertThat(sessionRepo.getActiveSessionsCount(), is(2));

    }

}
