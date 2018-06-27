package com.clouway.bankapp.adapter.web;

import com.clouway.bankapp.adapter.web.helper.PageHandler;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    PageHandler page;

    public HomeServlet(PageHandler page){
        this.page = page;
    }

    /**
     * Gets the index page and sets the title to Servlet bank.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setAttribute("title", "Servlet bank");
        page.getPage("/view/index.jsp", req, resp);
    }
}
