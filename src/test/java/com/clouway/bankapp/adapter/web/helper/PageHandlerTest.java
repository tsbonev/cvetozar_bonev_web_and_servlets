package com.clouway.bankapp.adapter.web.helper;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class PageHandlerTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private RequestDispatcher rd;

    @Mock
    private HttpSession session;

    private ServletPageHandler page;

    @Before
    public void setUp(){
        page = new ServletPageHandler();
    }

    @Test
    public void pageForwards() throws ServletException, IOException {

        String pagePath = "some path";

        context.checking(new Expectations(){{

            oneOf(req).getRequestDispatcher(pagePath);
            will(returnValue(rd));

            oneOf(rd).forward(req, resp);

        }});

        page.getPage(pagePath, req, resp);

    }

    @Test
    public void pageRedirectsWithMessage() throws IOException {

        String pagePath = "some path";
        String messageType = "errorMessage";
        String messageContent = "This is an error!";

        context.checking(new Expectations(){{

            oneOf(req).getSession();
            will(returnValue(session));
            oneOf(session).setAttribute(messageType, messageContent);

            oneOf(resp).sendRedirect(pagePath);

        }});

        page.redirectTo(pagePath, resp, req,
                messageType, messageContent);

    }

}
