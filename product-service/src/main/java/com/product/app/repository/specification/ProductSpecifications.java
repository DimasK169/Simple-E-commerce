package com.product.app.repository.specification;

import com.product.app.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

public class ProductSpecifications {
    //TODO Menampilkan isdeleted false
    public static Specification<Product> searchByKeyword(String search) {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (search == null || search.isEmpty()) {
                return null; // No filtering
            }
            String likePattern = "%" + search.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("productName")), likePattern); // Apply ILIKE equivalent
        };
    }
}