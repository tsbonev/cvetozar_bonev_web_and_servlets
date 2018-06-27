package com.clouway.bankapp.adapter.web.login;

import com.clouway.bankapp.adapter.web.filter.SessionFilter;
import com.clouway.bankapp.core.Session;
import com.clouway.bankapp.core.SessionRepository;
import com.clouway.bankapp.core.User;
import com.clouway.bankapp.core.UserRepository;
import com.clouway.bankapp.adapter.web.helper.PageHandler;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    UserRepository userRepo;
    SessionRepository sessionRepo;
    PageHandler page;
    int maxAge = 6000000;

    public LoginServlet(PageHandler page, UserRepository userRepo, SessionRepository sessionRepository){
        this.page = page;
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepository;
    }

    /**
     * Gets the login jsp form and changes the title to login.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Session session = SessionFilter.session.get();

        if(session != null && session.isAuthenticated()){
            page.redirectTo("/home", resp, req,
                    "infoMessage", "User logged in from session!");
            return;
        }

        req.setAttribute("title", "Login");
        page.getPage("view/user/login.jsp", req, resp);

    }

    /**
     * Connects to the database, checks the data
     * from the sent login form and authenticates it.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        User user;

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if(!validateUserForm(username, password, req, resp)) return;

        user = new User(username, password);

        if(userIsInSystem(user, resp, req)){

            String SID = (String) req.getAttribute("SID");

            logUserIntoSession(user, SID);

            page.redirectTo("/home", resp, req,
                    "successMessage", "Successfully logged in!");
        }
    }

    /**
     * Sets up a session and registers
     * it in the database.
     *
     * @param user to log into session
     */
    private void logUserIntoSession(User user, String SID){

        Session session = setUpSession(user, SID);

        sessionRepo.registerSession(session);

    }

    /**
     * Creates a new session object.
     *
     * @param user to attach to session
     * @return created session
     */
    private Session setUpSession(User user, String SID){

        Session session = new Session(
                user.getId(),
                SID,
                sessionLife(),
                true
        );

        return session;
    }

    /**
     * Calculates the timestamp on which a session
     * should expire and returns it.
     *
     * @return the time at which a session should expire
     */
    private Timestamp sessionLife(){
        return Timestamp.valueOf(LocalDateTime.now().plusDays(1));
    }

    /**
     * Checks if the username is in the database
     * and if it is redirects to the login page with
     * an error.
     *
     * @param user to check
     * @param resp servlet response
     * @param req servlet request
     * @return result of the check
     * @throws IOException
     */
    private boolean userIsInSystem(User user, HttpServletResponse resp, HttpServletRequest req) throws IOException {

        if(!checkPassword(user)){
            page.redirectTo("/login", resp, req,
                    "errorMessage", "User not registered!");
            return false;
        }
        return true;
    }

    /**
     * Retrieves a user from the database and compares
     * his password with the password of a user from
     * the login form. If a user is found, sets
     * the passed user's id to the found user's id.
     *
     * @param user to be compared with
     * @return result of the check
     */
    private boolean checkPassword(User user){

        Optional<User> possibleUser = userRepo.getByUsername(user.getUsername());

        if(!possibleUser.isPresent()){
            return false;
        }

        User userPresent = possibleUser.get();

        if(userPresent.getPassword().equals(user.getPassword())){
            user.setId(userPresent.getId());
            return true;
        }

        return false;

    }

    /**
     * Checks if the form was sent empty
     * and redirects to an error page if it is.
     *
     * @param username to check
     * @param password to check
     * @param req servlet request
     * @param resp servlet request
     * @return result of the check
     * @throws IOException
     */
    private boolean validateUserForm(String username, String password,
                                     HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){

            page.redirectTo("/login", resp, req,
                    "errorMessage", "Something went wrong!");
            return false;

        }
        return true;
    }

}
