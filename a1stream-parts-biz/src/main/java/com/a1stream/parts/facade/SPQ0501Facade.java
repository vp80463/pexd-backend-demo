package com.a1stream.parts.facade;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.domain.bo.parts.SPQ050101BO;
import com.a1stream.domain.form.parts.SPQ050101Form;
import com.a1stream.parts.service.SPQ0501Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
*
* 功能描述: Parts MI 查询
*
* @author mid2215
*/
@Component
public class SPQ0501Facade {

    @Resource
    private SPQ0501Service spq0501Service;

    public List<SPQ050101BO> findPartsMIList(SPQ050101Form model) {

        //如果输入point不存在，提示用户
        if(ObjectUtils.isEmpty(model.getPointCd())&& !ObjectUtils.isEmpty(model.getPoint())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.point"), model.getPoint(), CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        return spq0501Service.retrievePartsMIList(model);
    }
}
