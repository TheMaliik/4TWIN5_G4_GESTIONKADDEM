package tn.esprit.spring.kaddem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.spring.kaddem.entities.Course;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Professor;
import tn.esprit.spring.kaddem.repositories.CourseRepository;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.ProfessorRepository;
import tn.esprit.spring.kaddem.services.DepartementServiceImpl;
import tn.esprit.spring.kaddem.services.IDepartementService;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(DepartementServiceJUnitTest.TestConfig.class)
public class DepartementServiceJUnitTest {
    private static final Logger logger = LogManager.getLogger(DepartementServiceJUnitTest.class);

    @TestConfiguration
    static class TestConfig {
        @Bean
        public IDepartementService departementService(
                DepartementRepository departementRepository,
                ProfessorRepository professorRepository,
                CourseRepository courseRepository) {
            return new DepartementServiceImpl(departementRepository, professorRepository, courseRepository);
        }
    }

    @Autowired
    private IDepartementService departementService;
    
    @Autowired
    private DepartementRepository departementRepository;
    
    @Autowired
    private ProfessorRepository professorRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    private Departement department1;
    private Departement department2;
    private Professor professor1;
    private Professor professor2;
    private Course course1;
    
    @BeforeEach
    public void setUp() {
        try {
            assertNotNull(departementRepository, "DepartementRepository should not be null");
            assertNotNull(professorRepository, "ProfessorRepository should not be null");
            assertNotNull(courseRepository, "CourseRepository should not be null");
            assertNotNull(departementService, "DepartementService should not be null");
            
            // Clean up repositories
            departementRepository.deleteAll();
            professorRepository.deleteAll();
            courseRepository.deleteAll();
            
            // Create test departments
            department1 = new Departement();
            department1.setNomDepartement("Computer Science");
            department1.setDescription("CS Department");
            department1.setHeadOfDepartment("Dr. Smith");
            department1.setFoundationDate(new Date());
            department1.setCapacity(100);
            department1.setLocation("Building A");
            department1.setIsActive(true);
            department1.setEtudiants(new HashSet<>());
            department1.setProfessors(new HashSet<>());
            department1.setCourses(new HashSet<>());
            
            department2 = new Departement();
            department2.setNomDepartement("Mathematics");
            department2.setDescription("Math Department");
            department2.setHeadOfDepartment("Dr. Johnson");
            department2.setFoundationDate(new Date());
            department2.setCapacity(50);
            department2.setLocation("Building B");
            department2.setIsActive(false);
            department2.setEtudiants(new HashSet<>());
            department2.setProfessors(new HashSet<>());
            department2.setCourses(new HashSet<>());
            
            department1 = departementRepository.save(department1);
            department2 = departementRepository.save(department2);
            logger.debug("Created departments: {} and {}", department1.getNomDepartement(), department2.getNomDepartement());
            
            // Create test professors
            professor1 = new Professor();
            professor1.setFirstName("John");
            professor1.setLastName("Doe");
            professor1.setEmail("john.doe@university.com");
            professor1.setPhoneNumber("123456789");
            professor1.setSpecialization("AI");
            professor1.setHireDate(new Date());
            professor1.setYearsOfExperience(10);
            professor1.setDepartement(department1);
            
            professor2 = new Professor();
            professor2.setFirstName("Jane");
            professor2.setLastName("Smith");
            professor2.setEmail("jane.smith@university.com");
            professor2.setPhoneNumber("987654321");
            professor2.setSpecialization("Databases");
            professor2.setHireDate(new Date());
            professor2.setYearsOfExperience(5);
            professor2.setDepartement(department1);
            
            professor1 = professorRepository.save(professor1);
            professor2 = professorRepository.save(professor2);
            
            department1.getProfessors().add(professor1);
            department1.getProfessors().add(professor2);
            department1 = departementRepository.save(department1);
            
            logger.debug("Created professors: {} {} and {} {}", 
                professor1.getFirstName(), professor1.getLastName(),
                professor2.getFirstName(), professor2.getLastName());
            
            // Create test course
            course1 = new Course();
            course1.setTitle("Introduction to Programming");
            course1.setCode("CS101");
            course1.setCreditHours(3);
            course1.setDescription("Basic programming concepts");
            course1.setDepartements(new HashSet<>());
            
            course1 = courseRepository.save(course1);
            logger.debug("Created course: {}", course1.getTitle());
            
            logger.info("Test data setup completed successfully");
        } catch (Exception e) {
            logger.error("Error during test data setup: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Test
    public void testFindDepartementsByCapacityGreaterThan() {
        logger.info("Testing findDepartementsByCapacityGreaterThan");
        
        // Given
        Integer capacityThreshold = 75;
        logger.debug("Capacity threshold: {}", capacityThreshold);
        
        // When
        List<Departement> departments = departementService.findDepartementsByCapacityGreaterThan(capacityThreshold);
        
        // Then
        assertNotNull(departments);
        assertEquals(1, departments.size());
        assertEquals("Computer Science", departments.get(0).getNomDepartement());
        logger.info("Test findDepartementsByCapacityGreaterThan passed");
    }
    
    @Test
    public void testFindActiveDepartements() {
        logger.info("Testing findActiveDepartements");
        
        // When
        List<Departement> activeDepartments = departementService.findActiveDepartements();
        
        // Then
        assertNotNull(activeDepartments);
        assertEquals(1, activeDepartments.size());
        assertEquals("Computer Science", activeDepartments.get(0).getNomDepartement());
        logger.info("Test findActiveDepartements passed");
    }
    
    @Test
    public void testCalculateDepartmentEfficiency() {
        logger.info("Testing calculateDepartmentEfficiency");
        
        // When
        Integer efficiency = departementService.calculateDepartmentEfficiency(department1.getIdDepartement());
        
        // Then
        assertNotNull(efficiency);
        // Efficiency formula: (students/10) + (professors*3) + courses
        // 0 students, 2 professors, 0 courses = 0 + (2*3) + 0 = 6
        assertEquals(6, efficiency);
        logger.debug("Initial efficiency: {}", efficiency);
        
        // Test assigning a course increases efficiency
        logger.debug("Assigning course {} to department {}", course1.getIdCourse(), department1.getIdDepartement());
        boolean assigned = departementService.assignCourseToDepartement(course1.getIdCourse(), department1.getIdDepartement());
        assertTrue(assigned);
        
        efficiency = departementService.calculateDepartmentEfficiency(department1.getIdDepartement());
        assertEquals(7, efficiency); // Now it should be 6 + 1 = 7
        logger.debug("Updated efficiency after adding course: {}", efficiency);
        
        logger.info("Test calculateDepartmentEfficiency passed");
    }
    
    @Test
    public void testGetDepartmentStatistics() {
        logger.info("Testing getDepartmentStatistics");
        
        // When
        Map<String, Object> statistics = departementService.getDepartmentStatistics(department1.getIdDepartement());
        
        // Then
        assertNotNull(statistics);
        assertEquals("Computer Science", statistics.get("departmentName"));
        assertEquals(0, statistics.get("studentCount"));
        assertEquals(2, statistics.get("professorCount"));
        assertEquals(0, statistics.get("courseCount"));
        assertEquals(0.0, statistics.get("studentToProfessorRatio"));
        
        logger.debug("Department statistics: {}", statistics);
        logger.info("Test getDepartmentStatistics passed");
    }
    
    @Test
    public void testGetProfessorsByDepartmentOrderedByExperience() {
        logger.info("Testing getProfessorsByDepartmentOrderedByExperience");
        
        // When
        List<Professor> professors = departementService.getProfessorsByDepartmentOrderedByExperience(department1.getIdDepartement());
        
        // Then
        assertNotNull(professors);
        assertEquals(2, professors.size());
        assertEquals("John", professors.get(0).getFirstName()); // John has 10 years experience
        assertEquals("Jane", professors.get(1).getFirstName()); // Jane has 5 years experience
        
        logger.debug("Professors ordered by experience: {}({} years), {}({} years)", 
            professors.get(0).getFirstName(), professors.get(0).getYearsOfExperience(),
            professors.get(1).getFirstName(), professors.get(1).getYearsOfExperience());
        
        logger.info("Test getProfessorsByDepartmentOrderedByExperience passed");
    }
} 