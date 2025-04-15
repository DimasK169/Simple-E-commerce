package com.cart.service.repository.product;

import com.cart.service.entity.cart.CartEntity;
import com.cart.service.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("Select t From Product t Where t.productCode=:productCode")
    Optional<Product> findProductByCode(String productCode);

    @Query("Select t From Product t Where t.productCode=:productCode")
    List<Product> findManyProductByCode(String productCode);

}
