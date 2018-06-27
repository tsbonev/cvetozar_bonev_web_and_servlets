package com.clouway.bankapp.adapter.web;

import com.clouway.bankapp.adapter.web.filter.ErrorFilter;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class ErrorFilterTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    @Mock
    HttpServletRequest req;

    @Mock
    HttpServletResponse resp;

    @Mock
    RequestDispatcher rd;

    @Mock
    FilterChain chain;


    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setErr(System.err);
    }

    @Test
    public void exceptionShouldBeRedirectedToErrorHandler() throws ServletException, IOException {

        context.checking(new Expectations(){{

            oneOf(chain).doFilter(req, resp);
            will(throwException(new ServletException()));

            oneOf(req).getRequestDispatcher("/error");
            will(returnValue(rd));
            oneOf(rd).forward(req, resp);

        }});

        ErrorFilter errorFilter = new ErrorFilter();
        errorFilter.doFilter(req, resp, chain);
        assertThat(errContent.toString().contains("ServletException"), is(true));

    }


}
