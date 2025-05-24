package cuoiky;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/pet_db";
    private static final String USER = "root";           
    private static final String PASSWORD = "123456";          

    public static Connection getConnection() throws SQLException {
        try { 
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
