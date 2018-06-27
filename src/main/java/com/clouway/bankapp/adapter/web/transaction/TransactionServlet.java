package com.clouway.bankapp.adapter.web.transaction;

import com.clouway.bankapp.adapter.web.filter.SessionFilter;
import com.clouway.bankapp.core.*;
import com.clouway.bankapp.adapter.web.helper.PageHandler;
import org.apache.commons.lang3.StringUtils;

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

@WebServlet("/transaction")
public class TransactionServlet extends HttpServlet {

    private static Pattern amountPattern = Pattern.compile(
            "^([1-9]{1}[0-9]{0,9}[.,]{1}[0-9]{1,5})|" +
                    "([1-9]{1}[0-9]{0,9})|" +
                    "([0]{1}[.,]{1}[0-9]{0,4}[1-9]{1})|" +
                    "([0]{1}[.,]{1}[1-9]{0,4}[1-9]{1})$"
    );

    private static boolean matchToPattern(String string, Pattern pattern){

        Matcher matcher = pattern.matcher(string);
        return matcher.matches();

    }

    UserRepository userRepository;
    TransactionRepository transactionRepository;

    PageHandler page;

    public TransactionServlet(PageHandler page,
                              UserRepository userRepository,
                              TransactionRepository transactionRepository){
        this.page = page;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    protected double getMaxAmount(){
        return Double.MAX_VALUE / 8;
    }

    /**
     * Retrieves the session from the ThreadLocal.
     *
     * @return retrieved session
     */
    protected Session getSession(){
        return SessionFilter.session.get();
    }

    /**
     * Retrieves the session from the ThreadLocal.
     *
     * @return retrieved user
     */
    protected User getUserContext(){
        return SessionFilter.userContext.get();
    }

    /**
     * Gets a transaction form and
     * sets the title to Transaction.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setAttribute("title", "Transaction");

        String action = req.getParameter("action");
        action = StringUtils.capitalize(action);
        req.setAttribute("action", action);

        page.getPage("view/transaction/doTransaction.jsp", req, resp);

    }

    /**
     * Sets the connection of the repositories,
     * gets the session, validates the transaction amount,
     * checks if the request was sent by a valid user
     * to a valid account and saves the transaction
     * if all checks are passed.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String amountString = req.getParameter("amount");

        if(!validateAmountFormat(amountString, req, resp)) return;

        double amount = parseAmount(amountString);
        Operation operation = getOperation(req);

        if(!validateAmountSize(amount, req, resp)) return;

        User user = getUserContext();

        Transaction transaction = createTransaction(amount, operation, user);

        transactionRepository.save(transaction);

        page.redirectTo("/account", resp, req,
                "successMessage", "Transaction successful!");
    }

    /**
     * Creates a transaction from a given amount, operation
     * and username.
     *
     * @param amount for the transaction
     * @param operation of the transaction
     * @return the built transaction
     */
    private Transaction createTransaction(double amount, Operation operation, User user){

        Transaction transaction = new Transaction(
                operation,
                user.getId(),
                Date.valueOf(LocalDate.now()),
                amount
        );
        return transaction;
    }

    /**
     * Validates whether the string amount passed
     * is in the correct format.
     *
     * @param amount passed as string
     * @param req servlet request
     * @param resp servlet response
     * @return result of the validation
     * @throws IOException
     */
    private boolean validateAmountFormat(String amount,
                                         HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(!matchToPattern(amount, amountPattern)){
            page.redirectTo("/account", resp, req,
                    "errorMessage", "Transaction amount format invalid!");
            return false;
        }
        return true;
    }

    /**
     * Validates whether the double amount exceeds
     * the max amount permitted.
     *
     * @param amount to check
     * @param req servlet request
     * @param resp servlet response
     * @return the result of the check
     * @throws IOException
     */
    private boolean validateAmountSize(double amount, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(amount > getMaxAmount()
                || amount <= 0){
            page.redirectTo("/account", resp, req,
                    "errorMessage", "Transactions of that size are not permitted!");
            return false;
        }
        return true;
    }

    /**
     * Parses a string into a double.
     *
     * @param amount string to be parsed
     * @return parsed double
     */
    private double parseAmount(String amount){
        return Double.parseDouble(amount.replace(",", "."));
    }

    /**
     * Parses a string to an operation.
     *
     * @param req servlet request
     * @return parsed operation
     */
    private Operation getOperation(HttpServletRequest req){
        return Operation.valueOf(req.getParameter("action").toUpperCase());
    }

}
