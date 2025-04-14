package com.flash_sale.app.repository.flash_sale;

import com.flash_sale.app.entity.flash_sale.AuditTrails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditTrailsRepository extends JpaRepository<AuditTrails, Long> {
}
