package com.user.app.repository;

import com.user.app.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    @Query("Select t from Users t where t.userEmail=:userEmail")
    Users findByUserEmail(@Param("userEmail") String userEmail);
}
