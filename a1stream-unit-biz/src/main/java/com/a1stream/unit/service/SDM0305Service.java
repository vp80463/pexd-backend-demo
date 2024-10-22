package com.a1stream.unit.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.SalesOrderStatus;
import com.a1stream.common.manager.ConsumerManager;
import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.domain.bo.unit.SDM030501BO;
import com.a1stream.domain.entity.CmmEmployeeInstruction;
import com.a1stream.domain.form.unit.SDM030501Form;
import com.a1stream.domain.repository.CmmEmployeeInstructionRepository;
import com.a1stream.domain.repository.CmmGeorgaphyRepository;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.SysUserAuthorityRepository;
import com.a1stream.domain.vo.CmmEmployeeInstructionVO;
import com.a1stream.domain.vo.CmmGeorgaphyVO;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:Employee Instruction
*
* mid2303
* 2024年10月9日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/10/09   Ruan Hansheng New
*/
@Service
public class SDM0305Service {

    @Resource
    private CmmEmployeeInstructionRepository cmmEmployeeInstructionRepository;

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepository;

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private CmmGeorgaphyRepository cmmGeorgaphyRepository;

    @Resource
    private SysUserAuthorityRepository sysUserAuthorityRepository;

    @Resource
    private ConsumerManager consumerManager;

    public List<SDM030501BO> getEmployeeInstructionList(SDM030501Form form) {

        return cmmEmployeeInstructionRepository.getEmployeeInstructionList(form);
    }

    public List<CmmSiteMasterVO> getCmmSiteMasterVOList(Set<String> siteIdSet) {

        return BeanMapUtils.mapListTo(cmmSiteMasterRepository.findBySiteIdIn(siteIdSet), CmmSiteMasterVO.class);
    }

    public List<MstProductVO> getMstProductVOList(List<String> modelCd) {

        return BeanMapUtils.mapListTo(mstProductRepository.findByProductCdIn(modelCd), MstProductVO.class);
    }

    public List<CmmGeorgaphyVO> getCmmGeographyVOList(List<String> geographyNmList) {

        return BeanMapUtils.mapListTo(cmmGeorgaphyRepository.findByGeographyNmIn(geographyNmList), CmmGeorgaphyVO.class);
    }

    public SysUserAuthorityVO getSysUserAuthorityVO(String userId) {

        return BeanMapUtils.mapTo(sysUserAuthorityRepository.findFirstByUserId(userId), SysUserAuthorityVO.class);
    }

    public SDM030501Form confirm(SDM030501Form form) {
        
        List<SDM030501BO> importList = form.getImportList();
        List<String> modelCdList = importList.stream().map(SDM030501BO::getModelCd).collect(Collectors.toList());
        List<MstProductVO> modelList = this.getMstProductVOList(modelCdList);
        Map<String, MstProductVO> modelMap = modelList.stream().collect(Collectors.toMap(MstProductVO::getProductCd, Function.identity()));

        List<CmmEmployeeInstructionVO> cmmEmployeeInstructionVOList = new ArrayList<>();
        for (SDM030501BO item : importList) {
            BaseConsumerForm baseConsumerForm = new BaseConsumerForm();
            baseConsumerForm.setSiteId(form.getSiteId());
            baseConsumerForm.setLastNm(item.getLastNm());
            baseConsumerForm.setMiddleNm(item.getMiddleNm());
            baseConsumerForm.setFirstNm(item.getFirstNm());
            baseConsumerForm.setMobilePhone(item.getMobilePhone());
            baseConsumerForm.setSns(item.getSns());
            baseConsumerForm.setGender(item.getGender());
            baseConsumerForm.setBirthYear(item.getBirthYear());
            baseConsumerForm.setBirthDate(item.getBirthDate());
            baseConsumerForm.setProvince(item.getProvinceId());
            baseConsumerForm.setDistrict(item.getDistrictId());
            baseConsumerForm.setAddress(item.getAddress());
            baseConsumerForm.setEmail(item.getEmail());

            consumerManager.saveOrUpdateConsumer(baseConsumerForm);

            CmmEmployeeInstructionVO cmmEmployeeInstructionVO = new CmmEmployeeInstructionVO();
            cmmEmployeeInstructionVO.setConsumerId(baseConsumerForm.getConsumerId());
            cmmEmployeeInstructionVO.setLastNm(item.getLastNm());
            cmmEmployeeInstructionVO.setMiddleNm(item.getMiddleNm());
            cmmEmployeeInstructionVO.setFirstNm(item.getFirstNm());
            cmmEmployeeInstructionVO.setConsumerNm(this.getConsumerFullNm(item.getLastNm(), item.getMiddleNm(), item.getFirstNm()));
            cmmEmployeeInstructionVO.setMobilePhone(item.getMobilePhone());
            cmmEmployeeInstructionVO.setSns(item.getSns());
            cmmEmployeeInstructionVO.setGender(item.getGender());
            cmmEmployeeInstructionVO.setProvinceGeographyId(item.getProvinceId());
            cmmEmployeeInstructionVO.setProvinceGeographyNm(item.getProvince());
            cmmEmployeeInstructionVO.setCityGeographyId(item.getDistrictId());
            cmmEmployeeInstructionVO.setCityGeographyNm(item.getDistrict());
            cmmEmployeeInstructionVO.setAddress(item.getAddress());
            cmmEmployeeInstructionVO.setBirthDate(item.getBirthDate());
            cmmEmployeeInstructionVO.setBirthYear(item.getBirthYear());
            cmmEmployeeInstructionVO.setOrderStatus(SalesOrderStatus.INSTRUCTION.getCodeDbid());
            cmmEmployeeInstructionVO.setModelCd(item.getModelCd());
            MstProductVO model = modelMap.get(item.getModelCd());
            cmmEmployeeInstructionVO.setModelNm(model.getSalesDescription());
            cmmEmployeeInstructionVO.setTaxRate(BigDecimal.TEN);
            cmmEmployeeInstructionVO.setModelType(model.getModelType());
            cmmEmployeeInstructionVO.setDisplacement(model.getDisplacement());
            cmmEmployeeInstructionVO.setEmail(item.getEmail());
            cmmEmployeeInstructionVO.setEmployeeCd(item.getEmployeeCd());
            cmmEmployeeInstructionVO.setEmployeeNm(item.getEmployeeNm());
            cmmEmployeeInstructionVO.setCmmProductId(model.getProductId());
            cmmEmployeeInstructionVO.setPurchaseType(item.getPurchaseType());
            cmmEmployeeInstructionVO.setEmployeeDiscount(item.getEmployeeDiscount());

            cmmEmployeeInstructionVOList.add(cmmEmployeeInstructionVO);
        }

        cmmEmployeeInstructionRepository.saveInBatch(BeanMapUtils.mapListTo(cmmEmployeeInstructionVOList, CmmEmployeeInstruction.class));

        return form;
    }

    public String getConsumerFullNm(String lastNm, String middleNm, String firstNm) {

        return new StringBuilder()
                  .append(lastNm)
                  .append(CommonConstants.CHAR_SPACE)
                  .append(middleNm)
                  .append(CommonConstants.CHAR_SPACE)
                  .append(firstNm)
                  .toString();
    }

    public SDM030501Form cancel(SDM030501Form form) {

        List<Long> cmmEmployeeInstructionIdList = form.getImportList().stream().map(SDM030501BO::getCmmEmployeeInstructionId).collect(Collectors.toList());
        List<CmmEmployeeInstructionVO> cmmEmployeeInstructionVOList = BeanMapUtils.mapListTo(cmmEmployeeInstructionRepository.findByCmmEmployeeInstructionIdIn(cmmEmployeeInstructionIdList), CmmEmployeeInstructionVO.class);
        cmmEmployeeInstructionRepository.deleteAllInBatch(BeanMapUtils.mapListTo(cmmEmployeeInstructionVOList, CmmEmployeeInstruction.class));

        return form;
    }
}
