
projet_java;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Création de l'objet AuthService pour l'authentification
        AuthService authService = new AuthService();
        boolean authenticated = false;

        while (!authenticated) {
            // Demander le login et mot de passe à l'utilisateur
            System.out.println("\n===============================");
            System.out.println("  Authentification Utilisateur");
            System.out.println("===============================");
            System.out.print("Entrez votre login : ");
            String username = scanner.nextLine();

            System.out.print("Entrez votre mot de passe : ");
            String password = scanner.nextLine();

            User loggedInUser = authService.authenticate(username, password);

            if (loggedInUser != null) {
                System.out.println(ConsoleColors.GREEN + "\nBienvenue " + username + " ! Vous êtes connecté en tant que " + loggedInUser.getRole() + ConsoleColors.RESET);
                MenuService menuService = new MenuService();
                menuService.displayMenu(loggedInUser);
                authenticated = true; 
            } else {
                System.out.println(ConsoleColors.RED + "\nLogin ou mot de passe incorrect." + ConsoleColors.RESET);

                System.out.print("\nVoulez-vous réessayer ? (O/N) : ");
                String choice = scanner.nextLine();

                if (choice.equalsIgnoreCase("N")) {
                System.out.println(ConsoleColors.YELLOW + "\nAu revoir ! À bientôt !" + ConsoleColors.RESET);
                    break; 
                }
            }
        }

        scanner.close(); 
    }
}



// Classe pour gérer les couleurs ANSI dans la console
class ConsoleColors {
    public static final String RESET = "\033[0m";  
    public static final String RED = "\033[0;31m";   
    public static final String GREEN = "\033[0;32m"; 
    public static final String YELLOW = "\033[0;33m"; 
    public static final String BLUE = "\033[0;34m";   
    public static final String CYAN = "\033[0;36m";   
}


















