package tn.esprit.spring.kaddem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.*;
import java.util.HashSet;

@Entity
public class Departement implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDepartement;
    private String nomDepartement;
    
    // New attributes
    private String description;
    private String headOfDepartment;
    private Date foundationDate;
    private Integer capacity;
    private String location;
    private Boolean isActive;
    
    @OneToMany(mappedBy = "departement")
    @JsonIgnore
    private Set<Etudiant> etudiants;
    
    // New relationships
    @OneToMany(mappedBy = "departement", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Professor> professors;
    
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Course> courses;
    
    // Constructors
    public Departement() {
        this.etudiants = new HashSet<>();
        this.professors = new HashSet<>();
        this.courses = new HashSet<>();
    }
    
    public Departement(String nomDepartement) {
        this.nomDepartement = nomDepartement;
    }
    
    public Departement(Integer idDepartement, String nomDepartement) {
        this.idDepartement = idDepartement;
        this.nomDepartement = nomDepartement;
    }
    
    // Custom Builder Pattern
    public static DepartementBuilder builder() {
        return new DepartementBuilder();
    }
    
    public static class DepartementBuilder {
        private Departement departement = new Departement();
        
        public DepartementBuilder() {
            departement.etudiants = new HashSet<>();
            departement.professors = new HashSet<>();
            departement.courses = new HashSet<>();
        }
        
        public DepartementBuilder idDepartement(Integer idDepartement) {
            departement.setIdDepartement(idDepartement);
            return this;
        }
        
        public DepartementBuilder nomDepartement(String nomDepartement) {
            departement.setNomDepartement(nomDepartement);
            return this;
        }
        
        public DepartementBuilder description(String description) {
            departement.setDescription(description);
            return this;
        }
        
        public DepartementBuilder headOfDepartment(String headOfDepartment) {
            departement.setHeadOfDepartment(headOfDepartment);
            return this;
        }
        
        public DepartementBuilder foundationDate(Date foundationDate) {
            departement.setFoundationDate(foundationDate);
            return this;
        }
        
        public DepartementBuilder capacity(Integer capacity) {
            departement.setCapacity(capacity);
            return this;
        }
        
        public DepartementBuilder location(String location) {
            departement.setLocation(location);
            return this;
        }
        
        public DepartementBuilder isActive(Boolean isActive) {
            departement.setIsActive(isActive);
            return this;
        }
        
        public DepartementBuilder etudiants(Set<Etudiant> etudiants) {
            departement.setEtudiants(etudiants);
            return this;
        }
        
        public DepartementBuilder professors(Set<Professor> professors) {
            departement.setProfessors(professors);
            return this;
        }
        
        public DepartementBuilder courses(Set<Course> courses) {
            departement.setCourses(courses);
            return this;
        }
        
        public Departement build() {
            return departement;
        }
    }
    
    // Getters and Setters
    public Integer getIdDepartement() {
        return idDepartement;
    }
    
    public void setIdDepartement(Integer idDepartement) {
        this.idDepartement = idDepartement;
    }
    
    public String getNomDepartement() {
        return nomDepartement;
    }
    
    public void setNomDepartement(String nomDepartement) {
        this.nomDepartement = nomDepartement;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getHeadOfDepartment() {
        return headOfDepartment;
    }
    
    public void setHeadOfDepartment(String headOfDepartment) {
        this.headOfDepartment = headOfDepartment;
    }
    
    public Date getFoundationDate() {
        return foundationDate;
    }
    
    public void setFoundationDate(Date foundationDate) {
        this.foundationDate = foundationDate;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Set<Etudiant> getEtudiants() {
        return etudiants;
    }
    
    public void setEtudiants(Set<Etudiant> etudiants) {
        this.etudiants = etudiants;
    }
    
    public Set<Professor> getProfessors() {
        return professors;
    }
    
    public void setProfessors(Set<Professor> professors) {
        this.professors = professors;
    }
    
    public Set<Course> getCourses() {
        return courses;
    }
    
    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }
}
