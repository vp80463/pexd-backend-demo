package com.a1stream.master.service;

import com.a1stream.domain.bo.master.CMQ050801BO;
import com.a1stream.domain.form.master.CMQ050801Form;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ProductOrderResultHistoryRepository;
import com.a1stream.domain.repository.ProductOrderResultSummaryRepository;
import com.a1stream.domain.vo.ProductOrderResultHistoryVO;
import com.a1stream.domain.vo.ProductOrderResultSummaryVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* 功能描述:Parts Summary Information明细画面
*
* mid2330
* 2024年6月7日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/07   Liu Chaoran     New
*/
@Service
public class CMQ0508Service {

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private ProductOrderResultSummaryRepository productOrderResultSummaryRepository;

    @Resource
    private ProductOrderResultHistoryRepository productOrderResultHistoryRepository;

    public Page<CMQ050801BO> findPartsSummaryList(CMQ050801Form model, String siteId) {
        return mstProductRepository.findPartsSummaryList(model, siteId);
    }

    public List<CMQ050801BO> findPartsSummaryExportList(CMQ050801Form model, String siteId) {
        return mstProductRepository.findPartsSummaryExportList(model, siteId);
    }

    public List<ProductOrderResultSummaryVO> findProductOrderResultSummaryRepositoryInYears (String siteId, List<String> targetYear, Long productId, Long facilityId) {

        return BeanMapUtils.mapListTo(productOrderResultSummaryRepository.findProductOrderResultSummeryInYears(siteId, targetYear, productId, facilityId), ProductOrderResultSummaryVO.class);
    }

    public List<ProductOrderResultSummaryVO> findProductOrderResultSummaryRepositoryInYearsNoProductId(String siteId, List<String> targetYear, Long facilityId) {

        return BeanMapUtils.mapListTo(productOrderResultSummaryRepository.findProductOrderResultSummeryInYearsNoProductId(siteId, targetYear, facilityId), ProductOrderResultSummaryVO.class);
    }

    public List<ProductOrderResultHistoryVO> findProductOrderResultHisRepositoryInYears (String siteId, List<String> targetYear, Long productId, Long facilityId) {

        return BeanMapUtils.mapListTo(productOrderResultHistoryRepository.findProductOrderResultHisRepositoryInYears(siteId, targetYear, productId, facilityId), ProductOrderResultHistoryVO.class);
    }

    public List<ProductOrderResultHistoryVO> findProductOrderResultHisRepositoryInYearsNoProductId(String siteId, List<String> targetYear, Long facilityId) {

        return BeanMapUtils.mapListTo(productOrderResultHistoryRepository.findProductOrderResultHisRepositoryInYearsNoProductId(siteId, targetYear, facilityId), ProductOrderResultHistoryVO.class);
    }
}