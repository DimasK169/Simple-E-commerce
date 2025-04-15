package com.flash_sale.app.repository.flash_sale;

import com.flash_sale.app.entity.flash_sale.TrxFlashSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrxFlashSaleRepository extends JpaRepository<TrxFlashSale, Long> {

    Optional<TrxFlashSale> findByProductIdAndFsCode(Long productId, String fsCode);
}
