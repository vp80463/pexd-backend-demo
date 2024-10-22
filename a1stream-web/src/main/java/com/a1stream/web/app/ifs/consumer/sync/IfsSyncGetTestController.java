package com.a1stream.web.app.ifs.consumer.sync;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.model.BaseInfResponse;

@RestController
@RequestMapping("public/a1stream")
public class IfsSyncGetTestController {

    //标准开发流程样例
    @GetMapping(value    = "/syncGetTest.json")
    public BaseInfResponse testController(@RequestParam(name="paramTest") String paramTest) {

        BaseInfResponse baseInfResponse = new BaseInfResponse();
        System.out.println("hahah excute: syncGetApiTest--------------------:"+paramTest);
        baseInfResponse.setCode("200");
        baseInfResponse.setMessage("haha");
        baseInfResponse.setData("return data");
        return baseInfResponse;
    }
}
