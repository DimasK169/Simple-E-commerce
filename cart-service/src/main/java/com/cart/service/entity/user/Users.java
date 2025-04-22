package com.cart.service.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userPhone;
    private String userAddress;
    private String userRole;
    private Boolean userIsActive;
    private Date userCreatedDate;
    private Date userUpdatedDate;

}
