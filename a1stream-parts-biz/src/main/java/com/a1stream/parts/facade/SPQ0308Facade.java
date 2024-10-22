package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.parts.SPQ030801BO;
import com.a1stream.domain.form.parts.SPQ030801Form;
import com.a1stream.parts.service.SPQ0308Service;

import jakarta.annotation.Resource;

/**
* 功能描述:Parts Location Usage Inquiry
*
* @author mid2178
*/
@Component
public class SPQ0308Facade {

    @Resource
    private SPQ0308Service spq0308Service;

    public List<SPQ030801BO> doRetrieve(SPQ030801Form form, String siteId) {

        List<SPQ030801BO> locationAll =  spq0308Service.getLocationAll(form, siteId);
        Map<String, SPQ030801BO> inUseLocMap = spq0308Service.getLocationInUse(form, siteId);

        for(SPQ030801BO allBo : locationAll) {

            String key = allBo.getLocationType() + CommonConstants.CHAR_VERTICAL_BAR + allBo.getBinType();

            if(inUseLocMap.containsKey(key)) {// in use location

                allBo.setInUseQty(inUseLocMap.get(key).getInUseQty());
                allBo.setEmptyQty(allBo.getTotalQty().subtract(inUseLocMap.get(key).getInUseQty()));
            }else {// empty location

                allBo.setEmptyQty(allBo.getTotalQty());
            }

            allBo.setUsageRate(getPercent(allBo.getInUseQty(), allBo.getTotalQty()));
        }

        return locationAll;
    }

    private BigDecimal getPercent(BigDecimal arg1, BigDecimal arg2) {

        if (arg2.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO.setScale(2);
        }

        return arg1.divide(arg2, 4, RoundingMode.HALF_UP)
                   .multiply(BigDecimal.valueOf(100))
                   .setScale(2, RoundingMode.HALF_UP);
    }
}
