package com.lucap.scubakeep.repository;

import com.lucap.scubakeep.entity.Diver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on {@link Diver} entities.
 * <p>
 * Inherits standard data access methods from {@link JpaRepository}.
 */
@Repository
public interface DiverRepository extends JpaRepository<Diver, Long> {
}
