package com.a1stream.service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.a1stream.domain.bo.service.SVM011001BO;
import com.a1stream.domain.entity.ServicePdi;
import com.a1stream.domain.entity.ServicePdiItem;
import com.a1stream.domain.form.service.SVM011001Form;
import com.a1stream.domain.repository.CmmConsumerRepository;
import com.a1stream.domain.repository.CmmPdiRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.repository.ServicePdiItemRepository;
import com.a1stream.domain.repository.ServicePdiRepository;
import com.a1stream.domain.vo.ServicePdiItemVO;
import com.a1stream.domain.vo.ServicePdiVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
 * 功能描述:
 *
 * @author mid1966
 */
@Service
public class SVM0110Service {

    @Resource
    private SerializedProductRepository serializedProductRepository;

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private CmmConsumerRepository cmmConsumerRepository;

    @Resource
    private CmmPdiRepository cmmPdiRepository;

    @Resource
    private ServicePdiRepository servicePdiRepository;

    @Resource
    private ServicePdiItemRepository servicePdiItemRepository;

    public SVM011001Form getModelInfoByFrameNo(SVM011001Form form) {

        return serializedProductRepository.getModelInfoByFrameNo(form);
    }

    public SVM011001Form getConsumerInfoByFrameNo(SVM011001Form form) {

        return cmmConsumerRepository.getConsumerInfoByFrameNo(form);
    }

    public List<SVM011001BO> getPdiInfoListByProductCategoryId(SVM011001Form form) {

        return cmmPdiRepository.getPdiInfoListByProductCategoryId(form);
    }

    public List<SVM011001BO> getPdiInfoList(SVM011001Form form) {

        return cmmPdiRepository.getPdiInfoList(form);
    }

    public Integer getServicePdiRowCount(SVM011001Form form) {

        return servicePdiRepository.getServicePdiRowCount(form);
    }

    public void updateConfirm(ServicePdiVO servicePdiVO
                             ,List<ServicePdiItemVO> servicePdiItemVOList) {

        if (!ObjectUtils.isEmpty(servicePdiVO)) {
            servicePdiRepository.save(BeanMapUtils.mapTo(servicePdiVO, ServicePdi.class));
        }

        servicePdiItemRepository.saveInBatch(BeanMapUtils.mapListTo(servicePdiItemVOList, ServicePdiItem.class));
    }
}
