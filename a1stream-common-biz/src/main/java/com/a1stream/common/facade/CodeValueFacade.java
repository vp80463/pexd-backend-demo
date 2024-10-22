package com.a1stream.common.facade;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.logic.ConstantsLogic;
import com.a1stream.common.model.PrivacyPolicyCVBO;
import com.a1stream.common.model.PrivacyPolicyCVForm;
import com.a1stream.common.model.YmvnStockBO;
import com.a1stream.common.model.YmvnStockForm;
import com.a1stream.common.service.CodeValueService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

@Component
public class CodeValueFacade {

    private static final String CHAR_STATUS                           = "status";
    private static final String CHAR_AVAILABLE_QTY                    = "availableQty";

    private static final String ARG_ONE = "requestPassword";
    private static final String ARG_TWO = "requestTime";
    private static final String ARG_THREE = "requestId";
    private static final String ARG_FOUR = "dealerCode";
    private static final String ARG_FIVE = "partsNo";
    private static final String ARG_SIX = "sysOwnerCd";

    @Resource
    private CodeValueService codeValueService;

    @Resource
    private ConstantsLogic constantsLogic;

    public PrivacyPolicyCVBO getPrivacyPolicyResult(PrivacyPolicyCVForm model, String siteId) {

        return codeValueService.getPrivacyPolicyByNameAndPhone(siteId, model.getLastNm(), model.getMiddleNm(), model.getFirstNm() , model.getMobilePhone());
    }

    public YmvnStockBO getYmvnStock(YmvnStockForm form) {

        String requestPassword = codeValueService.getSystemParameter(MstCodeConstants.SystemParameterType.YMVNSTOCKPSW).getParameterValue();
        String sysOwnerCd = codeValueService.getSystemParameter(MstCodeConstants.SystemParameterType.YNSPRESPSYSOWNERCD).getParameterValue();

        String dealerCode = form.getSiteId();
        LocalDate currentDate = LocalDate.now();
        String rid = currentDate.toString();
        String partsNo = form.getPartsCd();
        String[][] ps = new String[][] {
                                        {ARG_ONE , requestPassword},
                                        {ARG_TWO , rid},
                                        {ARG_THREE, rid},
                                        {ARG_FOUR, dealerCode},
                                        {ARG_FIVE, partsNo},
                                        {ARG_SIX, sysOwnerCd}
                                    };
    
        String response = codeValueService.getYmvnStock(ps);
        if(StringUtils.isBlank(response)){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00400"));
        }
        JSONObject jsonObject = JSON.parseObject(response);

        YmvnStockBO ymvnStockBO = new YmvnStockBO();
        if(StringUtils.equals(jsonObject.getString(CHAR_STATUS), CommonConstants.CHAR_ZERO)){
            ymvnStockBO.setStockQty(jsonObject.getBigDecimal(CHAR_AVAILABLE_QTY));
        }
        return ymvnStockBO;

    }
}
