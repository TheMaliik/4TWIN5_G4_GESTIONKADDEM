package tn.esprit.spring.kaddem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.kaddem.entities.Departement;

import java.util.List;

@Repository
public interface DepartementRepository extends JpaRepository<Departement, Integer> {
    List<Departement> findByCapacityGreaterThan(Integer capacity);
    List<Departement> findByIsActiveTrue();
}
