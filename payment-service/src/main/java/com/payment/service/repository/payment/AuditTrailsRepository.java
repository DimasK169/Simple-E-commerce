package com.payment.service.repository.payment;

import com.payment.service.entity.payment.AuditTrails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditTrailsRepository extends JpaRepository<AuditTrails, Long> {
}
