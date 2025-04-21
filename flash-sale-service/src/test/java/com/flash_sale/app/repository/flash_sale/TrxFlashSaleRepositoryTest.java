package com.flash_sale.app.repository.flash_sale;

import com.flash_sale.app.entity.flash_sale.TrxFlashSale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TrxFlashSaleRepositoryTest {

    @Autowired
    private TrxFlashSaleRepository trxFlashSaleRepository;

    @Test
    public void testFindByFsId() {
        // Given
        TrxFlashSale trxFlashSale = TrxFlashSale.builder()
                .fsId(1L)
                .fsCode("FS001")
                .productId(1L)
                .productCode("PRD001")
                .trxDiscount(0.2)
                .trxPrice(80000)
                .build();

        trxFlashSaleRepository.save(trxFlashSale);

        // When
        List<TrxFlashSale> result = trxFlashSaleRepository.findByFsId(1L);

        // Then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("FS001", result.get(0).getFsCode());
    }

    @Test
    public void testFindByProductIdAndFsCode() {
        // Given
        TrxFlashSale trxFlashSale = TrxFlashSale.builder()
                .fsId(1L)
                .fsCode("FS001")
                .productId(2L)
                .productCode("PRD002")
                .trxDiscount(0.1)
                .trxPrice(90000)
                .build();

        trxFlashSaleRepository.save(trxFlashSale);

        // When
        Optional<TrxFlashSale> result = trxFlashSaleRepository.findByProductIdAndFsCode(2L, "FS001");

        // Then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("PRD002", result.get().getProductCode());
        Assertions.assertEquals(90000, result.get().getTrxPrice());
    }
}
