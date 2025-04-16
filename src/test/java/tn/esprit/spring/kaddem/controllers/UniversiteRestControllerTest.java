package tn.esprit.spring.kaddem.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.kaddem.controllers.UniversiteRestController;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.services.IUniversiteService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class UniversiteRestControllerTest {

    @InjectMocks
    private UniversiteRestController universiteRestController;

    @Mock
    private IUniversiteService universiteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUniversites() {
        List<Universite> universites = new ArrayList<>();
        when(universiteService.retrieveAllUniversites()).thenReturn(universites);

        List<Universite> result = universiteRestController.getUniversites();
        assertEquals(universites, result);
        verify(universiteService, times(1)).retrieveAllUniversites();
    }

    @Test
    void testRetrieveUniversite() {
        Universite universite = new Universite();
        when(universiteService.retrieveUniversite(anyInt())).thenReturn(universite);

        Universite result = universiteRestController.retrieveUniversite(1);
        assertEquals(universite, result);
        verify(universiteService, times(1)).retrieveUniversite(1);
    }

    @Test
    void testAddUniversite() {
        Universite universite = new Universite();
        when(universiteService.addUniversite(any(Universite.class))).thenReturn(universite);

        Universite result = universiteRestController.addUniversite(universite);
        assertEquals(universite, result);
        verify(universiteService, times(1)).addUniversite(universite);
    }

    @Test
    void testRemoveUniversite() {
        doNothing().when(universiteService).deleteUniversite(anyInt());

        universiteRestController.removeUniversite(1);
        verify(universiteService, times(1)).deleteUniversite(1);
    }

    @Test
    void testUpdateUniversite() {
        Universite universite = new Universite();
        when(universiteService.updateUniversite(any(Universite.class))).thenReturn(universite);

        Universite result = universiteRestController.updateUniversite(universite);
        assertEquals(universite, result);
        verify(universiteService, times(1)).updateUniversite(universite);
    }

    @Test
    void testAffecterUniversiteToDepartement() {
        doNothing().when(universiteService).assignUniversiteToDepartement(anyInt(), anyInt());

        universiteRestController.affectertUniversiteToDepartement(1, 2);
        verify(universiteService, times(1)).assignUniversiteToDepartement(1, 2);
    }

    @Test
    void testListerDepartementsUniversite() {
        Set<Departement> departements = new HashSet<>();
        when(universiteService.retrieveDepartementsByUniversite(anyInt())).thenReturn(departements);

        Set<Departement> result = universiteRestController.listerDepartementsUniversite(1);
        assertEquals(departements, result);
        verify(universiteService, times(1)).retrieveDepartementsByUniversite(1);
    }
}
