package com.a1stream.ifs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.entity.ServicePayment;
import com.a1stream.domain.entity.ServiceRequest;
import com.a1stream.domain.entity.ServiceRequestEditHistory;
import com.a1stream.domain.repository.ServicePaymentRepository;
import com.a1stream.domain.repository.ServiceRequestEditHistoryRepository;
import com.a1stream.domain.repository.ServiceRequestRepository;
import com.a1stream.domain.vo.ServicePaymentVO;
import com.a1stream.domain.vo.ServiceRequestEditHistoryVO;
import com.a1stream.domain.vo.ServiceRequestVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class SvPaymentService {

    @Resource
    private ServicePaymentRepository svPaymentRepo;

    @Resource
    private ServiceRequestRepository svRequestRepo;

    @Resource
    private ServiceRequestEditHistoryRepository svRequestHistRepo;

    public void maintainData(List<ServicePaymentVO> updSvPaymentVOs, List<ServiceRequestVO> updSvRequestVOs, List<ServiceRequestEditHistoryVO> updSvRequestHistVOs) {

        svPaymentRepo.saveInBatch(BeanMapUtils.mapListTo(updSvPaymentVOs, ServicePayment.class));
        svRequestRepo.saveInBatch(BeanMapUtils.mapListTo(updSvRequestVOs, ServiceRequest.class));
        svRequestHistRepo.saveInBatch(BeanMapUtils.mapListTo(updSvRequestHistVOs, ServiceRequestEditHistory.class));
    }

    public List<ServiceRequestVO> findSvRequestList(String siteId, String targetMonth) {

        return BeanMapUtils.mapListTo(svRequestRepo.findBySiteIdAndTargetMonth(siteId, targetMonth), ServiceRequestVO.class);
    }

    public List<ServicePaymentVO> findSvPaymentList(String siteId, String controlNo, String category, String bulletinNo) {

        List<ServicePayment> result = svPaymentRepo.findSvPaymentList(siteId, controlNo, category, bulletinNo);

        return BeanMapUtils.mapListTo(result, ServicePaymentVO.class);
    }
}
