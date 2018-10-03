package server;

import com.clouway.bankapp.adapter.jdbc.ConnectionCreator;
import com.clouway.bankapp.adapter.jdbc.ConnectionProvider;
import com.clouway.bankapp.adapter.jdbc.JdbcTemplate;
import com.clouway.bankapp.adapter.mysql.*;
import com.clouway.bankapp.adapter.web.filter.util.SessionCleanupTask;
import com.clouway.bankapp.adapter.web.helper.PageHandler;
import com.clouway.bankapp.adapter.web.helper.ServletPageHandler;
import com.clouway.bankapp.core.SessionRepository;
import com.clouway.bankapp.core.TransactionRepository;
import com.clouway.bankapp.core.UserRepository;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class App {


    private static boolean testDatabaseConnection(ConnectionCreator creator) throws SQLException {

        boolean connectionUp = false;

        for(int i = 0; i < 10; i++){
            try{
                connectionUp = creator.get().getPooledConnection().getConnection().isValid(300);
            }catch (CommunicationsException e){
                e.printStackTrace();
            }
        }

        return connectionUp;

    }

    public static void main(String[] args) {


        Properties properties = new Properties();

        try {
            properties.load(Thread.currentThread().
                    getContextClassLoader().
                    getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String url = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");

        final ConnectionCreator creator = new MySQLConnectionCreator(url, username, password);
        final ConnectionProvider provider = new MySQLConnectionProvider();
        final JdbcTemplate jdbcTemplate = new MySQLTemplate(provider);
        final PageHandler page = new ServletPageHandler();
        final UserRepository userRepo = new MySQLUserRepository(jdbcTemplate);
        final TransactionRepository transactionRepo = new MySQLTransactionRepository(jdbcTemplate);
        final SessionRepository sessionRepo = new MySQLSessionRepository(jdbcTemplate);

        try {
            if(!testDatabaseConnection(creator)){

                System.err.println("No connection to the database could be established!");
                return;

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        SessionCleanupTask cleanupTask = new SessionCleanupTask(sessionRepo);
        cleanupTask.startAsync().awaitRunning();

        Jetty jetty = new Jetty(8080, creator,
                page,
                userRepo,
                transactionRepo,
                sessionRepo);
        jetty.start();

    }
}
