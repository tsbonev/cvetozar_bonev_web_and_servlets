package com.clouway.bankapp.adapter.web;

import com.clouway.bankapp.adapter.web.helper.PageHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/error")
public class ErrorHandler extends HttpServlet {

    PageHandler page;

    public ErrorHandler(PageHandler page){
        this.page = page;
    }

    /**
     * Redirects to the home page with an error message.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String errorContent = "Something has happened!";

        String errorCode = req.getParameter("code");

        if(errorCode != null && !errorCode.isEmpty()){
            switch (errorCode){
                case "404":
                    errorContent = "Page not found!";
                    break;
                case "500":
                    errorContent = "Internal server error!";
                    break;
            }
        }

        page.redirectTo("/home", resp, req,
                "errorMessage", errorContent);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
