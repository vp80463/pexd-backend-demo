package com.a1stream.common.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.SystemParameterType;
import com.a1stream.domain.entity.SysAdditionalSetting;
import com.a1stream.domain.entity.SystemParameter;
import com.a1stream.domain.repository.SysAdditionalSettingRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.SysAdditionalSettingVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
 * @author su cao
 */
@Service
public class MenuCheckService {

    @Resource
    private SystemParameterRepository systemParamRepo;

    @Resource
    private SysAdditionalSettingRepository sysAdditionalSettingRepo;

    public List<SystemParameterVO> findMenuAccessTimeParam() {

        List<String> accessTime = List.of(SystemParameterType.ACCESS_START_TIME, SystemParameterType.ACCESS_END_TIME);

        List<SystemParameter> menuAccessTimeParam = systemParamRepo.findBySiteIdAndSystemParameterTypeIdIn(CommonConstants.CHAR_DEFAULT_SITE_ID, accessTime);

        return BeanMapUtils.mapListTo(menuAccessTimeParam, SystemParameterVO.class);
    }

    public SystemParameterVO findOnStockTakingParam(String siteId, Long facilityId) {

        SystemParameter stockTakingParam = systemParamRepo.getProcessingSystemParameter(siteId, facilityId, SystemParameterType.STOCKTAKING, CommonConstants.CHAR_ONE);

        return BeanMapUtils.mapTo(stockTakingParam, SystemParameterVO.class);
    }

    public List<SysAdditionalSettingVO> getSysAdditionalSetting(String siteId, String menuCd) {

        List<SysAdditionalSetting> setting = sysAdditionalSettingRepo.findBySiteIdAndMenuCodeLike(siteId, menuCd + CommonConstants.CHAR_PERCENT);

        return BeanMapUtils.mapListTo(setting, SysAdditionalSettingVO.class);
    }
}
