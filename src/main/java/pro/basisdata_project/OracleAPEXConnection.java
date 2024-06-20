package pro.basisdata_project;

import java.sql.*;

public class OracleAPEXConnection {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "SYSTEM";
    private static final String PASSWORD = "wonorejo88";

    private static Connection connection;

    public static Connection getConnection() {
        try {
            // Load the Oracle JDBC driver
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // Establish the connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection established successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("Oracle JDBC Driver not found. Include it in your library path.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection failed! Check output console.");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    public static boolean isConnectionSuccessful() {
        try {
            if (connection == null || connection.isClosed()) {
                getConnection();
            }
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Error while checking connection status.");
            e.printStackTrace();
            return false;
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Connection closed successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error while closing connection.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (isConnectionSuccessful()) {
            System.out.println("Connection to the database was successful.");
        } else {
            System.err.println("Failed to connect to the database.");
        }
    }
}
