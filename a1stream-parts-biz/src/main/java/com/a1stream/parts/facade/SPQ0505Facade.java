package com.a1stream.parts.facade;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.domain.bo.parts.SPQ050501BO;
import com.a1stream.domain.form.parts.SPQ050501Form;
import com.a1stream.parts.service.SPQ0505Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Component
public class SPQ0505Facade {

    @Resource
    private SPQ0505Service spq0505Service;

    public List<SPQ050501BO> findPartsRetailPriceMIList(SPQ050501Form model) {

        //如果输入point不存在，提示用户
        if(ObjectUtils.isEmpty(model.getPointCd())&& !ObjectUtils.isEmpty(model.getPoint())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.point"), model.getPoint(), CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        String year = model.getTargetMonth().substring(0, 4);
        String month = model.getTargetMonth().substring(4,6);

        return spq0505Service.findPartsRetailPriceMIList(model.getSiteId(), model.getPointCd(),year,month,model.getCustomerCd());
    }
}
