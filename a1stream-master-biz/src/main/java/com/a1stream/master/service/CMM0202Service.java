package com.a1stream.master.service;

import com.a1stream.domain.bo.master.CMM020201BO;
import com.a1stream.domain.bo.master.CMM020202BO;
import com.a1stream.domain.bo.master.CMM020202GridBO;
import com.a1stream.domain.entity.CmmPerson;
import com.a1stream.domain.entity.CmmPersonFacility;
import com.a1stream.domain.repository.CmmPersonFacilityRepository;
import com.a1stream.domain.repository.CmmPersonRepository;
import com.a1stream.domain.vo.CmmPersonFacilityVO;
import com.a1stream.domain.vo.CmmPersonVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CMM0202Service {

    @Resource
    private CmmPersonRepository cmmPersonRepository;

    @Resource
    private CmmPersonFacilityRepository cmmPersonFacilityRepository;

    public List<CMM020201BO> getEmployeeInfoListBySiteId(String siteId) {

        return cmmPersonRepository.findEmployeeInfoList(siteId);
    }

    public CMM020202BO getEmployeeDetail(Long personId, String siteId) {

        CMM020202BO employeeInfo = BeanMapUtils.mapTo(cmmPersonRepository.findById(personId), CMM020202BO.class);

        if(employeeInfo != null) {
            employeeInfo.setPointList(cmmPersonRepository.getPointDetail(personId, siteId));
        }

        return employeeInfo;
    }

    public void saveOrUpdateEmployee(CmmPersonVO cmmPersonVO, List<CMM020202GridBO> pointList, List<CmmPersonFacilityVO> removePointList) {

        cmmPersonRepository.save(BeanMapUtils.mapTo(cmmPersonVO, CmmPerson.class));

        if(removePointList != null && !removePointList.isEmpty()) {
            cmmPersonFacilityRepository.deleteAllInBatch(BeanMapUtils.mapListTo(removePointList, CmmPersonFacility.class));
        }

        if(!pointList.isEmpty()) {
            cmmPersonFacilityRepository.saveInBatch(BeanMapUtils.mapListTo(pointList, CmmPersonFacility.class));
        }
    }

    public CmmPersonVO findPersonById(Long personId) {

        return BeanMapUtils.mapTo(cmmPersonRepository.findById(personId), CmmPersonVO.class);
    }

    public boolean findPersonExistByCode(String siteId, String personCd) {

        return cmmPersonRepository.existsBySiteIdAndPersonCd(siteId, personCd);
    }

    public boolean findPersonExistByCodeAndNotThisPersonId(String siteId, String personCd, Long personId) {

        return cmmPersonRepository.existsBySiteIdAndPersonCdAndPersonIdNot(siteId, personCd, personId);
    }

    public List<CmmPersonFacilityVO> getRemovePointListByPersonId(String siteId, Long personId) {

        return BeanMapUtils.mapListTo(cmmPersonFacilityRepository.findBySiteIdAndPersonId(siteId, personId), CmmPersonFacilityVO.class);
    }
}