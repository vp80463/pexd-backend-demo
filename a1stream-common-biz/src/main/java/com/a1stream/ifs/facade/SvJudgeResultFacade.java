package com.a1stream.ifs.facade;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.ServiceRequestEditHistoryVO;
import com.a1stream.domain.vo.ServiceRequestJobVO;
import com.a1stream.domain.vo.ServiceRequestPartsVO;
import com.a1stream.domain.vo.ServiceRequestVO;
import com.a1stream.ifs.bo.SvClaimJudgeResultBO;
import com.a1stream.ifs.bo.SvClaimJudgeResultParam;
import com.a1stream.ifs.bo.SvRequestJudgeResultJob;
import com.a1stream.ifs.bo.SvRequestJudgeResultParts;
import com.a1stream.ifs.service.IfsCommService;
import com.a1stream.ifs.service.SvJudgeResultService;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Component
public class SvJudgeResultFacade {

    @Resource
    private SvJudgeResultService judgeRstSer;

    @Resource
    private IfsCommService cmmSiteMstSer;

    public final static String SERVICE_REQUEST_FREE_COUPON = "SERVICE_REQUEST_FREE_COUPON";
    public final static String SERVICE_REQUEST_CLAIM = "SERVICE_REQUEST_CLAIM";
    public final static String REGULAR_CLAIM = "REGULAR";
    public final static String SPECIAL_CLAIM = "CAMPAIGN";
    public final static String SERVICE_CAMPAIGN = "SERVICE_CAMPAIGN";
    public final static String REGULAR_CLAIM_FOR_BATTERY = "REGULARFORBATTERY";

    public final static String Y = CommonConstants.CHAR_Y;
    public final static String N = CommonConstants.CHAR_N;

    /**
     * IX_svClaimJudgeResult
     */
    public void importSvClaimJudgeResult(List<SvClaimJudgeResultBO> dataList) {

        doReceiveSvRequestJudgeResult(dataList);
    }

    /**
     * IX_svCouponJudgeResult
     */
    public void importSvFreeCouponJudgeResult(List<SvClaimJudgeResultBO> dataList) {

        doReceiveSvRequestJudgeResult(dataList);
    }

    /**
     *
     * SvESBServiceManagerImpl importServiceClaimJudgeResult
     */
    private void doReceiveSvRequestJudgeResult(List<SvClaimJudgeResultBO> dataList) {

        if(dataList ==null || dataList.isEmpty()) { return; }

        // 启用的经销商列表
        Set<String> dealerCds = dataList.stream().map(SvClaimJudgeResultBO::getApplicationDealerCode).collect(Collectors.toSet());
        Map<String, CmmSiteMasterVO> activeDealerMap = cmmSiteMstSer.getActiveDealerMap(dealerCds);

        // 参数准备
        SvClaimJudgeResultParam param = prepareInitParams(dataList, activeDealerMap);
        Map<String, MstFacilityVO> facilityMap = param.getFacilityMap();

        for(String siteId : activeDealerMap.keySet()) {
            List<SvClaimJudgeResultBO> items = param.getSiteDataList().get(siteId);

            for (SvClaimJudgeResultBO item : items) {
                findRequestType(item.getClaimType(), item.getCouponLevel(), param);

                MstFacilityVO fi = facilityMap.get(ComUtil.concat(siteId, item.getApplicationPointCode()));

                // Get ServiceRequestInfo by dealerCode & pointCode & requestNo
                ServiceRequestVO svRequestInfo = judgeRstSer.findSvRequest(item.getApplicationDealerCode()
                                                                        , fi.getFacilityId()
                                                                        , item.getApplicationNo());
                if (svRequestInfo != null) {
                    // Update judgement result to ServiceRequestInfo
                    updSvRequestJudgementResult(item, svRequestInfo, param, fi, siteId);
                    // Get actionKey by judgementStatusCode
                    String actionKey = param.getActJudgeMap().get(item.getJudgeStatusCode());
                    String statusId = param.getCalStatusMap().get(actionKey);
                    // Update ServiceRequestStatus by actionKey
                    String adujudicationMsg = StringUtils.equals(param.getRequestType(), SERVICE_REQUEST_FREE_COUPON)
                            ? item.getCouponAdujudicationMessage()
                            : item.getClaimAdujudicationMessage();

                    registSvReqStsHisInfo(statusId, adujudicationMsg, param);

                    svRequestInfo.setRequestStatus(statusId);

                    param.getSvRequestInfos().add(svRequestInfo);
                }
            }
        }

        judgeRstSer.maintainData(param);

        // TODO insert to cmm queue Info
        if (!param.getSvRequestInfos().isEmpty()) {

        }
    }

    // 参数准备
    private SvClaimJudgeResultParam prepareInitParams(List<SvClaimJudgeResultBO> dataList, Map<String, CmmSiteMasterVO> activeDealerMap) {

        SvClaimJudgeResultParam param = new SvClaimJudgeResultParam();

        Set<String> productCds = new HashSet<>();
        Set<String> pointCds = new HashSet<>();
        // Extracting dealer code as the key
        Function<SvClaimJudgeResultBO, String> keyExtractor = SvClaimJudgeResultBO::getApplicationDealerCode;
        Map<String, List<SvClaimJudgeResultBO>> siteDataList = cmmSiteMstSer.groupBySite(dataList, keyExtractor, activeDealerMap);

        for (SvClaimJudgeResultBO item : dataList) {
            String siteId = item.getApplicationDealerCode();
            if (activeDealerMap.keySet().contains(siteId)) {

                pointCds.add(item.getApplicationPointCode());
                productCds.add(item.getPrimaryFailedPartNo());
                Set<String> exchangeParts = item.getFailedParts().stream().map(SvRequestJudgeResultParts::getExchangePartNo).collect(Collectors.toSet());
                Set<String> jobCds = item.getJobCodes().stream().map(SvRequestJudgeResultJob::getJobCode).collect(Collectors.toSet());
                productCds.addAll(exchangeParts);
                productCds.addAll(jobCds);
            }
        }
        Set<String> siteIdSet = activeDealerMap.keySet();

        param.setFacilityMap(cmmSiteMstSer.getFacilityMap(siteIdSet, pointCds));
        param.setProductMap(judgeRstSer.getMstProductMap(productCds.stream().collect(Collectors.toList())));
        param.setSiteDataList(siteDataList);
        param.setSymptomId(judgeRstSer.getSymptomByL1()); // symptom: L1
        param.setConditionId(judgeRstSer.getConditionByC1()); // condition: C1

        return param;
    }

    private void updSvRequestJudgementResult(SvClaimJudgeResultBO item
                                            , ServiceRequestVO svReqInfo
                                            , SvClaimJudgeResultParam param
                                            , MstFacilityVO fi, String siteId) {

        String requestType = param.getRequestType();
        Map<String, Long> productMap = param.getProductMap();

        // Update serviceRequestInfo
        svReqInfo.setRequestNo(item.getApplicationNo());
        svReqInfo.setRequestFacilityId(fi != null? fi.getFacilityId() : null);
        svReqInfo.setRequestDealerCd(siteId);
        svReqInfo.setSerializedItemNo(item.getFrameNo());
        svReqInfo.setMileage(new BigDecimal(item.getMileage()));
        svReqInfo.setFactoryReceiptNo(item.getYnspireReceiptNo());
        svReqInfo.setFactoryReceiptDate(item.getYnspireReceiptDate());
        svReqInfo.setPaymentJobAmt(item.getPaymentLaborTotalAmount());
        svReqInfo.setPaymentMonth(item.getAccountingMonth());

        if (StringUtils.equals(requestType, SERVICE_REQUEST_CLAIM)
                || StringUtils.equals(requestType, SERVICE_CAMPAIGN)
                || StringUtils.equals(requestType, REGULAR_CLAIM_FOR_BATTERY)) {

            svReqInfo.setSituationHappenDate(item.getFailureDate());
            svReqInfo.setSymptomId(param.getSymptomId());
            svReqInfo.setConditionId(param.getConditionId());
            svReqInfo.setProblemComment(item.getProblemDescription());
            svReqInfo.setReasonComment(item.getCauseDescription());
            svReqInfo.setRepairComment(item.getRepairDescription());
            svReqInfo.setShopComment(item.getDealerComment());
            svReqInfo.setPaymentPartsAmt(item.getPaymentPartTotalAmount());
            svReqInfo.setPaymentTotalAmt(item.getPaymentTotalAmount());
            svReqInfo.setExpiryDate(item.getDropDueDate());
            svReqInfo.setSerializedProductId(productMap.get(item.getPrimaryFailedPartNo()));

            // Update serviceRequestInfo job items.
            updateServiceRequestJobItems(item.getJobCodes(), param);

            // Update serviceRequestInfo parts items.
            updateServiceRequestPartsItems(item.getFailedParts(), param);
        } else if (StringUtils.equals(SERVICE_REQUEST_FREE_COUPON, requestType)) {
            svReqInfo.setPaymentTotalAmt(item.getPaymentLaborTotalAmount());
        }
        param.setSiteId(svReqInfo.getSiteId());
        param.setSvReqId(svReqInfo.getServiceRequestId());
    }

    private void updateServiceRequestPartsItems(List<SvRequestJudgeResultParts> failedParts, SvClaimJudgeResultParam param) {

        List<ServiceRequestPartsVO> partsItemList = judgeRstSer.findSvRequestParts(param.getSvReqId());
        Map<Long, ServiceRequestPartsVO> oriPartsMap = partsItemList.stream().collect(Collectors.toMap(ServiceRequestPartsVO::getProductId, Function.identity()));
        for (SvRequestJudgeResultParts part : failedParts) {

            Long productId = param.getProductMap().get(part.getExchangePartNo());
            ServiceRequestPartsVO partsItem;
            if (oriPartsMap.containsKey(productId)) {
                partsItem = oriPartsMap.get(productId);
                partsItemList.remove(partsItem);
            } else {
                // add the new parts from IFmodel
                partsItem = new ServiceRequestPartsVO();

                partsItem.setSiteId(param.getSiteId());
                partsItem.setServiceRequestId(param.getSvReqId());
                partsItem.setUsedQty(part.getExchangePartQuantity());
                partsItem.setProductId(productId);
            }
            partsItem.setSelectFlag(Y);
            partsItem.setPaymentProductCd(part.getSupersedingPartNo());
            partsItem.setPaymentProductQty(part.getSupplyPartQuantity());
            partsItem.setPaymentProductPrice(part.getPaymentPartCost());
            partsItem.setPaymentAmount(part.getPaymentPartAmount());
            partsItem.setPaymentProductReceiveDate(part.getPartReceiveDate());

            param.getUpdSvReqPartList().add(partsItem);
        }
        // update the cancelled parts
        for (ServiceRequestPartsVO cancelled : partsItemList) {
            cancelled.setSelectFlag(N);
            param.getUpdSvReqPartList().add(cancelled);
        }
    }

    private void updateServiceRequestJobItems(List<SvRequestJudgeResultJob> jobCodes, SvClaimJudgeResultParam param) {

        List<ServiceRequestJobVO> jobItemList = judgeRstSer.findSvRequestJobs(param.getSvReqId());
        Map<Long, ServiceRequestJobVO> oriJobMap = jobItemList.stream().collect(Collectors.toMap(ServiceRequestJobVO::getJobId, Function.identity()));

        for (SvRequestJudgeResultJob job : jobCodes) {

            Long productId = param.getProductMap().get(job.getJobCode());
            ServiceRequestJobVO jobItem;
            if (oriJobMap.containsKey(productId)) {
                jobItem = oriJobMap.get(productId);
                jobItemList.remove(jobItem);
            } else {
                // add the new jobItem from IFmodel
                jobItem = new ServiceRequestJobVO();

                jobItem.setSiteId(param.getSiteId());
                jobItem.setServiceRequestId(param.getSvReqId());
                jobItem.setJobId(productId);
            }
            jobItem.setPaymentAmt(job.getPaymentLaborAmount());
            jobItem.setStdManhour(job.getFlatrate());
            jobItem.setSelectFlag(Y);

            param.getUpdSvReqJobList().add(jobItem);
        }
        // update the cancelled jobItem
        for (ServiceRequestJobVO cancelled : jobItemList) {
            cancelled.setSelectFlag(N);
            param.getUpdSvReqJobList().add(cancelled);
        }
    }

    private void registSvReqStsHisInfo(String statusId, String adujudicationMsg, SvClaimJudgeResultParam param) {

        ServiceRequestEditHistoryVO hist = new ServiceRequestEditHistoryVO();

        hist.setServiceRequestId(param.getSvReqId());
        hist.setSiteId(param.getSiteId());
        hist.setRequestStatus(statusId);
        hist.setComment(adujudicationMsg);
        hist.setChangeDate(LocalDateTime.now());

        param.getUpdSvReqEditHistList().add(hist);
    }

    private void findRequestType(String judgementClaimType, String couponLevel, SvClaimJudgeResultParam param) {

        String requestType = CommonConstants.CHAR_BLANK;
        if (StringUtils.equals(SPECIAL_CLAIM, judgementClaimType)) {
            requestType = SERVICE_CAMPAIGN;
        } else if (StringUtils.isNotEmpty(couponLevel)) {
          requestType = SERVICE_REQUEST_FREE_COUPON;
        } else if (StringUtils.equals(REGULAR_CLAIM, judgementClaimType)) {
            requestType = SERVICE_REQUEST_CLAIM;
        } else {
            requestType = REGULAR_CLAIM_FOR_BATTERY;
        }

        param.setRequestType(requestType);
    }
}
