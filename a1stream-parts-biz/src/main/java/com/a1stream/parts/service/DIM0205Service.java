package com.a1stream.parts.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import com.a1stream.domain.bo.parts.DIM020501BO;
import com.a1stream.domain.entity.Workzone;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.WorkzoneRepository;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.WorkzoneVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:Work Zone Maintenance
*
* @author mid2178
*/
@Service
public class DIM0205Service {

    @Resource
    private WorkzoneRepository workzoneRepo;

    @Resource
    private LocationRepository locationRepo;

    public void deleteWorkzone(WorkzoneVO workzoneVO) {
        workzoneRepo.delete(BeanMapUtils.mapTo(workzoneVO, Workzone.class));
    }

    public void saveOrUpdateWorkzone(WorkzoneVO workzoneVO) {

        workzoneRepo.save(BeanMapUtils.mapTo(workzoneVO, Workzone.class));
    }

    public List<WorkzoneVO> getWorkZoneByCd(String siteId, String workZoneCd, String workzoneNm) {

        return BeanMapUtils.mapListTo(workzoneRepo.getWorkZoneByCd(siteId, workZoneCd, workzoneNm), WorkzoneVO.class);
    }

    public List<WorkzoneVO> getWorkzoneByNm(String siteId, String workzoneNm) {

        return BeanMapUtils.mapListTo(workzoneRepo.getWorkZoneByNm(siteId, workzoneNm), WorkzoneVO.class);
    }

    public List<LocationVO> getByWorkzoneId(Long workzoneId, String siteId) {

        return BeanMapUtils.mapListTo(locationRepo.findByWorkzoneIdAndSiteId(workzoneId, siteId), LocationVO.class);
    }

    public void deleteWorkzoneById(Long workzoneId) {

        workzoneRepo.deleteById(workzoneId);
    }

    public List<WorkzoneVO> getWorkzoneByIds(Set<Long> workzoneIds) {

        return BeanMapUtils.mapListTo(workzoneRepo.findByWorkzoneIdIn(workzoneIds), WorkzoneVO.class);
    }

    public void saveAll(List<WorkzoneVO> dataList) {

        workzoneRepo.saveInBatch(BeanMapUtils.mapListTo(dataList, Workzone.class));
    }

    public List<DIM020501BO> getScreenData(String siteId, Long personId) {

        return workzoneRepo.getWorkzoneMaintenanceInfo(siteId, personId);
    }

    public WorkzoneVO findByWorkZoneId(Long facilityId, Long workZoneId) {
        return BeanMapUtils.mapTo(workzoneRepo.findFirstByFacilityIdAndWorkzoneId(facilityId, workZoneId), WorkzoneVO.class);
    }
}
