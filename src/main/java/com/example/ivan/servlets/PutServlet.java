package com.example.ivan.servlets;

import com.example.ivan.config.DbConfig;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
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

public class PutServlet extends HttpServlet {

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

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        Long id = Long.parseLong(req.getParameter("id"));
        String username = req.getParameter("username");
        updateUser(id, username);

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Обновление пользователя</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Пользователь успешно обновлен!</h1>");
        out.println("<p>Id пользователя: " + id + " Имя пользователя: " + username + "</p>");
        out.println("</body>");
        out.println("</html>");
    }

    public void updateUser(Long id, String username) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE user_andersen SET username = ? WHERE id =" + id);
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
