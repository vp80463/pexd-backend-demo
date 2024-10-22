package com.a1stream.unit.facade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.unit.SDM060101BO;
import com.a1stream.domain.form.unit.SDM060101Form;
import com.a1stream.domain.vo.CmmSpecialCompanyTaxVO;
import com.a1stream.unit.service.SDM0601Service;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Component
public class SDM0601Facade {

    @Resource
    private SDM0601Service sdm0601Ser;

    public Page<SDM060101BO> pageCusTaxList(SDM060101Form model) {

        return sdm0601Ser.pageCusTaxList(model);
    }

    public List<SDM060101BO> listCusTaxList(SDM060101Form model) {

        return sdm0601Ser.listCusTaxList(model);
    }

    public void deleteCusTax(Long taxId) {

        sdm0601Ser.deleteCmmSpecCompanyTax(taxId);
    }

    public void saveCusTaxData(SDM060101Form model) {

        Set<Long> delOldCusTaxIds = new HashSet<>();
        List<CmmSpecialCompanyTaxVO> saveCusTaxList = new ArrayList<>();

        // 导入数据时，进行全删全加操作：20241012
        List<SDM060101BO> insertRecords = model.getCusTaxData().getInsertRecords();
        if(!insertRecords.isEmpty() && StringUtils.equals(insertRecords.get(0).getImportFlag(), CommonConstants.CHAR_ONE)) {
            delOldCusTaxIds = sdm0601Ser.getDelOldIds();
        }

        List<SDM060101BO> newOrUpdRecords = model.getCusTaxData().getNewUpdateRecords();
        Set<Long> cusTaxIds = newOrUpdRecords.stream().map(SDM060101BO::getTaxId).collect(Collectors.toSet());
        Map<Long, CmmSpecialCompanyTaxVO> cusTaxIdMap = sdm0601Ser.getCusTaxIdMap(cusTaxIds);

        Set<String> cusTaxCodes = newOrUpdRecords.stream().map(SDM060101BO::getCusTaxCode).collect(Collectors.toSet());
        Map<String, CmmSpecialCompanyTaxVO> cusTaxCodeMap = sdm0601Ser.getCusTaxCodeMap(cusTaxCodes);
        Set<String> uniqueTaxCds = new HashSet<>();
        for(SDM060101BO item : newOrUpdRecords) {
            // 校验重复code
            if(uniqueTaxCds.contains(item.getCusTaxCode()) || cusTaxCodeMap.containsKey(item.getCusTaxCode())){
                throw new BusinessCodedException(ComUtil.t("M.E.00304", new String[] { ComUtil.t("label.cusTaxCode"), item.getCusTaxCode() }));
            }
            CmmSpecialCompanyTaxVO cusTax;
            if (cusTaxIdMap.containsKey(item.getTaxId())) {
                cusTax = cusTaxIdMap.get(item.getTaxId());
                // check是否已被更新
                if(!item.getUpdateCount().equals(cusTax.getUpdateCount())){
                    throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[]{ ComUtil.t("label.cusTaxCode"), item.getCusTaxCode(), ComUtil.t("label.cusTaxInformation")}));
                }
            } else {
                cusTax = new CmmSpecialCompanyTaxVO();
                cusTax.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
            }
            cusTax.setSpecialCompanyTaxCd(item.getCusTaxCode());
            cusTax.setSpecialCompanyTaxNm(item.getCusTaxName());
            cusTax.setAddress(item.getAddress());

            uniqueTaxCds.add(item.getCusTaxCode());
            saveCusTaxList.add(cusTax);
        }

        sdm0601Ser.saveCusTaxData(delOldCusTaxIds, saveCusTaxList);
    }

    public List<SDM060101BO> checkImport(List<SDM060101BO> resultList) {

        Set<String> cusTaxCodes = resultList.stream().map(SDM060101BO::getCusTaxCode).collect(Collectors.toSet());
        Map<String, CmmSpecialCompanyTaxVO> cusTaxCodeMap = sdm0601Ser.getCusTaxCodeMap(cusTaxCodes);

        Set<String> uniqueTaxCds = new HashSet<>();
        for(SDM060101BO bo : resultList){

            List<String> error = new ArrayList<>();
            StringBuilder errorMsg = new StringBuilder();
            if(uniqueTaxCds.contains(bo.getCusTaxCode())){
                errorMsg.append(ComUtil.t("M.E.00304 ", new String[] { ComUtil.t("label.cusTaxCode"), bo.getCusTaxCode() } ));
                error.add(errorMsg.toString());
            }
            if (cusTaxCodeMap.containsKey(bo.getCusTaxCode())) {
                errorMsg.append(ComUtil.t("M.E.00304", new String[] { ComUtil.t("label.cusTaxCode"), bo.getCusTaxCode() }));
                error.add(errorMsg.toString());
            }
            uniqueTaxCds.add(bo.getCusTaxCode());
            bo.setImportFlag(CommonConstants.CHAR_ONE);
            bo.setErrorMessage(errorMsg.toString());
            bo.setError(error);
        }

        return resultList;
    }
}
