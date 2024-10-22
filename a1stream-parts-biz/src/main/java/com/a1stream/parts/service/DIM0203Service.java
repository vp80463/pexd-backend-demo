package com.a1stream.parts.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.entity.ProductStockTaking;
import com.a1stream.domain.entity.SystemParameter;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.ProductStockTakingRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.ProductStockTakingVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:re-upload Parts Data
*
* @author mid2178
*/
@Service
public class DIM0203Service {

    @Resource
    private SystemParameterRepository systemParameterRepo;

    @Resource
    private LocationRepository locationRepo;

    @Resource
    private ProductStockStatusRepository productStockStatusRepo;

    @Resource
    private ProductStockTakingRepository productStockTakingRepo;

    public Integer getLocationLine(Long facilityId, String siteId) {

        return locationRepo.countByFacilityIdAndSiteId(facilityId, siteId);
    }

    public Integer getStockLine(Long facilityId, String siteId) {

        return productStockStatusRepo.countByFacilityIdAndSiteId(facilityId, siteId);
    }

    public SystemParameterVO getProcessingSystemParameter(String siteId, Long facilityId, String paramTypeId, String paramValue) {

        return BeanMapUtils.mapTo(systemParameterRepo.getProcessingSystemParameter(siteId, facilityId, paramTypeId, paramValue),SystemParameterVO.class);
    }

    public List<ProductStockTakingVO> getProductStockTakingInfo(String siteId, Long facilityId) {

        return BeanMapUtils.mapListTo(productStockTakingRepo.findBySiteIdAndFacilityId(siteId, facilityId), ProductStockTakingVO.class);
    }

    public void doCancelData(SystemParameterVO sysParamUpdate, List<ProductStockTakingVO> proStoTakDel) {

        if(sysParamUpdate != null) {
            systemParameterRepo.save(BeanMapUtils.mapTo(sysParamUpdate,SystemParameter.class));
        }
        if(!proStoTakDel.isEmpty()) {
            productStockTakingRepo.deleteAllInBatch(BeanMapUtils.mapListTo(proStoTakDel,ProductStockTaking.class));
        }
    }
}
