package com.payment.service.repository;

import com.payment.service.entity.payment.PaymentEntity;
import com.payment.service.repository.payment.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    private PaymentEntity payment;

    @BeforeEach
    void setup(){

        payment = PaymentEntity.builder()
                .userId(1l)
                .userEmail("user@example.com")
                .paymentNumber("PN001")
                .paymentType("gopay")
                .paymentCreatedDate(new Date())
                .build();

    }

    @Test
    @DisplayName("Save Payment Repository")
    void save() {
        PaymentEntity response = paymentRepository.save(payment);

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Success Find By Payment Number")
    void findByPaymentNumber() {
        PaymentEntity test = paymentRepository.save(payment);

        Optional<PaymentEntity> response = paymentRepository.findByPaymentNumber(test.getPaymentNumber());

        assertThat(response).isNotNull();
        assertThat(response.get().getUserEmail()).isEqualTo("user@example.com");
        assertThat(response.get().getPaymentNumber()).isEqualTo("PN001");
    }

    @Test
    @DisplayName("Success Find Payment By Email")
    void findPaymentByEmail(){
        PaymentEntity test = paymentRepository.save(payment);

        List<PaymentEntity> response = paymentRepository.findPaymentByEmail(test.getUserEmail());

        assertThat(response).isNotNull();
        assertThat(response.get(0).getUserEmail()).isEqualTo("user@example.com");
        assertThat(response.get(0).getPaymentNumber()).isEqualTo("PN001");
    }

}
