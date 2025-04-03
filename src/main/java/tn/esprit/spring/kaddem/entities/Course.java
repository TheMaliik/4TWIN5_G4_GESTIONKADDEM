package tn.esprit.spring.kaddem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import javax.persistence.*;
import java.util.HashSet;

@Entity
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCourse;
    
    private String title;
    private String code;
    private Integer creditHours;
    private String description;
    
    @ManyToOne
    @JsonIgnore
    private Professor professor;
    
    @ManyToMany(mappedBy = "courses")
    @JsonIgnore
    private Set<Departement> departements;
    
    // Constructors
    public Course() {
        this.departements = new HashSet<>();
    }
    
    public Course(String title, String code, Integer creditHours) {
        this.title = title;
        this.code = code;
        this.creditHours = creditHours;
    }
    
    // Custom Builder Pattern
    public static CourseBuilder builder() {
        return new CourseBuilder();
    }
    
    public static class CourseBuilder {
        private Course course = new Course();
        
        public CourseBuilder idCourse(Integer idCourse) {
            course.setIdCourse(idCourse);
            return this;
        }
        
        public CourseBuilder title(String title) {
            course.setTitle(title);
            return this;
        }
        
        public CourseBuilder code(String code) {
            course.setCode(code);
            return this;
        }
        
        public CourseBuilder creditHours(Integer creditHours) {
            course.setCreditHours(creditHours);
            return this;
        }
        
        public CourseBuilder description(String description) {
            course.setDescription(description);
            return this;
        }
        
        public CourseBuilder professor(Professor professor) {
            course.setProfessor(professor);
            return this;
        }
        
        public CourseBuilder departements(Set<Departement> departements) {
            course.setDepartements(departements);
            return this;
        }
        
        public Course build() {
            return course;
        }
    }
    
    // Getters and Setters
    public Integer getIdCourse() {
        return idCourse;
    }
    
    public void setIdCourse(Integer idCourse) {
        this.idCourse = idCourse;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public Integer getCreditHours() {
        return creditHours;
    }
    
    public void setCreditHours(Integer creditHours) {
        this.creditHours = creditHours;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Professor getProfessor() {
        return professor;
    }
    
    public void setProfessor(Professor professor) {
        this.professor = professor;
    }
    
    public Set<Departement> getDepartements() {
        return departements;
    }
    
    public void setDepartements(Set<Departement> departements) {
        this.departements = departements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(idCourse, course.idCourse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCourse);
    }
} 