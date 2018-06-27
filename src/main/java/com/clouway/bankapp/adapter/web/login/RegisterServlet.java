package com.clouway.bankapp.adapter.web.login;

import com.clouway.bankapp.core.*;
import com.clouway.bankapp.adapter.web.helper.PageHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static Pattern usernamePattern = Pattern.compile("^[\\w]{5,15}$");
    private static Pattern passwordPattern = Pattern.compile("^[\\w]{8,20}$");
    private static double minAmount = 5;

    private static boolean matchToPattern(String string, Pattern pattern){

        Matcher matcher = pattern.matcher(string);
        return matcher.matches();

    }


    UserRepository userRepository;
    TransactionRepository transactionRepository;

    PageHandler page;

    public RegisterServlet(PageHandler page, UserRepository userRepository, TransactionRepository transactionRepository){
        this.page = page;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Gets the register jsp form and sets the title to Register.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("title", "Register");
        page.getPage("view/user/register.jsp", req, resp);
    }

    /**
     * Sets the connections for the repositories,
     * checks if the register form fields are valid,
     * checks if the username is not taken and
     * registers the user if all checks are passed.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        User user;
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if(!validateLoginCredentials(username, password, req, resp)) return;

        try{
            user = userRepository.registerIfNotExists(new UserRegistrationRequest(username, password));
            createInitialTransaction(user);
            page.redirectTo("/home", resp, req,
                    "successMessage", "User registered successfully!");

        }catch (UserAlreadyExistsException e){
            page.redirectTo("/register", resp, req,
                    "errorMessage", "Username taken!");
        }

    }

    /**
     * Validates the username and password
     * from the register form and
     * redirects to an error page if
     * validations fail.
     *
     * @param username to validate
     * @param password to validate
     * @param req servlet request
     * @param resp servlet response
     * @return validation result
     * @throws IOException
     */
    private boolean validateLoginCredentials(String username, String password,
                                             HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if(!matchToPattern(username, usernamePattern) || !matchToPattern(password, passwordPattern)){
            page.redirectTo("/register", resp, req,
                    "errorMessage", "Something went wrong!");
            return false;
        }
        return true;
    }

    /**
     * Creates an initial transaction for each registered
     * user with an amount.
     *
     * @param user
     */
    private void createInitialTransaction(User user){

        Transaction transaction = new Transaction(
                Operation.DEPOSIT,
                user.getId(),
                Date.valueOf(LocalDate.now()),
                minAmount
        );
        transactionRepository.save(transaction);

    }

}
