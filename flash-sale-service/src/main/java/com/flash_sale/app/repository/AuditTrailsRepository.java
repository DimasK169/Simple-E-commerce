package com.flash_sale.app.repository;

import com.flash_sale.app.entity.AuditTrails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditTrailsRepository extends JpaRepository<AuditTrails, Long> {
}
