package com.clouway.bankapp.adapter.web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter("/*")
public class ErrorFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    /**
     * Catches any unchecked exceptions from the servlets,
     * redirects to the home page with an error message and
     * prints the stacktrace.
     * 
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        }catch (Exception e){
            e.printStackTrace();
            servletRequest.getRequestDispatcher("/error").forward(servletRequest, servletResponse);
        }

    }

    @Override
    public void destroy() {

    }
}
