package tn.esprit.spring.kaddem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.kaddem.entities.Professor;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
} 