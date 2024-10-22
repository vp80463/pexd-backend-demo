package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.unit.SDM010601BO;
import com.a1stream.domain.vo.CmmUnitPromotionItemVO;

public interface CmmUnitPromotionItemRepositoryCustom {

    List<SDM010601BO> getPromotionInfoByFrameNoList(List<String> frameNos);

    CmmUnitPromotionItemVO getPromotionInfoByFrameNo(String frameNo);

}
