package com.a1stream.master.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.domain.bo.master.CMM071701BO;
import com.a1stream.domain.entity.CmmServiceJobForDO;
import com.a1stream.domain.entity.MstProduct;
import com.a1stream.domain.form.master.CMM071701Form;
import com.a1stream.domain.repository.CmmServiceJobForDORepository;
import com.a1stream.domain.repository.MstProductCategoryRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.vo.CmmServiceJobForDOVO;
import com.a1stream.domain.vo.MstProductCategoryVO;
import com.a1stream.domain.vo.MstProductVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@Service
public class CMM0717Service {

    @Resource
    private CmmServiceJobForDORepository cmmServiceJobForDORepository;

    @Resource
    private MstProductCategoryRepository mstProductCategoryRepository;

    @Resource
    private MstProductRepository mstProductRepository;

    public List<CMM071701BO> findListByModelTypeCd(CMM071701Form form) {

        return cmmServiceJobForDORepository.findListByModelTypeCd(form);
    }

    public MstProductCategoryVO findMstProductCategoryVO(Long productCategoryId) {

        return BeanMapUtils.mapTo(mstProductCategoryRepository.findById(productCategoryId), MstProductCategoryVO.class);
    }

    public List<MstProductVO> getProductByCds(List<String> productCds, List<String> siteList) {

        return BeanMapUtils.mapListTo(mstProductRepository.getProductByCds(productCds, siteList, ProductClsType.SERVICE.getCodeDbid()), MstProductVO.class);
    }

    public List<MstProductCategoryVO> getMstProductCategoryList(List<String> categoryCdList) {

        return BeanMapUtils.mapListTo(mstProductCategoryRepository.findByCategoryCdIn(categoryCdList), MstProductCategoryVO.class);
    }

    public void importConfirm(CMM071701Form form) {

        // 删除表中的所有数据
        cmmServiceJobForDORepository.deleteAll();

        // 新增import导入的数据
        List<CmmServiceJobForDOVO> voList = new ArrayList<>();

        for(CMM071701BO bo : form.getImportList()) {

            CmmServiceJobForDOVO vo = new CmmServiceJobForDOVO();
            vo.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
            vo.setProductCategoryId(bo.getProductCategoryId());
            vo.setProductCategoryCd(bo.getModelTypeCd());
            vo.setProductCategoryNm(bo.getJobNm());

            if(bo.getJobId() == null) {

                MstProductVO mstProductVO = MstProductVO.create();
                mstProductVO.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
                mstProductVO.setProductCd(bo.getJobCd());
                mstProductVO.setLocalDescription(bo.getJobNm());
                mstProductVO.setProductClassification(ProductClsType.SERVICE.getCodeDbid());

                if (!ObjectUtils.isEmpty(mstProductVO) && !mstProductRepository.existsByProductCd(bo.getJobCd())) {
                    mstProductRepository.save(BeanMapUtils.mapTo(mstProductVO, MstProduct.class));
                }

                vo.setJobId(mstProductVO.getProductId());
            } else {

                vo.setJobId(bo.getJobId());
            }
            vo.setJobCd(bo.getJobCd());
            vo.setJobNm(bo.getJobNm());
            vo.setServiceCategory(ServiceCategory.REPAIR.getCodeDbid());
            vo.setManHours(bo.getLaborHours());
            vo.setLabourCost(bo.getLaborCost());
            vo.setTotalCost(bo.getTotalCost());
            vo.setUpdateCount(0);
            voList.add(vo);
        }

        cmmServiceJobForDORepository.saveInBatch(BeanMapUtils.mapListTo(voList, CmmServiceJobForDO.class));
    }

    public void updateConfirm(CMM071701Form form) {

        List<CmmServiceJobForDOVO> updateVOList = new ArrayList<>();
        List<Long> doIds = form.getUpdateGridDataList().getUpdateRecords().stream().map(CMM071701BO::getDoId).collect(Collectors.toList());
        List<CmmServiceJobForDOVO> cmmServiceJobForDOVOList = BeanMapUtils.mapListTo(cmmServiceJobForDORepository.findByServiceJobForDoIdIn(doIds), CmmServiceJobForDOVO.class);
        Map<Long, CmmServiceJobForDOVO> cmmServiceJobForDOVOMap = cmmServiceJobForDOVOList.stream().collect(Collectors.toMap(CmmServiceJobForDOVO::getServiceJobForDoId, Function.identity()));

        for(CMM071701BO bo : form.getUpdateGridDataList().getUpdateRecords()) {

            CmmServiceJobForDOVO vo = cmmServiceJobForDOVOMap.get(bo.getDoId());
            if(vo != null) {

                vo.setJobNm(bo.getJobNm());
                vo.setManHours(bo.getLaborHours());
                vo.setLabourCost(bo.getLaborCost());
                vo.setTotalCost(roundToNearestTenThousand(bo.getLaborHours(), bo.getLaborCost()));
                updateVOList.add(vo);
            }
        }

        cmmServiceJobForDORepository.saveInBatch(BeanMapUtils.mapListTo(updateVOList, CmmServiceJobForDO.class));
    }

    public BigDecimal roundToNearestTenThousand(BigDecimal laborHours, BigDecimal laborCost) {

        BigDecimal calcuValue = laborHours.multiply(laborCost);
        calcuValue = calcuValue.divide(new BigDecimal("10000"), 0, RoundingMode.HALF_UP).multiply(new BigDecimal("10000"));
        return calcuValue;
    }
}