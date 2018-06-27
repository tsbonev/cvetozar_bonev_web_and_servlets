package com.clouway.bankapp.adapter.web.transaction;

import com.clouway.bankapp.adapter.web.filter.SessionFilter;
import com.clouway.bankapp.core.*;
import com.clouway.bankapp.adapter.web.helper.PageHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/account")
public class AccountServlet extends HttpServlet {

    PageHandler page;
    TransactionRepository transactionRepository;
    UserRepository userRepository;

    public AccountServlet(PageHandler page, TransactionRepository transactionRepository,
                          UserRepository userRepository){
        this.page = page;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
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
     * Sets the connection of the repositories,
     * gets the LoginSession and counts up the
     * balance of the session username or the
     * username given in the url.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setAttribute("title", "Account");

        User user = getUserContext();

        double balance = calculateBalance(user.getId());

        req.setAttribute("balance", balance);

        page.getPage("view/transaction/account.jsp", req, resp);

    }

    /**
     * Calculates the balance of a user's account.
     *
     * @param userId of the account
     * @return balance of the account
     */
    private double calculateBalance(int userId){

        double balance = 0;

        List<Transaction> list = transactionRepository.getUserTransactions(userId);

        for (Transaction transaction : list) {
            switch (transaction.getOperation()){
                case DEPOSIT:
                    balance += transaction.getAmount();
                    break;
                case WITHDRAW:
                    balance -= transaction.getAmount();
                    break;
                default:
                    break;
            }
        }

        return balance;

    }
}
