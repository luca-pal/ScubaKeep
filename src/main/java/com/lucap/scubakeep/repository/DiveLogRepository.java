package com.lucap.scubakeep.repository;

import com.lucap.scubakeep.entity.DiveLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Repository interface for performing CRUD and query operations on {@link DiveLog} entities.
 * <p>
 * Inherits standard data access methods from {@link JpaRepository}.
 */
@Repository
public interface DiveLogRepository extends JpaRepository<DiveLog, Long> {

    java.util.List<DiveLog> findByDiverId(UUID diverId);

    Page<DiveLog> findByDiverId(UUID diverId, Pageable pageable);

    // Can cause performance issue with getAllDivers() with N+1 queries
    long countByDiverId(UUID diverId);
}
