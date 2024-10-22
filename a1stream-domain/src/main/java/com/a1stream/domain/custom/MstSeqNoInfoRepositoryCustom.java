package com.a1stream.domain.custom;

import com.a1stream.domain.vo.MstSeqNoInfoVO;

public interface MstSeqNoInfoRepositoryCustom {

    MstSeqNoInfoVO getBySeqNoType(String seqNoType, String siteId, Long facilityId);

    MstSeqNoInfoVO getBySeqNoType(String seqNoType);
}
