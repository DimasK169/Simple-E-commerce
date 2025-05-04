package com.product.app.repository.flashsale;

import com.product.app.entity.flashsale.TrxFlashSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrxFlashSaleRepository extends JpaRepository<TrxFlashSale, Long> {

    @Query("Select t From TrxFlashSale t Where t.productCode=:productCode")
    Optional<TrxFlashSale> findByProductCode (String productCode);

    @Query("Select t From TrxFlashSale t Where t.fsCode=:fsCode")
    List<String> findByFsCode (String fsCode);

}
