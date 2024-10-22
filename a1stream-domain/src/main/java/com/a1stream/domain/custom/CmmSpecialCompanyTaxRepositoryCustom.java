package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.bo.unit.SDM060101BO;
import com.a1stream.domain.form.unit.SDM060101Form;

/**
*
* 功能描述: Cus Tax 查询
*
* @author mid2215
*/
@Repository
public interface CmmSpecialCompanyTaxRepositoryCustom {

    /**
     * author: Tang Tiantian
     */
    Page<SDM060101BO> pageCusTaxList(SDM060101Form model);

    List<SDM060101BO> listCusTaxList(SDM060101Form model);
}
