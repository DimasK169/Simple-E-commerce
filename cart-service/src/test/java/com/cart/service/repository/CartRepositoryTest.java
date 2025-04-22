package com.cart.service.repository;

import com.cart.service.entity.cart.CartEntity;
import com.cart.service.repository.cart.CartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    private CartEntity cart1, cart2, cart3;

    @BeforeEach
    void setUp() {
        // Data pertama
        cart1 = new CartEntity();
        cart1.setUserEmail("user@example.com");
        cart1.setProductCode("P001");
        cart1.setPaymentNumber("PM123");
        cart1.setIsPayed(false);
        cart1.setIsFailed(false);

        // Data kedua
        cart2 = new CartEntity();
        cart2.setUserEmail("user@example.com");
        cart2.setProductCode("P002");
        cart2.setPaymentNumber("PM123");
        cart2.setIsPayed(false);
        cart2.setIsFailed(false);

        // Data ketiga: email sama, paymentNumber beda
        cart3 = new CartEntity();
        cart3.setUserEmail("user@example.com");
        cart3.setProductCode("P001");
        cart3.setPaymentNumber("PM999");
        cart3.setIsPayed(false);
        cart3.setIsFailed(true); // beda

        cartRepository.saveAll(List.of(cart1, cart2, cart3));
    }

    @Test
    @DisplayName("Valid FindByUserEmail")
    void testFindByUserEmail_withValidData_shouldReturnCorrectCarts() {
        List<CartEntity> result = cartRepository.findByUserEmail("user@example.com", "PM123", false, false);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CartEntity::getProductCode)
                .containsExactlyInAnyOrder("P001", "P002");
    }

    @Test
    @DisplayName("No Match FindByUserEmail")
    void testFindByUserEmail_withNoMatch_shouldReturnEmptyList() {
        List<CartEntity> result = cartRepository.findByUserEmail("wrong@example.com", "PM123", false, false);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Valid FindByUserEmailAndProductCodeAndPaymentNumber")
    void testFindByUserEmailAndProductCodeAndPaymentNumber_withValidData_shouldReturnCart() {
        Optional<CartEntity> result = cartRepository.findByUserEmailAndProductCodeAndPaymentNumber("user@example.com", "P001", "PM123", false, false);

        assertThat(result).isPresent();
        assertThat(result.get().getProductCode()).isEqualTo("P001");
    }

    @Test
    @DisplayName("Incorrect Status FindByUserEmailAndProductCodeAndPaymentNumber")
    void testFindByUserEmailAndProductCodeAndPaymentNumber_withIncorrectStatus_shouldReturnEmpty() {
        Optional<CartEntity> result = cartRepository.findByUserEmailAndProductCodeAndPaymentNumber("user@example.com", "P001", "PM123", true, false);

        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("Wrong Payment Number FindByUserEmailAndProductCodeAndPaymentNumber")
    void testFindByUserEmailAndProductCodeAndPaymentNumber_withWrongPaymentNumber_shouldReturnEmpty() {
        Optional<CartEntity> result = cartRepository.findByUserEmailAndProductCodeAndPaymentNumber("user@example.com", "P001", "INVALID", false, false);

        assertThat(result).isNotPresent();
    }

}
