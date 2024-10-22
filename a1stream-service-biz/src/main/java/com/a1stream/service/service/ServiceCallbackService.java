/******************************************************************************/
/* SYSTEM     : Stream                                                        */
/*                                                                            */
/* SUBSYSTEM  : Xm03                                                          */
/******************************************************************************/
package com.a1stream.service.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.domain.entity.CmmSerializedProductFscRemind;
import com.a1stream.domain.entity.RemindSchedule;
import com.a1stream.domain.repository.CmmSerializedProductFscRemindRepository;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.CmmServiceDemandRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.RemindScheduleRepository;
import com.a1stream.domain.repository.RemindSettingRepository;
import com.a1stream.domain.vo.CmmSerializedProductFscRemindVO;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmServiceDemandVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.RemindScheduleVO;
import com.a1stream.domain.vo.RemindSettingVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class ServiceCallbackService {

    @Resource
    private CmmSerializedProductFscRemindRepository cmmSerializedProductFscRemindRepo;
    @Resource
    private RemindSettingRepository remindSettingRepo;
    @Resource
    private CmmServiceDemandRepository cmmServiceDemandRepo;
    @Resource
    private CmmSerializedProductRepository cmmSerializedProductRepo;
    @Resource
    private MstProductRepository mstProductRepo;
    @Resource
    private RemindScheduleRepository remindScheduleRepo;

    public List<CmmSerializedProductFscRemindVO> findMcFscRemind(String fromDate, String toDate) {

        return BeanMapUtils.mapListTo(cmmSerializedProductFscRemindRepo.findByDateRange(fromDate, toDate), CmmSerializedProductFscRemindVO.class);
    }

    public List<RemindSettingVO> findRemindSettingByType(String remindType) {

        return BeanMapUtils.mapListTo(remindSettingRepo.findByRemindType(remindType), RemindSettingVO.class);
    }

    public List<CmmServiceDemandVO> findCmmServiceDemandVOList() {

        return BeanMapUtils.mapListTo(cmmServiceDemandRepo.findAllByOrderByBaseDateAfter(), CmmServiceDemandVO.class);
    }

    public List<CmmSerializedProductVO> findMcByIds(Set<Long> cmmSerializedProIdList) {

        return BeanMapUtils.mapListTo(cmmSerializedProductRepo.findBySerializedProductIdIn(cmmSerializedProIdList), CmmSerializedProductVO.class);
    }

    public List<MstProductVO> findModelByIds(Set<Long> modelIdList){
        return BeanMapUtils.mapListTo(mstProductRepo.findByProductIdIn(modelIdList), MstProductVO.class);
    }

    public void generateServiceFscRemind(List<RemindScheduleVO> remindScheduleForSave, List<CmmSerializedProductFscRemindVO> fscRemindForSave) {

        if (!remindScheduleForSave.isEmpty()) {remindScheduleRepo.saveInBatch(BeanMapUtils.mapListTo(remindScheduleForSave, RemindSchedule.class));}
        if (!fscRemindForSave.isEmpty()) {cmmSerializedProductFscRemindRepo.saveInBatch(BeanMapUtils.mapListTo(fscRemindForSave, CmmSerializedProductFscRemind.class));}
    }
}