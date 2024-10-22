package com.a1stream.unit.facade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.common.model.BaseHelperBO;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.unit.SVM010603BO;
import com.a1stream.domain.form.unit.SVM010603Form;
import com.a1stream.domain.vo.CmmLeadManagementUnitVO;
import com.a1stream.domain.vo.CmmLeadUpdateHistoryVO;
import com.a1stream.unit.service.SVM010603Service;
import com.ymsl.solid.base.exception.BusinessCodedException;

import jakarta.annotation.Resource;

/**
* 功能描述: MC Sales Lead
*
* mid2287
* 2024年8月27日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/27   Wang Nan      New
*/
@Component
public class SVM010603Facade {

    @Resource
    private SVM010603Service svm010603Ser;

    @Resource
    private HelperFacade helperFac;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

    public Page<SVM010603BO> pageMcSalesLeadData(SVM010603Form form, PJUserDetails uc) {

        validateBeforeRetrieve(form);

        return svm010603Ser.pageMcSalesLeadData(form, uc.getDealerCode(), uc.getDefaultPointCd());
    }

    public List<SVM010603BO> listMcSalesLeadData(SVM010603Form form, PJUserDetails uc) {

        validateBeforeRetrieve(form);
        Map<String, BaseHelperBO> statusMap = helperFac.getMstCodeBOMap(null, PJConstants.SalesLeadContactStatus.CODE_ID);

        List<SVM010603BO> listData = svm010603Ser.listMcSalesLeadData(form, uc.getDealerCode(), uc.getDefaultPointCd());
        for(SVM010603BO item : listData) {
            item.setContactStatus(statusMap.containsKey(item.getContactStatus())? statusMap.get(item.getContactStatus()).getCodeData2() : "");
            item.setContackDateFromCustomer(ComUtil.changeFormat(item.getContackDateFromCustomer()));
            item.setDealerCallDate(ComUtil.changeFormat(item.getDealerCallDate()));
        }

        return listData;
    }

    public List<SVM010603BO> getCmmLeadUpdHistory(Long leadResultId) {

        return svm010603Ser.getCmmLeadUpdHistory(leadResultId);
    }

    public void confirm(SVM010603Form form) {

        List<SVM010603BO> list = form.getGridData();
        if (list.isEmpty()) { return; }

        //保存前Check
        validateBeforeSave(list);

        List<Long> leadResultIds = list.stream().map(SVM010603BO::getLeadManagementResultId).toList();
        Map<Long, CmmLeadManagementUnitVO> cmmLeadUnitMap = svm010603Ser.getCmmLeadManagementUnit(leadResultIds);

        List<CmmLeadManagementUnitVO> updCmmLeadUnitList = new ArrayList<>();
        List<CmmLeadUpdateHistoryVO> insCmmLeadHistList = new ArrayList<>();
        for (SVM010603BO item : list) {
            if (cmmLeadUnitMap.containsKey(item.getLeadManagementResultId())) {
                //1.更新cmm_lead_management_unit
                CmmLeadManagementUnitVO cmmLeadUnit = cmmLeadUnitMap.get(item.getLeadManagementResultId());
                cmmLeadUnit.setContactStatus(item.getContactStatus());
                cmmLeadUnit.setRemark(item.getRemark());

                updCmmLeadUnitList.add(cmmLeadUnit);

                //2.新增cmm_lead_update_history
                buildCmmLeadHist(insCmmLeadHistList, cmmLeadUnit, item.getContactStatus());
            }
        }

        svm010603Ser.maintainData(insCmmLeadHistList, updCmmLeadUnitList);
    }

    private void buildCmmLeadHist(List<CmmLeadUpdateHistoryVO> insCmmLeadHistList, CmmLeadManagementUnitVO cmmLeadUnit, String contactSts) {

        CmmLeadUpdateHistoryVO hist = new CmmLeadUpdateHistoryVO();

        hist.setLeadManagementResultId(cmmLeadUnit.getLeadManagementResultId());
        hist.setSiteId(cmmLeadUnit.getSiteId());
        hist.setDealerCd(cmmLeadUnit.getDealerCd());
        hist.setTelephone(cmmLeadUnit.getTelephone());
        hist.setCustomerNm(cmmLeadUnit.getCustomerNm());
        hist.setCallDateByDealer(ComUtil.nowLocalDate());
        hist.setContactStatus(contactSts);
        hist.setMcFlag(CommonConstants.CHAR_Y);

        insCmmLeadHistList.add(hist);
    }

    private void validateBeforeSave(List<SVM010603BO> list) {

        for (SVM010603BO bo : list) {

            if (StringUtils.isNotBlank(bo.getDealerCallDate()) && StringUtils.isNotBlank(bo.getContackDateFromCustomer())) {

                LocalDate dealerCallDate = LocalDate.parse(bo.getDealerCallDate(), formatter);
                LocalDate contackDate = LocalDate.parse(bo.getContackDateFromCustomer(), formatter);

                if (dealerCallDate.isBefore(contackDate)) {
                    throw new BusinessCodedException(ComUtil.t("error.dateEqAfter", new String[] {
                                                     ComUtil.t("label.dealerCallDate"),
                                                     ComUtil.t("label.contactedDateFromCustomer")}));
                }
            }
        }
    }

    private void validateBeforeRetrieve(SVM010603Form form) {

        //日期Check
        if (StringUtils.isNotBlank(form.getDateFrom()) && StringUtils.isNotBlank(form.getDateTo())) {

            LocalDate dateFrom = LocalDate.parse(form.getDateFrom(), formatter);
            LocalDate dateTo = LocalDate.parse(form.getDateTo(), formatter);

            //若fromDate大于toDate则报错
            if (dateFrom.isAfter(dateTo)) {
                throw new BusinessCodedException(ComUtil.t("M.E.00205", new String[] {
                                                    ComUtil.t("label.toDate"),
                                                    ComUtil.t("label.fromDate")}));
            }

            //若fromDate和toDate大于当前系统日期则报错
            if (dateFrom.isAfter(LocalDate.now())) {
                throw new BusinessCodedException(ComUtil.t("M.E.00205", new String[] {
                                                 ComUtil.t("label.fromDate"),
                                                 ComUtil.t("label.sysDate")}));
            }
            if (dateTo.isAfter(LocalDate.now())) {
                throw new BusinessCodedException(ComUtil.t("M.E.00205", new String[] {
                                                 ComUtil.t("label.toDate"),
                                                 ComUtil.t("label.sysDate")}));
            }

        }

        //ModelCheck
        if(StringUtils.isNotBlank(form.getModel()) && ObjectUtils.isEmpty(form.getModelId())){
            throw new BusinessCodedException(ComUtil.t("M.E.00303", new String[] {
                                             ComUtil.t("label.model"), form.getModel(),
                                             ComUtil.t("label.productInformation")}));
        }
    }
}
