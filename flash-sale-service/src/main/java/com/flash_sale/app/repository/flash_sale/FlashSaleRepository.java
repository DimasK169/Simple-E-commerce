package com.flash_sale.app.repository.flash_sale;

import com.flash_sale.app.entity.flash_sale.FlashSale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {

    @Query("SELECT t FROM FlashSale t WHERE t.fsCode = :fsCode")
    List<FlashSale> findByFsCode(@Param("fsCode") String fsCode);
    Optional<FlashSale> findByFsCodeAndFsProduct(String fsCode, String fsProduct);
    Page<FlashSale> findAllByFsIsDeleteFalse(Pageable pageable);

}
