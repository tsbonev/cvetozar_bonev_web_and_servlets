package com.clouway.bankapp.adapter.web.filter;

import com.clouway.bankapp.adapter.jdbc.ConnectionCreator;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.PooledConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPerRequestFilter implements Filter {

    private FilterConfig filterConfig = null;

    public static ThreadLocal<Connection> connection = new ThreadLocal<>();

    /**
     * Returns the threadLocal connection
     * and if none has been set creates one
     * from the data source.
     *
     * @return a jdbc connection
     * @throws SQLException
     */
    public static Connection get() throws SQLException {

        if(connection.get() == null){

            PooledConnection pooledConnection = creator.get().getPooledConnection();

            connection.set(pooledConnection.getConnection());

        }

        return connection.get();

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    private static ConnectionCreator creator;

    public ConnectionPerRequestFilter(ConnectionCreator creator){
        this.creator = creator;
    }

    /**
     * Opens a new connection and saves it in a ThreadLocal variable
     * every time a request is sent to a page from
     * account, transaction, login, register or history.
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        try{
            PooledConnection pooledConnection = creator.get().getPooledConnection();

            connection.set(pooledConnection.getConnection());

            HttpServletRequest req = (HttpServletRequest) servletRequest;
            HttpServletResponse resp = (HttpServletResponse) servletResponse;

            filterChain.doFilter(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if(connection.get() != null){

                try {
                    connection.get().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                finally {
                    connection.remove();
                }
            }
        }

    }

    @Override
    public void destroy() {
    }
}
