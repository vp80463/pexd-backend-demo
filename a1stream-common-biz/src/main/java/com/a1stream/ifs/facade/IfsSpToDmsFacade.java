package com.a1stream.ifs.facade;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.bo.SpManifestItemBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.manager.ReceiptSlipManager;
import com.a1stream.ifs.bo.SpCreditBO;
import com.a1stream.ifs.bo.SpDiscountRateModelBO;
import com.a1stream.ifs.bo.SpPartsTaxModelBO;
import com.a1stream.ifs.bo.SpProductBO;
import com.a1stream.ifs.bo.SpPurchaseCancelModelBO;
import com.a1stream.ifs.bo.SpQuotationModelBO;
import com.a1stream.ifs.bo.SpRecommendationReturnModelBO;
import com.a1stream.ifs.bo.SpReturnApprovalModelBO;
import com.a1stream.ifs.bo.SpWholeSaleSignModelBO;
import com.alibaba.fastjson.JSONArray;
import com.ymsl.solid.base.util.CollectionUtils;

import jakarta.annotation.Resource;

@Component
public class IfsSpToDmsFacade {

    @Resource
    private SpProductFacade spProductFacade;

    @Resource
    private SpDiscountRateFacade spDiscountRateFacade;

    @Resource
    private ReceiptSlipManager receiptSlipManager;

    @Resource
    private SpPartsTaxFacade spPartsTaxFacade;

    @Resource
    private SpPurchaseCancelFacade spPurchaseCancelFacade;

    @Resource
    private SpQuotationFacade spQuotationFacade;

    @Resource
    private SpRecommendationReturnFacade recommendationReturnFacade;

    @Resource
    private SpReturnApprovalFacade spReturnApprovalFacade;

    @Resource
    private SpWholeSaleSignFacade spWholeSaleSignFacade;

    @Resource
    private SpCreditFacade spCreditFacade;

    public void doBusinessProcess(String interfCd, String detail) {

        switch(interfCd) {
            case InterfCode.IMPORTTODMS_PARTSINFO:
                spProductFacade.importPartsProductInfo(JSONArray.parseArray(detail, SpProductBO.class));
                break;
            case InterfCode.IX_SPCREDIT:
                spCreditFacade.doCredit(JSONArray.parseArray(detail, SpCreditBO.class));
                break;
            case InterfCode.IX_SPDISCOUNTRATE:
                spDiscountRateFacade.doDiscountRate(JSONArray.parseArray(detail, SpDiscountRateModelBO.class));
                break;
            case InterfCode.SPMANIFEST:
                receiptSlipManager.doManifestImports(JSONArray.parseArray(detail, SpManifestItemBO.class));
                break;
            case InterfCode.IX_PARTSTAX:
                spPartsTaxFacade.doPartsTax(JSONArray.parseArray(detail, SpPartsTaxModelBO.class));
                break;
            case InterfCode.IX_SPPURCHASECANCEL:
                spPurchaseCancelFacade.doPurchaseCancel(JSONArray.parseArray(detail, SpPurchaseCancelModelBO.class));
                break;
            case InterfCode.IX_SPQUOTATION:
                spQuotationFacade.doQuotation(JSONArray.parseArray(detail, SpQuotationModelBO.class));
                break;
            case InterfCode.IX_SPRECOMMENDATIONRETURN:
                recommendationReturnFacade.doRecommendationReturn(JSONArray.parseArray(detail, SpRecommendationReturnModelBO.class));
                break;
            case InterfCode.IX_SPRETURNAPPROVAL:
                spReturnApprovalFacade.doReturnApprova(JSONArray.parseArray(detail, SpReturnApprovalModelBO.class));
                break;
            case InterfCode.IX_SPWHOLESALESIGN:
                List<SpWholeSaleSignModelBO> results = JSONArray.parseArray(detail, SpWholeSaleSignModelBO.class);
                //将bodyList中的状态进行更改
                List<SpWholeSaleSignModelBO> transformedList = results.stream().map(model -> {
                    String status = model.getStatus();
                    if (CommonConstants.CHAR_ONE.equals(status)) {
                        model.setStatus(CommonConstants.CHAR_Y);
                    } else if (CommonConstants.CHAR_ZERO.equals(status)) {
                        model.setStatus(CommonConstants.CHAR_N);
                    }
                    return model;
                }) .collect(Collectors.toList());

                if(CollectionUtils.isNotEmpty(transformedList)) {
                    spWholeSaleSignFacade.doWholeSaleSign(transformedList);
                }
                break;
        }
    }
}
