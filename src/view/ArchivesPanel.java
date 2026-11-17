package view;

import controller.UIFactory;
import dao.ArchiveDAO;
import model.Archive;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Onglet "Archives" de l'administrateur.
 * Affiche les demandes et réclamations archivées, avec une recherche
 * multicritère : type d'élément + période (date de début / date de fin).
 */
public class ArchivesPanel extends JPanel {

    private final ArchiveDAO archiveDAO = new ArchiveDAO();

    private DefaultTableModel tableModel;
    private JComboBox<String> cbType;
    private JTextField        txtDebut;
    private JTextField        txtFin;
    private JLabel            lblResultat;

    // Format de saisie des dates dans les filtres
    private static final String FORMAT_DATE = "yyyy-MM-dd";

    public ArchivesPanel() {
        setLayout(new BorderLayout());
        setBackground(UIFactory.COULEUR_FOND);
        initUI();
        charger(); // au démarrage : toutes les archives
    }

    private void initUI() {
        // ── En-tête : titre + filtres ─────────────────────────────────────
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        header.setBackground(UIFactory.COULEUR_FOND);
        header.add(UIFactory.creerTitre("Archives"));

        header.add(UIFactory.creerLabel("Type :"));
        cbType = new JComboBox<>(new String[]{"Tous", "Demande", "Réclamation"});
        header.add(cbType);

        header.add(UIFactory.creerLabel("Du :"));
        txtDebut = UIFactory.creerChampTexte(8);
        txtDebut.setToolTipText("Date de début (" + FORMAT_DATE + ")");
        header.add(txtDebut);

        header.add(UIFactory.creerLabel("Au :"));
        txtFin = UIFactory.creerChampTexte(8);
        txtFin.setToolTipText("Date de fin (" + FORMAT_DATE + ")");
        header.add(txtFin);

        JButton btnRechercher = UIFactory.creerBoutonPrimaire("🔍  Rechercher");
        btnRechercher.addActionListener(e -> rechercher());
        header.add(btnRechercher);

        JButton btnReset = UIFactory.creerBoutonPrimaire("↻  Tout afficher");
        btnReset.addActionListener(e -> {
            cbType.setSelectedIndex(0);
            txtDebut.setText("");
            txtFin.setText("");
            charger();
        });
        header.add(btnReset);
        add(header, BorderLayout.NORTH);

        // ── Tableau des archives (lecture seule) ──────────────────────────
        String[] cols = {"N° archive", "Date d'archivage", "Type", "ID élément", "Intitulé"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Calibri", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Calibri", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(21, 101, 192));
        table.getTableHeader().setForeground(Color.WHITE);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ── Pied : nombre de résultats ────────────────────────────────────
        lblResultat = UIFactory.creerLabel(" ");
        lblResultat.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        add(lblResultat, BorderLayout.SOUTH);
    }

    /** Charge toutes les archives, sans filtre. */
    private void charger() {
        afficher(archiveDAO.recupererDossiersArchives());
    }

    /** Lit les filtres, les valide, puis lance la recherche multicritère. */
    private void rechercher() {
        String type = cbType.getSelectedIndex() == 0 ? null : (String) cbType.getSelectedItem();
        Date debut, fin;
        try {
            debut = lireDate(txtDebut.getText());
            fin   = lireDate(txtFin.getText());
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Format de date invalide. Utilisez " + FORMAT_DATE + " (ex : 2026-05-01).",
                    "Recherche", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (debut != null && fin != null && debut.after(fin)) {
            JOptionPane.showMessageDialog(this,
                    "La date de début doit être avant la date de fin.",
                    "Recherche", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Le DAO attend des java.sql.Date
        afficher(archiveDAO.rechercherArchives(type,
                debut == null ? null : new java.sql.Date(debut.getTime()),
                fin   == null ? null : new java.sql.Date(fin.getTime())));
    }

    /** Convertit le texte saisi en date (champ vide = pas de filtre). */
    private Date lireDate(String texte) throws ParseException {
        if (texte == null || texte.trim().isEmpty()) return null;
        SimpleDateFormat f = new SimpleDateFormat(FORMAT_DATE);
        f.setLenient(false); // refuse les dates impossibles (ex : 2026-02-31)
        return f.parse(texte.trim());
    }

    /** Remplit le tableau avec la liste d'archives donnée. */
    private void afficher(List<Archive> archives) {
        tableModel.setRowCount(0);
        for (Archive a : archives) {
            Integer idElement = a.getIdDemande() != null ? a.getIdDemande() : a.getIdReclamation();
            tableModel.addRow(new Object[]{
                    a.getIdArchive(),
                    a.getDateArchivage(),
                    a.getTypeElement(),
                    idElement,
                    archiveDAO.intituleElement(a)
            });
        }
        lblResultat.setText(archives.size() + " élément(s) archivé(s)");
    }
}
