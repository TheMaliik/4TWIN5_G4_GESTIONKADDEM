package tn.esprit.spring.kaddem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.*;

@Entity
public class Professor implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProfessor;
    
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String specialization;
    private Date hireDate;
    private Integer yearsOfExperience;
    
    @ManyToOne
    @JsonIgnore
    private Departement departement;
    
    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Course> courses;
    
    // Constructors
    public Professor() {
        this.courses = new HashSet<>();
    }
    
    public Professor(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    public Professor(Integer idProfessor, String firstName, String lastName, String email) {
        this.idProfessor = idProfessor;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    // Custom Builder Pattern
    public static ProfessorBuilder builder() {
        return new ProfessorBuilder();
    }
    
    public static class ProfessorBuilder {
        private Professor professor = new Professor();
        
        public ProfessorBuilder idProfessor(Integer idProfessor) {
            professor.setIdProfessor(idProfessor);
            return this;
        }
        
        public ProfessorBuilder firstName(String firstName) {
            professor.setFirstName(firstName);
            return this;
        }
        
        public ProfessorBuilder lastName(String lastName) {
            professor.setLastName(lastName);
            return this;
        }
        
        public ProfessorBuilder email(String email) {
            professor.setEmail(email);
            return this;
        }
        
        public ProfessorBuilder phoneNumber(String phoneNumber) {
            professor.setPhoneNumber(phoneNumber);
            return this;
        }
        
        public ProfessorBuilder specialization(String specialization) {
            professor.setSpecialization(specialization);
            return this;
        }
        
        public ProfessorBuilder hireDate(Date hireDate) {
            professor.setHireDate(hireDate);
            return this;
        }
        
        public ProfessorBuilder yearsOfExperience(Integer yearsOfExperience) {
            professor.setYearsOfExperience(yearsOfExperience);
            return this;
        }
        
        public ProfessorBuilder departement(Departement departement) {
            professor.setDepartement(departement);
            return this;
        }
        
        public ProfessorBuilder courses(Set<Course> courses) {
            professor.setCourses(courses);
            return this;
        }
        
        public Professor build() {
            return professor;
        }
    }
    
    // Getters and Setters
    public Integer getIdProfessor() {
        return idProfessor;
    }
    
    public void setIdProfessor(Integer idProfessor) {
        this.idProfessor = idProfessor;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public Date getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }
    
    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }
    
    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
    
    public Departement getDepartement() {
        return departement;
    }
    
    public void setDepartement(Departement departement) {
        this.departement = departement;
    }
    
    public Set<Course> getCourses() {
        return courses;
    }
    
    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Professor professor = (Professor) o;
        return Objects.equals(idProfessor, professor.idProfessor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProfessor);
    }
} 