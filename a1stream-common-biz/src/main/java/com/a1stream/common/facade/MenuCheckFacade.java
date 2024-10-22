package com.a1stream.common.facade;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.SystemParameterType;
import com.a1stream.common.model.MenuCheckBO;
import com.a1stream.common.service.MenuCheckService;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.vo.SysAdditionalSettingVO;
import com.a1stream.domain.vo.SystemParameterVO;

import jakarta.annotation.Resource;

/**
 * @author sucao
 */
@Component
public class MenuCheckFacade {

    @Resource
    private MenuCheckService menuCheckSer;

    /**
     * 夜间时段
     */
    public MenuCheckBO isMenuAccessTimePeriod(MenuCheckBO menuCheck) {

        // 当前时间
        String nowTime = ComUtil.nowTime();
        // 系统访问可用时段
        List<SystemParameterVO> menuAccessTimes = menuCheckSer.findMenuAccessTimeParam();
        Map<String, String> map = menuAccessTimes.stream().collect(Collectors.toMap(SystemParameterVO::getSystemParameterTypeId, SystemParameterVO::getParameterValue, (c1, c2) -> c1));
        String startTime = map.containsKey(SystemParameterType.ACCESS_START_TIME)? map.get(SystemParameterType.ACCESS_START_TIME) : "2200";
        String endTime = map.containsKey(SystemParameterType.ACCESS_END_TIME)? map.get(SystemParameterType.ACCESS_END_TIME) : "0600";
        if(StringUtils.compare(nowTime, startTime) > 0 || StringUtils.compare(nowTime, endTime) < 0) {
            menuCheck.setPermission(false);

            menuCheck.setMessage(ComUtil.t("errors.operationTimeCheck", new String[] {formatHM(startTime), formatHM(endTime)}));
        }

        return menuCheck;
    }

    private String formatHM(String inputStr) {

        if(inputStr.length() != 4) {
            return inputStr;
        }

        return inputStr.substring(0, 2) + ":" + inputStr.substring(2);
    }
    /**
     * 盘点状态时
     */
    public MenuCheckBO isPartsStockTaking(MenuCheckBO menuCheck, String siteId, String menuCd, Long pointId) {
        // 检查当前盘点进行中
        SystemParameterVO onStockTaking = menuCheckSer.findOnStockTakingParam(siteId, pointId);
        if (onStockTaking != null) {
            // 检查菜单是否设置检查
            List<SysAdditionalSettingVO> sysAdditionalSetting = menuCheckSer.getSysAdditionalSetting(siteId, menuCd);
            if (!sysAdditionalSetting.isEmpty()) {
                Optional<SysAdditionalSettingVO> options = sysAdditionalSetting.stream().filter(item -> StringUtils.equals(item.getPartsStocktakingCheck(), CommonConstants.CHAR_Y)) .findFirst();
                if (options.isPresent()) {
                    menuCheck.setPermission(false);
                    menuCheck.setMessage(ComUtil.t("errors.stockTaking"));
                }
            }
        }

        return menuCheck;
    }
}
