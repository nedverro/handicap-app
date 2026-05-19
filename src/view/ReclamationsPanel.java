package view;

import controller.Session;
import controller.UIFactory;
import dao.ReclamationDAO;
import model.Reclamation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReclamationsPanel extends JPanel {

    private JTable              table;
    private DefaultTableModel   tableModel;
    private int                 idSelectionne = -1;
    private final ReclamationDAO reclamationDAO = new ReclamationDAO();

    public ReclamationsPanel() {
        setLayout(new BorderLayout());
        setBackground(UIFactory.COULEUR_FOND);
        initUI();
        charger();
    }

    private void initUI() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        header.setBackground(UIFactory.COULEUR_FOND);
        header.add(UIFactory.creerTitre("Gestion des Réclamations"));

        if (Session.isHandicapee()) {
            JButton btnNew = UIFactory.creerBoutonPrimaire("+ Nouvelle");
            btnNew.addActionListener(e -> ouvrirFormulaire());
            header.add(btnNew);
        }
        JButton btnR = UIFactory.creerBoutonPrimaire("↻  Actualiser");
        btnR.addActionListener(e -> charger());
        header.add(btnR);
        add(header, BorderLayout.NORTH);

        String[] cols = {"ID", "Objet", "Description", "Statut", "Date"};
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
        if (Session.isAdmin()) table.setComponentPopupMenu(buildMenuAdmin());
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private JPopupMenu buildMenuAdmin() {
        JPopupMenu pop = new JPopupMenu();
        JMenuItem traiter = new JMenuItem("✏️  Traiter");
        traiter.addActionListener(e -> {
            if (idSelectionne < 0) return;
            String[] statuts = {"En cours", "Résolue"};
            String s = (String) JOptionPane.showInputDialog(this, "Nouveau statut :",
                    "Traiter", JOptionPane.PLAIN_MESSAGE, null, statuts, statuts[0]);
            if (s != null) {
                boolean ok = reclamationDAO.modifierStatut(idSelectionne, s);
                if (ok) { charger(); UIFactory.showToast("Statut mis à jour."); }
            }
        });
        pop.add(traiter);
        JMenuItem archiver = new JMenuItem("🗂️  Archiver");
        archiver.addActionListener(e -> {
            if (idSelectionne < 0) return;
            if (JOptionPane.showConfirmDialog(this, "Archiver la réclamation ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                boolean ok = reclamationDAO.supprimerReclamation(idSelectionne);
                if (ok) { charger(); UIFactory.showToast("Réclamation archivée."); }
            }
        });
        pop.add(archiver);
        return pop;
    }

    private void ouvrirFormulaire() {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nouvelle Réclamation", true);
        d.setSize(450, 280); d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(5, 1, 5, 8));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(UIFactory.creerLabel("Objet :"));
        JTextField txtObjet = UIFactory.creerChampTexte(30); p.add(txtObjet);
        p.add(UIFactory.creerLabel("Description :"));
        JTextArea txtDesc = new JTextArea(3, 30);
        txtDesc.setFont(new Font("Calibri", Font.PLAIN, 13));
        p.add(new JScrollPane(txtDesc));
        JButton btnOk = UIFactory.creerBoutonPrimaire("Soumettre");
        btnOk.addActionListener(e -> {
            String objet = txtObjet.getText().trim();
            String desc  = txtDesc.getText().trim();
            if (objet.isEmpty() || desc.isEmpty()) { UIFactory.showToast("Champs obligatoires."); return; }
            boolean ok = reclamationDAO.creerReclamation(objet, desc, Session.getCurrentUserId());
            if (ok) { d.dispose(); charger(); UIFactory.showToast("Réclamation envoyée."); }
        });
        p.add(btnOk);
        d.add(p); d.setVisible(true);
    }

    private void charger() {
        tableModel.setRowCount(0);
        List<Reclamation> liste = Session.isAdmin()
                ? reclamationDAO.recupererReclamations()
                : reclamationDAO.recupererReclamationsParUtilisateur(Session.getCurrentUserId());
        for (Reclamation r : liste) {
            tableModel.addRow(new Object[]{
                r.getIdReclamation(), r.getObjet(), r.getDescription(),
                r.getStatut(), r.getDateReclamation()
            });
        }
    }
}
