package tn.esprit.spring.kaddem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Equipe implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEquipe;
    
    private String nomEquipe;
    
    @Enumerated(EnumType.STRING)
    private Niveau niveau;
    
    @Temporal(TemporalType.DATE)
    private Date dateCreation;
    
    private Integer score;
    
    private Integer nombreMaxEtudiants;

    // Nouveaux attributs
    @ElementCollection
    @CollectionTable(name = "equipe_technologies", 
                    joinColumns = @JoinColumn(name = "equipe_id"))
    @Column(name = "technologie")
    private Set<String> technologiesUtilisees = new HashSet<>();

    private Boolean projetEnCours;

    private Double performanceIndex;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastEvaluation;

    @ManyToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Etudiant> etudiants;

    @OneToOne
    private DetailEquipe detailEquipe;

    // Constructeurs avec paramètres spécifiques
    public Equipe(String nomEquipe, Niveau niveau) {
        this.nomEquipe = nomEquipe;
        this.niveau = niveau;
        this.technologiesUtilisees = new HashSet<>();
    }

    public Equipe(String nomEquipe, Niveau niveau, Set<Etudiant> etudiants, DetailEquipe detailEquipe) {
        this.nomEquipe = nomEquipe;
        this.niveau = niveau;
        this.etudiants = etudiants;
        this.detailEquipe = detailEquipe;
        this.technologiesUtilisees = new HashSet<>();
    }

    // Les getters et setters sont gérés par Lombok avec les annotations @Getter et @Setter
}
