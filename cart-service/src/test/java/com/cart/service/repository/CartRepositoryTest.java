package com.cart.service.repository;

import com.cart.service.dto.request.CartRequest;
import com.cart.service.entity.cart.CartEntity;
import com.cart.service.repository.cart.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    private CartEntity cart;

    @BeforeEach
    void setup(){

        cart = CartEntity.builder()
                .userId(1l)
                .userEmail("user@example.com")
                .productId(1l)
                .productName("Laptop")
                .productCode("P001")
                .cartQuantity(10)
                .paymentNumber("N/A")
                .isPayed(false)
                .isFailed(false)
                .cartCreatedDate(new Date())
                .build();
    }

    @Test
    @DisplayName("Save Cart Repository")
    void save() {
        CartEntity response = cartRepository.save(cart);

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Success Find By User Email")
    void findByUserEmail_whenValid(){
        CartEntity test = cartRepository.save(cart);

        List<CartEntity> response = cartRepository.findByUserEmail("user@example.com", "N/A", false, false);

        assertThat(test).isNotNull();
        assertThat(response).isNotNull();
        assertThat(response.get(0).getProductCode()).isEqualTo("P001");
    }

    @Test
    @DisplayName("Success Find By User Email, Product Code And Payment Number")
    void findByUserEmailAndProductCodeAndPaymentNumber_whenValid(){
        CartEntity test = cartRepository.save(cart);

        Optional<CartEntity> response = cartRepository.findByUserEmailAndProductCodeAndPaymentNumber("user@example.com", "P001", "N/A", false, false);

        assertThat(test).isNotNull();
        assertThat(response).isNotNull();
        assertThat(response.get().getUserEmail()).isEqualTo("user@example.com");
        assertThat(response.get().getProductCode()).isEqualTo("P001");
        assertThat(response.get().getPaymentNumber()).isEqualTo("N/A");
    }

}
