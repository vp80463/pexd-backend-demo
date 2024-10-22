package com.a1stream.web.app.controller.parts;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.DIM020501BO;
import com.a1stream.domain.form.parts.DIM020501Form;
import com.a1stream.parts.facade.DIM0205Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:Work Zone Maintenance
*
* @author mid2178
*/
@RestController
@RequestMapping("parts/dim0205")
@FunctionId("DIM0205")
public class DIM0205Controller implements RestProcessAware {

    @Resource
    private DIM0205Facade dim0205Facade;

    /**
     * 查询workzone
     */
    @PostMapping(value = "/doInitial.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DIM020501BO> doInitial(@AuthenticationPrincipal final PJUserDetails uc) {

        return dim0205Facade.doInitial(uc.getDealerCode(), uc.getPersonId());
    }

    /**
     * 删除Workzone
     */
    @PostMapping(value = "/deleteWorkzone.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteWorkzone(@RequestBody final DIM020501Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        dim0205Facade.deleteWorkzone(form);
    }

    /**
     * 新建或修改Workzone
     */
    @PostMapping(value = "/newOrModifyWorkzone.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveOrUpdateWorkzone(@RequestBody final DIM020501Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        model.setSiteId(uc.getDealerCode());
        dim0205Facade.saveOrUpdateWorkzone(model);
    }
}
