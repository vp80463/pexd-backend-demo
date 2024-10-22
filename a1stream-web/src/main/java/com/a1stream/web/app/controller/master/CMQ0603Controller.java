package com.a1stream.web.app.controller.master;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.domain.bo.master.CMQ060301BO;
import com.a1stream.domain.bo.master.CMQ060303BO;
import com.a1stream.domain.form.master.CMQ060301Form;
import com.a1stream.domain.form.master.CMQ060302Form;
import com.a1stream.domain.form.master.CMQ060303Form;
import com.a1stream.master.facade.CMQ0603Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@RestController
@RequestMapping("master/cmq0603")
public class CMQ0603Controller implements RestProcessAware {

    @Resource
    private CMQ0603Facade cmq0603Facade;

    @PostMapping(value = "/findServiceJobInquiryList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CMQ060301BO> findCampaignInquiryList(@RequestBody final CMQ060301Form form) {

        return cmq0603Facade.findCampaignInquiryList(form);
    }

    @PostMapping(value = "/cmq060302InitSearch.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CMQ060302Form initial02Screen(@RequestBody final CMQ060302Form form) {

        return cmq0603Facade.init060302Screen(form);
    }

    @PostMapping(value = "/cmq060303InitSearch.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CMQ060303BO> inital03Screen(@RequestBody final CMQ060303Form form) {

        return cmq0603Facade.inital0603Screen(form);
    }
}