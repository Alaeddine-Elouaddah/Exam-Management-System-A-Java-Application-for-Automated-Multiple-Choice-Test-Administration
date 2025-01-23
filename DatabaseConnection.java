
package projet_java;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/qcm";
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = ""; 

    
    public static Connection getConnection() throws SQLException {
        try {
            
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Erreur lors de la connexion à la base de données.");
        }
    }
}
