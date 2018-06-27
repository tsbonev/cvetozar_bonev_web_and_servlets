package com.clouway.bankapp.adapter.web.login;

import com.clouway.bankapp.adapter.web.filter.SessionFilter;
import com.clouway.bankapp.core.Session;
import com.clouway.bankapp.core.SessionRepository;
import com.clouway.bankapp.adapter.web.helper.PageHandler;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    PageHandler page;
    SessionRepository sessionRepo;

    public LogoutServlet(PageHandler page, SessionRepository sessionRepository) {
        this.page = page;
        this.sessionRepo = sessionRepository;
    }

    protected Session getSession(){
        return SessionFilter.session.get();
    }

    /**
     * Removes the session and redirects to the home page.
     *
     * @param req  servlet request
     * @param resp servlet response
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Session session = getSession();

        if(session != null){
            sessionRepo.terminateSession(session.getSessionId());
            page.redirectTo("/home", resp, req,
                    "infoMessage", "User logged out!");
        }

    }
}
