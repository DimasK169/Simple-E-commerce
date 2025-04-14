package com.flash_sale.app.entity.flash_sale;

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
public class AuditTrails {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long AtId;
    private String AtAction;
    private Date AtDate;
    private String AtDescription;
    @Column(columnDefinition = "TEXT")
    private String AtRequest;
    @Column(columnDefinition = "TEXT")
    private String AtResponse;
}
