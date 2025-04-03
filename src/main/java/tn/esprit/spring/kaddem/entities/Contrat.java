package tn.esprit.spring.kaddem.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
public class Contrat implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idContrat;
    
    @Temporal(TemporalType.DATE)
    private Date dateDebutContrat;
    
    @Temporal(TemporalType.DATE)
    private Date dateFinContrat;
    
    @Enumerated(EnumType.STRING)
    private Specialite specialite;
    
    private Boolean archive;
    private Integer montantContrat;
    
    @ManyToOne(cascade = CascadeType.ALL)
    private Etudiant etudiant;

    // Constructors
    public Contrat() {
    }

    public Contrat(Date dateDebutContrat, Date dateFinContrat, Specialite specialite, Boolean archive,
               Integer montantContrat) {
        this.dateDebutContrat = dateDebutContrat;
        this.dateFinContrat = dateFinContrat;
        this.specialite = specialite;
        this.archive = archive;
        this.montantContrat = montantContrat;
    }

    public Contrat(Integer idContrat, Date dateDebutContrat, Date dateFinContrat, Specialite specialite,
               Boolean archive, Integer montantContrat) {
        this.idContrat = idContrat;
        this.dateDebutContrat = dateDebutContrat;
        this.dateFinContrat = dateFinContrat;
        this.specialite = specialite;
        this.archive = archive;
        this.montantContrat = montantContrat;
    }

    // Getters and Setters
    public Integer getIdContrat() {
        return idContrat;
    }
    
    public void setIdContrat(Integer idContrat) {
        this.idContrat = idContrat;
    }
    
    public Date getDateDebutContrat() {
        return dateDebutContrat;
    }
    
    public void setDateDebutContrat(Date dateDebutContrat) {
        this.dateDebutContrat = dateDebutContrat;
    }
    
    public Date getDateFinContrat() {
        return dateFinContrat;
    }
    
    public void setDateFinContrat(Date dateFinContrat) {
        this.dateFinContrat = dateFinContrat;
    }
    
    public Specialite getSpecialite() {
        return specialite;
    }
    
    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }
    
    public Boolean getArchive() {
        return archive;
    }
    
    public void setArchive(Boolean archive) {
        this.archive = archive;
    }
    
    public Integer getMontantContrat() {
        return montantContrat;
    }
    
    public void setMontantContrat(Integer montantContrat) {
        this.montantContrat = montantContrat;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
    }
    
    // Replace Lombok @ToString annotation with explicit toString() method
    @Override
    public String toString() {
        return "Contrat{" +
                "idContrat=" + idContrat +
                ", dateDebutContrat=" + dateDebutContrat +
                ", dateFinContrat=" + dateFinContrat +
                ", specialite=" + specialite +
                ", archive=" + archive +
                ", montantContrat=" + montantContrat +
                ", etudiant=" + (etudiant != null ? etudiant.getIdEtudiant() : null) +
                '}';
    }
}
