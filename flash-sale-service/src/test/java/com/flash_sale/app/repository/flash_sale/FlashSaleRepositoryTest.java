package com.flash_sale.app.repository.flash_sale;

import com.flash_sale.app.entity.flash_sale.FlashSale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class FlashSaleRepositoryTest {

    @Autowired
    private FlashSaleRepository flashSaleRepository;

    @Test
    public void testFindByFsCode() {
        // Given
        Date now = new Date();
        Date startDate = new Date(now.getTime() - (1000 * 60 * 60 * 24)); // now - 1 day
        Date endDate = new Date(now.getTime() + (1000 * 60 * 60 * 24));   // now + 1 day

        FlashSale flashSale = FlashSale.builder()
                .fsCode("FS001")
                .fsName("Flash Sale April")
                .productCode("P001")
                .fsProduct("Produk A")
                .fsStartDate(startDate)
                .fsEndDate(endDate)
                .fsIsDelete(false)
                .fsCreatedBy("admin")
                .fsCreatedDate(now)
                .fsUpdateDate(now)
                .build();

        flashSaleRepository.save(flashSale);

        // When
        List<FlashSale> result = flashSaleRepository.findByFsCode("FS001");

        // Then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("FS001", result.get(0).getFsCode());
    }

    @Test
    public void testFindByFsCodeAndFsProduct() {
        // Given
        Date now = new Date();
        Date startDate = new Date(now.getTime() - (1000 * 60 * 60 * 24)); // now - 1 day
        Date endDate = new Date(now.getTime() + (1000 * 60 * 60 * 24));   // now + 1 day

        FlashSale flashSale = FlashSale.builder()
                .fsCode("FS002")
                .fsName("Flash Sale Mei")
                .productCode("P002")
                .fsProduct("mie instan")
                .fsStartDate(startDate)
                .fsEndDate(endDate)
                .fsIsDelete(false)
                .fsCreatedBy("admin")
                .fsCreatedDate(now)
                .fsUpdateDate(now)
                .build();

        flashSaleRepository.save(flashSale);

        // When
        Optional<FlashSale> result = flashSaleRepository.findByFsCodeAndFsProduct("FS002", "mie instan");

        // Then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("FS002", result.get().getFsCode());
        Assertions.assertEquals("mie instan", result.get().getFsProduct());
    }

    @Test
    public void testFindAllByFsIsDeleteFalse() {
        // Given
        Date now = new Date();
        Date endDate = new Date(now.getTime() + (1000 * 60 * 60 * 24 * 3)); // now + 3 days

        flashSaleRepository.saveAll(List.of(
                FlashSale.builder()
                        .fsCode("FS003")
                        .fsName("Flash Sale Juni")
                        .productCode("P003")
                        .fsProduct("Teh Kotak")
                        .fsStartDate(now)
                        .fsEndDate(endDate)
                        .fsIsDelete(false)
                        .fsCreatedBy("admin")
                        .fsCreatedDate(now)
                        .fsUpdateDate(now)
                        .build(),
                FlashSale.builder()
                        .fsCode("FS004")
                        .fsName("Flash Sale Juli")
                        .productCode("P004")
                        .fsProduct("Kerupuk Kentang")
                        .fsStartDate(now)
                        .fsEndDate(endDate)
                        .fsIsDelete(true)
                        .fsCreatedBy("admin")
                        .fsCreatedDate(now)
                        .fsUpdateDate(now)
                        .build()
        ));

        // When
        var pageable = PageRequest.of(0, 10);
        var result = flashSaleRepository.findAllByFsIsDeleteFalse(pageable);

        // Then
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("FS003", result.getContent().get(0).getFsCode());
    }

    @Test
    public void testFindActiveFlashSaleCode() {
        // Given
        Date now = new Date();
        Date startDate = new Date(now.getTime() - (1000 * 60 * 60)); // 1 jam yang lalu
        Date endDate = new Date(now.getTime() + (1000 * 60 * 60));   // 1 jam yang akan datang

        flashSaleRepository.save(FlashSale.builder()
                .fsCode("FS005")
                .fsName("Flash Sale Agustus")
                .productCode("P005")
                .fsProduct("Kerupuk")
                .fsStartDate(startDate)
                .fsEndDate(endDate)
                .fsIsDelete(false)
                .fsCreatedBy("admin")
                .fsCreatedDate(now)
                .fsUpdateDate(now)
                .build());

        // When
        var result = flashSaleRepository.findActiveFlashSaleCode();

        // Then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("FS005", result.get());
    }

}
