package com.a1stream.web.app.controller.common;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.facade.MenuSettingFacade;
import com.a1stream.common.model.BaseResult;
import com.a1stream.common.model.MenuSettingForm;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dong zhen
 */
@RestController
@RequestMapping("common/menuSetting")
@FunctionId("menuSetting")
@Slf4j
public class MenuSettingController implements RestProcessAware {

    @Resource
    private MenuSettingFacade menuSettingFacade;

    @PostMapping(value = "/isChangeDealerCheck.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult isChangeDealerCheck(@RequestBody final MenuSettingForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        form.setBaseSiteId(uc.getCompanyCode());
        form.setRealSiteId(uc.getDealerCode());
        form.setUserId(uc.getUserId());
        form.setUserHabitTypeId(PJConstants.UserHabitConstant.ISCHANGEDEALER);

        result.setData(menuSettingFacade.isChangeDealerCheck(form));
        return result;
    }

    @PostMapping(value = "/dealerList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult dealerList(@RequestBody final MenuSettingForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        result.setData(menuSettingFacade.getAllDealerList(form));
        return result;
    }

    @PostMapping(value = "/switchSystemDealer.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult switchSystemDealer(@RequestBody final MenuSettingForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        form.setUserHabitTypeId(PJConstants.UserHabitConstant.ISCHANGEDEALER);
        form.setSiteId(uc.getCompanyCode());
        form.setUserId(uc.getUserId());
        menuSettingFacade.switchSystemDealer(form);
        result.setMessage(BaseResult.SUCCESS_MESSAGE);
        return result;
    }

    @PostMapping(value = "/clearSystemDealer.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult clearSystemDealer(@RequestBody final MenuSettingForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        BaseResult result = new BaseResult();
        form.setUserHabitTypeId(PJConstants.UserHabitConstant.ISCHANGEDEALER);
        form.setSiteId(uc.getCompanyCode());
        form.setUserId(uc.getUserId());
        menuSettingFacade.clearSystemDealer(form);
        result.setMessage(BaseResult.SUCCESS_MESSAGE);
        return result;
    }
}