package model;

import java.util.Date;

public class Demande {

    private int     idDemande;
    private Date    dateCreation;
    private String  typeDemande;
    private String  description;
    private String  statut;      // "En cours", "Acceptée", "Refusée", "Archivée"
    private int     idPersonne;
    private Integer idAdmin;     // nullable

    public Demande() {}

    public Demande(int idDemande, Date dateCreation, String typeDemande,
                   String description, String statut, int idPersonne, Integer idAdmin) {
        this.idDemande    = idDemande;
        this.dateCreation = dateCreation;
        this.typeDemande  = typeDemande;
        this.description  = description;
        this.statut       = statut;
        this.idPersonne   = idPersonne;
        this.idAdmin      = idAdmin;
    }

    public int     getIdDemande()           { return idDemande; }
    public void    setIdDemande(int id)     { this.idDemande = id; }
    public Date    getDateCreation()        { return dateCreation; }
    public void    setDateCreation(Date d)  { this.dateCreation = d; }
    public String  getTypeDemande()         { return typeDemande; }
    public void    setTypeDemande(String t) { this.typeDemande = t; }
    public String  getDescription()        { return description; }
    public void    setDescription(String d) { this.description = d; }
    public String  getStatut()             { return statut; }
    public void    setStatut(String s)     { this.statut = s; }
    public int     getIdPersonne()         { return idPersonne; }
    public void    setIdPersonne(int id)   { this.idPersonne = id; }
    public Integer getIdAdmin()            { return idAdmin; }
    public void    setIdAdmin(Integer id)  { this.idAdmin = id; }

    @Override
    public String toString() {
        return "[" + idDemande + "] " + typeDemande + " — " + statut;
    }
}
