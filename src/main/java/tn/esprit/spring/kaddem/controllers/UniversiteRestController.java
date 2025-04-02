package tn.esprit.spring.kaddem.controllers;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.services.IUniversiteService;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/universite")
@CrossOrigin("*")
public class UniversiteRestController {

    private static final Logger logger = LogManager.getLogger(UniversiteRestController.class);

    @Autowired
    IUniversiteService universiteService;

    
    @GetMapping("/retrieve-all-universites")
    public List<Universite> getUniversites() {
        logger.info("Récupération de toutes les universités");
        List<Universite> listUniversites = universiteService.retrieveAllUniversites();
        logger.debug("Nombre d'universités récupérées : {}", listUniversites.size());
        return listUniversites;
    }

   
    @GetMapping("/retrieve-universite/{universite-id}")
    public Universite retrieveUniversite(@PathVariable("universite-id") Integer universiteId) {
        logger.info("Récupération de l'université avec ID : {}", universiteId);
        Universite universite = universiteService.retrieveUniversite(universiteId);
        if (universite == null) {
            logger.warn("Aucune université trouvée avec ID : {}", universiteId);
        }
        return universite;
    }


    @PostMapping("/add-universite")
    public Universite addUniversite(@RequestBody Universite u) {
        logger.info("Ajout d'une nouvelle université : {}", u);
        Universite universite = universiteService.addUniversite(u);
        logger.debug("Université ajoutée avec succès : {}", universite);
        return universite;
    }

    
    @DeleteMapping("/remove-universite/{universite-id}")
    public void removeUniversite(@PathVariable("universite-id") Integer universiteId) {
        logger.info("Suppression de l'université avec ID : {}", universiteId);
        universiteService.deleteUniversite(universiteId);
        logger.debug("Université supprimée avec succès : {}", universiteId);
    }

    @PutMapping("/update-universite")
    public Universite updateUniversite(@RequestBody Universite u) {
        logger.info("Mise à jour de l'université : {}", u);
        Universite updatedUniversite = universiteService.updateUniversite(u);
        logger.debug("Université mise à jour avec succès : {}", updatedUniversite);
        return updatedUniversite;
    }

    @PutMapping(value="/affecter-universite-departement/{universiteId}/{departementId}")
    public void affectertUniversiteToDepartement(@PathVariable("universiteId") Integer universiteId, @PathVariable("departementId") Integer departementId) {
        logger.info("Affectation de l'université ID : {} au département ID : {}", universiteId, departementId);
        universiteService.assignUniversiteToDepartement(universiteId, departementId);
        logger.debug("Affectation réussie : Université ID {} -> Département ID {}", universiteId, departementId);
    }

    @GetMapping(value = "/listerDepartementsUniversite/{idUniversite}")
    public Set<Departement> listerDepartementsUniversite(@PathVariable("idUniversite") Integer idUniversite) {
        logger.info("Récupération des départements pour l'université ID : {}", idUniversite);
        Set<Departement> departements = universiteService.retrieveDepartementsByUniversite(idUniversite);
        logger.debug("Nombre de départements récupérés : {}", departements.size());
        return departements;
    }
}
