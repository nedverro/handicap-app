import controller.PasswordUtil;

public class GenHash {
    public static void main(String[] args) throws Exception {
        System.out.println("ADMIN:    " + PasswordUtil.hashPassword("admin123"));
        System.out.println("ETUDIANT: " + PasswordUtil.hashPassword("etudiant123"));
    }
}