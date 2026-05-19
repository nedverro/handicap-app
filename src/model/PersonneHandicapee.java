package model;

public class PersonneHandicapee extends Utilisateur {

    private String typeHandicap;
    private String numeroEtudiant;

    public PersonneHandicapee() {
        super();
        this.role = "HANDICAPEE";
    }

    public PersonneHandicapee(int id, String nom, String prenom,
                               String email, String mdp,
                               String typeHandicap, String numeroEtudiant) {
        super(id, nom, prenom, email, mdp, "HANDICAPEE");
        this.typeHandicap   = typeHandicap;
        this.numeroEtudiant = numeroEtudiant;
    }

    public String getTypeHandicap()              { return typeHandicap; }
    public void   setTypeHandicap(String t)      { this.typeHandicap = t; }
    public String getNumeroEtudiant()            { return numeroEtudiant; }
    public void   setNumeroEtudiant(String n)    { this.numeroEtudiant = n; }
}
