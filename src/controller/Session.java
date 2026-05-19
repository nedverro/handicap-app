package controller;

import model.Utilisateur;

public class Session {

    private static Utilisateur currentUser;

    private Session() {}

    public static void      setCurrentUser(Utilisateur u)  { currentUser = u; }
    public static Utilisateur getCurrentUser()             { return currentUser; }
    public static void      deconnecter()                  { currentUser = null; }

    public static boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }
    public static boolean isHandicapee() {
        return currentUser != null && "HANDICAPEE".equals(currentUser.getRole());
    }
    public static boolean isConnecte() { return currentUser != null; }

    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getIdUtilisateur() : -1;
    }
    public static String getCurrentUserFullName() {
        if (currentUser == null) return "Inconnu";
        return currentUser.getPrenom() + " " + currentUser.getNom();
    }
    public static String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : "";
    }
}
