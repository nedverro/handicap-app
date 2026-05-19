package model;

import java.util.Date;

public class HistoriqueAction {

    private int     idAction;
    private Date    dateAction;
    private String  action;
    private String  details;
    private Integer idDemande;
    private Integer idReclamation;

    public HistoriqueAction() {}

    public HistoriqueAction(int idAction, Date dateAction, String action,
                            String details, Integer idDemande, Integer idReclamation) {
        this.idAction      = idAction;
        this.dateAction    = dateAction;
        this.action        = action;
        this.details       = details;
        this.idDemande     = idDemande;
        this.idReclamation = idReclamation;
    }

    public int     getIdAction()                   { return idAction; }
    public void    setIdAction(int id)             { this.idAction = id; }
    public Date    getDateAction()                 { return dateAction; }
    public void    setDateAction(Date d)           { this.dateAction = d; }
    public String  getAction()                     { return action; }
    public void    setAction(String a)             { this.action = a; }
    public String  getDetails()                    { return details; }
    public void    setDetails(String d)            { this.details = d; }
    public Integer getIdDemande()                  { return idDemande; }
    public void    setIdDemande(Integer id)        { this.idDemande = id; }
    public Integer getIdReclamation()              { return idReclamation; }
    public void    setIdReclamation(Integer id)    { this.idReclamation = id; }

    @Override
    public String toString() {
        return "[" + dateAction + "] " + action + " — " + details;
    }
}
