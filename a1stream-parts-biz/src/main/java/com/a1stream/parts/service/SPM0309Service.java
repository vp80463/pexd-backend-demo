package com.a1stream.parts.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.SPM030901BO;
import com.a1stream.domain.entity.ProductStockTaking;
import com.a1stream.domain.form.parts.SPM030901Form;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.ProductStockTakingRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.repository.WorkzoneRepository;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.ProductStockTakingVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.a1stream.domain.vo.WorkzoneVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
*
* 功能描述: 库存盘点时真实库存录入
*
* @author mid2215
*/
@Service
public class SPM0309Service {

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    @Resource
    private ProductStockTakingRepository productStockTakingRepository;

    @Resource
    private WorkzoneRepository workzoneRepository;

    @Resource
    private LocationRepository lcoaLocationRepository;

    @Resource
    private SystemParameterRepository systemParameterRepository;

    public List<SPM030901BO> findPartsActualStockList(SPM030901Form model) {

        return productStockTakingRepository.listProductStockTaking(model);
    }

    public MstFacilityVO findByFacilityId(Long pointId) {

        return BeanMapUtils.mapTo(mstFacilityRepository.findByFacilityId(pointId),MstFacilityVO.class);
    }

    public List<WorkzoneVO> findWorkzoneBySiteId(String siteId) {
        return BeanMapUtils.mapListTo(workzoneRepository.findBySiteId(siteId), WorkzoneVO.class);
    }

    public List<LocationVO> findLocationBysiteId(String siteId) {
        return BeanMapUtils.mapListTo(lcoaLocationRepository.findBySiteId(siteId), LocationVO.class);
    }

    public ProductStockTakingVO getByProductStockTakingId(Long productStockTakingId){
        return BeanMapUtils.mapTo(productStockTakingRepository.getByProductStockTakingId(productStockTakingId),ProductStockTakingVO.class);
    }

    public Integer getMaxSeqNo(String siteId, Long facilityId) {
        return productStockTakingRepository.getMaxSeqNo(siteId, facilityId);
    }

    public void saveOrUpdatePartsActualStock(ProductStockTakingVO productStockTakingVO) {

        productStockTakingRepository.save(BeanMapUtils.mapTo(productStockTakingVO, ProductStockTaking.class));
    }

    public void deletePartsActualStock(ProductStockTakingVO productStockTakingVO) {
        productStockTakingRepository.delete(BeanMapUtils.mapTo(productStockTakingVO, ProductStockTaking.class));
    }

    public List<ProductStockTakingVO> findByProductStockTakingIdIn(Set<Long> productStockTakings){

        return BeanMapUtils.mapListTo(productStockTakingRepository.findByProductStockTakingIdIn(productStockTakings), ProductStockTakingVO.class);
    }

    public void editPartsActualStockList(List<ProductStockTakingVO> updateList){

        productStockTakingRepository.saveInBatch(BeanMapUtils.mapListTo(updateList, ProductStockTaking.class));

    }

    public ProductStockTakingVO findByPorudctIdAndLocationId(Long partsId, Long locationId) {

        return BeanMapUtils.mapTo(productStockTakingRepository.findByProductIdAndLocationId(partsId, locationId), ProductStockTakingVO.class);
    }

    public SystemParameterVO findSystemParameterVOList(String siteId, Long facilityId, String paramTypeId, String paramValue) {
        return BeanMapUtils.mapTo(systemParameterRepository.getProcessingSystemParameter(siteId, facilityId, paramTypeId, paramValue), SystemParameterVO.class);
    }
}