package dao;

import model.Archive;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArchiveDAO {

    public List<Archive> recupererDossiersArchives() {
        List<Archive> liste = new ArrayList<>();
        String sql = "SELECT * FROM archive ORDER BY date_archivage DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) liste.add(map(rs));
        } catch (SQLException e) {
            System.err.println("Erreur recupererArchives : " + e.getMessage());
        }
        return liste;
    }

    public List<Archive> rechercherArchives(String typeElement, Date dateDebut, Date dateFin) {
        List<Archive> liste = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM archive WHERE 1=1");
        if (typeElement != null && !typeElement.isEmpty()) sql.append(" AND type_element = ?");
        if (dateDebut   != null) sql.append(" AND date_archivage >= ?");
        if (dateFin     != null) sql.append(" AND date_archivage <= ?");
        sql.append(" ORDER BY date_archivage DESC");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (typeElement != null && !typeElement.isEmpty()) pst.setString(idx++, typeElement);
            if (dateDebut   != null) pst.setDate(idx++, new java.sql.Date(dateDebut.getTime()));
            if (dateFin     != null) pst.setDate(idx,   new java.sql.Date(dateFin.getTime()));
            ResultSet rs = pst.executeQuery();
            while (rs.next()) liste.add(map(rs));
        } catch (SQLException e) {
            System.err.println("Erreur rechercherArchives : " + e.getMessage());
        }
        return liste;
    }

    // Intitulé de l'élément archivé : type de la demande ou objet de la réclamation
    public String intituleElement(Archive a) {
        String sql;
        Integer id;
        if (a.getIdDemande() != null) {
            sql = "SELECT type_demande FROM demande WHERE id_demande = ?";
            id  = a.getIdDemande();
        } else if (a.getIdReclamation() != null) {
            sql = "SELECT objet FROM reclamation WHERE id_reclamation = ?";
            id  = a.getIdReclamation();
        } else {
            return "";
        }
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? rs.getString(1) : "";
            }
        } catch (SQLException e) {
            System.err.println("Erreur intituleElement : " + e.getMessage());
            return "";
        }
    }

    private Archive map(ResultSet rs) throws SQLException {
        Archive a = new Archive();
        a.setIdArchive(rs.getInt("id_archive"));
        a.setDateArchivage(rs.getDate("date_archivage"));
        a.setTypeElement(rs.getString("type_element"));
        int idD = rs.getInt("id_demande");    a.setIdDemande(rs.wasNull()    ? null : idD);
        int idR = rs.getInt("id_reclamation"); a.setIdReclamation(rs.wasNull() ? null : idR);
        return a;
    }
}
