package com.product.app.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Builder
public class AuditTrails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long AtId;
    private String AtAction;
    private Date AtDate;
    private String AtDescription;
    @Column(columnDefinition = "TEXT")
    private String AtRequest;
    @Column(columnDefinition = "TEXT")
    private String AtResponse;

}
