
package projet_java;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

public class MenuEtudiant {

    private static Scanner scanner = new Scanner(System.in);

    // Méthode pour afficher le menu de l'étudiant
    public void showMenu(User user) {
        boolean running = true;

        while (running) {
        	System.out.println("\n=============================");
            System.out.println("      Menu Étudiant ");
            System.out.println("=============================");
            System.out.println("1. Voir les examens disponibles");
            System.out.println("2. Passer un examen");
            System.out.println("3. Corriger un examen");
            System.out.println("4. Quitter");
            System.out.println("=================================");
            System.out.print("Choisissez une option : ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); 
            
            switch (choice) {
                case 1:
                    afficherExamens(user); 
                    break;
                case 2:
                    passerExamen(user); 
                    break;
                case 3:
                    corrigerExamen(user); 
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




    // Méthode pour afficher les examens disponibles
    private void afficherExamens(User user) {
        System.out.println("\nExamen(s) disponible(s) :");
        
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM exams";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int examId = rs.getInt("id");
                    String examName = rs.getString("name");
                    System.out.println("ID: " + examId + " - Nom de l'examen: " + examName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'affichage des examens.");
        }
    }


    // Méthode pour corriger un examen
    private void corrigerExamen(User user) {
        System.out.print("\nEntrez l'ID de l'examen que vous souhaitez corriger : ");
        int examId = scanner.nextInt();
        scanner.nextLine(); 

        try {
            String studentFilePath = "C:\\Users\\ramiz\\OneDrive\\Documents\\dossier_etudiant\\etudiant_" + user.getId() + "exams" + examId + ".txt";
            File studentFile = new File(studentFilePath);
            if (!studentFile.exists()) {
                System.out.println("Le fichier de l'examen de l'étudiant n'existe pas.");
                return;
            }

            String examFilePath = "C:\\Users\\ramiz\\OneDrive\\Documents\\dossier_professeur\\examen_" + examId + ".txt";
            File examFile = new File(examFilePath);
            if (!examFile.exists()) {
                System.out.println("Le fichier de l'examen n'existe pas.");
                return;
            }

            List<String> studentResponses = lireFichier(studentFile);
            List<String> correctAnswers = lireFichier(examFile);

            List<String> correction = new ArrayList<>();
            int score = 0; 

            for (int i = 0; i < studentResponses.size(); i++) {
                String studentAnswer = studentResponses.get(i).trim();
                String correctAnswer = correctAnswers.get(i).trim();
                String result;

                if (studentAnswer.equals(correctAnswer)) {
                    result = "Vrai : La bonne réponse est " + correctAnswer;
                    score++;  
                } else {
                    result = "Faux : La bonne réponse est " + correctAnswer;
                }

                correction.add("Question " + (i + 1) + ": " + result);
            }

            correction.add("\nScore final : " + score + " / " + studentResponses.size());

            String correctionFilePath = "C:\\Users\\ramiz\\OneDrive\\Documents\\dossier_correction\\correction-" + examId + "-etudiant" + user.getId() + ".txt";
            enregistrerCorrection(correctionFilePath, correction);

            System.out.println("La correction de votre examen a été enregistrée avec un score de " + score + ".");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la correction de l'examen.");
        }
    }


    // Méthode pour lire un fichier et retourner son contenu sous forme de liste de chaînes
    private List<String> lireFichier(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }


    // Méthode pour enregistrer la correction dans un fichier
    private void enregistrerCorrection(String filePath, List<String> correction) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : correction) {
                writer.write(line);
                writer.newLine();
            }
        }
    }







    // Méthode pour permettre à l'étudiant de passer un examen
    private void passerExamen(User user) {
        System.out.print("\nEntrez l'ID de l'examen que vous souhaitez passer : ");
        int examId = scanner.nextInt();
        scanner.nextLine(); 

        try (Connection connection = DatabaseConnection.getConnection()) {
            String checkExamQuery = "SELECT * FROM exams WHERE id = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkExamQuery)) {
                checkStmt.setInt(1, examId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Vous avez choisi l'examen : " + rs.getString("name"));
                        passerQuestions(connection, examId, user.getId());
                    } else {
                        System.out.println("Examen introuvable.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la récupération de l'examen.");
        }
    }


    // Méthode pour passer les questions d'un examen
    private void passerQuestions(Connection connection, int examId, int userId) throws SQLException {
        supprimerAnciennesRéponses(connection, userId, examId);

        String queryQuestions = "SELECT * FROM questions WHERE exam_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(queryQuestions)) {
            stmt.setInt(1, examId);
            try (ResultSet rs = stmt.executeQuery()) {
                int questionNumber = 1;
                while (rs.next()) {
                    int questionId = rs.getInt("id");
                    String questionText = rs.getString("question_text");
                    System.out.println("\nQuestion " + questionNumber + ": " + questionText);
                    int selectedOptionId = afficherOptions(connection, questionId);
                    enregistrerRéponse(userId, examId, questionId, selectedOptionId); 
                    enregistrerRéponseDansFichier(userId, examId, questionId, selectedOptionId); 
                }
                System.out.println("Merci d'avoir passé l'examen !");
            }
        }
    }



    // Méthode pour supprimer les anciennes réponses de l'étudiant pour cet examen
    private void supprimerAnciennesRéponses(Connection connection, int userId, int examId) {
        try {
            String deleteQuery = "DELETE FROM student_responses WHERE student_id = ? AND exam_id = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, userId);
                deleteStmt.setInt(2, examId);
                deleteStmt.executeUpdate();
                System.out.println("Anciennes réponses supprimées.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la suppression des anciennes réponses.");
        }
    }


    

 // Méthode pour afficher les options d'une question
    private int afficherOptions(Connection connection, int questionId) throws SQLException {
        System.out.println("Options :");
        String queryOptions = "SELECT * FROM options WHERE question_id = ?";
        try (PreparedStatement stmtOptions = connection.prepareStatement(queryOptions)) {
            stmtOptions.setInt(1, questionId);
            try (ResultSet rsOptions = stmtOptions.executeQuery()) {
                int optionNumber = 1;
                while (rsOptions.next()) {
                    System.out.println(optionNumber + ". " + rsOptions.getString("option_text"));
                    optionNumber++;
                }
            }
        }

        System.out.print("\nEntrez le numéro de l'option choisie : ");
        return scanner.nextInt();
    }


    // Méthode pour enregistrer la réponse dans la base de données
    private void enregistrerRéponse(int studentId, int examId, int questionId, int selectedOptionId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO student_responses (student_id, exam_id, question_id, selected_option_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, studentId);
                stmt.setInt(2, examId);
                stmt.setInt(3, questionId);
                stmt.setInt(4, selectedOptionId);
                stmt.executeUpdate();
                System.out.println("Réponse enregistrée dans la base de données.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'enregistrement de la réponse.");
        }
    }


    // Méthode pour enregistrer la réponse dans un fichier
    private void enregistrerRéponseDansFichier(int studentId, int examId, int questionId, int selectedOptionId) {
        String cheminFichier = "C:\\Users\\ramiz\\OneDrive\\Documents\\dossier_etudiant\\etudiant_" + studentId + "exams" + examId + ".txt";
        
        try {
            File dossier = new File("C:\\Users\\ramiz\\OneDrive\\Documents\\dossier_etudiant");
            if (!dossier.exists()) {
                dossier.mkdirs(); 
            }

            File file = new File(cheminFichier);
            if (!file.exists()) {
                file.createNewFile(); 
            }

            StringBuilder responses = new StringBuilder();
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    responses.append(scanner.nextLine()).append("\n");
                }
            }

            responses.append(selectedOptionId).append("\n");

            String[] allResponses = responses.toString().split("\n");
            if (allResponses.length > 2) {
                responses = new StringBuilder();
                responses.append(allResponses[allResponses.length - 2]).append("\n")
                        .append(allResponses[allResponses.length - 1]).append("\n");
            }

            try (FileWriter writer = new FileWriter(file, false)) { 
                writer.write(responses.toString()); 
                System.out.println("Réponse enregistrée dans le fichier.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'enregistrement de la réponse dans le fichier.");
        }
    }


    
}
