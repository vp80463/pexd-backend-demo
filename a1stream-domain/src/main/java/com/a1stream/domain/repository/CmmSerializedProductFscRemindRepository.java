package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.CmmSerializedProductFscRemind;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmSerializedProductFscRemindRepository extends JpaExtensionRepository<CmmSerializedProductFscRemind, Long> {

    @Query(value="select * from cmm_serialized_product_fsc_remind "
            + "where next_remind_gen_date >=:fromDate "
            + "and next_remind_gen_date <=:toDate ", nativeQuery=true)
    List<CmmSerializedProductFscRemind> findByDateRange( @Param("fromDate") String fromDate, @Param("toDate") String toDate);
}
