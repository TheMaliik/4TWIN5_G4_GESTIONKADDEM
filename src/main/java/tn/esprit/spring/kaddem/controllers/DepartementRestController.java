package tn.esprit.spring.kaddem.controllers;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Professor;
import tn.esprit.spring.kaddem.services.IDepartementService;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/departement")
public class DepartementRestController {
	private static final Logger logger = LogManager.getLogger(DepartementRestController.class);
	
	IDepartementService departementService;
	
	// http://localhost:8089/Kaddem/departement/retrieve-all-departements
	@GetMapping("/retrieve-all-departements")
	public List<Departement> getDepartements() {
		logger.info("Request to retrieve all departments");
		List<Departement> listDepartements = departementService.retrieveAllDepartements();
		logger.debug("Retrieved {} departments", listDepartements.size());
		return listDepartements;
	}
	
	// http://localhost:8089/Kaddem/departement/retrieve-departement/8
	@GetMapping("/retrieve-departement/{departement-id}")
	public Departement retrieveDepartement(@PathVariable("departement-id") Integer departementId) {
		logger.info("Request to retrieve department with ID: {}", departementId);
		Departement departement = departementService.retrieveDepartement(departementId);
		if (departement != null) {
			logger.debug("Retrieved department: {}", departement.getNomDepartement());
		} else {
			logger.warn("Department with ID {} not found", departementId);
		}
		return departement;
	}

	// http://localhost:8089/Kaddem/departement/add-departement
	@PostMapping("/add-departement")
	public Departement addDepartement(@RequestBody Departement d) {
		logger.info("Request to add a new department: {}", d.getNomDepartement());
		try {
			Departement departement = departementService.addDepartement(d);
			logger.info("Successfully added department with ID: {}", departement.getIdDepartement());
			return departement;
		} catch (Exception e) {
			logger.error("Error adding department: {}", e.getMessage(), e);
			throw e;
		}
	}

	// http://localhost:8089/Kaddem/departement/remove-departement/1
	@DeleteMapping("/remove-departement/{departement-id}")
	public void removeDepartement(@PathVariable("departement-id") Integer departementId) {
		logger.info("Request to delete department with ID: {}", departementId);
		try {
			departementService.deleteDepartement(departementId);
			logger.info("Successfully deleted department with ID: {}", departementId);
		} catch (Exception e) {
			logger.error("Error deleting department with ID {}: {}", departementId, e.getMessage(), e);
			throw e;
		}
	}

	// http://localhost:8089/Kaddem/departement/update-departement
	@PutMapping("/update-departement")
	public Departement updateDepartement(@RequestBody Departement e) {
		logger.info("Request to update department with ID: {}", e.getIdDepartement());
		try {
			Departement departement = departementService.updateDepartement(e);
			logger.info("Successfully updated department: {}", departement.getNomDepartement());
			return departement;
		} catch (Exception ex) {
			logger.error("Error updating department: {}", ex.getMessage(), ex);
			throw ex;
		}
	}

	// New advanced endpoints
	
	@GetMapping("/capacity-greater-than/{capacity}")
	public List<Departement> getDepartementsByCapacity(@PathVariable("capacity") Integer capacity) {
		logger.info("Request to find departments with capacity greater than: {}", capacity);
		List<Departement> departments = departementService.findDepartementsByCapacityGreaterThan(capacity);
		logger.debug("Found {} departments with capacity greater than {}", departments.size(), capacity);
		return departments;
	}
	
	@GetMapping("/active")
	public List<Departement> getActiveDepartements() {
		logger.info("Request to find active departments");
		List<Departement> activeDepartments = departementService.findActiveDepartements();
		logger.debug("Found {} active departments", activeDepartments.size());
		return activeDepartments;
	}
	
	@GetMapping("/students-per-department")
	public Map<String, Integer> getStudentsPerDepartment() {
		logger.info("Request to calculate students per department");
		Map<String, Integer> studentsPerDepartment = departementService.calculateStudentsPerDepartment();
		logger.debug("Calculated students for {} departments", studentsPerDepartment.size());
		return studentsPerDepartment;
	}
	
	@PostMapping("/assign-professor/{professorId}/to-department/{departementId}")
	public ResponseEntity<String> assignProfessorToDepartement(
			@PathVariable("professorId") Integer professorId,
			@PathVariable("departementId") Integer departementId) {
		logger.info("Request to assign professor {} to department {}", professorId, departementId);
		boolean success = departementService.assignProfessorToDepartement(professorId, departementId);
		if (success) {
			logger.info("Successfully assigned professor {} to department {}", professorId, departementId);
			return new ResponseEntity<>("Professor assigned successfully", HttpStatus.OK);
		} else {
			logger.warn("Failed to assign professor {} to department {}", professorId, departementId);
			return new ResponseEntity<>("Failed to assign professor", HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/assign-course/{courseId}/to-department/{departementId}")
	public ResponseEntity<String> assignCourseToDepartement(
			@PathVariable("courseId") Integer courseId,
			@PathVariable("departementId") Integer departementId) {
		logger.info("Request to assign course {} to department {}", courseId, departementId);
		boolean success = departementService.assignCourseToDepartement(courseId, departementId);
		if (success) {
			logger.info("Successfully assigned course {} to department {}", courseId, departementId);
			return new ResponseEntity<>("Course assigned successfully", HttpStatus.OK);
		} else {
			logger.warn("Failed to assign course {} to department {}", courseId, departementId);
			return new ResponseEntity<>("Failed to assign course", HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/efficiency/{departementId}")
	public Integer getDepartmentEfficiency(@PathVariable("departementId") Integer departementId) {
		logger.info("Request to calculate efficiency for department {}", departementId);
		Integer efficiency = departementService.calculateDepartmentEfficiency(departementId);
		logger.debug("Calculated efficiency for department {}: {}", departementId, efficiency);
		return efficiency;
	}
	
	@GetMapping("/statistics/{departementId}")
	public Map<String, Object> getDepartmentStatistics(@PathVariable("departementId") Integer departementId) {
		logger.info("Request to get statistics for department {}", departementId);
		Map<String, Object> statistics = departementService.getDepartmentStatistics(departementId);
		logger.debug("Retrieved statistics for department {}: {}", departementId, statistics);
		return statistics;
	}
	
	@GetMapping("/professors-by-experience/{departementId}")
	public List<Professor> getProfessorsByExperience(@PathVariable("departementId") Integer departementId) {
		logger.info("Request to get professors ordered by experience for department {}", departementId);
		List<Professor> professors = departementService.getProfessorsByDepartmentOrderedByExperience(departementId);
		logger.debug("Retrieved {} professors for department {}", professors.size(), departementId);
		return professors;
	}
}


