package com.a1stream.master.facade;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.model.ConstantsBO;
import com.a1stream.domain.bo.master.CMM020101BO;
import com.a1stream.domain.form.master.CMM020101Form;
import com.a1stream.domain.vo.BinTypeVO;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.WorkzoneVO;
import com.a1stream.master.service.CMM0201Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
*
* 功能描述: Location Maintenance
*
* @author mid2215
*/
@Component
public class CMM0201Facade {

    @Resource
    private CMM0201Service cmm0201Service;

    /**
     * 根据条件查找Location
     */
    public Page<CMM020101BO> findLocInfoInquiryList(CMM020101Form model, String siteId) {

        if(ObjectUtils.isEmpty(model.getPointId())&& !ObjectUtils.isEmpty(model.getPoint())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.partsNo"), model.getPoint(), CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        Page<CMM020101BO> pageData = cmm0201Service.findLocInfoInquiryList(model, siteId);

        // 用pointId取到pointName
        MstFacilityVO mstFacilities = cmm0201Service.findByFacilityId(model.getPointId());

        // 取到所有LocationType ,格式:Map<LocationTypeId，LocationTypeName>
        List<ConstantsBO> locationTypeList = List.of(PJConstants.LocationType.TENTATIVE
                                                    ,PJConstants.LocationType.NORMAL
                                                    ,PJConstants.LocationType.FROZEN
                                                    ,PJConstants.LocationType.SERVICE);

        Map<String,String> locationTypeMap = locationTypeList.stream().collect(Collectors.toMap(ConstantsBO::getCodeDbid, ConstantsBO::getCodeData1));

        // 取到所有Workzone ,格式:Map<workzoneId，workzoneName>
        List<WorkzoneVO> workzonesList = cmm0201Service.findWorkzoneBySiteId(siteId);
        Map<Long,String> workzoneMap = workzonesList.stream().collect(Collectors.toMap(WorkzoneVO::getWorkzoneId, WorkzoneVO::getDescription));

        // 取到所有BinType ,格式:Map<binTypeId，binTypeName>
        List<BinTypeVO> binTypeList = cmm0201Service.findBinTypeBySiteId(siteId);
        Map<Long,String> binTypeMap = binTypeList.stream().collect(Collectors.toMap(BinTypeVO::getBinTypeId, BinTypeVO::getDescription));

        //将Location表中的Id映射成Name
        for(CMM020101BO detailModel : pageData.getContent()) {

            if(mstFacilities != null) {
                detailModel.setPoint(mstFacilities.getFacilityCd()+mstFacilities.getFacilityNm());
            }

            detailModel.setLocationType(locationTypeMap.get(detailModel.getLocationTypeId()));
            detailModel.setWz(workzoneMap.get(detailModel.getWzId()));
            detailModel.setBinType(binTypeMap.get(detailModel.getBinTypeId()));
        }

        return pageData;
    }

    /**
     * 删除Location
     */
    public void deleteLocation(CMM020101Form model) {

        LocationVO locationVO = cmm0201Service.findLocationByLocationId(model.getLocationId());

        // 删除前验证
        validateDeleteLocationInfo(locationVO);

        cmm0201Service.deleteLocation(locationVO);
    }

    /**
     * 删除Location前校验
     */
    private void validateDeleteLocationInfo(LocationVO locationVO) {

        // 如果Location ID已经被使用在product_inventory表,则提示用户
        List<ProductInventoryVO> productInventoryVO = cmm0201Service.findBySiteIdAndFacilityIdAndLocationId(locationVO.getSiteId(), locationVO.getFacilityId(), locationVO.getLocationId());

        // 如果Location ID已经被使用在inventory_transaction表，则提示用户
        List<Long> isLocationInUse = cmm0201Service.isLocationInUse(locationVO.getSiteId(), locationVO.getFacilityId(), locationVO.getLocationId());

        if(CollectionUtils.isNotEmpty(productInventoryVO) || CollectionUtils.isNotEmpty(isLocationInUse)) {
            throw new BusinessCodedException("M.E.00302");
        }
    }

    /**
     * 新建Location或更新Location
     */
    public void saveOrUpdateLocation(CMM020101Form model, String siteId) {

        // 新增Location前验证
        this.validateNewOrModifyLocationInfo(model, siteId);

        LocationVO locationVO = this.buildLocationVO(model, siteId);

        cmm0201Service.saveOrUpdateUser(locationVO);
    }

    private void validateNewOrModifyLocationInfo(CMM020101Form model, String siteId) {

        List<LocationVO> locations = cmm0201Service.findLocExsits(siteId, model.getPointId(), model.getLocation());

        // 新建时---LocationCd在DB中不重复
        if(model.getLocationId() == null && CollectionUtils.isNotEmpty(locations)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00301", new String[] {model.getLocation()}));
        }
    }

    /**
     * 创建一个Location
     */
    private LocationVO buildLocationVO(CMM020101Form model, String siteId) {

        LocationVO locationVO = model.getLocationId()!=null ? cmm0201Service.findLocationByLocationId(model.getLocationId()) : new LocationVO();

        locationVO.setSiteId(siteId);
        locationVO.setFacilityId(model.getPointId());
        locationVO.setWorkzoneId(model.getWzId());
        locationVO.setLocationType(model.getLocationTypeId());
        locationVO.setLocationCd(model.getLocation());
        locationVO.setBinTypeId(model.getBinTypeId());
        locationVO.setPrimaryFlag(model.getMainLocation());

        return locationVO;
    }
}
