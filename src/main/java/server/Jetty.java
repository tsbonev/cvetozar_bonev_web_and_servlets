package server;

import com.clouway.bankapp.adapter.jdbc.ConnectionCreator;
import com.clouway.bankapp.core.SessionRepository;
import com.clouway.bankapp.core.TransactionRepository;
import com.clouway.bankapp.core.UserRepository;
import com.clouway.bankapp.adapter.web.ErrorHandler;
import com.clouway.bankapp.adapter.web.HomeServlet;
import com.clouway.bankapp.adapter.web.filter.*;
import com.clouway.bankapp.adapter.web.helper.PageHandler;
import com.clouway.bankapp.adapter.web.helper.UserCounter;
import com.clouway.bankapp.adapter.web.login.LoginServlet;
import com.clouway.bankapp.adapter.web.login.LogoutServlet;
import com.clouway.bankapp.adapter.web.login.RegisterServlet;
import com.clouway.bankapp.adapter.web.transaction.AccountServlet;
import com.clouway.bankapp.adapter.web.transaction.HistoryServlet;
import com.clouway.bankapp.adapter.web.transaction.TransactionServlet;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public final class Jetty {
    private final Server server;
    private final ConnectionCreator creator;

    private final PageHandler page;
    private final UserRepository userRepo;
    private final TransactionRepository transactionRepo;
    private final SessionRepository sessionRepo;


    public Jetty(int port, ConnectionCreator creator,
                 PageHandler page,
                 UserRepository userRepo,
                 TransactionRepository transactionRepo,
                 SessionRepository sessionRepo) {
        this.server = new Server(port);
        this.creator = creator;
        this.page = page;
        this.userRepo = userRepo;
        this.transactionRepo = transactionRepo;
        this.sessionRepo = sessionRepo;
    }

    public void start() {

        ServletContextHandler servletContext = new WebAppContext();
        servletContext.setResourceBase("web/WEB-INF");
        servletContext.setContextPath("/");

        org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList.setServerDefault(server);
        classlist.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration", "org.eclipse.jetty.plus.webapp.EnvConfiguration", "org.eclipse.jetty.plus.webapp.PlusConfiguration");
        classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.annotations.AnnotationConfiguration");

        servletContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",".*/[^/]*jstl.*\\.jar$");

        ErrorPageErrorHandler errorHandler = new ErrorPageErrorHandler();
        errorHandler.addErrorPage(404, "/error?code=404");
        errorHandler.addErrorPage(500, "/error?code=500");
        servletContext.setErrorHandler(errorHandler);

        servletContext.addEventListener(new ServletContextListener() {

            public void contextInitialized(ServletContextEvent servletContextEvent) {
                ServletContext servletContext = servletContextEvent.getServletContext();

                final int PAGESIZE = 20;

                servletContext.setAttribute("counter", new UserCounter(sessionRepo));

                servletContext.addServlet("home", new HomeServlet(page))
                        .addMapping("/", "/home");

                servletContext.addServlet("login", new LoginServlet(page, userRepo, sessionRepo))
                        .addMapping("/login");
                servletContext.addServlet("register", new RegisterServlet(page, userRepo, transactionRepo))
                        .addMapping("/register");
                servletContext.addServlet("logout", new LogoutServlet(page, sessionRepo)).
                        addMapping("/logout");

                servletContext.addServlet("account", new AccountServlet(page, transactionRepo, userRepo))
                        .addMapping("/account");
                servletContext.addServlet("transaction", new TransactionServlet(page, userRepo, transactionRepo))
                        .addMapping("/transaction");

                servletContext.addServlet("history", new HistoryServlet(page, transactionRepo,
                        userRepo, PAGESIZE))
                        .addMapping("/history");

                servletContext.addServlet("error", new ErrorHandler(page))
                        .addMapping("/error");



                servletContext.addFilter("errorFilter", new ErrorFilter())
                        .addMappingForUrlPatterns(null, false, "/*");

                servletContext.addFilter("connectionPerRequest", new ConnectionPerRequestFilter(creator))
                        .addMappingForUrlPatterns(null, false, "/*");

                servletContext.addFilter("sessionFilter", new SessionFilter(page, userRepo, sessionRepo))
                        .addMappingForUrlPatterns(null, false, "/*");


            }

            public void contextDestroyed(ServletContextEvent servletContextEvent) {}
        });

        ContextHandler staticResourceHandler = new ContextHandler();
        staticResourceHandler.setContextPath("/css");
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("src/main/resources/static/css");

        staticResourceHandler.setHandler(resourceHandler);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{staticResourceHandler, servletContext});

        server.setHandler(handlers);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
