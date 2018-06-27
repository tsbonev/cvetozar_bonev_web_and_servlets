package com.clouway.bankapp.adapter.web.filter;

import com.clouway.bankapp.adapter.web.helper.PageHandler;
import com.clouway.bankapp.core.Session;
import com.clouway.bankapp.core.SessionRepository;
import com.clouway.bankapp.core.User;
import com.clouway.bankapp.core.UserRepository;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class SessionFilter implements Filter {

    UserRepository userRepo;
    SessionRepository sessionRepo;
    PageHandler page;

    public static ThreadLocal<Session> session = new ThreadLocal<>();
    public static ThreadLocal<User> userContext = new ThreadLocal<>();

    public SessionFilter(PageHandler page,
                         UserRepository userRepository,
                         SessionRepository sessionRepository) {
        this.page = page;
        this.userRepo = userRepository;
        this.sessionRepo = sessionRepository;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Checks the user's browser for a SID
     * cookie and if one is presented and
     * has a persistent session then a session
     * object is retrieved and set to the ThreadLocal.
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        HttpServletRequest req = (HttpServletRequest) servletRequest;

        try {

            Optional<Cookie> sessionCookie = getSessionCookie(req);

            if (!sessionCookie.isPresent()) {
                addCookie(resp);
                page.redirectTo("/home", resp, req,
                        "", "");
                return;
            }

            Cookie SID = sessionCookie.get();
            req.setAttribute("SID", SID.getValue());

            Optional<Session> possibleSession = sessionRepo.getSessionAvailableAt(SID.getValue(),
                    Timestamp.valueOf(LocalDateTime.now()));

            if (possibleSession.isPresent()) {
                setSession(req, resp, filterChain, possibleSession.get());
            } else {
                authenticateRequest(req, resp, filterChain);
            }

        } finally {
            session.remove();
            userContext.remove();
        }


    }

    /**
     * Adds a cookie to the user's browser
     * and returns its value.
     *
     * @param resp servlet response
     * @return created cookie's value
     */
    private String addCookie(HttpServletResponse resp) {

        Cookie SID = new Cookie("SID", UUID.randomUUID().toString());
        SID.setMaxAge(60000);
        resp.addCookie(SID);
        return SID.getValue();

    }

    /**
     * Sets the session to the thread local,
     * refreshes it in the database and
     * fetches the user associated with it.
     *
     * @param req              servlet response
     * @param resp             servlet request
     * @param filterChain      filter chain
     * @param retrievedSession session found in the database
     * @throws IOException
     * @throws ServletException
     */
    private void setSession(HttpServletRequest req, HttpServletResponse resp,
                            FilterChain filterChain, Session retrievedSession) throws IOException, ServletException {

        setSessionUser(retrievedSession);

        sessionRepo.refreshSession(retrievedSession);

        session.set(retrievedSession);

        filterChain.doFilter(req, resp);

    }

    /**
     * Authenticates a servlet request
     * based on which page requires a
     * session.
     *
     * @param req         servlet request
     * @param resp        servlet response
     * @param filterChain filter chain
     * @throws IOException
     * @throws ServletException
     */
    private void authenticateRequest(HttpServletRequest req,
                                     HttpServletResponse resp,
                                     FilterChain filterChain) throws IOException, ServletException {

        if (!(req.getRequestURI().endsWith("/login")
                || req.getRequestURI().endsWith("/register")
                || req.getRequestURI().endsWith("/home")
                || req.getRequestURI().endsWith("/"))) {

            page.redirectTo("/home", resp, req,
                    "infoMessage", "You must be logged in to view this page!");
            return;
        }

        filterChain.doFilter(req, resp);

    }

    /**
     * Retrieves the session user and
     * sets it to the userContext threadlocal.
     *
     * @param session to attach a user to
     * @return session with a user
     */
    private void setSessionUser(Session session) {

        Optional<User> possibleUser = userRepo.getById(session.getUserId());

        if (possibleUser.isPresent()) {
            userContext.set(possibleUser.get());
        }

    }


    /**
     * Retrieves an optional SID cookie
     * from the user's browser.
     *
     * @param req servlet request
     * @return optional session cookie
     */
    private Optional<Cookie> getSessionCookie(HttpServletRequest req) {

        Optional<Cookie> cookie = Arrays.stream(req.getCookies())
                .filter(c -> c.getName().equals("SID"))
                .findFirst();

        return cookie;

    }

    @Override
    public void destroy() {

    }
}
