package dao;

import controller.Session;
import model.Reclamation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationDAO {

    public boolean creerReclamation(String objet, String description, int idUtilisateur) {
        String sql = "INSERT INTO reclamation (date_reclamation, objet, description, statut, id_personne) "
                   + "VALUES (CURRENT_DATE(), ?, ?, 'En attente', ?)";
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, objet);
            pst.setString(2, description);
            pst.setInt(3, idUtilisateur);
            pst.executeUpdate();
            ResultSet keys = pst.getGeneratedKeys();
            int idRec = 0;
            if (keys.next()) idRec = keys.getInt(1);
            ajouterHistorique(con, null, idRec, "Réclamation créée",
                    "Saisie par " + Session.getCurrentUserFullName());
            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur creerReclamation : " + e.getMessage());
            rollback(con);
            return false;
        } finally { resetAutoCommit(con); }
    }

    public boolean modifierStatut(int idReclamation, String nouveauStatut) {
        String sql = "UPDATE reclamation SET statut = ?, id_admin = ? WHERE id_reclamation = ?";
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, nouveauStatut);
            pst.setInt(2, Session.getCurrentUserId());
            pst.setInt(3, idReclamation);
            pst.executeUpdate();
            ajouterHistorique(con, null, idReclamation, "Statut modifié",
                    "Nouveau statut : " + nouveauStatut + " par " + Session.getCurrentUserFullName());
            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur modifierStatut : " + e.getMessage());
            rollback(con);
            return false;
        } finally { resetAutoCommit(con); }
    }

    public boolean supprimerReclamation(int idReclamation) {
        String sql1 = "UPDATE reclamation SET est_supprime=TRUE, statut='Archivée', "
                    + "date_suppression=CURRENT_DATE() WHERE id_reclamation=?";
        String sql2 = "INSERT INTO archive (date_archivage, type_element, id_reclamation) "
                    + "VALUES (CURRENT_DATE(), 'Réclamation', ?)";
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);
            PreparedStatement p1 = con.prepareStatement(sql1);
            p1.setInt(1, idReclamation); p1.executeUpdate();
            PreparedStatement p2 = con.prepareStatement(sql2);
            p2.setInt(1, idReclamation); p2.executeUpdate();
            ajouterHistorique(con, null, idReclamation, "Archivage",
                    "Archivée par " + Session.getCurrentUserFullName());
            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur supprimerReclamation : " + e.getMessage());
            rollback(con);
            return false;
        } finally { resetAutoCommit(con); }
    }

    public List<Reclamation> recupererReclamations() {
        List<Reclamation> liste = new ArrayList<>();
        String sql = "SELECT * FROM reclamation ORDER BY date_reclamation DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) liste.add(map(rs));
        } catch (SQLException e) {
            System.err.println("Erreur recupererReclamations : " + e.getMessage());
        }
        return liste;
    }

    public List<Reclamation> recupererReclamationsParUtilisateur(int idUtilisateur) {
        List<Reclamation> liste = new ArrayList<>();
        String sql = "SELECT * FROM reclamation WHERE id_personne = ? ORDER BY date_reclamation DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idUtilisateur);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) liste.add(map(rs));
        } catch (SQLException e) {
            System.err.println("Erreur recupererReclamationsParUtilisateur : " + e.getMessage());
        }
        return liste;
    }

    private Reclamation map(ResultSet rs) throws SQLException {
        Reclamation r = new Reclamation();
        r.setIdReclamation(rs.getInt("id_reclamation"));
        r.setDateReclamation(rs.getDate("date_reclamation"));
        r.setObjet(rs.getString("objet"));
        r.setDescription(rs.getString("description"));
        r.setStatut(rs.getString("statut"));
        r.setIdPersonne(rs.getInt("id_personne"));
        int idAdmin = rs.getInt("id_admin");
        r.setIdAdmin(rs.wasNull() ? null : idAdmin);
        return r;
    }

    private void ajouterHistorique(Connection con, Integer idDemande, Integer idReclamation,
                                    String action, String details) throws SQLException {
        String sql = "INSERT INTO historique_action (date_action, action, details, id_demande, id_reclamation) "
                   + "VALUES (NOW(), ?, ?, ?, ?)";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, action);
        pst.setString(2, details);
        if (idDemande     != null) pst.setInt(3, idDemande);     else pst.setNull(3, Types.INTEGER);
        if (idReclamation != null) pst.setInt(4, idReclamation); else pst.setNull(4, Types.INTEGER);
        pst.executeUpdate();
    }

    private void rollback(Connection con) {
        if (con != null) try { con.rollback(); } catch (SQLException ex) { /* ignore */ }
    }
    private void resetAutoCommit(Connection con) {
        if (con != null) try { con.setAutoCommit(true); } catch (SQLException ex) { /* ignore */ }
    }
}
