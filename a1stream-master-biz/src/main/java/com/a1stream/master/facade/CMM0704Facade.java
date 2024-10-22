package com.a1stream.master.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.master.CMM070401BO;
import com.a1stream.domain.entity.SysAdditionalSetting;
import com.a1stream.domain.vo.SysAdditionalSettingVO;
import com.a1stream.master.service.CMM0704Service;
import com.ymsl.solid.base.json.JsonUtils;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Component
public class CMM0704Facade {

    @Resource
    private CMM0704Service cmm0704Service;

    public List<CMM070401BO> getMenuCheckList(String siteId) {

        List<CMM070401BO> result = new ArrayList<>();

    	String menuJson = cmm0704Service.getMenuJson();
        List<CMM070401BO> menuList = JsonUtils.toList(menuJson, CMM070401BO.class);

        List<SysAdditionalSetting> additionalSettings = cmm0704Service.findBySiteId(siteId);
        List<SysAdditionalSettingVO> menuCheckList = BeanMapUtils.mapListTo(additionalSettings, SysAdditionalSettingVO.class);
        Map<String, SysAdditionalSettingVO> menuCheckMap = menuCheckList.stream().collect(Collectors.toMap(SysAdditionalSettingVO::getMenuCode, Function.identity()));

        for(CMM070401BO item : menuList) {

            setMenuCheckResult(item, menuCheckMap);

            result.add(item);
        }

        return result;
    }

    // 递归赋值所有子节点
    private void setMenuCheckResult(CMM070401BO node, Map<String, SysAdditionalSettingVO> menuCheckMap) {
        if (node == null) return;
        setValue2MenuCheck(node, menuCheckMap);
        for (CMM070401BO item : node.getChildren()) {
            setValue2MenuCheck(item, menuCheckMap);
            setMenuCheckResult(item, menuCheckMap); // 递归提取子节点
        }
    }

    private void setValue2MenuCheck(CMM070401BO item, Map<String, SysAdditionalSettingVO> menuCheckMap) {
        if (menuCheckMap.containsKey(item.getMenuCd())) {
            SysAdditionalSettingVO setting = menuCheckMap.get(item.getMenuCd());

            item.setWorkDayCheck(setting.getWorkDayCheck());
            item.setAccountMonthCheck(setting.getAccountMonthCheck());
            item.setPartsStockTakingCheck(setting.getPartsStocktakingCheck());
            item.setUnitStockTakingCheck(setting.getUnitStocktakingCheck());
        }
    }

    public void saveMenuCheckList(List<CMM070401BO> result, String siteId) {

        Map<String, CMM070401BO> allData = new HashMap<>();
        for(CMM070401BO item : result) {
            extractChildren(item, allData);
        }
        List<CMM070401BO> settingData = allData.values().stream()
                .filter(item -> (StringUtils.equals(item.getWorkDayCheck(), CommonConstants.CHAR_Y)
                        || StringUtils.equals(item.getAccountMonthCheck(), CommonConstants.CHAR_Y)
                        || StringUtils.equals(item.getPartsStockTakingCheck(), CommonConstants.CHAR_Y)
                        || StringUtils.equals(item.getUnitStockTakingCheck(), CommonConstants.CHAR_Y) ))
                .collect(Collectors.toList());

        List<SysAdditionalSetting> saveAdditionalSettings = new ArrayList<>();
        for(CMM070401BO item: settingData) {
            SysAdditionalSetting setting = new SysAdditionalSetting();

            setting.setMenuId(item.getMenuId());
            setting.setMenuCode(item.getMenuCd());
            setting.setMenuName(item.getMenuName());
            setting.setSiteId(siteId);
            setting.setWorkDayCheck(item.getWorkDayCheck());
            setting.setAccountMonthCheck(item.getAccountMonthCheck());
            setting.setPartsStocktakingCheck(item.getPartsStockTakingCheck());
            setting.setUnitStocktakingCheck(item.getUnitStockTakingCheck());

            saveAdditionalSettings.add(setting);
        }

        cmm0704Service.saveSysAdditionalSettings(saveAdditionalSettings, siteId);
    }

    // 递归方法提取所有子节点
    public void extractChildren(CMM070401BO node, Map<String, CMM070401BO> map) {
        if (node == null) return;
        add2NodeMap(node, map);
        for (CMM070401BO child : node.getChildren()) {
            add2NodeMap(child, map);
            extractChildren(child, map); // 递归提取子节点
        }
    }

    private void add2NodeMap(CMM070401BO node, Map<String, CMM070401BO> map) {
        if (!map.containsKey(node.getMenuCd())) {
            map.put(node.getMenuCd(), node);
        }
    }
}
