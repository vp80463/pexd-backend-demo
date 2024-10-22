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
import com.a1stream.domain.bo.parts.SPM030301BO;
import com.a1stream.domain.form.parts.SPM030301Form;
import com.a1stream.domain.vo.StoringLineVO;
import com.a1stream.parts.facade.SPM0303Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
 * 
* 功能描述: Parts Stock Register
*
* mid2287
* 2024年6月11日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/11   Wang Nan      New
 */
@RestController
@RequestMapping("parts/spm0303")
@FunctionId("SPM0303")
public class SPM0303Controller implements RestProcessAware {

    @Resource
    private SPM0303Facade spm0303Facade;

    @PostMapping(value = "/getStoringLineList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public StoringLineVO getStoringlineVO(@RequestBody final SPM030301Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        return spm0303Facade.getStoringlineVO(form);
    }

    @PostMapping(value = "/getStoringLineItemList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPM030301BO> getStoringLineItemList(@RequestBody final SPM030301Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        return spm0303Facade.getStoringLineItemList(form);
    }

    @PostMapping(value = "/confirmPartsStockRegister.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirmPartsStockRegister(@RequestBody final SPM030301Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        spm0303Facade.confirmPartsStockRegister(form, uc);
    }
}
