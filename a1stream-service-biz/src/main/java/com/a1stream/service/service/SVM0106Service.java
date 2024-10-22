package com.a1stream.service.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.service.SVM010601BO;
import com.a1stream.domain.bo.service.SVM010604BO;
import com.a1stream.domain.bo.unit.SVM010603BO;
import com.a1stream.domain.entity.CmmLeadManagement2s;
import com.a1stream.domain.entity.CmmLeadUpdateHistory;
import com.a1stream.domain.entity.CmmSerializedProduct;
import com.a1stream.domain.entity.RemindScheduleRecord;
import com.a1stream.domain.form.service.SVM010601Form;
import com.a1stream.domain.form.service.SVM010602Form;
import com.a1stream.domain.form.service.SVM010604Form;
import com.a1stream.domain.repository.CmmLeadManagement2sRepository;
import com.a1stream.domain.repository.CmmLeadUpdateHistoryRepository;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.RemindScheduleRecordRepository;
import com.a1stream.domain.repository.RemindScheduleRepository;
import com.a1stream.domain.vo.CmmLeadManagement2sVO;
import com.a1stream.domain.vo.CmmLeadUpdateHistoryVO;
import com.a1stream.domain.vo.RemindScheduleRecordVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* @author dong zhen
*/
@Service
public class SVM0106Service {

    @Resource
    private RemindScheduleRepository remindScheduleRepository;

    @Resource
    private RemindScheduleRecordRepository remindScheduleRecordRepository;

    @Resource
    private CmmSerializedProductRepository cmmSerializedProductRepository;

    @Resource
    private CmmLeadManagement2sRepository cmmLeadManagement2sRepository;

    @Resource
    private CmmLeadUpdateHistoryRepository cmmLeadUpdateHistoryRepository;

    public List<SVM010601BO> findServiceRemindList(SVM010601Form form) {

        return remindScheduleRepository.findServiceRemindList(form);
    }

    public List<RemindScheduleRecordVO> findServiceRemindRecordList(String siteId, Long remindScheduleId) {

        List<RemindScheduleRecord> remindScheduleRecordList = remindScheduleRecordRepository.findBySiteIdAndRemindScheduleId(siteId, remindScheduleId);
        return BeanMapUtils.mapListTo(remindScheduleRecordList, RemindScheduleRecordVO.class);
    }

    public void serviceFollowUpConfirm(SVM010602Form form, RemindScheduleRecordVO recordVO) {

        remindScheduleRecordRepository.save(BeanMapUtils.mapTo(recordVO, RemindScheduleRecord.class));

        CmmSerializedProduct serializedProduct = cmmSerializedProductRepository.findBySerializedProductId(form.getSerializedProductId());
        if (!serializedProduct.getRefuseCall().equals(form.getRefuseCall())){
            serializedProduct.setRefuseCall(form.getRefuseCall());
            cmmSerializedProductRepository.save(serializedProduct);
        }
    }

    public List<RemindScheduleRecordVO> findServiceRemindRecordOrderByList(String siteId, Set<Long> remindScheduleIdSet) {

        List<RemindScheduleRecord> remindScheduleRecordList = remindScheduleRecordRepository.findTopByRemindScheduleIdInOrderByDateCreatedDesc(siteId, remindScheduleIdSet);
        return BeanMapUtils.mapListTo(remindScheduleRecordList, RemindScheduleRecordVO.class);
    }

    public List<SVM010604BO> retrieveSalesLeadList(SVM010604Form form) {

        return cmmLeadManagement2sRepository.retrieveSalesLeadList(form);
    }

    public List<SVM010603BO> getCmmLeadUpdateHistoryVOList(Long leadManagementResultId) {

        return cmmLeadUpdateHistoryRepository.getCmmLeadUpdHistory(leadManagementResultId);
    }

    public List<CmmLeadManagement2sVO> getCmmLeadManagement2sVOList(Set<Long> leadManagementResultIdList) {

        List<CmmLeadManagement2s> cmmLeadManagement2sList = cmmLeadManagement2sRepository.findByLeadManagementResultIdIn(leadManagementResultIdList);
        return BeanMapUtils.mapListTo(cmmLeadManagement2sList, CmmLeadManagement2sVO.class);
    }

    public void salesLeadConfirm(List<CmmLeadManagement2sVO> cmmLeadManagement2sSaveList, List<CmmLeadUpdateHistoryVO> cmmLeadUpdateHistorySaveList) {

        cmmLeadManagement2sRepository.saveInBatch(BeanMapUtils.mapListTo(cmmLeadManagement2sSaveList, CmmLeadManagement2s.class));
        cmmLeadUpdateHistoryRepository.saveInBatch(BeanMapUtils.mapListTo(cmmLeadUpdateHistorySaveList, CmmLeadUpdateHistory.class));
    }

    public List<CmmLeadManagement2sVO> getCmmLeadManagement2sList(List<String> phoneList, List<String> timeStampList, List<String> categoryList, List<String> frameNoList) {

        List<CmmLeadManagement2s> cmmLeadManagement2sList = cmmLeadManagement2sRepository.findByMobilePhoneInAndTimeStampInAndOilFlagInAndFrameNoIn(phoneList, timeStampList, categoryList, frameNoList);
        return BeanMapUtils.mapListTo(cmmLeadManagement2sList, CmmLeadManagement2sVO.class);
    }
}