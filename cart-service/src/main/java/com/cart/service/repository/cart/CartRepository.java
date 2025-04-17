package com.cart.service.repository.cart;

import com.cart.service.dto.request.CartRequest;
import com.cart.service.entity.cart.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    @Query("Select t From CartEntity t Where t.userEmail=:userEmail and t.paymentNumber=:paymentNumber and t.isPayed=:isPayed and t.isFailed=:isFailed")
    List<CartEntity> findByUserEmail(String userEmail, String paymentNumber, Boolean isPayed, Boolean isFailed);

    @Query("Select t From CartEntity t Where t.userEmail=:userEmail and t.productCode=:productCode and t.paymentNumber=:paymentNumber and t.isPayed=:isPayed and t.isFailed=:isFailed")
    Optional<CartEntity> findByUserEmailAndProductCodeAndPaymentNumber(String userEmail, String productCode,String paymentNumber, Boolean isPayed, Boolean isFailed);

}
