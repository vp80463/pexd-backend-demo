package com.a1stream.web.app.controller.master;


import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.domain.bo.master.CMM050301BO;
import com.a1stream.domain.form.master.CMM050301Form;
import com.a1stream.master.facade.CMM0503Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@RestController
@RequestMapping("master/cmm0503")
public class CMM0503Controller implements RestProcessAware{

    @Resource
    private CMM0503Facade cmm0503Facade;

    @PostMapping(value = "/searchPartsMiddleGroupList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CMM050301BO> searchPartsMiddleGroupList(@RequestBody final CMM050301Form model) {

        return cmm0503Facade.searchPartsMiddleGroupList(model);
    }

}