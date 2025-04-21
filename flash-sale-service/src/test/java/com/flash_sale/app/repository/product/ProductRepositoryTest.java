package com.flash_sale.app.repository.product;

import com.flash_sale.app.entity.product.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testFindByProductCode() {
        // Given
        Product product = Product.builder()
                .productName("Samsung Galaxy S24")
                .productCode("SGS24")
                .productImage("s24.jpg")
                .productDescription("Flagship Samsung 2024")
                .productCategory("Electronics")
                .productStock(100)
                .productPrice(15000000)
                .productIsAvailable(true)
                .productIsDelete(false)
                .createdBy("admin")
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();

        productRepository.save(product);

        // When
        List<Product> result = productRepository.findByproductCode("SGS24");

        // Then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Samsung Galaxy S24", result.get(0).getProductName());
    }
}
