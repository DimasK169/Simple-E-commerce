//package com.flash_sale.app.repository.flash_sale;
//
//import com.flash_sale.app.entity.flash_sale.FlashSale;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.time.LocalDateTime;
//import java.util.Date;
//import java.util.List;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
//public class FlashSaleRepositoryTest {
//
//    @Autowired
//    private FlashSaleRepository flashSaleRepository;
//
//    @Test
//    public void testFindByFsCode() {
//        // Given
//        FlashSale flashSale = FlashSale.builder()
//                .fsCode("FS001")
//                .fsName("Flash Sale April")
//                .fsProduct("P001")
//                .fsStartDate(LocalDateTime.now().minusDays(1))
//                .fsEndDate(LocalDateTime.now().plusDays(1))
//                .fsIsDelete(false)
//                .fsCreatedBy("admin")
//                .fsCreatedDate(new Date())
//                .fsUpdateDate(new Date())
//                .build();
//
//        flashSaleRepository.save(flashSale);
//
//        // When
//        List<FlashSale> result = flashSaleRepository.findByFsCode("FS001");
//
//        // Then
//        Assertions.assertFalse(result.isEmpty());
//        Assertions.assertEquals(1, result.size());
//        Assertions.assertEquals("FS001", result.get(0).getFsCode());
//    }
//
//    @Test
//    public void testFindByFsCodeAndFsProduct() {
//        // Given
//        FlashSale flashSale = FlashSale.builder()
//                .fsCode("FS002")
//                .fsName("Flash Sale Mei")
//                .fsProduct("mie instan")
//                .fsStartDate(LocalDateTime.now().minusDays(1))
//                .fsEndDate(LocalDateTime.now().plusDays(1))
//                .fsIsDelete(false)
//                .fsCreatedBy("admin")
//                .fsCreatedDate(new Date())
//                .fsUpdateDate(new Date())
//                .build();
//
//        flashSaleRepository.save(flashSale);
//
//        // When
//        var result = flashSaleRepository.findByFsCodeAndFsProduct("FS002", "mie instan");
//
//        // Then
//        Assertions.assertTrue(result.isPresent());
//        Assertions.assertEquals("FS002", result.get().getFsCode());
//        Assertions.assertEquals("mie instan", result.get().getFsProduct());
//    }
//
//    @Test
//    public void testFindAllByFsIsDeleteFalse() {
//        // Given
//        flashSaleRepository.saveAll(List.of(
//                FlashSale.builder()
//                        .fsCode("FS003")
//                        .fsName("Flash Sale Juni")
//                        .fsProduct("Teh Kotak")
//                        .fsStartDate(LocalDateTime.now())
//                        .fsEndDate(LocalDateTime.now().plusDays(3))
//                        .fsIsDelete(false)
//                        .fsCreatedBy("admin")
//                        .fsCreatedDate(new Date())
//                        .fsUpdateDate(new Date())
//                        .build(),
//                FlashSale.builder()
//                        .fsCode("FS004")
//                        .fsName("Flash Sale Juli")
//                        .fsProduct("Kerupuk Kentang")
//                        .fsStartDate(LocalDateTime.now())
//                        .fsEndDate(LocalDateTime.now().plusDays(3))
//                        .fsIsDelete(true)
//                        .fsCreatedBy("admin")
//                        .fsCreatedDate(new Date())
//                        .fsUpdateDate(new Date())
//                        .build()
//        ));
//
//        // When
//        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
//        var result = flashSaleRepository.findAllByFsIsDeleteFalse(pageable);
//
//        // Then
//        Assertions.assertEquals(1, result.getTotalElements());
//        Assertions.assertEquals("FS003", result.getContent().get(0).getFsCode());
//    }
//
//    @Test
//    public void testFindActiveFlashSaleCode() {
//        // Given
//        flashSaleRepository.save(FlashSale.builder()
//                .fsCode("FS005")
//                .fsName("Flash Sale Agustus")
//                .fsProduct("Kerupuk")
//                .fsStartDate(LocalDateTime.now().minusHours(1))
//                .fsEndDate(LocalDateTime.now().plusHours(1))
//                .fsIsDelete(false)
//                .fsCreatedBy("admin")
//                .fsCreatedDate(new Date())
//                .fsUpdateDate(new Date())
//                .build());
//
//        // When
//        var result = flashSaleRepository.findActiveFlashSaleCode();
//
//        // Then
//        Assertions.assertTrue(result.isPresent());
//        Assertions.assertEquals("FS005", result.get());
//    }
//
//}
