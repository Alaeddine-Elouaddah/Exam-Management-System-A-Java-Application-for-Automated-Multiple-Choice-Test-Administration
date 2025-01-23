
 package projet_java;

import java.sql.*;
import java.util.Scanner;

public class MenuAdmin {

    private static Scanner scanner = new Scanner(System.in);

    // Méthode pour afficher le menu de l'admin
    public void showMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n=============================");
            System.out.println("  Menu Admin");
            System.out.println("=============================");
            System.out.println("1. Créer un utilisateur");
            System.out.println("2. Supprimer un utilisateur par ID");
            System.out.println("3. Afficher les utilisateurs");
            System.out.println("4. Quitter");
            System.out.println("=================================");
            System.out.print("Choisissez une option : ");

            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:               	
                    createUser(); 
                    break;
                case 2:                	
                    deleteUserById(); 
                    break;
                case 3:               	
                    displayUsers(); 
                    break;
                case 4:              	
                    System.out.println("Au revoir ! À bientôt !");
                    running = false; 
                    break;
                default:
                	
                    System.out.println(ConsoleColors.RED + "Choix invalide, veuillez réessayer." + ConsoleColors.RESET);
            }
        }
    }


    // Méthode pour créer un utilisateur
    private void createUser() {
        System.out.println("===============================================");
        System.out.println("         C R E A T I O N  D' U T I L I S A T E U R");
        System.out.println("===============================================");
        System.out.print("Entrez le login de l'utilisateur : ");
        String username = scanner.nextLine();
        System.out.print("Entrez le mot de passe de l'utilisateur : ");
        String password = scanner.nextLine();
        System.out.print("Entrez le rôle de l'utilisateur (professeur, etudiant) : ");
        String role = scanner.nextLine();

        if (isUsernameExists(username)) {
            System.out.println(ConsoleColors.RED + "Le login existe déjà. Impossible de créer un nouvel utilisateur avec ce login." + ConsoleColors.RESET);
            return;
        }

        if (role.equalsIgnoreCase("admin")) {
            System.out.println(ConsoleColors.RED + "Vous ne pouvez pas créer un autre utilisateur avec le rôle d'admin." + ConsoleColors.RESET);
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, role);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println(ConsoleColors.GREEN + "Utilisateur créé avec succès !" + ConsoleColors.RESET);
                } else {
                    System.out.println(ConsoleColors.RED + "Erreur lors de la création de l'utilisateur." + ConsoleColors.RESET);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(ConsoleColors.RED + "Erreur de connexion à la base de données." + ConsoleColors.RESET);
        }
    }


    // Méthode pour vérifier si un login existe déjà dans la base de données
    private boolean isUsernameExists(String username) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM users WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0; 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; 
    }


     // Méthode pour supprimer un utilisateur par ID
    private void deleteUserById() {
        System.out.print("Entrez l'ID de l'utilisateur à supprimer : ");
        int userId = scanner.nextInt();
        scanner.nextLine(); 

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println(ConsoleColors.GREEN + "Utilisateur supprimé avec succès !" + ConsoleColors.RESET);
                } else {
                    System.out.println(ConsoleColors.RED + "Aucun utilisateur trouvé avec cet ID." + ConsoleColors.RESET);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(ConsoleColors.RED + "Erreur de connexion à la base de données." + ConsoleColors.RESET);
        }
    }


    // Méthode pour afficher les utilisateurs
    private void displayUsers() {
        System.out.println(ConsoleColors.GREEN + "Liste des utilisateurs :" + ConsoleColors.RESET);

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    String role = resultSet.getString("role");
                    System.out.println("ID: " + id + ", Login: " + username + ", Rôle: " + role);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(ConsoleColors.RED + "Erreur de connexion à la base de données." + ConsoleColors.RESET);
        }
    }


    
    
}
