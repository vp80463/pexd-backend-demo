package com.a1stream.ifs.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.a1stream.domain.entity.MstProduct;
import com.a1stream.domain.entity.ServiceRequest;
import com.a1stream.domain.entity.ServiceRequestEditHistory;
import com.a1stream.domain.entity.ServiceRequestJob;
import com.a1stream.domain.entity.ServiceRequestParts;
import com.a1stream.domain.repository.CmmConditionRepository;
import com.a1stream.domain.repository.CmmSpecialClaimRepository;
import com.a1stream.domain.repository.CmmSpecialClaimSerialProRepository;
import com.a1stream.domain.repository.CmmSymptomRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ServiceRequestEditHistoryRepository;
import com.a1stream.domain.repository.ServiceRequestJobRepository;
import com.a1stream.domain.repository.ServiceRequestPartsRepository;
import com.a1stream.domain.repository.ServiceRequestRepository;
import com.a1stream.domain.vo.CmmConditionVO;
import com.a1stream.domain.vo.CmmSpecialClaimVO;
import com.a1stream.domain.vo.CmmSymptomVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ServiceRequestEditHistoryVO;
import com.a1stream.domain.vo.ServiceRequestJobVO;
import com.a1stream.domain.vo.ServiceRequestPartsVO;
import com.a1stream.domain.vo.ServiceRequestVO;
import com.a1stream.ifs.bo.SvClaimJudgeResultParam;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class SvJudgeResultService {

    @Resource
    private ServiceRequestRepository svRequestRepo;

    @Resource
    private ServiceRequestJobRepository svRequestJobRepo;

    @Resource
    private ServiceRequestPartsRepository svRequestPartsRepo;

    @Resource
    private ServiceRequestEditHistoryRepository svRequestHistRepo;

    @Resource
    private CmmSpecialClaimRepository cmmSpecialClaimRepo;

    @Resource
    private CmmSpecialClaimSerialProRepository cmmSpecClaimSerialProRepo;

    @Resource
    private MstProductRepository mstProductRepo;

    @Resource
    private MstFacilityRepository mstFacilityRepo;

    @Resource
    private CmmSymptomRepository symptomRepo;

    @Resource
    private CmmConditionRepository conditionRepo;

    public void maintainData(SvClaimJudgeResultParam param) {

        List<ServiceRequestVO> svRequestInfos = param.getSvRequestInfos();
        List<ServiceRequestJobVO> updSvReqJobList = param.getUpdSvReqJobList();
        List<ServiceRequestPartsVO> updSvReqPartList = param.getUpdSvReqPartList();
        List<ServiceRequestEditHistoryVO> updSvReqEditHistList = param.getUpdSvReqEditHistList();

        svRequestRepo.saveInBatch(BeanMapUtils.mapListTo(svRequestInfos, ServiceRequest.class));
        svRequestJobRepo.saveInBatch(BeanMapUtils.mapListTo(updSvReqJobList, ServiceRequestJob.class));
        svRequestPartsRepo.saveInBatch(BeanMapUtils.mapListTo(updSvReqPartList, ServiceRequestParts.class));
        svRequestHistRepo.saveInBatch(BeanMapUtils.mapListTo(updSvReqEditHistList, ServiceRequestEditHistory.class));
    }

    public ServiceRequestVO findSvRequest(String siteId, Long pointId, String requestNo) {

        return BeanMapUtils.mapTo(svRequestRepo.findFirstBySiteIdAndRequestFacilityIdAndRequestNo(siteId, pointId, requestNo), ServiceRequestVO.class);
    }

    public List<ServiceRequestJobVO> findSvRequestJobs(Long svRequestId) {

        return BeanMapUtils.mapListTo(svRequestJobRepo.findByserviceRequestId(svRequestId), ServiceRequestJobVO.class);
    }

    public List<ServiceRequestPartsVO> findSvRequestParts(Long svRequestId) {

        return BeanMapUtils.mapListTo(svRequestPartsRepo.findByserviceRequestId(svRequestId), ServiceRequestPartsVO.class);
    }

    public List<CmmSpecialClaimVO> findCmmSpecialClaimByBulletinNo(String bulletinNo) {

        return BeanMapUtils.mapListTo(cmmSpecialClaimRepo.findByBulletinNo(bulletinNo), CmmSpecialClaimVO.class);
    }

    public Map<String, Long> getMstProductMap(List<String> productCd) {

        if (productCd.isEmpty()) { return new HashMap<>(); }

        List<MstProduct> result = mstProductRepo.findByProductCdIn(productCd);
        List<MstProductVO> resultVO = BeanMapUtils.mapListTo(result, MstProductVO.class);

        return resultVO.stream().collect(Collectors.toMap(MstProductVO::getProductCd, MstProductVO::getProductId, (c1, c2) -> c1));
    }

    public Long getSymptomByL1() {

        CmmSymptomVO L1 = BeanMapUtils.mapTo(symptomRepo.findFirstBySymptomCd("L1"), CmmSymptomVO.class);

        return L1 != null? L1.getSymptomId() : null;
    }

    public Long getConditionByC1() {

        CmmConditionVO C1 = BeanMapUtils.mapTo(conditionRepo.findFirstByConditionCd("C1"), CmmConditionVO.class);

        return C1 != null? C1.getConditionId() : null;
    }
}
