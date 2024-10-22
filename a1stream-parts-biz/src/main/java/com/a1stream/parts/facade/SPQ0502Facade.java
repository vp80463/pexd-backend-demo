package com.a1stream.parts.facade;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.domain.bo.parts.SPQ050201BO;
import com.a1stream.domain.form.parts.SPQ050201Form;
import com.a1stream.parts.service.SPQ0502Service;
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
public class SPQ0502Facade {

    @Resource
    private SPQ0502Service spq0502Service;

    public List<SPQ050201BO> findPartsMIList(SPQ050201Form model) {

        String year = model.getTargetMonth().substring(0, 4);
        String month = model.getTargetMonth().substring(4,6);

        //如果输入point不存在，提示用户
        if(ObjectUtils.isEmpty(model.getPointCd())&& !ObjectUtils.isEmpty(model.getPoint())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.point"), model.getPoint(), CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        return spq0502Service.retrievePartsMIList(model.getSiteId(),model.getPointCd(),year,month,model.getCustomerCd());
    }
}
