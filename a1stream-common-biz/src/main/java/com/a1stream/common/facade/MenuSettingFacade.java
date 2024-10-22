package com.a1stream.common.facade;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.DealerInfoBO;
import com.a1stream.common.model.MenuSettingForm;
import com.a1stream.common.service.MenuSettingService;
import com.a1stream.domain.vo.UserHabitVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author dong zhen
 */
@Slf4j
@Component
public class MenuSettingFacade {

    @Resource
    private MenuSettingService menuSettingService;

    /**
     * 判断是否需要切换经销商检查。
     * <p>
     * 此方法用于根据传入的表单信息判断是否需要进行切换经销商的检查。
     * 判断逻辑基于表单中的实际站点ID和基础站点ID的值。
     * 如果实际站点ID为默认站点ID，则直接返回true，表示需要切换经销商检查。
     * 如果基础站点ID为默认站点ID，则通过用户习惯服务获取用户习惯信息。
     * 如果用户习惯信息为空或习惯内容为空，则返回true，表示需要切换经销商检查。
     * 如果以上条件都不满足，则返回false，表示不需要切换经销商检查。
     *
     * @param form 表单信息，包含实际站点ID、基础站点ID、用户ID和用户习惯类型ID等字段。
     * @return Boolean 返回一个布尔值，表示是否需要切换经销商检查。
     */
    public Boolean isChangeDealerCheck(MenuSettingForm form) {

        if (CommonConstants.CHAR_DEFAULT_SITE_ID.equals(form.getRealSiteId())) {
            return true;
        } else if (CommonConstants.CHAR_DEFAULT_SITE_ID.equals(form.getBaseSiteId())) {
            UserHabitVO userHabitVO = menuSettingService.getUserHabit(form.getBaseSiteId(), form.getUserId(), form.getUserHabitTypeId());
            return userHabitVO == null || StringUtils.isEmpty(userHabitVO.getHabitContent());
        } else {
            return false;
        }
    }

    public List<DealerInfoBO> getAllDealerList(MenuSettingForm form) {

        return menuSettingService.getAllDealerList(form.getDealerRetrieve());
    }

    public void switchSystemDealer(MenuSettingForm form) {

        menuSettingService.switchSystemDealer(form);
    }

    public void clearSystemDealer(MenuSettingForm form) {

        menuSettingService.clearSystemDealer(form);
    }
}
