
import java.sql.*;

public class AuthService {

    // Méthode pour authentifier un utilisateur et récupérer son rôle
	public User authenticate(String username, String password) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                
                ResultSet resultSet = preparedStatement.executeQuery();
                
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");  
                    String role = resultSet.getString("role");
                    return new User(id, username, password, role);  
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }
}
