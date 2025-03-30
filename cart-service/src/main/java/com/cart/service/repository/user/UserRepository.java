package com.cart.service.repository.user;

import com.cart.service.entity.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
}
