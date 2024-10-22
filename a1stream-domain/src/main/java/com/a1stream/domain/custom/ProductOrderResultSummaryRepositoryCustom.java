package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.data.domain.PageImpl;

import com.a1stream.domain.form.master.CMM050901Form;
import com.a1stream.domain.vo.ProductOrderResultSummaryVO;

/**
 * @author mid2259
 */
public interface ProductOrderResultSummaryRepositoryCustom {

    PageImpl<ProductOrderResultSummaryVO> searchPartsDemandList(CMM050901Form model,String siteId,List<String> yearList);
}