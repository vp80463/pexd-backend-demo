package com.a1stream.unit.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.unit.SDM060101BO;
import com.a1stream.domain.entity.CmmSpecialCompanyTax;
import com.a1stream.domain.form.unit.SDM060101Form;
import com.a1stream.domain.repository.CmmSpecialCompanyTaxRepository;
import com.a1stream.domain.vo.CmmSpecialCompanyTaxVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Service
public class SDM0601Service{

    @Resource
    private CmmSpecialCompanyTaxRepository cmmSpecCompanyTaxRepo;

    public Page<SDM060101BO> pageCusTaxList(SDM060101Form model) {

        return cmmSpecCompanyTaxRepo.pageCusTaxList(model);
    }

    public List<SDM060101BO> listCusTaxList(SDM060101Form model) {

        return cmmSpecCompanyTaxRepo.listCusTaxList(model);
    }

    public Set<Long> getDelOldIds(){

        List<CmmSpecialCompanyTaxVO> delOldCusTaxList = BeanMapUtils.mapListTo(cmmSpecCompanyTaxRepo.findAll(), CmmSpecialCompanyTaxVO.class);

        return delOldCusTaxList.stream().map(CmmSpecialCompanyTaxVO::getSpecialCompanyTaxId).collect(Collectors.toSet());
    }

    public Map<String, CmmSpecialCompanyTaxVO> getCusTaxCodeMap(Set<String> taxCodes){

        List<CmmSpecialCompanyTaxVO> cusTaxCodeList = BeanMapUtils.mapListTo(cmmSpecCompanyTaxRepo.findBySpecialCompanyTaxCdIn(taxCodes),CmmSpecialCompanyTaxVO.class);

        return cusTaxCodeList.stream().collect(Collectors.toMap(CmmSpecialCompanyTaxVO::getSpecialCompanyTaxCd, vo -> vo));
    }

    public Map<Long, CmmSpecialCompanyTaxVO> getCusTaxIdMap(Set<Long> taxIds){

        List<CmmSpecialCompanyTaxVO> cusTaxCodeList = BeanMapUtils.mapListTo(cmmSpecCompanyTaxRepo.findBySpecialCompanyTaxIdIn(taxIds),CmmSpecialCompanyTaxVO.class);

        return cusTaxCodeList.stream().collect(Collectors.toMap(CmmSpecialCompanyTaxVO::getSpecialCompanyTaxId, vo -> vo));
    }

    public void deleteCmmSpecCompanyTax(Long taxId) {

        cmmSpecCompanyTaxRepo.deleteById(taxId);
    }

    public void saveCusTaxData(Set<Long> delOldIds, List<CmmSpecialCompanyTaxVO> saveCusTaxList) {

        if (!delOldIds.isEmpty()) {
            cmmSpecCompanyTaxRepo.deleteAllByIdInBatch(delOldIds);
        }
        cmmSpecCompanyTaxRepo.saveInBatch(BeanMapUtils.mapListTo(saveCusTaxList, CmmSpecialCompanyTax.class));
    }
}
