package tn.esprit.spring.kaddem.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Niveau;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class EquipeServiceImplTest {

    @Autowired
    private IEquipeService equipeService;

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    private Equipe equipe1;
    private Equipe equipe2;
    private Etudiant etudiant;

    @BeforeEach
    void setUp() {
        // Créer deux équipes pour les tests
        equipe1 = new Equipe();
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
        equipe1 = equipeRepository.save(equipe1);

        equipe2 = new Equipe();
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
        equipe2 = equipeRepository.save(equipe2);

        // Créer un étudiant pour les tests
        etudiant = new Etudiant();
        etudiant.setNomE("Doe");
        etudiant.setPrenomE("John");
        etudiant = etudiantRepository.save(etudiant);
    }

    @Test
    void testPeutAccepterNouveauxMembres() {
        assertTrue(equipeService.peutAccepterNouveauxMembres(equipe1.getIdEquipe()));

        for (int i = 0; i < equipe1.getNombreMaxEtudiants(); i++) {
            Etudiant e = new Etudiant();
            e.setNomE("Test" + i);
            e.setPrenomE("Student" + i);
            e = etudiantRepository.save(e);
            equipe1.getEtudiants().add(e);
        }
        equipe1 = equipeRepository.save(equipe1);

        assertFalse(equipeService.peutAccepterNouveauxMembres(equipe1.getIdEquipe()));
    }

    @Test
    void testCalculerScoreMoyenParNiveau() {
        Map<Niveau, Double> moyennes = equipeService.calculerScoreMoyenParNiveau();

        assertEquals(80.0, moyennes.get(Niveau.JUNIOR));
        assertEquals(95.0, moyennes.get(Niveau.SENIOR));
    }

    @Test
    void testTransfererEtudiant() {
        // Ajouter l'étudiant à l'équipe source
        equipe1.getEtudiants().add(etudiant);
        equipeRepository.save(equipe1);

        // Tester le transfert
        boolean resultat = equipeService.transfererEtudiant(
            etudiant.getIdEtudiant(),
            equipe1.getIdEquipe(),
            equipe2.getIdEquipe()
        );

        assertTrue(resultat);

        // Vérifier que l'étudiant n'est plus dans l'équipe source
        equipe1 = equipeRepository.findById(equipe1.getIdEquipe()).orElse(null);
        assertFalse(equipe1.getEtudiants().contains(etudiant));

        // Vérifier que l'étudiant est maintenant dans l'équipe destination
        equipe2 = equipeRepository.findById(equipe2.getIdEquipe()).orElse(null);
        assertTrue(equipe2.getEtudiants().contains(etudiant));
    }

    @Test
    void testMettreAJourScoreEquipe() {
        // Test de mise à jour avec un score valide
        equipeService.mettreAJourScoreEquipe(equipe1.getIdEquipe(), 90);
        equipe1 = equipeRepository.findById(equipe1.getIdEquipe()).orElse(null);
        assertEquals(90, equipe1.getScore());

        // Test avec un score négatif
        assertThrows(IllegalArgumentException.class, () -> {
            equipeService.mettreAJourScoreEquipe(equipe1.getIdEquipe(), -10);
        });
    }

    @Test
    void testEvaluerPerformanceEquipe() {
        Double performance = equipeService.evaluerPerformanceEquipe(equipe1.getIdEquipe());
        
        assertNotNull(performance);
        assertTrue(performance > 0);
        
        // Vérifier que la performance est influencée par les technologies
        equipe1.getTechnologiesUtilisees().add("React");
        equipe1 = equipeRepository.save(equipe1);
        
        Double newPerformance = equipeService.evaluerPerformanceEquipe(equipe1.getIdEquipe());
        assertTrue(newPerformance > performance);
    }

    @Test
    void testRecommanderTechnologies() {
        Set<String> technologies = equipeService.recommanderTechnologies(equipe1.getIdEquipe());
        
        assertNotNull(technologies);
        assertFalse(technologies.isEmpty());
        
        // Vérifier que les technologies déjà maîtrisées ne sont pas recommandées
        assertFalse(technologies.contains("Java"));
        assertFalse(technologies.contains("Spring"));
    }

    @Test
    void testPlanifierEvaluation() {
        // Test pour une équipe avec bonne performance
        Date evaluation = equipeService.planifierEvaluation(equipe2.getIdEquipe());
        assertNotNull(evaluation);
        
        // La prochaine évaluation devrait être dans le futur
        assertTrue(evaluation.after(new Date()));
        
        // Pour une équipe avec performance > 90, l'évaluation devrait être dans 6 mois
        Calendar expected = Calendar.getInstance();
        expected.add(Calendar.MONTH, 6);
        Calendar actual = Calendar.getInstance();
        actual.setTime(evaluation);
        
        assertEquals(expected.get(Calendar.MONTH), actual.get(Calendar.MONTH));
    }

    @Test
    void testPlanifierEvaluationPremiereEvaluation() {
        // Test pour une équipe sans évaluation précédente
        equipe1.setLastEvaluation(null);
        equipe1 = equipeRepository.save(equipe1);
        
        Date evaluation = equipeService.planifierEvaluation(equipe1.getIdEquipe());
        assertNotNull(evaluation);
        
        // La première évaluation devrait être dans 1 mois
        Calendar expected = Calendar.getInstance();
        expected.add(Calendar.MONTH, 1);
        Calendar actual = Calendar.getInstance();
        actual.setTime(evaluation);
        
        assertEquals(expected.get(Calendar.MONTH), actual.get(Calendar.MONTH));
    }

    @Test
    void testEvaluerPerformanceEquipeAvecDifferentsNiveaux() {
        // Test pour différents niveaux d'équipe
        equipe1.setNiveau(Niveau.JUNIOR);
        Double perfJunior = equipeService.evaluerPerformanceEquipe(equipe1.getIdEquipe());
        
        equipe1.setNiveau(Niveau.SENIOR);
        Double perfSenior = equipeService.evaluerPerformanceEquipe(equipe1.getIdEquipe());
        
        equipe1.setNiveau(Niveau.EXPERT);
        Double perfExpert = equipeService.evaluerPerformanceEquipe(equipe1.getIdEquipe());
        
        // Vérifier que la performance augmente avec le niveau
        assertTrue(perfSenior > perfJunior);
        assertTrue(perfExpert > perfSenior);
    }
}
