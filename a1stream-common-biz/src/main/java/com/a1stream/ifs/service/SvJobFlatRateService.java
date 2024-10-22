package com.a1stream.ifs.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.a1stream.domain.entity.CmmServiceGroup;
import com.a1stream.domain.entity.CmmServiceGroupItem;
import com.a1stream.domain.entity.CmmServiceGroupJobManhour;
import com.a1stream.domain.repository.CmmServiceGroupItemRepository;
import com.a1stream.domain.repository.CmmServiceGroupJobManhourRepository;
import com.a1stream.domain.repository.CmmServiceGroupRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.vo.CmmServiceGroupItemVO;
import com.a1stream.domain.vo.CmmServiceGroupJobManhourVO;
import com.a1stream.domain.vo.CmmServiceGroupVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.ifs.bo.SvJobFlatRateParam;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class SvJobFlatRateService {

    @Resource
    private CmmServiceGroupRepository cmmSvGroupRepo;

    @Resource
    private CmmServiceGroupItemRepository cmmSvGroupItemRepo;

    @Resource
    private CmmServiceGroupJobManhourRepository cmmSvGroupJobRepo;

    @Resource
    private MstProductRepository mstProductRepo;

    public void maintainData(SvJobFlatRateParam param) {

        List<CmmServiceGroupVO> updSvGroups = param.getUpdSvGroups();
        List<CmmServiceGroupItemVO> updSvGroupItems = param.getUpdSvGroupItems();
        List<CmmServiceGroupJobManhourVO> updSvGroupJobs = param.getUpdSvGroupJobs();
        Set<Long> delGroupItems = param.getDelGroupItems();
        Set<Long> delGroupJobs = param.getDelGroupJobs();

        cmmSvGroupRepo.saveInBatch(BeanMapUtils.mapListTo(updSvGroups, CmmServiceGroup.class));
        cmmSvGroupItemRepo.saveInBatch(BeanMapUtils.mapListTo(updSvGroupItems, CmmServiceGroupItem.class));
        cmmSvGroupJobRepo.saveInBatch(BeanMapUtils.mapListTo(updSvGroupJobs, CmmServiceGroupJobManhour.class));

        if (!delGroupItems.isEmpty()) {
            cmmSvGroupItemRepo.deleteSvGroupItem(delGroupItems);
        }
        if (!delGroupJobs.isEmpty()) {
            cmmSvGroupJobRepo.deleteSvGroupJob(delGroupJobs);
        }
    }

    public void prepareExistDataMap(Set<String> svGroupCds, SvJobFlatRateParam param) {

        List<CmmServiceGroupVO> svGroups = BeanMapUtils.mapListTo(cmmSvGroupRepo.findByServiceGroupCdIn(svGroupCds), CmmServiceGroupVO.class);
        Set<Long> groupIds = svGroups.stream().map(CmmServiceGroupVO::getServiceGroupId).collect(Collectors.toSet());

        List<CmmServiceGroupItemVO> svGroupItems = BeanMapUtils.mapListTo(cmmSvGroupItemRepo.findByServiceGroupIdIn(groupIds), CmmServiceGroupItemVO.class);
        List<CmmServiceGroupJobManhourVO> svGroupJobs = BeanMapUtils.mapListTo(cmmSvGroupJobRepo.findByServiceGroupIdIn(groupIds), CmmServiceGroupJobManhourVO.class);

        Map<String, CmmServiceGroupVO> svGroupMap = svGroups.stream().collect(Collectors.toMap(CmmServiceGroupVO::getServiceGroupCd, Function.identity()));
        Map<Long, List<CmmServiceGroupItemVO>> svGroupItemMap = svGroupItems.stream().collect(Collectors.groupingBy(CmmServiceGroupItemVO::getServiceGroupId));
        Map<Long, List<CmmServiceGroupJobManhourVO>> svGroupJobMap = svGroupJobs.stream().collect(Collectors.groupingBy(CmmServiceGroupJobManhourVO::getServiceGroupId));

        param.setSvGroupMap(svGroupMap);
        param.setSvGroupItemMap(svGroupItemMap);
        param.setSvGroupJobMap(svGroupJobMap);
    }

    public void getMstProductMap(Set<String> productCd, SvJobFlatRateParam param) {

        List<MstProductVO> resultVO = BeanMapUtils.mapListTo(mstProductRepo.findByProductCdIn(productCd), MstProductVO.class);

        Map<String, MstProductVO> productMap = resultVO.stream().collect(Collectors.toMap(MstProductVO::getProductCd, Function.identity()));

        param.setMstProductMap(productMap);
    }
}
