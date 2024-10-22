package com.a1stream.master.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.master.CMM071701BO;
import com.a1stream.domain.form.master.CMM071701Form;
import com.a1stream.domain.vo.MstProductCategoryVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.master.service.CMM0717Service;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@Component
public class CMM0717Facade {

    @Resource
    private CMM0717Service cmm0717Service;

    public List<CMM071701BO> findRetrieveList(CMM071701Form form) {

        if(form.getModelTypeId() != null) {

            MstProductCategoryVO vo = cmm0717Service.findMstProductCategoryVO(form.getModelTypeId());
            form.setModelTypeCd(vo.getCategoryCd());
        }

        List<CMM071701BO> retrieveList = cmm0717Service.findListByModelTypeCd(form);

        Integer maxSeqNo = retrieveList.stream().map(CMM071701BO::getSeqNo).filter(seqNo -> seqNo != null).mapToInt(Integer::intValue).max().orElse(0);

        for(CMM071701BO bo : retrieveList) {

            bo.setSeqNo(++maxSeqNo);
        }

        return retrieveList;
    }

    public CMM071701Form checkFile(CMM071701Form form) {

        List<CMM071701BO> importList = form.getImportList();
        List<String> modelTypeCds = importList.stream().map(CMM071701BO::getModelTypeCd).distinct().collect(Collectors.toList());
        List<MstProductCategoryVO> modelTypeList = cmm0717Service.getMstProductCategoryList(modelTypeCds);
        Map<String, MstProductCategoryVO> productCategoryMap = modelTypeList.stream().collect(Collectors.toMap(MstProductCategoryVO::getCategoryCd, Function.identity()));

        List<String> productCds = importList.stream().map(CMM071701BO::getJobCd).distinct().collect(Collectors.toList());
        List<String> siteList = List.of(CommonConstants.CHAR_DEFAULT_SITE_ID);
        List<MstProductVO> mstProductList = cmm0717Service.getProductByCds(productCds, siteList);
        Map<String, MstProductVO> mstProductMap = mstProductList.stream().collect(Collectors.toMap(MstProductVO::getProductCd, Function.identity()));

        Map<String, String> jobCdJobNmMap = new HashMap<>();
        Set<String> modelJobKeys = new HashSet<>();

        Integer seq = CommonConstants.INTEGER_ONE;
        String key;

        for(CMM071701BO bo : importList) {

            StringBuilder errorMsg = new StringBuilder();
            List<String> error = new ArrayList<>();

            key = bo.getModelTypeCd() + "-" + bo.getJobCd();

            // modelTypeCd-jobCd唯一性check
            if(modelJobKeys.contains(key)) {

                errorMsg.append(CodedMessageUtils.getMessage("M.E.00304"
                                                           , new Object[]{CodedMessageUtils.getMessage("label.modelType") + "-" + CodedMessageUtils.getMessage("label.jobCode"), key}));
            } else {

                modelJobKeys.add(key);
            }

            // 检查jobCd是否已经存在且名称不同
            if(jobCdJobNmMap.containsKey(bo.getJobCd()) && !jobCdJobNmMap.get(bo.getJobCd()).equals(bo.getJobNm())) {

                errorMsg.append(CodedMessageUtils.getMessage("M.E.00405"
                                                           , new Object[]{CodedMessageUtils.getMessage("label.jobCode"), CodedMessageUtils.getMessage("label.jobName")}));
            } else {

                jobCdJobNmMap.put(bo.getJobCd(), bo.getJobNm());
            }

            bo.setErrorMessage(errorMsg.toString());
            if(StringUtils.isNotBlank(errorMsg.toString())){
                error.add(errorMsg.toString());
            }
            bo.setError(error);
            bo.setProductCategoryId(productCategoryMap.containsKey(bo.getModelTypeCd()) ? productCategoryMap.get(bo.getModelTypeCd()).getProductCategoryId() : null);
            bo.setJobId(mstProductMap.containsKey(bo.getJobCd()) ? mstProductMap.get(bo.getJobCd()).getProductId() : null);
            bo.setTotalCost(cmm0717Service.roundToNearestTenThousand(bo.getLaborHours(), bo.getLaborCost()));
            bo.setSeqNo(seq++);

            if(StringUtils.isNotBlank(bo.getErrorMessage())) {
                form.setErrorExistFlag(true);
            }
        }

        return form;
    }

    public void confirm(CMM071701Form form) {

        this.validationCheck(form);

        if(form.getImportList() == null) {

            cmm0717Service.updateConfirm(form);
        } else {

            // 如果是import,先删除cmm_service_job_for_do表中的所有数据,然后插入import的数据
            cmm0717Service.importConfirm(form);
        }
    }

    private void validationCheck(CMM071701Form form) {

        Map<String, String> jobCdJobNmMap = new HashMap<>();
        Set<String> modelJobKeys = new HashSet<>();
        String key;

        // 检查 jobCd 是否已经存在且名称不同
        for(CMM071701BO bo : form.getGridDataList()) {

            key = bo.getModelTypeCd() + "-" + bo.getJobCd();

            // modelTypeCd-jobCd唯一性check
            if(modelJobKeys.contains(key)) {

                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00304", new String[] {CodedMessageUtils.getMessage("label.modelType") + "-" + CodedMessageUtils.getMessage("label.jobCode"), key}));
            } else {

                modelJobKeys.add(key);
            }

            // 检查jobCd是否已经存在且名称不同
            if(jobCdJobNmMap.containsKey(bo.getJobCd()) && !jobCdJobNmMap.get(bo.getJobCd()).equals(bo.getJobNm())) {

                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00405", new String[] {CodedMessageUtils.getMessage("label.jobCode"), CodedMessageUtils.getMessage("label.jobName")}));
            } else {

                jobCdJobNmMap.put(bo.getJobCd(), bo.getJobNm());
            }
        }
    }
}