package com.user.app.repository;

import com.user.app.entity.AuditTrails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditTrailsRepository extends JpaRepository<AuditTrails, Long> {
}
