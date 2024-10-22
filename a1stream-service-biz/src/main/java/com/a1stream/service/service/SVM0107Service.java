package com.a1stream.service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.a1stream.domain.bo.service.SVM010701BO;
import com.a1stream.domain.entity.ServiceSchedule;
import com.a1stream.domain.form.service.SVM010701Form;
import com.a1stream.domain.form.service.SVM010702Form;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.repository.ServiceScheduleRepository;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.ServiceScheduleVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@Service
public class SVM0107Service {

    @Resource
    private ServiceScheduleRepository serviceScheduleRepository;

    @Resource
    private SerializedProductRepository serializedProductRepository;

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    public List<SVM010701BO> findServiceReservationList(SVM010701Form form) {

        return serviceScheduleRepository.findServiceReservationList(form);
    }

    public SVM010702Form getInfoByPlateNo(SVM010702Form form) {

        return serializedProductRepository.getInfoByPlateNo(form);
    }

    public Integer getServiceScheduleRowCount(SVM010702Form form) {

        return serviceScheduleRepository.getServiceScheduleRowCount(form);
    }

    public ServiceScheduleVO findServiceScheduleVO(Long serviceScheduleId) {

        return BeanMapUtils.mapTo(serviceScheduleRepository.findByServiceScheduleId(serviceScheduleId), ServiceScheduleVO.class);
    }

    public void updateServiceSchedule(ServiceScheduleVO serviceScheduleVO) {

        if (!ObjectUtils.isEmpty(serviceScheduleVO)) {
            serviceScheduleRepository.save(BeanMapUtils.mapTo(serviceScheduleVO, ServiceSchedule.class));
        }
    }

    public MstFacilityVO getFacilityVoByFacilityId(Long facilityId) {

        return BeanMapUtils.mapTo(this.mstFacilityRepository.findByFacilityId(facilityId), MstFacilityVO.class);
    }
}