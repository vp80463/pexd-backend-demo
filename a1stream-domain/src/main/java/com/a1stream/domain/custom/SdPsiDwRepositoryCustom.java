package com.a1stream.domain.custom;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.bo.unit.SDQ070401BO;
import com.a1stream.domain.bo.unit.SDQ070601BO;
import com.a1stream.domain.form.unit.SDQ070401Form;
import com.a1stream.domain.form.unit.SDQ070601Form;

/**
*
* 功能描述: Parts MI 查询
*
* @author mid2215
*/
@Repository
public interface SdPsiDwRepositoryCustom {

    /**
     * author: Tang Tiantian
     */
    Page<SDQ070401BO> findSdPsiDwList(SDQ070401Form model);

    /**
     * author: Tang Tiantian
     */
    Page<SDQ070601BO> findSdPsiDwList(SDQ070601Form model);

}
