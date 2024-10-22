package com.a1stream.master.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.master.CMM020101BO;
import com.a1stream.domain.entity.Location;
import com.a1stream.domain.form.master.CMM020101Form;
import com.a1stream.domain.repository.BinTypeRepository;
import com.a1stream.domain.repository.InventoryTransactionRepository;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.WorkzoneRepository;
import com.a1stream.domain.vo.BinTypeVO;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.WorkzoneVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
*
* 功能描述: Location Maintenance
*
* @author mid2215
*/
@Service
public class CMM0201Service {

    @Resource
    private LocationRepository locationRepository;

    @Resource
    private WorkzoneRepository workzoneRepository;

    @Resource
    private BinTypeRepository  binTypeRepository;

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    @Resource
    private ProductInventoryRepository productInventoryRepository;

    @Resource
    private InventoryTransactionRepository inventoryTransactionRepository;

    public Page<CMM020101BO> findLocInfoInquiryList(CMM020101Form model, String siteId) {

        return locationRepository.findLocInfoInquiryList(model, siteId);
    }

    public void deleteLocation(LocationVO locationVO) {
        locationRepository.delete(BeanMapUtils.mapTo(locationVO, Location.class));
    }

    public LocationVO findLocationByLocationId(Long locationId){
        return BeanMapUtils.mapTo(locationRepository.findByLocationId(locationId),LocationVO.class);
    }

    public void saveOrUpdateUser(LocationVO locationVO) {

        locationRepository.save(BeanMapUtils.mapTo(locationVO, Location.class));
    }

    public List<LocationVO> findLocExsits(String siteId, Long pointId, String location){
        return BeanMapUtils.mapListTo(locationRepository.findBySiteIdAndFacilityIdAndLocationCd(siteId, pointId, location),LocationVO.class);
    }

    public List<ProductInventoryVO> findBySiteIdAndFacilityIdAndLocationId(String siteId, Long facilityId, Long locationId) {
        return BeanMapUtils.mapListTo(productInventoryRepository.findBySiteIdAndFacilityIdAndLocationId(siteId, facilityId, locationId),ProductInventoryVO.class);
    }

    public List<Long> isLocationInUse(String siteId, Long facilityId, Long locationId) {
        return inventoryTransactionRepository.isLocationInUse(siteId,facilityId, locationId);
    }

    public MstFacilityVO findByFacilityId(Long pointId) {
        return BeanMapUtils.mapTo(mstFacilityRepository.findByFacilityId(pointId), MstFacilityVO.class);
    }

    public List<WorkzoneVO> findWorkzoneBySiteId(String siteId) {
        return BeanMapUtils.mapListTo(workzoneRepository.findBySiteId(siteId), WorkzoneVO.class);
    }

    public List<BinTypeVO> findBinTypeBySiteId(String siteId) {
        return BeanMapUtils.mapListTo(binTypeRepository.findBySiteId(siteId), BinTypeVO.class);
    }
}