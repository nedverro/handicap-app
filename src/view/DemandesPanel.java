package view;

import controller.Session;
import controller.UIFactory;
import dao.DemandeDAO;
import model.Demande;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DemandesPanel extends JPanel {

    private JTable            table;
    private DefaultTableModel tableModel;
    private int               idSelectionne = -1;
    private final DemandeDAO  demandeDAO = new DemandeDAO();

    public DemandesPanel() {
        setLayout(new BorderLayout());
        setBackground(UIFactory.COULEUR_FOND);
        initUI();
        charger();
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        header.setBackground(UIFactory.COULEUR_FOND);
        header.add(UIFactory.creerTitre("Gestion des Demandes"));
        JButton btnRefresh = UIFactory.creerBoutonPrimaire("↻  Actualiser");
        btnRefresh.addActionListener(e -> charger());
        header.add(btnRefresh);
        add(header, BorderLayout.NORTH);

        // Tableau
        String[] cols = {"ID", "Type", "Description", "Statut", "Date", "Étudiant ID"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Calibri", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Calibri", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(21, 101, 192));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) idSelectionne = (int) tableModel.getValueAt(row, 0);
        });
        table.setComponentPopupMenu(buildMenu());
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private JPopupMenu buildMenu() {
        JPopupMenu pop = new JPopupMenu();

        // Changer statut
        JMenuItem changer = new JMenuItem("✏️  Changer le statut");
        changer.addActionListener(e -> {
            if (idSelectionne < 0) return;
            String[] statuts = {"En cours", "Acceptée", "Refusée"};
            String s = (String) JOptionPane.showInputDialog(this,
                    "Choisir le nouveau statut :", "Modifier le statut",
                    JOptionPane.PLAIN_MESSAGE, null, statuts, statuts[0]);
            if (s != null) {
                boolean ok = demandeDAO.modifierStatutDemande(idSelectionne, s);
                if (ok) { charger(); UIFactory.showToast("Statut mis à jour."); }
            }
        });
        pop.add(changer);
        pop.addSeparator();

        // Archiver
        JMenuItem archiver = new JMenuItem("🗂️  Archiver");
        archiver.addActionListener(e -> {
            if (idSelectionne < 0) return;
            if (JOptionPane.showConfirmDialog(this, "Archiver la demande #" + idSelectionne + " ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                boolean ok = demandeDAO.supprimerDemande(idSelectionne);
                if (ok) { charger(); UIFactory.showToast("Demande archivée."); }
            }
        });
        pop.add(archiver);
        return pop;
    }

    private void charger() {
        tableModel.setRowCount(0);
        List<Demande> liste = Session.isAdmin()
                ? demandeDAO.listerToutesLesDemandes()
                : demandeDAO.listerDemandesParUtilisateur(Session.getCurrentUserId());
        for (Demande d : liste) {
            tableModel.addRow(new Object[]{
                d.getIdDemande(), d.getTypeDemande(), d.getDescription(),
                d.getStatut(), d.getDateCreation(), d.getIdPersonne()
            });
        }
    }
}
