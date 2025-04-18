package tn.esprit.spring.kaddem.controllers;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.services.IEtudiantService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/etudiant")
public class EtudiantRestController {

	private static final Logger logger = LogManager.getLogger(EtudiantRestController.class);

	@Autowired
	IEtudiantService etudiantService;

	@GetMapping("/retrieve-all-etudiants")
	public List<Etudiant> getEtudiants() {
		logger.info("Request to retrieve all Etudiants");
		List<Etudiant> listEtudiants = etudiantService.retrieveAllEtudiants();
		logger.debug("Retrieved {} etudiants", listEtudiants.size());
		return listEtudiants;
	}

	@GetMapping("/retrieve-etudiant/{etudiant-id}")
	public Etudiant retrieveEtudiant(@PathVariable("etudiant-id") Integer etudiantId) {
		logger.info("Request to retrieve Etudiant with ID: {}", etudiantId);
		Etudiant etudiant = etudiantService.retrieveEtudiant(etudiantId);
		if (etudiant != null) {
			logger.debug("Etudiant found: {}", etudiant);
		} else {
			logger.warn("No Etudiant found with ID: {}", etudiantId);
		}
		return etudiant;
	}

	@PostMapping("/add-etudiant")
	public Etudiant addEtudiant(@RequestBody Etudiant e) {
		logger.info("Adding new Etudiant: {}", e);
		Etudiant etudiant = etudiantService.addEtudiant(e);
		logger.debug("Added Etudiant with ID: {}", etudiant.getIdEtudiant());
		return etudiant;
	}

	@DeleteMapping("/remove-etudiant/{etudiant-id}")
	public void removeEtudiant(@PathVariable("etudiant-id") Integer etudiantId) {
		logger.info("Request to remove Etudiant with ID: {}", etudiantId);
		etudiantService.removeEtudiant(etudiantId);
		logger.debug("Removed Etudiant with ID: {}", etudiantId);
	}

	@PutMapping("/update-etudiant")
	public Etudiant updateEtudiant(@RequestBody Etudiant e) {
		logger.info("Updating Etudiant: {}", e);
		Etudiant updated = etudiantService.updateEtudiant(e);
		logger.debug("Updated Etudiant: {}", updated);
		return updated;
	}

	@PutMapping(value="/affecter-etudiant-departement/{etudiantId}/{departementId}")
	public void affecterEtudiantToDepartement(@PathVariable("etudiantId") Integer etudiantId, @PathVariable("departementId") Integer departementId){
		logger.info("Assigning Etudiant ID {} to Departement ID {}", etudiantId, departementId);
		etudiantService.assignEtudiantToDepartement(etudiantId, departementId);
		logger.debug("Assignment completed");
	}

	@PostMapping("/add-assign-Etudiant/{idContrat}/{idEquipe}")
	public Etudiant addEtudiantWithEquipeAndContract(@RequestBody Etudiant e, @PathVariable("idContrat") Integer idContrat, @PathVariable("idEquipe") Integer idEquipe) {
		logger.info("Adding Etudiant with contract ID {} and team ID {}", idContrat, idEquipe);
		Etudiant etudiant = etudiantService.addAndAssignEtudiantToEquipeAndContract(e, idContrat, idEquipe);
		logger.debug("Etudiant added and assigned: {}", etudiant);
		return etudiant;
	}

	@GetMapping(value = "/getEtudiantsByDepartement/{idDepartement}")
	public List<Etudiant> getEtudiantsParDepartement(@PathVariable("idDepartement") Integer idDepartement) {
		logger.info("Fetching Etudiants for Departement ID {}", idDepartement);
		List<Etudiant> etudiants = etudiantService.getEtudiantsByDepartement(idDepartement);
		logger.debug("Number of Etudiants in Departement {}: {}", idDepartement, etudiants.size());
		return etudiants;
	}
}
