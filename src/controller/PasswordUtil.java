package controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilitaire de hachage de mots de passe — VERSION SANS DÉPENDANCE EXTERNE.
 *
 * Utilise SHA-256 + sel aléatoire (disponible nativement dans Java).
 * Aucun JAR externe requis.
 *
 * Format du hash stocké en base : SEL$HASH
 *   - SEL  = 16 octets aléatoires encodés en Base64
 *   - HASH = SHA-256(sel + motDePasse) encodé en Base64
 */
public class PasswordUtil {

    private static final String SEPARATOR  = "$";
    private static final int    SALT_BYTES  = 16;

    private PasswordUtil() {}

    /**
     * Hache un mot de passe en clair.
     * Exemple : hashPassword("admin123") → "abc123==$xyz789=="
     *
     * @param motDePasseClair le mot de passe saisi par l'utilisateur
     * @return hash à stocker en base de données
     */
    public static String hashPassword(String motDePasseClair) {
        byte[] salt = generateSalt();
        String saltB64 = Base64.getEncoder().encodeToString(salt);
        String hash    = sha256(saltB64 + motDePasseClair);
        return saltB64 + SEPARATOR + hash;
    }

    /**
     * Vérifie qu'un mot de passe en clair correspond au hash stocké.
     *
     * @param motDePasseClair le mot de passe saisi
     * @param hashStocke      le hash récupéré depuis la base de données
     * @return true si le mot de passe est correct
     */
    public static boolean verifyPassword(String motDePasseClair, String hashStocke) {
        if (motDePasseClair == null || hashStocke == null) return false;
        if (!hashStocke.contains(SEPARATOR)) return false;

        String[] parts   = hashStocke.split("\\" + SEPARATOR, 2);
        String saltB64   = parts[0];
        String hashAttendu = parts[1];
        String hashCalcule = sha256(saltB64 + motDePasseClair);

        return hashAttendu.equals(hashCalcule);
    }

    // ── Méthodes privées ──────────────────────────────────────────────────

    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 non disponible", e);
        }
    }
}
