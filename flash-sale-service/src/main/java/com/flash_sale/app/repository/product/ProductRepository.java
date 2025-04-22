package com.flash_sale.app.repository.product;

import com.flash_sale.app.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("Select p from Product p where p.productCode=:productCode and p.productIsDelete=false")
    List<Product> findByproductCode(String productCode);
}
