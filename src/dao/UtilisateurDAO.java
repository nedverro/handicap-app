package dao;

import controller.PasswordUtil;
import model.Utilisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    // ── Authentification ──────────────────────────────────────────────────
    public Utilisateur seConnecter(String email, String motDePasse) {
        String sql = "SELECT * FROM utilisateur WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("mot_de_passe");
                if (!PasswordUtil.verifyPassword(motDePasse, storedHash)) {
                    return null;
                }
                Utilisateur u = new Utilisateur();
                u.setIdUtilisateur(rs.getInt("id_utilisateur"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                u.setEmail(rs.getString("email"));
                u.setRole(rs.getString("role"));
                return u;
            }
        } catch (SQLException e) {
            System.err.println("Erreur seConnecter : " + e.getMessage());
        }
        return null;
    }

    // ── Créer un utilisateur ──────────────────────────────────────────────
    public boolean creerUtilisateur(Utilisateur u) {
        String sql = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, u.getNom());
            pst.setString(2, u.getPrenom());
            pst.setString(3, u.getEmail());
            pst.setString(4, PasswordUtil.hashPassword(u.getMotDePasse()));
            pst.setString(5, u.getRole());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                ResultSet keys = pst.getGeneratedKeys();
                if (keys.next()) u.setIdUtilisateur(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur creerUtilisateur : " + e.getMessage());
        }
        return false;
    }

    // ── Lister tous les utilisateurs ──────────────────────────────────────
    public List<Utilisateur> listerUtilisateurs() {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur ORDER BY nom, prenom";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Utilisateur u = new Utilisateur();
                u.setIdUtilisateur(rs.getInt("id_utilisateur"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                u.setEmail(rs.getString("email"));
                u.setRole(rs.getString("role"));
                liste.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Erreur listerUtilisateurs : " + e.getMessage());
        }
        return liste;
    }

    // ── Modifier le rôle ─────────────────────────────────────────────────
    public boolean modifierRole(int idUtilisateur, String nouveauRole) {
        String sql = "UPDATE utilisateur SET role = ? WHERE id_utilisateur = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, nouveauRole);
            pst.setInt(2, idUtilisateur);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur modifierRole : " + e.getMessage());
        }
        return false;
    }
}
