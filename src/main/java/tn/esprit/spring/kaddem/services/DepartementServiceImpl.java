package tn.esprit.spring.kaddem.services;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import tn.esprit.spring.kaddem.entities.Course;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Professor;
import tn.esprit.spring.kaddem.repositories.CourseRepository;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.ProfessorRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepartementServiceImpl implements IDepartementService {
	private static final Logger logger = LogManager.getLogger(DepartementServiceImpl.class);

	private final DepartementRepository departementRepository;
	private final ProfessorRepository professorRepository;
	private final CourseRepository courseRepository;

	public DepartementServiceImpl(DepartementRepository departementRepository,
								ProfessorRepository professorRepository,
								CourseRepository courseRepository) {
		this.departementRepository = departementRepository;
		this.professorRepository = professorRepository;
		this.courseRepository = courseRepository;
	}

	@Override
	public List<Departement> retrieveAllDepartements() {
		logger.info("Retrieving all departments");
		List<Departement> departments = departementRepository.findAll();
		logger.debug("Found {} departments", departments.size());
		return departments;
	}

	@Override
	public Departement addDepartement(Departement d) {
		logger.info("Adding new department: {}", d.getNomDepartement());
		Departement savedDepartment = departementRepository.save(d);
		logger.info("Department saved with ID: {}", savedDepartment.getIdDepartement());
		return savedDepartment;
	}

	@Override
	public void deleteDepartement(Integer id) {
		logger.info("Deleting department with ID: {}", id);
		try {
			departementRepository.deleteById(id);
			logger.info("Department with ID {} successfully deleted", id);
		} catch (Exception e) {
			logger.error("Error deleting department with ID {}: {}", id, e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public Departement updateDepartement(Departement d) {
		logger.info("Updating department with ID: {}", d.getIdDepartement());
		Departement updatedDepartment = departementRepository.save(d);
		logger.info("Department with ID {} successfully updated", updatedDepartment.getIdDepartement());
		return updatedDepartment;
	}

	@Override
	public Departement retrieveDepartement(Integer id) {
		logger.info("Retrieving department with ID: {}", id);
		Optional<Departement> optionalDepartement = departementRepository.findById(id);
		if (optionalDepartement.isPresent()) {
			logger.debug("Found department: {}", optionalDepartement.get().getNomDepartement());
			return optionalDepartement.get();
		} else {
			logger.warn("Department with ID {} not found", id);
			return null;
		}
	}

	@Override
	public List<Departement> findDepartementsByCapacityGreaterThan(Integer capacity) {
		logger.info("Finding departments with capacity greater than: {}", capacity);
		List<Departement> departments = departementRepository.findByCapacityGreaterThan(capacity);
		logger.debug("Found {} departments with capacity greater than {}", departments.size(), capacity);
		return departments;
	}

	@Override
	public List<Departement> findActiveDepartements() {
		logger.info("Finding active departments");
		List<Departement> activeDepartments = departementRepository.findByIsActiveTrue();
		logger.debug("Found {} active departments", activeDepartments.size());
		return activeDepartments;
	}

	@Override
	public Map<String, Integer> calculateStudentsPerDepartment() {
		logger.info("Calculating number of students per department");
		Map<String, Integer> studentsPerDepartment = new HashMap<>();
		List<Departement> departements = departementRepository.findAll();
		
		for (Departement departement : departements) {
			int studentCount = departement.getEtudiants() != null ? departement.getEtudiants().size() : 0;
			studentsPerDepartment.put(departement.getNomDepartement(), studentCount);
			logger.debug("Department '{}' has {} students", departement.getNomDepartement(), studentCount);
		}
		
		logger.info("Calculated students for {} departments", studentsPerDepartment.size());
		return studentsPerDepartment;
	}

	@Override
	public boolean assignProfessorToDepartement(Integer professorId, Integer departementId) {
		logger.info("Assigning professor {} to department {}", professorId, departementId);
		
		Professor professor = professorRepository.findById(professorId).orElse(null);
		if (professor == null) {
			logger.warn("Professor with ID {} not found", professorId);
			return false;
		}
		
		Departement departement = departementRepository.findById(departementId).orElse(null);
		if (departement == null) {
			logger.warn("Department with ID {} not found", departementId);
			return false;
		}
		
		try {
			professor.setDepartement(departement);
			professorRepository.save(professor);
			logger.info("Successfully assigned professor {} to department {}", professorId, departementId);
			return true;
		} catch (Exception e) {
			logger.error("Error assigning professor {} to department {}: {}", 
				professorId, departementId, e.getMessage(), e);
			return false;
		}
	}

	@Override
	public boolean assignCourseToDepartement(Integer courseId, Integer departementId) {
		logger.info("Assigning course {} to department {}", courseId, departementId);
		
		Course course = courseRepository.findById(courseId).orElse(null);
		if (course == null) {
			logger.warn("Course with ID {} not found", courseId);
			return false;
		}
		
		Departement departement = departementRepository.findById(departementId).orElse(null);
		if (departement == null) {
			logger.warn("Department with ID {} not found", departementId);
			return false;
		}
		
		try {
			if (departement.getCourses() == null) {
				departement.setCourses(new HashSet<>());
			}
			departement.getCourses().add(course);
			departementRepository.save(departement);
			logger.info("Successfully assigned course {} to department {}", courseId, departementId);
			return true;
		} catch (Exception e) {
			logger.error("Error assigning course {} to department {}: {}", 
				courseId, departementId, e.getMessage(), e);
			return false;
		}
	}

	@Override
	public Integer calculateDepartmentEfficiency(Integer departementId) {
		logger.info("Calculating efficiency for department {}", departementId);
		
		Departement departement = departementRepository.findById(departementId).orElse(null);
		if (departement == null) {
			logger.warn("Department with ID {} not found", departementId);
			return 0;
		}
		
		// Calculate efficiency based on student count, professor count, and courses offered
		int studentCount = departement.getEtudiants() != null ? departement.getEtudiants().size() : 0;
		int professorCount = departement.getProfessors() != null ? departement.getProfessors().size() : 0;
		int courseCount = departement.getCourses() != null ? departement.getCourses().size() : 0;
		
		// Simple efficiency formula: (students/10) + (professors*3) + courses
		// Higher score means more efficient department
		int efficiency = (studentCount / 10) + (professorCount * 3) + courseCount;
		
		logger.debug("Efficiency calculation for department {}: students={}, professors={}, courses={}, efficiency={}", 
			departementId, studentCount, professorCount, courseCount, efficiency);
		
		return efficiency;
	}

	@Override
	public Map<String, Object> getDepartmentStatistics(Integer departementId) {
		logger.info("Getting statistics for department {}", departementId);
		
		Departement departement = departementRepository.findById(departementId).orElse(null);
		Map<String, Object> statistics = new HashMap<>();
		
		if (departement == null) {
			logger.warn("Department with ID {} not found", departementId);
			return statistics;
		}
		
		Set<Etudiant> students = departement.getEtudiants();
		Set<Professor> professors = departement.getProfessors();
		Set<Course> courses = departement.getCourses();
		
		int studentCount = students != null ? students.size() : 0;
		int professorCount = professors != null ? professors.size() : 0;
		int courseCount = courses != null ? courses.size() : 0;
		double ratio = professorCount > 0 && students != null ? 
			(double) studentCount / professorCount : 0;
		
		statistics.put("departmentName", departement.getNomDepartement());
		statistics.put("studentCount", studentCount);
		statistics.put("professorCount", professorCount);
		statistics.put("courseCount", courseCount);
		statistics.put("studentToProfessorRatio", ratio);
		statistics.put("efficiency", calculateDepartmentEfficiency(departementId));
		
		logger.debug("Statistics for department {}: students={}, professors={}, courses={}, ratio={}", 
			departementId, studentCount, professorCount, courseCount, ratio);
		
		return statistics;
	}

	@Override
	public List<Professor> getProfessorsByDepartmentOrderedByExperience(Integer departementId) {
		logger.info("Getting professors ordered by experience for department {}", departementId);
		
		Departement departement = departementRepository.findById(departementId).orElse(null);
		if (departement == null) {
			logger.warn("Department with ID {} not found", departementId);
			return new ArrayList<>();
		}
		
		if (departement.getProfessors() == null) {
			logger.debug("No professors found for department {}", departementId);
			return new ArrayList<>();
		}
		
		List<Professor> sortedProfessors = departement.getProfessors().stream()
			.sorted(Comparator.comparing(Professor::getYearsOfExperience).reversed())
			.collect(Collectors.toList());
		
		logger.debug("Retrieved {} professors for department {}", sortedProfessors.size(), departementId);
		return sortedProfessors;
	}
}
