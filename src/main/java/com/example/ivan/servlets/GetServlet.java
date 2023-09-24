package com.example.ivan.servlets;

import com.example.ivan.config.DbConfig;
import com.example.ivan.entity.User;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GetServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
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
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            connection.setAutoCommit(false);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> users = getUsers();
        req.setAttribute("users", users);
        PrintWriter pw = resp.getWriter();
        pw.println("<html>");
        for (User user : users) {
            pw.println("<h1>" + user.getId() + " | " + user.getUsername() + "</h1>");

            pw.println("<form action=\"/andersen_database_war/update\" method=\"POST\">");
            pw.write("<input class=\"form-control\" type=\"hidden\" name=\"id\" value=\"" + user.getId() + "\">");
            pw.write("<input class=\"form-control\" name=\"username\" placeholder=\"Update:\" type=\"text\">");
            pw.println("<button type=\"submit\">Update User</button>");
            pw.println("</form>");

            pw.println("<form action=\"/andersen_database_war/delete\" method=\"POST\">");
            pw.write("<input class=\"form-control\" type=\"hidden\" name=\"id\" value=\"" + user.getId() + "\">");
            pw.println("<button type=\"submit\">Delete User</button>");
            pw.println("</form>");
        }

        pw.println("<form action=\"\" method=\"POST\">");
        pw.write("<input class=\"form-control\" name=\"username\" placeholder=\"Write the Username:\" type=\"text\">");
        pw.println("<button type=\"submit\">Add User</button>");
        pw.println("</form>");
        pw.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String username = req.getParameter("username");
        addUser(username);

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Добавление пользователя</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Пользователь успешно добавлен!</h1>");
        out.println("<p>Имя пользователя: " + username + "</p>");
        out.println("</body>");
        out.println("</html>");
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM user_andersen ORDER BY id");
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setUsername(resultSet.getString("username"));
                users.add(user);
            }
            connection.commit();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return users;
    }

    public void addUser(String username) {
        try {
            CallableStatement statement = connection.prepareCall("INSERT INTO user_andersen (username) VALUES (?)");
            statement.setString(1, username);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
