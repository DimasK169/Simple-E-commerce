package com.product.app.repository.product;

import com.product.app.entity.product.AuditTrails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditTrailsRepository extends JpaRepository<AuditTrails, Long> {
}
