package com.a1stream.web.app.controller.master;


import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.domain.bo.master.CMM050201BO;
import com.a1stream.master.facade.CMM0502Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;


/**
* 功能描述:
*
* @author mid2259
*/
@RestController
@RequestMapping("master/cmm0502")
public class CMM0502Controller implements RestProcessAware{

    @Resource
    private CMM0502Facade cmm0502Facade;

    @PostMapping(value = "/searchPartsLargeGroupList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CMM050201BO> searchPartsLargeGroupList() {

        return cmm0502Facade.searchPartsLargeGroupList();
    }
}