package tn.esprit.spring.kaddem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Course;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Professor;
import tn.esprit.spring.kaddem.repositories.CourseRepository;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.ProfessorRepository;
import tn.esprit.spring.kaddem.services.DepartementServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartementServiceMockitoTest {
    private static final Logger logger = LogManager.getLogger(DepartementServiceMockitoTest.class);

    @Mock
    private DepartementRepository departementRepository;
    
    @Mock
    private ProfessorRepository professorRepository;
    
    @Mock
    private CourseRepository courseRepository;
    
    @InjectMocks
    private DepartementServiceImpl departementService;
    
    private Departement department;
    private Professor professor;
    private Course course;
    private Set<Etudiant> students;
    private Set<Professor> professors;
    
    @BeforeEach
    public void setUp() {
        logger.info("Setting up mock data for tests");
        
        // Create mock data
        department = new Departement();
        department.setIdDepartement(1);
        department.setNomDepartement("Computer Science");
        department.setCapacity(100);
        department.setIsActive(true);
        
        professor = new Professor();
        professor.setIdProfessor(1);
        professor.setFirstName("John");
        professor.setLastName("Doe");
        professor.setYearsOfExperience(10);
        
        course = new Course();
        course.setIdCourse(1);
        course.setTitle("Introduction to Programming");
        
        students = new HashSet<>();
        for (int i = 1; i <= 20; i++) {
            Etudiant student = new Etudiant();
            student.setIdEtudiant(i);
            student.setNomE("Student" + i);
            students.add(student);
        }
        
        professors = new HashSet<>();
        professors.add(professor);
        
        department.setEtudiants(students);
        department.setProfessors(professors);
        
        logger.debug("Mock department created: {}", department.getNomDepartement());
        logger.debug("Mock department has {} students and {} professors", 
            students.size(), professors.size());
    }
    
    @Test
    public void testCalculateStudentsPerDepartment() {
        logger.info("Testing calculateStudentsPerDepartment with mocks");
        
        // Given
        List<Departement> departments = new ArrayList<>();
        departments.add(department);
        
        when(departementRepository.findAll()).thenReturn(departments);
        logger.debug("Mocked departementRepository.findAll() to return {} departments", departments.size());
        
        // When
        Map<String, Integer> result = departementService.calculateStudentsPerDepartment();
        
        // Then
        assertEquals(1, result.size());
        assertEquals(20, result.get("Computer Science"));
        verify(departementRepository, times(1)).findAll();
        
        logger.debug("Result map contains {} entries", result.size());
        logger.debug("Computer Science department has {} students", result.get("Computer Science"));
        logger.info("Test calculateStudentsPerDepartment passed");
    }
    
    @Test
    public void testAssignProfessorToDepartement() {
        logger.info("Testing assignProfessorToDepartement with mocks");
        
        // Given
        when(professorRepository.findById(1)).thenReturn(Optional.of(professor));
        when(departementRepository.findById(1)).thenReturn(Optional.of(department));
        when(professorRepository.save(any(Professor.class))).thenReturn(professor);
        
        logger.debug("Mocked repository responses for professor and department with ID 1");
        
        // When
        boolean result = departementService.assignProfessorToDepartement(1, 1);
        
        // Then
        assertTrue(result);
        assertEquals(department, professor.getDepartement());
        verify(professorRepository, times(1)).findById(1);
        verify(departementRepository, times(1)).findById(1);
        verify(professorRepository, times(1)).save(professor);
        
        logger.debug("Professor successfully assigned to department");
        logger.info("Test assignProfessorToDepartement passed");
    }
    
    @Test
    public void testAssignCourseToDepartement() {
        logger.info("Testing assignCourseToDepartement with mocks");
        
        // Given
        department.setCourses(new HashSet<>());
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(departementRepository.findById(1)).thenReturn(Optional.of(department));
        when(departementRepository.save(any(Departement.class))).thenReturn(department);
        
        logger.debug("Mocked repository responses for course and department with ID 1");
        
        // When
        boolean result = departementService.assignCourseToDepartement(1, 1);
        
        // Then
        assertTrue(result);
        assertTrue(department.getCourses().contains(course));
        verify(courseRepository, times(1)).findById(1);
        verify(departementRepository, times(1)).findById(1);
        verify(departementRepository, times(1)).save(department);
        
        logger.debug("Course successfully assigned to department");
        logger.info("Test assignCourseToDepartement passed");
    }
    
    @Test
    public void testCalculateDepartmentEfficiency() {
        logger.info("Testing calculateDepartmentEfficiency with mocks");
        
        // Given
        department.setCourses(new HashSet<>());
        department.getCourses().add(course);
        
        when(departementRepository.findById(1)).thenReturn(Optional.of(department));
        logger.debug("Mocked departementRepository.findById(1) to return a department with 20 students, 1 professor, and 1 course");
        
        // When
        Integer efficiency = departementService.calculateDepartmentEfficiency(1);
        
        // Then
        // Efficiency formula: (students/10) + (professors*3) + courses
        // 20 students, 1 professor, 1 course = 2 + 3 + 1 = 6
        assertEquals(6, efficiency);
        verify(departementRepository, times(1)).findById(1);
        
        logger.debug("Calculated efficiency: {}", efficiency);
        logger.info("Test calculateDepartmentEfficiency passed");
    }
    
    @Test
    public void testGetDepartmentStatistics() {
        logger.info("Testing getDepartmentStatistics with mocks");
        
        // Given
        department.setCourses(new HashSet<>());
        department.getCourses().add(course);
        
        when(departementRepository.findById(1)).thenReturn(Optional.of(department));
        logger.debug("Mocked departementRepository.findById(1) to return a department with statistics data");
        
        // When
        Map<String, Object> statistics = departementService.getDepartmentStatistics(1);
        
        // Then
        assertEquals("Computer Science", statistics.get("departmentName"));
        assertEquals(20, statistics.get("studentCount"));
        assertEquals(1, statistics.get("professorCount"));
        assertEquals(1, statistics.get("courseCount"));
        assertEquals(20.0, statistics.get("studentToProfessorRatio"));
        
        logger.debug("Statistics result: {}", statistics);
        logger.info("Test getDepartmentStatistics passed");
    }
} 