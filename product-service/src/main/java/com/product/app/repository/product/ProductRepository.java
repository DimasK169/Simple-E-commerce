package com.product.app.repository.product;


import com.product.app.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("Select p from Product p where p.productCode=:productCode")
    Product findByproductCode(@Param("productCode") String productCode);
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.productCategory) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchByNameOrCategory(String keyword, Pageable pageable);
    boolean existsByProductCode(String productCode);
    Page<Product> findAllByProductIsDeleteFalse(Pageable pageable);
}
