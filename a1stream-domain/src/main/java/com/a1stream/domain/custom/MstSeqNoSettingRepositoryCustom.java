package com.a1stream.domain.custom;

import com.a1stream.domain.vo.MstSeqNoSettingVO;

public interface MstSeqNoSettingRepositoryCustom {

    MstSeqNoSettingVO getUsableSeqNoSetting(String seqNoType, String siteId, Long facilityId);
}
