package tn.esprit.spring.kaddem.services;

import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Professor;

import java.util.List;
import java.util.Map;

public interface IDepartementService {
    public List<Departement> retrieveAllDepartements();

    public Departement addDepartement (Departement d);

    public   Departement updateDepartement (Departement d);

    public  Departement retrieveDepartement (Integer idDepart);

    public  void deleteDepartement(Integer idDepartement);

    // Advanced methods
    List<Departement> findDepartementsByCapacityGreaterThan(Integer capacity);
    List<Departement> findActiveDepartements();
    Map<String, Integer> calculateStudentsPerDepartment();
    boolean assignProfessorToDepartement(Integer professorId, Integer departementId);
    boolean assignCourseToDepartement(Integer courseId, Integer departementId);
    Integer calculateDepartmentEfficiency(Integer departementId);
    Map<String, Object> getDepartmentStatistics(Integer departementId);
    List<Professor> getProfessorsByDepartmentOrderedByExperience(Integer departementId);
}
