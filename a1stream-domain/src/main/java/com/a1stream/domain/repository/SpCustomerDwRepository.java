package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.SpCustomerDw;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface SpCustomerDwRepository extends JpaExtensionRepository<SpCustomerDw, String>{

//    /**
//     * 根据年,月,仓库和customer查找数据
//     * author: Tang Tiantian
//     */
//    @Query(value = "  SELECT * FROM sp_customer_dw      "
//            + "        WHERE facility_cd = :facilityCd  "
//            + "          AND target_year = :year        "
//            + "          AND target_month = :month      "
//            + "          AND (customer_cd = :customerCd OR :customerCd = '')"
//            + "     ORDER BY customer_cd                ", nativeQuery=true)
//    List<SpCustomerDw> findPartsMIList(@Param("facilityCd") String pointCd, @Param("year") String year, @Param("month") String month, @Param("customerCd") String customerCd);

}
