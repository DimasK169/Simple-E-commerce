package com.flash_sale.app.repository;

import com.flash_sale.app.entity.FlashSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {

    @Query("Select t from FlashSale t where t.FsName=:FsName")
    FlashSale findByFsName(@Param("FsName") String fsName);

}
