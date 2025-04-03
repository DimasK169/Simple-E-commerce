package com.product.app.repository;

import com.product.app.entity.AuditTrails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditTrailsRepository extends JpaRepository<AuditTrails, Long> {
}
