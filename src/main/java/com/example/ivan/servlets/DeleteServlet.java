package com.example.ivan.servlets;

import com.example.ivan.config.DbConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteServlet extends HttpServlet {

    private static final Connection connection;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String url = DbConfig.DB_URL;
        String username = DbConfig.DB_USERNAME;
        String password = DbConfig.DB_PASSWORD;

        try {
            connection = DriverManager.getConnection(url, username, password);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        Long id = Long.parseLong(req.getParameter("id"));
        deleteUser(id);

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Удаление пользователя</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Пользователь успешно удален!</h1>");
        out.println("<p>Id пользователя: " + id + "</p>");
        out.println("</body>");
        out.println("</html>");
    }

    public void deleteUser(Long id) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM user_andersen WHERE id = ?");
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
