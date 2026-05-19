package controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class UIFactory {

    public static final Color COULEUR_PRIMAIRE = new Color(21,  101, 192);
    public static final Color COULEUR_ACCENT   = new Color(0,   191, 165);
    public static final Color COULEUR_FOND     = new Color(240, 244, 249);
    public static final Color COULEUR_TEXTE    = new Color(26,  43,  60);
    public static final Color COULEUR_ROUGE    = new Color(229,  57,  53);

    private UIFactory() {}

    // ── Bouton principal (fond bleu) ──────────────────────────────────────
    public static JButton creerBoutonPrimaire(String texte) {
        JButton btn = new JButton(texte);
        btn.setBackground(COULEUR_PRIMAIRE);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Calibri", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 38));
        return btn;
    }

    // ── Bouton danger (fond rouge) ────────────────────────────────────────
    public static JButton creerBoutonDanger(String texte) {
        JButton btn = creerBoutonPrimaire(texte);
        btn.setBackground(COULEUR_ROUGE);
        return btn;
    }

    // ── Toast non-bloquant ────────────────────────────────────────────────
    public static void showToast(String message) {
        JWindow toast = new JWindow();
        JLabel label = new JLabel("  " + message + "  ", SwingConstants.CENTER);
        label.setFont(new Font("Calibri", Font.BOLD, 13));
        label.setForeground(Color.WHITE);
        label.setBackground(COULEUR_PRIMAIRE);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        toast.add(label);
        toast.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        toast.setLocation((screen.width - toast.getWidth()) / 2, screen.height - 120);
        toast.setVisible(true);
        new Timer(2500, (ActionEvent e) -> toast.dispose()) {{ setRepeats(false); }}.start();
    }

    // ── Champ texte stylisé ───────────────────────────────────────────────
    public static JTextField creerChampTexte(int colonnes) {
        JTextField f = new JTextField(colonnes);
        f.setFont(new Font("Calibri", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 220), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }

    // ── Champ mot de passe stylisé ────────────────────────────────────────
    public static JPasswordField creerChampMotDePasse(int colonnes) {
        JPasswordField f = new JPasswordField(colonnes);
        f.setFont(new Font("Calibri", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 220), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }

    // ── Labels ────────────────────────────────────────────────────────────
    public static JLabel creerTitre(String texte) {
        JLabel l = new JLabel(texte);
        l.setFont(new Font("Calibri", Font.BOLD, 22));
        l.setForeground(COULEUR_TEXTE);
        return l;
    }

    public static JLabel creerLabel(String texte) {
        JLabel l = new JLabel(texte);
        l.setFont(new Font("Calibri", Font.PLAIN, 13));
        l.setForeground(COULEUR_TEXTE);
        return l;
    }

    public static JLabel creerLabelErreur(String texte) {
        JLabel l = new JLabel(texte);
        l.setFont(new Font("Calibri", Font.ITALIC, 12));
        l.setForeground(COULEUR_ROUGE);
        return l;
    }
}
