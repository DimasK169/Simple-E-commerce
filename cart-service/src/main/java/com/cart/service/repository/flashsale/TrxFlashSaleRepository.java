package com.cart.service.repository.flashsale;

import com.cart.service.entity.flashsale.TrxFlashSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TrxFlashSaleRepository extends JpaRepository<TrxFlashSale, Long> {

    @Query("Select t From TrxFlashSale t Where t.fsCode=:fsCode and t.productCode=:productCode")
    Optional<TrxFlashSale> findByFsCodeAndProductCode (String fsCode, String productCode);

}
