package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.bo.parts.SPQ050501BO;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Repository
public interface SpSalesDwRepositoryCustom {

    /**
     * author: Tang Tiantian
     */
    List<SPQ050501BO> findPartsRetailPriceMIList(String siteId, String facilityCd, String year, String month, String customerCd);

}
