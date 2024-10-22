package com.a1stream.ifs.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.ReturnRequestStatus;
import com.a1stream.common.logic.ConstantsLogic;
import com.a1stream.common.manager.MessageSendManager;
import com.a1stream.common.model.ConstantsBO;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.ReturnRequestItemVO;
import com.a1stream.domain.vo.ReturnRequestListVO;
import com.a1stream.ifs.bo.SpReturnApprovalModelBO;
import com.a1stream.ifs.service.SpReturnApprovalService;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Component
public class SpReturnApprovalFacade {

    @Resource
    private ConstantsLogic constantsLogic;

    @Resource
    private SpReturnApprovalService spReturnApprovalService;

    @Resource
    private MessageSendManager messageSendManager;

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepository;

    public void doReturnApprova(List<SpReturnApprovalModelBO> spReturnApprovalModelBOs){

        //1. Check input list is empty ? Return if it's empty.
        if(spReturnApprovalModelBOs ==null || spReturnApprovalModelBOs.isEmpty()) {
            return;
        }

        //文件中的dealerCode若不存在于cmm_site_master表中，则剔除出去-> update by lijiajun
        Set<String> dealerCds = spReturnApprovalModelBOs.stream().map(o -> o.getDealerCd()).collect(Collectors.toSet());
        List<CmmSiteMasterVO> cmmSiteMasters = BeanMapUtils.mapListTo(cmmSiteMasterRepository.findBySiteCdInAndActiveFlag(dealerCds,CommonConstants.CHAR_Y),CmmSiteMasterVO.class);
        Set<String> siteIds = cmmSiteMasters.stream().map(o -> o.getSiteId()).collect(Collectors.toSet());
        spReturnApprovalModelBOs = spReturnApprovalModelBOs.stream()
                                                           .filter(model -> siteIds.contains(model.getDealerCd()))
                                                           .collect(Collectors.toList());

        //group by dealerCode
        Map<String, List<SpReturnApprovalModelBO>> dealerCodelistMap = new HashMap<>();
        for (SpReturnApprovalModelBO returnModelBO : spReturnApprovalModelBOs) {

            if (dealerCodelistMap.containsKey(returnModelBO.getDealerCd())) {
                dealerCodelistMap.get(returnModelBO.getDealerCd()).add(returnModelBO);
            } else {
                List<SpReturnApprovalModelBO> newInputRow = new ArrayList<>();
                newInputRow.add(returnModelBO);
                dealerCodelistMap.put(returnModelBO.getDealerCd(), newInputRow);
            }
        }

        for (String dealerCode : dealerCodelistMap.keySet()) {

            StopWatch timer = new StopWatch();
            timer.start();
            Map<String,String> returnTypeCodeIdMap = new HashMap<>();
            Map<String,List<SpReturnApprovalModelBO>> consigneeCodeRowMap = new HashMap<>();
            Set<String> productCodes = new HashSet<>();
            List<String> recommendPointCodes = new ArrayList<>();

            List<ConstantsBO> returnRequestType = constantsLogic.getConstantsData(PJConstants.ReturnRequestType.class.getDeclaredFields());

            for(ConstantsBO constantsBO : returnRequestType){

                returnTypeCodeIdMap.put(constantsBO.getKey1(), constantsBO.getCodeDbid());
            }

            //2.group by consignee code.
            for(SpReturnApprovalModelBO inputRow : dealerCodelistMap.get(dealerCode) ){

                String pointCode = inputRow.getConsigneeCd();
                if (consigneeCodeRowMap.containsKey(pointCode)) {
                    consigneeCodeRowMap.get(pointCode).add(inputRow);
                } else {
                    List<SpReturnApprovalModelBO> newInputRow = new ArrayList<>();
                    newInputRow.add(inputRow);
                    consigneeCodeRowMap.put(pointCode, newInputRow);
                }
                productCodes.add(inputRow.getPartNo());
                recommendPointCodes.add(pointCode);
            }

            Set<String> facilityCodes = consigneeCodeRowMap.keySet();

            //1.Exchange site id with code.
            String siteId = spReturnApprovalService.getSiteIdByCode(dealerCode);

            //2.Exchange consignee id with code.
            Map<String,Long> facilityCodeIdMap = spReturnApprovalService.getPointCodeIdMapByCode(siteId,  facilityCodes);

            //3. Exchange product id with code.
            Map<String,Long> prdCodeIdMap = spReturnApprovalService.getProductCodeIdMapByCode(productCodes);

            //4.
            BigDecimal qty;
            BigDecimal qtyTotal = CommonConstants.BIGDECIMAL_ZERO;
            String expireDate = "";

            List<ReturnRequestListVO> newReturnReqListVOs = new ArrayList<ReturnRequestListVO>();
            List<ReturnRequestItemVO> newReturnReqItemVOs = new ArrayList<ReturnRequestItemVO>();
            for (String facilityCode : facilityCodes) {

                List<SpReturnApprovalModelBO> facilityItems = consigneeCodeRowMap.get(facilityCode);
                Long facilityId = facilityCodeIdMap.get(facilityCode);

                for(SpReturnApprovalModelBO partsReturnApprovalModel :facilityItems){

                    qty = NumberUtil.toBigDecimal(partsReturnApprovalModel.getApprovedQty());
                    qtyTotal = qtyTotal.add(qty);
                    if(NumberUtil.larger(qtyTotal, CommonConstants.BIGDECIMAL_ZERO))
                        break;
                }

                List<ReturnRequestListVO> returnRequestList = spReturnApprovalService.getReturnRequestList(siteId,facilityId);

                for (ReturnRequestListVO rrl : returnRequestList ) {

                    List<Long> requestItemIds = new ArrayList<>();
                    if (!NumberUtil.equals(qtyTotal, CommonConstants.BIGDECIMAL_ZERO)) {
                        // update status to approve
                        String approvalDate = DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER);
                        rrl.setRequestStatus(ReturnRequestStatus.APPROVED.getCodeDbid());
                        rrl.setApprovedDate(approvalDate);
                        expireDate = rrl.getExpireDate();

                        List<ReturnRequestItemVO> listItems = updateOrInsertData(rrl,facilityItems,siteId,prdCodeIdMap,returnTypeCodeIdMap,requestItemIds);
                        List<ReturnRequestItemVO> returnRequestItemVOList = spReturnApprovalService.getReturnRequestItemByListId(siteId, rrl.getReturnRequestListId());

                        for(ReturnRequestItemVO member:returnRequestItemVOList){

                            if (!requestItemIds.contains(member.getReturnRequestItemId())) {
                                if(member.getApprovedQty() != null && member.getApprovedQty().signum()==1){

                                    member.setRequestStatus(ReturnRequestStatus.APPROVED.getCodeDbid());
                                }else{

                                    member.setRequestStatus(ReturnRequestStatus.COMPLETED.getCodeDbid());
                                }
                                newReturnReqItemVOs.add(member);
                            }
                        }
                        newReturnReqItemVOs.addAll(listItems);
                    }else {
                        // update status to denied
                        rrl.setRequestStatus(ReturnRequestStatus.DENIED.getCodeDbid());
                        List<ReturnRequestItemVO> returnRequestItemVOList = spReturnApprovalService.getReturnRequestItemByListId(siteId, rrl.getReturnRequestListId());
                        for(ReturnRequestItemVO member:returnRequestItemVOList){

                            member.setRequestStatus(ReturnRequestStatus.COMPLETED.getCodeDbid());
                            newReturnReqItemVOs.add(member);
                        }
                    }
                    newReturnReqListVOs.add(rrl);
                }
            }

            //5. Save request list to DB in batch mode.
            spReturnApprovalService.saveRecommendationReturn(newReturnReqListVOs, newReturnReqItemVOs);

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
//          need to modify
            //7. send massage to Home page.
            if(newReturnReqListVOs.size() > 0) {
//                doSendMessageToHomePage(expireDate);
            }
        }
    }

//    private void doSendMessageToHomePage(String expireDate,String siteId) {
//
//        expireDate = DateUtil.changeFormat(expireDate, "yyyyMMdd", "yyyy/MM/dd");
//        String message = "Parts recommendation return list was imported, expired date is " + expireDate;
//        messageSendManager.notifyUserRolesInCode(siteId
//                                                        ,null //TODO Arrays.asList(Xm03SystemRoleConstants.USERROLE_NOSERIALIZED)
//                                                        ,XM03CodeInfoConstants.MessageTypeSub.KEY_ONEREADEDCLOSE
//                                                        ,XM03CodeInfoConstants.MessageCategorySub.KEY_INTERFACEINCOME, message);
//
//    }

    // update or insert data to db
    private List<ReturnRequestItemVO> updateOrInsertData(ReturnRequestListVO rrl
                                                        ,List<SpReturnApprovalModelBO> facilityItems
                                                        ,String siteId
                                                        ,Map<String,Long> prdCodeIdMap
                                                        ,Map<String,String> returnRequestTypeCodeMap
                                                        ,List<Long> requestItemIds){

        List<ReturnRequestItemVO>  returnRequestItemVOs = new ArrayList<>();

        for (SpReturnApprovalModelBO spPartsReturnApprovalModel : facilityItems) {

            String typeId = returnRequestTypeCodeMap.get(spPartsReturnApprovalModel.getRecommendType());

            List<ReturnRequestItemVO> returnRequestItemList = spReturnApprovalService.getReturnRequestItem(siteId
                                                                                    ,rrl.getReturnRequestListId()
                                                                                    ,prdCodeIdMap.get(spPartsReturnApprovalModel.getPartNo())
                                                                                    ,spPartsReturnApprovalModel.getYamahaInvoiceSeqNo()
                                                                                    ,typeId
                                                                                    ,NumberUtil.toBigDecimal(spPartsReturnApprovalModel.getPrice()));


            String appQty = spPartsReturnApprovalModel.getApprovedQty();
            BigDecimal approveQty = new BigDecimal("0");
            approveQty = StringUtils.isBlankText(appQty) ? CommonConstants.BIGDECIMAL_ZERO :NumberUtil.toBigDecimal(appQty);

            if (returnRequestItemList.size() > 0) {

                for (ReturnRequestItemVO rri : returnRequestItemList) {
                    // update returnRequestItem
                    rri.setApprovedQty(approveQty);
                    if (NumberUtil.larger(approveQty, CommonConstants.BIGDECIMAL_ZERO)) {
                        rri.setRequestStatus(ReturnRequestStatus.APPROVED.getCodeDbid());
                    } else {
                        rri.setRequestStatus(ReturnRequestStatus.COMPLETED.getCodeDbid());
                    }
                    returnRequestItemVOs.add(rri);
                    requestItemIds.add(rri.getReturnRequestItemId());
                }
            }else {
                // inert returnRequestItem

                ReturnRequestItemVO itemEntity = new ReturnRequestItemVO();
                itemEntity.setSiteId(siteId);
                itemEntity.setProductId(prdCodeIdMap.get(spPartsReturnApprovalModel.getPartNo()));
                itemEntity.setReturnRequestListId(rrl.getReturnRequestListId());
                itemEntity.setRequestType(typeId);
                itemEntity.setYamahaInvoiceSeqNo(spPartsReturnApprovalModel.getYamahaInvoiceSeqNo());
                itemEntity.setYamahaExternalInvoiceNo(spPartsReturnApprovalModel.getYamahaExternalInvoiceNo());
                itemEntity.setReturnPrice(NumberUtil.toBigDecimal(spPartsReturnApprovalModel.getPrice()).setScale(2));
                itemEntity.setApprovedQty(approveQty);
                if (NumberUtil.larger(approveQty, CommonConstants.BIGDECIMAL_ZERO)) {
                    itemEntity.setRequestStatus(ReturnRequestStatus.APPROVED.getCodeDbid());
                } else {
                    itemEntity.setRequestStatus(ReturnRequestStatus.COMPLETED.getCodeDbid());
                }
                returnRequestItemVOs.add(itemEntity);
            }
        }
        return returnRequestItemVOs;
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
