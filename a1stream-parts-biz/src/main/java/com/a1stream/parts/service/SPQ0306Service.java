package com.a1stream.parts.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.SPQ030601BO;
import com.a1stream.domain.entity.SystemParameter;
import com.a1stream.domain.form.parts.SPQ030601Form;
import com.a1stream.domain.repository.ProductStockTakingRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
 *
* 功能描述: Parts Stocktaking List Print
*
* mid2287
* 2024年6月7日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/07   Wang Nan      New
 */
@Service
public class SPQ0306Service {

    @Resource
    private ProductStockTakingRepository productStockTakingRepository;

    @Resource
    private SystemParameterRepository systemParameterRepository;

    public Page<SPQ030601BO> getPartsStocktakingListPageable(SPQ030601Form form, String siteId) {
        return productStockTakingRepository.getPartsStocktakingListPageable(form, siteId);
    }

    public SystemParameterVO findSystemParameterVOList(String siteId, Long facilityId, String paramTypeId, String paramValue) {
        return BeanMapUtils.mapTo(systemParameterRepository.getProcessingSystemParameter(siteId, facilityId, paramTypeId, paramValue), SystemParameterVO.class);
    }

    public List<SPQ030601BO> getPrintPartsStocktakingResultList(SPQ030601Form form, String siteId) {

        return productStockTakingRepository.getPrintPartsStocktakingResultList(form, siteId);
    }

    public SystemParameter getDisplayFlag(String typeId,String siteId) {

        return systemParameterRepository.findSystemParameterBySiteIdAndSystemParameterTypeId(siteId,typeId);
    }

}
