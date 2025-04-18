package tn.esprit.spring.kaddem.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.kaddem.controllers.EtudiantRestController;
import tn.esprit.spring.kaddem.entities.Etudiant;


import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EtudiantRestControllerTest {

    @Mock
    private IEtudiantService etudiantService;

    @InjectMocks
    private EtudiantRestController etudiantRestController;

    private Etudiant sampleEtudiant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleEtudiant = new Etudiant();
        sampleEtudiant.setIdEtudiant(1);
        sampleEtudiant.setPrenomE("Ahmed");
        sampleEtudiant.setNomE("Ben Hmida");
    }

    @Test
    void testGetEtudiants() {
        when(etudiantService.retrieveAllEtudiants()).thenReturn(Arrays.asList(sampleEtudiant));
        List<Etudiant> result = etudiantRestController.getEtudiants();
        assertEquals(1, result.size());
        verify(etudiantService).retrieveAllEtudiants();
    }

    @Test
    void testRetrieveEtudiant() {
        when(etudiantService.retrieveEtudiant(1)).thenReturn(sampleEtudiant);
        Etudiant result = etudiantRestController.retrieveEtudiant(1);
        assertNotNull(result);
        assertEquals("Ahmed", result.getPrenomE());
        verify(etudiantService).retrieveEtudiant(1);
    }

    @Test
    void testAddEtudiant() {
        when(etudiantService.addEtudiant(sampleEtudiant)).thenReturn(sampleEtudiant);
        Etudiant result = etudiantRestController.addEtudiant(sampleEtudiant);
        assertEquals(sampleEtudiant, result);
        verify(etudiantService).addEtudiant(sampleEtudiant);
    }

    @Test
    void testRemoveEtudiant() {
        doNothing().when(etudiantService).removeEtudiant(1);
        etudiantRestController.removeEtudiant(1);
        verify(etudiantService).removeEtudiant(1);
    }

    @Test
    void testUpdateEtudiant() {
        when(etudiantService.updateEtudiant(sampleEtudiant)).thenReturn(sampleEtudiant);
        Etudiant result = etudiantRestController.updateEtudiant(sampleEtudiant);
        assertEquals(sampleEtudiant, result);
        verify(etudiantService).updateEtudiant(sampleEtudiant);
    }

    @Test
    void testAffecterEtudiantToDepartement() {
        doNothing().when(etudiantService).assignEtudiantToDepartement(1, 2);
        etudiantRestController.affecterEtudiantToDepartement(1, 2);
        verify(etudiantService).assignEtudiantToDepartement(1, 2);
    }

    @Test
    void testAddEtudiantWithEquipeAndContract() {
        when(etudiantService.addAndAssignEtudiantToEquipeAndContract(sampleEtudiant, 1, 2)).thenReturn(sampleEtudiant);
        Etudiant result = etudiantRestController.addEtudiantWithEquipeAndContract(sampleEtudiant, 1, 2);
        assertEquals(sampleEtudiant, result);
        verify(etudiantService).addAndAssignEtudiantToEquipeAndContract(sampleEtudiant, 1, 2);
    }

    @Test
    void testGetEtudiantsParDepartement() {
        when(etudiantService.getEtudiantsByDepartement(1)).thenReturn(Arrays.asList(sampleEtudiant));
        List<Etudiant> result = etudiantRestController.getEtudiantsParDepartement(1);
        assertEquals(1, result.size());
        verify(etudiantService).getEtudiantsByDepartement(1);
    }
}