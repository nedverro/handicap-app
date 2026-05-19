package dao;

import controller.Session;
import model.Demande;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DemandeDAO {

    // ── Ajouter (avec transaction + historique) ───────────────────────────
    public boolean ajouterDemande(Demande d) {
        String sql = "INSERT INTO demande (date_creation, type_demande, description, statut, id_personne) "
                   + "VALUES (CURRENT_DATE(), ?, ?, 'En cours', ?)";
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, d.getTypeDemande());
            pst.setString(2, d.getDescription());
            pst.setInt(3, d.getIdPersonne());
            pst.executeUpdate();

            ResultSet keys = pst.getGeneratedKeys();
            int idDemande = 0;
            if (keys.next()) { idDemande = keys.getInt(1); d.setIdDemande(idDemande); }

            ajouterHistorique(con, idDemande, null, "Création demande",
                    "Créée par " + Session.getCurrentUserFullName());

            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur ajouterDemande : " + e.getMessage());
            rollback(con);
            return false;
        } finally { resetAutoCommit(con); }
    }

    // ── Modifier ──────────────────────────────────────────────────────────
    public boolean modifierDemande(int idDemande, String typeDemande, String description) {
        String sql = "UPDATE demande SET type_demande = ?, description = ? WHERE id_demande = ?";
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, typeDemande);
            pst.setString(2, description);
            pst.setInt(3, idDemande);
            pst.executeUpdate();
            ajouterHistorique(con, idDemande, null, "Modification",
                    "Modifiée par " + Session.getCurrentUserFullName());
            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur modifierDemande : " + e.getMessage());
            rollback(con);
            return false;
        } finally { resetAutoCommit(con); }
    }

    // ── Modifier le statut ────────────────────────────────────────────────
    public boolean modifierStatutDemande(int idDemande, String nouveauStatut) {
        String sql = "UPDATE demande SET statut = ?, id_admin = ? WHERE id_demande = ?";
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, nouveauStatut);
            pst.setInt(2, Session.getCurrentUserId());
            pst.setInt(3, idDemande);
            pst.executeUpdate();
            ajouterHistorique(con, idDemande, null, "Statut modifié",
                    "Nouveau statut : " + nouveauStatut + " par " + Session.getCurrentUserFullName());
            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur modifierStatut : " + e.getMessage());
            rollback(con);
            return false;
        } finally { resetAutoCommit(con); }
    }

    // ── Supprimer (soft delete) ───────────────────────────────────────────
    public boolean supprimerDemande(int idDemande) {
        String sql1 = "UPDATE demande SET statut='Archivée', est_supprime=TRUE, "
                    + "date_suppression=CURRENT_DATE() WHERE id_demande=?";
        String sql2 = "INSERT INTO archive (date_archivage, type_element, id_demande) "
                    + "VALUES (CURRENT_DATE(), 'Demande', ?)";
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);
            PreparedStatement p1 = con.prepareStatement(sql1);
            p1.setInt(1, idDemande); p1.executeUpdate();
            PreparedStatement p2 = con.prepareStatement(sql2);
            p2.setInt(1, idDemande); p2.executeUpdate();
            ajouterHistorique(con, idDemande, null, "Archivage",
                    "Archivée par " + Session.getCurrentUserFullName());
            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur supprimerDemande : " + e.getMessage());
            rollback(con);
            return false;
        } finally { resetAutoCommit(con); }
    }

    // ── Lister par utilisateur ────────────────────────────────────────────
    public List<Demande> listerDemandesParUtilisateur(int idUtilisateur) {
        List<Demande> liste = new ArrayList<>();
        String sql = "SELECT * FROM demande WHERE id_personne = ? "
                   + "AND (est_supprime = FALSE OR est_supprime IS NULL) ORDER BY date_creation DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idUtilisateur);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) liste.add(map(rs));
        } catch (SQLException e) {
            System.err.println("Erreur listerDemandes : " + e.getMessage());
        }
        return liste;
    }

    // ── Lister toutes (admin) ─────────────────────────────────────────────
    public List<Demande> listerToutesLesDemandes() {
        List<Demande> liste = new ArrayList<>();
        String sql = "SELECT * FROM demande ORDER BY date_creation DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) liste.add(map(rs));
        } catch (SQLException e) {
            System.err.println("Erreur listerToutesDemandes : " + e.getMessage());
        }
        return liste;
    }

    // ── Compter par statut (dashboard) ────────────────────────────────────
    public int compterDemandesParStatut(String statut) {
        String sql = "SELECT COUNT(*) FROM demande WHERE statut = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, statut);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Erreur compterDemandes : " + e.getMessage());
        }
        return 0;
    }

    // ── Mapping ResultSet → Demande ───────────────────────────────────────
    private Demande map(ResultSet rs) throws SQLException {
        Demande d = new Demande();
        d.setIdDemande(rs.getInt("id_demande"));
        d.setDateCreation(rs.getDate("date_creation"));
        d.setTypeDemande(rs.getString("type_demande"));
        d.setDescription(rs.getString("description"));
        d.setStatut(rs.getString("statut"));
        d.setIdPersonne(rs.getInt("id_personne"));
        int idAdmin = rs.getInt("id_admin");
        d.setIdAdmin(rs.wasNull() ? null : idAdmin);
        return d;
    }

    // ── Historique interne (réutilisé dans les transactions) ──────────────
    private void ajouterHistorique(Connection con, Integer idDemande, Integer idReclamation,
                                    String action, String details) throws SQLException {
        String sql = "INSERT INTO historique_action (date_action, action, details, id_demande, id_reclamation) "
                   + "VALUES (NOW(), ?, ?, ?, ?)";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, action);
        pst.setString(2, details);
        if (idDemande    != null) pst.setInt(3, idDemande);    else pst.setNull(3, Types.INTEGER);
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
