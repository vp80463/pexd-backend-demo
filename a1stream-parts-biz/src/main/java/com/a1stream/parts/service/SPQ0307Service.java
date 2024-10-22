package com.a1stream.parts.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.SPQ030701BO;
import com.a1stream.domain.form.parts.SPQ030701Form;
import com.a1stream.domain.repository.ProductStockTakingRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
 *
* 功能描述: Parts Stocktaking Progress Inquiry
*
* mid2287
* 2024年6月7日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/07   Wang Nan      New
 */
@Service
public class SPQ0307Service {

    @Resource
    private ProductStockTakingRepository productStockTakingRepository;

    @Resource
    private SystemParameterRepository systemParameterRepository;

    public Page<SPQ030701BO> getPartsStocktakingProgressList(SPQ030701Form form, String siteId) {
        return productStockTakingRepository.getPartsStocktakingProgressList(form, siteId);
    }

    public SystemParameterVO findSystemParameterVOList(String siteId, Long facilityId, String paramTypeId, String paramValue) {
        return BeanMapUtils.mapTo(systemParameterRepository.getProcessingSystemParameter(siteId, facilityId, paramTypeId, paramValue), SystemParameterVO.class);
    }

    public List<SPQ030701BO> getPrintPartsStocktakingProgressList(SPQ030701Form form, String siteId) {
        return productStockTakingRepository.getPrintPartsStocktakingProgressList(form, siteId);
    }
}
