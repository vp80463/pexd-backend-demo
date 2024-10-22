package com.a1stream.parts.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.SPM030801BO;
import com.a1stream.domain.entity.ProductStockTaking;
import com.a1stream.domain.entity.SystemParameter;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ProductStockTakingRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.ProductStockTakingVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:start stockTaking
*
* @author mid2178
*/
@Service
public class SPM0308Service {

    @Resource
    private SystemParameterRepository systemParameterRepo;

    @Resource
    private ProductStockTakingRepository productStockTakingRepo;

    @Resource
    private ProductInventoryRepository productInventoryRepo;

    @Resource
    private MstFacilityRepository mstFacilityRepo;

    public SystemParameterVO getProcessingSystemParameter(String siteId, Long facilityId, String paramTypeId, String paramValue) {

        return BeanMapUtils.mapTo(systemParameterRepo.getProcessingSystemParameter(siteId, facilityId, paramTypeId, paramValue), SystemParameterVO.class);
    }

    public SystemParameterVO getSystemParameterInfo(String siteId, Long facilityId, String systemParameterTypeId) {

        return BeanMapUtils.mapTo(systemParameterRepo.findBySiteIdAndFacilityIdAndSystemParameterTypeId(siteId, facilityId, systemParameterTypeId), SystemParameterVO.class);
    }

    public List<ProductStockTakingVO> getProductStockTakingInfo(String siteId, Long facilityId) {

        return BeanMapUtils.mapListTo(productStockTakingRepo.findBySiteIdAndFacilityId(siteId, facilityId), ProductStockTakingVO.class);
    }

    public void updateData(SystemParameterVO sysParam, List<ProductStockTakingVO> proStoTakDel, List<ProductStockTakingVO> proStoTakList) {

        if(sysParam != null) {
            systemParameterRepo.save(BeanMapUtils.mapTo(sysParam, SystemParameter.class));
        }
        if(!proStoTakDel.isEmpty()) {
            productStockTakingRepo.deleteAllInBatch(BeanMapUtils.mapListTo(proStoTakDel, ProductStockTaking.class));
        }
        productStockTakingRepo.saveInBatch(BeanMapUtils.mapListTo(proStoTakList, ProductStockTaking.class));
    }

    public List<SPM030801BO> getStockOnlyLocationInfo(String siteId, Long facilityId, String costType, String productClassification) {

        return productInventoryRepo.getStockOnlyLocationInfo(siteId, facilityId, costType, productClassification);
    }

    public List<SPM030801BO> getAllLocationInfo(String siteId, Long facilityId, String productClassification) {

        return productInventoryRepo.getAllLocationInfo(siteId, facilityId, productClassification);
    }

    public MstFacilityVO getFacilityCdAndFacilityNm(Long facilityId) {

        return BeanMapUtils.mapTo(mstFacilityRepo.findByFacilityId(facilityId), MstFacilityVO.class);
    }
}
