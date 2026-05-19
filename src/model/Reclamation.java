package model;

import java.util.Date;

public class Reclamation {

    private int     idReclamation;
    private Date    dateReclamation;
    private String  objet;
    private String  description;
    private String  statut;       // "En attente", "En cours", "Résolue", "Archivée"
    private int     idPersonne;
    private Integer idAdmin;

    public Reclamation() {}

    public Reclamation(int idReclamation, Date dateReclamation, String objet,
                       String description, String statut, int idPersonne, Integer idAdmin) {
        this.idReclamation   = idReclamation;
        this.dateReclamation = dateReclamation;
        this.objet           = objet;
        this.description     = description;
        this.statut          = statut;
        this.idPersonne      = idPersonne;
        this.idAdmin         = idAdmin;
    }

    public int     getIdReclamation()              { return idReclamation; }
    public void    setIdReclamation(int id)        { this.idReclamation = id; }
    public Date    getDateReclamation()            { return dateReclamation; }
    public void    setDateReclamation(Date d)      { this.dateReclamation = d; }
    public String  getObjet()                      { return objet; }
    public void    setObjet(String o)              { this.objet = o; }
    public String  getDescription()               { return description; }
    public void    setDescription(String d)        { this.description = d; }
    public String  getStatut()                    { return statut; }
    public void    setStatut(String s)            { this.statut = s; }
    public int     getIdPersonne()                { return idPersonne; }
    public void    setIdPersonne(int id)          { this.idPersonne = id; }
    public Integer getIdAdmin()                   { return idAdmin; }
    public void    setIdAdmin(Integer id)         { this.idAdmin = id; }

    @Override
    public String toString() {
        return "[" + idReclamation + "] " + objet + " — " + statut;
    }
}
