package com.a1stream.ifs.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.a1stream.domain.vo.CmmSpecialClaimProblemVO;
import com.a1stream.domain.vo.CmmSpecialClaimRepairVO;
import com.a1stream.domain.vo.CmmSpecialClaimSerialProVO;
import com.a1stream.domain.vo.CmmSpecialClaimStampingVO;
import com.a1stream.domain.vo.CmmSpecialClaimVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvSpecialClaimParam implements Serializable {

    private static final long serialVersionUID = 1L;

    // 更新对象
    private Set<Long> deleteIds = new HashSet<>();
    private List<CmmSpecialClaimVO> updCmmSpecialClaimVOList = new ArrayList<>();
    private List<CmmSpecialClaimProblemVO> updProblemList = new ArrayList<>();
    private List<CmmSpecialClaimRepairVO> updRepairList = new ArrayList<>();
    private List<CmmSpecialClaimStampingVO> updStampList = new ArrayList<>();

    private List<CmmSpecialClaimSerialProVO> updSerialProList = new ArrayList<>();

    private Set<Long> delSpecClaimSerialProId = new HashSet<>();
}
