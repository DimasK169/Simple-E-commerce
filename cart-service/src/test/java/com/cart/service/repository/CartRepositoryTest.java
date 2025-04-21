package com.cart.service.repository;

import com.cart.service.entity.cart.CartEntity;
import com.cart.service.repository.cart.CartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Test
    @DisplayName("Save Entity")
    public void ProductRepository_SaveAll_ReturnSavedProduct(){
        CartEntity test = CartEntity.builder()
                .productId(1L)
                .userId(1L)
                .trxFlashSaleId(1L)
                .userEmail("test@example.com")
                .productCode("P001")
                .productName("Product Dummy")
                .cartQuantity(2)
                .cartTotalPrice(50000)
                .paymentNumber("N/A")
                .fsCode("FS202504")
                .isPayed(false)
                .isFailed(false)
                .cartCreatedDate(new Date())
                .cartUpdatedDate(new Date())
                .build();

        CartEntity savedCart = cartRepository.save(test);

        Assertions.assertNotNull(savedCart);
        Assertions.assertNotNull(savedCart.getCartId());
        Assertions.assertEquals("P001", savedCart.getProductCode());
        Assertions.assertEquals("Product Dummy", savedCart.getProductName());
    }

}
