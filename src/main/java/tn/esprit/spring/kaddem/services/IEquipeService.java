package tn.esprit.spring.kaddem.services;

import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.entities.Niveau;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IEquipeService {
    List<Equipe> retrieveAllEquipes();
    Equipe addEquipe(Equipe e);
    Equipe updateEquipe(Equipe e);
    Equipe retrieveEquipe(Integer idEquipe);
    void deleteEquipe(Integer idEquipe);
    
    // Méthodes existantes avancées
    boolean peutAccepterNouveauxMembres(Integer idEquipe);
    Map<Niveau, Double> calculerScoreMoyenParNiveau();
    boolean transfererEtudiant(Integer idEtudiant, Integer idEquipeSource, Integer idEquipeDestination);
    void mettreAJourScoreEquipe(Integer idEquipe, Integer nouveauScore);
    public void evoluerEquipes();
    // Nouvelles méthodes avancées
    Double evaluerPerformanceEquipe(Integer idEquipe);
    Set<String> recommanderTechnologies(Integer idEquipe);
    Date planifierEvaluation(Integer idEquipe);
}
