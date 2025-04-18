package tn.esprit.spring.kaddem.services;

import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.entities.Niveau;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for Equipe service operations
 */
public interface IEquipeService {
    /**
     * Basic CRUD operations
     */
    List<Equipe> retrieveAllEquipes();
    Equipe addEquipe(Equipe e);
    Equipe updateEquipe(Equipe e);
    Equipe retrieveEquipe(Integer idEquipe);
    void deleteEquipe(Integer idEquipe);
    
    /**
     * Advanced operations
     */
    boolean peutAccepterNouveauxMembres(Integer idEquipe);
    Map<Niveau, Double> calculerScoreMoyenParNiveau();
    boolean transfererEtudiant(Integer idEtudiant, Integer idEquipeSource, Integer idEquipeDestination);
    void mettreAJourScoreEquipe(Integer idEquipe, Integer nouveauScore);
    void evoluerEquipes();
    
    /**
     * Additional advanced operations
     */
    Double evaluerPerformanceEquipe(Integer idEquipe);
    Set<String> recommanderTechnologies(Integer idEquipe);
    Date planifierEvaluation(Integer idEquipe);
}
