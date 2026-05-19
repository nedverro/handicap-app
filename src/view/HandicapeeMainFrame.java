package view;

import controller.Session;
import controller.UIFactory;
import dao.DemandeDAO;
import model.Demande;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HandicapeeMainFrame extends JFrame {

    private JTable            table;
    private DefaultTableModel tableModel;
    private int               idSelectionne = -1;
    private final DemandeDAO  demandeDAO = new DemandeDAO();

    public HandicapeeMainFrame() {
        setTitle("UIR Handicap — Espace Étudiant");
        setSize(900, 620);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        // Navbar
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(new Color(11, 31, 58));
        navbar.setPreferredSize(new Dimension(900, 55));
        JLabel appName = new JLabel("  UIR Handicap  |  Espace Étudiant");
        appName.setFont(new Font("Calibri", Font.BOLD, 16));
        appName.setForeground(Color.WHITE);
        navbar.add(appName, BorderLayout.WEST);
        JButton btnDeco = UIFactory.creerBoutonDanger("Déconnexion");
        btnDeco.setPreferredSize(new Dimension(120, 35));
        btnDeco.addActionListener(e -> { Session.deconnecter(); new LoginFrame().setVisible(true); dispose(); });
        JPanel rn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rn.setOpaque(false);
        JLabel ul = new JLabel(Session.getCurrentUserFullName() + "   ");
        ul.setFont(new Font("Calibri", Font.PLAIN, 13)); ul.setForeground(new Color(123,147,176));
        rn.add(ul); rn.add(btnDeco);
        navbar.add(rn, BorderLayout.EAST);

        // Onglets
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Calibri", Font.PLAIN, 14));
        tabs.addTab("📋  Mes Demandes",    buildDemandesPanel());
        tabs.addTab("📣  Mes Réclamations", new ReclamationsPanel());

        setLayout(new BorderLayout());
        add(navbar, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
    }

    private JPanel buildDemandesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIFactory.COULEUR_FOND);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        header.setBackground(UIFactory.COULEUR_FOND);
        header.add(UIFactory.creerTitre("Mes Demandes d'Aménagement"));
        JButton btnNew = UIFactory.creerBoutonPrimaire("+ Nouvelle");
        btnNew.addActionListener(e -> ouvrirFormulaire(false));
        header.add(btnNew);
        JButton btnMod = UIFactory.creerBoutonPrimaire("✏️  Modifier");
        btnMod.addActionListener(e -> {
            if (idSelectionne < 0) { UIFactory.showToast("Sélectionnez une demande."); return; }
            ouvrirFormulaire(true);
        });
        header.add(btnMod);
        JButton btnR = UIFactory.creerBoutonPrimaire("↻  Actualiser");
        btnR.addActionListener(e -> charger());
        header.add(btnR);
        panel.add(header, BorderLayout.NORTH);

        String[] cols = {"ID", "Type", "Description", "Statut", "Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Calibri", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Calibri", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(21,101,192));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) idSelectionne = (int) tableModel.getValueAt(row, 0);
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        charger();
        return panel;
    }

    private void ouvrirFormulaire(boolean modification) {
        JDialog d = new JDialog(this, modification ? "Modifier la demande" : "Nouvelle Demande", true);
        d.setSize(480, 300); d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(5, 1, 5, 8));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(UIFactory.creerLabel("Type de demande :"));
        String[] types = {"Aménagement d'examen", "Aide technique", "Accessibilité", "Autre"};
        JComboBox<String> cbo = new JComboBox<>(types); p.add(cbo);
        p.add(UIFactory.creerLabel("Description :"));
        JTextArea txtDesc = new JTextArea(3, 30);
        txtDesc.setFont(new Font("Calibri", Font.PLAIN, 13));
        p.add(new JScrollPane(txtDesc));
        JButton btnOk = UIFactory.creerBoutonPrimaire(modification ? "Enregistrer" : "Soumettre");
        btnOk.addActionListener(e -> {
            String type = (String) cbo.getSelectedItem();
            String desc = txtDesc.getText().trim();
            if (desc.isEmpty()) { UIFactory.showToast("Description obligatoire."); return; }
            boolean ok;
            if (modification) {
                ok = demandeDAO.modifierDemande(idSelectionne, type, desc);
            } else {
                Demande dem = new Demande();
                dem.setTypeDemande(type); dem.setDescription(desc);
                dem.setIdPersonne(Session.getCurrentUserId());
                ok = demandeDAO.ajouterDemande(dem);
            }
            if (ok) { d.dispose(); charger(); UIFactory.showToast(modification ? "Demande modifiée." : "Demande envoyée."); }
        });
        p.add(btnOk);
        d.add(p); d.setVisible(true);
    }

    private void charger() {
        tableModel.setRowCount(0);
        List<Demande> liste = demandeDAO.listerDemandesParUtilisateur(Session.getCurrentUserId());
        for (Demande dem : liste) {
            tableModel.addRow(new Object[]{
                dem.getIdDemande(), dem.getTypeDemande(),
                dem.getDescription(), dem.getStatut(), dem.getDateCreation()
            });
        }
    }
}
