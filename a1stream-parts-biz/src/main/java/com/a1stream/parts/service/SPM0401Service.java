package com.a1stream.parts.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.MstCodeConstants.PartsSaftyFactor;
import com.a1stream.domain.form.parts.SPM040101Form;
import com.a1stream.domain.repository.AbcDefinitionInfoRepository;
import com.a1stream.domain.repository.MstCodeInfoRepository;
import com.a1stream.domain.vo.AbcDefinitionInfoVO;
import com.a1stream.domain.vo.MstCodeInfoVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@Service
public class SPM0401Service {

    @Resource
    private AbcDefinitionInfoRepository abcDefinitionInfoRepository;
    @Resource
    private MstCodeInfoRepository mstCodeInfoRepository;

    public List<AbcDefinitionInfoVO> searchProductByCategory(SPM040101Form model,String siteId) {

        return BeanMapUtils.mapListTo(abcDefinitionInfoRepository.findByProductCategoryIdAndSiteId(model.getLargeGroupId(),siteId), AbcDefinitionInfoVO.class);

    }

    public List<MstCodeInfoVO> searchPartsSafetyFactor(String siteId){

        return BeanMapUtils.mapListTo(mstCodeInfoRepository.findByCodeIdAndSiteId(PartsSaftyFactor.CODE_ID, siteId), MstCodeInfoVO.class);
    }

}