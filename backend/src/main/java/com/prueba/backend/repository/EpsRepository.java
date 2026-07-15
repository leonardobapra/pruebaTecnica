package com.prueba.backend.repository;

import com.prueba.backend.model.Eps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EpsRepository extends JpaRepository<Eps, Long> {
    Optional<Eps> findByCode(String code);
    boolean existsByCode(String code);
}
