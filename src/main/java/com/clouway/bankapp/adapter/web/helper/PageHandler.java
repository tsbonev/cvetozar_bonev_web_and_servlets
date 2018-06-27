package com.clouway.bankapp.adapter.web.helper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface PageHandler {

    void getPage(String pagePath, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;
    void redirectTo(String servletPath, HttpServletResponse resp,
                    HttpServletRequest req, String messageType, String messageContent) throws IOException;
}
