package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // ⚙️ MODIFIER CES VALEURS selon votre configuration MySQL
    private static final String URL      = "jdbc:mysql://localhost:3306/uir_handicap"
                                         + "?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String USER     = "root";
    private static final String PASSWORD = "";  // ← votre mot de passe MySQL ici

    private static Connection instance = null;

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                instance = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL introuvable. Vérifiez libs/mysql-connector-j.jar : " + e.getMessage());
            }
        }
        return instance;
    }

    public static void closeConnection() {
        if (instance != null) {
            try {
                instance.close();
                instance = null;
            } catch (SQLException e) {
                System.err.println("Erreur fermeture connexion : " + e.getMessage());
            }
        }
    }
}
