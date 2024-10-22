package com.a1stream.unit.facade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.unit.SDM010901BO;
import com.a1stream.domain.form.unit.SDM010901Form;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.OrderSerializedItemVO;
import com.a1stream.domain.vo.ReceiptManifestItemVO;
import com.a1stream.domain.vo.ReceiptManifestSerializedItemVO;
import com.a1stream.domain.vo.ReceiptManifestVO;
import com.a1stream.domain.vo.ReceiptSerializedItemVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.SerializedProductTranVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.domain.vo.StoringLineItemVO;
import com.a1stream.domain.vo.StoringLineVO;
import com.a1stream.domain.vo.StoringSerializedItemVO;
import com.a1stream.unit.service.SDM0109Service;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.CollectionUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

/**
* 功能描述: Dealer Return By Vehicle
*
* mid2303
* 2024年8月29日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/08/29   Ruan Hansheng   New
*/
@Component
public class SDM0109Facade {

    @Resource
    private SDM0109Service sdm0109Service;

    public SDM010901Form getPointList(SDM010901Form form) {

        form.setPointList(sdm0109Service.getPointList());
        return form;
    }

    public SDM010901Form checkFile(SDM010901Form form) {

        // 上传的数据
        List<SDM010901BO> importList = form.getImportList();
        if (CollectionUtils.isEmpty(importList)) {
            return form;
        }

        Set<String> dealerCdSet = importList.stream().map(SDM010901BO::getDealerCd).collect(Collectors.toSet());
        List<CmmSiteMasterVO> cmmSiteMasterVOList = sdm0109Service.getCmmSiteMasterVOList(dealerCdSet);
        Map<String, CmmSiteMasterVO> cmmSiteMasterVOMap = cmmSiteMasterVOList.stream().collect(Collectors.toMap(CmmSiteMasterVO::getSiteId, Function.identity()));

        Set<String> pointCdSet = importList.stream().map(SDM010901BO::getPointCd).collect(Collectors.toSet());
        List<MstFacilityVO> mstFacilityVOList = sdm0109Service.getMstFacilityVOList(dealerCdSet, pointCdSet);
        Map<String, MstFacilityVO> mstFacilityVOMap = mstFacilityVOList.stream().collect(Collectors.toMap(vo -> vo.getSiteId() + vo.getFacilityCd(), Function.identity()));

        Set<String> frameNoSet = importList.stream().map(SDM010901BO::getFrameNo).collect(Collectors.toSet());
        List<CmmSerializedProductVO> cmmSerializedProductVOList = sdm0109Service.getCmmSerializedProductVOList(frameNoSet);
        Map<String, CmmSerializedProductVO> cmmSerializedProductVOMap = cmmSerializedProductVOList.stream().collect(Collectors.toMap(CmmSerializedProductVO::getFrameNo, Function.identity()));

        Set<Long> productIdSet = cmmSerializedProductVOList.stream().map(CmmSerializedProductVO::getProductId).collect(Collectors.toSet());
        List<MstProductVO> mstProductVOList = sdm0109Service.getMstProductVOList(productIdSet);
        Map<Long, MstProductVO> mstProductVOMap = mstProductVOList.stream().collect(Collectors.toMap(MstProductVO::getProductId, Function.identity()));

        frameNoSet = new HashSet<>();
        for (SDM010901BO importData : importList) {
            List<String> error          = new ArrayList<>();
            StringBuilder errorMsg = new StringBuilder();

            CmmSiteMasterVO cmmSiteMasterVO = cmmSiteMasterVOMap.get(importData.getDealerCd());
            MstFacilityVO mstFacilityVO = mstFacilityVOMap.get(importData.getDealerCd() + importData.getPointCd());
            CmmSerializedProductVO cmmSerializedProductVO = cmmSerializedProductVOMap.get(importData.getFrameNo());

            if (StringUtils.isBlankText(importData.getDealerCd())) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.10317"
                        , new Object[]{CodedMessageUtils.getMessage("label.dealerCode")
                                + CommonConstants.CHAR_LEFT_PARENTHESIS
                                + importData.getDealerCd() + CommonConstants.CHAR_RIGHT_PARENTHESIS}));
            } else {
                if (null == cmmSiteMasterVO) {
                    errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"
                            , new Object[]{CodedMessageUtils.getMessage("label.dealerCode")
                            , importData.getDealerCd()
                            , CodedMessageUtils.getMessage("label.tableCmmSiteMst")}));
                } else if (!form.getSiteId().equals(importData.getDealerCd())) {
                    errorMsg.append(CodedMessageUtils.getMessage("M.E.10165"
                            , new Object[]{importData.getDealerCd()}));
                }
            }

            if (StringUtils.isBlankText(importData.getPointCd())) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.10317"
                        , new Object[]{CodedMessageUtils.getMessage("label.pointCode")
                                + CommonConstants.CHAR_LEFT_PARENTHESIS
                                + importData.getPointCd() + CommonConstants.CHAR_RIGHT_PARENTHESIS}));
            } else {
                if (null == mstFacilityVO) {
                    errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"
                            , new Object[]{CodedMessageUtils.getMessage("label.pointCode")
                            , importData.getPointCd()
                            , CodedMessageUtils.getMessage("label.tableFacilityInfo")}));
                } else if (!form.getPointCd().equals(importData.getPointCd())) {
                    errorMsg.append(CodedMessageUtils.getMessage("M.E.10165"
                            , new Object[]{importData.getPointCd()}));
                }
            }

            if (StringUtils.isBlankText(importData.getDate())) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.10317"
                        , new Object[]{CodedMessageUtils.getMessage("label.date")
                                + CommonConstants.CHAR_LEFT_PARENTHESIS
                                + importData.getDate() + CommonConstants.CHAR_RIGHT_PARENTHESIS}));
            }

            if (StringUtils.isBlankText(importData.getFrameNo())) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.10317"
                        , new Object[]{CodedMessageUtils.getMessage("label.frameNumber")
                                + CommonConstants.CHAR_LEFT_PARENTHESIS
                                + importData.getFrameNo() + CommonConstants.CHAR_RIGHT_PARENTHESIS}));
            } else {
                if (null == cmmSerializedProductVO) {
                    errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"
                            , new Object[]{CodedMessageUtils.getMessage("label.frameNumber")
                            , importData.getFrameNo()
                            , CodedMessageUtils.getMessage("label.tableSerializedProductInfo")}));
                } else {
                    MstProductVO mstProductVO = mstProductVOMap.get(cmmSerializedProductVO.getProductId());
                    importData.setProductId(mstProductVO.getProductId());
                    importData.setProductCd(mstProductVO.getProductCd());
                    importData.setSerializedProductId(cmmSerializedProductVO.getSerializedProductId());
                }
            }

            if (frameNoSet.contains(importData.getFrameNo())) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"));
            }

            if (null != mstFacilityVO && null != cmmSerializedProductVO && mstFacilityVO.getFacilityId().compareTo(cmmSerializedProductVO.getFacilityId()) != 0) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.10204"
                        , new Object[]{CodedMessageUtils.getMessage("label.frameNumber")
                        , CodedMessageUtils.getMessage("label.pointCode")}));
            }

            frameNoSet.add(importData.getFrameNo());

            importData.setErrorMessage(errorMsg.toString());
            if (errorMsg.length() > 0) {
                error.add(errorMsg.toString());
            }
            importData.setError(error);
        }
        return form;
    }

    public void confirm(SDM010901Form form) {

        List<SDM010901BO> importList = form.getImportList();
        if (CollectionUtils.isEmpty(importList)) {
            return;
        }

        Set<String> frameNoSet = importList.stream().map(SDM010901BO::getFrameNo).collect(Collectors.toSet());
        // SerializedProduct
        List<SerializedProductVO> serializedProductVOList = sdm0109Service.getSerializedProductVOList(form.getSiteId(), frameNoSet);
        Set<Long> serializedProductIdSet = serializedProductVOList.stream().map(SerializedProductVO::getSerializedProductId).collect(Collectors.toSet());

        // ReceiptManifestSerializedItem
        List<ReceiptManifestSerializedItemVO> receiptManifestSerializedItemVOList = sdm0109Service.getReceiptManifestSerializedItemVOList(serializedProductIdSet, form.getSiteId());
        Set<Long> receiptManifestItemIdSet = receiptManifestSerializedItemVOList.stream().map(ReceiptManifestSerializedItemVO::getReceiptManifestItemId).collect(Collectors.toSet());

        // ReceiptManifestItem
        List<ReceiptManifestItemVO> receiptManifestItemVOList = sdm0109Service.getReceiptManifestItemVOList(receiptManifestItemIdSet);
        Set<Long> receiptManifestIdSet = receiptManifestItemVOList.stream().map(ReceiptManifestItemVO::getReceiptManifestId).collect(Collectors.toSet());

        // ReceiptManifest
        List<ReceiptManifestVO> receiptManifestVOList = sdm0109Service.getReceiptManifestVOList(receiptManifestIdSet);

        // ReceiptSerializedItem
        List<ReceiptSerializedItemVO> receiptSerializedItemVOList = sdm0109Service.getReceiptSerializedItemVOList(serializedProductIdSet, form.getSiteId());
        Set<Long> receiptSlipItemIdSet = receiptSerializedItemVOList.stream().map(ReceiptSerializedItemVO::getReceiptSlipItemId).collect(Collectors.toSet());

        // ReceiptSlipItem
        List<ReceiptSlipItemVO> receiptSlipItemVOList = sdm0109Service.getReceiptSlipItemVOList(receiptSlipItemIdSet);
        Set<Long> receiptSlipIdSet = receiptSlipItemVOList.stream().map(ReceiptSlipItemVO::getReceiptSlipId).collect(Collectors.toSet());

        // ReceiptSlip
        List<ReceiptSlipVO> receiptSlipVOList = sdm0109Service.getReceiptSlipVOList(receiptSlipIdSet);

        // StoringSerializedItem
        List<StoringSerializedItemVO> storingSerializedItemVOList = sdm0109Service.getStoringSerializedItemVOList(form.getSiteId(), serializedProductIdSet);
        Set<Long> storingLineItemIdSet = storingSerializedItemVOList.stream().map(StoringSerializedItemVO::getStoringLineItemId).collect(Collectors.toSet());

        // StoringLineItem
        List<StoringLineItemVO> storingLineItemVOList = sdm0109Service.getStoringLineItemVOList(storingLineItemIdSet);
        Set<Long> storingLineIdSet = storingLineItemVOList.stream().map(StoringLineItemVO::getStoringLineId).collect(Collectors.toSet());

        // StoringLine
        List<StoringLineVO> storingLineVOList = sdm0109Service.getStoringLineVOList(storingLineIdSet);

        // SerializedProductTran
        List<SerializedProductTranVO> serializedProductTranVOList = sdm0109Service.getSerializedProductTranVOList(serializedProductIdSet);

        // OrderSerializedItem
        List<OrderSerializedItemVO> orderSerializedItemVOList = sdm0109Service.getOrderSerializedItemVOList(serializedProductIdSet);

        sdm0109Service.confirm(form
                             , receiptManifestSerializedItemVOList
                             , receiptManifestItemVOList
                             , receiptManifestVOList
                             , receiptSerializedItemVOList
                             , receiptSlipItemVOList
                             , receiptSlipVOList
                             , storingSerializedItemVOList
                             , storingLineItemVOList
                             , storingLineVOList
                             , serializedProductTranVOList
                             , orderSerializedItemVOList);
    }
}
