package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.parts.SPM040101BO;
import com.a1stream.domain.form.parts.SPM040101Form;
import com.a1stream.domain.vo.AbcDefinitionInfoVO;
import com.a1stream.domain.vo.MstCodeInfoVO;
import com.a1stream.parts.service.SPM0401Service;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@Component
public class SPM0401Facade {

    @Resource
    private SPM0401Service spm0401Service;

    public List<SPM040101BO> searchRoqRopDefineList(SPM040101Form model,String siteId){

        List<AbcDefinitionInfoVO> abcDefinitionInfos = spm0401Service.searchProductByCategory(model,CommonConstants.CHAR_DEFAULT_SITE_ID);

        List<MstCodeInfoVO> mstCodeInfos = spm0401Service.searchPartsSafetyFactor(CommonConstants.CHAR_DEFAULT_SITE_ID);
        Map<BigDecimal,BigDecimal> map = new HashMap<>();
        for (MstCodeInfoVO mstCodeInfo : mstCodeInfos) {
            map.put(new BigDecimal(mstCodeInfo.getCodeData1()),new BigDecimal(mstCodeInfo.getCodeData2()) );
        }

        List<SPM040101BO> screenList = new ArrayList<>();
        for (AbcDefinitionInfoVO abcDefinitionInfo : abcDefinitionInfos) {

            SPM040101BO spm040101bo = new SPM040101BO();
            spm040101bo.setCostUsage(abcDefinitionInfo.getAbcType());
            spm040101bo.setTargetSupply(abcDefinitionInfo.getTargetSupplyRate() != null ? abcDefinitionInfo.getTargetSupplyRate().toString() : null);
            spm040101bo.setSafetyFactor(map.get(abcDefinitionInfo.getTargetSupplyRate()));
            spm040101bo.setSsmUpper(abcDefinitionInfo.getSsmUpper());
            spm040101bo.setSsmLower(abcDefinitionInfo.getSsmLower());
            spm040101bo.setAddLTDays(abcDefinitionInfo.getAddLeadtime());
            spm040101bo.setRopMonth(abcDefinitionInfo.getRopMonth());
            spm040101bo.setRoqMonth(abcDefinitionInfo.getRoqMonth());
            spm040101bo.setAbcDefinitionID(abcDefinitionInfo.getAbcDefinitionInfoId());

            screenList.add(spm040101bo);
        }

        return screenList;
    }

}