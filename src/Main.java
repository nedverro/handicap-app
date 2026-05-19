import view.LoginFrame;

import javax.swing.*;

/**
 * Point d'entrée de l'application UIR Handicap.
 * Lance la fenêtre de connexion sur le thread Swing (EDT).
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new LoginFrame().setVisible(true);
        });
    }
}
