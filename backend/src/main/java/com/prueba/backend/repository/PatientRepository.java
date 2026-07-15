package com.prueba.backend.repository;

import com.prueba.backend.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByDocumentNumber(String documentNumber);

    boolean existsByDocumentNumber(String documentNumber);

    @Query("SELECT p FROM Patient p WHERE " +
           "(COALESCE(:search, '') = '' OR " +
           " LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(p.documentNumber) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR COALESCE(:status, '') = '' OR p.status = :status) " +
           "AND (:priority IS NULL OR COALESCE(:priority, '') = '' OR p.priority = :priority)")
    Page<Patient> findByFilters(
            @Param("search") String search,
            @Param("status") String status,
            @Param("priority") String priority,
            Pageable pageable
    );

    long countByStatus(String status);
    long countByPriority(String priority);
}
