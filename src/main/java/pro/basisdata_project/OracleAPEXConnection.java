package pro.basisdata_project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleAPEXConnection {

    // JDBC URL, username, and password of Oracle APEX database server
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";

    // JDBC variables for opening and managing connection
    private static Connection connection;

    public static Connection getConnection() {
        try {
            // Load the Oracle JDBC driver
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // Establish the connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

