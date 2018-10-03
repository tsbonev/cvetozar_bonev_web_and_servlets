package com.clouway.bankapp.adapter.web.transaction;

import com.clouway.bankapp.core.*;
import com.clouway.bankapp.adapter.web.helper.PageHandler;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionSystemTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    @Mock
    private UserRepository userRepo;

    @Mock
    private TransactionRepository transactionRepo;

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

    private TransactionServlet transactionServlet;
    private HistoryServlet historyServlet;
    private AccountServlet accountServlet;
    private Session loginSession;

    final User realUser = new User(1,"admin", "password");

    final Transaction realTransaction = new Transaction(Operation.WITHDRAW,
            1, Date.valueOf(LocalDate.now()), 200.0);



    @Before
    public void setUp() {

        loginSession = new Session(1, "123",
                Timestamp.valueOf(LocalDateTime.now()),
                true);

        transactionServlet = new TransactionServlet(page, userRepo, transactionRepo){
            @Override
            protected double getMaxAmount(){
                return 999;
            }

            @Override
            protected Session getSession(){
                return loginSession;
            }

            @Override
            protected User getUserContext() {return realUser;}

        };

        historyServlet = new HistoryServlet(page, transactionRepo, userRepo, 20){
            @Override
            protected Session getSession(){
                return loginSession;
            }
        };

        accountServlet = new AccountServlet(page, transactionRepo, userRepo){
            @Override
            protected Session getSession(){
                return loginSession;
            }

            @Override
            protected User getUserContext() {return realUser;}
        };

    }

    @Test
    public void accountBalanceShouldBeCalculated() throws ServletException, IOException {


        List<Transaction> transactions = new ArrayList<>();
        transactions.add(realTransaction);

        context.checking(new Expectations(){{

            oneOf(req).setAttribute("title", "Account");

            oneOf(transactionRepo).getUserTransactions(realUser.getId());
            will(returnValue(transactions));

            oneOf(req).setAttribute("balance", -200.0);

            oneOf(page).getPage("view/transaction/account.jsp", req, resp);

        }});

        accountServlet.doGet(req, resp);

    }

    @Test
    public void historyShouldShowGlobalScope() throws ServletException, IOException {

        List<Transaction> transactionList = new ArrayList<>();

        context.checking(new Expectations(){{

            oneOf(req).setAttribute("title", "History");

            oneOf(req).getParameter("page");
            will(returnValue("1"));

            oneOf(transactionRepo).getUserTransactions(1, 1, 20);
            will(returnValue(transactionList));

            oneOf(req).setAttribute("hasNext", false);
            oneOf(req).setAttribute("currPage", 1);

            oneOf(req).setAttribute("transactions", transactionList);

            oneOf(page).getPage("view/transaction/history.jsp", req, resp);


        }});

        historyServlet.doGet(req, resp);

    }

    @Test
    public void historyShouldShowForUser() throws ServletException, IOException {

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(realTransaction);

        context.checking(new Expectations(){{

            oneOf(req).setAttribute("title", "History");

            oneOf(req).getParameter("page");
            will(returnValue("1"));

            oneOf(transactionRepo).getUserTransactions(realUser.getId(), 1, 20);
            will(returnValue(transactionList));


            oneOf(req).setAttribute("hasNext", false);
            oneOf(req).setAttribute("currPage", 1);

            oneOf(req).setAttribute(with(any(String.class)), with(any(List.class)));

            oneOf(page).getPage("view/transaction/history.jsp", req, resp);


        }});

        historyServlet.doGet(req, resp);

    }

    @Test
    public void transactionForCurrentUserSuccessful() throws ServletException, IOException {

        context.checking(new Expectations(){{

            oneOf(req).getParameter("amount");
            will(returnValue("2.0"));
            oneOf(req).getParameter("action");
            will(returnValue("DEPOSIT"));

            oneOf(transactionRepo).save(with(any(Transaction.class)));

            oneOf(page).redirectTo("/account", resp, req,
                    "successMessage", "Transaction successful!");

        }});

        transactionServlet.doPost(req, resp);

    }

    @Test
    public void transactionFormatIsInvalid() throws IOException, ServletException {

        User user = new User();
        user.setId(1);
        user.setUsername("username");

        context.checking(new Expectations(){{

            oneOf(req).getParameter("amount");
            will(returnValue("2ld0"));

            oneOf(page).redirectTo("/account", resp, req,
                    "errorMessage", "Transaction amount format invalid!");

        }});

        transactionServlet.doPost(req, resp);

    }

    @Test
    public void transactionAmountIsNotPermitted() throws ServletException, IOException {

        context.checking(new Expectations(){{

            oneOf(req).getParameter("amount");
            will(returnValue("999999999.0"));
            oneOf(req).getParameter("action");
            will(returnValue("DEPOSIT"));

            oneOf(page).redirectTo("/account", resp, req,
                    "errorMessage", "Transactions of that size are not permitted!");

        }});

        transactionServlet.doPost(req, resp);

    }

}
