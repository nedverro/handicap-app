package view;

import controller.Session;
import controller.UIFactory;
import dao.DemandeDAO;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final DemandeDAO demandeDAO = new DemandeDAO();

    public MainFrame() {
        setTitle("UIR Handicap — Espace Administrateur");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        // Navbar
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(new Color(11, 31, 58));
        navbar.setPreferredSize(new Dimension(1100, 55));

        JLabel appName = new JLabel("  UIR Handicap  |  Administrateur");
        appName.setFont(new Font("Calibri", Font.BOLD, 16));
        appName.setForeground(Color.WHITE);
        navbar.add(appName, BorderLayout.WEST);

        JButton btnDeco = UIFactory.creerBoutonDanger("Déconnexion");
        btnDeco.setPreferredSize(new Dimension(120, 35));
        btnDeco.addActionListener(e -> deconnecter());

        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightNav.setOpaque(false);
        JLabel userInfo = new JLabel(Session.getCurrentUserFullName() + "   ");
        userInfo.setFont(new Font("Calibri", Font.PLAIN, 13));
        userInfo.setForeground(new Color(123, 147, 176));
        rightNav.add(userInfo);
        rightNav.add(btnDeco);
        navbar.add(rightNav, BorderLayout.EAST);

        // Onglets
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Calibri", Font.PLAIN, 14));
        tabs.addTab("📊  Tableau de Bord",   buildDashboard());
        tabs.addTab("📋  Demandes",           new DemandesPanel());
        tabs.addTab("📣  Réclamations",       new ReclamationsPanel());
        tabs.addTab("🗂️  Archives",           buildPlaceholder("Archives & Recherche multicritère"));

        setLayout(new BorderLayout());
        add(navbar, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
    }

    private JPanel buildDashboard() {
        JPanel p = new JPanel(new GridLayout(2, 3, 20, 20));
        p.setBackground(UIFactory.COULEUR_FOND);
        p.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        p.add(statCard("En cours",  demandeDAO.compterDemandesParStatut("En cours"),  new Color(33,150,243)));
        p.add(statCard("Acceptées", demandeDAO.compterDemandesParStatut("Acceptée"), new Color(0,191,165)));
        p.add(statCard("Refusées",  demandeDAO.compterDemandesParStatut("Refusée"),  new Color(229,57,53)));
        p.add(statCard("Archivées", demandeDAO.compterDemandesParStatut("Archivée"), new Color(120,144,156)));
        int total = demandeDAO.compterDemandesParStatut("En cours")
                  + demandeDAO.compterDemandesParStatut("Acceptée")
                  + demandeDAO.compterDemandesParStatut("Refusée");
        p.add(statCard("Total demandes", total, new Color(21,101,192)));
        return p;
    }

    private JPanel statCard(String label, int val, Color couleur) {
        JPanel c = new JPanel(new GridLayout(2, 1));
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, couleur));
        JLabel lv = new JLabel(String.valueOf(val), SwingConstants.CENTER);
        lv.setFont(new Font("Calibri", Font.BOLD, 42)); lv.setForeground(couleur);
        JLabel ll = new JLabel(label, SwingConstants.CENTER);
        ll.setFont(new Font("Calibri", Font.PLAIN, 14)); ll.setForeground(UIFactory.COULEUR_TEXTE);
        c.add(lv); c.add(ll);
        return c;
    }

    private JPanel buildPlaceholder(String txt) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIFactory.COULEUR_FOND);
        JLabel l = new JLabel(txt, SwingConstants.CENTER);
        l.setFont(new Font("Calibri", Font.ITALIC, 16));
        l.setForeground(new Color(123, 147, 176));
        p.add(l);
        return p;
    }

    private void deconnecter() {
        int c = JOptionPane.showConfirmDialog(this, "Voulez-vous vous déconnecter ?",
                "Déconnexion", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            Session.deconnecter();
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
}
