
 package projet_java;

public class MenuService {

    
    // Méthode pour afficher le menu en fonction du rôle de l'utilisateur
    public void displayMenu(User user) {
        switch (user.getRole()) {
            case "admin":
                MenuAdmin menuAdmin = new MenuAdmin();
                menuAdmin.showMenu();
                break;
            case "professeur":
                MenuProfesseur menuProfesseur = new MenuProfesseur();
                menuProfesseur.showMenu();
                break;
            case "etudiant":
                MenuEtudiant menuEtudiant = new MenuEtudiant();
                menuEtudiant.showMenu(user);  
                break;
            default:
                System.out.println("Rôle inconnu.");
        }
    }
}
