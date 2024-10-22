package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.bo.parts.SPQ050101BO;
import com.a1stream.domain.bo.parts.SPQ050301BO;
import com.a1stream.domain.form.parts.SPQ050101Form;
import com.a1stream.domain.form.parts.SPQ050301Form;

/**
*
* 功能描述: Parts MI 查询
*
* @author mid2215
*/
@Repository
public interface SpGeneralyyyyRepositoryCustom {

    /**
     * author: Tang Tiantian
     */
    List<SPQ050101BO> findPartsMIList(SPQ050101Form model);

    /**
     * author: Tang Tiantian
     */
    List<SPQ050301BO> findPartsMIList(SPQ050301Form model);

}
