package projet_java;

import java.io.*;
import java.sql.*;
import java.util.*;

public class MenuProfesseur {

    private static Scanner scanner = new Scanner(System.in);

    // Méthode pour afficher le menu du professeur
    public void showMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n=============================");
            System.out.println("  Menu Professeur ");
            System.out.println("=============================");
            System.out.println("1. Ajouter un examen");
            System.out.println("2. Afficher les examens");
            System.out.println("3. Supprimer un examen");
            System.out.println("4. Quitter");
            System.out.println("=================================");
            System.out.print("Choisissez une option : ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consommer la ligne restante

            switch (choice) {
                case 1:
                    ajouterExamen(); // Ajouter un examen
                    break;
                case 2:
                    afficherExamens(); // Afficher les examens
                    break;
                case 3:
                    supprimerExamen(); // Supprimer un examen
                    break;
                case 4:
                    System.out.println("Au revoir !");
                    running = false;
                    break;
                default:
                    System.out.println("Choix invalide, veuillez réessayer.");
            }
        }
    }



    // Méthode pour ajouter un examen
    private void ajouterExamen() {
        System.out.println("=====================================");
        System.out.println("       Ajouter un examen");
        System.out.println("=====================================");

        System.out.print("Entrez le nom de l'examen : ");
        String nomExamen = scanner.nextLine();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String queryExamen = "INSERT INTO exams (name) VALUES (?)";
            try (PreparedStatement statement = connection.prepareStatement(queryExamen, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, nomExamen);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    ResultSet rs = statement.getGeneratedKeys();
                    int examId = 0;
                    if (rs.next()) {
                        examId = rs.getInt(1);
                    }

                    System.out.print("Entrez le nombre de questions : ");
                    int nbQuestions = scanner.nextInt();
                    scanner.nextLine(); 
                    
                    String examFilePath = "C:\\Users\\ramiz\\OneDrive\\Documents\\dossier_professeur\\examen_" + examId + ".txt";
                    BufferedWriter writer = new BufferedWriter(new FileWriter(examFilePath));

                    for (int i = 1; i <= nbQuestions; i++) {
                        System.out.println("\nAjouter la question " + i);
                        System.out.print("Entrez le texte de la question " + i + " : ");
                        String questionText = scanner.nextLine();

                        String queryQuestion = "INSERT INTO questions (exam_id, question_text, correct_answer) VALUES (?, ?, ?)";
                        try (PreparedStatement stmtQuestion = connection.prepareStatement(queryQuestion, Statement.RETURN_GENERATED_KEYS)) {
                            stmtQuestion.setInt(1, examId);
                            stmtQuestion.setString(2, questionText);
                            stmtQuestion.setInt(3, 0); 
                            stmtQuestion.executeUpdate();

                            ResultSet rsQuestion = stmtQuestion.getGeneratedKeys();
                            int questionId = 0;
                            if (rsQuestion.next()) {
                                questionId = rsQuestion.getInt(1);
                            }

                            String[] options = new String[3];
                            for (int j = 0; j < 3; j++) {
                                System.out.print("Entrez l'option " + (j + 1) + " : ");
                                options[j] = scanner.nextLine();

                                String queryOption = "INSERT INTO options (question_id, option_text, is_correct) VALUES (?, ?, ?)";
                                try (PreparedStatement stmtOption = connection.prepareStatement(queryOption, Statement.RETURN_GENERATED_KEYS)) {
                                    stmtOption.setInt(1, questionId);
                                    stmtOption.setString(2, options[j]);
                                    stmtOption.setBoolean(3, false); 
                                    stmtOption.executeUpdate();
                                }
                            }

                            System.out.print("Quelle option est correcte ? (1, 2 ou 3) : ");
                            int correctOption = scanner.nextInt();
                            scanner.nextLine(); 

                            String queryUpdateCorrectOption = "UPDATE options SET is_correct = ? WHERE question_id = ? AND option_text = ?";
                            try (PreparedStatement stmtUpdateOption = connection.prepareStatement(queryUpdateCorrectOption)) {
                                stmtUpdateOption.setBoolean(1, true);
                                stmtUpdateOption.setInt(2, questionId);
                                stmtUpdateOption.setString(3, options[correctOption - 1]);
                                stmtUpdateOption.executeUpdate();
                            }

                            String queryUpdateQuestion = "UPDATE questions SET correct_answer = ? WHERE id = ?";
                            try (PreparedStatement stmtUpdateQuestion = connection.prepareStatement(queryUpdateQuestion)) {
                                stmtUpdateQuestion.setInt(1, correctOption); // Enregistrer l'ID de l'option correcte
                                stmtUpdateQuestion.setInt(2, questionId);
                                stmtUpdateQuestion.executeUpdate();
                            }

                            writer.write(correctOption + "\n");
                        }
                    }

                    writer.close(); 
                    System.out.println("Examen ajouté avec succès !");
                    System.out.println("Le fichier de l'examen a été enregistré dans " + examFilePath);
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ajout de l'examen.");
        }
    }



    // Méthode pour afficher les examens
    private void afficherExamens() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM exams";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet rs = statement.executeQuery();

                System.out.println("\nListe des examens :");
                while (rs.next()) {
                    int examId = rs.getInt("id");
                    String examName = rs.getString("name");
                    System.out.println("ID : " + examId + " - Nom de l'examen : " + examName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'affichage des examens.");
        }
    }


    // Méthode pour supprimer un examen par ID
    private void supprimerExamen() {
        System.out.print("Entrez l'ID de l'examen à supprimer : ");
        int examId = scanner.nextInt();
        scanner.nextLine(); 

        try (Connection connection = DatabaseConnection.getConnection()) {
            String queryDeleteOptions = "DELETE FROM options WHERE question_id IN (SELECT id FROM questions WHERE exam_id = ?)";
            try (PreparedStatement stmtDeleteOptions = connection.prepareStatement(queryDeleteOptions)) {
                stmtDeleteOptions.setInt(1, examId);
                stmtDeleteOptions.executeUpdate();
            }

            String queryDeleteQuestions = "DELETE FROM questions WHERE exam_id = ?";
            try (PreparedStatement stmtDeleteQuestions = connection.prepareStatement(queryDeleteQuestions)) {
                stmtDeleteQuestions.setInt(1, examId);
                stmtDeleteQuestions.executeUpdate();
            }

            String queryDeleteExamen = "DELETE FROM exams WHERE id = ?";
            try (PreparedStatement stmtDeleteExamen = connection.prepareStatement(queryDeleteExamen)) {
                stmtDeleteExamen.setInt(1, examId);
                int rowsAffected = stmtDeleteExamen.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Examen et toutes ses questions et options supprimés avec succès !");
                } else {
                    System.out.println("Aucun examen trouvé avec cet ID.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la suppression de l'examen.");
        }
    }
    

}
