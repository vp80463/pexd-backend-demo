package com.a1stream.unit.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.a1stream.common.manager.ConsumerManager;
import com.a1stream.domain.bo.unit.SDM040103BO;
import com.a1stream.domain.entity.CmmGeorgaphy;
import com.a1stream.domain.entity.CmmRegistrationDocument;
import com.a1stream.domain.repository.CmmGeorgaphyRepository;
import com.a1stream.domain.repository.CmmRegistrationDocumentRepository;
import com.a1stream.domain.repository.MstCodeInfoRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.CmmRegistrationDocumentVO;
import com.a1stream.domain.vo.ConsumerPrivateDetailVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Service
public class SDM0401Service{

    @Resource
    private CmmRegistrationDocumentRepository cmmRegistrationDocumentRepository;

    @Resource
    private SerializedProductRepository serializedProductRepository;

    @Resource
    private SalesOrderRepository salesOrderRepository;

    @Resource
    private CmmGeorgaphyRepository cmmGeographyRepo;

    @Resource
    private ConsumerManager consumerManager;

    @Resource
    private MstCodeInfoRepository mstCodeInfoRepository;

    public void updateWarrantyCard(CmmRegistrationDocumentVO cmmRegistrationDocumentVO) {

        cmmRegistrationDocumentRepository.save(BeanMapUtils.mapTo(cmmRegistrationDocumentVO, CmmRegistrationDocument.class));
    }

    public SDM040103BO getWarrantyCardBasicInfo(Long registrationDocumentId) {

        return cmmRegistrationDocumentRepository.getWarrantyCardBasicInfo(registrationDocumentId);
    }

    public SDM040103BO getMotorCycleInfo(Long serializedProductId) {

        return serializedProductRepository.getMotorCycleInfo(serializedProductId);
    }

    public CmmConsumerVO getCmmConsumer(Long consumerId) {

        return consumerManager.getCmmConsumerVO(consumerId);
    }

    public ConsumerPrivateDetailVO getConsumerPrivateDetail(Long cmmConsumerId, String siteId) {

        return consumerManager.getConsumerPrivateDetailVO(cmmConsumerId,siteId);
    }

    public CmmRegistrationDocumentVO getCmmRegistrationDocumentById(Long registrationDocumentId) {

        return BeanMapUtils.mapTo(cmmRegistrationDocumentRepository.findById(registrationDocumentId), CmmRegistrationDocumentVO.class) ;
    }

    public Map<Long, String> findGeographyMap(Long... args) {

        Set<Long> geographyIds = new HashSet<>();
        for(Long arg : args) {
            geographyIds.add(arg);
        }
        if (geographyIds.isEmpty()) {

            return new HashMap<>();
        }
        List<CmmGeorgaphy> cmmGeographyList = cmmGeographyRepo.findByGeographyIdIn(geographyIds);

        return cmmGeographyList.stream().collect(Collectors.toMap(CmmGeorgaphy::getGeographyId, CmmGeorgaphy::getGeographyNm));
    }
}
