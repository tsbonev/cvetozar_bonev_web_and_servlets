package com.clouway.bankapp.adapter.web.helper;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServletPageHandler implements PageHandler {

    /**
     * Returns a jsp page with the forward method and closes the jdbc connection.
     *
     * @param pagePath page to find
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    public void getPage(String pagePath, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        RequestDispatcher rd = req.getRequestDispatcher(pagePath);
        rd.forward(req, resp);
    }

    /**
     * Redirects to a servlet or jsp page and saves
     * a message type and content into the request session,
     * closes the connection after it has redirected.
     *
     * @param servletPath to redirect to
     * @param resp servlet response
     * @param req servlet request
     * @param messageType type of the message
     * @param messageContent content of the message
     * @throws IOException
     */
    public void redirectTo(String servletPath, HttpServletResponse resp, HttpServletRequest req, String messageType, String messageContent) throws IOException{

        req.getSession().setAttribute(messageType, messageContent);
        resp.sendRedirect(servletPath);

    }

}
