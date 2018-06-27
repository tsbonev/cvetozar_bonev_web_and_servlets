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

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {

    TransactionRepository transactionRepository;
    UserRepository userRepository;

    PageHandler page;
    int pageSize;


    public HistoryServlet(PageHandler page, TransactionRepository transactionRepository,
                          UserRepository userRepository, int pageSize){
        this.page = page;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.pageSize = pageSize;
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
     * Sets the connection for the repositories
     * and returns a jsp history page with
     * a paginated list of transactions for
     * the requested user.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setAttribute("title", "History");

        Session session = getSession();

        int currPage = getCurrentPage(req);

        int userId = session.getUserId();

        List<Transaction> transactions = transactionRepository.getUserTransactions(
                userId, currPage, pageSize);

        readPage(currPage, req, transactions);

        page.getPage("view/transaction/history.jsp", req, resp);

    }

    /**
     * Reads a page from transactions and
     * sets a servlet request attribute
     * to the received list.
     *
     * @param currPage to set the offset to
     * @param req servlet request
     * @param transactions the list of transactions
     */
    private void readPage(int currPage,
                          HttpServletRequest req, List<Transaction> transactions){


        boolean hasNextPage = transactions.size() > pageSize;

        req.setAttribute("hasNext", hasNextPage);
        req.setAttribute("currPage", currPage);

        if(hasNextPage){
            transactions.remove(transactions.size() - 1);
        }

        req.setAttribute("transactions", transactions);

    }

    /**
     * Returns the current page from a parameter,
     * if the parameter is invalid default to the first page.
     *
     * @param req servlet request
     * @return the current page
     */
    private int getCurrentPage(HttpServletRequest req){

        int currPage;

        String page = req.getParameter("page");

        try {
            currPage = (Integer.parseInt(page));
        }
        catch (NumberFormatException e){
            currPage = 1;
        }

        return currPage;

    }
}
