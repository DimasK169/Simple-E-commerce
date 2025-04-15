package com.cart.service.repository.user;

import com.cart.service.entity.product.Product;
import com.cart.service.entity.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    @Query("Select t From Users t Where t.userEmail=:userEmail")
    Optional<Users> findByUserEmail(String userEmail);

}
