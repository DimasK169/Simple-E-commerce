package com.product.app.repository.product;

import com.product.app.entity.product.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

//    @BeforeEach
//    void setUp() {
//        Product product1 = Product.builder()
//                .productCode("P001")
//                .productName("Laptop")
//                .productCategory("Electronics")
//                .productIsDelete(false)
//                .build();
//
//        Product product2 = Product.builder()
//                .productCode("P002")
//                .productName("Headphones")
//                .productCategory("Electronics")
//                .productIsDelete(true)
//                .build();
//
//        productRepository.save(product1);
//        productRepository.save(product2);
//    }

    @Test
    public void ProductRepository_SaveAll_ReturnSavedProduct(){
        Product test = Product.builder()
                .productCode("P001")
                .productName("Laptop")
                .productCategory("Electronics")
                .productIsDelete(false)
                .build();

        Product savedProduct = productRepository.save(test);
        Assertions.assertNotNull(savedProduct);
    }

    @Test
    public void ProductRepositoryGetReturnMoreThanOne(){
        Product test1 = Product.builder()
                .productCode("P001")
                .productName("Laptop")
                .productCategory("Electronics")
                .productIsDelete(false)
                .build();

        Product test2 = Product.builder()
                .productCode("P002")
                .productName("Headphones")
                .productCategory("Electronics")
                .productIsDelete(false)
                .build();

        productRepository.save(test1);
        productRepository.save(test2);

        List<Product> productList = productRepository.findAll();

        Assertions.assertNotNull(productList);
        Assertions.assertEquals(2, productList.size());
    }

    @Test
    void testFindByProductCode() {
        Product test = Product.builder()
                .productCode("P001")
                .productName("Laptop")
                .productCategory("Electronics")
                .productIsDelete(false)
                .build();

        productRepository.save(test);

        Product product = productRepository.findByproductCode("P001");
        assertNotNull(product);
        assertEquals("Laptop", product.getProductName());
    }

    @Test
    void testExistsByProductCode() {
        Product test = Product.builder()
                .productCode("P001")
                .productName("Laptop")
                .productCategory("Electronics")
                .productIsDelete(false)
                .build();

        productRepository.save(test);

        boolean exists = productRepository.existsByProductCode("P001");
        assertTrue(exists);

        boolean notExists = productRepository.existsByProductCode("P999");
        assertFalse(notExists);
    }

    @Test
    void testSearchByNameOrCategory() {
        Product test = Product.builder()
                .productCode("P001")
                .productName("Laptop")
                .productCategory("Electronics")
                .productIsDelete(false)
                .build();

        productRepository.save(test);

        Page<Product> result = productRepository.searchByNameOrCategory("laptop", PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
        assertEquals("Laptop", result.getContent().get(0).getProductName());
    }

    @Test
    void testFindAllByProductIsDeleteFalse() {
        Product test = Product.builder()
                .productCode("P001")
                .productName("Laptop")
                .productCategory("Electronics")
                .productIsDelete(false)
                .build();


        productRepository.save(test);

        Page<Product> result = productRepository.findAllByProductIsDeleteFalse(PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
        assertFalse(result.getContent().get(0).getProductIsDelete());
    }
}
