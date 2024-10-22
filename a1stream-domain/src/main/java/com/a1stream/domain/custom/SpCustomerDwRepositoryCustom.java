package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.bo.parts.SPQ050201BO;

/**
*
* 功能描述: Parts MI 查询
*
* @author mid2215
*/
@Repository
public interface SpCustomerDwRepositoryCustom {

    /**
     * author: Tang Tiantian
     */
    List<SPQ050201BO> findPartsMIList(String siteId, String facilityCd, String targetYear, String targetMonth, String customerCd);

}
