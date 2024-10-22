package com.a1stream.parts.facade;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.domain.bo.parts.SPQ050401BO;
import com.a1stream.domain.form.parts.SPQ050401Form;
import com.a1stream.parts.service.SPQ0504Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.jpa.tenant.annotation.MultiTenant;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Component
public class SPQ0504Facade {

    @Resource
    private SPQ0504Service spq0504Service;

    @MultiTenant("a1stream-mi-db")
    public List<SPQ050401BO> findWhList(SPQ050401Form model) {

        //如果输入point不存在，提示用户
        if(ObjectUtils.isEmpty(model.getPointCd())&& !ObjectUtils.isEmpty(model.getPoint())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.point"), model.getPoint(), CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        String year = model.getTargetMonth().substring(0, 4);
        String month = model.getTargetMonth().substring(4,6);

        return spq0504Service.retrieveWhList(model.getSiteId(), model.getPointCd(), year, month);
    }
}
