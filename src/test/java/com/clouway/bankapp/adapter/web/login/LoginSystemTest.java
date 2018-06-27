package com.clouway.bankapp.adapter.web.login;

import com.clouway.bankapp.core.*;
import com.clouway.bankapp.adapter.web.helper.PageHandler;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

@SuppressWarnings("Duplicates")
public class LoginSystemTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private HttpSession session;

    @Mock
    private PageHandler page;

    @Mock
    private Connection conn;

    @Mock
    private UserRepository userRepo;
    @Mock
    private SessionRepository sessionRepo;
    @Mock
    private TransactionRepository transactionRepo;

    private LoginServlet loginServlet;
    private RegisterServlet registerServlet;
    private LogoutServlet logoutServlet;
    private Session loginSession;

    @Before
    public void setUp() {

        loginServlet = new LoginServlet(page, userRepo, sessionRepo);

        registerServlet = new RegisterServlet(page, userRepo, transactionRepo);

        logoutServlet = new LogoutServlet(page, sessionRepo){
            @Override
            protected Session getSession(){
                return loginSession;
            }
        };

        loginSession = new Session(1, "123", Timestamp.valueOf(LocalDateTime.now()), true);

    }


    final User realUser = new User("admin", "password");
    Optional<User> optionalUser = Optional.of(realUser);
    final User fakeUser = new User("fake", "not a real pass");
    Optional<User> fakeOptionalUser = Optional.of(fakeUser);
    final Cookie sessionCookie = new Cookie("SID", "1234");

    @Test
    public void loginWithCorrectAccount() throws IOException {

        context.checking(new Expectations() {{

            oneOf(req).getParameter("username");
            will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password");
            will(returnValue(realUser.getPassword()));

            oneOf(req).getAttribute("SID");
            will(returnValue(sessionCookie.getValue()));

            oneOf(userRepo).getByUsername(realUser.getUsername());
            will(returnValue(optionalUser));

            oneOf(sessionRepo).registerSession(with(any(Session.class)));

            oneOf(page).redirectTo("/home", resp, req, "successMessage",
                    "Successfully logged in!");

        }});

        loginServlet.doPost(req, resp);

    }

    @Test
    public void loginWithEmptyForm() throws IOException {

        context.checking(new Expectations() {{

            oneOf(req).getParameter("username");
            will(returnValue(""));
            oneOf(req).getParameter("password");
            will(returnValue(null));

            oneOf(page).redirectTo("/login", resp, req,
                    "errorMessage", "Something went wrong!");

        }});

        loginServlet.doPost(req, resp);

    }

    @Test
    public void loginWithIncorrectAccount() throws IOException {

        context.checking(new Expectations() {{

            oneOf(req).getParameter("username");
            will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password");
            will(returnValue(realUser.getPassword()));

            oneOf(userRepo).getByUsername(realUser.getUsername());
            will(returnValue(fakeOptionalUser));

            oneOf(page).redirectTo("/login", resp, req,
                    "errorMessage", "User not registered!");

        }});

        loginServlet.doPost(req, resp);

    }

    @Test
    public void userLogsInAndThenOut() throws IOException {

        context.checking(new Expectations() {{

            oneOf(req).getParameter("username");
            will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password");
            will(returnValue(realUser.getPassword()));

            oneOf(req).getAttribute("SID");
            will(returnValue(sessionCookie.getValue()));

            oneOf(userRepo).getByUsername(realUser.getUsername());
            will(returnValue(optionalUser));

            oneOf(sessionRepo).registerSession(with(any(Session.class)));

            oneOf(page).redirectTo("/home", resp, req,
                    "successMessage", "Successfully logged in!");

            oneOf(sessionRepo).terminateSession(loginSession.getSessionId());


            oneOf(page).redirectTo("/home", resp, req,
                    "infoMessage", "User logged out!");


        }});

        loginServlet.doPost(req, resp);

        logoutServlet.doGet(req, resp);

    }

    @Test
    public void usernameIsTaken() throws ServletException, IOException, UserAlreadyExistsException {

        realUser.setId(1);

        context.checking(new Expectations() {{

            oneOf(req).getParameter("username");
            will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password");
            will(returnValue(realUser.getPassword()));

            oneOf(userRepo).registerIfNotExists(with(any(UserRegistrationRequest.class)));
            will(throwException(new UserAlreadyExistsException()));

            oneOf(page).redirectTo("/register", resp, req,
                    "errorMessage", "Username taken!");

        }});

        registerServlet.doPost(req, resp);

    }

    @Test
    public void registerUser() throws ServletException, IOException, UserAlreadyExistsException {

        realUser.setId(0);

        context.checking(new Expectations() {{

            oneOf(req).getParameter("username");
            will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password");
            will(returnValue(realUser.getPassword()));
            oneOf(userRepo).registerIfNotExists(with(any(UserRegistrationRequest.class)));
            will(returnValue(realUser));

            oneOf(transactionRepo).save(with(any(Transaction.class)));

            oneOf(page).redirectTo("/home", resp, req,
                    "successMessage", "User registered successfully!");

        }});

        registerServlet.doPost(req, resp);

    }

    @Test
    public void registerWithEmptyForm() throws ServletException, IOException {

        context.checking(new Expectations() {{

            oneOf(req).getParameter("username");
            will(returnValue(""));
            oneOf(req).getParameter("password");
            will(returnValue(null));

            oneOf(page).redirectTo("/register", resp, req,
                    "errorMessage", "Something went wrong!");

        }});

        registerServlet.doPost(req, resp);

    }


}
