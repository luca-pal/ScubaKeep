package com.lucap.scubakeep.repository;

import com.lucap.scubakeep.entity.DiveLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on {@link DiveLog} entities.
 * <p>
 * Inherits default methods from {@link JpaRepository}, including findById, save, delete, and findAll.
 */
@Repository
public interface DiveLogRepository extends JpaRepository<DiveLog, Long> {
}
