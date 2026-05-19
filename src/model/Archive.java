package model;

import java.util.Date;

public class Archive {

    private int     idArchive;
    private Date    dateArchivage;
    private String  typeElement;
    private Integer idDemande;
    private Integer idReclamation;

    public Archive() {}

    public Archive(int idArchive, Date dateArchivage, String typeElement,
                   Integer idDemande, Integer idReclamation) {
        this.idArchive      = idArchive;
        this.dateArchivage  = dateArchivage;
        this.typeElement    = typeElement;
        this.idDemande      = idDemande;
        this.idReclamation  = idReclamation;
    }

    public int     getIdArchive()                { return idArchive; }
    public void    setIdArchive(int id)          { this.idArchive = id; }
    public Date    getDateArchivage()            { return dateArchivage; }
    public void    setDateArchivage(Date d)      { this.dateArchivage = d; }
    public String  getTypeElement()              { return typeElement; }
    public void    setTypeElement(String t)      { this.typeElement = t; }
    public Integer getIdDemande()               { return idDemande; }
    public void    setIdDemande(Integer id)      { this.idDemande = id; }
    public Integer getIdReclamation()            { return idReclamation; }
    public void    setIdReclamation(Integer id)  { this.idReclamation = id; }
}
