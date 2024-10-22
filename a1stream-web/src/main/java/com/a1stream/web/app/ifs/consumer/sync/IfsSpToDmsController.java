package com.a1stream.web.app.ifs.consumer.sync;

import java.io.BufferedReader;
import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.model.BaseInfResponse;
import com.a1stream.ifs.facade.IfsSpToDmsFacade;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("public/a1stream")
@RestController
@FunctionId("IFS")
public class IfsSpToDmsController {

    @Resource
    private IfsSpToDmsFacade spToDmsFac;

    private static final String SUCCESS_CODE = "SUCCESS";
    private static final String FAILURE_CODE = "FAILURE";
    private static final String DETAIL = "detail";
    private static final String ERROR_LOG = "InterfProcessResponse";

    // SpToDms_PartsInfo
    @PostMapping(value = "/partsInfo.json")
    public BaseInfResponse partsInfo(@RequestBody String jsonString) {

        return execute(InterfCode.IMPORTTODMS_PARTSINFO, jsonString);
    }

    // SpToDms_SpCredit
    @PostMapping(value = "/credit.json")
    public BaseInfResponse credit(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SPCREDIT, jsonString);
    }

    // SpToDms_PurchaseDis
    @PostMapping(value = "/discountRate.json")
    public BaseInfResponse discountRate(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SPDISCOUNTRATE, jsonString);
    }

    // SpToDms_Manifest
    @PostMapping(value = "/manifest.json")
    public BaseInfResponse manifest(HttpServletRequest message) {

        String data = getMessage(message);
        return execute(InterfCode.SPMANIFEST, data);
    }

    // SpToDms_PartsTax
    @PostMapping(value = "/partsTax.json")
    public BaseInfResponse partsTax(@RequestBody String jsonString) {

        return execute(InterfCode.IX_PARTSTAX, jsonString);
    }

    // SpToDms_PurchaseCancel
    @PostMapping(value = "/purchaseCancel.json")
    public BaseInfResponse purchaseCancel(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SPPURCHASECANCEL, jsonString);
    }

    // SpToDMS_SpQuotation
    @PostMapping(value = "/quotation.json")
    public BaseInfResponse quotation(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SPQUOTATION, jsonString);
    }

    // SpToDms_spRecommendationReturn
    @PostMapping(value = "/recommendationReturn.json")
    public BaseInfResponse recommendationReturn(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SPRECOMMENDATIONRETURN, jsonString);
    }

    // SpToDms_spReturnApproval
    @PostMapping(value = "/returnApproval.json")
    public BaseInfResponse returnApproval(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SPRETURNAPPROVAL, jsonString);
    }

    // SpToDms_SpWholeSaleSign
    @PostMapping(value = "/wholeSaleSign.json")
    public BaseInfResponse wholeSaleSign(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SPWHOLESALESIGN, jsonString);
    }

    public BaseInfResponse execute(String interfCd, String jsonString) {

        BaseInfResponse response = new BaseInfResponse();
        response.setCode(SUCCESS_CODE);
        response.setMessage(CommonConstants.CHAR_BLANK);
        response.setData(CommonConstants.CHAR_BLANK);
        try {
            //获取上游系统发送过来的消息体
            JSONObject json = JSONObject.parseObject(jsonString);
            String detail = JSONArray.toJSONString(json.get(DETAIL));
            if(!StringUtils.isEmpty(detail)) {
                spToDmsFac.doBusinessProcess(interfCd, detail);
            }
        } catch (Exception e) {
            log.error(ERROR_LOG, e);
            response.setCode(FAILURE_CODE);
            response.setMessage(e.getMessage());
            response.setData(e.getMessage());
        }

        return response;
    }

    private String getMessage(HttpServletRequest message){

        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = message.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return requestBody.toString();
    }

}
