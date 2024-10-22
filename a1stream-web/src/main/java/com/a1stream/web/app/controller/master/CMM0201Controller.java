package com.a1stream.web.app.controller.master;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.master.CMM020101BO;
import com.a1stream.domain.form.master.CMM020101Form;
import com.a1stream.master.facade.CMM0201Facade;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
*
* 功能描述: Location Maintenance
*
* @author mid2215
*/
@RestController
@RequestMapping("master/cmm0201")
public class CMM0201Controller implements RestProcessAware{

    @Resource
    private CMM0201Facade cmm0201Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    /**
     * 根据条件查找Location
     */
    @PostMapping(value = "/findLocationInformationInquiryList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<CMM020101BO> findLocationInformationInquiryList(@RequestBody final CMM020101Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0201Facade.findLocInfoInquiryList(form, uc.getDealerCode());
    }

    /**
     * 删除Location
     */
    @PostMapping(value = "/deleteLocation.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteLocation(@RequestBody final CMM020101Form form) {
        cmm0201Facade.deleteLocation(form);
    }

    /**
     * 新建或修改Location
     */
    @PostMapping(value = "/newOrModifyLocation.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveOrUpdateLocation(@RequestBody final CMM020101Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        cmm0201Facade.saveOrUpdateLocation(model, uc.getDealerCode());
    }
}