package com.a1stream.web.app.ifs.consumer.sync;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.model.BaseInfResponse;
import com.a1stream.ifs.facade.IfsSwToDmsFacade;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("public/a1stream")
@RestController
@FunctionId("IFS")
public class IfsSwToDmsController {

    @Resource
    private IfsSwToDmsFacade swToDmsFac;

    @PostMapping("/svAuthorizationNo.json")
    public BaseInfResponse svAuthorizationNo(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SV_AUTHORIZATIONNO, jsonString);
    }

    @PostMapping("/svBigBikeModelInfo.json")
    public BaseInfResponse svBigBikeModelInfo(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SV_BIGBIKEMODELINFO, jsonString);
    }

    @PostMapping("/svClaimJudgeResult.json")
    public BaseInfResponse svClaimJudgeResult(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SV_CLAIMJUDGERESULT, jsonString);
    }

    @PostMapping("/svCouponJudgeResult.json")
    public BaseInfResponse svCouponJudgeResult(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SV_COUPONJUDGERESULT, jsonString);
    }

    @PostMapping("/svJobFlatrate.json")
    public BaseInfResponse svJobFlatrate(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SV_JOBFLATRATE, jsonString);
    }

    @PostMapping("/svJobMaster.json")
    public BaseInfResponse svJobMaster(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SV_JOBMASTER, jsonString);
    }

    @PostMapping("/svModifiedSpecialClaimResult.json")
    public BaseInfResponse svModifiedSpecialClaimResult(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SV_MODIFIEDSPECIALCLAIMRESULT, jsonString);
    }

    @PostMapping("/svPayment.json")
    public BaseInfResponse svPayment(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SV_PAYMENT, jsonString);
    }

    @PostMapping("/svRegisterDoc.json")
    public BaseInfResponse svRegisterDoc(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SV_REGISTERDOC, jsonString);
    }

    @PostMapping("/svRegisterDocChange.json")
    public BaseInfResponse svRegisterDocChange(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SV_REGISTERDOCCHANGE, jsonString);
    }

    @PostMapping("/svSpecialClaimApplication.json")
    public BaseInfResponse svSpecialClaimApplication(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SV_SPECIALCLAIMAPPLICATION, jsonString);
    }

    @PostMapping("/svSpecialClaimMaster.json")
    public BaseInfResponse svSpecialClaimMaster(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SV_SPECIALCLAIMMASTER, jsonString);
    }

    @PostMapping("/svSpecialClaimTargetMc.json")
    public BaseInfResponse svSpecialClaimTargetMc(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SV_SPECIALCLAIMTARGETMC, jsonString);
    }

    @PostMapping("/svWarranty.json")
    public BaseInfResponse svWarranty(@RequestBody String jsonString) {

        return execute(InterfCode.IX_SV_WARRANTY, jsonString);
    }

    public BaseInfResponse execute(String interfCd, String jsonString) {

        BaseInfResponse response = new BaseInfResponse();
        response.setCode("SUCCESS");
        response.setMessage("");
        response.setData("");
        try {
            //获取上游系统发送过来的消息体
            JSONObject json = JSONObject.parseObject(jsonString);
            String detail = JSONArray.toJSONString(json.get("detail"));
            if(StringUtils.isEmpty(detail)) {
                swToDmsFac.doBusinessProcess(interfCd, detail);
            }
        }catch (Exception e) {
            log.error("InterfProcessResponse:", e);
            response.setCode("FAILURE");
            response.setMessage(e.getMessage());
            response.setData(e.getMessage());
        }

        return response;
    }
}
