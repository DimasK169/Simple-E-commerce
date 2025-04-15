package com.cart.service.repository.flashsale;

import com.cart.service.entity.flashsale.FlashSale;
import com.cart.service.entity.flashsale.TrxFlashSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {

    @Query("Select t From FlashSale t Where t.fsCode=:fsCode and t.productCode=:productCode")
    Optional<FlashSale> findByFsCodeAndProductCode (String fsCode, String productCode);

}
