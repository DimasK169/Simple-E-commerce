package com.payment.service.repository.cart;

import com.payment.service.entity.cart.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    @Query("Select t From CartEntity t Where t.userEmail=:userEmail and t.isReadyToPay=:isReadyToPay and t.isPayed=:isPayed and t.isFailed=:isFailed")
    List<CartEntity> findByUserEmail(String userEmail, Boolean isReadyToPay, Boolean isPayed, Boolean isFailed);

    @Query("Select t From CartEntity t Where t.userEmail=:userEmail and t.paymentNumber=:paymentNumber and t.isReadyToPay=:isReadyToPay and t.isPayed=:isPayed and t.isFailed=:isFailed")
    List<CartEntity> findByUserEmailAndPaymentNumber(String userEmail, String paymentNumber, Boolean isReadyToPay, Boolean isPayed, Boolean isFailed);

}
