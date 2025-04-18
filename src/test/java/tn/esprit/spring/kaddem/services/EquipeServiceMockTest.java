package tn.esprit.spring.kaddem.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Niveau;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EquipeServiceMockTest {

    @Mock
    private EquipeRepository equipeRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private EquipeServiceImpl equipeService;

    private Equipe equipe1;
    private Equipe equipe2;
    private Etudiant etudiant;

    @BeforeEach
    void setUp() {
        // Initialiser les données de test
        equipe1 = new Equipe();
        equipe1.setIdEquipe(1);
        equipe1.setNomEquipe("Équipe A");
        equipe1.setNiveau(Niveau.JUNIOR);
        equipe1.setScore(80);
        equipe1.setNombreMaxEtudiants(3);
        equipe1.setDateCreation(new Date());
        equipe1.setEtudiants(new HashSet<>());
        equipe1.setTechnologiesUtilisees(new HashSet<>(Arrays.asList("Java", "Spring")));
        equipe1.setProjetEnCours(true);
        equipe1.setPerformanceIndex(75.0);
        equipe1.setLastEvaluation(new Date());

        equipe2 = new Equipe();
        equipe2.setIdEquipe(2);
        equipe2.setNomEquipe("Équipe B");
        equipe2.setNiveau(Niveau.SENIOR);
        equipe2.setScore(95);
        equipe2.setNombreMaxEtudiants(3);
        equipe2.setDateCreation(new Date());
        equipe2.setEtudiants(new HashSet<>());
        equipe2.setTechnologiesUtilisees(new HashSet<>(Arrays.asList("Java", "Angular", "Docker")));
        equipe2.setProjetEnCours(true);
        equipe2.setPerformanceIndex(92.0);
        equipe2.setLastEvaluation(new Date());

        etudiant = new Etudiant();
        etudiant.setIdEtudiant(1);
        etudiant.setNomE("Doe");
        etudiant.setPrenomE("John");
    }

    @Test
    void testPeutAccepterNouveauxMembres() {
        // Configuration du mock
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe1));

        // Test avec une équipe vide
        assertTrue(equipeService.peutAccepterNouveauxMembres(1));

        // Ajouter des étudiants jusqu'à la limite
        Set<Etudiant> etudiants = new HashSet<>();
        for (int i = 0; i < equipe1.getNombreMaxEtudiants(); i++) {
            Etudiant e = new Etudiant();
            e.setIdEtudiant(i + 2);
            etudiants.add(e);
        }
        equipe1.setEtudiants(etudiants);

        // Test avec une équipe pleine
        assertFalse(equipeService.peutAccepterNouveauxMembres(1));

        // Vérifier que findById a été appelé
        verify(equipeRepository, times(2)).findById(1);
    }

    @Test
    void testCalculerScoreMoyenParNiveau() {
        // Configuration du mock
        when(equipeRepository.findAll()).thenReturn(Arrays.asList(equipe1, equipe2));

        // Exécuter le test
        Map<Niveau, Double> moyennes = equipeService.calculerScoreMoyenParNiveau();

        // Vérifier les résultats
        assertEquals(80.0, moyennes.get(Niveau.JUNIOR));
        assertEquals(95.0, moyennes.get(Niveau.SENIOR));

        // Vérifier que findAll a été appelé
        verify(equipeRepository).findAll();
    }

    @Test
    void testTransfererEtudiant() {
        // Ajouter l'étudiant à l'équipe source
        equipe1.getEtudiants().add(etudiant);

        // Configuration des mocks
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe1));
        when(equipeRepository.findById(2)).thenReturn(Optional.of(equipe2));
        when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));
        when(equipeRepository.save(any(Equipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Exécuter le test
        boolean resultat = equipeService.transfererEtudiant(1, 1, 2);

        // Vérifier le résultat
        assertTrue(resultat);
        assertFalse(equipe1.getEtudiants().contains(etudiant));
        assertTrue(equipe2.getEtudiants().contains(etudiant));

        // Vérifier les appels aux mocks
        verify(equipeRepository, times(2)).findById(anyInt());
        verify(etudiantRepository).findById(1);
        verify(equipeRepository, times(2)).save(any(Equipe.class));
    }

    @Test
    void testMettreAJourScoreEquipe() {
        // Configuration du mock
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe1));
        when(equipeRepository.save(any(Equipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Test avec un score valide
        equipeService.mettreAJourScoreEquipe(1, 90);
        assertEquals(90, equipe1.getScore());

        // Test avec un score négatif
        assertThrows(IllegalArgumentException.class, () -> {
            equipeService.mettreAJourScoreEquipe(1, -10);
        });

        // Vérifier les appels aux mocks
        verify(equipeRepository, times(2)).findById(1);
        verify(equipeRepository).save(equipe1);
    }

    @Test
    void testTransfererEtudiant_EquipePleine() {
        // Remplir l'équipe de destination
        Set<Etudiant> etudiants = new HashSet<>();
        for (int i = 0; i < equipe2.getNombreMaxEtudiants(); i++) {
            Etudiant e = new Etudiant();
            e.setIdEtudiant(i + 2);
            etudiants.add(e);
        }
        equipe2.setEtudiants(etudiants);

        // Configuration des mocks
        when(equipeRepository.findById(2)).thenReturn(Optional.of(equipe2));

        // Exécuter le test
        boolean resultat = equipeService.transfererEtudiant(1, 1, 2);

        // Vérifier que le transfert a échoué
        assertFalse(resultat);

        // Vérifier que save n'a pas été appelé
        verify(equipeRepository, never()).save(any(Equipe.class));
    }

    @Test
    void testEvaluerPerformanceEquipe() {
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe1));
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe1);

        Double performance = equipeService.evaluerPerformanceEquipe(1);
        
        assertNotNull(performance);
        assertTrue(performance > 0);
        
        verify(equipeRepository).findById(1);
        verify(equipeRepository).save(any(Equipe.class));
    }

    @Test
    void testEvaluerPerformanceEquipeNonTrouvee() {
        when(equipeRepository.findById(999)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () -> {
            equipeService.evaluerPerformanceEquipe(999);
        });
        
        verify(equipeRepository).findById(999);
        verify(equipeRepository, never()).save(any(Equipe.class));
    }

    @Test
    void testRecommanderTechnologies() {
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe1));

        Set<String> technologies = equipeService.recommanderTechnologies(1);
        
        assertNotNull(technologies);
        assertFalse(technologies.isEmpty());
        assertFalse(technologies.contains("Java")); // Ne devrait pas recommander les technologies déjà maîtrisées
        assertFalse(technologies.contains("Spring"));
        
        verify(equipeRepository).findById(1);
    }

    @Test
    void testRecommanderTechnologiesParNiveau() {
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe1));
        
        // Test pour niveau JUNIOR
        equipe1.setNiveau(Niveau.JUNIOR);
        Set<String> techsJunior = equipeService.recommanderTechnologies(1);
        assertTrue(techsJunior.contains("Angular") || techsJunior.contains("Git"));
        
        // Test pour niveau SENIOR
        equipe1.setNiveau(Niveau.SENIOR);
        Set<String> techsSenior = equipeService.recommanderTechnologies(1);
        assertTrue(techsSenior.contains("Microservices") || techsSenior.contains("Docker"));
        
        // Test pour niveau EXPERT
        equipe1.setNiveau(Niveau.EXPERT);
        Set<String> techsExpert = equipeService.recommanderTechnologies(1);
        assertTrue(techsExpert.contains("Cloud Native") || techsExpert.contains("DevOps"));
        
        verify(equipeRepository, times(3)).findById(1);
    }

    @Test
    void testPlanifierEvaluation() {
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe1));
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe1);

        Date evaluation = equipeService.planifierEvaluation(1);
        
        assertNotNull(evaluation);
        assertTrue(evaluation.after(new Date()));
        
        verify(equipeRepository).findById(1);
        verify(equipeRepository).save(any(Equipe.class));
    }

    @Test
    void testPlanifierEvaluationPremiereEvaluation() {
        equipe1.setLastEvaluation(null);
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe1));
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe1);

        Date evaluation = equipeService.planifierEvaluation(1);
        
        assertNotNull(evaluation);
        
        Calendar expected = Calendar.getInstance();
        expected.add(Calendar.MONTH, 1);
        Calendar actual = Calendar.getInstance();
        actual.setTime(evaluation);
        
        assertEquals(expected.get(Calendar.MONTH), actual.get(Calendar.MONTH));
        
        verify(equipeRepository).findById(1);
        verify(equipeRepository).save(any(Equipe.class));
    }

    @Test
    void testPlanifierEvaluationBaseSurPerformance() {
        // Test pour différents niveaux de performance
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe1));
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe1);

        // Performance élevée (>= 90)
        equipe1.setPerformanceIndex(95.0);
        Date evaluationHaute = equipeService.planifierEvaluation(1);
        Calendar calHaute = Calendar.getInstance();
        calHaute.setTime(evaluationHaute);
        
        // Performance moyenne (>= 70)
        equipe1.setPerformanceIndex(75.0);
        Date evaluationMoyenne = equipeService.planifierEvaluation(1);
        Calendar calMoyenne = Calendar.getInstance();
        calMoyenne.setTime(evaluationMoyenne);
        
        // Performance basse (< 70)
        equipe1.setPerformanceIndex(60.0);
        Date evaluationBasse = equipeService.planifierEvaluation(1);
        Calendar calBasse = Calendar.getInstance();
        calBasse.setTime(evaluationBasse);
        
        // Vérifier que les intervalles d'évaluation sont corrects
        assertTrue(calHaute.get(Calendar.MONTH) > calMoyenne.get(Calendar.MONTH));
        assertTrue(calMoyenne.get(Calendar.MONTH) > calBasse.get(Calendar.MONTH));
        
        verify(equipeRepository, times(3)).findById(1);
        verify(equipeRepository, times(3)).save(any(Equipe.class));
    }

    /**
     * Tests for addEquipe method - Updated for single save
     */
    @Test
    void testAddEquipe() {
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe1);
        
        Equipe result = equipeService.addEquipe(equipe1);
        
        assertNotNull(result);
        assertEquals(equipe1.getNomEquipe(), result.getNomEquipe());
        
        // After our fix, save is only called once
        verify(equipeRepository, times(1)).save(any(Equipe.class));
    }

    /**
     * Tests for updateEquipe method - Updated for single save
     */
    @Test
    void testUpdateEquipe() {
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe1);
        
        Equipe result = equipeService.updateEquipe(equipe1);
        
        assertNotNull(result);
        assertEquals(equipe1.getNomEquipe(), result.getNomEquipe());
        
        // After our fix, save is only called once
        verify(equipeRepository, times(1)).save(any(Equipe.class));
    }

    /**
     * Additional tests for transfererEtudiant with edge cases
     */
    @Test
    void testTransfererEtudiantEquipeSourceNonExistante() {
        // Create a new equipeService instance with new mocks to isolate this test
        EquipeRepository mockRepo = mock(EquipeRepository.class);
        EtudiantRepository mockEtuRepo = mock(EtudiantRepository.class);
        
        EquipeServiceImpl isolatedService = new EquipeServiceImpl();
        // Use reflection to inject mocks
        try {
            java.lang.reflect.Field repoField = EquipeServiceImpl.class.getDeclaredField("equipeRepository");
            repoField.setAccessible(true);
            repoField.set(isolatedService, mockRepo);
            
            java.lang.reflect.Field etuRepoField = EquipeServiceImpl.class.getDeclaredField("etudiantRepository");
            etuRepoField.setAccessible(true);
            etuRepoField.set(isolatedService, mockEtuRepo);
        } catch (Exception e) {
            fail("Failed to inject mocks: " + e.getMessage());
        }
        
        // Mock behavior
        when(mockRepo.findById(2)).thenReturn(Optional.of(equipe2));
        when(mockRepo.findById(1)).thenReturn(Optional.empty());
        
        // Execute the method under test
        boolean result = isolatedService.transfererEtudiant(1, 1, 2);
        
        // Verify the results
        assertFalse(result);
    }

    @Test
    void testTransfererEtudiantEquipeDestNonExistante() {
        // Create a new equipeService instance with new mocks to isolate this test
        EquipeRepository mockRepo = mock(EquipeRepository.class);
        EtudiantRepository mockEtuRepo = mock(EtudiantRepository.class);
        
        EquipeServiceImpl isolatedService = new EquipeServiceImpl();
        // Use reflection to inject mocks
        try {
            java.lang.reflect.Field repoField = EquipeServiceImpl.class.getDeclaredField("equipeRepository");
            repoField.setAccessible(true);
            repoField.set(isolatedService, mockRepo);
            
            java.lang.reflect.Field etuRepoField = EquipeServiceImpl.class.getDeclaredField("etudiantRepository");
            etuRepoField.setAccessible(true);
            etuRepoField.set(isolatedService, mockEtuRepo);
        } catch (Exception e) {
            fail("Failed to inject mocks: " + e.getMessage());
        }
        
        // Create a fresh copy of equipe1 with students
        Equipe sourceEquipe = new Equipe();
        sourceEquipe.setIdEquipe(1);
        sourceEquipe.setNomEquipe("Source Team");
        sourceEquipe.setEtudiants(new HashSet<>(Arrays.asList(etudiant)));
        
        // Mock behavior
        when(mockRepo.findById(2)).thenReturn(Optional.empty());
        when(mockRepo.findById(1)).thenReturn(Optional.of(sourceEquipe));
        
        // Execute the method under test
        boolean result = isolatedService.transfererEtudiant(1, 1, 2);
        
        // Verify the results
        assertFalse(result);
    }

    /**
     * Tests for evaluerPerformanceEquipe with edge cases
     */
    @Test
    void testEvaluerPerformanceEquipeNullValues() {
        Equipe equipeNullValues = new Equipe();
        equipeNullValues.setIdEquipe(1);
        equipeNullValues.setScore(null);
        equipeNullValues.setNiveau(null);
        equipeNullValues.setProjetEnCours(null);
        equipeNullValues.setTechnologiesUtilisees(null);
        
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipeNullValues));
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipeNullValues);
        
        Double performance = equipeService.evaluerPerformanceEquipe(1);
        
        assertNotNull(performance);
        assertEquals(0.0, performance); // Base score 0, no bonus for niveau/techno/project
        verify(equipeRepository).findById(1);
        verify(equipeRepository).save(equipeNullValues);
    }

    /**
     * Tests for planifierEvaluation with null performance
     */
    @Test
    void testPlanifierEvaluationNullPerformance() {
        equipe1.setPerformanceIndex(null);
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe1));
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe1);
        
        // This is the proper way to stub a method on the spy
        EquipeServiceImpl equipeServiceSpy = spy(equipeService);
        doReturn(85.0).when(equipeServiceSpy).evaluerPerformanceEquipe(1);
        
        Date evaluation = equipeServiceSpy.planifierEvaluation(1);
        
        assertNotNull(evaluation);
        Calendar expected = Calendar.getInstance();
        expected.add(Calendar.MONTH, 3); // Performance 85.0 -> 3 months
        Calendar actual = Calendar.getInstance();
        actual.setTime(evaluation);
        
        assertEquals(expected.get(Calendar.MONTH), actual.get(Calendar.MONTH));
        verify(equipeRepository).findById(1);
        verify(equipeRepository).save(any(Equipe.class));
    }

    /**
     * Tests for evoluerEquipes with edge cases
     */
    @Test
    void testEvoluerEquipesWithNullContrats() {
        Equipe equipeJunior = new Equipe();
        equipeJunior.setIdEquipe(1);
        equipeJunior.setNiveau(Niveau.JUNIOR);
        
        // Using HashSet instead of casting to List in evoluerEquipes method
        Set<Etudiant> etudiantsJunior = new HashSet<>();
        
        // Student with null contrats list
        Etudiant e1 = new Etudiant();
        e1.setIdEtudiant(1);
        e1.setContrats(null);
        etudiantsJunior.add(e1);
        
        // Student with empty contrats list
        Etudiant e2 = new Etudiant();
        e2.setIdEtudiant(2);
        e2.setContrats(new HashSet<>());
        etudiantsJunior.add(e2);
        
        equipeJunior.setEtudiants(etudiantsJunior);
        
        when(equipeRepository.findAll()).thenReturn(Collections.singletonList(equipeJunior));
        
        equipeService.evoluerEquipes();
        
        // Should not evolve as no students have active contracts
        assertEquals(Niveau.JUNIOR, equipeJunior.getNiveau());
        verify(equipeRepository).findAll();
        verify(equipeRepository, never()).save(any(Equipe.class));
    }
}
