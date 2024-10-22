package com.a1stream.unit.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.unit.CMM090101BO;
import com.a1stream.domain.entity.CmmSpecialCompanyTax;
import com.a1stream.domain.entity.MstProduct;
import com.a1stream.domain.form.unit.CMM090101Form;
import com.a1stream.domain.repository.CmmSpecialCompanyTaxRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.vo.CmmSpecialCompanyTaxVO;
import com.a1stream.domain.vo.MstProductVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Service
public class CMM0901Service{

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private CmmSpecialCompanyTaxRepository cmmSpecialCompanyTaxRepository;

    public List<CMM090101BO> findMcPriceList(CMM090101Form model) {

        return mstProductRepository.findModelPriceList(model);
    }

    public List<MstProductVO> findByModelCodeIn(List<String> modelCodes){
        return BeanMapUtils.mapListTo(mstProductRepository.findByProductCdIn(modelCodes),MstProductVO.class);
    }

    public CmmSpecialCompanyTaxVO findByTaxId(Long taxId){
        return BeanMapUtils.mapTo(cmmSpecialCompanyTaxRepository.findById(taxId),CmmSpecialCompanyTaxVO.class);
    }

    public List<MstProductVO> findByProductIds(Set<Long> taxIds){
        return BeanMapUtils.mapListTo(mstProductRepository.findByProductIdIn(taxIds),MstProductVO.class);
    }

    public void deleteCmmSpecialCompanyTax(CmmSpecialCompanyTaxVO cmmSpecialCompanyTaxVO) {
        cmmSpecialCompanyTaxRepository.delete(BeanMapUtils.mapTo(cmmSpecialCompanyTaxVO, CmmSpecialCompanyTax.class));
    }

    public CmmSpecialCompanyTaxVO findByMcPriceCode(String taxCodes){

        return BeanMapUtils.mapTo(cmmSpecialCompanyTaxRepository.findBySpecialCompanyTaxCd(taxCodes),CmmSpecialCompanyTaxVO.class);
    }

    public List<CmmSpecialCompanyTaxVO> findByMcPriceCodeIn(Set<String> taxCodes){

        return BeanMapUtils.mapListTo(cmmSpecialCompanyTaxRepository.findBySpecialCompanyTaxCdIn(taxCodes),CmmSpecialCompanyTaxVO.class);
    }

    public void editModelPriceInfo(List<MstProductVO> updateList){//,List<MstProductVO> insertList

        mstProductRepository.saveInBatch(BeanMapUtils.mapListTo(updateList, MstProduct.class));
        // cmmSpecialCompanyTaxRepository.saveInBatch(BeanMapUtils.mapListTo(insertList, CmmSpecialCompanyTax.class));
    }
}
