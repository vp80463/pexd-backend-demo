package com.a1stream.ifs.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.common.constants.PJConstants.ServicePaymentStatus;
import com.a1stream.common.constants.PJConstants.ServiceRequestStatus;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.ServicePaymentVO;
import com.a1stream.domain.vo.ServiceRequestEditHistoryVO;
import com.a1stream.domain.vo.ServiceRequestVO;
import com.a1stream.ifs.bo.SvPaymentBO;
import com.a1stream.ifs.service.IfsCommService;
import com.a1stream.ifs.service.SvPaymentService;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Component
public class SvPaymentFacade {

    @Resource
    private SvPaymentService paymentSer;

    @Resource
    private IfsCommService cmmSiteMstSer;

    private static final String REQUEST_APPROVED = ServiceRequestStatus.APPROVED.getCodeDbid();
    private static final String ACCOUNT_CONFIRM = ServiceRequestStatus.ACCOUNTCONFIRMED.getCodeDbid();

    /**
     * IX_svPayment
     * ISvESBServiceManager serviceRequestPaymentManager doReceiveServicePaymentInfo
     */
    public void importServicePayments(List<SvPaymentBO> dataList) {

        if(dataList ==null || dataList.isEmpty()) { return; }

        // 启用的经销商列表
        Set<String> dealerCds = dataList.stream().map(SvPaymentBO::getPaymentDealerCode).collect(Collectors.toSet());
        Map<String, CmmSiteMasterVO> activeDealerMap = cmmSiteMstSer.getActiveDealerMap(dealerCds);

        Map<String, List<SvPaymentBO>> siteDataMap = getSiteDataMap(dataList, activeDealerMap);

        List<ServicePaymentVO> updSvPaymentVOs = new ArrayList<>();
        List<ServiceRequestVO> updSvRequestVOs = new ArrayList<>();
        List<ServiceRequestEditHistoryVO> updSvRequestHistVOs = new ArrayList<>();

        for(String dealerCd : activeDealerMap.keySet()) {

            CmmSiteMasterVO siteInfo = activeDealerMap.get(dealerCd);
            List<SvPaymentBO> items = siteDataMap.get(dealerCd);

            String siteId = siteInfo.getSiteId();
            ServicePaymentVO svPaymentInfo = null;
            for (SvPaymentBO item : items) {
                String paymentCategory = transferProcessType(item.getProcessType());
                String bulletinNo = StringUtils.EMPTY;
                String targetMonth = item.getAccountingMonth();

                if (StringUtils.equals(paymentCategory, ServiceCategory.SPECIALCLAIM.getCodeDbid())) {
                    bulletinNo = item.getBulletinNo();
                }

                List<ServicePaymentVO> svPaymentList = paymentSer.findSvPaymentList(siteId, item.getPaymentControlNo(), paymentCategory, bulletinNo);
                svPaymentInfo = svPaymentList.isEmpty()? null : svPaymentList.get(0);

                buildSvPaymentInfo(updSvRequestVOs, updSvRequestHistVOs, siteId, svPaymentInfo, item, paymentCategory, bulletinNo, targetMonth);

                updSvPaymentVOs.add(svPaymentInfo);
            }

            paymentSer.maintainData(updSvPaymentVOs, updSvRequestVOs, updSvRequestHistVOs);
        }
    }

    private void buildSvPaymentInfo(List<ServiceRequestVO> updSvRequestVOs
                                , List<ServiceRequestEditHistoryVO> updSvRequestHistVOs
                                , String siteId
                                , ServicePaymentVO svPaymentInfo
                                , SvPaymentBO item
                                , String paymentCategory
                                , String bulletinNo
                                , String targetMonth) {

        if (svPaymentInfo == null) {

            svPaymentInfo = new ServicePaymentVO();

            svPaymentInfo.setSiteId(siteId);
            svPaymentInfo.setReceiptDate(ComUtil.nowDate());
            svPaymentInfo.setFactoryPaymentControlNo(item.getPaymentControlNo());

            svPaymentInfo.setPaymentStatus(ServicePaymentStatus.INFORECEIVED.getCodeDbid());
        } else {

            svPaymentInfo.setFactoryDocReceiptDate(item.getAccountDocReceiptDate());
            svPaymentInfo.setPaymentAmtSpecialClaimTotal(item.getPaymentClaimTotalAmount());
            svPaymentInfo.setPaymentAmtSpecialClaimJob(item.getPaymentClaimJobAmount());
            svPaymentInfo.setPaymentAmtSpecialClaimPart(item.getPaymentClaimPartAmount());
            if (StringUtils.isEmpty(svPaymentInfo.getFactoryDocReceiptDate())
                    && StringUtils.isNotEmpty(item.getAccountDocReceiptDate())) {

                svPaymentInfo.setPaymentStatus(ServicePaymentStatus.STATEMENTRECEIPT.getCodeDbid());
                // Set relate ServiceRequest to Close.
                doAccountReceiptConfirm(siteId, targetMonth, updSvRequestVOs, updSvRequestHistVOs);
            }
        }
        if (StringUtils.equals(paymentCategory, ServiceCategory.SPECIALCLAIM.getCodeDbid())) {
            svPaymentInfo.setBulletinNo(bulletinNo);
        }
        svPaymentInfo.setFactoryBudgetSettleDate(item.getYnspireAccountDate());
        svPaymentInfo.setTargetMonth(targetMonth);
        svPaymentInfo.setPaymentAmt(item.getPaymentTotalAmount());
        svPaymentInfo.setPaymentCategory(paymentCategory);
        svPaymentInfo.setPaymentAmtWarrantyClaimTotal(item.getPaymentClaimTotalAmount());
        svPaymentInfo.setPaymentAmtWarrantyClaimJob(item.getPaymentClaimJobAmount());
        svPaymentInfo.setPaymentAmtWarrantyClaimPart(item.getPaymentClaimPartAmount());
        svPaymentInfo.setPaymentAmtBatteryWarranty(item.getPaymentClaimBatteryAmount());
        svPaymentInfo.setPaymentAmtFreeCouponTotal(item.getPaymentCouponTotalAmount());
        svPaymentInfo.setPaymentAmtFreeCouponLevel1(item.getPaymentCouponLevel1Amount());
        svPaymentInfo.setPaymentAmtFreeCouponLevel2(item.getPaymentCouponLevel2Amount());
        svPaymentInfo.setPaymentAmtFreeCouponLevel3(item.getPaymentCouponLevel3Amount());
    }

    private void doAccountReceiptConfirm(String siteId, String paymentMonth
                                        , List<ServiceRequestVO> updSvRequestVOs
                                        , List<ServiceRequestEditHistoryVO> updSvRequestHistVOs) {

        List<ServiceRequestVO> svRequests = paymentSer.findSvRequestList(siteId, paymentMonth);

        for(ServiceRequestVO request : svRequests) {
            if (StringUtils.equals(request.getRequestStatus(), REQUEST_APPROVED)) {
                request.setRequestStatus(ACCOUNT_CONFIRM);

                buildSvRequestEditHist(request, updSvRequestHistVOs);

                updSvRequestVOs.add(request);
            }
        }
    }

    private void buildSvRequestEditHist(ServiceRequestVO request, List<ServiceRequestEditHistoryVO> updSvRequestHistVOs) {

        ServiceRequestEditHistoryVO hist = new ServiceRequestEditHistoryVO();

        hist.setSiteId(request.getSiteId());
        hist.setRequestStatus(request.getRequestStatus());
        hist.setServiceRequestId(request.getServiceRequestId());

        updSvRequestHistVOs.add(hist);
    }

    private String transferProcessType(String outerData) {

        String serviceCategory = "";
        switch(outerData) {
            case "REGULAR":
                serviceCategory = ServiceCategory.CLAIM.getCodeDbid();
                break;
            case "REGULARFORBATTERY":
                serviceCategory = ServiceCategory.CLAIMBATTERY.getCodeDbid();
                break;
            case "CAMPAIGN":
                serviceCategory = ServiceCategory.SPECIALCLAIM.getCodeDbid();
                break;
        }

        return serviceCategory;
    }

    private Map<String, List<SvPaymentBO>> getSiteDataMap(List<SvPaymentBO> dataList, Map<String, CmmSiteMasterVO> activeDealerMap) {

        Map<String, List<SvPaymentBO>> siteDataMap = new HashMap<>();
        for (SvPaymentBO item : dataList) {
            String siteId = item.getPaymentDealerCode();
            if (activeDealerMap.keySet().contains(siteId)) {
                if (siteDataMap.containsKey(siteId)) {
                    siteDataMap.get(siteId).add(item);
                } else {
                    List<SvPaymentBO> siteData = new ArrayList<>();
                    siteData.add(item);
                    siteDataMap.put(siteId, siteData);
                }
            }
        }

        return siteDataMap;
    }
}
