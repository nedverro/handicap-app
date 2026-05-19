package view;

import controller.Session;
import controller.UIFactory;
import dao.UtilisateurDAO;
import model.Utilisateur;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {

    private static final Color C_NAVY   = new Color(11,  31,  58);
    private static final Color C_BLUE   = new Color(21, 101, 192);
    private static final Color C_MINT   = new Color(0,  191, 165);
    private static final Color C_WHITE  = Color.WHITE;
    private static final Color C_BG     = new Color(240, 244, 249);
    private static final Color C_MUTED  = new Color(123, 147, 176);
    private static final Color C_RED    = new Color(229,  57,  53);
    private static final Color C_BORDER = new Color(180, 206, 230);

    private JTextField     txtEmail;
    private JPasswordField txtPassword;
    private JLabel         lblError;
    private JButton        btnTogglePwd;
    private JButton        btnConnexion;
    private boolean        pwdVisible = false;

    private final UtilisateurDAO dao = new UtilisateurDAO();

    public LoginFrame() {
        setTitle("UIR Handicap — Connexion");
        setUndecorated(true);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        try { setShape(new RoundRectangle2D.Double(0, 0, 900, 560, 20, 20)); }
        catch (UnsupportedOperationException ignored) {}
        initUI();
        enableDrag();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_WHITE);
        root.add(buildLeft(),  BorderLayout.WEST);
        root.add(buildRight(), BorderLayout.CENTER);
        add(root);
    }

    // ── Panneau gauche navy ───────────────────────────────────────────────
    private JPanel buildLeft() {
        JPanel p = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_NAVY);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Formes géométriques décoratives
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(new Color(255, 255, 255, 18));
                int[][] lines = {{30,60,220,280},{90,30,290,190},{50,130,240,330},{120,190,300,110},{70,250,260,75}};
                for (int[] l : lines) g2.drawLine(l[0],l[1],l[2],l[3]);
                // Barre mint en bas
                g2.setColor(C_MINT);
                g2.fillRect(0, getHeight()-5, getWidth(), 5);
                g2.dispose();
            }
        };
        p.setPreferredSize(new Dimension(360, 560));
        p.setOpaque(true);

        JLabel ico = new JLabel("♿", SwingConstants.CENTER);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        ico.setForeground(C_MINT);
        ico.setBounds(0, 70, 360, 72);
        p.add(ico);

        JLabel titre = new JLabel("<html><center>UIR<br>Handicap</center></html>", SwingConstants.CENTER);
        titre.setFont(new Font("Calibri", Font.BOLD, 40));
        titre.setForeground(C_WHITE);
        titre.setBounds(0, 145, 360, 105);
        p.add(titre);

        JPanel sep = new JPanel(); sep.setBackground(C_MINT); sep.setBounds(130,258,100,3); p.add(sep);

        JLabel sub = new JLabel("<html><center>Gestion de Demandes<br>&amp; Réclamations</center></html>", SwingConstants.CENTER);
        sub.setFont(new Font("Calibri", Font.ITALIC, 13));
        sub.setForeground(C_MUTED);
        sub.setBounds(30, 268, 300, 52);
        p.add(sub);

        String[] techs = {"Java Swing", "MySQL", "MVC"};
        Color[]  cols  = {C_BLUE, C_MINT, new Color(90,130,195)};
        for (int i = 0; i < techs.length; i++) {
            JLabel chip = new JLabel(techs[i], SwingConstants.CENTER);
            chip.setFont(new Font("Calibri", Font.BOLD, 11));
            chip.setForeground(C_WHITE); chip.setOpaque(true); chip.setBackground(cols[i]);
            chip.setBorder(BorderFactory.createEmptyBorder(4,10,4,10));
            chip.setBounds(30+i*108, 348, 95, 25);
            p.add(chip);
        }

        JLabel uir = new JLabel("Université Internationale de Rabat", SwingConstants.CENTER);
        uir.setFont(new Font("Calibri", Font.PLAIN, 11));
        uir.setForeground(new Color(70,90,120));
        uir.setBounds(0, 518, 360, 20);
        p.add(uir);

        return p;
    }

    // ── Panneau droit formulaire ──────────────────────────────────────────
    private JPanel buildRight() {
        JPanel p = new JPanel(null);
        p.setBackground(C_BG);

        // Bouton ×
        JButton btnX = makeX();
        btnX.setBounds(496, 12, 36, 36);
        p.add(btnX);

        // Titre
        JLabel welcome = new JLabel("Bienvenue !");
        welcome.setFont(new Font("Calibri", Font.BOLD, 30));
        welcome.setForeground(C_NAVY);
        welcome.setBounds(58, 58, 300, 40);
        p.add(welcome);

        JLabel sub2 = new JLabel("Connectez-vous à votre espace personnel");
        sub2.setFont(new Font("Calibri", Font.PLAIN, 13));
        sub2.setForeground(C_MUTED);
        sub2.setBounds(58, 98, 340, 22);
        p.add(sub2);

        JPanel accent = new JPanel(); accent.setBackground(C_BLUE);
        accent.setBounds(58, 128, 45, 3); p.add(accent);

        // Email
        JLabel lEmail = fieldLabel("Adresse email");
        lEmail.setBounds(58, 152, 250, 22); p.add(lEmail);
        txtEmail = new JTextField();
        styleField(txtEmail);
        txtEmail.setBounds(58, 176, 365, 44);
        txtEmail.addActionListener(e -> txtPassword.requestFocus());
        p.add(txtEmail);

        // Mot de passe
        JLabel lPwd = fieldLabel("Mot de passe");
        lPwd.setBounds(58, 236, 250, 22); p.add(lPwd);

        JPanel pwdWrap = new JPanel(null);
        pwdWrap.setBounds(58, 260, 365, 44);
        pwdWrap.setBackground(C_WHITE);
        pwdWrap.setBorder(new RoundBorder(C_BORDER, 8));

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Calibri", Font.PLAIN, 15));
        txtPassword.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));
        txtPassword.setBackground(C_WHITE);
        txtPassword.setBounds(0, 0, 318, 44);
        txtPassword.addActionListener(e -> doLogin());
        pwdWrap.add(txtPassword);

        btnTogglePwd = new JButton("👁");
        btnTogglePwd.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        btnTogglePwd.setContentAreaFilled(false); btnTogglePwd.setBorderPainted(false);
        btnTogglePwd.setFocusPainted(false);
        btnTogglePwd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTogglePwd.setBounds(320, 10, 40, 24);
        btnTogglePwd.addActionListener(e -> togglePwd());
        pwdWrap.add(btnTogglePwd);
        p.add(pwdWrap);

        // Erreur
        lblError = new JLabel("");
        lblError.setFont(new Font("Calibri", Font.ITALIC, 12));
        lblError.setForeground(C_RED);
        lblError.setBounds(58, 312, 365, 22); p.add(lblError);

        // Bouton connexion
        btnConnexion = buildLoginBtn();
        btnConnexion.setBounds(58, 342, 365, 48); p.add(btnConnexion);

        // Séparateur
        JPanel sepH = new JPanel(); sepH.setBackground(new Color(210,222,236));
        sepH.setBounds(58, 406, 365, 1); p.add(sepH);

        // Comptes démo
        JLabel lHelp = new JLabel("Comptes de démonstration :");
        lHelp.setFont(new Font("Calibri", Font.BOLD, 12));
        lHelp.setForeground(C_MUTED);
        lHelp.setBounds(58, 418, 365, 20); p.add(lHelp);

        p.add(demoLabel("🔐  Admin     :  admin@uir.ac.ma   /  admin123",    "admin@uir.ac.ma",  "admin123",   58, 440));
        p.add(demoLabel("🎓  Étudiant  :  ilias@uir.ac.ma   /  etudiant123", "ilias@uir.ac.ma",  "etudiant123", 58, 460));

        JLabel ver = new JLabel("v1.0  —  UIR Handicap  2024–2025");
        ver.setFont(new Font("Calibri", Font.PLAIN, 10));
        ver.setForeground(new Color(180,195,210));
        ver.setBounds(58, 530, 365, 18); p.add(ver);

        return p;
    }

    // ── Authentification ──────────────────────────────────────────────────
    private void doLogin() {
        String email = txtEmail.getText().trim();
        String pwd   = new String(txtPassword.getPassword()).trim();

        if (email.isEmpty() || pwd.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            shake(); return;
        }

        btnConnexion.setEnabled(false);
        lblError.setForeground(C_MUTED);
        lblError.setText("Vérification...");

        new SwingWorker<Utilisateur, Void>() {
            @Override protected Utilisateur doInBackground() { return dao.seConnecter(email, pwd); }
            @Override protected void done() {
                try {
                    Utilisateur user = get();
                    if (user != null && "ADMIN".equals(user.getRole())) {
                        Session.setCurrentUser(user);
                        new MainFrame().setVisible(true);
                        dispose();
                    } else if (user != null && "HANDICAPEE".equals(user.getRole())) {
                        Session.setCurrentUser(user);
                        new HandicapeeMainFrame().setVisible(true);
                        dispose();
                    } else {
                        showError("Email ou mot de passe incorrect.");
                        txtPassword.setText("");
                        shake();
                        btnConnexion.setEnabled(true);
                    }
                } catch (Exception ex) {
                    showError("Erreur : " + ex.getMessage());
                    btnConnexion.setEnabled(true);
                }
            }
        }.execute();
    }

    private void togglePwd() {
        pwdVisible = !pwdVisible;
        txtPassword.setEchoChar(pwdVisible ? (char) 0 : '•');
        btnTogglePwd.setText(pwdVisible ? "🙈" : "👁");
    }

    private void shake() {
        Point o = getLocation();
        int[] dx = {9,-9,7,-7,5,-5,3,-3,0};
        Timer t = new Timer(28, null);
        t.addActionListener(new ActionListener() {
            int i = 0;
            public void actionPerformed(ActionEvent e) {
                if (i < dx.length) setLocation(o.x+dx[i++], o.y);
                else { setLocation(o); t.stop(); }
            }
        });
        t.start();
    }

    private void showError(String msg) {
        lblError.setForeground(C_RED);
        lblError.setText("⚠  " + msg);
    }

    private void enableDrag() {
        int[] ds = {0, 0};
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { ds[0]=e.getX(); ds[1]=e.getY(); }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(getX()+e.getX()-ds[0], getY()+e.getY()-ds[1]);
            }
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private JLabel fieldLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Calibri", Font.BOLD, 13));
        l.setForeground(C_NAVY);
        return l;
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("Calibri", Font.PLAIN, 14));
        f.setBackground(C_WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(C_BORDER, 8),
            BorderFactory.createEmptyBorder(0, 14, 0, 14)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    new RoundBorder(C_BLUE, 8), BorderFactory.createEmptyBorder(0,14,0,14)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    new RoundBorder(C_BORDER, 8), BorderFactory.createEmptyBorder(0,14,0,14)));
            }
        });
    }

    private JButton buildLoginBtn() {
        JButton btn = new JButton("Se connecter") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isEnabled())                g2.setColor(new Color(160,190,220));
                else if (getModel().isPressed()) g2.setColor(new Color(13,71,161));
                else if (getModel().isRollover()) g2.setColor(new Color(25,118,210));
                else                              g2.setColor(C_BLUE);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(C_WHITE);
                g2.setFont(new Font("Calibri", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,
                              (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> doLogin());
        return btn;
    }

    private JLabel demoLabel(String text, String email, String pwd, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Calibri", Font.PLAIN, 11));
        l.setForeground(C_BLUE);
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.setBounds(x, y, 365, 18);
        l.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { txtEmail.setText(email); txtPassword.setText(pwd); lblError.setText(""); }
            public void mouseEntered(MouseEvent e) { l.setForeground(C_MINT); }
            public void mouseExited(MouseEvent e)  { l.setForeground(C_BLUE); }
        });
        return l;
    }

    private JButton makeX() {
        JButton b = new JButton("×");
        b.setFont(new Font("Calibri", Font.PLAIN, 22)); b.setForeground(C_MUTED);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> System.exit(0));
        return b;
    }

    // ── Bordure arrondie ──────────────────────────────────────────────────
    static class RoundBorder extends AbstractBorder {
        private final Color color; private final int radius;
        RoundBorder(Color c, int r) { color=c; radius=r; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.drawRoundRect(x,y,w-1,h-1,radius,radius); g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(4,4,4,4); }
        @Override public Insets getBorderInsets(Component c, Insets i) { i.set(4,4,4,4); return i; }
    }
}
