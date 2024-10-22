package com.a1stream.web.app.ifs.consumer.sync;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.model.BaseInfResponse;

@RestController
@RequestMapping("public/a1stream")
public class IfsSyncTestController {

    //标准开发流程样例
    @PostMapping(value    = "/syncTest.json")
    public BaseInfResponse testController() {

        BaseInfResponse baseInfResponse = new BaseInfResponse();
        System.out.println("hahah excute: syncApiTest--------------------:");
        baseInfResponse.setCode("SUCCESS");
        baseInfResponse.setMessage("haha");
        baseInfResponse.setData("return data");
        return baseInfResponse;
    }
}