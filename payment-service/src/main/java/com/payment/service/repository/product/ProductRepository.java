package com.payment.service.repository.product;

import com.payment.service.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("Select t From Product t Where t.productCode=:productCode")
    List<Product> findManyProductByCode(String productCode);

}
