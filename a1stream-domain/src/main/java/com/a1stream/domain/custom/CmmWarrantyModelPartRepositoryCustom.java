package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.vo.CmmWarrantyModelPartVO;

public interface CmmWarrantyModelPartRepositoryCustom {

    List<CmmWarrantyModelPartVO> findModelPartByModelCd(String modelCd);
}
