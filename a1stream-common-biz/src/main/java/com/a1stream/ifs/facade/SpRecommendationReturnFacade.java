
package com.a1stream.ifs.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.ReturnRequestStatus;
import com.a1stream.common.constants.PJConstants.ReturnRequestType;
import com.a1stream.common.logic.ConstantsLogic;
import com.a1stream.common.manager.MessageSendManager;
import com.a1stream.common.manager.RoleManager;
import com.a1stream.common.model.ConstantsBO;
import com.a1stream.domain.entity.ReturnRequestList;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ReturnRequestItemVO;
import com.a1stream.domain.vo.ReturnRequestListVO;
import com.a1stream.ifs.bo.SpRecommendationReturnModelBO;
import com.a1stream.ifs.service.SpRecommendationReturnService;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;

import jakarta.annotation.Resource;

@Component
public class SpRecommendationReturnFacade {

    @Resource
    private ConstantsLogic constantsLogic;

    @Resource
    private SpRecommendationReturnService spRecommendationReturnService;

    @Resource
    private MessageSendManager messageSendManager;

    @Resource
    private RoleManager roleManager;

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepository;

    public void doRecommendationReturn(List<SpRecommendationReturnModelBO> spRecommendationReturnModels){

        //1. Check input list is empty ? Return if it's empty.
        if(spRecommendationReturnModels ==null || spRecommendationReturnModels.isEmpty()) {
            return;
        }

        //文件中的dealerCode若不存在于cmm_site_master表中，则剔除出去-> update by lijiajun
        Set<String> dealerCds = spRecommendationReturnModels.stream().map(o -> o.getDealerCode()).collect(Collectors.toSet());
        List<CmmSiteMasterVO> cmmSiteMasters = BeanMapUtils.mapListTo(cmmSiteMasterRepository.findBySiteCdInAndActiveFlag(dealerCds,CommonConstants.CHAR_Y),CmmSiteMasterVO.class);
        Set<String> siteIds = cmmSiteMasters.stream().map(o -> o.getSiteId()).collect(Collectors.toSet());
        spRecommendationReturnModels = spRecommendationReturnModels.stream()
                                                                   .filter(model -> siteIds.contains(model.getDealerCode()))
                                                                   .collect(Collectors.toList());

        //group by dealerCode
        Map<String, List<SpRecommendationReturnModelBO>> dealerCodelistMap = new HashMap<>();
        for (SpRecommendationReturnModelBO returnModelBO : spRecommendationReturnModels) {

            if (dealerCodelistMap.containsKey(returnModelBO.getDealerCode())) {
                dealerCodelistMap.get(returnModelBO.getDealerCode()).add(returnModelBO);
            } else {
                List<SpRecommendationReturnModelBO> newInputRow = new ArrayList<>();
                newInputRow.add(returnModelBO);
                dealerCodelistMap.put(returnModelBO.getDealerCode(), newInputRow);
            }
        }

        for (String dealerCode : dealerCodelistMap.keySet()) {

            StopWatch timer = new StopWatch();
            timer.start();
            Map<String,String> returnTypeCodeIdMap = new HashMap<>();
            Map<String,List<SpRecommendationReturnModelBO>> consigneeCodeRowMap = new HashMap<>();
            Set<String> productCodes = new HashSet<>();
            Set<String> recommendPointCodes = new HashSet<>();

            String[] REQUEST_RET_DONE_STATUS = new String[] { ReturnRequestStatus.REQUESTED.getCodeDbid(),
                                                              ReturnRequestStatus.APPROVED.getCodeDbid(),
                                                              ReturnRequestStatus.ONPICKING.getCodeDbid()
                                                              };
            String[] REQUEST_RET_STATUS;

            List<ConstantsBO> returnRequestType = constantsLogic.getConstantsData(PJConstants.ReturnRequestType.class.getDeclaredFields());

            for(ConstantsBO constantsBO : returnRequestType){

                returnTypeCodeIdMap.put(constantsBO.getKey1(), constantsBO.getCodeDbid());
            }

            //Add 'recommend' to list.
            int l = REQUEST_RET_DONE_STATUS.length;
            REQUEST_RET_STATUS = new String[l + 1];
            System.arraycopy(REQUEST_RET_DONE_STATUS, 0, REQUEST_RET_STATUS, 0, l);
            REQUEST_RET_STATUS[l] = ReturnRequestStatus.RECOMMENDED.getCodeDbid();

            //2.group by consignee code.
            for(SpRecommendationReturnModelBO inputRow : dealerCodelistMap.get(dealerCode) ){

                String pointCode = inputRow.getPointCode();
                if (consigneeCodeRowMap.containsKey(pointCode)) {
                    consigneeCodeRowMap.get(pointCode).add(inputRow);
                } else {
                    List<SpRecommendationReturnModelBO> newInputRow = new ArrayList<>();
                    newInputRow.add(inputRow);
                    consigneeCodeRowMap.put(pointCode, newInputRow);
                }
                productCodes.add(inputRow.getPartsNo());
                if(!isScrapped(inputRow.getRecommendType(),returnTypeCodeIdMap)) { //Check if it's not scrapped.
                    recommendPointCodes.add(pointCode);
                }
            }

            Set<String> facilityCodes = consigneeCodeRowMap.keySet();

            //1.Exchange site id with code.
            String siteId = spRecommendationReturnService.getSiteIdByCode(dealerCode);

            //2.Exchange consignee id with code.
            Map<String,Long> facilityCodeIdMap = spRecommendationReturnService.getPointCodeIdMapByCode(siteId,  facilityCodes);

            //3. Exchange product id with code.
            Map<String,Long> prdCodeIdMap = spRecommendationReturnService.getProductCodeIdMapByCode(CommonConstants.CHAR_DEFAULT_SITE_ID, productCodes);

            //4. Exchange product id with code.
            List<MstProductVO> products = BeanMapUtils.mapListTo(mstProductRepository.findByProductCdInAndProductClassification(productCodes, ProductClsType.PART.getCodeDbid()),MstProductVO.class);
            Map<String,MstProductVO> productMap = products.stream().collect(Collectors.toMap(MstProductVO::getProductCd, Function.identity()));

            //5.
            String recommendDate=DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER);
            List<ReturnRequestListVO> newReturnReqListVOs = new ArrayList<ReturnRequestListVO>();
            List<ReturnRequestItemVO> newReturnReqItemVOs = new ArrayList<ReturnRequestItemVO>();
            String status;
            String expireDate = "";
            for (String facilityCode : facilityCodes) {

                List<SpRecommendationReturnModelBO> facilityItems = consigneeCodeRowMap.get(facilityCode);
                Long facilityId = facilityCodeIdMap.get(facilityCode);
                List<ReturnRequestList> existsRecommendRows = new ArrayList<ReturnRequestList>(1);

                //4.1 Check this facility has 'Requested' or 'Approved' or 'On Picking' status, then skip.
                boolean isSkip = spRecommendationReturnService.checkRequestHasDoneOnPoint(siteId
                                                               , facilityId
                                                               , REQUEST_RET_STATUS
                                                               , REQUEST_RET_DONE_STATUS
                                                               , existsRecommendRows);
                if(isSkip) { //skip
                    continue;
                }

                //4.2 Delete records when it's 'Recommend'
                if(!existsRecommendRows.isEmpty()) {
                    spRecommendationReturnService.deleteReturnRequestList(existsRecommendRows);
                }

                //3.3 Add new status records into DB.
                if(recommendPointCodes.contains(facilityCode)) {
                    //Do 'recommend' process.
                    status = ReturnRequestStatus.RECOMMENDED.getCodeDbid();
                }else {
                    //Do 'complete' process.
                    status = ReturnRequestStatus.COMPLETED.getCodeDbid();
                }
                expireDate = facilityItems.iterator().next().getExpireDate();
                ReturnRequestListVO rb = ReturnRequestListVO.create();
                rb.setSiteId(siteId);
                rb.setFacilityId(facilityId);
                rb.setRequestStatus(status);
                rb.setExpireDate(expireDate);
                rb.setRecommendDate(recommendDate);
                rb.setProductClassification(ProductClsType.PART.getCodeDbid());
                rb.setUpdateProgram(CommonConstants.CHAR_IFS);
                rb.setCreatedBy(CommonConstants.CHAR_IFS);
                rb.setLastUpdatedBy(CommonConstants.CHAR_IFS);

                String partsNo;
                for(SpRecommendationReturnModelBO row : facilityItems) {
                    partsNo = row.getPartsNo();

                    ReturnRequestItemVO itemEntity = new ReturnRequestItemVO();
                    itemEntity.setSiteId(siteId);
                    itemEntity.setFacilityId(facilityId);
                    itemEntity.setProductId(prdCodeIdMap.get(partsNo));
                    itemEntity.setProductCd(partsNo);
                    itemEntity.setProductNm(productMap.get(partsNo).getSalesDescription());
                    itemEntity.setRequestType(getReturnTypeIdByCode(row.getRecommendType(),facilityCode, partsNo,returnTypeCodeIdMap));
                    itemEntity.setYamahaInvoiceSeqNo(row.getInvoiceSeqNo());
                    itemEntity.setYamahaExternalInvoiceNo(row.getExternalInvoiceNo());
                    itemEntity.setReturnPrice(row.getPrice());
                    itemEntity.setRecommendQty(row.getRecommandQty());
                    itemEntity.setRequestStatus(rb.getRequestStatus());
                    itemEntity.setReturnRequestListId(rb.getReturnRequestListId());
                    itemEntity.setProductClassification(ProductClsType.PART.getCodeDbid());
                    itemEntity.setUpdateProgram(CommonConstants.CHAR_IFS);
                    itemEntity.setCreatedBy(CommonConstants.CHAR_IFS);
                    itemEntity.setLastUpdatedBy(CommonConstants.CHAR_IFS);

                    newReturnReqItemVOs.add(itemEntity);
                }
                //Add to waitting-insert list.

                newReturnReqListVOs.add(rb);
            }

            //5. Save request list to DB in batch mode.
            spRecommendationReturnService.saveRecommendationReturn(newReturnReqListVOs, newReturnReqItemVOs);

            // clear list
            if(returnTypeCodeIdMap != null) {
                returnTypeCodeIdMap.clear();
                returnTypeCodeIdMap = null;
            }

            if(consigneeCodeRowMap != null) {
                consigneeCodeRowMap.clear();
                consigneeCodeRowMap = null;
            }
            if(productCodes != null) {
                productCodes.clear();
                productCodes = null;
            }
            if(recommendPointCodes != null) {
                recommendPointCodes.clear();
                recommendPointCodes = null;
            }
            timer.stop();

            if(!newReturnReqListVOs.isEmpty()) {

                String sparepartCd = PJConstants.RoleCode.SPAREPART;
                String roleType = roleManager.getRoleTypeByDealerCode(sparepartCd);
                Long roleId = roleManager.getRoleIdByDealerCodeAndDealerType(sparepartCd, roleType);
                String message = "Parts recommendation return list was imported, expired date is " + expireDate;

                List<Long> roleIds = new ArrayList<>();
                Map<Long,String> roleCdMap = new HashMap<>();

                roleIds.add(roleId);
                roleCdMap.put(roleId,sparepartCd);

                messageSendManager.notifyUserRoles(siteId,roleIds,roleType,PJConstants.ProductClsType.PART.getCodeDbid(),message,"S098REPORTREADY",null);
            }
        }
    }

    private boolean isScrapped(String recommTypeCode,Map<String,String> returnTypeCodeIdMap) {

        return ReturnRequestType.KEY_SCRAP.getCodeDbid().equals(returnTypeCodeIdMap.get(recommTypeCode));
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

}
