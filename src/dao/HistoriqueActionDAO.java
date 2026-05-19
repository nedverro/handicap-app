package dao;

import model.HistoriqueAction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoriqueActionDAO {

    public List<HistoriqueAction> chargerHistoriqueDemande(int idDemande) {
        return charger("id_demande", idDemande);
    }

    public List<HistoriqueAction> chargerHistoriqueReclamation(int idReclamation) {
        return charger("id_reclamation", idReclamation);
    }

    private List<HistoriqueAction> charger(String colonne, int id) {
        List<HistoriqueAction> liste = new ArrayList<>();
        String sql = "SELECT * FROM historique_action WHERE " + colonne + " = ? ORDER BY date_action DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                HistoriqueAction h = new HistoriqueAction();
                h.setIdAction(rs.getInt("id_action"));
                h.setDateAction(rs.getTimestamp("date_action"));
                h.setAction(rs.getString("action"));
                h.setDetails(rs.getString("details"));
                int idD = rs.getInt("id_demande");    h.setIdDemande(rs.wasNull()    ? null : idD);
                int idR = rs.getInt("id_reclamation"); h.setIdReclamation(rs.wasNull() ? null : idR);
                liste.add(h);
            }
        } catch (SQLException e) {
            System.err.println("Erreur chargerHistorique : " + e.getMessage());
        }
        return liste;
    }
}
