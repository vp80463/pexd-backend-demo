/**
 *
 */
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
import com.a1stream.domain.bo.parts.SPM020202FunctionBO;
import com.a1stream.domain.form.parts.SPM020202Form;
import com.a1stream.parts.facade.SPM020202Facade;

import jakarta.annotation.Resource;

/**
* 功能描述: Sales Return History Inquiry
*
* mid2330
* 2024年7月2日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/07/02   Liu Chaoran   New
*/
@RestController
@RequestMapping("parts/spm020202")
@FunctionId("SPM020202")
public class SPM020202Controller {
    @Resource
    private SPM020202Facade spm020202Facade;

    @PostMapping(value = "/getSalesReturnHistoryHeaderList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPM020202FunctionBO> getSalesReturnHistoryHeaderList(@RequestBody final SPM020202Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return spm020202Facade.getSalesReturnHistoryHeaderList(form,uc.getDealerCode());
    }

    @PostMapping(value = "/getSalesReturnHistoryDetail.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPM020202FunctionBO> getSalesReturnHistoryDetailList(@RequestBody final SPM020202Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return spm020202Facade.getSalesReturnHistoryDetailList(form,uc.getDealerCode());
    }
}
