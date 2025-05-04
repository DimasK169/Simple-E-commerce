package com.product.app.repository.product;


import com.product.app.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("Select p from Product p where p.productCode=:productCode")
    Product findByproductCode(@Param("productCode") String productCode);
    @Query("SELECT p FROM Product p WHERE (LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.productCategory) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.productIsDelete = false AND p.productIsAvailable = true")
    Page<Product> searchByNameOrCategory(@Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE (LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.productCategory) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.productIsDelete = false")
    Page<Product> searchByNameOrCategoryForAdmin(@Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT p FROM Product p WhERE p.productIsDelete = false AND p.productIsAvailable = true")
    Page<Product> findByProductCustomer(Pageable pageable);
    @Query("SELECT p FROM Product p WhERE p.productIsDelete = false")
    Page<Product> findByProductAdmin(Pageable pageable);
    boolean existsByProductCode(String productCode);
    Page<Product> findAllByProductIsDeleteFalse(Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.productCode NOT IN :productCode and p.productIsDelete = false")
    Page<Product> findProductsByCodes(@Param("productCode") List<String> productCode, Pageable pageable);
}
