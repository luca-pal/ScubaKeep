package com.lucap.scubakeep.repository;

import com.lucap.scubakeep.entity.Diver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for performing CRUD and query operations on {@link Diver} entities.
 * <p>
 * Inherits standard data access methods from {@link JpaRepository}.
 */
@Repository
public interface DiverRepository extends JpaRepository<Diver, UUID> {

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    java.util.Optional<Diver> findByEmail(String email);
    java.util.Optional<Diver> findByUsername(String username);
}
