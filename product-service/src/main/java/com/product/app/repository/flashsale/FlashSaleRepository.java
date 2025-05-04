package com.product.app.repository.flashsale;

import com.product.app.entity.flashsale.FlashSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {

    @Query("SELECT DISTINCT t.productCode FROM FlashSale t " +
            "WHERE t.fsStartDate <= CURRENT_TIMESTAMP " +
            "AND t.fsEndDate >= CURRENT_TIMESTAMP " +
            "AND t.fsIsDelete = false")
    List<String> findActiveFlashSaleCode();

}
