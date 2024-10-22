package com.a1stream.service.facade;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.domain.bo.service.SVM010601BO;
import com.a1stream.domain.bo.service.SVM010602BO;
import com.a1stream.domain.bo.service.SVM010604BO;
import com.a1stream.domain.bo.unit.SVM010603BO;
import com.a1stream.domain.form.service.SVM010601Form;
import com.a1stream.domain.form.service.SVM010602Form;
import com.a1stream.domain.form.service.SVM010604Form;
import com.a1stream.domain.vo.CmmLeadManagement2sVO;
import com.a1stream.domain.vo.CmmLeadUpdateHistoryVO;
import com.a1stream.domain.vo.RemindScheduleRecordVO;
import com.a1stream.service.service.SVM0106Service;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.plugins.userauth.util.ListSortUtils;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
* @author dong zhen
*/
@Component
public class SVM0106Facade {

    @Resource
    private SVM0106Service svm0106Service;

    @Resource
    private HelperFacade helperFacade;

    private static final String ERROR_MSG = "Error: ";
    private static final String WARNING_MSG = "Warning: ";

    public List<SVM010601BO> findServiceRemindList(SVM010601Form form) {

        List<SVM010601BO> remindList = svm0106Service.findServiceRemindList(form);

        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(PJConstants.RemindType.CODE_ID);

        remindList.forEach(remind -> {
            remind.setRemindType(codeMap.get(remind.getRemindTypeId()));
            remind.setRemindFlag(remind.getRemindedDate() != null && !remind.getRemindedDate().isEmpty());
            remind.setRefuseCallFlag(CommonConstants.CHAR_Y.equals(remind.getRefuseCall()));
        });
        return remindList;
    }

    public List<SVM010602BO> findServiceRemindRecordList(SVM010602Form form) {

        List<RemindScheduleRecordVO> remindScheduleRecordVOList = svm0106Service.findServiceRemindRecordList(form.getSiteId(), form.getRemindScheduleId());
        ListSortUtils.sort(remindScheduleRecordVOList, new String[]{"lastUpdated"}, new boolean[]{false});

        List<SVM010602BO> resultList = new ArrayList<>();
        for (RemindScheduleRecordVO remind : remindScheduleRecordVOList) {
            SVM010602BO remindBO = new SVM010602BO();
            remindBO.setRecordDate(remind.getRecordDate());
            remindBO.setSubject(remind.getDescription());
            remindBO.setLastUpdatedBy(remind.getLastUpdatedBy());
            resultList.add(remindBO);
        }
        return resultList;
    }

    public void serviceFollowUpConfirm(SVM010602Form form) {

        RemindScheduleRecordVO recordVO = new RemindScheduleRecordVO();
        recordVO.setSiteId(form.getSiteId());
        recordVO.setRecordDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
        recordVO.setRecordSubject(form.getDescription());
        recordVO.setDescription(form.getSubject());
        recordVO.setRemindScheduleId(form.getRemindScheduleId());
        recordVO.setSatisfactionPoint(form.getSatisfactionPoint());
        svm0106Service.serviceFollowUpConfirm(form, recordVO);
    }

    public List<SVM010601BO> findServiceRemindExportList(SVM010601Form form) {

        List<SVM010601BO> remindList = svm0106Service.findServiceRemindList(form);

        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(PJConstants.RemindType.CODE_ID);

        Set<Long> remindScheduleIdSet = remindList.stream().map(SVM010601BO::getRemindScheduleId).collect(Collectors.toSet());
        List<RemindScheduleRecordVO> remindScheduleRecordVOList = svm0106Service.findServiceRemindRecordOrderByList(form.getSiteId(), remindScheduleIdSet);
        Map<Long, RemindScheduleRecordVO> topRecordsMap = remindScheduleRecordVOList.stream()
                .collect(Collectors.toMap(
                        RemindScheduleRecordVO::getRemindScheduleId,
                        c -> c,
                        (record1, record2) -> record1.getDateCreated().isAfter(record2.getDateCreated()) ? record1 : record2
                ));

        remindList.forEach(remind -> {
            remind.setRemindType(codeMap.get(remind.getRemindTypeId()));
            remind.setRemindFlagStr(remind.getRemindedDate() != null && !remind.getRemindedDate().isEmpty() ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
            remind.setSubject(topRecordsMap.get(remind.getRemindScheduleId()).getDescription());
        });
        return remindList;
    }

    public List<SVM010604BO> retrieveSalesLeadList(SVM010604Form form) {

        List<SVM010604BO> resultList = svm0106Service.retrieveSalesLeadList(form);

        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(PJConstants.ContactStatus2s.CODE_ID);
        resultList.forEach(lead -> lead.setTelemarketingResult(codeMap.get(lead.getTelemarketingResultTypeId())));
        return resultList;
    }

    public List<SVM010604BO> retrieveSalesLeadHistoryList(SVM010604Form form) {

        List<SVM010604BO> resultList = new ArrayList<>();
        List<SVM010603BO> historyList = svm0106Service.getCmmLeadUpdateHistoryVOList(form.getLeadManagementResultId());

        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(PJConstants.ContactStatus2s.CODE_ID);

        for(SVM010603BO member: historyList){

            SVM010604BO lead = new SVM010604BO();
            lead.setContactStatus(codeMap.get(member.getDetailContactStatus()));
            lead.setMobilePhone(member.getDetailPhoneNo());
            lead.setCallDateByDealer(member.getDetailDealerCallDate());
            lead.setCustomerNm(member.getDetailCustomerNm());
            resultList.add(lead);
        }
        return resultList;
    }

    public void salesLeadConfirm(SVM010604Form form) {

        Set<Long> leadManagementResultIdList = form.getGridList().stream().map(SVM010604BO::getLeadManagementResultId).collect(Collectors.toSet());
        List<CmmLeadManagement2sVO> cmmLeadManagement2sVOList = svm0106Service.getCmmLeadManagement2sVOList(leadManagementResultIdList);
        Map<Long, CmmLeadManagement2sVO> leadMap = cmmLeadManagement2sVOList.stream().collect(Collectors.toMap(CmmLeadManagement2sVO::getLeadManagementResultId, c -> c));

        List<CmmLeadManagement2sVO> cmmLeadManagement2sSaveList = new ArrayList<>();
        List<CmmLeadUpdateHistoryVO> cmmLeadUpdateHistorySaveList = new ArrayList<>();
        for(SVM010604BO member : form.getGridList()){

            CmmLeadManagement2sVO cmmLeadManagement2sVO = leadMap.get(member.getLeadManagementResultId());
            cmmLeadManagement2sVO.setCallDateByDealer(member.getCallDateByDealer());
            cmmLeadManagement2sVO.setContactStatus(member.getTelemarketingResultTypeId());
            cmmLeadManagement2sVO.setNote(member.getNote());
            cmmLeadManagement2sSaveList.add(cmmLeadManagement2sVO);

            CmmLeadUpdateHistoryVO changeHistoryVO = getCmmLeadUpdateHistoryVO(form, member);
            cmmLeadUpdateHistorySaveList.add(changeHistoryVO);
        }

        svm0106Service.salesLeadConfirm(cmmLeadManagement2sSaveList, cmmLeadUpdateHistorySaveList);
    }

    private static CmmLeadUpdateHistoryVO getCmmLeadUpdateHistoryVO(SVM010604Form form, SVM010604BO member) {

        CmmLeadUpdateHistoryVO changeHistoryVO = new CmmLeadUpdateHistoryVO();
        changeHistoryVO.setSiteId(form.getSiteId());
        changeHistoryVO.setDealerCd(form.getPointCd());
        changeHistoryVO.setTelephone(member.getMobilePhone());
        changeHistoryVO.setCustomerNm(member.getFirstNm() + CommonConstants.CHAR_SPACE + member.getMiddleNm() + CommonConstants.CHAR_SPACE + member.getLastNm());
        changeHistoryVO.setCallDateByDealer(member.getCallDateByDealer());
        changeHistoryVO.setContactStatus(member.getTelemarketingResultTypeId());
        changeHistoryVO.setMcFlag(CommonConstants.CHAR_ZERO);
        changeHistoryVO.setOilFlag(form.getCategory());
        changeHistoryVO.setLeadManagementResultId(member.getLeadManagementResultId());
        changeHistoryVO.setLeadStatus(member.getScoring());
        changeHistoryVO.setFrameNo(member.getFrameNo());
        changeHistoryVO.setPlateNo(member.getPlateNo());
        return changeHistoryVO;
    }

    public SVM010604Form checkFile(SVM010604Form form) {

        List<SVM010604BO> importList = form.getImportList();
        Map<String, CmmLeadManagement2sVO> leadMap = this.getLeadMap(importList);

        for (SVM010604BO member : importList) {
            List<String> error        = new ArrayList<>();
            List<Object[]> errorParam = new ArrayList<>();

            if (!"FSC".equals(member.getCategory()) && !"Oil".equals(member.getCategory())){
                error.add("M.E.10503");
                errorParam.add(new Object[]{});
            }

            if (StringUtils.isEmpty(PJConstants.ContactStatus2s.getCodeByDescription(member.getTelemarketingResult()))){
                error.add("M.E.10504");
                errorParam.add(new Object[]{});
            } else {
                member.setTelemarketingResultTypeId(PJConstants.ContactStatus2s.getCodeByDescription(member.getTelemarketingResult()));
            }

            String key = member.getMobilePhone() + member.getTimeStamp() + member.getCategory() + member.getFrameNo();
            if (!leadMap.containsKey(key)){
                error.add("M.E.10505");
                errorParam.add(new Object[]{});
            } else {
                member.setLeadManagementResultId(leadMap.get(key).getLeadManagementResultId());
            }
            member.setError(error);
            member.setErrorParam(errorParam);
        }
        return form;
    }

    private Map<String, CmmLeadManagement2sVO> getLeadMap(List<SVM010604BO> importList) {

        List<String> phoneList = importList.stream().map(SVM010604BO::getMobilePhone).toList();
        List<String> timeStampList = importList.stream().map(SVM010604BO::getTimeStamp).toList();
        List<String> categoryList = importList.stream()
                .map(item -> {
                    String category = item.getCategory();
                    if ("FSC".equals(category)) {
                        return "0";
                    } else if ("Oil".equals(category)) {
                        return "1";
                    } else {
                        return category;
                    }
                })
                .toList();
        List<String> frameNoList = importList.stream().map(SVM010604BO::getFrameNo).toList();
        List<CmmLeadManagement2sVO> cmmLeadManagement2sVOList = svm0106Service.getCmmLeadManagement2sList(phoneList, timeStampList, categoryList, frameNoList);
        Map<String, CmmLeadManagement2sVO> leadMap = new HashMap<>();
        for (CmmLeadManagement2sVO member : cmmLeadManagement2sVOList){
            String category = "";
            if (CommonConstants.CHAR_ZERO.equals(member.getOilFlag())) {
                category = "FSC";
            } else if (CommonConstants.CHAR_ONE.equals(member.getOilFlag())) {
                category = "Oil";
            }
            String key = member.getMobilePhone() + member.getTimeStamp() + category + member.getFrameNo();
            leadMap.put(key, member);
        }
        return leadMap;
    }

    public Object getValidFileList(SVM010604Form form) {

        Map<String, CmmLeadManagement2sVO> leadMap = this.getLeadMap(form.getImportList());
        form.getImportList().forEach(member -> {

            StringBuilder errorMsg = new StringBuilder(ERROR_MSG);

            if (!"FSC".equals(member.getCategory()) && !"Oil".equals(member.getCategory())){
                errorMsg.append(CodedMessageUtils.getMessage("M.E.10503", new Object[]{""}));
            }

            if (StringUtils.isEmpty(PJConstants.ContactStatus2s.getCodeByDescription(member.getTelemarketingResult()))){
                errorMsg.append(CodedMessageUtils.getMessage("M.E.10504", new Object[]{""}));
            }

            String key = member.getMobilePhone() + member.getTimeStamp() + member.getCategory() + member.getFrameNo();
            if (!leadMap.containsKey(key)){
                errorMsg.append(CodedMessageUtils.getMessage("M.E.10505", new Object[]{""}));
            }

            member.setErrorMessage(errorMsg.toString());
        });

        return form.getImportList();
    }
}