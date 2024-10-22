package com.a1stream.ifs.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.ReturnRequestStatus;
import com.a1stream.common.constants.PJConstants.ReturnRequestType;
import com.a1stream.domain.entity.CmmSiteMaster;
import com.a1stream.domain.entity.MstCodeInfo;
import com.a1stream.domain.entity.MstFacility;
import com.a1stream.domain.entity.MstProduct;
import com.a1stream.domain.entity.ReturnRequestItem;
import com.a1stream.domain.entity.ReturnRequestList;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.repository.MstCodeInfoRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ReturnRequestItemRepository;
import com.a1stream.domain.repository.ReturnRequestListRepository;
import com.a1stream.domain.vo.ReturnRequestItemVO;
import com.a1stream.domain.vo.ReturnRequestListVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Service
public class SpRecommendationReturnService {

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepository;

    @Resource
    private MstCodeInfoRepository mstCodeInfoRepository;

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private ReturnRequestListRepository returnRequestListRepository;

    @Resource
    private ReturnRequestItemRepository returnRequestItemRepository;

    //根据查询出来的parts信息进行更新或者插入
    public void saveRecommendationReturn(List<ReturnRequestListVO> newReturnReqListVOs
                                       , List<ReturnRequestItemVO> newReturnReqItemVOs){

        //5. Save request list to DB in batch mode.
        this.saveReturnRequestListVO(newReturnReqListVOs);
        this.saveReturnRequestItemVO(newReturnReqItemVOs);
    }

    public String getReturnTypeIdByCode(String returnTypeCode
                                       , String pointCode
                                       , String partsNo
                                       , Map<String,String> returnTypeCodeIdMap) {

        Assert.hasText(returnTypeCode, String.format("Recommend type code is empty, point code:%s, parts no:%s.", pointCode, partsNo ));
        String foundId = returnTypeCodeIdMap.get(returnTypeCode);
        Assert.hasText(foundId, String.format("Can't find recommend type [%s] in local DB, point code:%s, parts no:%s.", returnTypeCode, pointCode, partsNo ));
        return foundId;
    }

    public String getSiteIdByCode(String siteCode) {

        Set<String> siteCodes = new HashSet<>();
        siteCodes.add(siteCode);
        List<CmmSiteMaster> site = cmmSiteMasterRepository.findBySiteIdIn(siteCodes);

        return (site.size()> 0) ? site.get(0).getSiteId() : null;
    }

    public Map<String,Long> getPointCodeIdMapByCode(String siteId, Set<String> consigneeCodeSet) {

        List<MstFacility> qry = mstFacilityRepository.findBySiteIdAndFacilityCdIn(siteId, consigneeCodeSet);
        Map<String,Long> facilityInfoMap = new HashMap<String,Long>();
        for(MstFacility facilityInfo : qry){
            facilityInfoMap.put(facilityInfo.getFacilityCd() ,facilityInfo.getFacilityId());
        }
        return facilityInfoMap;

    }

    public  Map<String,String> getAllReturnRequestTypeCodeIdMap() {

        List<MstCodeInfo> s = mstCodeInfoRepository.findByCodeId(ReturnRequestType.CODE_ID);
        Map<String,String> typeMap = new HashMap<String,String>();
        for(MstCodeInfo mstCodeInfo: s){
            typeMap.put(mstCodeInfo.getKey1(),mstCodeInfo.getCodeDbid());
        }

        return typeMap;
    }

    public Map<String, Long> getProductCodeIdMapByCode(String siteId, Set<String> productCodes) {

        List<MstProduct>  ps = mstProductRepository.findByProductCdInAndProductClassification(productCodes, ProductClsType.PART.getCodeDbid());
        Map<String, Long> pcdIdMap = new HashMap<>();

        for (MstProduct product : ps) {
            pcdIdMap.put(product.getProductCd(), product.getProductId());
        }

        //Check if all  the input product codes exist in DB.
        if(productCodes.size() != ps.size()) { //Not equals, find the not existing row.
            StringBuffer notExistsProductCodes = new StringBuffer();
            for(String inputCode : productCodes ) {
                if(!pcdIdMap.containsKey(inputCode)) {
                    if(notExistsProductCodes.length() >0){
                        notExistsProductCodes.append(",");
                    }
                    notExistsProductCodes.append(inputCode);
                }
            }
            Assert.isTrue(false, "There're some products not found for consingee in DB, including [" + notExistsProductCodes +  "]"
                        );
        }
        return pcdIdMap;
    }

    public boolean checkRequestHasDoneOnPoint(String siteId
                                             , Long pointId
                                             , String[] REQUEST_RET_STATUS
                                             , String[] REQUEST_RET_DONE_STATUS
                                             , List<ReturnRequestList> existsRecommendRows) {

        List<String> status = Arrays.asList(REQUEST_RET_STATUS);
        List<String> doneStatus = Arrays.asList(REQUEST_RET_DONE_STATUS);
        List<ReturnRequestList> reqs = returnRequestListRepository.findBySiteIdAndFacilityIdAndRequestStatusIn(siteId,pointId,status);
        for (ReturnRequestList item : reqs) {
            if(doneStatus.contains(item.getRequestStatus()))  { //It's the one of 3 status.
                return true;
            }else if(StringUtils.equals(ReturnRequestStatus.RECOMMENDED.getCodeDbid(),item.getRequestStatus())) { //It's 'recommend' status.
                existsRecommendRows.add(item);
            }
        }

        return false;
    }

    public void saveReturnRequestListVO(List<ReturnRequestListVO> returnRequestListListVOs) {

        List<ReturnRequestList> returnRequestLists = BeanMapUtils.mapListTo(returnRequestListListVOs, ReturnRequestList.class);
        returnRequestListRepository.saveInBatch(returnRequestLists);
    }

    public void saveReturnRequestItemVO(List<ReturnRequestItemVO> returnRequestItemVOList) {

        List<ReturnRequestItem> returnRequestItemList = BeanMapUtils.mapListTo(returnRequestItemVOList, ReturnRequestItem.class);
        returnRequestItemRepository.saveInBatch(returnRequestItemList);
    }

    public void deleteReturnRequestList(List<ReturnRequestList> existsRecommendRows) {

        returnRequestListRepository.deleteAllInBatch(existsRecommendRows);
    }

}
