package com.a1stream.ifs.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.a1stream.domain.vo.CmmServiceGroupItemVO;
import com.a1stream.domain.vo.CmmServiceGroupJobManhourVO;
import com.a1stream.domain.vo.CmmServiceGroupVO;
import com.a1stream.domain.vo.MstProductVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvJobFlatRateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long svGroupId;

    // 更新数据对象
    private List<CmmServiceGroupVO> updSvGroups = new ArrayList<>();
    private List<CmmServiceGroupItemVO> updSvGroupItems = new ArrayList<>();
    private List<CmmServiceGroupJobManhourVO> updSvGroupJobs = new ArrayList<>();
    private Set<Long> delGroupItems = new HashSet<>();
    private Set<Long> delGroupJobs = new HashSet<>();

    // 事前准备的参数
    private Map<String, MstProductVO> mstProductMap;
    private Map<String, CmmServiceGroupVO> svGroupMap;
    private Map<Long, List<CmmServiceGroupItemVO>> svGroupItemMap;
    private Map<Long, List<CmmServiceGroupJobManhourVO>> svGroupJobMap;
}