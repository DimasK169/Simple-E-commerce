package com.payment.service.repository.payment;

import com.payment.service.entity.cart.CartEntity;
import com.payment.service.entity.payment.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    @Query("Select t From PaymentEntity t Where t.paymentNumber=:paymentNumber")
    Optional<PaymentEntity> findByPaymentNumber(@Param("paymentNumber") String paymentNumber);

    @Query("Select t From PaymentEntity t Where t.userEmail=:userEmail and t.paymentStatus=:paymentStatus")
    List<PaymentEntity> findPaymentByEmailAndStatus(String userEmail, String paymentStatus);

    @Query("Select t From PaymentEntity t Where t.userEmail=:userEmail")
    List<PaymentEntity> findPaymentByEmail(String userEmail);

}
